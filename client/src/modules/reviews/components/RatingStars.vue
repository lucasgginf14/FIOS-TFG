<template>
  <div
    class="rating-stars"
    :class="{
      'rating-stars--readonly': readonly,
      'rating-stars--compact': compact
    }"
    :aria-label="label"
  >
    <button
      v-for="star in 5"
      :key="star"
      type="button"
      class="rating-stars__star"
      :class="{ 'is-active': star <= normalizedValue }"
      :disabled="readonly"
      :aria-label="`${label} ${star}`"
      @click="updateValue(star)"
    >
      <i :class="star <= normalizedValue ? 'bi bi-star-fill' : 'bi bi-star'"></i>
    </button>

    <span v-if="showValue" class="rating-stars__value">{{ normalizedValue }}/5</span>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  modelValue: {
    type: Number,
    default: 0
  },
  readonly: {
    type: Boolean,
    default: false
  },
  compact: {
    type: Boolean,
    default: false
  },
  showValue: {
    type: Boolean,
    default: false
  },
  label: {
    type: String,
    default: "Rating"
  }
});

const emit = defineEmits(["update:modelValue"]);

const normalizedValue = computed(() => {
  const value = Number(props.modelValue || 0);
  return Number.isFinite(value) ? Math.max(0, Math.min(5, value)) : 0;
});

function updateValue(value) {
  if (props.readonly) {
    return;
  }

  emit("update:modelValue", value);
}
</script>

<style scoped>
.rating-stars {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  color: #1db954;
}

.rating-stars__star {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  padding: 0;
  border: none;
  background: transparent;
  color: inherit;
  font-size: 1rem;
  transition: transform 0.18s ease, color 0.18s ease, opacity 0.18s ease;
}

.rating-stars__star.is-active {
  color: #1db954;
}

.rating-stars__star:not(.is-active) {
  color: rgba(255, 255, 255, 0.24);
}

.rating-stars:not(.rating-stars--readonly) .rating-stars__star:hover {
  transform: translateY(-1px);
}

.rating-stars__star:disabled {
  cursor: default;
  opacity: 1;
}

.rating-stars--compact .rating-stars__star {
  width: 24px;
  height: 24px;
  font-size: 0.85rem;
}

.rating-stars__value {
  margin-left: 0.25rem;
  color: #ffffff;
  font-size: 0.88rem;
  font-weight: 700;
}
</style>
