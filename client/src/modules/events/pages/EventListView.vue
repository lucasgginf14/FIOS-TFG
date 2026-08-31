<template>
  <div class="events-page">
    <section class="events-shell">
      <div class="container py-5">
        <header class="events-header">
          <div class="events-header__copy">
            <span class="events-header__eyebrow">{{ t("events.header.eyebrow") }}</span>
            <h1>{{ t("events.header.title") }}</h1>
            <p>{{ t("events.header.subtitle") }}</p>
          </div>

          <div class="events-header__actions">
            <button
              v-if="canUseUserFeatures"
              type="button"
              class="btn btn-success events-header__create"
              :disabled="userEventDataLoading || userEventSubmitting"
              @click="openUserEventModal"
            >
              <i class="bi bi-calendar-plus" aria-hidden="true"></i>
              {{ t("bands.actions.publishEvent") }}
            </button>

            <div class="events-view-toggle">
              <button
                type="button"
                class="events-view-toggle__button"
                :class="{ 'is-active': viewMode === 'list' }"
                @click="setViewMode('list')"
              >
                {{ t("events.actions.list") }}
              </button>
              <button
                type="button"
                class="events-view-toggle__button"
                :class="{ 'is-active': viewMode === 'map' }"
                @click="setViewMode('map')"
              >
                {{ t("events.actions.map") }}
              </button>
            </div>

            <template v-if="showInlineAdminActions">
              <button type="button" class="btn btn-outline-light" @click="openTicketmasterModal">
                {{ t("events.actions.importTicketmaster") }}
              </button>
              <button type="button" class="btn btn-success" @click="openCreateModal">
                {{ t("events.actions.createEvent") }}
              </button>
            </template>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
          {{ pageNotice.message }}
        </div>

        <EventFiltersBar
          v-model="filters"
          :genre-options="genreOptions"
          :type-options="eventTypeOptions"
          :source-options="eventSourceOptions"
          @apply="applyFilters"
          @clear="clearFilters"
        />

        <EventQuickTabs
          class="section-spacing"
          :tabs="quickTabs"
          :model-value="quickTab"
          @update:model-value="handleQuickTabChange"
        />

        <div v-if="loading" class="state-card section-spacing">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("events.states.loading") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error section-spacing">
          <strong>{{ t("events.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("events.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <section class="events-panel section-spacing">
            <div class="events-panel__header">
              <div>
                <span class="events-panel__eyebrow">{{ t("events.list.eyebrow") }}</span>
                <h2>{{ t("events.list.title") }}</h2>
              </div>
              <span class="events-panel__count">
                {{ t("events.list.total", { count: visibleEvents.length }) }}
              </span>
            </div>

            <div v-if="viewMode === 'map'">
              <div v-if="mapLoading" class="state-card state-card--soft">
                <p>{{ t("events.map.loading") }}</p>
              </div>
              <EventMapView
                v-else
                :items="visibleMapItems"
                :selected-id="null"
                :empty-title="t('events.map.emptyTitle')"
                :empty-text="t('events.map.emptyText')"
                @select="openEventDetail($event.id)"
              />
            </div>

            <div v-if="visibleEvents.length" class="events-grid" :class="{ 'events-grid--list': viewMode === 'map' }">
              <EventCard v-for="event in visibleEvents" :key="event.id" :event="event" />
            </div>

            <div v-else-if="events.length" class="state-card state-card--soft">
              <strong>{{ t("events.empty.filteredTitle") }}</strong>
              <p>{{ t("events.empty.filteredText") }}</p>
            </div>

            <EventEmptyState
              v-else
              :title="t('events.empty.title')"
              :text="t('events.empty.text')"
              :action-label="t('events.empty.action')"
              @explore="clearFilters"
            />
          </section>
        </template>
      </div>
    </section>

    <EventAdminModal
      :open="adminModalOpen"
      :model-value="eventForm"
      :editing="Boolean(editingEventId)"
      :submitting="adminSubmitting"
      :error-message="adminModalError"
      @close="closeAdminModal"
      @submit="submitAdminEvent"
      @update:model-value="eventForm = $event"
    />

    <BandEventCreateModal
      :open="userEventModalOpen"
      :model-value="userEventForm"
      :bands="userEventPublishBands"
      :spaces="publicSpaces"
      :submitting="userEventSubmitting"
      :error-message="userEventModalError"
      @close="closeUserEventModal"
      @submit="submitUserEvent"
      @update:modelValue="userEventForm = $event"
    />

    <TicketmasterImportModal
      :open="ticketmasterModalOpen"
      :model-value="ticketmasterFilters"
      :minimum-start-date="ticketmasterMinimumStartDate"
      :results="ticketmasterResults"
      :searching="ticketmasterSearching"
      :bulk-importing="ticketmasterBulkImporting"
      :importing-id="ticketmasterImportingId"
      :error-message="ticketmasterError"
      :has-searched="ticketmasterHasSearched"
      @close="closeTicketmasterModal"
      @search="searchTicketmaster"
      @import="importTicketmasterEvent"
      @import-all="importTicketmasterResults"
      @update:model-value="ticketmasterFilters = $event"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import BandMemberRepository from "@/repositories/BandMemberRepository";
import BandRepository from "@/repositories/BandRepository";
import EventRepository from "@/repositories/EventRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import TicketmasterRepository from "@/repositories/TicketmasterRepository";
import BandEventCreateModal from "@/modules/bands/components/BandEventCreateModal.vue";
import EventAdminModal from "../components/EventAdminModal.vue";
import EventCard from "../components/EventCard.vue";
import EventEmptyState from "../components/EventEmptyState.vue";
import EventFiltersBar from "../components/EventFiltersBar.vue";
import EventMapView from "../components/EventMapView.vue";
import EventQuickTabs from "../components/EventQuickTabs.vue";
import {
  EVENT_SOURCE_KEYS,
  EVENT_TYPE_KEYS,
  TICKETMASTER_MIN_START_DATE,
  buildAdminEventPayload,
  buildBandEventPayload,
  buildEventQueryFromRoute,
  buildEventRouteQuery,
  buildMapIntersection,
  createEmptyEventForm,
  createEventFilters,
  filterEvents,
  getEventTicketValidationKey,
  hasMeaningfulEventFilters,
  matchesQuickTab,
  normalizeTicketmasterSearch,
  resolveTicketmasterErrorMessage,
  sortEvents
} from "../eventUtils";
import TicketmasterImportModal from "../components/TicketmasterImportModal.vue";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { t } = useI18n();

const loading = ref(true);
const mapLoading = ref(false);
const fatalError = ref("");
const pageNotice = ref(null);

const filters = ref(buildEventQueryFromRoute(route.query));
const quickTab = ref(typeof route.query.quick === "string" ? route.query.quick : "upcoming");
const viewMode = ref(route.query.view === "map" ? "map" : "list");

const events = ref([]);
const mapItems = ref([]);
const userBands = ref([]);
const userBandMembers = ref({});
const publicSpaces = ref([]);

const adminModalOpen = ref(false);
const adminSubmitting = ref(false);
const adminModalError = ref("");
const editingEventId = ref(null);
const eventForm = ref(createEmptyEventForm());

const userEventModalOpen = ref(false);
const userEventSubmitting = ref(false);
const userEventDataLoading = ref(false);
const userEventModalError = ref("");
const userEventForm = ref(createEmptyEventForm());

const ticketmasterModalOpen = ref(false);
const ticketmasterSearching = ref(false);
const ticketmasterBulkImporting = ref(false);
const ticketmasterImportingId = ref("");
const ticketmasterError = ref("");
const ticketmasterResults = ref([]);
const ticketmasterHasSearched = ref(false);
const ticketmasterFilters = ref({
  city: "",
  keyword: "",
  musicalGenre: "",
  startDate: TICKETMASTER_MIN_START_DATE,
  endDate: "",
  countryCode: "ES"
});

let syncingRoute = false;

const showInlineAdminActions = false;
const eventTypeOptions = EVENT_TYPE_KEYS;
const eventSourceOptions = EVENT_SOURCE_KEYS;
const ticketmasterMinimumStartDate = TICKETMASTER_MIN_START_DATE;
const canUseUserFeatures = computed(
  () => store.state.user.logged && store.state.user.platformRole !== "ADMIN"
);
const currentUserId = computed(() => Number(store.state.user.id || 0));

const genreOptions = computed(() =>
  [...new Set(events.value.map((item) => item.musicalGenre).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const visibleEvents = computed(() =>
  sortEvents(filterEvents(events.value, filters.value, quickTab.value), filters.value.order)
);

const visibleMapItems = computed(() => buildMapIntersection(mapItems.value, visibleEvents.value));

const userEventPublishBands = computed(() =>
  userBands.value.filter((band) => {
    const members = userBandMembers.value[band.id] ?? [];
    return (
      band.active &&
      members.some((member) =>
        Number(member.user?.id) === currentUserId.value &&
        member.roleInBand === "LEADER" &&
        member.active !== false
      )
    );
  })
);

const quickTabs = computed(() => {
  const baseFilter = { ...filters.value, freeOnly: false };

  return [
    { id: "upcoming", label: t("events.quickTabs.upcoming"), count: events.value.filter((item) => matchesQuickTab(item, "upcoming") && filterEvents([item], baseFilter, "").length).length },
    { id: "today", label: t("events.quickTabs.today"), count: events.value.filter((item) => matchesQuickTab(item, "today") && filterEvents([item], baseFilter, "").length).length },
    { id: "week", label: t("events.quickTabs.week"), count: events.value.filter((item) => matchesQuickTab(item, "week") && filterEvents([item], baseFilter, "").length).length },
    { id: "free", label: t("events.quickTabs.free"), count: events.value.filter((item) => matchesQuickTab(item, "free") && filterEvents([item], baseFilter, "").length).length },
    { id: "external", label: t("events.quickTabs.external"), count: events.value.filter((item) => matchesQuickTab(item, "external") && filterEvents([item], baseFilter, "").length).length }
  ];
});

onMounted(() => {
  syncNoticeFromRoute();
  loadPage();
});

watch(
  () => route.query,
  () => {
    if (syncingRoute) {
      return;
    }

    syncNoticeFromRoute();
    filters.value = buildEventQueryFromRoute(route.query);
    quickTab.value = typeof route.query.quick === "string" ? route.query.quick : "upcoming";
    viewMode.value = route.query.view === "map" ? "map" : "list";
    loadPage();
  }
);

async function loadPage() {
  loading.value = true;
  fatalError.value = "";

  try {
    await loadEvents();
    if (viewMode.value === "map") {
      await loadMapEvents();
    } else {
      mapItems.value = [];
    }
  } catch (error) {
    fatalError.value = getApiErrorMessage(error, t, "events.states.error");
  } finally {
    loading.value = false;
  }
}

async function loadEvents() {
  const backendParams = buildBackendParams();
  const useUpcoming = !hasMeaningfulEventFilters(filters.value) && !backendParams.city && !backendParams.date && !backendParams.musicalGenre && !backendParams.eventType && !backendParams.source;

  try {
    events.value = useUpcoming
      ? await EventRepository.getUpcoming()
      : await EventRepository.getAll(backendParams);
  } catch (error) {
    if (!useUpcoming) {
      throw error;
    }
    events.value = await EventRepository.getAll(backendParams);
  }
}

async function loadMapEvents() {
  mapLoading.value = true;

  try {
    mapItems.value = await EventRepository.getMap(buildBackendParams());
  } catch (error) {
    mapItems.value = [];
    pageNotice.value = {
      type: "warning",
      message: getApiErrorMessage(error, t, "events.map.error")
    };
  } finally {
    mapLoading.value = false;
  }
}

function buildBackendParams() {
  const params = {};

  if (filters.value.city) params.city = filters.value.city;
  if (filters.value.date) params.date = filters.value.date;
  if (filters.value.musicalGenre) params.musicalGenre = filters.value.musicalGenre;
  if (filters.value.eventType) params.eventType = filters.value.eventType;
  if (filters.value.source) params.source = filters.value.source;

  return params;
}

async function applyFilters() {
  await syncRouteState();
  await loadPage();
}

async function clearFilters() {
  filters.value = createEventFilters();
  quickTab.value = "upcoming";
  pageNotice.value = null;
  await syncRouteState();
  await loadPage();
}

async function handleQuickTabChange(value) {
  quickTab.value = value || "";
  await syncRouteState();
  await loadPage();
}

async function setViewMode(mode) {
  if (viewMode.value === mode) {
    return;
  }

  viewMode.value = mode;
  await syncRouteState();

  if (mode === "map") {
    await loadMapEvents();
  }
}

function openEventDetail(id) {
  router.push({ name: "EventDetail", params: { id } });
}

function openCreateModal() {
  editingEventId.value = null;
  eventForm.value = createEmptyEventForm();
  adminModalError.value = "";
  adminModalOpen.value = true;
}

function closeAdminModal() {
  adminModalOpen.value = false;
  adminModalError.value = "";
}

async function openUserEventModal() {
  if (!canUseUserFeatures.value) {
    return;
  }

  userEventDataLoading.value = true;
  userEventModalError.value = "";

  try {
    await loadUserEventCreationData();

    if (!userEventPublishBands.value.length) {
      pageNotice.value = {
        type: "warning",
        message: t("bands.placeholders.noLeaderEventBand")
      };
      return;
    }

    userEventForm.value = createEmptyUserEventForm();
    userEventModalOpen.value = true;
  } catch (error) {
    pageNotice.value = {
      type: "warning",
      message: getApiErrorMessage(error, t, "bands.modals.event.error")
    };
  } finally {
    userEventDataLoading.value = false;
  }
}

function closeUserEventModal() {
  userEventModalOpen.value = false;
  userEventModalError.value = "";
}

async function submitUserEvent() {
  const bandId = Number(userEventForm.value.bandId);
  const payload = buildBandEventPayload(userEventForm.value);

  if (
    !bandId ||
    !payload.title ||
    !payload.eventDate ||
    !payload.eventType ||
    !payload.venueName ||
    !payload.city ||
    !payload.country
  ) {
    userEventModalError.value = t("bands.modals.event.validation");
    return;
  }

  if (payload.startTime && payload.endTime && payload.startTime >= payload.endTime) {
    userEventModalError.value = t("bands.modals.event.timeValidation");
    return;
  }

  const userTicketValidationKey = getEventTicketValidationKey(payload);

  if (userTicketValidationKey) {
    userEventModalError.value = t(userTicketValidationKey);
    return;
  }

  userEventSubmitting.value = true;
  userEventModalError.value = "";

  try {
    await EventRepository.createForBand(bandId, payload);
    pageNotice.value = { type: "success", message: t("bands.modals.event.created") };
    closeUserEventModal();
  } catch (error) {
    userEventModalError.value = getApiErrorMessage(error, t, "bands.modals.event.error");
  } finally {
    userEventSubmitting.value = false;
  }
}

async function loadUserEventCreationData() {
  const [bandsResult, spacesResult] = await Promise.allSettled([
    BandRepository.getMine(),
    MusicalSpaceRepository.getAll()
  ]);

  if (bandsResult.status !== "fulfilled") {
    throw bandsResult.reason;
  }

  userBands.value = bandsResult.value ?? [];
  publicSpaces.value = spacesResult.status === "fulfilled" ? (spacesResult.value ?? []) : [];

  const memberEntries = await Promise.allSettled(
    userBands.value.map(async (band) => [band.id, await BandMemberRepository.getByBand(band.id)])
  );

  const nextMap = {};
  memberEntries.forEach((result) => {
    if (result.status === "fulfilled") {
      const [bandId, members] = result.value;
      nextMap[bandId] = members ?? [];
    }
  });

  userBandMembers.value = nextMap;

  if (spacesResult.status === "rejected") {
    pageNotice.value = {
      type: "warning",
      message: t("bands.states.partialData")
    };
  }
}

async function submitAdminEvent() {
  const payload = buildAdminEventPayload(eventForm.value);

  if (!payload.title || !payload.eventDate || !payload.venueName || !payload.city || !payload.country) {
    adminModalError.value = t("events.admin.validation");
    return;
  }

  const ticketValidationKey = getEventTicketValidationKey(payload);

  if (ticketValidationKey) {
    adminModalError.value = t(ticketValidationKey);
    return;
  }

  adminSubmitting.value = true;
  adminModalError.value = "";

  try {
    if (editingEventId.value) {
      await EventRepository.update(editingEventId.value, payload);
      pageNotice.value = { type: "success", message: t("events.admin.updated") };
    } else {
      await EventRepository.create(payload);
      pageNotice.value = { type: "success", message: t("events.admin.created") };
    }

    closeAdminModal();
    await loadPage();
  } catch (error) {
    adminModalError.value = getApiErrorMessage(error, t, "events.admin.error");
  } finally {
    adminSubmitting.value = false;
  }
}

function openTicketmasterModal() {
  ticketmasterFilters.value = createTicketmasterFiltersFromCurrentView();
  ticketmasterModalOpen.value = true;
  ticketmasterError.value = "";
  ticketmasterResults.value = [];
  ticketmasterHasSearched.value = false;
}

function closeTicketmasterModal() {
  ticketmasterModalOpen.value = false;
  ticketmasterError.value = "";
}

async function searchTicketmaster(nextFilters = null) {
  if (nextFilters) {
    ticketmasterFilters.value = nextFilters;
  }

  ticketmasterSearching.value = true;
  ticketmasterError.value = "";
  ticketmasterHasSearched.value = true;

  try {
    ticketmasterResults.value = await TicketmasterRepository.getEvents(
      normalizeTicketmasterSearch(ticketmasterFilters.value)
    );
  } catch (error) {
    ticketmasterResults.value = [];
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.error"));
  } finally {
    ticketmasterSearching.value = false;
  }
}

async function importTicketmasterEvent(item) {
  ticketmasterImportingId.value = item.externalId;
  ticketmasterError.value = "";

  try {
    await TicketmasterRepository.importEvent(item.externalId);
    pageNotice.value = { type: "success", message: t("events.ticketmaster.imported") };
    closeTicketmasterModal();
    await loadPage();
  } catch (error) {
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.importError"));
  } finally {
    ticketmasterImportingId.value = "";
  }
}

async function importTicketmasterResults() {
  ticketmasterBulkImporting.value = true;
  ticketmasterError.value = "";

  try {
    const result = await TicketmasterRepository.importEvents(
      normalizeTicketmasterSearch(ticketmasterFilters.value)
    );
    pageNotice.value = {
      type: "success",
      message: t("events.ticketmaster.bulkImported", {
        count: result.importedCount,
        existing: result.alreadyImportedCount
      })
    };
    closeTicketmasterModal();
    await loadPage();
  } catch (error) {
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.importError"));
  } finally {
    ticketmasterBulkImporting.value = false;
  }
}

async function syncRouteState() {
  syncingRoute = true;

  try {
    await router.replace({
      name: "EventList",
      query: buildEventRouteQuery(filters.value, viewMode.value, quickTab.value)
    });
  } finally {
    syncingRoute = false;
  }
}

function syncNoticeFromRoute() {
  if (route.query.deleted === "true") {
    pageNotice.value = { type: "success", message: t("events.admin.deleted") };
  }
}

function createTicketmasterFiltersFromCurrentView() {
  const routeDate = filters.value.date || "";

  return {
    city: filters.value.city || "",
    keyword: filters.value.text || "",
    musicalGenre: filters.value.musicalGenre || "",
    startDate: routeDate && routeDate >= TICKETMASTER_MIN_START_DATE
      ? routeDate
      : TICKETMASTER_MIN_START_DATE,
    endDate: "",
    countryCode: "ES"
  };
}

function createEmptyUserEventForm() {
  const selectedBand = userEventPublishBands.value[0];

  return {
    ...createEmptyEventForm(),
    bandId: selectedBand?.id ? String(selectedBand.id) : "",
    musicalGenre: selectedBand?.mainGenre || "",
    city: selectedBand?.baseCity || "",
    country: "Spain",
    status: "DRAFT",
    source: "INTERNAL"
  };
}
</script>

<style scoped>
.events-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.events-shell {
  padding-bottom: 3rem;
}

.events-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.events-header__copy {
  max-width: 720px;
}

.events-header__eyebrow,
.events-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.events-header h1 {
  margin: 0.4rem 0 0.6rem;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: -0.04em;
}

.events-header p {
  margin: 0;
  color: #b6b6b6;
}

.events-header__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.events-header__create {
  min-height: 44px;
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  white-space: nowrap;
}

.events-view-toggle {
  display: inline-flex;
  padding: 0.3rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.events-view-toggle__button {
  min-height: 38px;
  padding: 0 1rem;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: #cfcfcf;
  font-weight: 700;
}

.events-view-toggle__button.is-active {
  background: #1db954;
  color: #041106;
}

.section-spacing {
  margin-top: 1.2rem;
}

.events-panel {
  padding: 1.35rem;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.events-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.events-panel__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.4rem;
}

.events-panel__count {
  color: #b9b9b9;
}

.events-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.events-grid--list {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.page-notice,
.state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice.warning,
.state-card--soft {
  color: #d6d6d6;
}

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
  .events-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .events-header,
  .events-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .events-header__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 767.98px) {
  .events-grid,
  .events-grid--list {
    grid-template-columns: 1fr;
  }

  .events-header__actions {
    width: 100%;
  }
}
</style>
