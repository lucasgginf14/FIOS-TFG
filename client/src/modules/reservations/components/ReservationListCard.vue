<template>
  <article
    class="reservation-card clickable-card"
    :class="[
      `reservation-card--${reservation.statusTone || 'muted'}`,
      { 'reservation-card--past': reservation.isPast && ['PENDING', 'ACCEPTED'].includes(reservation.state) }
    ]"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="reservation-card__media">
      <AppImage
        :src="reservation.image"
        :alt="reservation.name"
        :fallback-src="spacePlaceholder"
        :fallback-label="reservation.name"
        icon-class="bi bi-image"
      />
    </div>

    <div class="reservation-card__content">
      <div class="reservation-card__header">
        <div>
          <div class="reservation-card__title-row">
            <h3>{{ reservation.name }}</h3>
          </div>
          <p>{{ reservation.location || t("reservations.cards.noLocation") }}</p>
        </div>

        <div class="reservation-card__badges">
          <span class="status-badge" :class="`status-badge--${reservation.statusTone}`">
            {{ reservation.statusLabel }}
          </span>
          <span
            v-if="reservation.timingLabel"
            class="timing-badge"
            :class="`timing-badge--${reservation.timingTone || 'muted'}`"
          >
            {{ reservation.timingLabel }}
          </span>
        </div>
      </div>

      <div class="reservation-card__meta">
        <span><i class="bi bi-calendar3"></i>{{ reservation.dateLabel }}</span>
        <span><i class="bi bi-clock"></i>{{ reservation.timeRangeLabel }}</span>
        <span><i class="bi bi-people"></i>{{ reservation.attendeesLabel }}</span>
        <span><i class="bi bi-sliders"></i>{{ reservation.sessionTypeLabel }}</span>
        <span><i class="bi bi-cash-coin"></i>{{ reservation.priceLabel }}</span>
      </div>

      <div class="reservation-card__actions" @keydown.stop>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>
        <button
          v-if="reservation.canModify"
          type="button"
          class="btn btn-outline-light btn-sm"
          @click.stop="$emit('edit', reservation)"
        >
          <i class="bi bi-pencil-square"></i>
          {{ t("reservations.actions.modify") }}
        </button>
        <button
          v-if="reservation.canCancel"
          type="button"
          class="btn btn-outline-danger btn-sm"
          :disabled="busy"
          @click.stop="$emit('cancel', reservation)"
        >
          <i class="bi bi-x-circle"></i>
          {{ busy ? t("reservations.states.cancelling") : t("reservations.actions.cancel") }}
        </button>
        <button
          v-if="reservation.canAccept"
          type="button"
          class="btn btn-success btn-sm"
          :disabled="busy"
          @click.stop="$emit('state', reservation, 'ACCEPTED')"
        >
          <i class="bi bi-check2-circle"></i>
          {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.accept") }}
        </button>
        <button
          v-if="reservation.canReject"
          type="button"
          class="btn btn-outline-danger btn-sm"
          :disabled="busy"
          @click.stop="$emit('state', reservation, 'REJECTED')"
        >
          <i class="bi bi-slash-circle"></i>
          {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.reject") }}
        </button>
        <button
          v-if="reservation.canComplete"
          type="button"
          class="btn btn-success btn-sm"
          :disabled="busy"
          @click.stop="$emit('state', reservation, 'COMPLETED')"
        >
          <i class="bi bi-flag"></i>
          {{ busy ? t("reservations.states.updatingState") : t("reservations.actions.complete") }}
        </button>
        <button
          v-if="reservation.canContact"
          type="button"
          class="btn btn-outline-light btn-sm"
          @click.stop="$emit('contact', reservation)"
        >
          <i class="bi bi-chat-dots"></i>
          {{ t("reservations.actions.contact") }}
        </button>
        <button
          v-if="reservation.canRebook"
          type="button"
          class="btn btn-success btn-sm"
          @click.stop="$emit('rebook', reservation)"
        >
          <i class="bi bi-arrow-repeat"></i>
          {{ t("reservations.actions.rebook") }}
        </button>
        <button
          v-if="reservation.canShowReason"
          type="button"
          class="btn btn-outline-light btn-sm"
          @click.stop="$emit('reason', reservation)"
        >
          <i class="bi bi-info-circle"></i>
          {{ t("reservations.actions.viewReason") }}
        </button>
      </div>
    </div>
  </article>
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

const emit = defineEmits(["view", "edit", "cancel", "state", "contact", "rebook", "reason"]);

const { t } = useI18n();

const cardLabel = computed(() => `${t("reservations.actions.view")}: ${props.reservation.name}`);

function openDetail() {
  emit("view", props.reservation);
}
</script>

<style scoped>
.reservation-card {
  position: relative;
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 1rem;
  padding: 0.85rem;
  overflow: hidden;
  border-radius: 18px;
  background: #121414;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.18);
}

.reservation-card::before {
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  content: "";
  background: rgba(255, 255, 255, 0.22);
}

.reservation-card--success::before {
  background: #1db954;
}

.reservation-card--warning::before {
  background: #f4d06f;
}

.reservation-card--danger::before {
  background: #ff6b7d;
}

.reservation-card--muted::before {
  background: rgba(255, 255, 255, 0.28);
}

.reservation-card--past {
  border-color: rgba(244, 208, 111, 0.32);
  background:
    linear-gradient(90deg, rgba(244, 208, 111, 0.08), transparent 42%),
    #121414;
}

.reservation-card__media,
.reservation-card__placeholder {
  width: 100%;
  height: 122px;
  min-height: 0;
  border-radius: 14px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
}

.reservation-card__media :deep(.app-image),
.reservation-card__media :deep(.app-image__img),
.reservation-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.reservation-card__content {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.reservation-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.reservation-card__title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.65rem;
}

.reservation-card__title-row h3 {
  margin: 0;
  font-size: 1.08rem;
}

.reservation-card__header p {
  margin: 0.35rem 0 0;
  color: #b5b5b5;
}

.reservation-card__badges {
  display: flex;
  flex: 0 0 auto;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.45rem;
}

.reservation-card__meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(132px, 1fr));
  gap: 0.55rem 0.85rem;
  color: #d4d4d4;
}

.reservation-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.reservation-card__meta i {
  color: #1db954;
}

.reservation-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
}

.reservation-card__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  min-height: 34px;
  border-radius: 10px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.75rem;
  border-radius: 10px;
  font-size: 0.8rem;
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
  background: rgba(255, 255, 255, 0.1);
  color: #d5d5d5;
}

.status-badge--danger {
  background: rgba(220, 53, 69, 0.14);
  color: #ffbdc6;
}

.timing-badge {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.7rem;
  border-radius: 10px;
  font-size: 0.78rem;
  font-weight: 700;
}

.timing-badge--success {
  background: rgba(29, 185, 84, 0.12);
  color: #dfffe9;
}

.timing-badge--attention {
  background: rgba(244, 208, 111, 0.15);
  color: #f7dda0;
}

.timing-badge--muted {
  background: rgba(255, 255, 255, 0.09);
  color: #d7d7d7;
}

.timing-badge--danger {
  background: rgba(220, 53, 69, 0.15);
  color: #ffbdc6;
}

@media (max-width: 767.98px) {
  .reservation-card {
    grid-template-columns: 1fr;
  }

  .reservation-card__media,
  .reservation-card__placeholder {
    height: clamp(180px, 42vw, 240px);
  }

  .reservation-card__header {
    flex-direction: column;
  }

  .reservation-card__badges {
    justify-content: flex-start;
  }

  .reservation-card__meta {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .reservation-card__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 575.98px) {
  .reservation-card__meta {
    grid-template-columns: 1fr;
  }

  .reservation-card__actions .btn {
    width: 100%;
  }
}
</style>
