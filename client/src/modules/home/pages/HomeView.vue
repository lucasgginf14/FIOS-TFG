<template>
  <div class="home-page">
    <section class="home-hero">
      <div class="container py-5 py-xl-6">
        <div class="hero-copy">
          <h1 class="hero-copy__title">{{ t("home.hero.title") }}</h1>
          <p class="hero-copy__subtitle">{{ t("home.hero.subtitle") }}</p>

          <form class="hero-search" @submit.prevent="submitPrimarySearch">
            <div class="hero-search__field">
              <span class="hero-search__icon" aria-hidden="true">
                <i class="bi bi-search"></i>
              </span>
              <input
                v-model.trim="searchText"
                type="text"
                class="form-control hero-search__input"
                :placeholder="t('home.hero.inputPlaceholder')"
              />
              <button class="btn hero-search__submit" type="submit">
                {{ t("common.actions.search") }}
              </button>
            </div>
          </form>
        </div>

        <form class="quick-filters" @submit.prevent="submitQuickFilters">
          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-city">{{ t("common.labels.city") }}</label>
            <input
              id="home-filter-city"
              v-model.trim="quickFilters.city"
              class="form-control home-input"
              :placeholder="t('home.quickFilters.cityPlaceholder')"
            />
          </div>

          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-date">{{ t("common.labels.date") }}</label>
            <input
              id="home-filter-date"
              v-model="quickFilters.date"
              type="date"
              class="form-control home-input"
            />
          </div>

          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-space-type">{{
              t("common.labels.spaceType")
            }}</label>
            <input
              id="home-filter-space-type"
              v-model.trim="quickFilters.spaceType"
              class="form-control home-input"
              :placeholder="t('home.quickFilters.spaceTypePlaceholder')"
            />
          </div>

          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-genre">{{ t("common.labels.genre") }}</label>
            <input
              id="home-filter-genre"
              v-model.trim="quickFilters.musicalGenre"
              class="form-control home-input"
              :placeholder="t('home.quickFilters.genrePlaceholder')"
            />
          </div>

          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-budget">{{
              t("common.labels.budget")
            }}</label>
            <input
              id="home-filter-budget"
              v-model.trim="quickFilters.maxBudget"
              type="number"
              min="0"
              class="form-control home-input"
              :placeholder="t('home.quickFilters.budgetPlaceholder')"
            />
          </div>

          <div class="quick-filter-pill">
            <label class="home-label" for="home-filter-people">{{
              t("common.labels.people")
            }}</label>
            <input
              id="home-filter-people"
              v-model.trim="quickFilters.peopleCount"
              type="number"
              min="1"
              class="form-control home-input"
              :placeholder="t('home.quickFilters.peoplePlaceholder')"
            />
          </div>

          <button class="btn hero-search__submit quick-filters__submit" type="submit">
            {{ t("common.actions.search") }}
          </button>
        </form>

        <div v-if="publicDataWarning" class="home-warning">
          {{ t("home.errors.publicDataWarning") }}
        </div>
      </div>
    </section>

    <section class="home-section home-section--discovery">
      <div class="container">
        <div class="content-columns">
          <section class="content-column">
            <header class="section-heading">
              <div>
                <h2>{{ t("home.featuredSpaces.title") }}</h2>
                <p>{{ t("home.featuredSpaces.subtitle") }}</p>
              </div>
              <RouterLink class="section-link" :to="{ name: 'MusicalSpaceList' }">
                {{ t("home.viewAll") }}
              </RouterLink>
            </header>

            <div v-if="publicDataLoading" class="empty-state empty-state--loading">
              <strong>{{ t("home.states.loadingPublic") }}</strong>
              <p>{{ t("home.states.loadingPublicText") }}</p>
            </div>

            <div v-else-if="publicDataErrors.spaces" class="empty-state empty-state--error">
              <strong>{{ t("home.errors.spacesTitle") }}</strong>
              <p>{{ t("home.errors.spacesText") }}</p>
            </div>

            <div v-else-if="displayedSpaces.length" class="content-stack">
              <RouterLink
                v-for="space in displayedSpaces"
                :key="space.id"
                class="feature-card clickable-card"
                :to="getSpaceRoute(space)"
                :aria-label="`${t('home.actions.viewSpace')}: ${space.name}`"
              >
                <div class="feature-card__media">
                  <AppImage
                    :src="space.mainImage"
                    :alt="space.name"
                    :fallback-src="spacePlaceholder"
                    :fallback-label="space.name"
                    icon-class="bi bi-speaker"
                  />
                </div>
                <div class="feature-card__topline">
                  <span class="feature-pill">{{ formatSpaceType(space.spaceType) }}</span>
                  <span class="feature-rating">{{ formatRating(space.rating) }}</span>
                </div>
                <h3>{{ space.name }}</h3>
                <p class="feature-card__location">
                  {{ space.city }}<span v-if="space.province">, {{ space.province }}</span>
                </p>
                <p class="feature-card__reason">
                  {{ t(`home.featuredSpaces.reasons.${space.featuredReason || "balanced"}`) }}
                </p>
                <div class="feature-card__meta-grid">
                  <div>
                    <span>{{ t("home.featuredSpaces.capacity") }}</span>
                    <strong>{{ space.capacity ?? "--" }}</strong>
                  </div>
                  <div>
                    <span>{{ t("home.featuredSpaces.estimatedPrice") }}</span>
                    <strong>{{ space.priceLabel ?? t("common.labels.onRequest") }}</strong>
                  </div>
                  <div>
                    <span>{{ t("home.featuredSpaces.rating") }}</span>
                    <strong>{{ formatReviews(space.reviewsCount) }}</strong>
                  </div>
                  <div>
                    <span>{{ t("home.featuredSpaces.soundproofed") }}</span>
                    <strong>{{ formatSoundproofed(space.soundproofed) }}</strong>
                  </div>
                </div>
                <div class="feature-card__actions">
                  <span class="clickable-card__open-indicator" aria-hidden="true">
                    <i class="bi bi-arrow-up-right"></i>
                  </span>
                </div>
              </RouterLink>
            </div>

            <div v-else class="empty-state">
              <strong>{{ t("home.placeholders.spacesTitle") }}</strong>
              <p>{{ t("home.placeholders.spacesText") }}</p>
            </div>
          </section>

          <section class="content-column">
            <header class="section-heading">
              <div>
                <h2>{{ t("home.featuredEvents.title") }}</h2>
                <p>{{ t("home.featuredEvents.subtitle") }}</p>
              </div>
              <RouterLink class="section-link" :to="{ name: 'EventList' }">
                {{ t("home.viewAll") }}
              </RouterLink>
            </header>

            <div v-if="publicDataLoading" class="empty-state empty-state--loading">
              <strong>{{ t("home.states.loadingPublic") }}</strong>
              <p>{{ t("home.states.loadingPublicText") }}</p>
            </div>

            <div v-else-if="publicDataErrors.events" class="empty-state empty-state--error">
              <strong>{{ t("home.errors.eventsTitle") }}</strong>
              <p>{{ t("home.errors.eventsText") }}</p>
            </div>

            <div v-else-if="displayedEvents.length" class="content-stack">
              <RouterLink
                v-for="event in displayedEvents"
                :key="event.id"
                class="feature-card clickable-card"
                :to="getEventRoute(event)"
                :aria-label="`${t('home.actions.viewEvent')}: ${event.title}`"
              >
                <div class="feature-card__media">
                  <AppImage
                    :src="getEventCardImage(event)"
                    :alt="event.title"
                    :fallback-src="eventPlaceholder"
                    :fallback-label="event.title"
                    icon-class="bi bi-calendar-event"
                  />
                </div>
                <div class="feature-card__topline">
                  <span class="feature-pill">{{ formatEventType(event.eventType) }}</span>
                  <span class="feature-card__date">{{ formatDate(event.eventDate) }}</span>
                </div>
                <h3>{{ event.title }}</h3>
                <p class="feature-card__location">
                  {{ event.city }}<span v-if="event.country">, {{ event.country }}</span>
                </p>
                <p class="feature-card__reason">
                  {{ t(`home.featuredEvents.reasons.${event.featuredReason || "upcoming"}`) }}
                </p>
                <div class="feature-card__meta-grid">
                  <div>
                    <span>{{ t("common.labels.date") }}</span>
                    <strong>{{ formatDate(event.eventDate) }}</strong>
                  </div>
                  <div>
                    <span>{{ t("common.labels.genre") }}</span>
                    <strong>{{ event.musicalGenre || "--" }}</strong>
                  </div>
                  <div>
                    <span>{{ t("common.labels.time") }}</span>
                    <strong>{{ formatTimeRange(event.startTime, event.endTime) }}</strong>
                  </div>
                  <div>
                    <span>{{ t("common.labels.capacity") }}</span>
                    <strong>{{ event.capacity ?? "--" }}</strong>
                  </div>
                </div>
                <div class="feature-card__actions">
                  <span class="clickable-card__open-indicator" aria-hidden="true">
                    <i class="bi bi-arrow-up-right"></i>
                  </span>
                </div>
              </RouterLink>
            </div>

            <div v-else class="empty-state">
              <strong>{{ t("home.placeholders.eventsTitle") }}</strong>
              <p>{{ t("home.placeholders.eventsText") }}</p>
            </div>
          </section>

          <section class="content-column">
            <header class="section-heading">
              <div>
                <h2>{{ t("home.recruitments.title") }}</h2>
                <p>{{ t("home.recruitments.subtitle") }}</p>
              </div>
              <RouterLink class="section-link" :to="{ name: 'BandRecruitmentList' }">
                {{ t("home.viewAll") }}
              </RouterLink>
            </header>

            <div v-if="publicDataLoading" class="empty-state empty-state--loading">
              <strong>{{ t("home.states.loadingPublic") }}</strong>
              <p>{{ t("home.states.loadingPublicText") }}</p>
            </div>

            <div v-else-if="publicDataErrors.recruitments" class="empty-state empty-state--error">
              <strong>{{ t("home.errors.recruitmentsTitle") }}</strong>
              <p>{{ t("home.errors.recruitmentsText") }}</p>
            </div>

            <div v-else-if="displayedRecruitments.length" class="content-stack">
              <RouterLink
                v-for="recruitment in displayedRecruitments"
                :key="recruitment.id"
                class="feature-card clickable-card"
                :to="getRecruitmentRoute(recruitment)"
                :aria-label="`${t('home.actions.viewOffer')}: ${recruitment.title}`"
              >
                <div class="feature-card__media">
                  <AppImage
                    :src="recruitment.band?.image"
                    :alt="recruitment.band?.name || recruitment.title"
                    :fallback-src="bandPlaceholder"
                    :fallback-label="recruitment.band?.name || recruitment.title"
                    icon-class="bi bi-vinyl"
                  />
                </div>
                <div class="feature-card__topline">
                  <span class="feature-pill">{{
                    recruitment.instrument?.name || recruitment.roleWanted || t("search.cards.recruitment.roleFallback")
                  }}</span>
                  <span class="feature-card__date">{{ recruitment.city }}</span>
                </div>
                <h3>{{ recruitment.title }}</h3>
                <p class="feature-card__location">
                  {{ recruitment.band?.name || t("home.personal.fallbackBand") }}
                  <span v-if="recruitment.band?.mainGenre"
                    >&middot; {{ recruitment.band.mainGenre }}</span
                  >
                </p>
                <p class="feature-card__reason feature-card__reason--placeholder" aria-hidden="true">
                  &nbsp;
                </p>
                <div class="feature-card__meta-grid">
                  <div>
                    <span>{{ t("home.recruitments.instrument") }}</span>
                    <strong>{{
                      recruitment.instrument?.name || recruitment.roleWanted || "--"
                    }}</strong>
                  </div>
                  <div>
                    <span>{{ t("home.recruitments.level") }}</span>
                    <strong>{{ formatRecruitmentLevel(recruitment.levelRequired) }}</strong>
                  </div>
                  <div>
                    <span>{{ t("home.recruitments.vacancies") }}</span>
                    <strong>{{ recruitment.vacancies ?? "--" }}</strong>
                  </div>
                  <div>
                    <span>{{ t("common.labels.genre") }}</span>
                    <strong>{{ recruitment.band?.mainGenre || "--" }}</strong>
                  </div>
                </div>
                <div class="feature-card__actions">
                  <span class="clickable-card__open-indicator" aria-hidden="true">
                    <i class="bi bi-arrow-up-right"></i>
                  </span>
                </div>
              </RouterLink>
            </div>

            <div v-else class="empty-state">
              <strong>{{ t("home.placeholders.recruitmentsTitle") }}</strong>
              <p>{{ t("home.placeholders.recruitmentsText") }}</p>
            </div>
          </section>
        </div>
      </div>
    </section>

    <section class="home-section home-section--personal">
      <div class="container">
        <div class="section-heading">
          <div>
            <h2>{{ t("home.personal.title") }}</h2>
            <p>{{ t("home.personal.subtitle") }}</p>
          </div>
        </div>

        <div v-if="canUseUserFeatures" class="row g-4">
          <div class="col-12 col-xl-4">
            <div class="panel-card h-100">
              <h3>{{ t("home.personal.searches") }}</h3>
              <p v-if="personalDataLoading" class="panel-empty">
                {{ t("home.states.loadingPersonal") }}
              </p>
              <p v-else-if="personalDataErrors.searches" class="panel-empty panel-empty--error">
                {{ t("home.errors.searchesText") }}
              </p>
              <div v-else-if="latestSearches.length" class="panel-list">
                <article v-for="item in latestSearches" :key="item.id" class="panel-item">
                  <strong>{{ item.originalText }}</strong>
                  <span>{{ formatDateTime(item.searchDate) }}</span>
                </article>
              </div>
              <p v-else class="panel-empty">{{ t("home.placeholders.searchesText") }}</p>
            </div>
          </div>

          <div class="col-12 col-xl-4">
            <div class="panel-card h-100">
              <h3>{{ t("home.personal.reservations") }}</h3>
              <p v-if="personalDataLoading" class="panel-empty">
                {{ t("home.states.loadingPersonal") }}
              </p>
              <p v-else-if="personalDataErrors.reservations" class="panel-empty panel-empty--error">
                {{ t("home.errors.reservationsText") }}
              </p>
              <div v-else-if="myReservations.length" class="panel-list">
                <article v-for="item in myReservations" :key="item.id" class="panel-item">
                  <strong>{{ item.musicalSpace?.name || t("home.personal.fallbackSpace") }}</strong>
                  <span>{{ formatDate(item.sessionDate) }} &middot; {{ formatReservationStatus(item.state) }}</span>
                </article>
              </div>
              <p v-else class="panel-empty">{{ t("home.placeholders.reservationsText") }}</p>
            </div>
          </div>

          <div class="col-12 col-xl-4">
            <div class="panel-card h-100">
              <h3>{{ t("home.personal.messages") }}</h3>
              <p v-if="personalDataLoading" class="panel-empty">
                {{ t("home.states.loadingPersonal") }}
              </p>
              <p v-else-if="personalDataErrors.messages" class="panel-empty panel-empty--error">
                {{ t("home.errors.messagesText") }}
              </p>
              <div v-else-if="recentMessages.length" class="panel-list">
                <article
                  v-for="item in recentMessages"
                  :key="item.reservationId"
                  class="panel-item"
                >
                  <strong>{{
                    item.musicalSpace?.name ||
                    item.otherUser?.name ||
                    t("home.personal.fallbackConversation")
                  }}</strong>
                  <span>{{
                    t("home.personal.unreadCount", { count: item.unreadMessagesCount })
                  }}</span>
                </article>
              </div>
              <p v-else class="panel-empty">{{ t("home.placeholders.messagesText") }}</p>
            </div>
          </div>
        </div>

        <div v-else-if="!isLogged" class="panel-card panel-card--cta">
          <h3>{{ t("home.personal.loginCtaTitle") }}</h3>
          <p>{{ t("home.personal.loginCtaText") }}</p>
          <div class="d-flex flex-wrap gap-2">
            <RouterLink class="btn hero-search__submit" :to="{ name: 'Login' }">
              {{ t("common.actions.login") }}
            </RouterLink>
            <RouterLink class="btn cta-outline" :to="{ name: 'Register' }">
              {{ t("common.actions.register") }}
            </RouterLink>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import AppImage from "@/common/components/AppImage.vue";
import bandPlaceholder from "@/assets/placeholders/band-placeholder.svg";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";
import { getStore } from "@/common/store";
import BandRecruitmentRepository from "@/repositories/BandRecruitmentRepository";
import EventRepository from "@/repositories/EventRepository";
import MessageRepository from "@/repositories/MessageRepository";
import ReservationSessionRepository from "@/repositories/ReservationSessionRepository";
import SearchRepository from "@/repositories/SearchRepository";
import {
  selectFeaturedEvents,
  selectFeaturedRecruitments,
  selectFeaturedSpaces
} from "../homeFeaturedScoring";

const router = useRouter();
const store = getStore();
const { t, locale } = useI18n();

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);

const searchText = ref("");
const publicDataLoading = ref(true);
const personalDataLoading = ref(false);

const featuredSpaces = ref([]);
const featuredEvents = ref([]);
const featuredRecruitments = ref([]);
const publicSpaces = ref([]);
const publicEvents = ref([]);
const publicRecruitments = ref([]);
const latestSearches = ref([]);
const myReservations = ref([]);
const recentMessages = ref([]);

const publicDataErrors = reactive({
  spaces: false,
  events: false,
  recruitments: false
});

const personalDataErrors = reactive({
  searches: false,
  reservations: false,
  messages: false
});

const quickFilters = reactive({
  city: "",
  date: "",
  spaceType: "",
  musicalGenre: "",
  maxBudget: "",
  peopleCount: ""
});

const displayedSpaces = computed(() => featuredSpaces.value);
const displayedEvents = computed(() => featuredEvents.value);
const displayedRecruitments = computed(() => featuredRecruitments.value);
const publicDataWarning = computed(() => Object.values(publicDataErrors).some(Boolean));

onMounted(async () => {
  await loadPublicData();

  if (canUseUserFeatures.value) {
    await loadPersonalData();
  }
});

function buildSearchQuery(extra = {}) {
  const query = { ...extra };

  if (searchText.value.trim()) query.q = searchText.value.trim();
  if (quickFilters.city.trim()) query.city = quickFilters.city.trim();
  if (quickFilters.date) query.date = quickFilters.date;
  if (quickFilters.spaceType.trim()) query.spaceType = quickFilters.spaceType.trim();
  if (quickFilters.musicalGenre.trim()) query.musicalGenre = quickFilters.musicalGenre.trim();
  if (quickFilters.maxBudget.trim()) query.maxBudget = quickFilters.maxBudget.trim();
  if (quickFilters.peopleCount.trim()) query.peopleCount = quickFilters.peopleCount.trim();

  return query;
}

async function submitPrimarySearch() {
  await router.push({ name: "SearchPage", query: buildSearchQuery() });
}

async function submitQuickFilters() {
  await router.push({ name: "SearchPage", query: buildSearchQuery() });
}

async function loadPublicData() {
  publicDataLoading.value = true;
  resetPublicDataErrors();

  const [spacesResult, eventsResult, recruitmentsResult] = await Promise.allSettled([
    loadFeaturedSpaces(),
    loadFeaturedEvents(),
    loadRecruitments()
  ]);

  if (spacesResult.status === "rejected") {
    featuredSpaces.value = [];
    publicDataErrors.spaces = true;
  }

  if (eventsResult.status === "rejected") {
    featuredEvents.value = [];
    publicDataErrors.events = true;
  }

  if (recruitmentsResult.status === "rejected") {
    featuredRecruitments.value = [];
    publicDataErrors.recruitments = true;
  }

  publicDataLoading.value = false;
}

async function loadFeaturedSpaces() {
  const result = await SearchRepository.search({});
  const enrichedSpaces = (result?.spaces ?? []).map((space) => ({
    ...space,
    rating: Number(space.rating ?? 0),
    reviewsCount: Number(space.reviewsCount ?? 0),
    priceLabel: formatEstimatedPriceLabel(space.estimatedPrice)
  }));

  publicSpaces.value = enrichedSpaces;
  featuredSpaces.value = selectFeaturedSpaces(enrichedSpaces);
}

async function loadFeaturedEvents() {
  let events = [];

  try {
    events = await EventRepository.getUpcoming();
  } catch {
    events = await EventRepository.getAll();
  }

  publicEvents.value = events ?? [];
  featuredEvents.value = selectFeaturedEvents(events);
}

async function loadRecruitments() {
  const recruitments = await BandRecruitmentRepository.getAll();
  publicRecruitments.value = recruitments ?? [];
  featuredRecruitments.value = selectFeaturedRecruitments(recruitments);
}

async function loadPersonalData() {
  personalDataLoading.value = true;
  resetPersonalDataErrors();

  const [historyResult, reservationsResult, messagesResult] = await Promise.allSettled([
    SearchRepository.getHistory(),
    ReservationSessionRepository.getMine(),
    MessageRepository.getUnread()
  ]);

  latestSearches.value =
    historyResult.status === "fulfilled" ? (historyResult.value ?? []).slice(0, 3) : [];
  myReservations.value =
    reservationsResult.status === "fulfilled" ? (reservationsResult.value ?? []).slice(0, 3) : [];
  recentMessages.value =
    messagesResult.status === "fulfilled"
      ? (messagesResult.value?.reservations ?? []).slice(0, 3)
      : [];

  personalDataErrors.searches = historyResult.status === "rejected";
  personalDataErrors.reservations = reservationsResult.status === "rejected";
  personalDataErrors.messages = messagesResult.status === "rejected";
  personalDataLoading.value = false;
}

function resetPublicDataErrors() {
  publicDataErrors.spaces = false;
  publicDataErrors.events = false;
  publicDataErrors.recruitments = false;
}

function resetPersonalDataErrors() {
  personalDataErrors.searches = false;
  personalDataErrors.reservations = false;
  personalDataErrors.messages = false;
}

function formatDate(value) {
  if (!value) return "--";

  return new Intl.DateTimeFormat(locale.value, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(new Date(value));
}

function formatSpaceType(value) {
  return t(`spaceDetail.spaceTypeLabels.${value || "OTHER"}`);
}

function formatEventType(value) {
  return t(`events.types.${value || "OTHER"}`);
}

function formatReservationStatus(value) {
  return t(`reservations.statuses.${value || "PENDING"}`);
}

function getSpaceRoute(space) {
  return space?.id
    ? { name: "MusicalSpaceDetail", params: { id: space.id } }
    : { name: "MusicalSpaceList" };
}

function getEventRoute(event) {
  return event?.id ? { name: "EventDetail", params: { id: event.id } } : { name: "EventList" };
}

function getRecruitmentRoute(recruitment) {
  return recruitment?.id
    ? { name: "BandRecruitmentList", query: { highlight: String(recruitment.id) } }
    : { name: "BandRecruitmentList" };
}

function getEventCardImage(event) {
  return event?.posterImage || event?.mainImage || event?.image || "";
}

function formatDateTime(value) {
  if (!value) return "--";

  return new Intl.DateTimeFormat(locale.value, {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

function formatTimeRange(start, end) {
  if (!start && !end) return "--";
  return [start?.slice(0, 5), end?.slice(0, 5)].filter(Boolean).join(" - ");
}

function formatRating(value) {
  if (typeof value !== "number" || Number.isNaN(value) || value <= 0) return "--";
  return value.toFixed(1);
}

function formatReviews(value) {
  if (!value) return "--";
  return `${value}`;
}

function formatSoundproofed(value) {
  if (value === true) return t("home.featuredSpaces.soundproofed");
  if (value === false) return t("home.featuredSpaces.notSoundproofed");
  return "--";
}

function formatRecruitmentLevel(value) {
  return value ? t(`bands.levels.${value}`) : "--";
}

function formatEstimatedPriceLabel(value) {
  const price = Number(value);

  if (!Number.isFinite(price) || price < 0) {
    return t("common.labels.onRequest");
  }

  return t("home.featuredSpaces.pricePerHour", {
    price: formatCurrency(price)
  });
}

function formatCurrency(value) {
  return new Intl.NumberFormat(locale.value, {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0
  }).format(value);
}
</script>

<style scoped>
.home-page {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(115deg, rgba(29, 185, 84, 0.16) 0%, rgba(29, 185, 84, 0.06) 26rem, rgba(29, 185, 84, 0) 52rem),
    linear-gradient(180deg, #080a09 0%, #050505 44rem, #050505 100%);
  color: #fff;
}

.home-page::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.03) 0%, rgba(255, 255, 255, 0) 4.5rem),
    linear-gradient(180deg, rgba(5, 5, 5, 0) 0%, rgba(5, 5, 5, 0.38) 34rem, rgba(5, 5, 5, 0) 56rem);
}

.home-hero {
  position: relative;
  z-index: 1;
  overflow: hidden;
  min-height: min(720px, calc(100vh - 72px));
  padding-bottom: clamp(4rem, 8vw, 7rem);
  background: transparent;
}

.home-hero::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(5, 5, 5, 0) 0%, rgba(5, 5, 5, 0.5) 100%);
  pointer-events: none;
}

.home-hero > .container {
  position: relative;
  z-index: 1;
}

.py-xl-6 {
  padding-top: 5.5rem;
  padding-bottom: 5.5rem;
}

.hero-copy {
  max-width: 760px;
  margin: 0 auto;
  text-align: center;
}

.hero-copy__title {
  margin-top: 0;
  margin-bottom: 1.1rem;
  font-size: clamp(2.6rem, 5vw, 4.8rem);
  line-height: 0.98;
  letter-spacing: 0;
}

.hero-copy__subtitle {
  max-width: 640px;
  margin: 0 auto 1.75rem;
  color: #b3b3b3;
  font-size: 1.08rem;
  line-height: 1.75;
}

.hero-search {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hero-search__field {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  width: min(100%, 760px);
  padding: 0.7rem;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.28);
}

.hero-search__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 16px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 1rem;
}

.hero-search__input {
  min-height: 58px;
  border: 0;
  border-radius: 20px;
  background: transparent;
  color: #fff;
  font-size: 1rem;
}

.hero-search__input::placeholder {
  color: #8d8d8d;
}

.hero-search__input:focus {
  color: #fff;
  background: transparent;
  box-shadow: none;
}

.hero-search__submit,
.cta-outline {
  min-height: 52px;
  padding: 0 1.2rem;
  border-radius: 18px;
  font-weight: 700;
}

.hero-search__submit {
  border: 0;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
}

.quick-filters {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: stretch;
  gap: 0.9rem;
  margin: 2.25rem auto 0;
  max-width: 1240px;
}

.quick-filter-pill {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
  padding: 0.72rem 0.85rem;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.18);
}

.quick-filters__submit {
  min-width: 0;
  width: 100%;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.section-heading h2 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.section-heading p {
  margin: 0.35rem 0 0;
  color: #b3b3b3;
}

.section-link {
  color: #1db954;
  text-decoration: none;
  font-weight: 600;
  white-space: nowrap;
}

.section-link:hover {
  color: #25d366;
}

.home-warning {
  display: inline-flex;
  align-items: center;
  min-height: 44px;
  margin-top: 1.25rem;
  padding: 0.75rem 1rem;
  border-radius: 16px;
  background: rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.18);
  color: #f4d06f;
}

.home-section {
  position: relative;
  z-index: 1;
  padding: 2rem 0 3rem;
  background: transparent;
}

.home-section--discovery {
  margin-top: -5rem;
  padding-top: 5rem;
}

.home-section--personal {
  padding-bottom: 4.5rem;
}

.content-columns {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1.5rem;
  align-items: stretch;
}

.content-column {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-width: 0;
}

.content-stack {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 1rem;
  height: 100%;
}

.feature-card,
.panel-card,
.empty-state {
  border-radius: 26px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 26px 60px rgba(0, 0, 0, 0.22);
}

.feature-card {
  display: flex;
  flex-direction: column;
  min-height: 34rem;
  padding: 1.4rem;
}

.feature-card__media {
  margin: -1.4rem -1.4rem 1rem;
  aspect-ratio: 16 / 10;
  min-height: 0;
  overflow: hidden;
  border-radius: 26px 26px 18px 18px;
}

.feature-card__media :deep(.app-image),
.feature-card__media :deep(.app-image__img),
.feature-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.feature-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  display: -webkit-box;
  overflow: hidden;
  margin-bottom: 0.45rem;
  font-size: 1.35rem;
  font-weight: 700;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.feature-card__location,
.feature-card__date {
  display: -webkit-box;
  overflow: hidden;
  color: #b3b3b3;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.feature-card__reason {
  display: -webkit-box;
  overflow: hidden;
  margin: 0.15rem 0 1rem;
  min-height: 2.8rem;
  color: #d7f8df;
  font-size: 0.92rem;
  line-height: 1.5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.feature-card__reason--placeholder {
  visibility: hidden;
}

.feature-pill {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.feature-rating {
  color: #fff;
  font-weight: 700;
}

.feature-card__meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
  margin-top: 1.15rem;
}

.feature-card__meta-grid span {
  display: block;
  color: #8f8f8f;
  font-size: 0.82rem;
  margin-bottom: 0.25rem;
}

.feature-card__meta-grid strong {
  display: block;
  font-size: 0.98rem;
}

.feature-card__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: auto;
  padding-top: 1rem;
}

.panel-card {
  height: 100%;
  padding: 1.4rem;
}

.panel-card h3 {
  margin-bottom: 1rem;
  font-size: 1.15rem;
  font-weight: 700;
}

.panel-card--cta {
  padding: 2rem;
}

.panel-card--cta p {
  color: #b3b3b3;
  max-width: 720px;
}

.panel-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.panel-item {
  padding: 0.95rem 1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.panel-item strong {
  display: block;
  margin-bottom: 0.28rem;
}

.panel-item span,
.panel-empty,
.empty-state p {
  color: #b3b3b3;
}

.panel-empty--error,
.empty-state--error p {
  color: #f4d06f;
}

.empty-state {
  padding: 1.3rem 1.4rem;
}

.empty-state--error {
  border-color: rgba(255, 193, 7, 0.22);
  background:
    radial-gradient(circle at top right, rgba(255, 193, 7, 0.08), transparent 34%),
    linear-gradient(180deg, #111111 0%, #181818 100%);
}

.empty-state strong {
  display: block;
  margin-bottom: 0.35rem;
}

.home-label {
  margin-bottom: 0.45rem;
  color: #fff;
  font-weight: 600;
  font-size: 0.76rem;
  letter-spacing: 0;
  text-transform: uppercase;
}

.home-input {
  min-height: 42px;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  color: #fff;
  font-size: 0.96rem;
}

.home-input::placeholder {
  color: #8d8d8d;
}

.home-input:focus {
  color: #fff;
  background: transparent;
  box-shadow: none;
}

.cta-outline {
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #fff;
  background: rgba(255, 255, 255, 0.03);
}

.cta-outline:hover {
  color: #1db954;
  border-color: rgba(29, 185, 84, 0.45);
}

@media (max-width: 991.98px) {
  .quick-filters {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content-columns {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .hero-search__field {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-search__icon {
    width: 100%;
    height: 42px;
  }

  .hero-search__submit {
    width: 100%;
  }

  .feature-card__meta-grid {
    grid-template-columns: 1fr;
  }

}

@media (max-width: 767.98px) {
  .quick-filters {
    grid-template-columns: 1fr;
  }

  .content-columns {
    grid-template-columns: 1fr;
  }

  .section-heading {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-copy__title {
    font-size: 2.4rem;
  }

}

@media (min-width: 1200px) {
  .quick-filters {
    grid-template-columns: repeat(6, minmax(0, 1fr)) 132px;
  }
}
</style>
