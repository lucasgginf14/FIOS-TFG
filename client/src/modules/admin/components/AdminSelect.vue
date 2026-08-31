<template>
  <div ref="rootEl" class="admin-select" :class="{ 'is-open': open }">
    <button
      type="button"
      class="admin-select__trigger"
      :aria-expanded="String(open)"
      :aria-label="label || selectedLabel"
      @click="toggle"
      @keydown.down.prevent="openMenu"
      @keydown.enter.prevent="toggle"
      @keydown.esc.prevent="close"
    >
      <span class="admin-select__value">{{ selectedLabel }}</span>
      <i class="bi bi-chevron-down admin-select__caret" aria-hidden="true"></i>
    </button>

    <Transition name="admin-select-menu">
      <div v-if="open" class="admin-select__menu" role="listbox">
        <button
          v-for="option in options"
          :key="String(option.value)"
          type="button"
          class="admin-select__option"
          :class="{ 'is-selected': isSelected(option) }"
          role="option"
          :aria-selected="String(isSelected(option))"
          @click="selectOption(option)"
        >
          <span class="admin-select__option-dot" aria-hidden="true"></span>
          <span class="admin-select__option-label">{{ option.label }}</span>
          <i v-if="isSelected(option)" class="bi bi-check2 admin-select__check" aria-hidden="true"></i>
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
  open.value = !open.value;
}

function openMenu() {
  open.value = true;
}

function close() {
  open.value = false;
}

function selectOption(option) {
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
.admin-select {
  position: relative;
  min-width: 0;
  width: 100%;
}

.admin-select__trigger {
  width: 100%;
  min-height: 50px;
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.045), rgba(255, 255, 255, 0.015)),
    rgba(7, 8, 8, 0.78);
  color: #ffffff;
  font-weight: 800;
  text-align: left;
  box-shadow: none;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    box-shadow 0.18s ease;
}

.admin-select__trigger:hover,
.admin-select.is-open .admin-select__trigger {
  border-color: rgba(29, 185, 84, 0.62);
  background-color: rgba(12, 14, 13, 0.96);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.13);
}

.admin-select__trigger:focus-visible {
  outline: 2px solid rgba(29, 185, 84, 0.88);
  outline-offset: 2px;
}

.admin-select__value {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.admin-select__caret {
  flex: 0 0 auto;
  color: #87908c;
  font-size: 0.9rem;
  transition:
    color 0.18s ease,
    transform 0.18s ease;
}

.admin-select.is-open .admin-select__caret {
  color: #1ed760;
  transform: rotate(180deg);
}

.admin-select__menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 0;
  right: 0;
  z-index: 60;
  max-height: 290px;
  overflow-y: auto;
  padding: 0.45rem;
  border: 1px solid rgba(29, 185, 84, 0.22);
  border-radius: 18px;
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.16), transparent 34%),
    linear-gradient(180deg, rgba(18, 20, 19, 0.99), rgba(8, 9, 9, 0.99));
  box-shadow:
    0 24px 52px rgba(0, 0, 0, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(18px);
}

.admin-select__menu::-webkit-scrollbar {
  width: 8px;
}

.admin-select__menu::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.45);
}

.admin-select__option {
  width: 100%;
  min-height: 42px;
  display: flex;
  align-items: center;
  gap: 0.65rem;
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

.admin-select__option:hover,
.admin-select__option.is-selected {
  color: #ffffff;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(255, 255, 255, 0.07);
}

.admin-select__option:hover {
  transform: translateX(2px);
}

.admin-select__option-dot {
  width: 0.48rem;
  height: 0.48rem;
  flex: 0 0 auto;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
}

.admin-select__option.is-selected .admin-select__option-dot {
  background: #1ed760;
  box-shadow: 0 0 0 4px rgba(29, 185, 84, 0.12);
}

.admin-select__option-label {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.admin-select__check {
  color: #1ed760;
  font-size: 1rem;
}

.admin-select-menu-enter-active,
.admin-select-menu-leave-active {
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
}

.admin-select-menu-enter-from,
.admin-select-menu-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
</style>
