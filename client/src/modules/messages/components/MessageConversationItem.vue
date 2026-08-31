<template>
  <button
    type="button"
    class="conversation-item"
    :class="{ 'is-active': active, 'has-unread': conversation.unreadCount > 0 }"
    @click="$emit('select', conversation)"
  >
    <div class="conversation-item__media">
      <AppImage
        :src="conversation.image"
        :alt="conversation.title"
        :fallback-src="spacePlaceholder"
        :fallback-label="conversation.title"
        icon-class="bi bi-music-note-beamed"
      />
    </div>

    <div class="conversation-item__body">
      <div class="conversation-item__topline">
        <strong>{{ conversation.title }}</strong>
        <span class="conversation-item__date">{{ conversation.lastMessageLabel }}</span>
      </div>

      <div class="conversation-item__meta">
        <span>{{ conversation.participantName }}</span>
        <span class="status-badge">{{ conversation.reservationStateLabel }}</span>
      </div>

      <div v-if="conversation.contextLabel" class="conversation-item__context">
        <i class="bi bi-calendar2-week" aria-hidden="true"></i>
        <span>{{ conversation.contextLabel }}</span>
      </div>

      <div class="conversation-item__bottomline">
        <p>{{ conversation.snippet }}</p>
        <span v-if="conversation.unreadCount > 0" class="unread-badge">{{
          conversation.unreadCount
        }}</span>
        <span v-else class="conversation-item__open" aria-hidden="true">
          <i class="bi bi-chevron-right"></i>
        </span>
      </div>
    </div>
  </button>
</template>

<script setup>
import AppImage from "@/common/components/AppImage.vue";
import spacePlaceholder from "@/assets/placeholders/space-placeholder.svg";

defineProps({
  conversation: {
    type: Object,
    required: true
  },
  active: {
    type: Boolean,
    default: false
  }
});

defineEmits(["select"]);
</script>

<style scoped>
.conversation-item {
  position: relative;
  display: grid;
  grid-template-columns: 60px minmax(0, 1fr);
  gap: 0.85rem;
  width: 100%;
  padding: 0.95rem;
  border: 1px solid transparent;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.025);
  color: inherit;
  text-align: left;
  transition:
    background-color 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.conversation-item::before {
  content: "";
  position: absolute;
  top: 0.95rem;
  bottom: 0.95rem;
  left: 0;
  width: 3px;
  border-radius: 999px;
  background: transparent;
}

.conversation-item:hover,
.conversation-item.is-active {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.08);
  transform: translateY(-1px);
}

.conversation-item.is-active {
  background:
    linear-gradient(135deg, rgba(29, 185, 84, 0.14), rgba(255, 255, 255, 0.04));
  border-color: rgba(29, 185, 84, 0.34);
  box-shadow:
    inset 0 0 0 1px rgba(29, 185, 84, 0.08),
    0 18px 38px rgba(0, 0, 0, 0.18);
}

.conversation-item.has-unread::before,
.conversation-item.is-active::before {
  background: #1db954;
}

.conversation-item__media,
.conversation-item__placeholder {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.2);
}

.conversation-item__media :deep(.app-image) {
  width: 60px;
  height: 60px;
}

.conversation-item__body {
  min-width: 0;
}

.conversation-item__topline,
.conversation-item__bottomline,
.conversation-item__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.6rem;
}

.conversation-item__topline strong,
.conversation-item__bottomline p {
  min-width: 0;
}

.conversation-item__topline strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.98rem;
}

.conversation-item__date,
.conversation-item__meta span:first-child {
  color: #9e9e9e;
  font-size: 0.8rem;
}

.conversation-item__meta {
  margin-top: 0.3rem;
}

.conversation-item__meta span:first-child {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-item__bottomline {
  margin-top: 0.45rem;
}

.conversation-item__context {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  min-width: 0;
  margin-top: 0.42rem;
  color: #8f8f8f;
  font-size: 0.78rem;
}

.conversation-item__context i {
  flex: 0 0 auto;
  color: #1db954;
  font-size: 0.82rem;
}

.conversation-item__context span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conversation-item__bottomline p {
  margin: 0;
  color: #c3c3c3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge,
.unread-badge,
.conversation-item__open {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  padding: 0 0.6rem;
  border-radius: 999px;
  font-size: 0.74rem;
  font-weight: 700;
}

.status-badge {
  background: rgba(255, 255, 255, 0.08);
  color: #d6d6d6;
}

.unread-badge {
  min-width: 24px;
  background: #1db954;
  color: #041106;
}

.conversation-item__open {
  min-width: 24px;
  color: #9f9f9f;
  background: rgba(255, 255, 255, 0.05);
}

.conversation-item:hover .conversation-item__open,
.conversation-item.is-active .conversation-item__open {
  color: #1db954;
  background: rgba(29, 185, 84, 0.12);
}
</style>
