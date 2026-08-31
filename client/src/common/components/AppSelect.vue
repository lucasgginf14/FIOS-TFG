<template>
  <div
    ref="rootEl"
    class="app-select"
    :class="{ 'is-open': open, 'is-disabled': disabled }"
  >
    <button
      type="button"
      class="app-select__trigger"
      :aria-expanded="String(open)"
      :aria-label="label || selectedLabel"
      :disabled="disabled"
      @click="toggle"
      @keydown.down.prevent="openMenu"
      @keydown.enter.prevent="toggle"
      @keydown.esc.prevent="close"
    >
      <span class="app-select__value">{{ selectedLabel }}</span>
      <i class="bi bi-chevron-down app-select__caret" aria-hidden="true"></i>
    </button>

    <Transition name="app-select-menu">
      <div v-if="open" class="app-select__menu" role="listbox">
        <button
          v-for="option in options"
          :key="String(option.value)"
          type="button"
          class="app-select__option"
          :class="{ 'is-selected': isSelected(option) }"
          :disabled="option.disabled"
          role="option"
          :aria-selected="String(isSelected(option))"
          @click="selectOption(option)"
        >
          <span class="app-select__option-dot" aria-hidden="true"></span>
          <span class="app-select__option-copy">
            <span class="app-select__option-label">{{ option.label }}</span>
            <span v-if="option.description" class="app-select__option-description">
              {{ option.description }}
            </span>
          </span>
          <i v-if="isSelected(option)" class="bi bi-check2 app-select__check" aria-hidden="true"></i>
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";

const props = defineProps({
  modelValue: {
    type: [String, Number, Boolean],
    default: ""
  },
  options: {
    type: Array,
    default: () => []
  },
  label: {
    type: String,
    default: ""
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["update:modelValue"]);

const rootEl = ref(null);
const open = ref(false);

const selectedOption = computed(() =>
  props.options.find((option) => normalize(option.value) === normalize(props.modelValue))
);

const selectedLabel = computed(() => selectedOption.value?.label || props.options[0]?.label || "");

function normalize(value) {
  return value == null ? "" : String(value);
}

function isSelected(option) {
  return normalize(option.value) === normalize(props.modelValue);
}

function toggle() {
  if (props.disabled) return;
  open.value = !open.value;
}

function openMenu() {
  if (props.disabled) return;
  open.value = true;
}

function close() {
  open.value = false;
}

function selectOption(option) {
  if (option.disabled) return;
  emit("update:modelValue", option.value);
  close();
}

function handleDocumentClick(event) {
  if (!rootEl.value?.contains(event.target)) {
    close();
  }
}

onMounted(() => {
  document.addEventListener("click", handleDocumentClick);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleDocumentClick);
});
</script>

<style scoped>
.app-select {
  position: relative;
  width: 100%;
  min-width: 0;
}

.app-select__trigger {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  width: 100%;
  min-height: 46px;
  padding: 0 0.95rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.045), rgba(255, 255, 255, 0.015)),
    rgba(7, 8, 8, 0.78);
  color: #ffffff;
  font-weight: 760;
  text-align: left;
  box-shadow: none;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    box-shadow 0.18s ease;
}

.app-select__trigger:hover,
.app-select.is-open .app-select__trigger {
  border-color: rgba(29, 185, 84, 0.56);
  background-color: rgba(12, 14, 13, 0.96);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.11);
}

.app-select__trigger:focus-visible {
  outline: 2px solid rgba(29, 185, 84, 0.88);
  outline-offset: 2px;
}

.app-select.is-disabled .app-select__trigger {
  cursor: not-allowed;
  opacity: 0.62;
}

.app-select__value {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-select__caret {
  flex: 0 0 auto;
  color: #87908c;
  font-size: 0.9rem;
  transition:
    color 0.18s ease,
    transform 0.18s ease;
}

.app-select.is-open .app-select__caret {
  color: #1ed760;
  transform: rotate(180deg);
}

.app-select__menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 0;
  right: 0;
  z-index: 90;
  max-height: 300px;
  overflow-y: auto;
  padding: 0.45rem;
  border: 1px solid rgba(29, 185, 84, 0.24);
  border-radius: 18px;
  background:
    linear-gradient(145deg, rgba(29, 185, 84, 0.12), rgba(29, 185, 84, 0) 44%),
    linear-gradient(180deg, rgba(18, 20, 19, 0.99), rgba(8, 9, 9, 0.99));
  box-shadow:
    0 24px 52px rgba(0, 0, 0, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(18px);
}

.app-select__menu::-webkit-scrollbar {
  width: 8px;
}

.app-select__menu::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.45);
}

.app-select__option {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  width: 100%;
  min-height: 42px;
  padding: 0.55rem 0.65rem;
  border: 1px solid transparent;
  border-radius: 14px;
  background: transparent;
  color: #d8dedb;
  font-weight: 700;
  text-align: left;
  transition:
    color 0.16s ease,
    border-color 0.16s ease,
    background-color 0.16s ease,
    transform 0.16s ease;
}

.app-select__option:hover,
.app-select__option.is-selected {
  color: #ffffff;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(255, 255, 255, 0.07);
}

.app-select__option:hover {
  transform: translateX(2px);
}

.app-select__option:disabled {
  cursor: not-allowed;
  opacity: 0.54;
  transform: none;
}

.app-select__option-dot {
  flex: 0 0 auto;
  width: 0.48rem;
  height: 0.48rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
}

.app-select__option.is-selected .app-select__option-dot {
  background: #1ed760;
  box-shadow: 0 0 0 4px rgba(29, 185, 84, 0.12);
}

.app-select__option-copy {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 0.16rem;
}

.app-select__option-label,
.app-select__option-description {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-select__option-description {
  color: #9da5a0;
  font-size: 0.78rem;
  font-weight: 600;
}

.app-select__check {
  color: #1ed760;
  font-size: 1rem;
}

.app-select-menu-enter-active,
.app-select-menu-leave-active {
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
}

.app-select-menu-enter-from,
.app-select-menu-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
</style>
