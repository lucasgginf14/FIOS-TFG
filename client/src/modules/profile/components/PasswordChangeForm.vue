<template>
  <section class="profile-card">
    <div class="profile-card__header">
      <span class="profile-card__eyebrow">{{ t("profile.password.eyebrow") }}</span>
      <h2>{{ t("profile.password.title") }}</h2>
      <p>{{ t("profile.password.subtitle") }}</p>
    </div>

    <div v-if="errorMessage" class="profile-feedback profile-feedback--error">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="profile-feedback profile-feedback--success">
      {{ successMessage }}
    </div>

    <form class="profile-form" @submit.prevent="$emit('submit')">
      <div class="row g-3">
        <div class="col-12">
          <label class="form-label" for="current-password">{{ t("profile.password.currentPassword") }}</label>
          <input
            id="current-password"
            :value="modelValue.currentPassword"
            type="password"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.currentPassword }"
            autocomplete="current-password"
            @input="updateField('currentPassword', $event.target.value)"
          />
          <small v-if="fieldErrors.currentPassword" class="profile-field-error">
            {{ fieldErrors.currentPassword }}
          </small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="new-password">{{ t("profile.password.newPassword") }}</label>
          <input
            id="new-password"
            :value="modelValue.newPassword"
            type="password"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.newPassword }"
            autocomplete="new-password"
            @input="updateField('newPassword', $event.target.value)"
          />
          <small v-if="fieldErrors.newPassword" class="profile-field-error">
            {{ fieldErrors.newPassword }}
          </small>
        </div>

        <div class="col-md-6">
          <label class="form-label" for="confirm-password">{{ t("profile.password.confirmPassword") }}</label>
          <input
            id="confirm-password"
            :value="modelValue.confirmNewPassword"
            type="password"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.confirmNewPassword }"
            autocomplete="new-password"
            @input="updateField('confirmNewPassword', $event.target.value)"
          />
          <small v-if="fieldErrors.confirmNewPassword" class="profile-field-error">
            {{ fieldErrors.confirmNewPassword }}
          </small>
        </div>
      </div>

      <div class="profile-form__actions">
        <button type="button" class="btn btn-outline-light" @click="$emit('cancel')">
          {{ t("profile.actions.cancel") }}
        </button>
        <button type="submit" class="btn btn-success" :disabled="submitting">
          {{ submitting ? t("profile.password.saving") : t("profile.actions.updatePassword") }}
        </button>
      </div>
    </form>
  </section>
</template>

<script setup>
import { useI18n } from "vue-i18n";

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  submitting: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  },
  successMessage: {
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

.profile-feedback--success {
  color: #dfffe9;
  border: 1px solid rgba(29, 185, 84, 0.2);
  background: rgba(29, 185, 84, 0.08);
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
