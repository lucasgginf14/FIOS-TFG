<template>
  <div class="event-quick-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      type="button"
      class="event-quick-tabs__item"
      :class="{ 'is-active': modelValue === tab.id }"
      @click="$emit('update:modelValue', modelValue === tab.id ? '' : tab.id)"
    >
      <span>{{ tab.label }}</span>
      <strong>{{ tab.count }}</strong>
    </button>
  </div>
</template>

<script setup>
defineProps({
  tabs: {
    type: Array,
    default: () => []
  },
  modelValue: {
    type: String,
    default: ""
  }
});

defineEmits(["update:modelValue"]);
</script>

<style scoped>
.event-quick-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.event-quick-tabs__item {
  display: inline-flex;
  align-items: center;
  gap: 0.65rem;
  min-height: 42px;
  padding: 0 1rem;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  font-weight: 600;
}

.event-quick-tabs__item strong {
  color: #9f9f9f;
}

.event-quick-tabs__item.is-active {
  border-color: rgba(29, 185, 84, 0.24);
  background: rgba(29, 185, 84, 0.12);
  color: #dfffe9;
}

.event-quick-tabs__item.is-active strong {
  color: #dfffe9;
}
</style>
