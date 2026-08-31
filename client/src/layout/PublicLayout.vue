<template>
  <div class="layout-shell layout-shell--public">
    <AppNavbar />
    <main class="layout-main">
      <slot />
    </main>
  </div>
</template>

<script setup>
import AppNavbar from "./AppNavbar.vue";
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
}

.layout-shell--public {
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.08), transparent 24%),
    linear-gradient(180deg, #07110a 0%, #0d0d0d 72px, #f7f8fa 72px, #ffffff 100%);
}

.layout-main {
  min-height: calc(100vh - 82px);
}
</style>
