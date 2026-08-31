<template>
  <section class="event-empty">
    <i class="bi bi-calendar-x"></i>
    <strong>{{ title }}</strong>
    <p>{{ text }}</p>
    <button type="button" class="btn btn-success" @click="$emit('explore')">
      {{ actionLabel }}
    </button>
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, default: "" },
  text: { type: String, default: "" },
  actionLabel: { type: String, default: "" }
});

defineEmits(["explore"]);
</script>

<style scoped>
.event-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  padding: 2.5rem 1.5rem;
  text-align: center;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.2);
}

.event-empty i {
  color: #1db954;
  font-size: 2rem;
}

.event-empty p {
  max-width: 520px;
  margin: 0;
  color: #b8b8b8;
}
</style>
