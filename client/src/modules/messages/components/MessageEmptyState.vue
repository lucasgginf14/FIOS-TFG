<template>
  <section class="message-empty">
    <div class="message-empty__icon">
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
    default: "bi bi-chat-square-text"
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
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.85rem;
  min-height: 260px;
  padding: 1.5rem;
  text-align: center;
}

.message-empty__icon {
  display: grid;
  place-items: center;
  width: 4rem;
  height: 4rem;
  border-radius: 22px;
  background:
    linear-gradient(145deg, rgba(29, 185, 84, 0.18), rgba(29, 185, 84, 0.08));
  border: 1px solid rgba(29, 185, 84, 0.18);
  color: #1db954;
  font-size: 1.6rem;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.18);
}

.message-empty h2 {
  margin: 0;
  color: #f4f4f4;
  font-size: 1.26rem;
  font-weight: 800;
}

.message-empty p {
  max-width: 420px;
  margin: 0;
  color: #b5b5b5;
  line-height: 1.7;
}
</style>
