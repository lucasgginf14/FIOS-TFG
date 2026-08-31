<template>
  <section class="profile-card">
    <div class="profile-card__header">
      <span class="profile-card__eyebrow">{{ t("profile.image.eyebrow") }}</span>
      <h2>{{ t("profile.image.title") }}</h2>
      <p>{{ t("profile.image.subtitle") }}</p>
    </div>

    <div v-if="errorMessage" class="profile-feedback profile-feedback--error">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="profile-feedback profile-feedback--success">
      {{ successMessage }}
    </div>

    <form class="profile-form" @submit.prevent="$emit('submit')">
      <ImageUploadField
        :model-value="modelValue.profileImage"
        :label="t('profile.image.inputLabel')"
        :alt="t('profile.image.previewAlt')"
        :fallback-src="avatarPlaceholder"
        :fallback-label="t('profile.image.empty')"
        icon-class="bi bi-person-circle"
        variant="large"
        @update:model-value="updateField('profileImage', $event)"
      />

      <div class="profile-form__actions">
        <button type="button" class="btn btn-outline-danger" :disabled="removing || !currentImage" @click="$emit('remove')">
          {{ removing ? t("profile.image.removing") : t("profile.image.remove") }}
        </button>
        <button type="submit" class="btn btn-success" :disabled="submitting">
          {{ submitting ? t("profile.image.saving") : t("profile.image.save") }}
        </button>
      </div>
    </form>
  </section>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import ImageUploadField from "@/common/components/ImageUploadField.vue";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";

const props = defineProps({
  currentImage: {
    type: String,
    default: ""
  },
  modelValue: {
    type: Object,
    required: true
  },
  submitting: {
    type: Boolean,
    default: false
  },
  removing: {
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
  }
});

const emit = defineEmits(["update:modelValue", "submit", "remove"]);
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

.profile-form :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.profile-form :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
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
