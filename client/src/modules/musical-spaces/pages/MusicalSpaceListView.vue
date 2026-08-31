<template>
  <div class="space-list-page">
    <section class="space-list-shell">
      <div class="container py-5">
        <header class="space-list-header">
          <div class="space-list-header__copy">
            <h1>{{ t("spaceList.header.title") }}</h1>
            <p>{{ t("spaceList.header.subtitle") }}</p>
          </div>

          <div class="space-list-header__actions">
            <div v-if="canUseUserFeatures" class="space-view-switch">
              <button
                type="button"
                class="space-view-switch__button"
                :class="{ 'is-active': currentView === 'explore' }"
                @click="setView('explore')"
              >
                <i class="bi bi-compass" aria-hidden="true"></i>
                {{ t("spaceList.header.explore") }}
              </button>
              <button
                type="button"
                class="space-view-switch__button"
                :class="{ 'is-active': currentView === 'mine' }"
                @click="setView('mine')"
              >
                <i class="bi bi-person-workspace" aria-hidden="true"></i>
                {{ t("spaceList.header.mine") }}
              </button>
            </div>

            <button
              v-if="canUseUserFeatures"
              type="button"
              class="btn btn-success space-list-header__cta"
              @click="openCreateModal"
            >
              <i class="bi bi-plus-lg" aria-hidden="true"></i>
              {{ t("spaceList.header.create") }}
            </button>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("spaceList.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("spaceList.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadSpaces">
            {{ t("spaceList.states.retry") }}
          </button>
        </div>

        <template v-else>
          <section class="section-spacing">
            <MusicalSpaceFiltersBar
              v-model="filters"
              :city-options="cityOptions"
              :type-options="typeOptions"
              @clear="resetFilters"
            />
          </section>

          <section class="space-results section-spacing">
            <div class="space-results__header">
              <div>
                <h2>{{ listTitle }}</h2>
              </div>
              <span class="space-results__count">
                {{ t("spaceList.list.total", { count: visibleSpaces.length }) }}
              </span>
            </div>

            <MusicalSpaceEmptyState
              v-if="!rawSpaces.length || !visibleSpaces.length"
              :mine="currentView === 'mine'"
              :filtered="Boolean(rawSpaces.length && !visibleSpaces.length)"
              :logged="canUseUserFeatures"
              @create="openCreateModal"
              @clear="resetFilters"
            />

            <div v-else class="space-grid">
              <MusicalSpaceListCard
                v-for="space in visibleSpaces"
                :key="space.id"
                :space="space"
                :mine="currentView === 'mine'"
                :busy="actionSpaceId === space.id"
                @edit="openEditModal"
                @deactivate="handleDeactivate"
              />
            </div>
          </section>
        </template>
      </div>
    </section>

    <MusicalSpaceFormModal
      :open="formModalOpen"
      :model-value="spaceForm"
      :editing="Boolean(editingSpaceId)"
      :submitting="formSubmitting"
      :error-message="formErrorMessage"
      @close="closeFormModal"
      @submit="submitSpaceForm"
      @update:model-value="spaceForm = $event"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import SpaceReviewRepository from "@/repositories/SpaceReviewRepository";
import MusicalSpaceEmptyState from "../components/MusicalSpaceEmptyState.vue";
import MusicalSpaceFiltersBar from "../components/MusicalSpaceFiltersBar.vue";
import MusicalSpaceFormModal from "../components/MusicalSpaceFormModal.vue";
import MusicalSpaceListCard from "../components/MusicalSpaceListCard.vue";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const rawSpaces = ref([]);
const metaById = ref({});
const actionSpaceId = ref(null);

const formModalOpen = ref(false);
const formSubmitting = ref(false);
const formErrorMessage = ref("");
const editingSpaceId = ref(null);
const spaceForm = ref(createEmptySpaceForm());

const filters = ref(createDefaultFilters());

const loadingDetailIds = new Set();
const loadingRatingIds = new Set();
let modeRedirectInProgress = false;
let nextLoadNotice = null;

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);
const requestedMineView = computed(() => route.query.view === "mine");
const currentView = computed(() =>
  requestedMineView.value && canUseUserFeatures.value ? "mine" : "explore"
);

const spaceModels = computed(() =>
  rawSpaces.value.map((space) => {
    const meta = metaById.value[space.id] ?? {};

    return {
      ...space,
      ...meta,
      city: meta.location?.city ?? space.city ?? "",
      province: meta.location?.province ?? space.province ?? "",
      mainImage: meta.mainImage ?? space.mainImage ?? "",
      description: meta.description ?? "",
      squareMeters: meta.squareMeters ?? null,
      soundproofed: typeof meta.soundproofed === "boolean" ? meta.soundproofed : null,
      rating: meta.rating ?? null,
      reviewsCount: meta.reviewsCount ?? 0,
      active: typeof meta.active === "boolean" ? meta.active : space.active,
      approvalStatus: meta.approvalStatus ?? space.approvalStatus
    };
  })
);

const visibleSpaces = computed(() => {
  const text = normalizeText(filters.value.searchText);
  const minCapacity = Number(filters.value.minCapacity) || 0;

  return [...spaceModels.value]
    .filter((space) => {
      const haystack = normalizeText([
        space.name,
        space.city,
        space.province,
        t(`spaceDetail.spaceTypeLabels.${space.spaceType || "OTHER"}`)
      ].join(" "));

      if (text && !haystack.includes(text)) {
        return false;
      }

      if (filters.value.city && space.city !== filters.value.city) {
        return false;
      }

      if (filters.value.spaceType && space.spaceType !== filters.value.spaceType) {
        return false;
      }

      if (minCapacity > 0 && Number(space.capacity || 0) < minCapacity) {
        return false;
      }

      if (filters.value.soundproofed === "yes" && space.soundproofed !== true) {
        return false;
      }

      if (filters.value.soundproofed === "no" && space.soundproofed !== false) {
        return false;
      }

      return true;
    })
    .sort(sortSpaces);
});

const cityOptions = computed(() =>
  [...new Set(spaceModels.value.map((space) => space.city).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const typeOptions = computed(() =>
  [...new Set(spaceModels.value.map((space) => space.spaceType).filter(Boolean))]
);

const listTitle = computed(() =>
  currentView.value === "mine" ? t("spaceList.list.mineTitle") : t("spaceList.list.exploreTitle")
);

watch(
  () => `${route.query.view || ""}|${canUseUserFeatures.value}`,
  () => {
    loadSpaces();
  },
  { immediate: true }
);

watch(
  () => rawSpaces.value.map((space) => space.id).join(","),
  (signature) => {
    if (!signature) {
      return;
    }

    ensureDetails(rawSpaces.value.map((space) => space.id));
  },
  { immediate: true }
);

watch(
  () => route.query.mode,
  () => {
    handleModeQuery();
  },
  { immediate: true }
);

watch(
  () => visibleSpaces.value.slice(0, 12).map((space) => space.id).join(","),
  (signature) => {
    if (!signature) {
      return;
    }

    ensureRatings(visibleSpaces.value.slice(0, 12).map((space) => space.id));
  },
  { immediate: true }
);

async function loadSpaces() {
  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;

  try {
    if (requestedMineView.value && !canUseUserFeatures.value) {
      pageNotice.value = {
        type: "warning",
        message: t("spaceList.notices.privateFallback")
      };
    }

    rawSpaces.value =
      currentView.value === "mine"
        ? await MusicalSpaceRepository.getMine()
        : await MusicalSpaceRepository.getAll();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "spaceList.states.error");
  } finally {
    if (nextLoadNotice) {
      pageNotice.value = nextLoadNotice;
      nextLoadNotice = null;
    }

    loading.value = false;
  }
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
    const nextQuery = { ...route.query };
    delete nextQuery.mode;
    await router.replace({ name: "MusicalSpaceList", query: nextQuery });
    return;
  }

  openCreateModal();
}

async function setView(view) {
  const nextQuery = { ...route.query };

  if (view === "mine") {
    nextQuery.view = "mine";
  } else {
    delete nextQuery.view;
  }

  await router.replace({
    name: "MusicalSpaceList",
    query: nextQuery
  });
}

function resetFilters() {
  filters.value = createDefaultFilters();
}

function openCreateModal() {
  if (!canUseUserFeatures.value) {
    if (!isLogged.value) {
      router.push({ name: "Login", query: { redirect: route.fullPath } });
    }
    return;
  }

  editingSpaceId.value = null;
  spaceForm.value = createEmptySpaceForm();
  formErrorMessage.value = "";
  formModalOpen.value = true;
}

async function openEditModal(space) {
  if (!canUseUserFeatures.value) {
    return;
  }

  actionSpaceId.value = space.id;
  formErrorMessage.value = "";

  try {
    const detail = await MusicalSpaceRepository.getById(space.id);
    metaById.value = {
      ...metaById.value,
      [space.id]: {
        ...(metaById.value[space.id] ?? {}),
        ...detail
      }
    };
    editingSpaceId.value = space.id;
    spaceForm.value = mapSpaceToForm(detail);
    formModalOpen.value = true;
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "spaceList.notices.editLoadError")
    };
  } finally {
    actionSpaceId.value = null;
  }
}

async function closeFormModal() {
  formModalOpen.value = false;
  formErrorMessage.value = "";

  if (route.query.mode === "create") {
    const nextQuery = { ...route.query };
    delete nextQuery.mode;

    await router.replace({
      name: "MusicalSpaceList",
      query: nextQuery
    });
  }
}

async function submitSpaceForm() {
  if (!canUseUserFeatures.value) {
    return;
  }

  const payload = buildSpacePayload(spaceForm.value);

  if (!payload) {
    formErrorMessage.value = t("spaceList.form.validation");
    return;
  }

  formSubmitting.value = true;
  formErrorMessage.value = "";

  try {
    if (editingSpaceId.value) {
      const updated = await MusicalSpaceRepository.update(editingSpaceId.value, payload);
      metaById.value = {
        ...metaById.value,
        [updated.id]: {
          ...(metaById.value[updated.id] ?? {}),
          ...updated
        }
      };
      nextLoadNotice = {
        type: "success",
        message: t("spaceList.notices.updated")
      };
      await loadSpaces();
    } else {
      const created = await MusicalSpaceRepository.create(payload);
      metaById.value = {
        ...metaById.value,
        [created.id]: {
          ...(metaById.value[created.id] ?? {}),
          ...created
        }
      };
      nextLoadNotice = {
        type: "success",
        message: t("spaceList.notices.createdPending")
      };

      if (currentView.value !== "mine") {
        await setView("mine");
      } else {
        await loadSpaces();
      }
    }

    await closeFormModal();
  } catch (error) {
    formErrorMessage.value = getApiErrorMessage(error, t, "spaceList.form.submitError");
  } finally {
    formSubmitting.value = false;
  }
}

async function handleDeactivate(space) {
  if (!canUseUserFeatures.value) {
    return;
  }

  const confirmed = window.confirm(
    t("spaceList.confirm.deactivate", { name: space.name })
  );

  if (!confirmed) {
    return;
  }

  actionSpaceId.value = space.id;

  try {
    const updated = await MusicalSpaceRepository.deactivate(space.id);
    metaById.value = {
      ...metaById.value,
      [space.id]: {
        ...(metaById.value[space.id] ?? {}),
        ...updated
      }
    };
    nextLoadNotice = {
      type: "success",
      message: t("spaceList.notices.deactivated")
    };
    await loadSpaces();
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "spaceList.notices.deactivateError")
    };
  } finally {
    actionSpaceId.value = null;
  }
}

function ensureDetails(ids) {
  ids.forEach((id) => {
    if (hasSpaceDetail(metaById.value[id]) || loadingDetailIds.has(id)) {
      return;
    }

    loadDetail(id);
  });
}

function ensureRatings(ids) {
  ids.forEach((id) => {
    const currentMeta = metaById.value[id];

    if (currentMeta?.ratingLoaded || loadingRatingIds.has(id)) {
      return;
    }

    loadRating(id);
  });
}

async function loadDetail(id) {
  loadingDetailIds.add(id);

  try {
    const detail = await MusicalSpaceRepository.getById(id);

    metaById.value = {
      ...metaById.value,
      [id]: {
        ...(metaById.value[id] ?? {}),
        ...detail
      }
    };
  } finally {
    loadingDetailIds.delete(id);
  }
}

async function loadRating(id) {
  loadingRatingIds.add(id);

  try {
    const rating = await SpaceReviewRepository.getSpaceRating(id);

    metaById.value = {
      ...metaById.value,
      [id]: {
        ...(metaById.value[id] ?? {}),
        rating: Number(rating.averageOverallRating ?? 0),
        reviewsCount: Number(rating.reviewsCount ?? 0),
        ratingLoaded: true
      }
    };
  } catch {
    metaById.value = {
      ...metaById.value,
      [id]: {
        ...(metaById.value[id] ?? {}),
        rating: null,
        reviewsCount: 0,
        ratingLoaded: true
      }
    };
  } finally {
    loadingRatingIds.delete(id);
  }
}

function hasSpaceDetail(space) {
  return Boolean(space?.location || space?.description != null || space?.squareMeters != null || typeof space?.soundproofed === "boolean");
}

function sortSpaces(left, right) {
  if (filters.value.order === "capacity") {
    return Number(right.capacity || 0) - Number(left.capacity || 0);
  }

  if (filters.value.order === "city") {
    return `${left.city || ""}${left.name || ""}`.localeCompare(`${right.city || ""}${right.name || ""}`);
  }

  if (filters.value.order === "name") {
    return `${left.name || ""}`.localeCompare(`${right.name || ""}`);
  }

  const leftRating = Number(left.rating || 0);
  const rightRating = Number(right.rating || 0);

  if (leftRating !== rightRating) {
    return rightRating - leftRating;
  }

  return `${left.name || ""}`.localeCompare(`${right.name || ""}`);
}

function createDefaultFilters() {
  return {
    searchText: "",
    city: "",
    spaceType: "",
    minCapacity: "",
    soundproofed: "",
    order: "rating"
  };
}

function createEmptySpaceForm() {
  return {
    name: "",
    description: "",
    spaceType: "REHEARSAL_ROOM",
    capacity: "",
    squareMeters: "",
    soundproofed: true,
    mainImage: "",
    street: "",
    portal: "",
    floor: "",
    postalCode: "",
    city: "",
    province: "",
    country: "",
    latitude: "",
    longitude: ""
  };
}

function mapSpaceToForm(space) {
  return {
    name: space.name ?? "",
    description: space.description ?? "",
    spaceType: space.spaceType ?? "REHEARSAL_ROOM",
    capacity: String(space.capacity ?? ""),
    squareMeters: String(space.squareMeters ?? ""),
    soundproofed: Boolean(space.soundproofed),
    mainImage: space.mainImage ?? "",
    street: space.location?.street ?? "",
    portal: space.location?.portal ?? "",
    floor: space.location?.floor ?? "",
    postalCode: space.location?.postalCode ?? "",
    city: space.location?.city ?? "",
    province: space.location?.province ?? "",
    country: space.location?.country ?? "",
    latitude: space.location?.latitude ?? "",
    longitude: space.location?.longitude ?? ""
  };
}

function buildSpacePayload(form) {
  const capacity = Number(form.capacity);
  const squareMeters = Number(form.squareMeters);

  if (
    !form.name.trim() ||
    !form.spaceType ||
    !Number.isFinite(capacity) ||
    capacity < 1 ||
    !Number.isFinite(squareMeters) ||
    squareMeters < 0.1 ||
    !form.country.trim() ||
    !form.province.trim() ||
    !form.city.trim() ||
    !form.street.trim() ||
    !form.postalCode.trim()
  ) {
    return null;
  }

  return {
    name: form.name.trim(),
    description: form.description.trim() || null,
    spaceType: form.spaceType,
    capacity,
    mainImage: form.mainImage.trim() || null,
    squareMeters,
    soundproofed: Boolean(form.soundproofed),
    location: {
      country: form.country.trim(),
      province: form.province.trim(),
      city: form.city.trim(),
      street: form.street.trim(),
      portal: form.portal.trim() || null,
      floor: form.floor.trim() || null,
      postalCode: form.postalCode.trim(),
      latitude: parseNullableNumber(form.latitude),
      longitude: parseNullableNumber(form.longitude)
    }
  };
}

function parseNullableNumber(value) {
  if (value === "" || value == null) {
    return null;
  }

  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
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
.space-list-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.space-list-shell {
  padding-bottom: 3rem;
}

.space-list-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.5rem 0 1.45rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.space-list-header__copy {
  max-width: 760px;
}

.space-list-header h1 {
  margin: 0 0 0.6rem;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: -0.04em;
}

.space-list-header p {
  margin: 0;
  color: #b6b6b6;
  line-height: 1.75;
}

.space-list-header__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 0.25rem;
}

.space-view-switch {
  display: inline-flex;
  gap: 0.25rem;
  padding: 0.28rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.025);
}

.space-view-switch__button {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 40px;
  padding: 0 1rem;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: #cfcfcf;
  font-weight: 700;
}

.space-view-switch__button.is-active {
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  box-shadow: 0 10px 24px rgba(29, 185, 84, 0.2);
}

.space-list-header__cta {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 46px;
  padding-inline: 1.15rem;
  border-radius: 16px;
  border: 0;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  font-weight: 800;
  box-shadow: 0 14px 30px rgba(29, 185, 84, 0.2);
}

.section-spacing {
  margin-top: 1.35rem;
}

.space-results {
  padding: 1.35rem;
  border-radius: 30px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.08), transparent 28%),
    linear-gradient(180deg, rgba(17, 17, 17, 0.92) 0%, rgba(24, 24, 24, 0.92) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.space-results__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.space-results__header h2 {
  margin: 0;
  font-size: 1.4rem;
}

.space-results__count {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.055);
  color: #d7d7d7;
  font-weight: 700;
}

.space-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
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
  .space-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .space-list-header,
  .space-results__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .space-list-header__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 767.98px) {
  .space-grid {
    grid-template-columns: 1fr;
  }

  .space-view-switch,
  .space-list-header__actions {
    width: 100%;
  }

  .space-view-switch__button,
  .space-list-header__cta {
    flex: 1;
  }
}
</style>
