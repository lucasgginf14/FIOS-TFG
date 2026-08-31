<template>
  <section class="detail-section">
    <div class="detail-section__header">
      <div>
        <div class="detail-section__eyebrow">{{ eyebrow }}</div>
        <h2>{{ title }}</h2>
      </div>
    </div>

    <div class="info-grid">
      <article v-for="item in items" :key="item.label" class="info-grid__card">
        <div class="info-grid__icon"><i :class="item.icon"></i></div>
        <div>
          <div class="info-grid__label">{{ item.label }}</div>
          <div class="info-grid__value">{{ item.value }}</div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  items: { type: Array, default: () => [] }
});
</script>

<style scoped>
.detail-section {
  padding: 1.35rem;
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.detail-section__header {
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

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
}

.info-grid__card {
  display: flex;
  align-items: flex-start;
  gap: 0.9rem;
  min-height: 104px;
  padding: 0.95rem;
  border-radius: 18px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.055), rgba(255, 255, 255, 0.035));
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}

.info-grid__card:hover {
  border-color: rgba(29, 185, 84, 0.35);
  background: rgba(29, 185, 84, 0.08);
  transform: translateY(-1px);
}

.info-grid__icon {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
}

.info-grid__label {
  color: #9e9e9e;
  font-size: 0.82rem;
  margin-bottom: 0.35rem;
}

.info-grid__value {
  color: #ffffff;
  font-weight: 700;
  line-height: 1.45;
}

@media (max-width: 991.98px) {
  .info-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
