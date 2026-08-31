<template>
  <section class="band-empty-state">
    <div class="band-empty-state__icon">
      <i class="bi bi-people"></i>
    </div>
    <h2>{{ title }}</h2>
    <p>{{ text }}</p>
    <div class="band-empty-state__actions">
      <button v-if="createLabel" type="button" class="btn btn-success" @click="$emit('create')">
        {{ createLabel }}
      </button>
      <button type="button" class="btn btn-outline-light" @click="$emit('explore')">
        {{ exploreLabel }}
      </button>
    </div>
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, default: "" },
  text: { type: String, default: "" },
  createLabel: { type: String, default: "" },
  exploreLabel: { type: String, default: "" }
});

defineEmits(["create", "explore"]);
</script>

<style scoped>
.band-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.85rem;
  min-height: 320px;
  padding: 2rem 1.4rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.18);
  text-align: center;
}

.band-empty-state__icon {
  display: grid;
  place-items: center;
  width: 4rem;
  height: 4rem;
  border-radius: 24px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
  font-size: 1.65rem;
}

.band-empty-state h2 {
  margin: 0;
  font-size: 1.35rem;
}

.band-empty-state p {
  max-width: 480px;
  margin: 0;
  color: #b6b6b6;
  line-height: 1.7;
}

.band-empty-state__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
}
</style>
