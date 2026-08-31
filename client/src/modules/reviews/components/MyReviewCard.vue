<template>
  <article
    class="my-review-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="my-review-card__header">
      <div>
        <span class="my-review-card__eyebrow">{{ eyebrowLabel }}</span>
        <h2>{{ titleLabel }}</h2>
        <p>{{ subtitleLabel }}</p>
      </div>

      <div class="my-review-card__summary">
        <RatingStars
          :model-value="Number(item.overallRating || 0)"
          :label="t('reviewBoard.form.overallRating')"
          readonly
          show-value
        />
        <span>{{ createdAtLabel }}</span>
      </div>
    </div>

    <p class="my-review-card__comment">
      {{ item.comment || t("reviewBoard.mine.noComment") }}
    </p>

    <div class="my-review-card__metrics">
      <article v-for="metric in metricFields" :key="metric.field" class="my-review-card__metric">
        <span>{{ metric.label }}</span>
        <RatingStars :model-value="Number(item[metric.field] || 0)" :label="metric.label" readonly compact />
      </article>
    </div>

    <div class="my-review-card__actions">
      <span class="clickable-card__open-indicator" aria-hidden="true">
        <i class="bi bi-arrow-up-right"></i>
      </span>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import RatingStars from "./RatingStars.vue";

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  locale: {
    type: String,
    default: "es"
  }
});

const router = useRouter();
const { t } = useI18n();

const isUserReview = computed(() => props.item.reviewType === "USER");
const isReceivedReview = computed(() => props.item.reviewDirection === "RECEIVED");

const detailRoute = computed(() =>
  props.item.musicalSpace?.id
    ? { name: "MusicalSpaceDetail", params: { id: props.item.musicalSpace.id } }
    : null
);

const titleLabel = computed(() => {
  if (!isUserReview.value) {
    return props.item.musicalSpace?.name || t("reviewBoard.mine.fallbackSpace");
  }

  const user = isReceivedReview.value ? props.item.reviewer : props.item.reviewedUser;
  return buildUserName(user) || t("reviewBoard.mine.fallbackUser");
});

const subtitleLabel = computed(() => {
  const spaceContext = [
    props.item.musicalSpace?.name,
    props.item.musicalSpace?.city,
    props.item.musicalSpace?.province
  ].filter(Boolean).join(", ");

  if (isUserReview.value) {
    return spaceContext || t("reviewBoard.mine.noLocation");
  }

  return [props.item.musicalSpace?.city, props.item.musicalSpace?.province]
    .filter(Boolean)
    .join(", ") || t("reviewBoard.mine.noLocation");
});

const eyebrowLabel = computed(() => {
  if (!isUserReview.value) return t("reviewBoard.types.SPACE");
  return isReceivedReview.value ? t("reviewBoard.types.USER_RECEIVED") : t("reviewBoard.types.USER");
});

const metricFields = computed(() => {
  if (isUserReview.value) {
    return [
      { field: "communicationRating", label: t("reviewBoard.metrics.communication") },
      { field: "punctualityRating", label: t("reviewBoard.metrics.punctuality") },
      { field: "careRating", label: t("reviewBoard.metrics.care") }
    ];
  }

  return [
    { field: "soundQualityRating", label: t("reviewBoard.metrics.sound") },
    { field: "equipmentRating", label: t("reviewBoard.metrics.equipment") },
    { field: "cleanlinessRating", label: t("reviewBoard.metrics.cleanliness") },
    { field: "locationRating", label: t("reviewBoard.metrics.location") }
  ];
});

const cardLabel = computed(() => `${t("reviewBoard.actions.viewSpace")}: ${titleLabel.value}`);

const createdAtLabel = computed(() => {
  if (!props.item.createdAt) {
    return "--";
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.item.createdAt));
});

function openDetail() {
  if (detailRoute.value) {
    router.push(detailRoute.value);
  }
}

function buildUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}
</script>

<style scoped>
.my-review-card {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.my-review-card__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.my-review-card__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.my-review-card h2 {
  margin: 0.45rem 0 0.35rem;
  color: #ffffff;
  font-size: 1.28rem;
  font-weight: 700;
}

.my-review-card__header p,
.my-review-card__summary span {
  margin: 0;
  color: #b8b8b8;
}

.my-review-card__summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.4rem;
}

.my-review-card__comment {
  margin: 1rem 0 0;
  color: #d7d7d7;
  line-height: 1.7;
  white-space: pre-wrap;
}

.my-review-card__metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.my-review-card__metric {
  padding: 0.9rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.my-review-card__metric span {
  display: block;
  margin-bottom: 0.45rem;
  color: #bdbdbd;
  font-size: 0.84rem;
}

.my-review-card__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: auto;
  padding-top: 1rem;
}

.my-review-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 767.98px) {
  .my-review-card__header {
    flex-direction: column;
  }

  .my-review-card__summary {
    align-items: flex-start;
  }

  .my-review-card__metrics {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 575.98px) {
  .my-review-card__actions .btn {
    width: 100%;
  }
}
</style>
