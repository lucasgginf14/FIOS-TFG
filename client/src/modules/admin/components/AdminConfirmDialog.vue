<template>
  <div v-if="open" class="admin-confirm" @click.self="$emit('close')">
    <div class="admin-confirm__dialog" role="dialog" aria-modal="true">
      <div class="admin-confirm__header">
        <div>
          <span class="admin-confirm__eyebrow">{{ eyebrow }}</span>
          <h2>{{ title }}</h2>
        </div>

        <button type="button" class="admin-confirm__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <p class="admin-confirm__message">{{ message }}</p>

      <div v-if="requireText" class="admin-confirm__field">
        <label>{{ inputLabel }}</label>
        <textarea
          ref="inputRef"
          :value="modelValue"
          rows="4"
          class="form-control"
          :placeholder="inputPlaceholder"
          @input="$emit('update:modelValue', $event.target.value)"
        ></textarea>
      </div>

      <div v-if="errorMessage" class="admin-confirm__error">
        {{ errorMessage }}
      </div>

      <div class="admin-confirm__footer">
        <button
          type="button"
          class="btn btn-outline-light"
          :disabled="loading"
          @click="$emit('close')"
        >
          {{ cancelLabel }}
        </button>
        <button
          type="button"
          class="btn"
          :class="confirmClass"
          :disabled="loading"
          @click="$emit('confirm')"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
          {{ confirmLabel }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from "vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  eyebrow: {
    type: String,
    default: "Admin"
  },
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  confirmLabel: {
    type: String,
    required: true
  },
  cancelLabel: {
    type: String,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  variant: {
    type: String,
    default: "danger"
  },
  requireText: {
    type: Boolean,
    default: false
  },
  inputLabel: {
    type: String,
    default: ""
  },
  inputPlaceholder: {
    type: String,
    default: ""
  },
  modelValue: {
    type: String,
    default: ""
  },
  errorMessage: {
    type: String,
    default: ""
  }
});

defineEmits(["close", "confirm", "update:modelValue"]);

const inputRef = ref(null);

const confirmClass = computed(() => {
  if (props.variant === "success") {
    return "btn-success";
  }

  if (props.variant === "warning") {
    return "btn-warning";
  }

  return "btn-danger";
});

watch(
  () => props.open,
  async (isOpen) => {
    if (!isOpen || !props.requireText) {
      return;
    }

    await nextTick();
    inputRef.value?.focus();
  }
);
</script>

<style scoped>
.admin-confirm {
  position: fixed;
  inset: 0;
  z-index: 1090;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(10px);
}

.admin-confirm__dialog {
  width: min(560px, 100%);
  padding: 1.2rem;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, #101010 0%, #191919 100%);
  color: #ffffff;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.35);
}

.admin-confirm__header,
.admin-confirm__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.admin-confirm__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.admin-confirm__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.35rem;
}

.admin-confirm__close {
  width: 42px;
  height: 42px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.admin-confirm__message {
  margin: 1rem 0 0;
  color: #c6c6c6;
}

.admin-confirm__field {
  margin-top: 1rem;
}

.admin-confirm__field label {
  display: block;
  margin-bottom: 0.4rem;
  color: #d6d6d6;
  font-size: 0.84rem;
  font-weight: 700;
}

.admin-confirm__field :deep(.form-control) {
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.admin-confirm__error {
  margin-top: 0.9rem;
  padding: 0.8rem 0.9rem;
  border-radius: 16px;
  color: #ffb8c2;
  border: 1px solid rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.admin-confirm__footer {
  margin-top: 1.1rem;
}

@media (max-width: 575.98px) {
  .admin-confirm__header,
  .admin-confirm__footer {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
