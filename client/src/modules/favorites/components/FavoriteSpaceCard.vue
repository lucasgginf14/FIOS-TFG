<template>
  <article
    class="favorite-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="favorite-card__media">
      <AppImage
        :src="item.mainImage"
        :alt="item.name"
        :fallback-src="spacePlaceholder"
        :fallback-label="t('favorites.card.imagePlaceholder')"
        icon-class="bi bi-heart"
      />
    </div>

    <div class="favorite-card__body">
      <div class="favorite-card__topline">
        <span class="favorite-card__type">{{ t(`spaceDetail.spaceTypeLabels.${item.spaceType || "OTHER"}`) }}</span>
        <span v-if="savedAtLabel" class="favorite-card__saved">
          {{ t("favorites.card.savedAt", { date: savedAtLabel }) }}
        </span>
      </div>

      <h2 class="favorite-card__title">{{ item.name }}</h2>
      <p class="favorite-card__location">
        {{ [item.city, item.province].filter(Boolean).join(", ") || t("favorites.card.locationFallback") }}
      </p>

      <div class="favorite-card__meta">
        <div class="favorite-card__meta-item">
          <span>{{ t("common.labels.capacity") }}</span>
          <strong>{{ t("favorites.card.capacityValue", { value: item.capacity ?? "--" }) }}</strong>
        </div>
        <div class="favorite-card__meta-item">
          <span>{{ t("favorites.card.soundproofed") }}</span>
          <strong>{{ soundproofedLabel }}</strong>
        </div>
        <div class="favorite-card__meta-item">
          <span>{{ t("favorites.card.rating") }}</span>
          <strong>{{ ratingLabel }}</strong>
        </div>
      </div>

      <div class="favorite-card__actions" @keydown.stop>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>
        <button type="button" class="btn btn-outline-danger" :disabled="busy" @click.stop="$emit('remove', item)">
          {{ busy ? t("favorites.card.removing") : t("favorites.card.remove") }}
        </button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  locale: {
    type: String,
    default: "es"
  },
  busy: {
    type: Boolean,
    default: false
  }
});

defineEmits(["remove"]);

const { t } = useI18n();
const router = useRouter();

const detailRoute = computed(() => ({
  name: "MusicalSpaceDetail",
  params: { id: props.item.spaceId }
}));

const cardLabel = computed(() => `${t("favorites.card.viewDetail")}: ${props.item.name}`);

const soundproofedLabel = computed(() => {
  if (props.item.soundproofed === true) {
    return t("favorites.card.soundproofedYes");
  }

  if (props.item.soundproofed === false) {
    return t("favorites.card.soundproofedNo");
  }

  return t("favorites.card.soundproofedUnknown");
});

const ratingLabel = computed(() => {
  if (typeof props.item.rating !== "number" || Number.isNaN(props.item.rating) || props.item.rating <= 0) {
    return t("favorites.card.noRating");
  }

  return t("favorites.card.ratingValue", {
    rating: props.item.rating.toFixed(1),
    count: props.item.reviewsCount || 0
  });
});

const savedAtLabel = computed(() => {
  if (!props.item.savedAt) {
    return "";
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.item.savedAt));
});

function openDetail() {
  if (!props.item.spaceId) {
    return;
  }

  router.push(detailRoute.value);
}
</script>

<style scoped>
.favorite-card {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.favorite-card__media {
  aspect-ratio: 16 / 10;
  min-height: 0;
  background: #0d0d0d;
}

.favorite-card__media :deep(.app-image),
.favorite-card__media :deep(.app-image__img),
.favorite-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.favorite-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 1.25rem;
}

.favorite-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.favorite-card__type {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.favorite-card__saved {
  color: #9f9f9f;
  font-size: 0.8rem;
}

.favorite-card__title {
  margin: 1rem 0 0.4rem;
  font-size: 1.35rem;
  font-weight: 700;
}

.favorite-card__location {
  margin: 0;
  color: #b3b3b3;
}

.favorite-card__meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1.1rem;
}

.favorite-card__meta-item {
  padding: 0.85rem 0.9rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.favorite-card__meta-item span {
  display: block;
  color: #8f8f8f;
  font-size: 0.8rem;
  margin-bottom: 0.25rem;
}

.favorite-card__meta-item strong {
  display: block;
  font-size: 0.94rem;
}

.favorite-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: auto;
  padding-top: 1.2rem;
}

.favorite-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 767.98px) {
  .favorite-card__meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 575.98px) {
  .favorite-card__topline,
  .favorite-card__actions .btn {
    width: 100%;
  }

  .favorite-card__topline {
    flex-direction: column;
    align-items: flex-start;
  }

  .favorite-card__actions {
    justify-content: stretch;
  }
}
</style>
