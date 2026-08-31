<template>
  <section
    class="upcoming-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="upcoming-card__lead">
      <span class="upcoming-card__eyebrow">{{ t("reservations.upcoming.eyebrow") }}</span>
      <h2>{{ relativeLabel }}</h2>
      <p>{{ reservation.statusLabel }}</p>
    </div>

    <div class="upcoming-card__content">
      <div class="upcoming-card__media">
        <AppImage
          :src="reservation.image"
          :alt="reservation.name"
          :fallback-src="spacePlaceholder"
          :fallback-label="reservation.name"
          icon-class="bi bi-image"
        />
      </div>

      <div class="upcoming-card__info">
        <div class="upcoming-card__topline">
          <h3>{{ reservation.name }}</h3>
          <span class="status-badge" :class="`status-badge--${reservation.statusTone}`">
            {{ reservation.statusLabel }}
          </span>
        </div>

        <div class="upcoming-card__meta">
          <span><i class="bi bi-geo-alt"></i>{{ reservation.location || t("reservations.cards.noLocation") }}</span>
          <span><i class="bi bi-calendar3"></i>{{ reservation.dateLabel }}</span>
          <span><i class="bi bi-clock"></i>{{ reservation.timeRangeLabel }}</span>
          <span><i class="bi bi-sliders"></i>{{ reservation.sessionTypeLabel }}</span>
          <span><i class="bi bi-people"></i>{{ reservation.attendeesLabel }}</span>
          <span><i class="bi bi-cash-coin"></i>{{ reservation.priceLabel }}</span>
        </div>

        <div class="upcoming-card__actions" @keydown.stop>
          <span class="clickable-card__open-indicator" aria-hidden="true">
            <i class="bi bi-arrow-up-right"></i>
          </span>
          <button
            v-if="reservation.canAccept"
            type="button"
            class="btn btn-success"
            :disabled="busy"
            @click.stop="$emit('state', reservation, 'ACCEPTED')"
          >
            <i class="bi bi-check2-circle"></i>
            {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.accept") }}
          </button>
          <button
            v-if="reservation.canReject"
            type="button"
            class="btn btn-outline-danger"
            :disabled="busy"
            @click.stop="$emit('state', reservation, 'REJECTED')"
          >
            <i class="bi bi-slash-circle"></i>
            {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.reject") }}
          </button>
          <button
            v-if="reservation.canComplete"
            type="button"
            class="btn btn-success"
            :disabled="busy"
            @click.stop="$emit('state', reservation, 'COMPLETED')"
          >
            <i class="bi bi-flag"></i>
            {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.complete") }}
          </button>
          <button
            v-if="reservation.canContact"
            type="button"
            class="btn btn-outline-light"
            @click.stop="$emit('contact', reservation)"
          >
            <i class="bi bi-chat-dots"></i>
            {{ t("reservations.actions.contact") }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";

const props = defineProps({
  reservation: {
    type: Object,
    required: true
  },
  busy: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["view", "state", "contact"]);

const { t } = useI18n();

const cardLabel = computed(() => `${t("reservations.actions.view")}: ${props.reservation.name}`);

const relativeLabel = computed(() => {
  if (props.reservation.daysUntil == null) {
    return t("reservations.upcoming.fallback");
  }

  if (props.reservation.daysUntil <= 0) {
    return t("reservations.upcoming.today");
  }

  if (props.reservation.daysUntil === 1) {
    return t("reservations.upcoming.tomorrow");
  }

  return t("reservations.upcoming.inDays", { count: props.reservation.daysUntil });
});

function openDetail() {
  emit("view", props.reservation);
}
</script>

<style scoped>
.upcoming-card {
  padding: 1.05rem;
  border-radius: 18px;
  background:
    linear-gradient(90deg, rgba(29, 185, 84, 0.1), transparent 34%),
    #121414;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.18);
}

.upcoming-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.upcoming-card__lead h2 {
  margin: 0.3rem 0 0.25rem;
  font-size: clamp(1.25rem, 2vw, 1.7rem);
}

.upcoming-card__lead p {
  margin: 0;
  color: #b8b8b8;
}

.upcoming-card__content {
  display: grid;
  grid-template-columns: 164px minmax(0, 1fr);
  gap: 1rem;
  margin-top: 1rem;
}

.upcoming-card__media,
.upcoming-card__placeholder {
  width: 100%;
  height: 136px;
  min-height: 0;
  border-radius: 14px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
}

.upcoming-card__media :deep(.app-image),
.upcoming-card__media :deep(.app-image__img),
.upcoming-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.upcoming-card__info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 1rem;
}

.upcoming-card__topline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.upcoming-card__topline h3 {
  margin: 0;
  font-size: 1.2rem;
}

.upcoming-card__meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.65rem 0.9rem;
  color: #d4d4d4;
}

.upcoming-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-width: 0;
}

.upcoming-card__meta i {
  color: #1db954;
}

.upcoming-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.55rem;
}

.upcoming-card__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  min-height: 38px;
  border-radius: 10px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 0.8rem;
  border-radius: 10px;
  font-size: 0.82rem;
  font-weight: 700;
}

.status-badge--success {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.status-badge--warning {
  background: rgba(255, 193, 7, 0.14);
  color: #ffe9a7;
}

.status-badge--muted {
  background: rgba(255, 255, 255, 0.09);
  color: #d7d7d7;
}

.status-badge--danger {
  background: rgba(220, 53, 69, 0.14);
  color: #ffbdc6;
}

@media (max-width: 991.98px) {
  .upcoming-card__content {
    grid-template-columns: 1fr;
  }

  .upcoming-card__media,
  .upcoming-card__placeholder {
    height: clamp(180px, 42vw, 240px);
  }

  .upcoming-card__meta {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 575.98px) {
  .upcoming-card__topline {
    flex-direction: column;
  }

  .upcoming-card__actions {
    justify-content: stretch;
  }

  .upcoming-card__meta {
    grid-template-columns: 1fr;
  }

  .upcoming-card__actions .btn {
    width: 100%;
  }
}
</style>
