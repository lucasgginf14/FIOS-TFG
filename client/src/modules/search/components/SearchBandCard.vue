<template>
  <RouterLink class="result-card" :to="detailRoute" :aria-label="cardLabel">
    <div class="result-card__media" :class="{ 'is-placeholder': !band.image }">
      <AppImage
        :src="band.image"
        :alt="band.name"
        :fallback-src="bandPlaceholder"
        :fallback-label="t('bands.cards.imageFallback')"
        icon-class="bi bi-vinyl"
      />
      <div class="result-card__media-shade" aria-hidden="true"></div>
      <div class="result-card__media-top">
        <span class="result-card__badge">{{ t("search.cards.band.badge") }}</span>
      </div>
      <div class="result-card__hero">
        <span class="result-card__hero-mark" aria-hidden="true">{{ initials }}</span>
        <div>
          <div class="result-card__hero-band">{{ band.name }}</div>
          <div class="result-card__hero-genre">{{ band.mainGenre || t("bands.cards.noGenre") }}</div>
        </div>
      </div>
    </div>

    <div class="result-card__body">
      <div class="result-card__topline">
        <div>
          <div class="result-card__eyebrow">{{ activeLabel }}</div>
          <h3>{{ band.name }}</h3>
        </div>
      </div>

      <p class="result-card__description">
        {{ band.description || t("bands.cards.noDescription") }}
      </p>

      <p class="result-card__location">
        <i class="bi bi-geo-alt"></i>
        {{ band.baseCity || t("bands.cards.noCity") }}
      </p>

      <div class="result-card__meta">
        <span><i class="bi bi-disc"></i> {{ band.mainGenre || t("bands.cards.noGenre") }}</span>
        <span><i class="bi bi-clock-history"></i> {{ createdAtLabel }}</span>
      </div>

      <div class="result-card__footer" aria-hidden="true">
        <span class="result-card__open-indicator">
          <i class="bi bi-arrow-up-right"></i>
        </span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import bandPlaceholder from "@/assets/placeholders/band-placeholder.svg";

const props = defineProps({
  band: {
    type: Object,
    required: true
  }
});

const { locale, t } = useI18n();

const detailRoute = computed(() => ({
  name: "BandDetail",
  params: { id: props.band.id }
}));

const cardLabel = computed(() => `${t("search.cards.band.cta")}: ${props.band.name}`);

const activeLabel = computed(() =>
  props.band.active === false ? t("bands.statuses.inactive") : t("bands.statuses.active")
);

const createdAtLabel = computed(() => {
  if (!props.band.creationDate) {
    return "--";
  }

  const date = new Date(props.band.creationDate);

  if (Number.isNaN(date.getTime())) {
    return "--";
  }

  return new Intl.DateTimeFormat(locale.value, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(date);
});

const initials = computed(() =>
  (props.band.name || "FIOS")
    .split(" ")
    .slice(0, 2)
    .map((chunk) => chunk.charAt(0))
    .join("")
    .toUpperCase()
);
</script>

<style scoped>
.result-card {
  position: relative;
  display: grid;
  grid-template-columns: minmax(240px, 300px) minmax(0, 1fr);
  overflow: hidden;
  border-radius: 24px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 26px 60px rgba(0, 0, 0, 0.22);
  color: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: transform 180ms ease, border-color 180ms ease, box-shadow 180ms ease;
}

.result-card:hover,
.result-card:focus-visible {
  color: inherit;
  outline: none;
  transform: translateY(-3px);
  border-color: rgba(29, 185, 84, 0.5);
  box-shadow: 0 30px 70px rgba(0, 0, 0, 0.34), 0 0 0 1px rgba(29, 185, 84, 0.18);
}

.result-card:hover .result-card__topline h3,
.result-card:focus-visible .result-card__topline h3 {
  color: #e9fff1;
}

.result-card__media {
  position: relative;
  height: clamp(190px, 24vw, 220px);
  min-height: 0;
  overflow: hidden;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.28), transparent 32%),
    linear-gradient(160deg, #0f2015 0%, #111111 100%);
}

.result-card__media :deep(.app-image),
.result-card__media :deep(.app-image__img),
.result-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.result-card__media :deep(.app-image__img) {
  transform: scale(1.01);
  transition: transform 220ms ease;
}

.result-card:hover .result-card__media :deep(.app-image__img),
.result-card:focus-visible .result-card__media :deep(.app-image__img) {
  transform: scale(1.045);
}

.result-card__media-shade {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(0, 0, 0, 0.34) 0%, transparent 42%),
    linear-gradient(0deg, rgba(0, 0, 0, 0.72) 0%, transparent 68%);
  pointer-events: none;
}

.result-card__media-top {
  position: absolute;
  inset: 1rem 1rem auto 1rem;
  display: flex;
  align-items: flex-start;
  gap: 0.6rem;
}

.result-card__badge {
  display: inline-flex;
  align-items: center;
  min-width: 0;
  max-width: 100%;
  min-height: 34px;
  padding: 0 0.9rem;
  border-radius: 14px;
  background: rgba(10, 10, 10, 0.68);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
  line-height: 1.1;
}

.result-card__hero {
  position: absolute;
  left: 1rem;
  right: 1rem;
  bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.95rem;
  padding: 0.85rem;
  border-radius: 18px;
  background: rgba(8, 8, 8, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
}

.result-card__hero-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  flex: 0 0 auto;
  border-radius: 16px;
  background: rgba(29, 185, 84, 0.18);
  color: #1db954;
  font-weight: 800;
}

.result-card__hero-band {
  color: #ffffff;
  font-size: 1.05rem;
  font-weight: 700;
  line-height: 1.2;
}

.result-card__hero-genre {
  color: #aeb5af;
}

.result-card__body {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1.25rem 4.65rem 1.25rem 1.25rem;
}

.result-card__eyebrow {
  color: #1db954;
  font-size: 0.84rem;
  font-weight: 700;
}

.result-card__topline h3 {
  margin: 0.4rem 0 0;
  color: #ffffff;
  font-size: 1.35rem;
  font-weight: 700;
  line-height: 1.2;
  transition: color 180ms ease;
}

.result-card__description {
  display: -webkit-box;
  margin: 0;
  overflow: hidden;
  color: #b5b5b5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.result-card__location,
.result-card__meta span {
  color: #c4c4c4;
}

.result-card__location {
  margin: 0;
}

.result-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.result-card__meta span {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 0.7rem;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.result-card__meta i,
.result-card__location i {
  margin-right: 0.35rem;
  color: #1db954;
}

.result-card__footer {
  display: flex;
  justify-content: flex-end;
  position: absolute;
  right: 1.25rem;
  bottom: 1.25rem;
  margin-top: 0;
  pointer-events: none;
}

.result-card__open-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid rgba(29, 185, 84, 0.28);
  background: rgba(29, 185, 84, 0.12);
  color: #ffffff;
  opacity: 0.72;
  transform: translateX(-4px);
  transition: opacity 180ms ease, transform 180ms ease, border-color 180ms ease, background 180ms ease;
}

.result-card:hover .result-card__open-indicator,
.result-card:focus-visible .result-card__open-indicator {
  opacity: 1;
  transform: translateX(0);
  border-color: rgba(29, 185, 84, 0.45);
  background: rgba(29, 185, 84, 0.18);
}

@media (max-width: 767.98px) {
  .result-card {
    grid-template-columns: 1fr;
  }

  .result-card__body {
    padding: 1.15rem 4.35rem 1.15rem 1.15rem;
  }
}
</style>
