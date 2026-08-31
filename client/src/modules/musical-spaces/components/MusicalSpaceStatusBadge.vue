<template>
  <span class="space-status-badge" :class="badgeClass">
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const props = defineProps({
  approvalStatus: {
    type: String,
    default: ""
  },
  active: {
    type: Boolean,
    default: true
  }
});

const { t } = useI18n();

const resolvedStatus = computed(() => {
  if (props.active === false) {
    return "INACTIVE";
  }

  return props.approvalStatus || "APPROVED";
});

const badgeClass = computed(() => {
  const mapping = {
    APPROVED: "space-status-badge--success",
    PENDING: "space-status-badge--warning",
    REJECTED: "space-status-badge--danger",
    INACTIVE: "space-status-badge--neutral"
  };

  return mapping[resolvedStatus.value] || "space-status-badge--neutral";
});

const label = computed(() => t(`spaceList.status.${resolvedStatus.value}`));
</script>

<style scoped>
.space-status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  padding: 0.25rem 0.7rem;
  border-radius: 999px;
  border: 1px solid transparent;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.03em;
  white-space: nowrap;
}

.space-status-badge--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.2);
  background: rgba(29, 185, 84, 0.14);
}

.space-status-badge--warning {
  color: #fff2c5;
  border-color: rgba(255, 193, 7, 0.2);
  background: rgba(255, 193, 7, 0.14);
}

.space-status-badge--danger {
  color: #ffd0d5;
  border-color: rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.14);
}

.space-status-badge--neutral {
  color: #d8d8d8;
  border-color: rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}
</style>
