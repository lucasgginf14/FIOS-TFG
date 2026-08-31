<template>
  <section class="hero-media">
    <div class="hero-media__image-shell">
      <AppImage
        :src="image"
        :alt="title"
        :fallback-src="spacePlaceholder"
        :fallback-label="placeholderLabel"
        icon-class="bi bi-image"
      />

      <div class="hero-media__shade"></div>
      <div v-if="title" class="hero-media__caption">
        <span>{{ title }}</span>
      </div>

      <button
        v-if="showFavorite"
        class="hero-media__favorite"
        type="button"
        :disabled="favoriteBusy"
        @click="$emit('toggle-favorite')"
      >
        <i :class="isFavorite ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
        {{ favoriteLabel }}
      </button>
    </div>
  </section>
</template>

<script setup>
import AppImage from "@/common/components/AppImage.vue";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";

defineProps({
  image: { type: String, default: "" },
  title: { type: String, default: "" },
  isFavorite: { type: Boolean, default: false },
  favoriteBusy: { type: Boolean, default: false },
  favoriteLabel: { type: String, default: "" },
  showFavorite: { type: Boolean, default: true },
  placeholderLabel: { type: String, default: "" }
});

defineEmits(["toggle-favorite"]);
</script>

<style scoped>
.hero-media__image-shell {
  position: relative;
  min-height: 360px;
  overflow: hidden;
  border-radius: 28px;
  background: #0d0d0d;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.hero-media__image-shell :deep(.app-image) {
  min-height: 360px;
}

.hero-media__image-shell :deep(img) {
  transition: transform 0.5s ease;
}

.hero-media__image-shell:hover :deep(img) {
  transform: scale(1.035);
}

.hero-media__shade {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(0, 0, 0, 0) 38%, rgba(0, 0, 0, 0.72) 100%);
}

.hero-media__caption {
  position: absolute;
  right: 1.15rem;
  bottom: 1.15rem;
  left: 1.15rem;
  display: flex;
  justify-content: flex-start;
  pointer-events: none;
}

.hero-media__caption span {
  max-width: min(620px, 100%);
  padding: 0.6rem 0.8rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 18px;
  background: rgba(17, 17, 17, 0.78);
  color: #ffffff;
  font-size: 1.25rem;
  font-weight: 700;
  line-height: 1.1;
}

.hero-media__favorite {
  position: absolute;
  top: 1.1rem;
  right: 1.1rem;
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  min-height: 44px;
  padding: 0 1rem;
  border: 1px solid rgba(29, 185, 84, 0.35);
  border-radius: 999px;
  background: rgba(17, 17, 17, 0.78);
  color: #ffffff;
  font-weight: 700;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}

.hero-media__favorite:hover:not(:disabled) {
  background: rgba(29, 185, 84, 0.18);
  transform: translateY(-1px);
}

.hero-media__favorite:disabled {
  cursor: wait;
  opacity: 0.72;
}

.hero-media__favorite i {
  color: #1db954;
}

@media (max-width: 767.98px) {
  .hero-media__image-shell :deep(.app-image),
  .hero-media__image-shell {
    min-height: 260px;
  }

  .hero-media__caption {
    right: 0.9rem;
    bottom: 0.9rem;
    left: 0.9rem;
  }

  .hero-media__caption span {
    font-size: 1rem;
  }

  .hero-media__favorite {
    right: 0.9rem;
    left: 0.9rem;
    justify-content: center;
  }
}
</style>
