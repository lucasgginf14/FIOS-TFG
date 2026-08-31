<template>
  <div class="result-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      class="result-tabs__item"
      :class="{ 'is-active': tab.id === activeTab }"
      type="button"
      @click="$emit('change', tab.id)"
    >
      <span>{{ tab.label }}</span>
      <span class="result-tabs__count">{{ tab.count }}</span>
    </button>
  </div>
</template>

<script setup>
defineProps({
  tabs: {
    type: Array,
    default: () => []
  },
  activeTab: {
    type: String,
    default: "all"
  }
});

defineEmits(["change"]);
</script>

<style scoped>
.result-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.result-tabs__item {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  min-height: 42px;
  padding: 0 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
  color: #d4d4d4;
  font-weight: 600;
  transition:
    color 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease;
}

.result-tabs__item:hover {
  color: #ffffff;
  border-color: rgba(29, 185, 84, 0.28);
}

.result-tabs__item.is-active {
  border-color: rgba(29, 185, 84, 0.34);
  background: rgba(29, 185, 84, 0.12);
  color: #ffffff;
}

.result-tabs__count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  min-height: 28px;
  padding: 0 0.45rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  font-size: 0.8rem;
}
</style>
