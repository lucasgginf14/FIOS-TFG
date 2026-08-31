<template>
  <div class="favorite-page">
    <section class="favorite-shell">
      <div class="container py-5">
        <header class="favorite-header">
          <div class="favorite-header__copy">
            <span class="favorite-header__eyebrow">{{ t("favorites.header.eyebrow") }}</span>
            <h1>{{ t("favorites.header.title") }}</h1>
          </div>

          <RouterLink class="btn btn-success favorite-header__cta" :to="{ name: 'MusicalSpaceList' }">
            {{ t("favorites.header.explore") }}
          </RouterLink>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("favorites.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("favorites.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadFavorites">
            {{ t("favorites.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <section class="section-spacing">
            <FavoriteFiltersBar
              v-model="filters"
              :city-options="cityOptions"
              :type-options="typeOptions"
              @clear="resetFilters"
            />
          </section>

          <section class="favorite-results section-spacing">
            <div class="favorite-results__header">
              <div>
                <span class="favorite-results__eyebrow">{{ t("favorites.list.eyebrow") }}</span>
                <h2>{{ t("favorites.list.title") }}</h2>
              </div>
              <span class="favorite-results__count">
                {{ t("favorites.list.total", { count: visibleFavorites.length }) }}
              </span>
            </div>

            <FavoriteEmptyState
              v-if="!favoriteItems.length || !visibleFavorites.length"
              :filtered="Boolean(favoriteItems.length && !visibleFavorites.length)"
              @clear="resetFilters"
            />

            <div v-else class="favorite-grid">
              <FavoriteSpaceCard
                v-for="item in visibleFavorites"
                :key="item.favoriteId"
                :item="item"
                :locale="locale"
                :busy="removingSpaceId === item.spaceId"
                @remove="handleRemove"
              />
            </div>
          </section>
        </template>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { RouterLink } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import FavoriteSpaceRepository from "@/repositories/FavoriteSpaceRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import SpaceReviewRepository from "@/repositories/SpaceReviewRepository";
import FavoriteEmptyState from "../components/FavoriteEmptyState.vue";
import FavoriteFiltersBar from "../components/FavoriteFiltersBar.vue";
import FavoriteSpaceCard from "../components/FavoriteSpaceCard.vue";

const { locale, t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const rawFavorites = ref([]);
const metaById = ref({});
const removingSpaceId = ref(null);
const filters = ref(createDefaultFilters());

const loadingDetailIds = new Set();
const loadingRatingIds = new Set();

const favoriteItems = computed(() =>
  rawFavorites.value.map((favorite) => {
    const space = favorite.musicalSpace ?? {};
    const meta = metaById.value[space.id] ?? {};

    return {
      favoriteId: favorite.id,
      spaceId: space.id,
      savedAt: favorite.savedAt ?? null,
      name: meta.name ?? space.name ?? "",
      spaceType: meta.spaceType ?? space.spaceType ?? "",
      capacity: meta.capacity ?? space.capacity ?? null,
      mainImage: meta.mainImage ?? space.mainImage ?? "",
      city: meta.location?.city ?? space.city ?? "",
      province: meta.location?.province ?? space.province ?? "",
      active: typeof meta.active === "boolean" ? meta.active : space.active,
      soundproofed: typeof meta.soundproofed === "boolean" ? meta.soundproofed : null,
      rating: meta.rating ?? null,
      reviewsCount: meta.reviewsCount ?? 0
    };
  })
);

const visibleFavorites = computed(() => {
  const text = normalizeText(filters.value.searchText);

  return [...favoriteItems.value]
    .filter((item) => {
      const haystack = normalizeText([
        item.name,
        item.city,
        item.province,
        t(`spaceDetail.spaceTypeLabels.${item.spaceType || "OTHER"}`)
      ].join(" "));

      if (text && !haystack.includes(text)) {
        return false;
      }

      if (filters.value.city && item.city !== filters.value.city) {
        return false;
      }

      if (filters.value.spaceType && item.spaceType !== filters.value.spaceType) {
        return false;
      }

      return true;
    })
    .sort(sortFavorites);
});

const cityOptions = computed(() =>
  [...new Set(favoriteItems.value.map((item) => item.city).filter(Boolean))]
    .sort((left, right) => left.localeCompare(right))
);

const typeOptions = computed(() =>
  [...new Set(favoriteItems.value.map((item) => item.spaceType).filter(Boolean))]
);

watch(
  () => visibleFavorites.value.slice(0, 12).map((item) => item.spaceId).join(","),
  (signature) => {
    if (!signature) {
      return;
    }

    const ids = visibleFavorites.value.slice(0, 12).map((item) => item.spaceId);
    ensureDetails(ids);
    ensureRatings(ids);
  },
  { immediate: true }
);

loadFavorites();

async function loadFavorites() {
  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;

  try {
    rawFavorites.value = await FavoriteSpaceRepository.getMine();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "favorites.states.error");
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  filters.value = createDefaultFilters();
}

async function handleRemove(item) {
  const confirmed = window.confirm(t("favorites.confirm.remove", { name: item.name }));

  if (!confirmed) {
    return;
  }

  removingSpaceId.value = item.spaceId;

  try {
    await FavoriteSpaceRepository.remove(item.spaceId);
    rawFavorites.value = rawFavorites.value.filter((favorite) => favorite.musicalSpace?.id !== item.spaceId);
    pageNotice.value = {
      type: "success",
      message: t("favorites.notices.removed")
    };
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "favorites.notices.removeError")
    };
  } finally {
    removingSpaceId.value = null;
  }
}

function ensureDetails(ids) {
  ids.forEach((id) => {
    if (!id || hasSpaceDetail(metaById.value[id]) || loadingDetailIds.has(id)) {
      return;
    }

    loadDetail(id);
  });
}

function ensureRatings(ids) {
  ids.forEach((id) => {
    if (!id) {
      return;
    }

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

function sortFavorites(left, right) {
  if (filters.value.order === "name") {
    return `${left.name || ""}`.localeCompare(`${right.name || ""}`);
  }

  if (filters.value.order === "city") {
    return `${left.city || ""}${left.name || ""}`.localeCompare(`${right.city || ""}${right.name || ""}`);
  }

  if (filters.value.order === "rating") {
    const leftRating = Number(left.rating || 0);
    const rightRating = Number(right.rating || 0);

    if (leftRating !== rightRating) {
      return rightRating - leftRating;
    }
  }

  return new Date(right.savedAt || 0).getTime() - new Date(left.savedAt || 0).getTime();
}

function createDefaultFilters() {
  return {
    searchText: "",
    city: "",
    spaceType: "",
    order: "recent"
  };
}

function hasSpaceDetail(space) {
  return Boolean(space?.location || typeof space?.soundproofed === "boolean");
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
.favorite-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.favorite-shell {
  padding-bottom: 3rem;
}

.favorite-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.favorite-header__copy {
  max-width: 760px;
}

.favorite-header__eyebrow,
.favorite-results__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.favorite-header h1 {
  margin: 0.4rem 0 0.6rem;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: -0.04em;
}

.favorite-header__cta {
  min-height: 46px;
  padding-inline: 1.15rem;
  border-radius: 16px;
}

.section-spacing {
  margin-top: 1.2rem;
}

.favorite-results {
  padding: 1.35rem;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.favorite-results__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.favorite-results__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.4rem;
}

.favorite-results__count {
  color: #b9b9b9;
}

.favorite-grid {
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
  .favorite-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .favorite-header,
  .favorite-results__header {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 767.98px) {
  .favorite-grid {
    grid-template-columns: 1fr;
  }
}
</style>
