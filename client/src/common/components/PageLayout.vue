<template>
  <section class="page-layout py-5" :class="`page-layout--${theme}`">
    <div class="container">
      <div class="page-header mb-4">
        <span v-if="eyebrow" class="text-uppercase text-secondary small fw-semibold">
          {{ eyebrow }}
        </span>
        <h1 class="display-6 fw-bold mt-2 mb-2">{{ title }}</h1>
        <p v-if="description" class="lead text-secondary mb-0">
          {{ description }}
        </p>
      </div>

      <slot />
    </div>
  </section>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ""
  },
  eyebrow: {
    type: String,
    default: ""
  },
  theme: {
    type: String,
    default: "light"
  }
});
</script>

<style scoped>
.page-layout {
  min-height: calc(100vh - 72px);
}

.page-layout--light {
  background:
    radial-gradient(circle at top left, rgba(13, 110, 253, 0.08), transparent 28%),
    linear-gradient(180deg, #f8fbff 0%, #ffffff 42%);
}

.page-layout--dark {
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.16), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.page-layout--dark .page-header .text-secondary,
.page-layout--dark .page-header .lead {
  color: #a9a9a9 !important;
}

.page-layout--dark .page-header h1 {
  color: #ffffff;
}

.page-header {
  max-width: 760px;
}
</style>
