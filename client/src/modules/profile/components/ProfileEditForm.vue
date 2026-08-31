<template>
  <section class="profile-card">
    <div class="profile-card__header">
      <span class="profile-card__eyebrow">{{ t("profile.edit.eyebrow") }}</span>
      <h2>{{ t("profile.edit.title") }}</h2>
      <p>{{ t("profile.edit.subtitle") }}</p>
    </div>

    <div v-if="errorMessage" class="profile-feedback profile-feedback--error">
      {{ errorMessage }}
    </div>

    <form class="profile-form" @submit.prevent="$emit('submit')">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label" for="profile-name">{{ t("profile.fields.name") }}</label>
          <input
            id="profile-name"
            :value="modelValue.name"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.name }"
            @input="updateField('name', $event.target.value)"
          />
          <small v-if="fieldErrors.name" class="profile-field-error">{{ fieldErrors.name }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="profile-first-surname">{{ t("profile.fields.firstSurname") }}</label>
          <input
            id="profile-first-surname"
            :value="modelValue.firstSurname"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.firstSurname }"
            @input="updateField('firstSurname', $event.target.value)"
          />
          <small v-if="fieldErrors.firstSurname" class="profile-field-error">{{ fieldErrors.firstSurname }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="profile-second-surname">{{ t("profile.fields.secondSurname") }}</label>
          <input
            id="profile-second-surname"
            :value="modelValue.secondSurname"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.secondSurname }"
            @input="updateField('secondSurname', $event.target.value)"
          />
          <small v-if="fieldErrors.secondSurname" class="profile-field-error">{{ fieldErrors.secondSurname }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="profile-phone">{{ t("profile.fields.phone") }}</label>
          <input
            id="profile-phone"
            :value="modelValue.phone"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.phone }"
            @input="updateField('phone', $event.target.value)"
          />
          <small v-if="fieldErrors.phone" class="profile-field-error">{{ fieldErrors.phone }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="profile-birth-date">{{ t("profile.fields.birthDate") }}</label>
          <input
            id="profile-birth-date"
            :value="modelValue.birthDate"
            type="date"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.birthDate }"
            @input="updateField('birthDate', $event.target.value)"
          />
          <small v-if="fieldErrors.birthDate" class="profile-field-error">{{ fieldErrors.birthDate }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label">{{ t("profile.fields.instrument") }}</label>
          <AppSelect
            :model-value="modelValue.instrumentId"
            :options="instrumentSelectOptions"
            :label="t('profile.fields.instrument')"
            :disabled="submitting || instrumentsLoading || Boolean(instrumentCatalogError)"
            @update:model-value="updateField('instrumentId', $event)"
          />
          <small v-if="fieldErrors.instrumentId" class="profile-field-error">{{ fieldErrors.instrumentId }}</small>
          <small v-else-if="instrumentCatalogError" class="profile-field-error">{{ instrumentCatalogError }}</small>
          <small v-else class="profile-field-help">{{ t("profile.edit.instrumentHelp") }}</small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="profile-email">{{ t("profile.fields.email") }}</label>
          <input
            id="profile-email"
            :value="email"
            type="email"
            class="form-control"
            disabled
            readonly
          />
        </div>
      </div>

      <div class="profile-form__actions">
        <button type="button" class="btn btn-outline-light" @click="$emit('cancel')">
          {{ t("profile.actions.cancel") }}
        </button>
        <button type="submit" class="btn btn-success" :disabled="submitting">
          {{ submitting ? t("profile.edit.saving") : t("profile.actions.saveChanges") }}
        </button>
      </div>
    </form>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  instruments: {
    type: Array,
    default: () => []
  },
  instrumentsLoading: {
    type: Boolean,
    default: false
  },
  instrumentCatalogError: {
    type: String,
    default: ""
  },
  email: {
    type: String,
    default: ""
  },
  submitting: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  },
  fieldErrors: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(["update:modelValue", "submit", "cancel"]);
const { t } = useI18n();

const instrumentSelectOptions = computed(() => [
  {
    value: "",
    label: props.instrumentsLoading ? t("profile.edit.loadingInstruments") : t("profile.fields.noInstrument")
  },
  ...props.instruments.map((instrument) => ({
    value: String(instrument.id),
    label: instrument.name
  }))
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.profile-card {
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.profile-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-card__header h2 {
  margin: 0.35rem 0 0.45rem;
  font-size: 1.3rem;
}

.profile-card__header p {
  margin: 0;
  color: #b8b8b8;
}

.profile-feedback {
  margin-top: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 16px;
}

.profile-feedback--error {
  color: #ffb8c2;
  border: 1px solid rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.08);
}

.profile-form {
  margin-top: 1rem;
}

.profile-form :deep(.form-label) {
  color: #c3c3c3;
  font-size: 0.84rem;
  font-weight: 600;
}

.profile-form :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.profile-form :deep(.form-control:disabled) {
  opacity: 1;
  color: #a5a5a5;
  background: rgba(255, 255, 255, 0.03);
}

.profile-form :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.profile-form :deep(.form-control.is-invalid) {
  border-color: rgba(255, 99, 132, 0.78);
  box-shadow: none;
}

.profile-field-error {
  display: block;
  margin-top: 0.35rem;
  color: #ffb8c2;
  font-size: 0.8rem;
}

.profile-field-help {
  display: block;
  margin-top: 0.35rem;
  color: #969f99;
  font-size: 0.8rem;
}

.profile-form__actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1rem;
}

.profile-form__actions .btn {
  min-height: 44px;
  border-radius: 16px;
}

@media (max-width: 575.98px) {
  .profile-form__actions {
    flex-direction: column;
  }
}
</style>
