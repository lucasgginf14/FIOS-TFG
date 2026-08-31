<template>
  <div v-if="open" class="band-event-modal" @click.self="$emit('close')">
    <div class="band-event-modal__dialog" role="dialog" aria-modal="true">
      <header class="band-event-modal__header">
        <div>
          <span class="band-event-modal__eyebrow">{{ t("bands.modals.event.eyebrow") }}</span>
          <h2>{{ t("bands.modals.event.title") }}</h2>
        </div>

        <button
          type="button"
          class="band-event-modal__close"
          :aria-label="t('common.actions.close')"
          @click="$emit('close')"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </header>

      <div v-if="errorMessage" class="band-event-modal__error">
        {{ errorMessage }}
      </div>

      <form class="band-event-modal__form" @submit.prevent="$emit('submit')">
        <div class="band-event-modal__grid">
          <div class="event-field">
            <label>{{ t("bands.modals.event.band") }}</label>
            <AppSelect
              :model-value="modelValue.bandId"
              :options="bandSelectOptions"
              :label="t('bands.modals.event.band')"
              @update:model-value="updateField('bandId', $event)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.musicalSpace") }}</label>
            <AppSelect
              :model-value="modelValue.musicalSpaceId"
              :options="spaceSelectOptions"
              :label="t('bands.modals.event.musicalSpace')"
              @update:model-value="handleSpaceChange"
            />
          </div>

          <div class="event-field event-field--wide">
            <label>{{ t("bands.modals.event.eventTitle") }}</label>
            <input
              :value="modelValue.title"
              type="text"
              class="form-control"
              required
              @input="updateField('title', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.type") }}</label>
            <AppSelect
              :model-value="modelValue.eventType"
              :options="typeSelectOptions"
              :label="t('bands.modals.event.type')"
              @update:model-value="updateField('eventType', $event)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.date") }}</label>
            <input
              :value="modelValue.eventDate"
              type="date"
              class="form-control"
              :min="minimumEventDate"
              required
              @input="updateField('eventDate', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.startTime") }}</label>
            <input
              :value="modelValue.startTime"
              type="time"
              class="form-control"
              @input="updateField('startTime', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.endTime") }}</label>
            <input
              :value="modelValue.endTime"
              type="time"
              class="form-control"
              @input="updateField('endTime', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.venueName") }}</label>
            <input
              :value="modelValue.venueName"
              type="text"
              class="form-control"
              required
              @input="updateField('venueName', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.city") }}</label>
            <input
              :value="modelValue.city"
              type="text"
              class="form-control"
              required
              @input="updateField('city', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.province") }}</label>
            <input
              :value="modelValue.province"
              type="text"
              class="form-control"
              @input="updateField('province', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.country") }}</label>
            <input
              :value="modelValue.country"
              type="text"
              class="form-control"
              required
              @input="updateField('country', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.genre") }}</label>
            <input
              :value="modelValue.musicalGenre"
              type="text"
              class="form-control"
              @input="updateField('musicalGenre', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.capacity") }}</label>
            <input
              :value="modelValue.capacity"
              type="number"
              min="1"
              class="form-control"
              @input="updateField('capacity', $event.target.value)"
            />
          </div>

          <div class="event-field">
            <label>{{ t("bands.modals.event.ticketPrice") }}</label>
            <input
              :value="modelValue.ticketPrice"
              type="number"
              min="0"
              step="0.01"
              class="form-control"
              @input="updateField('ticketPrice', $event.target.value)"
            />
          </div>

          <div class="event-field event-field--wide">
            <label>{{ t("bands.modals.event.location") }}</label>
            <input
              :value="modelValue.location"
              type="text"
              class="form-control"
              @input="updateField('location', $event.target.value)"
            />
          </div>

          <div class="event-field event-field--wide">
            <ImageUploadField
              :model-value="modelValue.posterImage"
              :label="t('bands.modals.event.posterImage')"
              :alt="modelValue.title"
              :fallback-src="eventPlaceholder"
              :fallback-label="t('events.cards.imageFallback')"
              @update:model-value="updateField('posterImage', $event)"
            />
          </div>

          <div class="event-field event-field--wide">
            <label>{{ t("bands.modals.event.description") }}</label>
            <textarea
              :value="modelValue.description"
              rows="4"
              class="form-control"
              @input="updateField('description', $event.target.value)"
            ></textarea>
          </div>
        </div>

        <footer class="band-event-modal__footer">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("bands.actions.cancel") }}
          </button>
          <button type="submit" class="btn btn-success" :disabled="submitting">
            {{ submitting ? t("bands.states.publishingEvent") : t("bands.modals.event.submit") }}
          </button>
        </footer>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";
import ImageUploadField from "@/common/components/ImageUploadField.vue";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import { EVENT_TYPE_KEYS } from "@/modules/events/eventUtils";

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: Object, required: true },
  bands: { type: Array, default: () => [] },
  spaces: { type: Array, default: () => [] },
  submitting: { type: Boolean, default: false },
  errorMessage: { type: String, default: "" }
});

const emit = defineEmits(["close", "submit", "update:modelValue"]);
const { t } = useI18n();
const minimumEventDate = todayIsoDate();

const bandSelectOptions = computed(() => [
  { value: "", label: t("bands.modals.event.selectBand") },
  ...props.bands.map((band) => ({
    value: String(band.id),
    label: band.name
  }))
]);

const spaceSelectOptions = computed(() => [
  { value: "", label: t("bands.modals.event.noMusicalSpace") },
  ...props.spaces.map((space) => ({
    value: String(space.id),
    label: space.name,
    description: [space.city, space.province].filter(Boolean).join(", ")
  }))
]);

const typeSelectOptions = computed(() =>
  EVENT_TYPE_KEYS.map((type) => ({
    value: type,
    label: t(`events.types.${type}`)
  }))
);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function handleSpaceChange(value) {
  const selectedSpace = props.spaces.find((space) => String(space.id) === String(value));

  emit("update:modelValue", {
    ...props.modelValue,
    musicalSpaceId: value,
    ...(selectedSpace
      ? {
          venueName: selectedSpace.name || props.modelValue.venueName,
          city: selectedSpace.city || props.modelValue.city,
          province: selectedSpace.province || props.modelValue.province
        }
      : {})
  });
}

function todayIsoDate() {
  const today = new Date();
  const localDate = new Date(today.getTime() - today.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}
</script>

<style scoped>
.band-event-modal {
  position: fixed;
  inset: 0;
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(10px);
}

.band-event-modal__dialog {
  width: min(980px, 100%);
  max-height: 92vh;
  overflow: auto;
  padding: 1.35rem;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.12), transparent 34%),
    linear-gradient(180deg, #111111 0%, #181818 100%);
  color: #ffffff;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.38);
}

.band-event-modal__header,
.band-event-modal__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.band-event-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.band-event-modal__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
  letter-spacing: 0;
}

.band-event-modal__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.band-event-modal__error {
  margin-top: 1rem;
  padding: 0.85rem 1rem;
  border-radius: 16px;
  border: 1px solid rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb8c2;
}

.band-event-modal__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.event-field {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 0.38rem;
}

.event-field--wide {
  grid-column: span 2;
}

.event-field label {
  color: #c0c0c0;
  font-size: 0.82rem;
  font-weight: 600;
}

.event-field :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.event-field :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.event-field :deep(textarea.form-control) {
  min-height: 120px;
}

.band-event-modal__footer {
  justify-content: flex-end;
  margin-top: 1rem;
}

.band-event-modal__footer .btn {
  min-height: 44px;
  border-radius: 14px;
  font-weight: 700;
}

@media (max-width: 767.98px) {
  .band-event-modal {
    padding: 0.75rem;
  }

  .band-event-modal__header,
  .band-event-modal__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .band-event-modal__grid {
    grid-template-columns: 1fr;
  }

  .event-field--wide {
    grid-column: auto;
  }
}
</style>
