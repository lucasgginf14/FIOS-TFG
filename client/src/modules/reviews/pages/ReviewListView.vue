<template>
  <div class="review-page">
    <section class="review-shell">
      <div class="container py-5">
        <header class="review-header">
          <div class="review-header__copy">
            <span class="review-header__eyebrow">{{ t("reviewBoard.header.eyebrow") }}</span>
            <h1>{{ t("reviewBoard.header.title") }}</h1>
            <p>{{ t("reviewBoard.header.subtitle") }}</p>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("reviewBoard.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("reviewBoard.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("reviewBoard.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <ReviewStatsRow :cards="statsCards" />

          <section class="review-panel section-spacing">
            <div class="review-tabs">
              <button
                type="button"
                class="review-tabs__button"
                :class="{ 'is-active': currentTab === 'pending' }"
                @click="currentTab = 'pending'"
              >
                {{ t("reviewBoard.tabs.pending") }}
              </button>
              <button
                type="button"
                class="review-tabs__button"
                :class="{ 'is-active': currentTab === 'mine' }"
                @click="currentTab = 'mine'"
              >
                {{ t("reviewBoard.tabs.mine") }}
              </button>
              <button
                type="button"
                class="review-tabs__button"
                :class="{ 'is-active': currentTab === 'received' }"
                @click="currentTab = 'received'"
              >
                {{ t("reviewBoard.tabs.received") }}
              </button>
            </div>

            <div class="review-panel__header">
              <div>
                <span class="review-panel__eyebrow">
                  {{ activePanelEyebrow }}
                </span>
                <h2>{{ activePanelTitle }}</h2>
              </div>
              <span class="review-panel__count">
                {{ activePanelCount }}
              </span>
            </div>

            <ReviewFiltersBar
              v-if="currentTab !== 'pending'"
              class="review-panel__filters"
              v-model="filters"
              @clear="resetFilters"
            />

            <ReviewEmptyState
              v-if="showEmptyState"
              :tab="currentTab"
              :filtered="currentTab !== 'pending' && Boolean(activeReviewList.length && !filteredReviews.length)"
              @clear="resetFilters"
            />

            <div v-else class="review-grid">
              <template v-if="currentTab === 'pending'">
                <PendingReviewCard
                  v-for="item in pendingReviews"
                  :key="item.reservationId"
                  :item="item"
                  :locale="locale"
                  @review="openReviewModal"
                />
              </template>

              <template v-else>
                <MyReviewCard
                  v-for="item in filteredReviews"
                  :key="item.id"
                  :item="item"
                  :locale="locale"
                />
              </template>
            </div>
          </section>
        </template>
      </div>
    </section>

    <ReviewFormModal
      :open="formModalOpen"
      :model-value="reviewForm"
      :reservation="activePendingReview"
      :submitting="formSubmitting"
      :error-message="formErrorMessage"
      @close="closeReviewModal"
      @submit="submitReview"
      @update:model-value="reviewForm = $event"
    />
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { getApiErrorMessage } from "@/common/apiErrors";
import SpaceReviewRepository from "@/repositories/SpaceReviewRepository";
import UserReviewRepository from "@/repositories/UserReviewRepository";
import MyReviewCard from "../components/MyReviewCard.vue";
import PendingReviewCard from "../components/PendingReviewCard.vue";
import ReviewEmptyState from "../components/ReviewEmptyState.vue";
import ReviewFiltersBar from "../components/ReviewFiltersBar.vue";
import ReviewFormModal from "../components/ReviewFormModal.vue";
import ReviewStatsRow from "../components/ReviewStatsRow.vue";

const { locale, t } = useI18n();
const REVIEW_TYPES = Object.freeze({
  SPACE: "SPACE",
  USER: "USER"
});
const REVIEW_DIRECTIONS = Object.freeze({
  AUTHORED: "AUTHORED",
  RECEIVED: "RECEIVED"
});

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const currentTab = ref("pending");
const pendingReviews = ref([]);
const myReviews = ref([]);
const receivedReviews = ref([]);

const formModalOpen = ref(false);
const formSubmitting = ref(false);
const formErrorMessage = ref("");
const activePendingReview = ref(null);
const reviewForm = ref(createEmptyReviewForm());

const filters = ref(createDefaultFilters());

const activeReviewList = computed(() =>
  currentTab.value === "received" ? receivedReviews.value : myReviews.value
);

const filteredReviews = computed(() => {
  const needle = normalizeText(filters.value.searchText);
  const minRating = Number(filters.value.minRating || 0);

  return [...activeReviewList.value]
    .filter((item) => {
      const haystack = normalizeText(buildReviewSearchText(item));

      if (needle && !haystack.includes(needle)) {
        return false;
      }

      if (minRating > 0 && Number(item.overallRating || 0) < minRating) {
        return false;
      }

      return true;
    })
    .sort(sortReviews);
});

const averageRating = computed(() => {
  if (!myReviews.value.length) {
    return "--";
  }

  const total = myReviews.value.reduce((sum, item) => sum + Number(item.overallRating || 0), 0);
  return (total / myReviews.value.length).toFixed(1);
});

const reviewedTargetsCount = computed(() =>
  new Set(
    myReviews.value
      .map((item) => {
        if (item.reviewType === REVIEW_TYPES.USER) {
          return item.reviewedUser?.id ? `USER:${item.reviewedUser.id}` : null;
        }
        return item.musicalSpace?.id ? `SPACE:${item.musicalSpace.id}` : null;
      })
      .filter(Boolean)
  ).size
);

const statsCards = computed(() => [
  {
    id: "pending",
    icon: "bi bi-chat-square-heart",
    value: pendingReviews.value.length,
    label: t("reviewBoard.stats.pending")
  },
  {
    id: "published",
    icon: "bi bi-stars",
    value: myReviews.value.length,
    label: t("reviewBoard.stats.published")
  },
  {
    id: "received",
    icon: "bi bi-person-heart",
    value: receivedReviews.value.length,
    label: t("reviewBoard.stats.received")
  },
  {
    id: "average",
    icon: "bi bi-star-fill",
    value: averageRating.value,
    label: t("reviewBoard.stats.average")
  },
  {
    id: "targets",
    icon: "bi bi-building",
    value: reviewedTargetsCount.value,
    label: t("reviewBoard.stats.targets")
  }
]);

const activePanelEyebrow = computed(() => {
  if (currentTab.value === "pending") return t("reviewBoard.pending.eyebrow");
  if (currentTab.value === "received") return t("reviewBoard.received.eyebrow");
  return t("reviewBoard.mine.eyebrow");
});

const activePanelTitle = computed(() => {
  if (currentTab.value === "pending") return t("reviewBoard.pending.title");
  if (currentTab.value === "received") return t("reviewBoard.received.title");
  return t("reviewBoard.mine.title");
});

const activePanelCount = computed(() => {
  if (currentTab.value === "pending") {
    return t("reviewBoard.pending.total", { count: pendingReviews.value.length });
  }
  if (currentTab.value === "received") {
    return t("reviewBoard.received.total", { count: filteredReviews.value.length });
  }
  return t("reviewBoard.mine.total", { count: filteredReviews.value.length });
});

const showEmptyState = computed(() => {
  if (currentTab.value === "pending") {
    return pendingReviews.value.length === 0;
  }

  return activeReviewList.value.length === 0 || filteredReviews.value.length === 0;
});

loadPage();

async function loadPage() {
  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;

  try {
    await loadReviewCollections();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "reviewBoard.states.error");
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.value = createDefaultFilters();
}

function openReviewModal(item) {
  activePendingReview.value = item;
  reviewForm.value = createEmptyReviewForm();
  formErrorMessage.value = "";
  formModalOpen.value = true;
}

function closeReviewModal() {
  formModalOpen.value = false;
  formErrorMessage.value = "";
}

async function submitReview() {
  const reservationId = activePendingReview.value?.reservationId;
  const reviewType = activePendingReview.value?.reviewType || REVIEW_TYPES.SPACE;
  const validationError = validateReviewForm(reviewForm.value, t, reviewType);

  if (!reservationId || validationError) {
    formErrorMessage.value = validationError || t("reviewBoard.form.validation");
    return;
  }

  formSubmitting.value = true;
  formErrorMessage.value = "";

  try {
    if (reviewType === REVIEW_TYPES.USER) {
      await UserReviewRepository.createForReservation(reservationId, buildUserReviewPayload(reviewForm.value));
    } else {
      await SpaceReviewRepository.createForReservation(reservationId, buildSpaceReviewPayload(reviewForm.value));
    }

    await loadReviewCollections();
    currentTab.value = "mine";
    pageNotice.value = {
      type: "success",
      message: t("reviewBoard.notices.created")
    };

    closeReviewModal();
  } catch (error) {
    formErrorMessage.value = getApiErrorMessage(error, t, "reviewBoard.form.error");
  } finally {
    formSubmitting.value = false;
  }
}

function createDefaultFilters() {
  return {
    searchText: "",
    minRating: "",
    order: "recent"
  };
}

function createEmptyReviewForm() {
  return {
    overallRating: 0,
    soundQualityRating: 0,
    equipmentRating: 0,
    cleanlinessRating: 0,
    locationRating: 0,
    communicationRating: 0,
    punctualityRating: 0,
    careRating: 0,
    comment: ""
  };
}

function sortReviews(left, right) {
  if (filters.value.order === "highest") {
    return Number(right.overallRating || 0) - Number(left.overallRating || 0);
  }

  if (filters.value.order === "lowest") {
    return Number(left.overallRating || 0) - Number(right.overallRating || 0);
  }

  return new Date(right.createdAt || 0).getTime() - new Date(left.createdAt || 0).getTime();
}

function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}

async function loadReviewCollections() {
  const [pendingSpaces, pendingUsers, spaceReviews, userReviews, receivedUserReviews] = await Promise.all([
    SpaceReviewRepository.getPending(),
    UserReviewRepository.getPending(),
    SpaceReviewRepository.getMine(),
    UserReviewRepository.getMine(),
    UserReviewRepository.getReceived()
  ]);

  pendingReviews.value = [
    ...(pendingSpaces ?? []).map(normalizePendingSpaceReview),
    ...(pendingUsers ?? []).map(normalizePendingUserReview)
  ].sort(sortPendingReviews);

  myReviews.value = [
    ...(spaceReviews ?? []).map(normalizeSpaceReview),
    ...(userReviews ?? []).map((item) => normalizeUserReview(item, REVIEW_DIRECTIONS.AUTHORED))
  ].sort(sortReviews);

  receivedReviews.value = (receivedUserReviews ?? [])
    .map((item) => normalizeUserReview(item, REVIEW_DIRECTIONS.RECEIVED))
    .sort(sortReviews);
}

function normalizePendingSpaceReview(item) {
  return {
    ...item,
    reviewType: REVIEW_TYPES.SPACE
  };
}

function normalizePendingUserReview(item) {
  return {
    ...item,
    reviewType: REVIEW_TYPES.USER
  };
}

function normalizeSpaceReview(item) {
  return {
    ...item,
    reviewType: REVIEW_TYPES.SPACE,
    reviewDirection: REVIEW_DIRECTIONS.AUTHORED
  };
}

function normalizeUserReview(item, reviewDirection) {
  return {
    ...item,
    reviewType: REVIEW_TYPES.USER,
    reviewDirection
  };
}

function sortPendingReviews(left, right) {
  return new Date(right.sessionDate || 0).getTime() - new Date(left.sessionDate || 0).getTime();
}

function buildSpaceReviewPayload(form) {
  return {
    comment: form.comment.trim() || null,
    overallRating: Number(form.overallRating),
    soundQualityRating: Number(form.soundQualityRating),
    equipmentRating: Number(form.equipmentRating),
    cleanlinessRating: Number(form.cleanlinessRating),
    locationRating: Number(form.locationRating)
  };
}

function buildUserReviewPayload(form) {
  return {
    comment: form.comment.trim() || null,
    overallRating: Number(form.overallRating),
    communicationRating: Number(form.communicationRating),
    punctualityRating: Number(form.punctualityRating),
    careRating: Number(form.careRating)
  };
}

function buildReviewSearchText(item) {
  return [
    item.musicalSpace?.name,
    item.comment,
    buildUserName(item.user),
    buildUserName(item.reviewer),
    buildUserName(item.reviewedUser)
  ].join(" ");
}

function buildUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}

function validateReviewForm(form, translate, reviewType) {
  const keys = reviewType === REVIEW_TYPES.USER
    ? ["overallRating", "communicationRating", "punctualityRating", "careRating"]
    : [
        "overallRating",
        "soundQualityRating",
        "equipmentRating",
        "cleanlinessRating",
        "locationRating"
      ];

  const invalid = keys.some((key) => {
    const value = Number(form[key]);
    return !Number.isInteger(value) || value < 1 || value > 5;
  });

  return invalid ? translate("reviewBoard.form.validation") : "";
}
</script>

<style scoped>
.review-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.review-shell {
  padding-bottom: 3rem;
}

.review-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1.5rem;
}

.review-header__eyebrow,
.review-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.review-header h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
}

.review-header p {
  max-width: 700px;
  margin: 0.85rem 0 0;
  color: #b8b8b8;
  font-size: 1rem;
  line-height: 1.7;
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

.review-panel {
  padding: 1.35rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.review-tabs {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.35rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.05);
}

.review-tabs__button {
  min-height: 42px;
  padding: 0 1rem;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #bcbcbc;
  font-weight: 600;
}

.review-tabs__button.is-active {
  background: rgba(29, 185, 84, 0.16);
  color: #ffffff;
}

.review-panel__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 1.25rem;
}

.review-panel__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
  font-weight: 700;
}

.review-panel__count {
  color: #c9c9c9;
  font-size: 0.95rem;
}

.review-panel__filters {
  margin-top: 1rem;
}

.review-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

@media (max-width: 991.98px) {
  .review-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767.98px) {
  .review-header,
  .review-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
