<template>
  <nav class="admin-tabs" :aria-label="label">
    <button
      v-for="section in sections"
      :key="section.id"
      type="button"
      class="admin-tabs__button"
      :class="{ 'is-active': modelValue === section.id }"
      @click="$emit('update:modelValue', section.id)"
    >
      <span>{{ section.label }}</span>
      <span v-if="section.count != null" class="admin-tabs__count">{{ section.count }}</span>
    </button>
  </nav>
</template>

<script setup>
defineProps({
  sections: {
    type: Array,
    default: () => []
  },
  modelValue: {
    type: String,
    required: true
  },
  label: {
    type: String,
    default: "Admin navigation"
  }
});

defineEmits(["update:modelValue"]);
</script>

<style scoped>
.admin-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
}

.admin-tabs__button {
  display: inline-flex;
  align-items: center;
  gap: 0.65rem;
  min-height: 42px;
  padding: 0 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
  color: #c9c9c9;
  font-size: 0.92rem;
  font-weight: 700;
  transition:
    transform 160ms ease,
    background-color 160ms ease,
    border-color 160ms ease,
    color 160ms ease;
}

.admin-tabs__button:hover {
  transform: translateY(-1px);
  border-color: rgba(29, 185, 84, 0.2);
  color: #ffffff;
}

.admin-tabs__button.is-active {
  background: #1db954;
  border-color: #1db954;
  color: #041106;
}

.admin-tabs__count {
  min-width: 1.8rem;
  padding: 0.05rem 0.45rem;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.16);
  font-size: 0.78rem;
  text-align: center;
}

.admin-tabs__button.is-active .admin-tabs__count {
  background: rgba(4, 17, 6, 0.14);
}
</style>
