<template>
  <div class="status-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      type="button"
      class="status-tabs__button"
      :class="{ 'is-active': activeTab === tab.id }"
      @click="$emit('change', tab.id)"
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
  activeTab: {
    type: String,
    default: "all"
  }
});

defineEmits(["change"]);
</script>

<style scoped>
.status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 0.35rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.035);
}

.status-tabs__button {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  min-height: 38px;
  padding: 0 0.85rem;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: transparent;
  color: #d0d0d0;
  font-weight: 600;
}

.status-tabs__button strong {
  color: #ffffff;
  font-size: 0.9rem;
}

.status-tabs__button.is-active {
  border-color: rgba(29, 185, 84, 0.22);
  background: rgba(29, 185, 84, 0.14);
  color: #ffffff;
}

@media (max-width: 575.98px) {
  .status-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 0.2rem;
  }
}
</style>
