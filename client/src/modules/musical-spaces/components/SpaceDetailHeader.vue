<template>
  <header class="detail-header">
    <div class="detail-header__body">
      <div>
        <div class="detail-header__eyebrow">{{ eyebrow }}</div>
        <h1>{{ title }}</h1>
      </div>

      <div class="detail-header__meta">
        <span v-for="item in items" :key="item.label" class="detail-header__pill">
          <i v-if="item.icon" :class="item.icon"></i>
          {{ item.label }}
        </span>
      </div>
    </div>
  </header>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  items: { type: Array, default: () => [] }
});
</script>

<style scoped>
.detail-header {
  display: grid;
  gap: 1rem;
}

.detail-header__body {
  display: grid;
  justify-items: center;
  gap: 1rem;
  padding: 1.55rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.16), transparent 34%),
    linear-gradient(180deg, rgba(17, 17, 17, 0.98) 0%, rgba(24, 24, 24, 0.98) 100%);
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.24);
  text-align: center;
}

.detail-header__eyebrow {
  color: #1db954;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.detail-header h1 {
  max-width: 980px;
  margin: 0.55rem 0 0;
  color: #ffffff;
  font-size: 3rem;
  line-height: 1.1;
  letter-spacing: 0;
}

.detail-header__meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.65rem;
}

.detail-header__pill {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 36px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  font-size: 0.92rem;
  font-weight: 600;
}

.detail-header__pill i {
  color: #1db954;
}

@media (max-width: 767.98px) {
  .detail-header__body {
    padding: 1rem;
    border-radius: 22px;
  }

  .detail-header h1 {
    font-size: 2rem;
  }

  .detail-header__pill {
    width: 100%;
    justify-content: center;
  }
}
</style>
