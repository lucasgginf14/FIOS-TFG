<template>
  <form class="composer" @submit.prevent="$emit('submit')">
    <textarea
      :value="modelValue"
      class="form-control"
      rows="1"
      :placeholder="placeholder"
      :disabled="disabled || sending"
      @input="$emit('update:modelValue', $event.target.value)"
      @keydown.enter.exact.prevent="$emit('submit')"
    />

    <button
      type="submit"
      class="btn btn-success composer__button"
      :disabled="disabled || sending || !modelValue.trim()"
    >
      <i
        class="bi"
        :class="sending ? 'bi-hourglass-split' : 'bi-send'"
        aria-hidden="true"
      ></i>
      <span>{{ sending ? loadingLabel : submitLabel }}</span>
    </button>
  </form>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  placeholder: {
    type: String,
    default: ""
  },
  submitLabel: {
    type: String,
    default: ""
  },
  loadingLabel: {
    type: String,
    default: ""
  },
  sending: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

defineEmits(["update:modelValue", "submit"]);
</script>

<style scoped>
.composer {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.7rem;
  align-items: end;
  padding: 0.6rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.035);
}

.composer :deep(.form-control) {
  min-height: 52px;
  max-height: 128px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
  resize: vertical;
}

.composer :deep(.form-control::placeholder) {
  color: #9c9c9c;
}

.composer :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.composer__button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-height: 48px;
  padding-inline: 1.1rem;
  border-radius: 16px;
  white-space: nowrap;
}

@media (max-width: 767.98px) {
  .composer {
    grid-template-columns: 1fr;
  }

  .composer__button {
    width: 100%;
  }
}
</style>
