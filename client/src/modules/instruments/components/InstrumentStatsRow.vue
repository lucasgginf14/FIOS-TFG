<template>
  <section class="instrument-stats">
    <article v-for="card in cards" :key="card.id" class="instrument-stat-card">
      <div class="instrument-stat-card__icon">
        <i :class="card.icon"></i>
      </div>
      <div>
        <div class="instrument-stat-card__value">{{ card.value }}</div>
        <div class="instrument-stat-card__label">{{ card.label }}</div>
      </div>
    </article>
  </section>
</template>

<script setup>
defineProps({
  cards: {
    type: Array,
    default: () => []
  }
});
</script>

<style scoped>
.instrument-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1rem;
}

.instrument-stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.15rem;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.instrument-stat-card__icon {
  width: 52px;
  height: 52px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 1.25rem;
}

.instrument-stat-card__value {
  font-size: 1.6rem;
  font-weight: 700;
  color: #ffffff;
}

.instrument-stat-card__label {
  color: #b8b8b8;
  font-size: 0.92rem;
}

@media (max-width: 991.98px) {
  .instrument-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 575.98px) {
  .instrument-stats {
    grid-template-columns: 1fr;
  }
}
</style>
