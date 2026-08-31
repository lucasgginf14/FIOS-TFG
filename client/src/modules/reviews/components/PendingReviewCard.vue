<template>
  <article
    class="pending-review-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="pending-review-card__media">
      <AppImage
        :src="mediaSrc"
        :alt="titleLabel"
        :fallback-src="fallbackSrc"
        :fallback-label="fallbackLabel"
        :icon-class="mediaIcon"
      />
    </div>

    <div class="pending-review-card__body">
      <div class="pending-review-card__topline">
        <span class="pending-review-card__type">
          {{ t(`reviewBoard.types.${item.reviewType || "SPACE"}`) }}
        </span>
        <span class="pending-review-card__type pending-review-card__type--muted">
          {{ t(`reservations.sessionTypes.${item.sessionType || "OTHER"}`) }}
        </span>
      </div>

      <h2>{{ titleLabel }}</h2>
      <p class="pending-review-card__location">
        {{ subtitleLabel }}
      </p>

      <div class="pending-review-card__meta">
        <span>
          <i class="bi bi-calendar3"></i>
          {{ sessionDateLabel }}
        </span>
        <span>
          <i class="bi bi-clock"></i>
          {{ timeRangeLabel }}
        </span>
      </div>

      <div class="pending-review-card__actions" @keydown.stop>
        <button type="button" class="btn btn-success" @click.stop="$emit('review', item)">
          {{ t("reviewBoard.actions.write") }}
        </button>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>
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
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";

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

defineEmits(["review"]);

const router = useRouter();
const { t } = useI18n();

const isUserReview = computed(() => props.item.reviewType === "USER");

const detailRoute = computed(() =>
  props.item.musicalSpace?.id
    ? { name: "MusicalSpaceDetail", params: { id: props.item.musicalSpace.id } }
    : null
);

const titleLabel = computed(() => {
  if (isUserReview.value) {
    return buildUserName(props.item.reviewedUser) || t("reviewBoard.pending.fallbackUser");
  }

  return props.item.musicalSpace?.name || t("reviewBoard.pending.fallbackSpace");
});

const subtitleLabel = computed(() => {
  if (isUserReview.value) {
    return [
      props.item.musicalSpace?.name,
      props.item.musicalSpace?.city,
      props.item.musicalSpace?.province
    ].filter(Boolean).join(", ") || t("reviewBoard.pending.noLocation");
  }

  return [props.item.musicalSpace?.city, props.item.musicalSpace?.province]
    .filter(Boolean)
    .join(", ") || t("reviewBoard.pending.noLocation");
});

const mediaSrc = computed(() =>
  isUserReview.value ? props.item.reviewedUser?.profileImage : props.item.musicalSpace?.mainImage
);

const fallbackSrc = computed(() => (isUserReview.value ? avatarPlaceholder : spacePlaceholder));

const fallbackLabel = computed(() =>
  isUserReview.value ? t("reviewBoard.pending.fallbackUser") : t("reviewBoard.pending.fallbackSpace")
);

const mediaIcon = computed(() => (isUserReview.value ? "bi bi-person" : "bi bi-music-note-list"));

const cardLabel = computed(() => `${t("reviewBoard.actions.viewSpace")}: ${titleLabel.value}`);

const sessionDateLabel = computed(() => {
  if (!props.item.sessionDate) {
    return "--";
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.item.sessionDate));
});

const timeRangeLabel = computed(() => {
  const start = formatTime(props.item.startTime);
  const end = formatTime(props.item.endTime);

  if (!start && !end) {
    return t("reviewBoard.pending.noSchedule");
  }

  return [start, end].filter(Boolean).join(" - ");
});

function formatTime(value) {
  if (!value) {
    return "";
  }

  return String(value).slice(0, 5);
}

function buildUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}

function openDetail() {
  if (detailRoute.value) {
    router.push(detailRoute.value);
  }
}
</script>

<style scoped>
.pending-review-card {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr);
  min-height: 100%;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.pending-review-card__media {
  aspect-ratio: 1;
  align-self: start;
  min-height: 0;
  background: rgba(255, 255, 255, 0.03);
}

.pending-review-card__media :deep(.app-image),
.pending-review-card__media :deep(.app-image__img),
.pending-review-card__media :deep(.app-image__placeholder) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.pending-review-card__body {
  display: flex;
  flex-direction: column;
  padding: 1.2rem;
}

.pending-review-card__type {
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

.pending-review-card__topline {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.pending-review-card__type--muted {
  background: rgba(255, 255, 255, 0.06);
  color: #cfcfcf;
}

.pending-review-card h2 {
  margin: 0.9rem 0 0.4rem;
  color: #ffffff;
  font-size: 1.3rem;
  font-weight: 700;
}

.pending-review-card__location {
  margin: 0;
  color: #b3b3b3;
  font-weight: 600;
}

.pending-review-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem 1.15rem;
  margin-top: 1rem;
  color: #c4c4c4;
}

.pending-review-card__meta i {
  margin-right: 0.35rem;
  color: #1db954;
}

.pending-review-card__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: auto;
  padding-top: 1.2rem;
}

.pending-review-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 767.98px) {
  .pending-review-card {
    grid-template-columns: 1fr;
  }

  .pending-review-card__media {
    aspect-ratio: auto;
    height: clamp(180px, 42vw, 240px);
  }
}

@media (max-width: 575.98px) {
  .pending-review-card__actions .btn {
    width: 100%;
  }
}
</style>
