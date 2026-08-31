<template>
  <div v-if="open" class="space-modal" @click.self="$emit('close')">
    <div class="space-modal__dialog" role="dialog" aria-modal="true">
      <div class="space-modal__header">
        <div>
          <span class="space-modal__eyebrow">
            {{ editing ? t("spaceList.form.editEyebrow") : t("spaceList.form.createEyebrow") }}
          </span>
          <h2>{{ editing ? t("spaceList.form.editTitle") : t("spaceList.form.createTitle") }}</h2>
          <p>{{ t("spaceList.form.intro") }}</p>
        </div>

        <button type="button" class="space-modal__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="!editing" class="space-modal__notice">
        {{ t("spaceList.form.pendingHint") }}
      </div>

      <div v-if="errorMessage" class="space-modal__error">
        {{ errorMessage }}
      </div>

      <form class="space-modal__grid" @submit.prevent="$emit('submit')">
        <div class="space-field space-field--wide">
          <label>{{ t("spaceList.form.name") }}</label>
          <input :value="modelValue.name" type="text" class="form-control" @input="updateField('name', $event.target.value)" />
        </div>

        <div class="space-field space-field--wide">
          <label>{{ t("spaceList.form.description") }}</label>
          <textarea :value="modelValue.description" rows="4" class="form-control" @input="updateField('description', $event.target.value)"></textarea>
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.spaceType") }}</label>
          <AppSelect
            :model-value="modelValue.spaceType"
            :options="spaceTypeSelectOptions"
            :label="t('spaceList.form.spaceType')"
            @update:model-value="updateField('spaceType', $event)"
          />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.capacity") }}</label>
          <input :value="modelValue.capacity" type="number" min="1" class="form-control" @input="updateField('capacity', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.squareMeters") }}</label>
          <input :value="modelValue.squareMeters" type="number" min="0.1" step="0.1" class="form-control" @input="updateField('squareMeters', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.soundproofed") }}</label>
          <AppSelect
            :model-value="modelValue.soundproofed"
            :options="soundproofedSelectOptions"
            :label="t('spaceList.form.soundproofed')"
            @update:model-value="updateField('soundproofed', $event)"
          />
        </div>

        <div class="space-field space-field--wide">
          <ImageUploadField
            :model-value="modelValue.mainImage"
            :label="t('spaceList.form.mainImage')"
            :alt="modelValue.name"
            :fallback-src="spacePlaceholder"
            :fallback-label="t('spaceList.card.imagePlaceholder')"
            @update:model-value="updateField('mainImage', $event)"
          />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.country") }}</label>
          <input :value="modelValue.country" type="text" class="form-control" @input="updateField('country', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.province") }}</label>
          <input :value="modelValue.province" type="text" class="form-control" @input="updateField('province', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.city") }}</label>
          <input :value="modelValue.city" type="text" class="form-control" @input="updateField('city', $event.target.value)" />
        </div>

        <div class="space-field space-field--wide">
          <label>{{ t("spaceList.form.street") }}</label>
          <input :value="modelValue.street" type="text" class="form-control" @input="updateField('street', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.portal") }}</label>
          <input :value="modelValue.portal" type="text" class="form-control" @input="updateField('portal', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.floor") }}</label>
          <input :value="modelValue.floor" type="text" class="form-control" @input="updateField('floor', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.postalCode") }}</label>
          <input :value="modelValue.postalCode" type="text" class="form-control" @input="updateField('postalCode', $event.target.value)" />
        </div>

        <div class="space-field space-field--wide">
          <label>{{ t("spaceList.form.coordinates") }}</label>
          <div class="space-coordinate-tools">
            <input
              :value="coordinatesInput"
              type="text"
              class="form-control"
              :placeholder="t('spaceList.form.coordinatesPlaceholder')"
              autocomplete="off"
              @input="updateCoordinatesInput($event.target.value)"
              @keydown.enter.prevent="applyCoordinates"
            />
            <button type="button" class="btn btn-outline-light" @click="applyCoordinates">
              <i class="bi bi-crosshair"></i>
              {{ t("spaceList.form.coordinatesAction") }}
            </button>
          </div>
          <small class="space-field__hint">{{ t("spaceList.form.coordinatesHint") }}</small>
          <small
            v-if="coordinatesMessage"
            class="space-field__feedback"
            :class="{ 'space-field__feedback--error': coordinatesMessageType === 'error' }"
          >
            {{ coordinatesMessage }}
          </small>
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.latitude") }}</label>
          <input :value="modelValue.latitude" type="number" step="0.000001" class="form-control" @input="updateField('latitude', $event.target.value)" />
        </div>

        <div class="space-field">
          <label>{{ t("spaceList.form.longitude") }}</label>
          <input :value="modelValue.longitude" type="number" step="0.000001" class="form-control" @input="updateField('longitude', $event.target.value)" />
        </div>

        <div class="space-modal__footer">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("spaceList.form.cancel") }}
          </button>
          <button type="submit" class="btn btn-success" :disabled="submitting">
            {{
              submitting
                ? editing
                  ? t("spaceList.form.saving")
                  : t("spaceList.form.creating")
                : editing
                  ? t("spaceList.form.save")
                  : t("spaceList.form.create")
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";
import ImageUploadField from "@/common/components/ImageUploadField.vue";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";
import { formatCoordinate, parseCoordinates } from "../coordinateParser";

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: Object, required: true },
  editing: { type: Boolean, default: false },
  submitting: { type: Boolean, default: false },
  errorMessage: { type: String, default: "" }
});

const emit = defineEmits(["close", "submit", "update:modelValue"]);
const { t } = useI18n();
const coordinatesInput = ref("");
const coordinatesMessage = ref("");
const coordinatesMessageType = ref("");

const typeOptions = [
  "REHEARSAL_ROOM",
  "RECORDING_STUDIO",
  "CONCERT_HALL",
  "CLASSROOM",
  "MULTIPURPOSE",
  "OTHER"
];

const spaceTypeSelectOptions = computed(() =>
  typeOptions.map((type) => ({
    value: type,
    label: t(`spaceDetail.spaceTypeLabels.${type}`)
  }))
);

const soundproofedSelectOptions = computed(() => [
  { value: true, label: t("spaceList.filters.soundproofedYes") },
  { value: false, label: t("spaceList.filters.soundproofedNo") }
]);

watch(
  () => props.open,
  (open) => {
    if (open) {
      resetCoordinatesInput();
    }
  }
);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function updateCoordinatesInput(value) {
  coordinatesInput.value = value;
  coordinatesMessage.value = "";
  coordinatesMessageType.value = "";
}

function resetCoordinatesInput() {
  coordinatesInput.value = "";
  coordinatesMessage.value = "";
  coordinatesMessageType.value = "";
}

function applyCoordinates() {
  const coordinates = parseCoordinates(coordinatesInput.value);

  if (!coordinates) {
    coordinatesMessage.value = t("spaceList.form.coordinatesError");
    coordinatesMessageType.value = "error";
    return;
  }

  const latitude = formatCoordinate(coordinates.latitude);
  const longitude = formatCoordinate(coordinates.longitude);

  coordinatesInput.value = `${latitude}, ${longitude}`;
  coordinatesMessage.value = t("spaceList.form.coordinatesApplied");
  coordinatesMessageType.value = "success";

  emit("update:modelValue", {
    ...props.modelValue,
    latitude,
    longitude
  });
}

</script>

<style scoped>
.space-modal {
  position: fixed;
  inset: 0;
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.74);
  backdrop-filter: blur(10px);
}

.space-modal__dialog {
  width: min(1040px, 100%);
  max-height: 92vh;
  overflow: auto;
  padding: 1.35rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.space-modal__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.space-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.space-modal__header h2 {
  margin: 0.35rem 0 0.45rem;
}

.space-modal__header p {
  margin: 0;
  color: #b8b8b8;
}

.space-modal__close {
  width: 42px;
  height: 42px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.space-modal__notice,
.space-modal__error {
  margin-top: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 16px;
}

.space-modal__notice {
  border: 1px solid rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
  color: #ddffea;
}

.space-modal__error {
  border: 1px solid rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb8c2;
}

.space-modal__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.space-field {
  display: flex;
  flex-direction: column;
  gap: 0.38rem;
}

.space-field--wide {
  grid-column: 1 / -1;
}

.space-field label {
  color: #c0c0c0;
  font-size: 0.82rem;
  font-weight: 600;
}

.space-field :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.space-field :deep(textarea.form-control) {
  min-height: 120px;
}

.space-field :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.space-coordinate-tools {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.65rem;
}

.space-coordinate-tools .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-height: 46px;
  border-radius: 16px;
  white-space: nowrap;
}

.space-field__hint,
.space-field__feedback {
  color: #a8a8a8;
  font-size: 0.78rem;
}

.space-field__feedback {
  color: #a8f0c2;
}

.space-field__feedback--error {
  color: #ffb8c2;
}

.space-modal__footer {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

@media (max-width: 991.98px) {
  .space-modal__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .space-modal {
    padding: 0.75rem;
  }

  .space-modal__header,
  .space-modal__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .space-modal__grid {
    grid-template-columns: 1fr;
  }

  .space-coordinate-tools {
    grid-template-columns: 1fr;
  }
}
</style>
