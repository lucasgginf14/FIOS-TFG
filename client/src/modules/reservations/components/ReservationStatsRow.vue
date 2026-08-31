<template>
  <div class="stats-row">
    <article
      v-for="stat in stats"
      :key="stat.id"
      class="stat-card"
      :class="`stat-card--${stat.tone || 'neutral'}`"
    >
      <div class="stat-card__icon">
        <i :class="stat.icon"></i>
      </div>
      <div class="stat-card__body">
        <strong>{{ stat.value }}</strong>
        <span>{{ stat.label }}</span>
      </div>
    </article>
  </div>
</template>

<script setup>
defineProps({
  stats: {
    type: Array,
    default: () => []
  }
});
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.75rem;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  min-height: 86px;
  padding: 0.85rem 0.95rem;
  border-radius: 16px;
  background: #121414;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.16);
}

.stat-card__icon {
  display: grid;
  place-items: center;
  width: 2.65rem;
  height: 2.65rem;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
  font-size: 1.1rem;
}

.stat-card__body {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.stat-card__body strong {
  font-size: 1.55rem;
  line-height: 1;
}

.stat-card__body span {
  color: #b4b4b4;
  font-size: 0.95rem;
}

.stat-card--success .stat-card__icon {
  background: rgba(29, 185, 84, 0.16);
  color: #1db954;
}

.stat-card--warning .stat-card__icon {
  background: rgba(255, 193, 7, 0.16);
  color: #f4d06f;
}

.stat-card--muted .stat-card__icon {
  background: rgba(255, 255, 255, 0.08);
  color: #d0d0d0;
}

.stat-card--danger .stat-card__icon {
  background: rgba(220, 53, 69, 0.16);
  color: #ff9cab;
}

@media (max-width: 991.98px) {
  .stats-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 575.98px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
