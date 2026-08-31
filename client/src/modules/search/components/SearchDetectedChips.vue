<template>
  <div v-if="chips.length" class="detected-chips">
    <div class="detected-chips__list">
      <span v-for="chip in chips" :key="chip.key" class="detected-chip">
        {{ chip.label }}
      </span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  chips: {
    type: Array,
    default: () => []
  }
});
</script>

<style scoped>
.detected-chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.9rem;
  margin-top: 1.2rem;
}

.detected-chips__list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.7rem;
}

.detected-chip {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 0.95rem;
  border-radius: 999px;
  border: 1px solid rgba(29, 185, 84, 0.2);
  background: rgba(29, 185, 84, 0.08);
  color: #dfffe9;
  font-size: 0.88rem;
  font-weight: 600;
}

</style>
