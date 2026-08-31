<template>
  <RouterLink class="event-card clickable-card" :to="detailRoute" :aria-label="cardLabel">
    <div class="event-card__media">
      <AppImage
        :src="image"
        :alt="event.title"
        :fallback-src="eventPlaceholder"
        :fallback-label="t('events.cards.imageFallback')"
        icon-class="bi bi-calendar2-event"
      />
    </div>

    <div class="event-card__body">
      <div class="event-card__badges">
        <span class="event-badge">{{ t(`events.types.${event.eventType || "OTHER"}`) }}</span>
        <span class="event-badge" :class="getEventBadgeTone(event.source)">
          {{ t(`events.sources.${event.source || "INTERNAL"}`) }}
        </span>
      </div>

      <h3>{{ event.title }}</h3>
      <p class="event-card__meta">{{ formatEventDateTime(event, locale) }}</p>
      <p class="event-card__meta">{{ locationLabel || t("events.cards.locationFallback") }}</p>

      <div class="event-card__chips">
        <span v-if="event.musicalGenre" class="event-chip">{{ event.musicalGenre }}</span>
        <span v-if="event.ticketPrice != null" class="event-chip">
          {{ ticketPriceLabel }}
        </span>
        <span v-if="event.capacity != null" class="event-chip">
          {{ t("events.cards.capacity", { value: event.capacity }) }}
        </span>
      </div>

      <div class="event-card__footer">
        <span class="event-card__source">
          {{ event.source === "EXTERNAL" ? "Ticketmaster" : "FIOS" }}
        </span>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import {
  formatEventDateTime,
  formatEventMoney,
  getEventBadgeTone,
  getEventImage,
  getEventLocationLabel
} from "../eventUtils";

const props = defineProps({
  event: {
    type: Object,
    required: true
  }
});

const { locale, t } = useI18n();

const image = computed(() => getEventImage(props.event));
const locationLabel = computed(() => getEventLocationLabel(props.event));
const detailRoute = computed(() => ({
  name: "EventDetail",
  params: { id: props.event.id }
}));
const cardLabel = computed(() => `${t("events.actions.view")}: ${props.event.title}`);
const ticketPriceLabel = computed(() =>
  formatEventMoney(
    props.event.ticketPrice,
    locale.value,
    t("events.cards.onRequest"),
    t("common.labels.free")
  )
);
</script>

<style scoped>
.event-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  border-radius: 26px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.event-card__media {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #0e0e0e;
}

.event-card__media :deep(.app-image),
.event-card__media :deep(.app-image__img),
.event-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.event-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 1.15rem;
}

.event-card__badges,
.event-card__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
}

.event-card__badges {
  margin-bottom: 0.8rem;
}

.event-card h3 {
  margin: 0;
  font-size: 1.2rem;
  line-height: 1.3;
}

.event-card__meta {
  margin: 0.45rem 0 0;
  color: #b6b6b6;
}

.event-card__chips {
  margin-top: 0.9rem;
}

.event-chip,
.event-badge {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
}

.event-badge--internal {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.event-badge--external {
  background: rgba(73, 149, 255, 0.16);
  color: #d9ecff;
}

.event-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: auto;
  padding-top: 1rem;
}

.event-card__source {
  color: #8e8e8e;
  font-size: 0.88rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

@media (max-width: 575.98px) {
  .event-card__footer {
    align-items: stretch;
  }

  .event-card__source,
  .event-card__footer .btn {
    width: 100%;
  }
}
</style>
