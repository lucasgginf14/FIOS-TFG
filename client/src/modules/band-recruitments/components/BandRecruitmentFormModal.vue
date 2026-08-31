<template>
  <div v-if="open" class="modal-shell" @click.self="$emit('close')">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-card__header">
        <div>
          <span class="modal-card__eyebrow">
            {{ editing ? t("recruitmentBoard.form.editEyebrow") : t("recruitmentBoard.form.createEyebrow") }}
          </span>
          <h3>{{ editing ? t("recruitmentBoard.form.editTitle") : t("recruitmentBoard.form.createTitle") }}</h3>
          <p>{{ t("recruitmentBoard.form.intro") }}</p>
        </div>
        <button type="button" class="modal-card__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="errorMessage" class="modal-card__banner modal-card__banner--error">{{ errorMessage }}</div>

      <form class="modal-card__body" @submit.prevent="$emit('submit')">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <label class="form-label">{{ t("recruitmentBoard.form.band") }}</label>
            <AppSelect
              :model-value="modelValue.bandId"
              :options="bandSelectOptions"
              :label="t('recruitmentBoard.form.band')"
              @update:model-value="update('bandId', $event)"
            />
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">{{ t("recruitmentBoard.form.instrument") }}</label>
            <AppSelect
              :model-value="modelValue.instrumentId"
              :options="instrumentSelectOptions"
              :label="t('recruitmentBoard.form.instrument')"
              @update:model-value="update('instrumentId', $event)"
            />
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">{{ t("recruitmentBoard.form.title") }}</label>
            <input :value="modelValue.title" class="form-control" @input="update('title', $event.target.value)" />
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">{{ t("recruitmentBoard.form.roleWanted") }}</label>
            <input :value="modelValue.roleWanted" class="form-control" @input="update('roleWanted', $event.target.value)" />
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">{{ t("recruitmentBoard.form.levelRequired") }}</label>
            <AppSelect
              :model-value="modelValue.levelRequired"
              :options="levelSelectOptions"
              :label="t('recruitmentBoard.form.levelRequired')"
              @update:model-value="update('levelRequired', $event)"
            />
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">{{ t("recruitmentBoard.form.city") }}</label>
            <input :value="modelValue.city" class="form-control" @input="update('city', $event.target.value)" />
          </div>

          <div class="col-12 col-md-4">
            <label class="form-label">{{ t("recruitmentBoard.form.vacancies") }}</label>
            <input
              :value="modelValue.vacancies"
              type="number"
              min="1"
              class="form-control"
              @input="update('vacancies', $event.target.value)"
            />
          </div>

          <div class="col-12">
            <label class="form-label">{{ t("recruitmentBoard.form.description") }}</label>
            <textarea :value="modelValue.description" rows="4" class="form-control" @input="update('description', $event.target.value)"></textarea>
          </div>
        </div>

        <div class="modal-card__footer">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("recruitmentBoard.actions.cancel") }}
          </button>
          <button type="submit" class="btn btn-success" :disabled="submitting">
            {{
              submitting
                ? editing
                  ? t("recruitmentBoard.form.saving")
                  : t("recruitmentBoard.form.creating")
                : editing
                  ? t("recruitmentBoard.form.save")
                  : t("recruitmentBoard.form.create")
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: Object, required: true },
  bands: { type: Array, default: () => [] },
  instruments: { type: Array, default: () => [] },
  editing: { type: Boolean, default: false },
  submitting: { type: Boolean, default: false },
  errorMessage: { type: String, default: "" }
});

const emit = defineEmits(["close", "submit", "update:modelValue"]);
const { t } = useI18n();

const levels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "PROFESSIONAL"];

const bandSelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.form.selectBand") },
  ...props.bands.map((band) => ({
    value: String(band.id),
    label: band.name
  }))
]);

const instrumentSelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.form.selectInstrument") },
  ...props.instruments.map((instrument) => ({
    value: String(instrument.id),
    label: instrument.name
  }))
]);

const levelSelectOptions = computed(() =>
  levels.map((level) => ({
    value: level,
    label: t(`bands.levels.${level}`)
  }))
);

function update(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.modal-shell {
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

.modal-card {
  width: min(100%, 860px);
  max-height: calc(100vh - 2rem);
  overflow: auto;
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.32);
  color: #ffffff;
}

.modal-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.modal-card__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.modal-card__header h3 {
  margin: 0.35rem 0 0.35rem;
  font-size: 1.35rem;
}

.modal-card__header p {
  margin: 0;
  color: #b8b8b8;
}

.modal-card__close {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.modal-card__banner {
  margin-bottom: 1rem;
  padding: 0.85rem 0.95rem;
  border-radius: 16px;
}

.modal-card__banner--error {
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.modal-card :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.modal-card :deep(textarea.form-control) {
  min-height: 120px;
}

.modal-card :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.modal-card__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.2rem;
}

@media (max-width: 767.98px) {
  .modal-card__header,
  .modal-card__footer {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
