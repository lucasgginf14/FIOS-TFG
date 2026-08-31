<template>
  <section class="detail-section">
    <div class="detail-section__header">
      <div>
        <div class="detail-section__eyebrow">{{ eyebrow }}</div>
        <h2>{{ title }}</h2>
      </div>
      <span class="detail-section__count">{{ countLabel }}</span>
    </div>

    <div v-if="loading" class="empty-state">{{ loadingLabel }}</div>
    <div v-else-if="errorMessage" class="empty-state empty-state--error">{{ errorMessage }}</div>
    <div v-else-if="!equipment.length" class="empty-state">{{ emptyLabel }}</div>
    <div v-else class="equipment-grid">
      <article v-for="item in equipment" :key="item.id" class="equipment-grid__card">
        <div class="equipment-grid__header">
          <span class="equipment-grid__icon"><i :class="item.icon"></i></span>
          <span class="equipment-grid__state">{{ item.stateLabel }}</span>
        </div>
        <h3>{{ item.name }}</h3>
        <p>{{ item.quantityLabel }}</p>
        <small v-if="item.observations">{{ item.observations }}</small>
      </article>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  countLabel: { type: String, default: "" },
  loading: { type: Boolean, default: false },
  loadingLabel: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  equipment: { type: Array, default: () => [] }
});
</script>

<style scoped>
.detail-section {
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}
.detail-section__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}
.detail-section__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}
.detail-section h2 {
  margin: 0.35rem 0 0;
  color: #ffffff;
  font-size: 1.35rem;
  font-weight: 700;
}
.detail-section__count {
  min-height: 34px;
  padding: 0 0.8rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.08);
  color: #dfffe9;
  font-size: 0.86rem;
  line-height: 34px;
}
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}
.equipment-grid__card {
  padding: 0.85rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}
.equipment-grid__card:hover {
  border-color: rgba(29, 185, 84, 0.35);
  background: rgba(29, 185, 84, 0.08);
  transform: translateY(-1px);
}
.equipment-grid__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 0.9rem;
}
.equipment-grid__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 16px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
}
.equipment-grid__state {
  max-width: 140px;
  color: #9e9e9e;
  font-size: 0.82rem;
  font-weight: 800;
  text-align: right;
}
.equipment-grid__card h3 {
  margin: 0 0 0.35rem;
  color: #ffffff;
  font-size: 1.05rem;
  font-weight: 700;
}
.equipment-grid__card p,
.equipment-grid__card small {
  margin: 0;
  color: #bdbdbd;
}
.equipment-grid__card small {
  display: block;
  margin-top: 0.6rem;
  line-height: 1.45;
}
.empty-state {
  padding: 1.1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #d1d1d1;
}
.empty-state--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}
@media (max-width: 767.98px) {
  .detail-section__header {
    flex-direction: column;
  }

  .equipment-grid {
    grid-template-columns: 1fr;
  }
}
</style>
