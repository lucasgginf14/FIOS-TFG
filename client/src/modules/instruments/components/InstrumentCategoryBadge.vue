<template>
  <span class="instrument-category-badge" :class="`instrument-category-badge--${categoryClass}`">
    <i :class="iconClass"></i>
    {{ t(`instrumentBoard.categories.${category || "OTHER"}`) }}
  </span>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const props = defineProps({
  category: {
    type: String,
    default: "OTHER"
  }
});

const { t } = useI18n();

const categoryClass = computed(() => String(props.category || "OTHER").toLowerCase());

const iconClass = computed(() => {
  const icons = {
    STRINGS: "bi bi-music-note-beamed",
    WIND: "bi bi-wind",
    BRASS: "bi bi-soundwave",
    PERCUSSION: "bi bi-disc",
    KEYBOARD: "bi bi-sliders2",
    ELECTRONIC: "bi bi-sliders",
    VOICE: "bi bi-mic",
    OTHER: "bi bi-vinyl"
  };

  return icons[props.category] || icons.OTHER;
});
</script>

<style scoped>
.instrument-category-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 32px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.78rem;
  font-weight: 700;
}

.instrument-category-badge i {
  color: #1db954;
}

.instrument-category-badge--voice {
  background: rgba(29, 185, 84, 0.14);
}
</style>
