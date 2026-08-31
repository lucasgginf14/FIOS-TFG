<template>
  <div class="app-image">
    <img
      v-if="resolvedSrc"
      :src="resolvedSrc"
      :alt="alt"
      class="app-image__img"
      @error="handleError"
    />
    <div v-else class="app-image__placeholder">
      <i :class="iconClass"></i>
      <span v-if="fallbackLabel">{{ fallbackLabel }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";

const props = defineProps({
  src: {
    type: String,
    default: ""
  },
  alt: {
    type: String,
    default: ""
  },
  fallbackSrc: {
    type: String,
    default: ""
  },
  fallbackLabel: {
    type: String,
    default: ""
  },
  iconClass: {
    type: String,
    default: "bi bi-image"
  }
});

const primaryFailed = ref(false);
const fallbackFailed = ref(false);

const normalizedPrimarySrc = computed(() => normalizeSrc(props.src));
const normalizedFallbackSrc = computed(() => normalizeSrc(props.fallbackSrc));

const resolvedSrc = computed(() => {
  if (normalizedPrimarySrc.value && !primaryFailed.value) {
    return normalizedPrimarySrc.value;
  }

  if (normalizedFallbackSrc.value && !fallbackFailed.value) {
    return normalizedFallbackSrc.value;
  }

  return "";
});

watch(
  () => props.src,
  () => {
    primaryFailed.value = false;
    fallbackFailed.value = false;
  }
);

watch(
  () => props.fallbackSrc,
  () => {
    fallbackFailed.value = false;
  }
);

function handleError() {
  if (resolvedSrc.value === normalizedPrimarySrc.value && normalizedFallbackSrc.value) {
    primaryFailed.value = true;
    return;
  }

  fallbackFailed.value = true;
}

function normalizeSrc(value) {
  return typeof value === "string" ? value.trim() : "";
}
</script>

<style scoped>
.app-image {
  position: relative;
  display: block;
  width: 100%;
  height: 100%;
  min-height: inherit;
  overflow: hidden;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.18), transparent 30%),
    linear-gradient(180deg, #101010 0%, #171717 100%);
}

.app-image__img {
  position: absolute;
  inset: 0;
  display: block;
  width: 100%;
  height: 100%;
  min-height: 0;
  object-fit: cover;
}

.app-image__placeholder {
  position: absolute;
  inset: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
  height: 100%;
  min-height: 0;
  padding: 1rem;
  color: #d2d2d2;
  text-align: center;
}

.app-image__placeholder i {
  color: #1db954;
  font-size: 2rem;
}

.app-image__placeholder span {
  font-size: 0.84rem;
  line-height: 1.5;
}
</style>
