<template>
  <div class="search-page">
    <section class="search-hero">
      <div class="container py-5 py-xl-6">
        <div class="search-hero__copy">
          <h1 class="search-hero__title">{{ t("search.hero.title") }}</h1>
          <p class="search-hero__subtitle">{{ t("search.hero.subtitle") }}</p>
        </div>

        <SearchTopBar
          v-model="searchText"
          :placeholder="t('search.hero.placeholder')"
          :submit-label="t('common.actions.search')"
          :loading-label="t('search.hero.searching')"
          :loading="loadingResults"
          @submit="submitSearch"
        />

        <SearchDetectedChips
          :chips="detectedChips"
        />

        <div v-if="warningMessage" class="search-banner search-banner--warning">
          {{ warningMessage }}
        </div>

        <div v-if="errorMessage" class="search-banner search-banner--error">
          {{ errorMessage }}
        </div>
      </div>
    </section>

    <section class="search-body">
      <div class="container pb-5">
        <div class="search-toolbar">
          <div class="search-view-switch">
            <button
              class="search-view-switch__button"
              :class="{ 'is-active': currentView === 'list' }"
              type="button"
              @click="setView('list')"
            >
              {{ t("search.viewModes.list") }}
            </button>
            <button
              class="search-view-switch__button"
              :class="{ 'is-active': currentView === 'map' }"
              type="button"
              @click="setView('map')"
            >
              {{ t("search.viewModes.map") }}
            </button>
          </div>

          <div class="search-toolbar__summary">
            {{ t("search.results.summary", { count: totalResultsCount }) }}
          </div>
        </div>

        <div class="search-layout">
          <aside class="search-layout__sidebar">
            <SearchFiltersSidebar
              :model-value="filters"
              :counts="sidebarCounts"
              :loading="loadingResults"
              @apply="applyFilters"
              @clear="clearFilters"
            />
          </aside>

          <main class="search-layout__main">
            <div class="search-results-panel">
              <div class="search-results-panel__header">
                <div>
                  <div class="search-results-panel__eyebrow">{{ t("search.results.eyebrow") }}</div>
                  <h2>{{ t("search.results.title") }}</h2>
                </div>
                <SearchResultTabs
                  :tabs="tabs"
                  :active-tab="activeTab"
                  @change="activeTab = $event"
                />
              </div>

              <div v-if="loadingResults && !hasAnyResults" class="search-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("search.states.loading") }}</p>
              </div>

              <template v-else-if="currentView === 'map'">
                <div v-if="mapErrorMessage" class="search-banner search-banner--soft">
                  {{ mapErrorMessage }}
                </div>

                <SearchMapView
                  :items="visibleMapItems"
                  :empty-title="t('search.map.emptyTitle')"
                  :empty-text="t('search.map.emptyText')"
                />
              </template>

              <template v-else>
                <div v-if="displayedResults.length" class="results-stack">
                  <template v-for="result in displayedResults" :key="`${result.kind}-${result.item.id}`">
                    <SearchSpaceCard v-if="result.kind === 'space'" :space="result.item" />
                    <SearchEventCard v-else-if="result.kind === 'event'" :event="result.item" />
                    <SearchBandCard v-else-if="result.kind === 'band'" :band="result.item" />
                    <SearchRecruitmentCard v-else :recruitment="result.item" />
                  </template>
                </div>

                <div v-else class="search-state-card search-state-card--empty">
                  <i class="bi bi-search"></i>
                  <strong>{{ t("search.states.emptyTitle") }}</strong>
                  <p>{{ t("search.states.emptyText") }}</p>
                </div>
              </template>
            </div>
          </main>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import SearchRepository from "@/repositories/SearchRepository";
import SearchBandCard from "../components/SearchBandCard.vue";
import SearchDetectedChips from "../components/SearchDetectedChips.vue";
import SearchEventCard from "../components/SearchEventCard.vue";
import SearchFiltersSidebar from "../components/SearchFiltersSidebar.vue";
import SearchMapView from "../components/SearchMapView.vue";
import SearchRecruitmentCard from "../components/SearchRecruitmentCard.vue";
import SearchResultTabs from "../components/SearchResultTabs.vue";
import SearchSpaceCard from "../components/SearchSpaceCard.vue";
import SearchTopBar from "../components/SearchTopBar.vue";
import { createDefaultSearchFilters, normalizeSearchFilters } from "../searchFilters";
import {
  normalizeText,
  searchTextMatchesBand,
  searchTextMatchesRecruitment,
  shouldIncludeBands,
  shouldIncludeRecruitments
} from "../searchMatching";

const route = useRoute();
const router = useRouter();
const { locale, t } = useI18n();

const searchText = ref("");
const filters = ref(createDefaultSearchFilters());
const activeTab = ref("all");

const loadingResults = ref(false);
const errorMessage = ref("");
const warningMessage = ref("");
const mapErrorMessage = ref("");

const rawSpaces = ref([]);
const rawEvents = ref([]);
const rawBands = ref([]);
const rawRecruitments = ref([]);
const mapItems = ref([]);
const chipData = ref(createEmptyCriteria());
const activeCriteria = ref(createEmptyCriteria());

let searchExecutionId = 0;

const currentView = computed(() => filters.value.view);

const effectiveCriteria = computed(() => {
  const resolvedTimeRange = resolveTimeRange(filters.value);
  const manualDate = filters.value.date || resolveExactDateFromPreset(filters.value.datePreset);
  const manualDateRange = manualDate ? { dateFrom: "", dateTo: "" } : resolveDateRangeFromFilters(filters.value);

  return {
    city: filters.value.city || activeCriteria.value.city || "",
    province: filters.value.province || activeCriteria.value.province || "",
    autonomousCommunity: filters.value.autonomousCommunity || activeCriteria.value.autonomousCommunity || "",
    date: manualDate || activeCriteria.value.date || "",
    dateFrom: manualDate
      ? ""
      : manualDateRange.dateFrom || activeCriteria.value.dateFrom || "",
    dateTo: manualDate
      ? ""
      : manualDateRange.dateTo || activeCriteria.value.dateTo || "",
    datePreset: filters.value.datePreset || "",
    startTime: filters.value.startTime || resolvedTimeRange.startTime || activeCriteria.value.startTime || "",
    endTime: filters.value.endTime || resolvedTimeRange.endTime || activeCriteria.value.endTime || "",
    spaceType: filters.value.spaceType || activeCriteria.value.spaceType || "",
    peopleCount: filters.value.peopleCount || activeCriteria.value.peopleCount || 0,
    musicalGenre: filters.value.musicalGenre || activeCriteria.value.musicalGenre || "",
    maxBudget: filters.value.maxBudget || activeCriteria.value.maxBudget || 0,
    intent: activeCriteria.value.intent || ""
  };
});

const filteredSpaces = computed(() =>
  rawSpaces.value
    .filter((space) => matchesSpace(space, effectiveCriteria.value))
    .sort(sortSpaces)
);

const filteredEvents = computed(() =>
  rawEvents.value
    .filter((event) => matchesEvent(event, effectiveCriteria.value))
    .sort(sortEvents)
);

const filteredBands = computed(() =>
  shouldIncludeBands(searchText.value, effectiveCriteria.value)
    ? rawBands.value
        .filter((band) => matchesBand(band, searchText.value, effectiveCriteria.value))
        .sort(sortBands)
    : []
);

const filteredRecruitments = computed(() =>
  shouldIncludeRecruitments(searchText.value, effectiveCriteria.value)
    ? rawRecruitments.value
        .filter((recruitment) =>
          matchesRecruitment(recruitment, searchText.value, effectiveCriteria.value)
        )
        .sort(sortRecruitments)
    : []
);

const sidebarSpaces = computed(() => filteredSpaces.value);
const sidebarEvents = computed(() => filteredEvents.value);
const sidebarBands = computed(() => filteredBands.value);
const sidebarRecruitments = computed(() => filteredRecruitments.value);

const sidebarCounts = computed(() => ({
  spaces: filteredSpaces.value.length,
  events: filteredEvents.value.length,
  bands: filteredBands.value.length,
  recruitments: filteredRecruitments.value.length
}));

const tabs = computed(() => [
  {
    id: "all",
    label: t("search.tabs.all"),
    count:
      sidebarSpaces.value.length +
      sidebarEvents.value.length +
      sidebarBands.value.length +
      sidebarRecruitments.value.length
  },
  {
    id: "spaces",
    label: t("search.tabs.spaces"),
    count: sidebarSpaces.value.length
  },
  {
    id: "events",
    label: t("search.tabs.events"),
    count: sidebarEvents.value.length
  },
  {
    id: "bands",
    label: t("search.tabs.bands"),
    count: sidebarBands.value.length
  },
  {
    id: "recruitments",
    label: t("search.tabs.recruitments"),
    count: sidebarRecruitments.value.length
  }
]);

const displayedResults = computed(() => {
  if (activeTab.value === "spaces") {
    return sidebarSpaces.value.map((item) => ({ kind: "space", item }));
  }

  if (activeTab.value === "events") {
    return sidebarEvents.value.map((item) => ({ kind: "event", item }));
  }

  if (activeTab.value === "recruitments") {
    return sidebarRecruitments.value.map((item) => ({ kind: "recruitment", item }));
  }

  if (activeTab.value === "bands") {
    return sidebarBands.value.map((item) => ({ kind: "band", item }));
  }

  return mixResults(sidebarSpaces.value, sidebarEvents.value, sidebarBands.value, sidebarRecruitments.value);
});

const visibleMapItems = computed(() => {
  return mapItems.value.filter((item) => {
    if (activeTab.value === "spaces") {
      return item.type === "MUSICAL_SPACE";
    }

    if (activeTab.value === "events") {
      return item.type === "EVENT";
    }

    if (activeTab.value === "recruitments") {
      return false;
    }

    if (activeTab.value === "bands") {
      return false;
    }

    return matchesMapItem(item, effectiveCriteria.value);
  });
});

const totalResultsCount = computed(() => tabs.value.find((tab) => tab.id === "all")?.count ?? 0);
const hasAnyResults = computed(() => totalResultsCount.value > 0);

const detectedChips = computed(() => buildChips(chipData.value, t, locale.value));

watch(
  () => route.query,
  async (query) => {
    const nextFilters = readFiltersFromQuery(query);
    const nextSearchText = getQueryString(query.q);

    searchText.value = nextSearchText;
    filters.value = nextFilters;

    await executeSearch(nextSearchText, nextFilters);
  },
  { deep: true, immediate: true }
);

watch(tabs, (tabList) => {
  const validIds = tabList.filter((tab) => tab.count > 0).map((tab) => tab.id);

  if (!tabList.some((tab) => tab.id === activeTab.value)) {
    activeTab.value = "all";
    return;
  }

  if (activeTab.value !== "all" && !validIds.includes(activeTab.value)) {
    activeTab.value = "all";
  }
});

async function submitSearch() {
  const nextQuery = buildRouteQuery(searchText.value, filters.value);

  if (areQueriesEqual(nextQuery, route.query)) {
    await executeSearch(searchText.value, filters.value);
    return;
  }

  await router.push({ name: "SearchPage", query: nextQuery });
}

async function applyFilters(nextFilters) {
  const mergedFilters = normalizeSearchFilters({ ...nextFilters, view: currentView.value });
  const nextQuery = buildRouteQuery(searchText.value, mergedFilters);

  if (areQueriesEqual(nextQuery, route.query)) {
    filters.value = mergedFilters;
    await executeSearch(searchText.value, mergedFilters);
    return;
  }

  await router.push({ name: "SearchPage", query: nextQuery });
}

async function clearFilters() {
  const resetFilters = normalizeSearchFilters({
    ...createDefaultSearchFilters(),
    view: currentView.value
  });
  const nextQuery = buildRouteQuery(searchText.value, resetFilters);

  if (areQueriesEqual(nextQuery, route.query)) {
    filters.value = resetFilters;
    await executeSearch(searchText.value, resetFilters);
    return;
  }

  await router.push({ name: "SearchPage", query: nextQuery });
}

async function setView(view) {
  const nextFilters = normalizeSearchFilters({ ...filters.value, view });
  const nextQuery = buildRouteQuery(searchText.value, nextFilters);

  if (areQueriesEqual(nextQuery, route.query)) {
    filters.value = nextFilters;
    await executeSearch(searchText.value, nextFilters);
    return;
  }

  await router.push({ name: "SearchPage", query: nextQuery });
}

async function executeSearch(text, activeFiltersState) {
  const executionId = ++searchExecutionId;
  const structuredCriteria = buildStructuredCriteria(activeFiltersState);

  loadingResults.value = true;
  errorMessage.value = "";
  warningMessage.value = "";
  mapErrorMessage.value = "";

  const [primaryResult] = await Promise.allSettled([
    text
      ? SearchRepository.searchNaturalLanguage({ text, ...structuredCriteria })
      : SearchRepository.search(structuredCriteria)
  ]);

  if (executionId !== searchExecutionId) {
    return;
  }

  let spaces = [];
  let events = [];
  let bands = [];
  let recruitments = [];
  let criteriaForMap = structuredCriteria;

  if (primaryResult.status === "fulfilled") {
    spaces = primaryResult.value.spaces ?? [];
    events = primaryResult.value.events ?? [];
    bands = primaryResult.value.bands ?? [];
    recruitments = primaryResult.value.recruitments ?? [];

    if (text) {
      const detectedCriteria = mapSearchDataToCriteria(primaryResult.value.detectedData);
      const mergedCriteria = mergeDetectedAndStructuredCriteria(
        detectedCriteria,
        structuredCriteria,
        activeFiltersState
      );

      activeCriteria.value = normalizeCriteriaShape(mergedCriteria);
      chipData.value = normalizeCriteriaShape({
        ...mergedCriteria,
        intent: primaryResult.value.detectedData?.detectedIntent ?? ""
      });
      criteriaForMap = serializeCriteriaForRequest(activeCriteria.value);
    } else {
      const appliedFilters = mapSearchDataToCriteria(primaryResult.value.appliedFilters);

      activeCriteria.value = normalizeCriteriaShape({
        ...appliedFilters,
        ...structuredCriteria,
        intent: primaryResult.value.appliedFilters?.detectedIntent ?? "BOTH"
      });
      chipData.value = normalizeCriteriaShape({
        ...appliedFilters,
        ...structuredCriteria,
        intent: primaryResult.value.appliedFilters?.detectedIntent ?? "BOTH"
      });
      criteriaForMap = serializeCriteriaForRequest(activeCriteria.value);
    }
  } else {
    rawSpaces.value = [];
    rawEvents.value = [];
    rawBands.value = [];
    rawRecruitments.value = [];
    mapItems.value = [];
    activeCriteria.value = createEmptyCriteria();
    chipData.value = createEmptyCriteria();
    errorMessage.value = getApiErrorMessage(primaryResult.reason, t, "search.states.error");
  }

  rawSpaces.value = spaces;
  rawEvents.value = events;
  rawBands.value = bands;
  rawRecruitments.value = recruitments;

  if (!errorMessage.value && currentView.value === "map") {
    await loadMapResults(criteriaForMap, executionId);
  } else if (primaryResult.status === "fulfilled") {
    mapItems.value = [];
  }

  loadingResults.value = false;
}

async function loadMapResults(criteria, executionId) {
  try {
    const mapResult = await SearchRepository.getMap(criteria);

    if (executionId !== searchExecutionId) {
      return;
    }

    mapItems.value = mapResult.items ?? [];
  } catch (error) {
    if (executionId !== searchExecutionId) {
      return;
    }

    mapItems.value = [];
    mapErrorMessage.value = getApiErrorMessage(error, t, "search.map.placeholder");
  }
}

function readFiltersFromQuery(query) {
  return normalizeSearchFilters({
    city: getQueryString(query.city),
    province: getQueryString(query.province),
    autonomousCommunity: getQueryString(query.autonomousCommunity),
    date: getQueryString(query.date),
    dateFrom: getQueryString(query.dateFrom),
    dateTo: getQueryString(query.dateTo),
    datePreset: getQueryString(query.datePreset),
    spaceType: getQueryString(query.spaceType),
    musicalGenre: getQueryString(query.musicalGenre || query.genre),
    maxBudget: getQueryString(query.maxBudget),
    peopleCount: getQueryString(query.peopleCount),
    timeOfDay: getQueryString(query.timeOfDay),
    startTime: getQueryString(query.startTime),
    endTime: getQueryString(query.endTime),
    view: getQueryString(query.view)
  });
}

function buildRouteQuery(text, activeFiltersState) {
  const nextQuery = {};

  if (text.trim()) {
    nextQuery.q = text.trim();
  }

  if (activeFiltersState.city) nextQuery.city = activeFiltersState.city;
  if (activeFiltersState.province) nextQuery.province = activeFiltersState.province;
  if (activeFiltersState.autonomousCommunity) {
    nextQuery.autonomousCommunity = activeFiltersState.autonomousCommunity;
  }
  if (activeFiltersState.date) nextQuery.date = activeFiltersState.date;
  if (activeFiltersState.dateFrom) nextQuery.dateFrom = activeFiltersState.dateFrom;
  if (activeFiltersState.dateTo) nextQuery.dateTo = activeFiltersState.dateTo;
  if (activeFiltersState.datePreset) nextQuery.datePreset = activeFiltersState.datePreset;
  if (activeFiltersState.spaceType) nextQuery.spaceType = activeFiltersState.spaceType;
  if (activeFiltersState.musicalGenre) nextQuery.musicalGenre = activeFiltersState.musicalGenre;
  if (activeFiltersState.maxBudget > 0) nextQuery.maxBudget = String(activeFiltersState.maxBudget);
  if (activeFiltersState.peopleCount > 0) nextQuery.peopleCount = String(activeFiltersState.peopleCount);
  if (activeFiltersState.timeOfDay) nextQuery.timeOfDay = activeFiltersState.timeOfDay;
  if (activeFiltersState.startTime) nextQuery.startTime = activeFiltersState.startTime;
  if (activeFiltersState.endTime) nextQuery.endTime = activeFiltersState.endTime;
  if (activeFiltersState.view === "map") {
    nextQuery.view = "map";
  }

  return nextQuery;
}

function buildStructuredCriteria(activeFiltersState) {
  const criteria = {};
  const resolvedTimeRange = resolveTimeRange(activeFiltersState);
  const resolvedDate = activeFiltersState.date || resolveExactDateFromPreset(activeFiltersState.datePreset);
  const resolvedDateRange = resolvedDate
    ? { dateFrom: "", dateTo: "" }
    : resolveDateRangeFromFilters(activeFiltersState);

  if (activeFiltersState.city) criteria.city = activeFiltersState.city;
  if (activeFiltersState.province) criteria.province = activeFiltersState.province;
  if (activeFiltersState.autonomousCommunity) {
    criteria.autonomousCommunity = activeFiltersState.autonomousCommunity;
  }
  if (resolvedDate) criteria.date = resolvedDate;
  if (!resolvedDate && resolvedDateRange.dateFrom) criteria.dateFrom = resolvedDateRange.dateFrom;
  if (!resolvedDate && resolvedDateRange.dateTo) criteria.dateTo = resolvedDateRange.dateTo;
  if (activeFiltersState.startTime || resolvedTimeRange.startTime) {
    criteria.startTime = activeFiltersState.startTime || resolvedTimeRange.startTime;
  }
  if (activeFiltersState.endTime || resolvedTimeRange.endTime) {
    criteria.endTime = activeFiltersState.endTime || resolvedTimeRange.endTime;
  }
  if (activeFiltersState.spaceType) criteria.spaceType = activeFiltersState.spaceType;
  if (activeFiltersState.peopleCount > 0) criteria.peopleCount = activeFiltersState.peopleCount;
  if (activeFiltersState.musicalGenre) criteria.musicalGenre = activeFiltersState.musicalGenre;
  if (activeFiltersState.maxBudget > 0) criteria.maxBudget = activeFiltersState.maxBudget;

  return criteria;
}

function resolveTimeRange(activeFiltersState) {
  if (activeFiltersState.startTime || activeFiltersState.endTime) {
    return {
      startTime: activeFiltersState.startTime || "",
      endTime: activeFiltersState.endTime || ""
    };
  }

  if (activeFiltersState.timeOfDay === "morning") {
    return { startTime: "08:00", endTime: "13:00" };
  }

  if (activeFiltersState.timeOfDay === "afternoon") {
    return { startTime: "13:00", endTime: "19:00" };
  }

  if (activeFiltersState.timeOfDay === "night") {
    return { startTime: "19:00", endTime: "23:30" };
  }

  return { startTime: "", endTime: "" };
}

function resolveExactDateFromPreset(datePreset) {
  if (!datePreset || datePreset === "weekend" || datePreset === "week") {
    return "";
  }

  const today = new Date();

  if (datePreset === "today") {
    return toIsoDate(today);
  }

  if (datePreset === "tomorrow") {
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    return toIsoDate(tomorrow);
  }

  return "";
}

function resolveDateRangeFromFilters(activeFiltersState) {
  if (activeFiltersState.dateFrom || activeFiltersState.dateTo) {
    return {
      dateFrom: activeFiltersState.dateFrom || activeFiltersState.dateTo || "",
      dateTo: activeFiltersState.dateTo || activeFiltersState.dateFrom || ""
    };
  }

  const today = new Date();

  if (activeFiltersState.datePreset === "week") {
    const endOfWeek = new Date(today);
    endOfWeek.setDate(today.getDate() + (7 - today.getDay() || 7));
    return { dateFrom: toIsoDate(today), dateTo: toIsoDate(endOfWeek) };
  }

  if (activeFiltersState.datePreset === "weekend") {
    const nextSaturday = new Date(today);
    const daysUntilSaturday = (6 - today.getDay() + 7) % 7;
    nextSaturday.setDate(today.getDate() + daysUntilSaturday);

    const nextSunday = new Date(nextSaturday);
    nextSunday.setDate(nextSaturday.getDate() + 1);

    return { dateFrom: toIsoDate(nextSaturday), dateTo: toIsoDate(nextSunday) };
  }

  return { dateFrom: "", dateTo: "" };
}

function matchesSpace(space, criteria) {
  if (criteria.city && !normalizedIncludes(space.city, criteria.city)) {
    return false;
  }

  if (criteria.province && !normalizedIncludes(space.province, criteria.province)) {
    return false;
  }

  if (criteria.spaceType && space.spaceType !== criteria.spaceType) {
    return false;
  }

  if (criteria.peopleCount > 0 && Number(space.capacity ?? 0) < criteria.peopleCount) {
    return false;
  }

  if (criteria.maxBudget > 0) {
    if (space.estimatedPrice == null) {
      return false;
    }

    if (Number(space.estimatedPrice) > criteria.maxBudget) {
      return false;
    }
  }

  return true;
}

function matchesEvent(event, criteria) {
  if (criteria.city && !normalizedIncludes(event.city, criteria.city)) {
    return false;
  }

  if (criteria.province && !normalizedIncludes(event.province, criteria.province)) {
    return false;
  }

  if (criteria.musicalGenre && !normalizedIncludes(event.musicalGenre, criteria.musicalGenre)) {
    return false;
  }

  if (criteria.peopleCount > 0 && Number(event.capacity ?? 0) < criteria.peopleCount) {
    return false;
  }

  if (criteria.maxBudget > 0) {
    if (event.ticketPrice == null) {
      return false;
    }

    if (Number(event.ticketPrice) > criteria.maxBudget) {
      return false;
    }
  }

  if (criteria.date && event.eventDate !== criteria.date) {
    return false;
  }

  if (!criteria.date && !matchesDateRange(event.eventDate, criteria.dateFrom, criteria.dateTo)) {
    return false;
  }

  if (criteria.datePreset && !matchesDatePreset(event.eventDate, criteria.datePreset)) {
    return false;
  }

  if ((criteria.startTime || criteria.endTime) && !matchesTimeRange(event, criteria.startTime, criteria.endTime)) {
    return false;
  }

  return true;
}

function matchesRecruitment(recruitment, text, criteria) {
  const city = recruitment.city || recruitment.band?.baseCity || "";
  const genre = recruitment.band?.mainGenre || "";

  if (criteria.city && !normalizedIncludes(city, criteria.city)) {
    return false;
  }

  if (criteria.musicalGenre && !normalizedIncludes(genre, criteria.musicalGenre)) {
    return false;
  }

  return searchTextMatchesRecruitment(recruitment, text);
}

function matchesBand(band, text, criteria) {
  if (criteria.city && !normalizedIncludes(band.baseCity, criteria.city)) {
    return false;
  }

  if (criteria.musicalGenre && !normalizedIncludes(band.mainGenre, criteria.musicalGenre)) {
    return false;
  }

  return searchTextMatchesBand(band, text);
}

function matchesMapItem(item, criteria) {
  if (criteria.city && !normalizedIncludes(item.city, criteria.city)) {
    return false;
  }

  if (criteria.province && !normalizedIncludes(item.province, criteria.province)) {
    return false;
  }

  if (criteria.maxBudget > 0 && item.price != null && Number(item.price) > criteria.maxBudget) {
    return false;
  }

  if (item.type === "EVENT") {
    if (criteria.date && item.date !== criteria.date) {
      return false;
    }

    if (!criteria.date && !matchesDateRange(item.date, criteria.dateFrom, criteria.dateTo)) {
      return false;
    }

    if (criteria.datePreset && !matchesDatePreset(item.date, criteria.datePreset)) {
      return false;
    }

    if ((criteria.startTime || criteria.endTime) && !matchesMapTime(item, criteria.startTime, criteria.endTime)) {
      return false;
    }
  }

  return true;
}

function matchesMapTime(item, startTime, endTime) {
  const itemStart = item.startTime?.slice?.(0, 5) ?? item.startTime ?? "";

  if (!itemStart) {
    return true;
  }

  if (startTime && endTime) {
    return itemStart >= startTime && itemStart <= endTime;
  }

  if (startTime) {
    return itemStart >= startTime;
  }

  return itemStart <= endTime;
}

function matchesTimeRange(event, startTime, endTime) {
  const eventStart = event.startTime?.slice?.(0, 5) ?? "";
  const eventEnd = event.endTime?.slice?.(0, 5) ?? eventStart;

  if (!eventStart) {
    return false;
  }

  if (startTime && endTime) {
    return eventStart <= endTime && eventEnd >= startTime;
  }

  if (startTime) {
    return eventStart >= startTime;
  }

  return eventStart <= endTime;
}

function matchesDateRange(dateValue, dateFrom, dateTo) {
  if (!dateValue || (!dateFrom && !dateTo)) {
    return true;
  }

  if (dateFrom && dateValue < dateFrom) {
    return false;
  }

  if (dateTo && dateValue > dateTo) {
    return false;
  }

  return true;
}

function matchesDatePreset(dateValue, preset) {
  if (!dateValue || !preset) {
    return true;
  }

  const eventDate = new Date(`${dateValue}T00:00:00`);
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (preset === "today") {
    return toIsoDate(eventDate) === toIsoDate(today);
  }

  if (preset === "tomorrow") {
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    return toIsoDate(eventDate) === toIsoDate(tomorrow);
  }

  if (preset === "week") {
    const endOfWeek = new Date(today);
    endOfWeek.setDate(today.getDate() + (7 - today.getDay() || 7));
    return eventDate >= today && eventDate <= endOfWeek;
  }

  if (preset === "weekend") {
    return eventDate >= today && (eventDate.getDay() === 0 || eventDate.getDay() === 6);
  }

  return true;
}

function sortSpaces(left, right) {
  const leftPrice = left.estimatedPrice == null ? Number.MAX_SAFE_INTEGER : Number(left.estimatedPrice);
  const rightPrice = right.estimatedPrice == null ? Number.MAX_SAFE_INTEGER : Number(right.estimatedPrice);

  if (leftPrice !== rightPrice) {
    return leftPrice - rightPrice;
  }

  return `${left.city}${left.name}`.localeCompare(`${right.city}${right.name}`);
}

function sortEvents(left, right) {
  return `${left.eventDate}${left.startTime}`.localeCompare(`${right.eventDate}${right.startTime}`);
}

function sortRecruitments(left, right) {
  return `${right.publicationDate || ""}`.localeCompare(`${left.publicationDate || ""}`);
}

function sortBands(left, right) {
  return `${left.baseCity || ""}${left.name || ""}`.localeCompare(
    `${right.baseCity || ""}${right.name || ""}`
  );
}

function mixResults(spaces, events, bands, recruitments) {
  const queues = [
    spaces.map((item) => ({ kind: "space", item })),
    events.map((item) => ({ kind: "event", item })),
    bands.map((item) => ({ kind: "band", item })),
    recruitments.map((item) => ({ kind: "recruitment", item }))
  ].map((queue) => [...queue]);

  const mixed = [];
  let pointer = 0;

  while (queues.some((queue) => queue.length)) {
    const queue = queues[pointer % queues.length];

    if (queue.length) {
      mixed.push(queue.shift());
    }

    pointer += 1;
  }

  return mixed;
}

function buildChips(criteria, translate, activeLocale) {
  const chips = [];

  if (criteria.city) {
    chips.push({ key: "city", label: criteria.city });
  }

  if (criteria.province) {
    chips.push({ key: "province", label: criteria.province });
  }

  if (criteria.autonomousCommunity) {
    chips.push({ key: "autonomousCommunity", label: criteria.autonomousCommunity });
  }

  if (criteria.spaceType) {
    chips.push({
      key: "spaceType",
      label: translate(`search.spaceTypeLabels.${criteria.spaceType}`)
    });
  }

  if (criteria.peopleCount > 0) {
    chips.push({
      key: "peopleCount",
      label: translate("search.chips.people", { value: criteria.peopleCount })
    });
  }

  if (criteria.date) {
    chips.push({
      key: "date",
      label: formatChipDate(criteria.date, activeLocale, translate)
    });
  }

  if (!criteria.date && (criteria.dateFrom || criteria.dateTo)) {
    chips.push({
      key: "dateRange",
      label: translate("search.chips.dateRange", {
        from: formatChipDate(criteria.dateFrom || criteria.dateTo, activeLocale, translate),
        to: formatChipDate(criteria.dateTo || criteria.dateFrom, activeLocale, translate)
      })
    });
  }

  if (criteria.startTime || criteria.endTime) {
    chips.push({
      key: "time",
      label: [criteria.startTime, criteria.endTime]
        .map(formatChipTime)
        .filter(Boolean)
        .join(" - ")
    });
  }

  if (criteria.musicalGenre) {
    chips.push({ key: "genre", label: criteria.musicalGenre });
  }

  if (criteria.maxBudget > 0) {
    chips.push({
      key: "budget",
      label: translate("search.chips.budget", { value: criteria.maxBudget })
    });
  }

  return chips;
}

function formatChipTime(value) {
  const text = String(value || "").trim();
  const match = text.match(/^(\d{1,2}):(\d{2})(?::\d{2})?/);

  if (!match) {
    return text;
  }

  return `${match[1].padStart(2, "0")}:${match[2]}`;
}

function formatChipDate(value, activeLocale, translate) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const tomorrow = new Date(today);
  tomorrow.setDate(today.getDate() + 1);

  const date = new Date(`${value}T00:00:00`);

  if (toIsoDate(date) === toIsoDate(today)) {
    return translate("search.chips.today");
  }

  if (toIsoDate(date) === toIsoDate(tomorrow)) {
    return translate("search.chips.tomorrow");
  }

  return new Intl.DateTimeFormat(activeLocale, {
    weekday: "short",
    day: "2-digit",
    month: "short"
  }).format(date);
}

function mapSearchDataToCriteria(searchData = {}) {
  const data = searchData ?? {};

  return normalizeCriteriaShape({
    city: data.detectedCity ?? data.city,
    province: data.detectedProvince ?? data.province,
    autonomousCommunity: data.detectedAutonomousCommunity ?? data.autonomousCommunity,
    date: data.detectedDate ?? data.date,
    dateFrom: data.detectedDateFrom ?? data.dateFrom,
    dateTo: data.detectedDateTo ?? data.dateTo,
    startTime: data.detectedStartTime ?? data.startTime,
    endTime: data.detectedEndTime ?? data.endTime,
    spaceType: data.detectedSpaceType ?? data.spaceType,
    peopleCount: data.detectedPeopleCount ?? data.peopleCount,
    musicalGenre: data.detectedMusicalGenre ?? data.musicalGenre,
    maxBudget: data.detectedMaxBudget ?? data.maxBudget,
    intent: data.detectedIntent ?? ""
  });
}

function mergeDetectedAndStructuredCriteria(
  detectedCriteria,
  structuredCriteria,
  activeFiltersState
) {
  const detected = normalizeCriteriaShape(detectedCriteria);
  const structured = normalizeCriteriaShape(structuredCriteria);
  const hasExplicitLocation = Boolean(
    activeFiltersState.city ||
      activeFiltersState.province ||
      activeFiltersState.autonomousCommunity ||
      structured.city ||
      structured.province ||
      structured.autonomousCommunity
  );
  const hasExplicitDate = Boolean(
    activeFiltersState.date ||
      activeFiltersState.datePreset ||
      activeFiltersState.dateFrom ||
      activeFiltersState.dateTo ||
      structured.date ||
      structured.dateFrom ||
      structured.dateTo
  );

  return normalizeCriteriaShape({
    ...detected,
    city: hasExplicitLocation ? structured.city : detected.city,
    province: hasExplicitLocation ? structured.province : detected.province,
    autonomousCommunity: hasExplicitLocation
      ? structured.autonomousCommunity
      : detected.autonomousCommunity,
    date: hasExplicitDate ? structured.date : detected.date,
    dateFrom: hasExplicitDate ? structured.dateFrom : detected.dateFrom,
    dateTo: hasExplicitDate ? structured.dateTo : detected.dateTo,
    datePreset: activeFiltersState.datePreset || "",
    startTime: structured.startTime || detected.startTime,
    endTime: structured.endTime || detected.endTime,
    spaceType: structured.spaceType || detected.spaceType,
    peopleCount: structured.peopleCount > 0 ? structured.peopleCount : detected.peopleCount,
    musicalGenre: structured.musicalGenre || detected.musicalGenre,
    maxBudget: structured.maxBudget > 0 ? structured.maxBudget : detected.maxBudget,
    intent: structured.intent || detected.intent
  });
}

function normalizeCriteriaShape(criteria = {}) {
  return {
    city: criteria.city || "",
    province: criteria.province || "",
    autonomousCommunity: criteria.autonomousCommunity || "",
    date: criteria.date || "",
    dateFrom: criteria.dateFrom || "",
    dateTo: criteria.dateTo || "",
    datePreset: criteria.datePreset || "",
    startTime: criteria.startTime || "",
    endTime: criteria.endTime || "",
    spaceType: criteria.spaceType || "",
    peopleCount: Number(criteria.peopleCount) || 0,
    musicalGenre: criteria.musicalGenre || "",
    maxBudget: Number(criteria.maxBudget) || 0,
    intent: criteria.intent || ""
  };
}

function serializeCriteriaForRequest(criteria) {
  const payload = {};

  if (criteria.city) payload.city = criteria.city;
  if (criteria.province) payload.province = criteria.province;
  if (criteria.autonomousCommunity) payload.autonomousCommunity = criteria.autonomousCommunity;
  if (criteria.date) payload.date = criteria.date;
  if (!criteria.date && criteria.dateFrom) payload.dateFrom = criteria.dateFrom;
  if (!criteria.date && criteria.dateTo) payload.dateTo = criteria.dateTo;
  if (criteria.startTime) payload.startTime = criteria.startTime;
  if (criteria.endTime) payload.endTime = criteria.endTime;
  if (criteria.spaceType) payload.spaceType = criteria.spaceType;
  if (criteria.peopleCount > 0) payload.peopleCount = criteria.peopleCount;
  if (criteria.musicalGenre) payload.musicalGenre = criteria.musicalGenre;
  if (criteria.maxBudget > 0) payload.maxBudget = criteria.maxBudget;

  return payload;
}

function createEmptyCriteria() {
  return {
    city: "",
    province: "",
    autonomousCommunity: "",
    date: "",
    dateFrom: "",
    dateTo: "",
    datePreset: "",
    startTime: "",
    endTime: "",
    spaceType: "",
    peopleCount: 0,
    musicalGenre: "",
    maxBudget: 0,
    intent: ""
  };
}

function getQueryString(value) {
  if (Array.isArray(value)) {
    return value[0] ?? "";
  }

  return typeof value === "string" ? value : "";
}

function areQueriesEqual(left, right) {
  return JSON.stringify(normalizeRouteQuery(left)) === JSON.stringify(normalizeRouteQuery(right));
}

function normalizeRouteQuery(query = {}) {
  return Object.keys(query)
    .sort()
    .reduce((accumulator, key) => {
      const value = query[key];
      accumulator[key] = Array.isArray(value) ? value.join(",") : String(value);
      return accumulator;
    }, {});
}

function normalizedIncludes(source, candidate) {
  return normalizeText(source).includes(normalizeText(candidate));
}

function toIsoDate(date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}
</script>

<style scoped>
.search-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.py-xl-6 {
  padding-top: 5.5rem;
  padding-bottom: 5.5rem;
}

.search-hero__copy {
  max-width: 760px;
  margin: 0 auto 1.85rem;
  text-align: center;
}

.search-hero__title {
  margin: 0 0 1rem;
  font-size: clamp(2.4rem, 5vw, 4.6rem);
  line-height: 0.98;
  letter-spacing: -0.04em;
}

.search-hero__subtitle {
  max-width: 660px;
  margin: 0 auto;
  color: #b3b3b3;
  font-size: 1.06rem;
  line-height: 1.75;
}

.search-banner {
  width: min(100%, 980px);
  margin: 1rem auto 0;
  padding: 0.95rem 1rem;
  border-radius: 18px;
  border: 1px solid transparent;
}

.search-banner--warning {
  background: rgba(255, 193, 7, 0.08);
  border-color: rgba(255, 193, 7, 0.18);
  color: #f4d06f;
}

.search-banner--error {
  background: rgba(220, 53, 69, 0.08);
  border-color: rgba(220, 53, 69, 0.16);
  color: #ffb3bd;
}

.search-banner--soft {
  margin-bottom: 1rem;
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.08);
  color: #d2d2d2;
}

.search-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  width: min(100%, 1180px);
  margin-right: auto;
  margin-bottom: 1.25rem;
  margin-left: auto;
}

.search-view-switch {
  display: inline-flex;
  padding: 0.3rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.search-view-switch__button {
  min-height: 40px;
  padding: 0 1rem;
  border: 0;
  border-radius: 14px;
  background: transparent;
  color: #bdbdbd;
  font-weight: 700;
}

.search-view-switch__button.is-active {
  background: rgba(29, 185, 84, 0.15);
  color: #ffffff;
}

.search-toolbar__summary {
  color: #bdbdbd;
}

.search-layout {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  gap: 1.4rem;
  align-items: start;
  width: min(100%, 1180px);
  margin-right: auto;
  margin-left: auto;
}

.search-layout__sidebar,
.search-layout__main {
  min-width: 0;
}

.search-results-panel {
  padding: 1.35rem;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.search-results-panel__header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.3rem;
}

.search-results-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.search-results-panel__header h2 {
  margin: 0.3rem 0 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.results-stack {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.search-state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.9rem;
  min-height: 320px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: #d0d0d0;
  text-align: center;
}

.search-state-card--empty i {
  color: #1db954;
  font-size: 2rem;
}

@media (max-width: 991.98px) {
  .search-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767.98px) {
  .search-toolbar,
  .search-results-panel__header {
    flex-direction: column;
    align-items: stretch;
  }

  .search-hero__title {
    font-size: 2.3rem;
  }
}
</style>
