<template>
  <span class="admin-status-badge" :class="badgeClass">
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  tone: {
    type: String,
    default: "neutral"
  }
});

const badgeClass = computed(() => `admin-status-badge--${props.tone}`);
</script>

<style scoped>
.admin-status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.35rem;
  min-height: 26px;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  border: 1px solid transparent;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  white-space: nowrap;
}

.admin-status-badge::before {
  content: "";
  width: 0.38rem;
  height: 0.38rem;
  border-radius: 999px;
  background: currentColor;
  opacity: 0.8;
}

.admin-status-badge--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.2);
  background: rgba(29, 185, 84, 0.14);
}

.admin-status-badge--warning {
  color: #fff2c5;
  border-color: rgba(255, 193, 7, 0.2);
  background: rgba(255, 193, 7, 0.14);
}

.admin-status-badge--danger {
  color: #ffd0d5;
  border-color: rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.14);
}

.admin-status-badge--info {
  color: #d7f7ff;
  border-color: rgba(13, 202, 240, 0.2);
  background: rgba(13, 202, 240, 0.14);
}

.admin-status-badge--neutral {
  color: #d8d8d8;
  border-color: rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}
</style>
