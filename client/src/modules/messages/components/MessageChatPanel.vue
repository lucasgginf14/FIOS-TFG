<template>
  <section
    class="chat-panel"
    :class="{
      'is-empty': !conversation,
      'has-empty-thread': conversation && !loading && !messages.length
    }"
  >
    <template v-if="conversation">
      <header class="chat-panel__header">
        <div class="chat-panel__header-main">
          <button type="button" class="chat-panel__conversation-toggle" @click="$emit('back')">
            <i class="bi bi-list-ul"></i>
            {{ backLabel }}
          </button>

          <div>
            <div class="chat-panel__title-row">
              <h2>{{ conversation.title }}</h2>
              <span class="status-badge">{{ conversation.reservationStateLabel }}</span>
            </div>
            <p>{{ conversation.participantName }}<span v-if="conversation.location"> · {{ conversation.location }}</span></p>
          </div>
        </div>

        <button
          type="button"
          class="btn btn-outline-light btn-sm chat-panel__reservation-action"
          @click="$emit('open-reservation', conversation)"
        >
          <i class="bi bi-calendar-check" aria-hidden="true"></i>
          <span>{{ reservationLabel }}</span>
        </button>
      </header>

      <div v-if="errorMessage" class="chat-panel__banner chat-panel__banner--error">
        {{ errorMessage }}
      </div>

      <div v-if="infoMessage" class="chat-panel__banner">
        {{ infoMessage }}
      </div>

      <div ref="messagesContainer" class="chat-panel__messages">
        <div v-if="loading" class="chat-panel__state">
          <div class="spinner-border text-success" role="status"></div>
        </div>

        <MessageEmptyState
          v-else-if="!messages.length"
          icon="bi bi-chat-heart"
          :title="emptyTitle"
          :text="emptyText"
        />

        <template v-else>
          <div v-for="group in groupedMessages" :key="group.dayLabel" class="chat-panel__day-group">
            <div class="chat-panel__day-label">
              <span>{{ group.dayLabel }}</span>
            </div>

            <div class="chat-panel__bubble-stack">
              <MessageBubble
                v-for="message in group.items"
                :key="message.id"
                :message="message"
              />
            </div>
          </div>
        </template>
      </div>

      <div class="chat-panel__composer">
        <div v-if="!conversation.canSend" class="chat-panel__locked">
          {{ lockedLabel }}
        </div>

        <MessageComposer
          v-model="draft"
          :placeholder="composerPlaceholder"
          :submit-label="sendLabel"
          :loading-label="sendingLabel"
          :sending="sending"
          :disabled="!conversation.canSend"
          @submit="$emit('send')"
        />
      </div>
    </template>

    <div v-else class="chat-panel__select-state">
      <div class="chat-panel__select-visual" aria-hidden="true">
        <div class="chat-panel__select-avatar">
          <i class="bi bi-chat-dots"></i>
        </div>
        <div class="chat-panel__select-lines">
          <span class="is-wide"></span>
          <span></span>
          <span class="is-short"></span>
        </div>
        <div class="chat-panel__select-bubbles">
          <span></span>
          <span></span>
        </div>
      </div>

      <MessageEmptyState
        class="chat-panel__select-copy"
        icon="bi bi-chat-square-dots"
        :title="placeholderTitle"
        :text="placeholderText"
      />
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, ref, watch } from "vue";
import MessageBubble from "./MessageBubble.vue";
import MessageComposer from "./MessageComposer.vue";
import MessageEmptyState from "./MessageEmptyState.vue";

const emit = defineEmits(["back", "open-reservation", "update:draft", "send"]);

const props = defineProps({
  conversation: {
    type: Object,
    default: null
  },
  messages: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  sending: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  },
  infoMessage: {
    type: String,
    default: ""
  },
  draft: {
    type: String,
    default: ""
  },
  backLabel: {
    type: String,
    default: ""
  },
  reservationLabel: {
    type: String,
    default: ""
  },
  emptyTitle: {
    type: String,
    default: ""
  },
  emptyText: {
    type: String,
    default: ""
  },
  placeholderTitle: {
    type: String,
    default: ""
  },
  placeholderText: {
    type: String,
    default: ""
  },
  composerPlaceholder: {
    type: String,
    default: ""
  },
  sendLabel: {
    type: String,
    default: ""
  },
  sendingLabel: {
    type: String,
    default: ""
  },
  lockedLabel: {
    type: String,
    default: ""
  }
});

const messagesContainer = ref(null);

const groupedMessages = computed(() => {
  const groups = [];

  props.messages.forEach((message) => {
    const existingGroup = groups[groups.length - 1];

    if (existingGroup && existingGroup.dayLabel === message.dayLabel) {
      existingGroup.items.push(message);
      return;
    }

    groups.push({
      dayLabel: message.dayLabel,
      items: [message]
    });
  });

  return groups;
});

const draft = computed({
  get: () => props.draft,
  set: (value) => emit("update:draft", value)
});

watch(
  () => [props.messages.length, props.loading, props.conversation?.reservationId],
  async () => {
    await nextTick();
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  }
);
</script>

<style scoped>
.chat-panel {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  min-height: 100%;
  width: 100%;
}

.chat-panel.is-empty {
  min-height: 0;
}

.chat-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.chat-panel__header-main {
  display: flex;
  align-items: flex-start;
  gap: 0.9rem;
}

.chat-panel__conversation-toggle {
  display: none;
  align-items: center;
  gap: 0.45rem;
  min-height: 38px;
  padding: 0 0.85rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.chat-panel__title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.65rem;
}

.chat-panel__title-row h2 {
  margin: 0;
  font-size: 1.3rem;
}

.chat-panel__header p {
  margin: 0.3rem 0 0;
  color: #a9a9a9;
}

.chat-panel__reservation-action {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  white-space: nowrap;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: #d8d8d8;
  font-size: 0.78rem;
  font-weight: 700;
}

.chat-panel__banner {
  margin-top: 0.85rem;
  padding: 0.85rem 0.95rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  color: #d7d7d7;
}

.chat-panel__banner--error {
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.chat-panel__messages {
  flex: 1;
  min-height: 300px;
  max-height: min(58vh, 560px);
  padding: 1rem 0.2rem 1rem 0;
  overflow: auto;
}

.chat-panel.has-empty-thread .chat-panel__messages {
  flex: 0 0 auto;
  min-height: 172px;
  max-height: none;
  padding: 0.95rem 0 1rem;
  overflow: visible;
}

.chat-panel.has-empty-thread .chat-panel__messages :deep(.message-empty) {
  min-height: 172px;
  padding: 1rem;
  border-radius: 20px;
  background:
    radial-gradient(circle at top, rgba(29, 185, 84, 0.12), transparent 42%),
    rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.07);
}

.chat-panel__state {
  display: grid;
  place-items: center;
  min-height: 180px;
}

.chat-panel__day-group + .chat-panel__day-group {
  margin-top: 1rem;
}

.chat-panel__day-label {
  display: flex;
  justify-content: center;
  margin-bottom: 0.85rem;
}

.chat-panel__day-label span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #bdbdbd;
  font-size: 0.78rem;
}

.chat-panel__bubble-stack {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.chat-panel__composer {
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.chat-panel__locked {
  margin-bottom: 0.75rem;
  padding: 0.8rem 0.9rem;
  border-radius: 16px;
  background: rgba(255, 193, 7, 0.08);
  color: #f4d06f;
}

.chat-panel__select-state {
  display: grid;
  place-items: center;
  align-content: center;
  gap: 1.25rem;
  min-height: clamp(340px, 46vh, 520px);
  padding: 2rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at 50% 0%, rgba(29, 185, 84, 0.14), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.035), rgba(255, 255, 255, 0.015));
}

.chat-panel__select-visual {
  position: relative;
  display: grid;
  grid-template-columns: 54px minmax(0, 1fr);
  gap: 0.85rem;
  width: min(100%, 360px);
  padding: 1.1rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(8, 10, 9, 0.78);
  box-shadow: 0 28px 70px rgba(0, 0, 0, 0.28);
}

.chat-panel__select-avatar {
  display: grid;
  place-items: center;
  width: 54px;
  height: 54px;
  border-radius: 18px;
  background: linear-gradient(145deg, rgba(29, 185, 84, 0.24), rgba(29, 185, 84, 0.1));
  color: #1db954;
  font-size: 1.25rem;
}

.chat-panel__select-lines {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.48rem;
}

.chat-panel__select-lines span,
.chat-panel__select-bubbles span {
  display: block;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
}

.chat-panel__select-lines span {
  width: 68%;
  height: 0.6rem;
}

.chat-panel__select-lines .is-wide {
  width: 92%;
  background: rgba(255, 255, 255, 0.16);
}

.chat-panel__select-lines .is-short {
  width: 46%;
}

.chat-panel__select-bubbles {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
  margin-top: 0.5rem;
}

.chat-panel__select-bubbles span {
  width: 58%;
  height: 1.9rem;
  background: rgba(255, 255, 255, 0.07);
}

.chat-panel__select-bubbles span:last-child {
  align-self: flex-end;
  width: 48%;
  background: rgba(29, 185, 84, 0.18);
}

.chat-panel__select-state :deep(.message-empty) {
  min-height: 0;
  padding: 0;
}

.chat-panel__select-state :deep(.message-empty__icon) {
  display: none;
}

@media (max-width: 991.98px) {
  .chat-panel__conversation-toggle {
    display: inline-flex;
  }

  .chat-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .chat-panel__reservation-action {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 575.98px) {
  .chat-panel__messages {
    min-height: 240px;
    max-height: none;
  }

  .chat-panel.has-empty-thread .chat-panel__messages {
    min-height: 150px;
  }

  .chat-panel.has-empty-thread .chat-panel__messages :deep(.message-empty) {
    min-height: 150px;
  }

  .chat-panel__select-state {
    min-height: 300px;
    padding: 1.15rem;
    border-radius: 20px;
  }

  .chat-panel__select-visual {
    display: none;
  }
}
</style>
