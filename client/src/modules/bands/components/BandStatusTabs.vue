<template>
  <div class="band-tabs">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      type="button"
      class="band-tabs__button"
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
  tabs: { type: Array, default: () => [] },
  activeTab: { type: String, default: "all" }
});

defineEmits(["change"]);
</script>

<style scoped>
.band-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.band-tabs__button {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  min-height: 42px;
  padding: 0 0.95rem;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #d0d0d0;
  font-weight: 600;
}

.band-tabs__button strong {
  color: #ffffff;
  font-size: 0.9rem;
}

.band-tabs__button.is-active {
  border-color: rgba(29, 185, 84, 0.22);
  background: rgba(29, 185, 84, 0.14);
  color: #ffffff;
}

@media (max-width: 575.98px) {
  .band-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 0.2rem;
  }
}
</style>
