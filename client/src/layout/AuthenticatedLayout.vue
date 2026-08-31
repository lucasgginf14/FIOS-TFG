<template>
  <div class="layout-shell layout-shell--authenticated">
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

.layout-shell--authenticated {
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.1), transparent 28%),
    linear-gradient(180deg, #06120b 0%, #0b0b0b 72px, #f3f6f4 72px, #ffffff 100%);
}

.layout-shell--authenticated:has(.admin-page) {
  background: #050505;
}

.layout-main {
  min-height: calc(100vh - 82px);
}
</style>
