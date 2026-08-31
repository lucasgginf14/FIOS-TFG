<template>
  <div class="recruitment-page">
    <section class="recruitment-shell">
      <div class="container py-5">
        <header class="recruitment-header">
          <div class="recruitment-header__copy">
            <span class="recruitment-header__eyebrow">{{ t("recruitmentBoard.header.eyebrow") }}</span>
            <h1>{{ t("recruitmentBoard.header.title") }}</h1>
            <p>{{ t("recruitmentBoard.header.subtitle") }}</p>
          </div>

          <div class="recruitment-header__actions">
            <div v-if="canUseUserFeatures" class="recruitment-view-switch">
              <button
                type="button"
                class="recruitment-view-switch__button"
                :class="{ 'is-active': currentView === 'explore' }"
                @click="setView('explore')"
              >
                {{ t("recruitmentBoard.header.explore") }}
              </button>
              <button
                type="button"
                class="recruitment-view-switch__button"
                :class="{ 'is-active': currentView === 'mine' }"
                @click="setView('mine')"
              >
                {{ t("recruitmentBoard.header.mine") }}
              </button>
            </div>

            <button
              v-if="canPublish"
              type="button"
              class="btn btn-success recruitment-header__cta"
              @click="openCreateModal()"
            >
              {{ t("recruitmentBoard.header.create") }}
            </button>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("recruitmentBoard.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("recruitmentBoard.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("recruitmentBoard.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <section class="section-spacing">
            <BandRecruitmentFiltersBar
              v-model="filters"
              :instruments="instruments"
              :city-options="cityOptions"
              :genre-options="genreOptions"
              @clear="resetFilters"
            />
          </section>

          <section class="recruitment-results section-spacing">
            <div class="recruitment-results__header">
              <div>
                <span class="recruitment-results__eyebrow">{{ t("recruitmentBoard.list.eyebrow") }}</span>
                <h2>{{ listTitle }}</h2>
              </div>
              <span class="recruitment-results__count">
                {{ t("recruitmentBoard.list.total", { count: visibleRecruitments.length }) }}
              </span>
            </div>

            <BandRecruitmentEmptyState
              v-if="!rawRecruitments.length || !visibleRecruitments.length"
              :mine="currentView === 'mine'"
              :filtered="Boolean(rawRecruitments.length && !visibleRecruitments.length)"
              :can-publish="canPublish"
              :has-bands="hasBands"
              @create="openCreateModal()"
              @clear="resetFilters"
            />

            <div v-else class="recruitment-grid">
              <BandRecruitmentCard
                v-for="item in visibleRecruitments"
                :key="item.id"
                :item="item"
                :locale="locale"
                :mine="currentView === 'mine'"
                :busy="closingRecruitmentId === item.id"
                :highlighted="highlightedRecruitmentId === item.id"
                @view="handleViewRecruitment"
                @edit="openEditModal"
                @close="handleCloseRecruitment"
              />
            </div>
          </section>
        </template>
      </div>
    </section>

    <BandRecruitmentFormModal
      :open="formModalOpen"
      :model-value="form"
      :bands="leaderBands"
      :instruments="instruments"
      :editing="Boolean(editingRecruitmentId)"
      :submitting="formSubmitting"
      :error-message="formError"
      @close="closeFormModal"
      @submit="submitRecruitment"
      @update:model-value="form = $event"
    />

    <BandRecruitmentDetailModal
      :open="detailModalOpen"
      :item="activeDetail"
      :locale="locale"
      :loading="detailLoading"
      :error-message="detailError"
      :can-edit="canEditActiveDetail"
      :show-status="currentView === 'mine'"
      @close="closeDetailModal"
      @edit="handleEditFromDetail"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import BandMemberRepository from "@/repositories/BandMemberRepository";
import BandRecruitmentRepository from "@/repositories/BandRecruitmentRepository";
import BandRepository from "@/repositories/BandRepository";
import InstrumentRepository from "@/repositories/InstrumentRepository";
import BandRecruitmentCard from "../components/BandRecruitmentCard.vue";
import BandRecruitmentDetailModal from "../components/BandRecruitmentDetailModal.vue";
import BandRecruitmentEmptyState from "../components/BandRecruitmentEmptyState.vue";
import BandRecruitmentFiltersBar from "../components/BandRecruitmentFiltersBar.vue";
import BandRecruitmentFormModal from "../components/BandRecruitmentFormModal.vue";
import { mapRecruitmentPayload } from "@/modules/bands/bandUtils";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { locale, t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const rawRecruitments = ref([]);
const instruments = ref([]);
const myBands = ref([]);
const memberRolesByBand = ref({});

const filters = ref(createDefaultFilters());

const formModalOpen = ref(false);
const formSubmitting = ref(false);
const formError = ref("");
const editingRecruitmentId = ref(null);
const form = ref(createEmptyRecruitmentForm());

const detailModalOpen = ref(false);
const detailLoading = ref(false);
const detailError = ref("");
const activeDetailId = ref(null);
const detailById = ref({});

const closingRecruitmentId = ref(null);

let modeRedirectInProgress = false;
let nextLoadNotice = null;

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);
const currentUserId = computed(() => Number(store.state.user.id));
const requestedMineView = computed(() => route.query.view === "mine");
const currentView = computed(() =>
  requestedMineView.value && canUseUserFeatures.value ? "mine" : "explore"
);

const leaderBands = computed(() =>
  myBands.value.filter((band) => memberRolesByBand.value[band.id] === "LEADER")
);

const hasBands = computed(() => myBands.value.length > 0);
const canPublish = computed(() => leaderBands.value.length > 0);
const highlightedRecruitmentId = computed(() => Number(route.query.highlight) || null);

const visibleRecruitments = computed(() => {
  const needle = normalizeText(filters.value.searchText);

  return [...rawRecruitments.value]
    .filter((item) => {
      const genre = item.band?.mainGenre || "";
      const city = item.city || item.band?.baseCity || "";
      const haystack = normalizeText([
        item.title,
        item.band?.name,
        city,
        item.roleWanted,
        item.instrument?.name,
        genre
      ].join(" "));

      if (needle && !haystack.includes(needle)) {
        return false;
      }

      if (filters.value.instrumentId && String(item.instrument?.id || "") !== filters.value.instrumentId) {
        return false;
      }

      if (filters.value.levelRequired && item.levelRequired !== filters.value.levelRequired) {
        return false;
      }

      if (filters.value.city && city !== filters.value.city) {
        return false;
      }

      if (filters.value.genre && genre !== filters.value.genre) {
        return false;
      }

      return true;
    })
    .sort(sortRecruitments);
});

const cityOptions = computed(() =>
  [...new Set(rawRecruitments.value.map((item) => item.city || item.band?.baseCity).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const genreOptions = computed(() =>
  [...new Set(rawRecruitments.value.map((item) => item.band?.mainGenre).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const listTitle = computed(() =>
  currentView.value === "mine"
    ? t("recruitmentBoard.list.mineTitle")
    : t("recruitmentBoard.list.exploreTitle")
);

const activeDetail = computed(() => detailById.value[activeDetailId.value] || null);
const canEditActiveDetail = computed(() => {
  const recruitment = activeDetail.value;
  return canManageRecruitment(recruitment);
});

watch(
  () => `${route.query.view || ""}|${canUseUserFeatures.value}`,
  () => {
    loadPage();
  },
  { immediate: true }
);

watch(
  () => `${route.query.mode || ""}|${route.query.bandId || ""}|${loading.value}|${canUseUserFeatures.value}|${canPublish.value}`,
  () => {
    if (!loading.value) {
      handleModeQuery();
    }
  },
  { immediate: true }
);

watch(
  () => `${route.query.highlight || ""}|${loading.value}`,
  () => {
    if (!loading.value) {
      handleHighlightQuery();
    }
  },
  { immediate: true }
);

async function loadPage() {
  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;

  try {
    const listPromise =
      currentView.value === "mine"
        ? BandRecruitmentRepository.getMine()
        : BandRecruitmentRepository.getAll();

    const supportPromises = [
      InstrumentRepository.getAll(),
      canUseUserFeatures.value ? BandRepository.getMine() : Promise.resolve([])
    ];

    if (requestedMineView.value && !canUseUserFeatures.value) {
      pageNotice.value = {
        type: "warning",
        message: t("recruitmentBoard.notices.privateFallback")
      };
    }

    const [listResult, instrumentsResult, bandsResult] = await Promise.allSettled([
      listPromise,
      ...supportPromises
    ]);

    if (listResult.status !== "fulfilled") {
      throw listResult.reason;
    }

    rawRecruitments.value = listResult.value ?? [];
    instruments.value = instrumentsResult.status === "fulfilled" ? (instrumentsResult.value ?? []) : [];
    myBands.value = bandsResult.status === "fulfilled" ? (bandsResult.value ?? []) : [];

    if (canUseUserFeatures.value && myBands.value.length) {
      await loadMemberRoles();
    } else {
      memberRolesByBand.value = {};
    }

    if (instrumentsResult.status === "rejected" || bandsResult.status === "rejected") {
      pageNotice.value = pageNotice.value ?? {
        type: "warning",
        message: t("recruitmentBoard.notices.partialSupport")
      };
    }
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "recruitmentBoard.states.error");
  } finally {
    if (nextLoadNotice) {
      pageNotice.value = nextLoadNotice;
      nextLoadNotice = null;
    }

    loading.value = false;
  }
}

async function loadMemberRoles() {
  const entries = await Promise.allSettled(
    myBands.value.map(async (band) => [band.id, await BandMemberRepository.getByBand(band.id)])
  );

  const nextRoles = {};

  entries.forEach((entry) => {
    if (entry.status !== "fulfilled") {
      return;
    }

    const [bandId, members] = entry.value;
    const me = (members ?? []).find((member) => Number(member.user?.id) === currentUserId.value);
    if (me?.roleInBand) {
      nextRoles[bandId] = me.roleInBand;
    }
  });

  memberRolesByBand.value = nextRoles;
}

async function handleModeQuery() {
  if (route.query.mode !== "create") {
    return;
  }

  if (!isLogged.value) {
    if (modeRedirectInProgress) {
      return;
    }

    modeRedirectInProgress = true;

    try {
      await router.push({
        name: "Login",
        query: { redirect: route.fullPath }
      });
    } finally {
      modeRedirectInProgress = false;
    }

    return;
  }

  if (!canUseUserFeatures.value) {
    await clearCreateQuery();
    return;
  }

  if (!canPublish.value) {
    pageNotice.value = {
      type: "warning",
      message: hasBands.value
        ? t("recruitmentBoard.notices.noManageableBands")
        : t("recruitmentBoard.notices.noBands")
    };
    await clearCreateQuery();
    return;
  }

  openCreateModal(route.query.bandId);
}

async function handleHighlightQuery() {
  const highlightId = Number(route.query.highlight);

  if (!highlightId) {
    return;
  }

  if (detailModalOpen.value && activeDetailId.value === highlightId) {
    return;
  }

  await openDetailModal(highlightId);
}

async function setView(view) {
  const nextQuery = { ...route.query };

  if (view === "mine") {
    nextQuery.view = "mine";
  } else {
    delete nextQuery.view;
  }

  await router.replace({
    name: "BandRecruitmentList",
    query: nextQuery
  });
}

function resetFilters() {
  filters.value = createDefaultFilters();
}

function openCreateModal(preselectedBandId = "") {
  if (!canUseUserFeatures.value) {
    return;
  }

  if (!canPublish.value) {
    return;
  }

  const band = leaderBands.value.find((item) => String(item.id) === String(preselectedBandId))
    || leaderBands.value[0]
    || null;

  editingRecruitmentId.value = null;
  form.value = createEmptyRecruitmentForm(band?.id, band?.baseCity);
  formError.value = "";
  formModalOpen.value = true;
}

async function openEditModal(item) {
  if (!canManageRecruitment(item)) {
    return;
  }

  formError.value = "";
  editingRecruitmentId.value = item.id;

  const detail = await getRecruitmentDetail(item.id);
  if (!detail) {
    return;
  }

  form.value = mapRecruitmentToForm(detail);
  formModalOpen.value = true;
}

async function closeFormModal() {
  formModalOpen.value = false;
  formError.value = "";

  if (route.query.mode === "create") {
    await clearCreateQuery();
  }
}

async function submitRecruitment() {
  if (!canUseUserFeatures.value) {
    return;
  }

  const bandId = Number(form.value.bandId);
  const payload = mapRecruitmentPayload(form.value);

  if (!bandId || !payload.title || !payload.roleWanted || !payload.city || !payload.instrumentId || !payload.vacancies) {
    formError.value = t("recruitmentBoard.form.validation");
    return;
  }

  formSubmitting.value = true;
  formError.value = "";

  try {
    if (editingRecruitmentId.value) {
      const updated = await BandRecruitmentRepository.update(editingRecruitmentId.value, payload);
      detailById.value = {
        ...detailById.value,
        [updated.id]: updated
      };
      nextLoadNotice = {
        type: "success",
        message: t("recruitmentBoard.notices.updated")
      };
      await loadPage();
    } else {
      const created = await BandRecruitmentRepository.createForBand(bandId, payload);
      detailById.value = {
        ...detailById.value,
        [created.id]: created
      };
      nextLoadNotice = {
        type: "success",
        message: t("recruitmentBoard.notices.created")
      };

      if (currentView.value !== "mine") {
        await setView("mine");
      } else {
        await loadPage();
      }
    }

    await closeFormModal();
  } catch (error) {
    formError.value = getApiErrorMessage(error, t, "recruitmentBoard.form.submitError");
  } finally {
    formSubmitting.value = false;
  }
}

async function handleCloseRecruitment(item) {
  if (!canManageRecruitment(item)) {
    return;
  }

  const confirmed = window.confirm(t("recruitmentBoard.confirm.close", { title: item.title }));

  if (!confirmed) {
    return;
  }

  closingRecruitmentId.value = item.id;

  try {
    const closed = await BandRecruitmentRepository.close(item.id);

    rawRecruitments.value = rawRecruitments.value.map((current) =>
      current.id === item.id
        ? { ...current, status: closed.status, publicationDate: closed.publicationDate }
        : current
    );

    detailById.value = {
      ...detailById.value,
      [item.id]: closed
    };

    pageNotice.value = {
      type: "success",
      message: t("recruitmentBoard.notices.closed")
    };
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "recruitmentBoard.notices.closeError")
    };
  } finally {
    closingRecruitmentId.value = null;
  }
}

async function handleViewRecruitment(item) {
  const nextQuery = {
    ...route.query,
    highlight: String(item.id)
  };

  await router.replace({
    name: "BandRecruitmentList",
    query: nextQuery
  });

  await openDetailModal(item.id);
}

async function openDetailModal(id) {
  detailModalOpen.value = true;
  activeDetailId.value = Number(id);
  detailError.value = "";

  const detail = await getRecruitmentDetail(id);
  if (!detail) {
    detailModalOpen.value = true;
  }
}

async function closeDetailModal() {
  detailModalOpen.value = false;
  detailError.value = "";

  if (route.query.highlight) {
    const nextQuery = { ...route.query };
    delete nextQuery.highlight;

    await router.replace({
      name: "BandRecruitmentList",
      query: nextQuery
    });
  }
}

async function handleEditFromDetail(item) {
  if (!canManageRecruitment(item)) {
    return;
  }

  detailModalOpen.value = false;
  await openEditModal(item);
}

function canManageRecruitment(item) {
  if (!item?.band?.id) {
    return false;
  }

  return leaderBands.value.some((band) => Number(band.id) === Number(item.band.id));
}

async function getRecruitmentDetail(id) {
  const numericId = Number(id);

  if (detailById.value[numericId]) {
    return detailById.value[numericId];
  }

  detailLoading.value = true;
  detailError.value = "";

  try {
    const detail = await BandRecruitmentRepository.getById(numericId);
    detailById.value = {
      ...detailById.value,
      [numericId]: detail
    };
    return detail;
  } catch (error) {
    detailError.value = getApiErrorMessage(error, t, "recruitmentBoard.detail.error");
    return null;
  } finally {
    detailLoading.value = false;
  }
}

async function clearCreateQuery() {
  const nextQuery = { ...route.query };
  delete nextQuery.mode;
  delete nextQuery.bandId;

  await router.replace({
    name: "BandRecruitmentList",
    query: nextQuery
  });
}

function createDefaultFilters() {
  return {
    searchText: "",
    instrumentId: "",
    levelRequired: "",
    city: "",
    genre: "",
    order: "recent"
  };
}

function createEmptyRecruitmentForm(bandId = "", city = "") {
  return {
    bandId: bandId ? String(bandId) : "",
    title: "",
    description: "",
    roleWanted: "",
    levelRequired: "INTERMEDIATE",
    city: city || "",
    vacancies: "1",
    instrumentId: ""
  };
}

function mapRecruitmentToForm(item) {
  return {
    bandId: item.band?.id ? String(item.band.id) : "",
    title: item.title || "",
    description: item.description || "",
    roleWanted: item.roleWanted || "",
    levelRequired: item.levelRequired || "INTERMEDIATE",
    city: item.city || item.band?.baseCity || "",
    vacancies: String(item.vacancies ?? 1),
    instrumentId: item.instrument?.id ? String(item.instrument.id) : ""
  };
}

function sortRecruitments(left, right) {
  if (filters.value.order === "city") {
    return `${left.city || left.band?.baseCity || ""}${left.title || ""}`
      .localeCompare(`${right.city || right.band?.baseCity || ""}${right.title || ""}`);
  }

  if (filters.value.order === "instrument") {
    return `${left.instrument?.name || left.roleWanted || ""}${left.title || ""}`
      .localeCompare(`${right.instrument?.name || right.roleWanted || ""}${right.title || ""}`);
  }

  if (filters.value.order === "vacancies") {
    return Number(right.vacancies || 0) - Number(left.vacancies || 0);
  }

  return new Date(right.publicationDate || 0).getTime() - new Date(left.publicationDate || 0).getTime();
}

function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}
</script>

<style scoped>
.recruitment-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.recruitment-shell {
  padding-bottom: 3rem;
}

.recruitment-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.recruitment-header__copy {
  max-width: 760px;
}

.recruitment-header__eyebrow,
.recruitment-results__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.recruitment-header h1 {
  margin: 0.4rem 0 0.6rem;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: 0;
}

.recruitment-header p {
  margin: 0;
  color: #b6b6b6;
  line-height: 1.75;
}

.recruitment-header__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.recruitment-view-switch {
  display: inline-flex;
  padding: 0.3rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.recruitment-view-switch__button {
  min-height: 40px;
  padding: 0 1rem;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: #cfcfcf;
  font-weight: 700;
}

.recruitment-view-switch__button.is-active {
  background: #1db954;
  color: #041106;
}

.recruitment-header__cta {
  min-height: 46px;
  padding-inline: 1.15rem;
  border-radius: 16px;
}

.section-spacing {
  margin-top: 1.2rem;
}

.recruitment-results {
  padding: 1.35rem;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.recruitment-results__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.recruitment-results__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.4rem;
}

.recruitment-results__count {
  color: #b9b9b9;
}

.recruitment-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.page-notice,
.state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice--warning {
  color: #fff2c5;
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.page-notice--error,
.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  text-align: center;
}

@media (max-width: 1199.98px) {
  .recruitment-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 991.98px) {
  .recruitment-header,
  .recruitment-results__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .recruitment-header__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 767.98px) {
  .recruitment-view-switch,
  .recruitment-header__actions {
    width: 100%;
  }

  .recruitment-view-switch__button,
  .recruitment-header__cta {
    flex: 1;
  }
}
</style>
