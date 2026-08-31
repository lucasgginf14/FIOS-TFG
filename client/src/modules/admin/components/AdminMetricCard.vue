<template>
  <article class="admin-metric-card" :class="toneClass">
    <div class="admin-metric-card__icon">
      <i :class="icon"></i>
    </div>

    <div class="admin-metric-card__content">
      <span class="admin-metric-card__label">{{ label }}</span>
      <strong class="admin-metric-card__value">
        <span
          v-if="loading"
          class="spinner-border spinner-border-sm text-success"
          role="status"
        ></span>
        <span v-else>{{ value ?? "--" }}</span>
      </strong>
      <span v-if="subtitle" class="admin-metric-card__subtitle">{{ subtitle }}</span>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    default: "--"
  },
  subtitle: {
    type: String,
    default: ""
  },
  icon: {
    type: String,
    default: "bi bi-bar-chart-line"
  },
  tone: {
    type: String,
    default: "neutral"
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const toneClass = computed(() => `admin-metric-card--${props.tone}`);
</script>

<style scoped>
.admin-metric-card {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  height: 100%;
  padding: 1.15rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.095);
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.06), transparent 36%),
    linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(11, 11, 11, 0.98) 100%);
  box-shadow:
    0 22px 54px rgba(0, 0, 0, 0.24),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  transition:
    border-color 0.18s ease,
    transform 0.18s ease,
    box-shadow 0.18s ease;
}

.admin-metric-card:hover {
  transform: translateY(-2px);
  border-color: rgba(29, 185, 84, 0.2);
  box-shadow:
    0 28px 70px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.admin-metric-card__icon {
  width: 52px;
  height: 52px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  font-size: 1.25rem;
  background: rgba(255, 255, 255, 0.065);
  color: #ffffff;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.04);
}

.admin-metric-card__content {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.3rem;
}

.admin-metric-card__label {
  color: #9f9f9f;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.admin-metric-card__value {
  min-height: 2.4rem;
  color: #ffffff;
  font-size: clamp(1.55rem, 2vw, 2.1rem);
  line-height: 1.1;
  letter-spacing: -0.03em;
}

.admin-metric-card__subtitle {
  color: #bdbdbd;
  font-size: 0.9rem;
}

.admin-metric-card--success .admin-metric-card__icon {
  background: rgba(29, 185, 84, 0.14);
  color: #72e0a0;
}

.admin-metric-card--warning .admin-metric-card__icon {
  background: rgba(255, 193, 7, 0.12);
  color: #ffd15a;
}

.admin-metric-card--info .admin-metric-card__icon {
  background: rgba(13, 202, 240, 0.12);
  color: #7fe3ff;
}

.admin-metric-card--danger .admin-metric-card__icon {
  background: rgba(220, 53, 69, 0.12);
  color: #ff9ba7;
}
</style>
