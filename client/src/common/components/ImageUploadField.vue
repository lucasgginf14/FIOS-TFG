<template>
  <div class="image-upload" :class="`image-upload--${variant}`">
    <label v-if="label" class="image-upload__label">{{ label }}</label>

    <div class="image-upload__layout">
      <div class="image-upload__preview">
        <AppImage
          :src="modelValue"
          :alt="alt"
          :fallback-src="fallbackSrc"
          :fallback-label="fallbackLabel"
          :icon-class="iconClass"
        />
      </div>

      <div class="image-upload__controls">
        <div class="image-upload__actions">
          <label class="image-upload__file-button" :class="{ 'is-disabled': disabled || uploading }">
            <i class="bi" :class="uploading ? 'bi-hourglass-split' : 'bi-upload'"></i>
            <span>{{ uploading ? t("common.imageUpload.uploading") : t("common.imageUpload.upload") }}</span>
            <input
              type="file"
              :accept="accept"
              :disabled="disabled || uploading"
              @change="handleFileChange"
            />
          </label>

          <button
            type="button"
            class="image-upload__clear"
            :title="t('common.imageUpload.clear')"
            :aria-label="t('common.imageUpload.clear')"
            :disabled="disabled || uploading || !modelValue"
            @click="clearImage"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <input
          v-if="showUrlInput"
          :value="modelValue"
          type="url"
          class="form-control image-upload__url"
          :placeholder="t('common.imageUpload.urlPlaceholder')"
          :disabled="disabled || uploading"
          @input="updateUrl($event.target.value)"
        />

        <small v-if="errorMessage" class="image-upload__error">{{ errorMessage }}</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { getApiErrorMessage } from "@/common/apiErrors";
import AppImage from "@/common/components/AppImage.vue";
import ImageRepository from "@/repositories/ImageRepository";

defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  label: {
    type: String,
    default: ""
  },
  alt: {
    type: String,
    default: ""
  },
  fallbackSrc: {
    type: String,
    default: ""
  },
  fallbackLabel: {
    type: String,
    default: ""
  },
  iconClass: {
    type: String,
    default: "bi bi-image"
  },
  accept: {
    type: String,
    default: "image/jpeg,image/png,image/gif,image/webp"
  },
  disabled: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String,
    default: "compact"
  },
  showUrlInput: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["update:modelValue", "uploaded"]);
const { t } = useI18n();
const uploading = ref(false);
const errorMessage = ref("");

async function handleFileChange(event) {
  const file = event.target.files?.[0];
  event.target.value = "";

  if (!file) {
    return;
  }

  uploading.value = true;
  errorMessage.value = "";

  try {
    const upload = await ImageRepository.upload(file);
    emit("update:modelValue", upload.url);
    emit("uploaded", upload);
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "common.imageUpload.error");
  } finally {
    uploading.value = false;
  }
}

function updateUrl(value) {
  errorMessage.value = "";
  emit("update:modelValue", value);
}

function clearImage() {
  errorMessage.value = "";
  emit("update:modelValue", "");
}
</script>

<style scoped>
.image-upload {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 0;
}

.image-upload__label {
  color: #c0c0c0;
  font-size: 0.82rem;
  font-weight: 600;
}

.image-upload__layout {
  display: grid;
  grid-template-columns: 144px minmax(0, 1fr);
  gap: 0.8rem;
  align-items: stretch;
}

.image-upload--large .image-upload__layout {
  grid-template-columns: 1fr;
}

.image-upload__preview {
  overflow: hidden;
  min-height: 112px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.image-upload--large .image-upload__preview {
  min-height: 240px;
}

.image-upload__preview :deep(.app-image),
.image-upload__preview :deep(.app-image__img),
.image-upload__preview :deep(.app-image__placeholder) {
  min-height: inherit;
}

.image-upload__controls {
  display: flex;
  min-width: 0;
  flex-direction: column;
  justify-content: center;
  gap: 0.65rem;
}

.image-upload__actions {
  display: flex;
  gap: 0.55rem;
}

.image-upload__file-button,
.image-upload__clear {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #ffffff;
  font-weight: 700;
}

.image-upload__file-button {
  flex: 1;
  gap: 0.45rem;
  padding: 0 0.9rem;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.06);
}

.image-upload__file-button input {
  display: none;
}

.image-upload__file-button.is-disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.image-upload__clear {
  flex: 0 0 42px;
  background: rgba(220, 53, 69, 0.08);
}

.image-upload__clear:disabled {
  opacity: 0.45;
}

.image-upload__url {
  min-width: 0;
}

.image-upload__error {
  color: #ffb8c2;
  font-size: 0.78rem;
}

@media (max-width: 575.98px) {
  .image-upload__layout {
    grid-template-columns: 1fr;
  }
}
</style>
