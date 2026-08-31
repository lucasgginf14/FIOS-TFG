<template>
  <section class="conversation-list">
    <div class="conversation-list__top">
      <div class="conversation-list__heading">
        <span>{{ titleLabel }}</span>
        <strong>{{ summaryLabel }}</strong>
      </div>

      <div
        v-if="scopeTabs.length"
        class="conversation-list__scope-tabs"
        role="tablist"
        :aria-label="scopeLabel"
      >
        <button
          v-for="tab in scopeTabs"
          :key="tab.id"
          type="button"
          role="tab"
          :aria-selected="activeScope === tab.id"
          :class="{ 'is-active': activeScope === tab.id }"
          @click="$emit('update:scope', tab.id)"
        >
          <i v-if="tab.icon" :class="tab.icon" aria-hidden="true"></i>
          <span>{{ tab.label }}</span>
          <strong>{{ tab.count }}</strong>
        </button>
      </div>

      <div class="conversation-list__search">
        <i class="bi bi-search"></i>
        <input
          :value="search"
          type="search"
          class="form-control"
          :placeholder="searchPlaceholder"
          @input="$emit('update:search', $event.target.value)"
        />
      </div>

      <div
        v-if="statusTabs.length"
        class="conversation-list__status-tabs"
        role="tablist"
        :aria-label="statusLabel"
      >
        <button
          v-for="tab in statusTabs"
          :key="tab.id"
          type="button"
          role="tab"
          :aria-selected="activeStatus === tab.id"
          :class="{ 'is-active': activeStatus === tab.id }"
          @click="$emit('update:status', tab.id)"
        >
          <span>{{ tab.label }}</span>
          <strong>{{ tab.count }}</strong>
        </button>
      </div>
    </div>

    <div v-if="loading" class="conversation-list__state">
      <div class="spinner-border text-success" role="status"></div>
    </div>

    <MessageEmptyState
      v-else-if="!conversations.length"
      :title="emptyTitle"
      :text="emptyText"
    />

    <div v-else class="conversation-list__items">
      <MessageConversationItem
        v-for="conversation in conversations"
        :key="conversation.reservationId"
        :conversation="conversation"
        :active="activeReservationId === conversation.reservationId"
        @select="$emit('select', $event)"
      />
    </div>
  </section>
</template>

<script setup>
import MessageConversationItem from "./MessageConversationItem.vue";
import MessageEmptyState from "./MessageEmptyState.vue";

defineProps({
  conversations: {
    type: Array,
    default: () => []
  },
  activeReservationId: {
    type: Number,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  search: {
    type: String,
    default: ""
  },
  statusTabs: {
    type: Array,
    default: () => []
  },
  activeStatus: {
    type: String,
    default: "all"
  },
  scopeTabs: {
    type: Array,
    default: () => []
  },
  activeScope: {
    type: String,
    default: "all"
  },
  titleLabel: {
    type: String,
    default: ""
  },
  summaryLabel: {
    type: String,
    default: ""
  },
  searchPlaceholder: {
    type: String,
    default: ""
  },
  statusLabel: {
    type: String,
    default: ""
  },
  scopeLabel: {
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
  }
});

defineEmits(["select", "update:search", "update:status", "update:scope"]);
</script>

<style scoped>
.conversation-list {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  max-height: min(72vh, 760px);
}

.conversation-list__top {
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
  margin-bottom: 1rem;
}

.conversation-list__heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
  min-height: 42px;
}

.conversation-list__heading span {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.conversation-list__heading strong {
  color: #f3f3f3;
  font-size: 0.88rem;
  font-weight: 700;
  white-space: nowrap;
}

.conversation-list__search {
  position: relative;
}

.conversation-list__search i {
  position: absolute;
  left: 0.95rem;
  top: 50%;
  transform: translateY(-50%);
  color: #1db954;
}

.conversation-list__search :deep(.form-control) {
  min-height: 48px;
  padding-left: 2.6rem;
  border-radius: 18px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.conversation-list__search :deep(.form-control::placeholder) {
  color: #9f9f9f;
}

.conversation-list__search :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.conversation-list__scope-tabs,
.conversation-list__status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
}

.conversation-list__scope-tabs {
  display: flex;
  flex-wrap: wrap;
}

.conversation-list__scope-tabs button,
.conversation-list__status-tabs button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  flex: 1 1 108px;
  min-height: 34px;
  padding: 0 0.75rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.045);
  color: #c9c9c9;
  font-size: 0.78rem;
  font-weight: 700;
  white-space: nowrap;
}

.conversation-list__scope-tabs button {
  flex: 1 0 max-content;
  min-height: 38px;
  padding: 0 0.65rem;
}

.conversation-list__scope-tabs button span {
  overflow: visible;
  text-overflow: clip;
}

.conversation-list__scope-tabs i {
  flex: 0 0 auto;
  color: #1db954;
  font-size: 0.9rem;
}

.conversation-list__scope-tabs button.is-active i {
  color: #dfffe9;
}

.conversation-list__scope-tabs button:hover,
.conversation-list__scope-tabs button.is-active,
.conversation-list__status-tabs button:hover,
.conversation-list__status-tabs button.is-active {
  border-color: rgba(29, 185, 84, 0.34);
  background: rgba(29, 185, 84, 0.14);
  color: #f2fff6;
}

.conversation-list__scope-tabs strong,
.conversation-list__status-tabs strong {
  display: inline-grid;
  place-items: center;
  min-width: 22px;
  height: 22px;
  padding: 0 0.35rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
  font-size: 0.72rem;
}

.conversation-list__scope-tabs button.is-active strong,
.conversation-list__status-tabs button.is-active strong {
  background: rgba(29, 185, 84, 0.28);
  color: #dfffe9;
}

.conversation-list__items {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  gap: 0.5rem;
  min-height: 0;
  overflow: auto;
  padding-right: 0.15rem;
}

.conversation-list__state {
  display: grid;
  place-items: center;
  min-height: 220px;
}

@media (max-width: 767.98px) {
  .conversation-list {
    max-height: none;
  }
}
</style>
