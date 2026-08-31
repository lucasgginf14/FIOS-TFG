<template>
  <RouterView v-slot="{ Component }">
    <component :is="layoutComponent">
      <component :is="Component" />
    </component>
  </RouterView>
</template>

<script setup>
import { computed } from "vue";
import { getStore } from "@/common/store";
import PublicLayout from "@/layout/PublicLayout.vue";
import AuthenticatedLayout from "@/layout/AuthenticatedLayout.vue";

const store = getStore();

const layoutComponent = computed(() =>
  store.state.user.logged ? AuthenticatedLayout : PublicLayout
);
</script>

<style>
:root {
  color-scheme: dark light;
}

html,
body,
#app {
  height: 100%;
  min-height: 100%;
  margin: 0;
  background: #050505;
  overscroll-behavior-y: none;
}

body {
  font-family:
    Inter,
    "Segoe UI",
    system-ui,
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
  color: #1f2937;
}

button {
  font: inherit;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-width: 0;
  text-align: center;
}

.btn-sm {
  gap: 0.35rem;
}

.btn:disabled,
button:disabled {
  cursor: not-allowed;
}

button:focus-visible,
a:focus-visible,
.btn:focus-visible,
.form-control:focus-visible {
  outline: 2px solid rgba(29, 185, 84, 0.85);
  outline-offset: 2px;
}

.clickable-card {
  color: inherit;
  text-decoration: none;
  cursor: pointer;
  transition:
    transform 180ms ease,
    border-color 180ms ease,
    box-shadow 180ms ease;
}

.clickable-card:hover,
.clickable-card:focus-visible {
  color: inherit;
  outline: none;
  transform: translateY(-3px);
  border-color: rgba(29, 185, 84, 0.5);
  box-shadow:
    0 30px 70px rgba(0, 0, 0, 0.34),
    0 0 0 1px rgba(29, 185, 84, 0.18);
}

.clickable-card:hover h2,
.clickable-card:hover h3,
.clickable-card:focus-visible h2,
.clickable-card:focus-visible h3 {
  color: #e9fff1;
}

.clickable-card__open-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid rgba(29, 185, 84, 0.28);
  background: rgba(29, 185, 84, 0.12);
  color: #ffffff;
  opacity: 0.72;
  transform: translateX(-4px);
  transition:
    opacity 180ms ease,
    transform 180ms ease,
    border-color 180ms ease,
    background 180ms ease;
}

.clickable-card:hover .clickable-card__open-indicator,
.clickable-card:focus-visible .clickable-card__open-indicator {
  opacity: 1;
  transform: translateX(0);
  border-color: rgba(29, 185, 84, 0.45);
  background: rgba(29, 185, 84, 0.18);
}

.clickable-row {
  cursor: pointer;
  transition:
    background 180ms ease,
    box-shadow 180ms ease;
}

.clickable-row:hover,
.clickable-row:focus-visible {
  outline: none;
  background: rgba(29, 185, 84, 0.07);
  box-shadow: inset 3px 0 0 rgba(29, 185, 84, 0.65);
}
</style>
