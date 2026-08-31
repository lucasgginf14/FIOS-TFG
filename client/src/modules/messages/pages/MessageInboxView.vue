<template>
  <div class="messages-page">
    <section class="messages-shell">
      <div class="container py-5">
        <header class="messages-header">
          <div>
            <h1>{{ t("messages.header.title") }}</h1>
            <p>{{ t("messages.header.subtitle") }}</p>
          </div>

          <div class="messages-header__meta">
            <span class="messages-header__pill">
              {{ t("messages.header.unread", { count: totalUnreadMessages }) }}
            </span>
          </div>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
          {{ pageNotice.message }}
        </div>

        <div class="messages-layout">
          <aside
            class="messages-sidebar"
            :class="{ 'is-hidden-mobile': isMobile && selectedConversation }"
          >
            <MessageConversationList
              :conversations="visibleConversations"
              :active-reservation-id="selectedConversation?.reservationId ?? null"
              :loading="initialLoading"
              :search="searchText"
              :scope-tabs="conversationScopeTabs"
              :active-scope="activeScopeFilter"
              :status-tabs="conversationStatusTabs"
              :active-status="activeStatusFilter"
              :title-label="t('messages.list.title')"
              :summary-label="t('messages.list.summary', { count: visibleConversations.length })"
              :search-placeholder="t('messages.list.searchPlaceholder')"
              :scope-label="t('messages.filters.scope')"
              :status-label="t('messages.filters.status')"
              :empty-title="t('messages.list.emptyTitle')"
              :empty-text="t('messages.list.emptyText')"
              @update:search="searchText = $event"
              @update:scope="setScopeFilter"
              @update:status="setStatusFilter"
              @select="selectConversation($event)"
            />
          </aside>

          <main
            class="messages-chat"
            :class="{
              'has-conversation': selectedConversation,
              'is-hidden-mobile': isMobile && !selectedConversation && visibleConversations.length
            }"
          >
            <MessageChatPanel
              :conversation="selectedConversation"
              :messages="activeMessages"
              :loading="messagesLoading"
              :sending="sendingMessage"
              :error-message="chatErrorMessage"
              :info-message="chatInfoMessage"
              :draft="draftMessage"
              :back-label="t('messages.chat.back')"
              :reservation-label="t('messages.chat.viewReservation')"
              :empty-title="t('messages.chat.emptyTitle')"
              :empty-text="t('messages.chat.emptyText')"
              :placeholder-title="chatPlaceholderTitle"
              :placeholder-text="chatPlaceholderText"
              :composer-placeholder="t('messages.chat.composerPlaceholder')"
              :send-label="t('messages.chat.send')"
              :sending-label="t('messages.chat.sending')"
              :locked-label="t('messages.chat.locked')"
              @update:draft="draftMessage = $event"
              @send="sendMessage"
              @back="closeConversation"
              @open-reservation="openReservation"
            />
          </main>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import MessageRepository from "@/repositories/MessageRepository";
import ReservationSessionRepository from "@/repositories/ReservationSessionRepository";
import MessageChatPanel from "../components/MessageChatPanel.vue";
import MessageConversationList from "../components/MessageConversationList.vue";
import {
  appendSentMessagePreview,
  buildConversations,
  CONVERSATION_SCOPE_ALL,
  CONVERSATION_SCOPE_MANAGED,
  CONVERSATION_SCOPE_MINE,
  conversationMatchesScope,
  filterConversations,
  mapMessages,
  mergeConversationPreview,
  mergeReservationScopes,
  sortConversations
} from "../messageUtils";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { locale, t } = useI18n();

const initialLoading = ref(true);
const messagesLoading = ref(false);
const sendingMessage = ref(false);
const searchText = ref("");
const activeStatusFilter = ref("all");
const activeScopeFilter = ref(CONVERSATION_SCOPE_ALL);
const conversations = ref([]);
const selectedConversationId = ref(null);
const activeMessages = ref([]);
const draftMessage = ref("");
const chatErrorMessage = ref("");
const chatInfoMessage = ref("");
const pageNotice = ref(null);
const isMobile = ref(false);

let conversationsLoaded = false;
let activeConversationRequest = 0;
let syncingRouteSelection = false;

const currentUserId = computed(() => store.state.user.id);

const scopedConversations = computed(() =>
  conversations.value.filter((conversation) => conversationMatchesScope(conversation, activeScopeFilter.value))
);

const visibleConversations = computed(() =>
  filterConversations(
    scopedConversations.value.filter((conversation) =>
      conversationMatchesStatus(conversation, activeStatusFilter.value)
    ),
    searchText.value
  )
);

const conversationScopeTabs = computed(() => {
  const options = [
    {
      id: CONVERSATION_SCOPE_ALL,
      icon: "bi bi-inboxes",
      label: t("messages.filters.allConversations")
    },
    {
      id: CONVERSATION_SCOPE_MINE,
      icon: "bi bi-person-lines-fill",
      label: t("messages.filters.myReservations")
    },
    {
      id: CONVERSATION_SCOPE_MANAGED,
      icon: "bi bi-shop",
      label: t("messages.filters.managedReservations")
    }
  ];

  return options.map((option) => ({
    ...option,
    count: option.id === CONVERSATION_SCOPE_ALL
      ? conversations.value.length
      : conversations.value.filter((conversation) => conversationMatchesScope(conversation, option.id)).length
  }));
});

const conversationStatusTabs = computed(() => {
  const options = [
    { id: "all", label: t("messages.filters.allStatuses") },
    { id: "PENDING", label: t("reservations.statuses.PENDING") },
    { id: "ACCEPTED", label: t("reservations.statuses.ACCEPTED") },
    { id: "COMPLETED", label: t("reservations.statuses.COMPLETED") },
    { id: "CANCELLED", label: t("reservations.statuses.CANCELLED") },
    { id: "REJECTED", label: t("reservations.statuses.REJECTED") }
  ];

  return options.map((option) => ({
    ...option,
    count: option.id === "all"
      ? scopedConversations.value.length
      : scopedConversations.value.filter((conversation) => conversation.reservationState === option.id).length
  }));
});

const selectedConversation = computed(
  () => conversations.value.find((item) => item.reservationId === selectedConversationId.value) || null
);

const totalUnreadMessages = computed(() =>
  conversations.value.reduce((total, conversation) => total + Number(conversation.unreadCount || 0), 0)
);

const chatPlaceholderTitle = computed(() =>
  visibleConversations.value.length
    ? t("messages.chat.placeholderTitle")
    : t("messages.list.emptyTitle")
);

const chatPlaceholderText = computed(() =>
  visibleConversations.value.length
    ? t("messages.chat.placeholderText")
    : t("messages.list.emptyText")
);

onMounted(async () => {
  syncViewport();
  window.addEventListener("resize", syncViewport);
  await loadInbox();
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", syncViewport);
});

watch(
  () => route.query.reservationId,
  async () => {
    if (syncingRouteSelection) {
      return;
    }

    if (!conversationsLoaded) {
      return;
    }

    await syncSelectionFromRoute();
  }
);

watch(
  () => selectedConversationId.value,
  () => {
    chatErrorMessage.value = "";
    chatInfoMessage.value = "";
    draftMessage.value = "";
  }
);

async function loadInbox() {
  initialLoading.value = true;
  pageNotice.value = null;

  try {
    const [myReservations, managedReservations, unreadSummary] = await Promise.all([
      ReservationSessionRepository.getMine(),
      ReservationSessionRepository.getManagedSpacesReservations(),
      MessageRepository.getUnread()
    ]);

    const reservations = mergeReservationScopes(myReservations ?? [], managedReservations ?? []);
    conversations.value = buildConversations(reservations ?? [], unreadSummary ?? {}, locale.value, t);
    notifyUnreadRefresh();
    conversationsLoaded = true;
    await syncSelectionFromRoute();
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "messages.states.error")
    };
  } finally {
    initialLoading.value = false;
  }
}

async function syncSelectionFromRoute() {
  const reservationId = Number(route.query.reservationId);

  if (!reservationId) {
    return;
  }

  const targetConversation = conversations.value.find(
    (conversation) => conversation.reservationId === reservationId
  );

  if (!targetConversation) {
    pageNotice.value = {
      type: "warning",
      message: t("messages.states.reservationUnavailable")
    };
    selectedConversationId.value = null;
    activeMessages.value = [];
    await router.replace({ name: "MessageInbox" });
    return;
  }

  if (selectedConversationId.value === reservationId && targetConversation.hasLoadedMessages) {
    return;
  }

  revealConversationInFilters(targetConversation);
  await openConversation(targetConversation, false);
}

async function selectConversation(conversation) {
  await openConversation(conversation, true);
}

async function setStatusFilter(status) {
  activeStatusFilter.value = status || "all";

  if (
    selectedConversation.value &&
    !conversationMatchesStatus(selectedConversation.value, activeStatusFilter.value)
  ) {
    await closeConversation();
  }
}

async function setScopeFilter(scope) {
  activeScopeFilter.value = scope || CONVERSATION_SCOPE_ALL;

  if (
    selectedConversation.value &&
    !conversationMatchesScope(selectedConversation.value, activeScopeFilter.value)
  ) {
    await closeConversation();
  }
}

async function openConversation(conversation, syncRoute = true) {
  if (!conversation) {
    return;
  }

  revealConversationInFilters(conversation);

  const reservationId = conversation.reservationId;

  if (syncRoute && String(route.query.reservationId) !== String(reservationId)) {
    syncingRouteSelection = true;
    selectedConversationId.value = reservationId;
    await router.replace({
      name: "MessageInbox",
      query: { reservationId: String(reservationId) }
    });
    syncingRouteSelection = false;
  }

  selectedConversationId.value = reservationId;
  chatErrorMessage.value = "";
  chatInfoMessage.value = "";
  messagesLoading.value = true;
  activeMessages.value = [];

  const requestId = ++activeConversationRequest;

  try {
    const response = await MessageRepository.getByReservation(reservationId);

    if (requestId !== activeConversationRequest) {
      return;
    }

    const mappedMessages = mapMessages(response ?? [], currentUserId.value, locale.value, t);
    activeMessages.value = mappedMessages;

    conversations.value = sortConversations(
      conversations.value.map((item) =>
        item.reservationId === reservationId
          ? mergeConversationPreview(item, mappedMessages, locale.value, t)
          : item
      )
    );
    notifyUnreadRefresh();

    if (!mappedMessages.length) {
      chatInfoMessage.value = t("messages.chat.emptyInfo");
    }
  } catch (error) {
    if (requestId !== activeConversationRequest) {
      return;
    }

    activeMessages.value = [];
    chatErrorMessage.value = getApiErrorMessage(error, t, "messages.chat.error");

    if (syncRoute || String(route.query.reservationId) === String(reservationId)) {
      pageNotice.value = {
        type: "warning",
        message: t("messages.states.openFailed")
      };
      await router.replace({ name: "MessageInbox" });
      selectedConversationId.value = null;
    }
  } finally {
    if (requestId === activeConversationRequest) {
      messagesLoading.value = false;
    }
  }
}

async function sendMessage() {
  const conversation = selectedConversation.value;
  const content = draftMessage.value.trim();

  if (!conversation || !content || sendingMessage.value) {
    return;
  }

  if (!conversation.canSend) {
    chatErrorMessage.value = t("messages.chat.locked");
    return;
  }

  sendingMessage.value = true;
  chatErrorMessage.value = "";
  chatInfoMessage.value = "";

  try {
    const sentMessage = await MessageRepository.sendToReservation(conversation.reservationId, content);
    const mappedMessage = mapMessages([sentMessage], currentUserId.value, locale.value, t)[0];

    activeMessages.value = [...activeMessages.value, mappedMessage];
    draftMessage.value = "";

    conversations.value = sortConversations(
      conversations.value.map((item) =>
        item.reservationId === conversation.reservationId
          ? appendSentMessagePreview(item, mappedMessage, locale.value, t)
          : item
      )
    );
    notifyUnreadRefresh();
  } catch (error) {
    chatErrorMessage.value = getApiErrorMessage(error, t, "messages.chat.sendError");
  } finally {
    sendingMessage.value = false;
  }
}

async function closeConversation() {
  selectedConversationId.value = null;
  activeMessages.value = [];
  chatErrorMessage.value = "";
  chatInfoMessage.value = "";
  draftMessage.value = "";

  if (route.query.reservationId) {
    syncingRouteSelection = true;
    await router.replace({ name: "MessageInbox" });
    syncingRouteSelection = false;
  }
}

function openReservation(conversation) {
  router.push({
    name: "ReservationList",
    query: {
      reservationId: String(conversation.reservationId),
      ...(shouldOpenReceivedReservation(conversation) ? { tab: "received" } : {})
    }
  });
}

function conversationMatchesStatus(conversation, status = "all") {
  return !status || status === "all" || conversation?.reservationState === status;
}

function revealConversationInFilters(conversation) {
  if (!conversationMatchesScope(conversation, activeScopeFilter.value)) {
    activeScopeFilter.value =
      conversation?.scope === CONVERSATION_SCOPE_MANAGED
        ? CONVERSATION_SCOPE_MANAGED
        : CONVERSATION_SCOPE_MINE;
  }

  if (!conversationMatchesStatus(conversation, activeStatusFilter.value)) {
    activeStatusFilter.value = "all";
  }
}

function shouldOpenReceivedReservation(conversation) {
  return conversation?.scope === CONVERSATION_SCOPE_MANAGED
    || (
      activeScopeFilter.value === CONVERSATION_SCOPE_MANAGED &&
      conversationMatchesScope(conversation, CONVERSATION_SCOPE_MANAGED)
    );
}

function syncViewport() {
  isMobile.value = window.innerWidth < 992;
}

function notifyUnreadRefresh() {
  window.dispatchEvent(new CustomEvent("messages:refresh-unread"));
}
</script>

<style scoped>
.messages-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 22%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.messages-shell {
  padding-bottom: 3rem;
}

.messages-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.3rem;
}

.messages-header h1 {
  margin: 0 0 0.6rem;
  font-size: clamp(2rem, 4vw, 3.25rem);
  letter-spacing: -0.04em;
}

.messages-header p {
  margin: 0;
  color: #b3b3b3;
  max-width: 620px;
}

.messages-header__pill {
  display: inline-flex;
  align-items: center;
  min-height: 40px;
  padding: 0 1rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.12);
  border: 1px solid rgba(29, 185, 84, 0.22);
  color: #dfffe9;
  font-weight: 700;
}

.messages-layout {
  display: grid;
  grid-template-columns: minmax(340px, 0.85fr) minmax(0, 1.55fr);
  gap: 1.15rem;
  align-items: start;
}

.messages-sidebar,
.messages-chat {
  min-height: 0;
  padding: 1rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.1), transparent 32%),
    linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
  overflow: hidden;
}

.messages-chat {
  display: flex;
}

.messages-chat.has-conversation {
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.12), transparent 32%),
    linear-gradient(180deg, #121212 0%, #161616 100%);
}

.page-notice {
  margin-bottom: 1rem;
  padding: 0.95rem 1rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice.warning {
  color: #f4d06f;
  border-color: rgba(255, 193, 7, 0.2);
  background: rgba(255, 193, 7, 0.08);
}

.page-notice.error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

@media (max-width: 991.98px) {
  .messages-header,
  .messages-layout {
    grid-template-columns: 1fr;
  }

  .messages-header {
    flex-direction: column;
  }

  .messages-sidebar.is-hidden-mobile,
  .messages-chat.is-hidden-mobile {
    display: none;
  }

  .messages-sidebar,
  .messages-chat {
    border-radius: 22px;
    padding: 0.85rem;
  }
}

@media (max-width: 575.98px) {
  .messages-page :deep(.container) {
    padding-left: 0.85rem;
    padding-right: 0.85rem;
  }

  .messages-header h1 {
    font-size: 2rem;
  }
}
</style>
