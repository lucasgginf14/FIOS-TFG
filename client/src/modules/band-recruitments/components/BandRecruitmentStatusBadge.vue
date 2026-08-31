<template>
  <span class="recruitment-status-badge" :class="badgeClass">
    {{ t(`recruitmentBoard.status.${status || "OPEN"}`) }}
  </span>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const props = defineProps({
  status: {
    type: String,
    default: "OPEN"
  }
});

const { t } = useI18n();

const badgeClass = computed(() =>
  props.status === "CLOSED"
    ? "recruitment-status-badge--closed"
    : "recruitment-status-badge--open"
);
</script>

<style scoped>
.recruitment-status-badge {
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

.recruitment-status-badge--open {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.2);
  background: rgba(29, 185, 84, 0.14);
}

.recruitment-status-badge--closed {
  color: #ffd0d5;
  border-color: rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.14);
}
</style>
