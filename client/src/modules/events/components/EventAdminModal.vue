<template>
  <div v-if="open" class="event-modal" @click.self="$emit('close')">
    <div class="event-modal__dialog" role="dialog" aria-modal="true">
      <div class="event-modal__header">
        <div>
          <span class="event-modal__eyebrow">
            {{ editing ? t("events.admin.editEyebrow") : t("events.admin.createEyebrow") }}
          </span>
          <h2>{{ editing ? t("events.admin.editTitle") : t("events.admin.createTitle") }}</h2>
        </div>

        <button type="button" class="event-modal__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="errorMessage" class="event-modal__error">
        {{ errorMessage }}
      </div>

      <div class="event-modal__grid">
        <div class="event-field event-field--wide">
          <label>{{ t("events.admin.fields.title") }}</label>
          <input :value="modelValue.title" type="text" class="form-control" @input="updateField('title', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.type") }}</label>
          <AdminSelect
            :model-value="modelValue.eventType"
            :options="typeSelectOptions"
            :label="t('events.admin.fields.type')"
            @update:model-value="updateField('eventType', $event)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.status") }}</label>
          <AdminSelect
            :model-value="modelValue.status"
            :options="statusSelectOptions"
            :label="t('events.admin.fields.status')"
            @update:model-value="updateField('status', $event)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.date") }}</label>
          <input
            :value="modelValue.eventDate"
            type="date"
            class="form-control"
            :min="editing ? undefined : minimumEventDate"
            @input="updateField('eventDate', $event.target.value)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.startTime") }}</label>
          <input :value="modelValue.startTime" type="time" class="form-control" @input="updateField('startTime', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.endTime") }}</label>
          <input :value="modelValue.endTime" type="time" class="form-control" @input="updateField('endTime', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.city") }}</label>
          <input :value="modelValue.city" type="text" class="form-control" @input="updateField('city', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.province") }}</label>
          <input :value="modelValue.province" type="text" class="form-control" @input="updateField('province', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.country") }}</label>
          <input :value="modelValue.country" type="text" class="form-control" @input="updateField('country', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.venueName") }}</label>
          <input :value="modelValue.venueName" type="text" class="form-control" @input="updateField('venueName', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.genre") }}</label>
          <input :value="modelValue.musicalGenre" type="text" class="form-control" @input="updateField('musicalGenre', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.capacity") }}</label>
          <input :value="modelValue.capacity" type="number" min="1" class="form-control" @input="updateField('capacity', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.ticketPrice") }}</label>
          <input :value="modelValue.ticketPrice" type="number" min="0" step="0.01" class="form-control" @input="updateField('ticketPrice', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.latitude") }}</label>
          <input :value="modelValue.latitude" type="number" step="0.000001" class="form-control" @input="updateField('latitude', $event.target.value)" />
        </div>

        <div class="event-field">
          <label>{{ t("events.admin.fields.longitude") }}</label>
          <input :value="modelValue.longitude" type="number" step="0.000001" class="form-control" @input="updateField('longitude', $event.target.value)" />
        </div>

        <div class="event-field event-field--wide">
          <label>{{ t("events.admin.fields.location") }}</label>
          <input :value="modelValue.location" type="text" class="form-control" @input="updateField('location', $event.target.value)" />
        </div>

        <div class="event-field event-field--wide">
          <ImageUploadField
            :model-value="modelValue.posterImage"
            :label="t('events.admin.fields.posterImage')"
            :alt="modelValue.title"
            :fallback-src="eventPlaceholder"
            :fallback-label="t('events.cards.imageFallback')"
            @update:model-value="updateField('posterImage', $event)"
          />
        </div>

        <div class="event-field event-field--wide">
          <label>{{ t("events.admin.fields.externalUrl") }}</label>
          <input :value="modelValue.externalUrl" type="url" class="form-control" @input="updateField('externalUrl', $event.target.value)" />
        </div>

        <div class="event-field event-field--wide">
          <label>{{ t("events.admin.fields.description") }}</label>
          <textarea :value="modelValue.description" rows="4" class="form-control" @input="updateField('description', $event.target.value)"></textarea>
        </div>
      </div>

      <div class="event-modal__footer">
        <div class="event-modal__source">
          {{ t("events.admin.fields.source") }}:
          <strong>{{ t(`events.sources.${modelValue.source || "INTERNAL"}`) }}</strong>
        </div>

        <div class="event-modal__actions">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("events.actions.cancel") }}
          </button>
          <button type="button" class="btn btn-success" :disabled="submitting" @click="$emit('submit')">
            {{ submitting ? t("events.admin.saving") : editing ? t("events.actions.saveChanges") : t("events.actions.createEvent") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import ImageUploadField from "@/common/components/ImageUploadField.vue";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import AdminSelect from "@/modules/admin/components/AdminSelect.vue";
import { EVENT_STATUS_KEYS, EVENT_TYPE_KEYS } from "../eventUtils";

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: Object, required: true },
  editing: { type: Boolean, default: false },
  submitting: { type: Boolean, default: false },
  errorMessage: { type: String, default: "" }
});

const emit = defineEmits(["close", "submit", "update:modelValue"]);
const { t } = useI18n();
const typeOptions = EVENT_TYPE_KEYS;
const statusOptions = EVENT_STATUS_KEYS;
const minimumEventDate = todayIsoDate();
const typeSelectOptions = computed(() =>
  typeOptions.map((type) => ({
    value: type,
    label: t(`events.types.${type}`)
  }))
);
const statusSelectOptions = computed(() =>
  statusOptions.map((status) => ({
    value: status,
    label: t(`events.statuses.${status}`)
  }))
);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function todayIsoDate() {
  const today = new Date();
  const localDate = new Date(today.getTime() - today.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}
</script>

<style scoped>
.event-modal {
  position: fixed;
  inset: 0;
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
}

.event-modal__dialog {
  width: min(980px, 100%);
  max-height: 92vh;
  overflow: auto;
  padding: 1.35rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.event-modal__header,
.event-modal__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.event-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.event-modal__header h2 {
  margin: 0.35rem 0 0;
}

.event-modal__close {
  width: 42px;
  height: 42px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.event-modal__error {
  margin-top: 1rem;
  padding: 0.85rem 1rem;
  border-radius: 16px;
  border: 1px solid rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb8c2;
}

.event-modal__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.event-field {
  display: flex;
  flex-direction: column;
  gap: 0.38rem;
}

.event-field--wide {
  grid-column: 1 / -1;
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

.event-field :deep(textarea.form-control) {
  min-height: 120px;
}

.event-modal__footer {
  margin-top: 1rem;
}

.event-modal__source {
  color: #b9b9b9;
}

.event-modal__actions {
  display: flex;
  gap: 0.75rem;
}

@media (max-width: 767.98px) {
  .event-modal {
    padding: 0.75rem;
  }

  .event-modal__header,
  .event-modal__footer,
  .event-modal__actions {
    flex-direction: column;
    align-items: stretch;
  }

  .event-modal__grid {
    grid-template-columns: 1fr;
  }
}
</style>
