<template>
  <div class="instrument-page">
    <section class="instrument-shell">
      <div class="container py-5">
        <header class="instrument-header">
          <div class="instrument-header__copy">
            <span class="instrument-header__eyebrow">{{ t("instrumentBoard.header.eyebrow") }}</span>
            <h1>{{ t("instrumentBoard.header.title") }}</h1>
            <p>{{ t("instrumentBoard.header.subtitle") }}</p>
          </div>

          <div class="instrument-header__actions">
            <div v-if="canUseUserFeatures" class="instrument-view-switch">
              <button
                type="button"
                class="instrument-view-switch__button"
                :class="{ 'is-active': currentView === 'catalog' }"
                @click="setView('catalog')"
              >
                {{ t("instrumentBoard.header.catalog") }}
              </button>
              <button
                type="button"
                class="instrument-view-switch__button"
                :class="{ 'is-active': currentView === 'mine' }"
                @click="setView('mine')"
              >
                {{ t("instrumentBoard.header.mine") }}
              </button>
            </div>

            <button
              v-if="showInlineAdminActions"
              type="button"
              class="btn btn-success instrument-header__cta"
              @click="openCreateModal"
            >
              {{ t("instrumentBoard.header.create") }}
            </button>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("instrumentBoard.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("instrumentBoard.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("instrumentBoard.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <InstrumentStatsRow :cards="statsCards" />

          <section v-if="currentView === 'catalog'" class="section-spacing">
            <InstrumentFiltersBar
              v-model="filters"
              :category-options="categoryOptions"
              @clear="resetFilters"
            />
          </section>

          <section class="instrument-results section-spacing">
            <div class="instrument-results__header">
              <div>
                <span class="instrument-results__eyebrow">{{ t("instrumentBoard.list.eyebrow") }}</span>
                <h2>{{ listTitle }}</h2>
              </div>
              <span class="instrument-results__count">
                {{
                  currentView === "catalog"
                    ? t("instrumentBoard.list.total", { count: visibleCatalog.length })
                    : t("instrumentBoard.list.total", { count: myInstrumentItems.length })
                }}
              </span>
            </div>

            <InstrumentEmptyState
              v-if="showEmptyState"
              :mine="currentView === 'mine'"
              :filtered="Boolean(currentView === 'catalog' && instruments.length && !visibleCatalog.length)"
              :logged="canUseUserFeatures"
              @clear="resetFilters"
              @explore="setView('catalog')"
            />

            <div v-else class="instrument-grid">
              <InstrumentCard
                v-for="instrument in currentItems"
                :key="instrument.id"
                :instrument="instrument"
                :logged="canUseUserFeatures"
                :owned="myInstrumentIds.has(Number(instrument.id))"
                :admin="showInlineAdminActions"
                :busy="actionInstrumentId === instrument.id"
                @toggle="toggleMyInstrument"
                @edit="openEditModal"
              />
            </div>
          </section>
        </template>
      </div>
    </section>

    <InstrumentFormModal
      :open="formModalOpen"
      :model-value="instrumentForm"
      :editing="Boolean(editingInstrumentId)"
      :submitting="formSubmitting"
      :error-message="formErrorMessage"
      @close="closeFormModal"
      @submit="submitInstrument"
      @update:model-value="instrumentForm = $event"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import InstrumentRepository from "@/repositories/InstrumentRepository";
import InstrumentCard from "../components/InstrumentCard.vue";
import InstrumentEmptyState from "../components/InstrumentEmptyState.vue";
import InstrumentFiltersBar from "../components/InstrumentFiltersBar.vue";
import InstrumentFormModal from "../components/InstrumentFormModal.vue";
import InstrumentStatsRow from "../components/InstrumentStatsRow.vue";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const instruments = ref([]);
const myInstruments = ref([]);

const filters = ref(createDefaultFilters());

const formModalOpen = ref(false);
const formSubmitting = ref(false);
const formErrorMessage = ref("");
const editingInstrumentId = ref(null);
const instrumentForm = ref(createEmptyInstrumentForm());

const actionInstrumentId = ref(null);

let modeRedirectInProgress = false;

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);
const showInlineAdminActions = false;
const requestedMineView = computed(() => route.query.view === "mine");
const currentView = computed(() =>
  requestedMineView.value && canUseUserFeatures.value ? "mine" : "catalog"
);

const myInstrumentIds = computed(() =>
  new Set(myInstruments.value.map((item) => Number(item.id)).filter(Number.isFinite))
);

const visibleCatalog = computed(() => {
  const needle = normalizeText(filters.value.searchText);

  return [...instruments.value]
    .filter((instrument) => {
      const haystack = normalizeText([
        instrument.name,
        t(`instrumentBoard.categories.${instrument.category || "OTHER"}`)
      ].join(" "));

      if (needle && !haystack.includes(needle)) {
        return false;
      }

      if (filters.value.category && instrument.category !== filters.value.category) {
        return false;
      }

      return true;
    })
    .sort(sortInstruments);
});

const myInstrumentItems = computed(() =>
  [...myInstruments.value].sort((left, right) => sortItems(left, right, "name"))
);

const currentItems = computed(() =>
  currentView.value === "mine" ? myInstrumentItems.value : visibleCatalog.value
);

const categoryOptions = computed(() =>
  [...new Set(instruments.value.map((item) => item.category).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const statsCards = computed(() => [
  {
    id: "total",
    icon: "bi bi-vinyl",
    value: instruments.value.length,
    label: t("instrumentBoard.stats.total")
  },
  {
    id: "categories",
    icon: "bi bi-tags",
    value: categoryOptions.value.length,
    label: t("instrumentBoard.stats.categories")
  },
  {
    id: "mine",
    icon: "bi bi-music-note-list",
    value: canUseUserFeatures.value ? myInstruments.value.length : "--",
    label: t("instrumentBoard.stats.mine")
  },
  {
    id: "voice",
    icon: "bi bi-mic",
    value: instruments.value.filter((item) => item.category === "VOICE").length,
    label: t("instrumentBoard.stats.voice")
  }
]);

const listTitle = computed(() =>
  currentView.value === "mine"
    ? t("instrumentBoard.list.mineTitle")
    : t("instrumentBoard.list.catalogTitle")
);

const showEmptyState = computed(() => {
  if (currentView.value === "mine") {
    return myInstrumentItems.value.length === 0;
  }

  return !instruments.value.length || !visibleCatalog.value.length;
});

watch(
  () => `${route.query.view || ""}|${route.query.mode || ""}|${isLogged.value}|${isAdmin.value}`,
  () => {
    loadPage();
  },
  { immediate: true }
);

async function loadPage() {
  if (requestedMineView.value && !canUseUserFeatures.value) {
    if (!isLogged.value) {
      await redirectToLogin();
      return;
    }

    await setView("catalog");
  }

  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;

  try {
    const [catalogResult, mineResult] = await Promise.allSettled([
      InstrumentRepository.getAll(),
      canUseUserFeatures.value ? InstrumentRepository.getMine() : Promise.resolve([])
    ]);

    if (catalogResult.status !== "fulfilled") {
      throw catalogResult.reason;
    }

    instruments.value = catalogResult.value ?? [];
    myInstruments.value = mineResult.status === "fulfilled" ? (mineResult.value ?? []) : [];

    if (mineResult.status === "rejected") {
      pageNotice.value = {
        type: "warning",
        message: t("instrumentBoard.notices.minePartial")
      };
    }

    await handleModeQuery();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "instrumentBoard.states.error");
  } finally {
    loading.value = false;
  }
}

async function handleModeQuery() {
  if (route.query.mode !== "create") {
    return;
  }

  if (!isLogged.value) {
    await redirectToLogin();
    return;
  }

  if (!showInlineAdminActions) {
    await clearCreateQuery();
    return;
  }

  openCreateModal();
}

async function redirectToLogin() {
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
}

async function setView(view) {
  const nextQuery = { ...route.query };

  if (view === "mine") {
    nextQuery.view = "mine";
  } else {
    delete nextQuery.view;
  }

  await router.replace({
    name: "InstrumentList",
    query: nextQuery
  });
}

function resetFilters() {
  filters.value = createDefaultFilters();
}

async function toggleMyInstrument(instrument) {
  if (!canUseUserFeatures.value) {
    if (!isLogged.value) {
      await redirectToLogin();
    }
    return;
  }

  if (!isLogged.value) {
    await redirectToLogin();
    return;
  }

  actionInstrumentId.value = instrument.id;

  const instrumentId = Number(instrument.id);
  const selected = myInstrumentIds.value.has(instrumentId);
  const nextIds = selected ? [] : [instrumentId];

  try {
    const updated = await InstrumentRepository.updateMine({
      instrumentIds: nextIds
    });

    myInstruments.value = updated ?? [];
    pageNotice.value = {
      type: "success",
      message: selected
        ? t("instrumentBoard.notices.removed")
        : t("instrumentBoard.notices.selected")
    };
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "instrumentBoard.notices.updateError")
    };
  } finally {
    actionInstrumentId.value = null;
  }
}

function openCreateModal() {
  if (!showInlineAdminActions) {
    return;
  }

  editingInstrumentId.value = null;
  instrumentForm.value = createEmptyInstrumentForm();
  formErrorMessage.value = "";
  formModalOpen.value = true;
}

function openEditModal(instrument) {
  if (!showInlineAdminActions) {
    return;
  }

  editingInstrumentId.value = instrument.id;
  instrumentForm.value = {
    name: instrument.name || "",
    category: instrument.category || ""
  };
  formErrorMessage.value = "";
  formModalOpen.value = true;
}

async function closeFormModal() {
  formModalOpen.value = false;
  formErrorMessage.value = "";

  if (route.query.mode === "create") {
    await clearCreateQuery();
  }
}

async function submitInstrument() {
  if (!showInlineAdminActions) {
    return;
  }

  const payload = {
    name: instrumentForm.value.name.trim(),
    category: instrumentForm.value.category
  };

  if (!payload.name || !payload.category) {
    formErrorMessage.value = t("instrumentBoard.form.validation");
    return;
  }

  formSubmitting.value = true;
  formErrorMessage.value = "";

  try {
    if (editingInstrumentId.value) {
      await InstrumentRepository.update(editingInstrumentId.value, payload);
      pageNotice.value = {
        type: "success",
        message: t("instrumentBoard.notices.updated")
      };
    } else {
      await InstrumentRepository.create(payload);
      pageNotice.value = {
        type: "success",
        message: t("instrumentBoard.notices.created")
      };
    }

    await closeFormModal();
    instruments.value = await InstrumentRepository.getAll();
  } catch (error) {
    formErrorMessage.value = getApiErrorMessage(error, t, "instrumentBoard.form.error");
  } finally {
    formSubmitting.value = false;
  }
}

async function clearCreateQuery() {
  const nextQuery = { ...route.query };
  delete nextQuery.mode;

  await router.replace({
    name: "InstrumentList",
    query: nextQuery
  });
}

function createDefaultFilters() {
  return {
    searchText: "",
    category: "",
    order: "name"
  };
}

function createEmptyInstrumentForm() {
  return {
    name: "",
    category: ""
  };
}

function sortInstruments(left, right) {
  return sortItems(left, right, filters.value.order);
}

function sortItems(left, right, order) {
  if (order === "category") {
    return `${left.category || ""}${left.name || ""}`.localeCompare(`${right.category || ""}${right.name || ""}`);
  }

  return `${left.name || ""}`.localeCompare(`${right.name || ""}`);
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
.instrument-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.instrument-shell {
  padding-bottom: 3rem;
}

.instrument-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1.5rem;
}

.instrument-header__eyebrow,
.instrument-results__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.instrument-header h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
}

.instrument-header p {
  max-width: 700px;
  margin: 0.85rem 0 0;
  color: #b8b8b8;
  font-size: 1rem;
  line-height: 1.7;
}

.instrument-header__actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 1rem;
}

.instrument-view-switch {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.35rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.05);
}

.instrument-view-switch__button {
  min-height: 42px;
  padding: 0 1rem;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #bcbcbc;
  font-weight: 600;
}

.instrument-view-switch__button.is-active {
  background: rgba(29, 185, 84, 0.16);
  color: #ffffff;
}

.instrument-header__cta {
  min-height: 44px;
  border-radius: 14px;
}

.section-spacing {
  margin-top: 1.5rem;
}

.page-notice {
  margin-top: 1.25rem;
  padding: 0.95rem 1rem;
  border-radius: 18px;
  border: 1px solid transparent;
}

.page-notice--success {
  background: rgba(29, 185, 84, 0.1);
  border-color: rgba(29, 185, 84, 0.2);
  color: #d4ffe4;
}

.page-notice--warning {
  background: rgba(255, 193, 7, 0.1);
  border-color: rgba(255, 193, 7, 0.18);
  color: #ffe8a3;
}

.page-notice--error {
  background: rgba(220, 53, 69, 0.12);
  border-color: rgba(220, 53, 69, 0.22);
  color: #ffb3bd;
}

.state-card {
  margin-top: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.85rem;
  padding: 2rem;
  text-align: center;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.state-card p {
  margin: 0;
  color: #c4c4c4;
}

.state-card--error {
  align-items: flex-start;
  text-align: left;
}

.instrument-results {
  padding: 1.35rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.instrument-results__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
}

.instrument-results__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
  font-weight: 700;
}

.instrument-results__count {
  color: #c9c9c9;
  font-size: 0.95rem;
}

.instrument-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

@media (max-width: 1199.98px) {
  .instrument-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .instrument-header,
  .instrument-results__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .instrument-header__actions {
    width: 100%;
    align-items: stretch;
  }
}

@media (max-width: 575.98px) {
  .instrument-grid {
    grid-template-columns: 1fr;
  }
}
</style>
