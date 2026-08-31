<template>
  <section class="empty-state">
    <div class="empty-state__icon">
      <i :class="icon"></i>
    </div>
    <h2>{{ title }}</h2>
    <p>{{ text }}</p>
    <button
      v-if="actionLabel"
      type="button"
      class="btn btn-success"
      @click="$emit('action')"
    >
      {{ actionLabel }}
    </button>
  </section>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: "bi bi-calendar2-heart"
  },
  title: {
    type: String,
    required: true
  },
  text: {
    type: String,
    required: true
  },
  actionLabel: {
    type: String,
    default: ""
  }
});

defineEmits(["action"]);
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.85rem;
  min-height: 320px;
  padding: 2rem 1.4rem;
  border-radius: 18px;
  background: #121414;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.16);
  text-align: center;
}

.empty-state__icon {
  display: grid;
  place-items: center;
  width: 4rem;
  height: 4rem;
  border-radius: 16px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
  font-size: 1.65rem;
}

.empty-state h2 {
  margin: 0;
  font-size: 1.35rem;
}

.empty-state p {
  max-width: 460px;
  margin: 0;
  color: #b6b6b6;
  line-height: 1.7;
}
</style>
