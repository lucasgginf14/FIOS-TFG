<template>
  <article
    class="space-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="space-card__media" :class="{ 'is-placeholder': !space.mainImage }">
      <AppImage
        :src="space.mainImage"
        :alt="space.name"
        :fallback-src="spacePlaceholder"
        :fallback-label="t('spaceList.card.imagePlaceholder')"
        icon-class="bi bi-building"
      />
      <div class="space-card__media-shade" aria-hidden="true"></div>
    </div>

    <div class="space-card__body">
      <div class="space-card__topline">
        <span class="space-card__type">{{ t(`spaceDetail.spaceTypeLabels.${space.spaceType || "OTHER"}`) }}</span>
        <MusicalSpaceStatusBadge
          v-if="mine"
          :approval-status="space.approvalStatus"
          :active="space.active"
        />
      </div>

      <h2 class="space-card__title">{{ space.name }}</h2>
      <p class="space-card__location">
        <i class="bi bi-geo-alt" aria-hidden="true"></i>
        {{ [space.city, space.province].filter(Boolean).join(", ") || t("spaceList.card.locationFallback") }}
      </p>

      <div class="space-card__meta">
        <div class="space-card__meta-item">
          <span><i class="bi bi-people" aria-hidden="true"></i>{{ t("common.labels.capacity") }}</span>
          <strong>{{ t("spaceList.card.capacityValue", { value: space.capacity ?? "--" }) }}</strong>
        </div>
        <div class="space-card__meta-item">
          <span><i class="bi bi-aspect-ratio" aria-hidden="true"></i>{{ t("spaceList.card.squareMeters") }}</span>
          <strong>{{ formatSquareMeters(space.squareMeters) }}</strong>
        </div>
        <div class="space-card__meta-item">
          <span><i class="bi bi-volume-mute" aria-hidden="true"></i>{{ t("spaceList.card.soundproofed") }}</span>
          <strong>{{ soundproofedLabel }}</strong>
        </div>
        <div class="space-card__meta-item">
          <span><i class="bi bi-star" aria-hidden="true"></i>{{ t("spaceList.card.rating") }}</span>
          <strong>{{ formatRating(space.rating, space.reviewsCount) }}</strong>
        </div>
      </div>

      <p v-if="space.description" class="space-card__description">
        {{ truncate(space.description) }}
      </p>

      <div class="space-card__actions" @keydown.stop>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>

        <template v-if="mine">
          <button
            v-if="canManageAvailability"
            type="button"
            class="btn btn-success"
            @click.stop="openAvailability"
          >
            <i class="bi bi-calendar-week" aria-hidden="true"></i>
            {{ t("spaceList.card.manageAvailability") }}
          </button>
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="!space.active"
            @click.stop="$emit('edit', space)"
          >
            {{ t("spaceList.card.edit") }}
          </button>
          <button
            type="button"
            class="btn btn-outline-danger"
            :disabled="busy || !space.active"
            @click.stop="$emit('deactivate', space)"
          >
            {{ busy ? t("spaceList.card.deactivating") : t("spaceList.card.deactivate") }}
          </button>
        </template>
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
import MusicalSpaceStatusBadge from "./MusicalSpaceStatusBadge.vue";

const props = defineProps({
  space: {
    type: Object,
    required: true
  },
  mine: {
    type: Boolean,
    default: false
  },
  busy: {
    type: Boolean,
    default: false
  }
});

defineEmits(["edit", "deactivate"]);

const { t } = useI18n();
const router = useRouter();

const detailRoute = computed(() => ({
  name: "MusicalSpaceDetail",
  params: { id: props.space.id }
}));
const availabilityRoute = computed(() => ({
  name: "MusicalSpaceAvailabilityManage",
  params: { id: props.space.id }
}));

const cardLabel = computed(() => `${t("spaceList.card.viewDetail")}: ${props.space.name}`);
const canManageAvailability = computed(() =>
  props.mine && props.space.active !== false && props.space.approvalStatus === "APPROVED"
);

const soundproofedLabel = computed(() => {
  if (props.space.soundproofed === true) {
    return t("spaceList.card.soundproofedYes");
  }

  if (props.space.soundproofed === false) {
    return t("spaceList.card.soundproofedNo");
  }

  return t("spaceList.card.soundproofedUnknown");
});

function formatSquareMeters(value) {
  return value != null
    ? t("spaceList.card.squareMetersValue", { value })
    : t("spaceList.card.notSpecified");
}

function formatRating(rating, reviewsCount) {
  if (typeof rating !== "number" || Number.isNaN(rating) || rating <= 0) {
    return t("spaceList.card.noRating");
  }

  return t("spaceList.card.ratingValue", {
    rating: rating.toFixed(1),
    count: reviewsCount || 0
  });
}

function truncate(value) {
  const text = (value || "").trim();

  if (text.length <= 140) {
    return text;
  }

  return `${text.slice(0, 137)}...`;
}

function openDetail() {
  if (!props.space.id) {
    return;
  }

  router.push(detailRoute.value);
}

function openAvailability() {
  if (!props.space.id || !canManageAvailability.value) {
    return;
  }

  router.push(availabilityRoute.value);
}
</script>

<style scoped>
.space-card {
  position: relative;
  display: flex;
  flex-direction: column;
  min-height: 100%;
  overflow: hidden;
  border-radius: 28px;
  background:
    linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.space-card__media {
  position: relative;
  aspect-ratio: 16 / 10;
  min-height: 0;
  overflow: hidden;
  background: #0d0d0d;
}

.space-card__media :deep(.app-image),
.space-card__media :deep(.app-image__img),
.space-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.space-card__media :deep(img) {
  transition: transform 0.3s ease;
}

.space-card:hover .space-card__media :deep(img),
.space-card:focus-visible .space-card__media :deep(img) {
  transform: scale(1.04);
}

.space-card__media-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 45%, rgba(0, 0, 0, 0.38));
  pointer-events: none;
}

.space-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 1.3rem;
}

.space-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.space-card__type {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.13);
  color: #dfffe9;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.space-card__title {
  margin: 1rem 0 0.4rem;
  font-size: 1.35rem;
  font-weight: 700;
}

.space-card__location {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin: 0;
  color: #b3b3b3;
}

.space-card__location i {
  color: #1db954;
  font-size: 0.95rem;
}

.space-card__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1.1rem;
}

.space-card__meta-item {
  padding: 0.9rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.space-card__meta-item span {
  display: flex;
  align-items: center;
  gap: 0.38rem;
  color: #8f8f8f;
  font-size: 0.8rem;
  margin-bottom: 0.25rem;
}

.space-card__meta-item span i {
  color: #1db954;
  font-size: 0.86rem;
}

.space-card__meta-item strong {
  display: block;
  font-size: 0.94rem;
}

.space-card__description {
  margin: 1rem 0 0;
  color: #cdcdcd;
  line-height: 1.7;
}

.space-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: auto;
  padding-top: 1.2rem;
}

.space-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 575.98px) {
  .space-card__meta {
    grid-template-columns: 1fr;
  }

  .space-card__actions .btn {
    width: 100%;
  }

  .space-card__actions {
    justify-content: stretch;
  }
}
</style>
