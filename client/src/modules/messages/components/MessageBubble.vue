<template>
  <article class="message-bubble" :class="{ 'is-own': message.own }">
    <div class="message-bubble__meta">
      <span>{{ message.authorName }}</span>
      <span>{{ message.timeLabel }}</span>
    </div>
    <div class="message-bubble__body">
      {{ message.content }}
    </div>
  </article>
</template>

<script setup>
defineProps({
  message: {
    type: Object,
    required: true
  }
});
</script>

<style scoped>
.message-bubble {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  max-width: min(82%, 560px);
  min-width: 0;
}

.message-bubble.is-own {
  margin-left: auto;
}

.message-bubble__meta {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: #9e9e9e;
  font-size: 0.74rem;
}

.message-bubble.is-own .message-bubble__meta {
  justify-content: flex-end;
}

.message-bubble__body {
  padding: 0.9rem 1rem;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: #f5f5f5;
  line-height: 1.7;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.message-bubble.is-own .message-bubble__body {
  background: linear-gradient(135deg, rgba(29, 185, 84, 0.2), rgba(29, 185, 84, 0.12));
  border: 1px solid rgba(29, 185, 84, 0.2);
  color: #effff4;
}

@media (max-width: 575.98px) {
  .message-bubble {
    max-width: 92%;
  }

  .message-bubble__meta {
    flex-wrap: wrap;
    row-gap: 0.15rem;
  }
}
</style>
