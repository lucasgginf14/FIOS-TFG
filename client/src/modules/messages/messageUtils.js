import {
  formatDate,
  formatDateTime,
  normalizeTimeString
} from "@/modules/musical-spaces/spaceDetailUtils";

const CHAT_ENABLED_STATES = ["PENDING", "ACCEPTED"];
export const CONVERSATION_SCOPE_ALL = "all";
export const CONVERSATION_SCOPE_MINE = "mine";
export const CONVERSATION_SCOPE_MANAGED = "managed";
export const CONVERSATION_SCOPE_BOTH = "both";

export function mergeReservationScopes(myReservations = [], managedReservations = []) {
  const reservationsById = new Map();

  addReservationsWithScope(reservationsById, myReservations, CONVERSATION_SCOPE_MINE);
  addReservationsWithScope(reservationsById, managedReservations, CONVERSATION_SCOPE_MANAGED);

  return [...reservationsById.values()];
}

export function conversationMatchesScope(conversation, activeScope = CONVERSATION_SCOPE_ALL) {
  const scope = normalizeConversationScope(conversation?.scope);

  return !activeScope
    || activeScope === CONVERSATION_SCOPE_ALL
    || scope === activeScope
    || scope === CONVERSATION_SCOPE_BOTH;
}

export function buildConversations(reservations = [], unreadSummary = {}, locale, t) {
  const unreadByReservation = new Map(
    (unreadSummary?.reservations ?? []).map((item) => [Number(item.reservationId), item])
  );

  return [...reservations]
    .map((reservation) => {
      const unreadRef = unreadByReservation.get(Number(reservation.id));
      const scope = normalizeConversationScope(reservation?.conversationScope);
      const participantName =
        buildParticipantName(unreadRef?.otherUser) ||
        buildReservationParticipantName(reservation, scope) ||
        t("messages.list.fallbackParticipant");
      const location = [reservation?.musicalSpace?.city, reservation?.musicalSpace?.province]
        .filter(Boolean)
        .join(", ");
      const reservationDate = reservation?.sessionDate || "";
      const reservationTime = formatReservationTime(reservation);

      return {
        reservationId: reservation.id,
        reservation,
        scope,
        title: reservation?.musicalSpace?.name || t("messages.list.fallbackTitle"),
        image: reservation?.musicalSpace?.mainImage || "",
        participantName,
        location,
        reservationState: reservation?.state || "PENDING",
        reservationStateLabel: t(`reservations.statuses.${reservation?.state || "PENDING"}`),
        unreadCount: Number(unreadRef?.unreadMessagesCount || 0),
        snippet: t("messages.list.noMessagesYet"),
        lastMessageDateTime: unreadRef?.lastMessageDateTime || null,
        lastMessageLabel: unreadRef?.lastMessageDateTime
          ? formatConversationTimestamp(unreadRef.lastMessageDateTime, locale, t)
          : formatDate(reservationDate, locale),
        contextLabel: [location, reservationTime].filter(Boolean).join(" · "),
        canSend: CHAT_ENABLED_STATES.includes(reservation?.state),
        sortTimestamp: resolveConversationTimestamp(reservation, unreadRef?.lastMessageDateTime),
        hasLoadedMessages: false
      };
    })
    .sort((left, right) => right.sortTimestamp - left.sortTimestamp);
}

export function mapMessages(messages = [], currentUserId, locale, t) {
  return (messages ?? []).map((message) => {
    const authorName = buildParticipantName(message.user) || t("messages.chat.fallbackAuthor");

    return {
      ...message,
      own: Number(message?.user?.id) === Number(currentUserId),
      timeLabel: formatMessageTime(message?.dateTime, locale),
      dayLabel: formatMessageDay(message?.dateTime, locale, t),
      authorName
    };
  });
}

export function mergeConversationPreview(conversation, messages = [], locale, t) {
  if (!conversation) {
    return conversation;
  }

  const lastMessage = messages[messages.length - 1];

  if (!lastMessage) {
    return {
      ...conversation,
      unreadCount: 0,
      hasLoadedMessages: true
    };
  }

  return {
    ...conversation,
    snippet: truncateText(lastMessage.content, 88),
    unreadCount: 0,
    lastMessageDateTime: lastMessage.dateTime,
    lastMessageLabel: formatConversationTimestamp(lastMessage.dateTime, locale, t),
    sortTimestamp: lastMessage.dateTime ? new Date(lastMessage.dateTime).getTime() : conversation.sortTimestamp,
    hasLoadedMessages: true
  };
}

export function appendSentMessagePreview(conversation, message, locale, t) {
  if (!conversation || !message) {
    return conversation;
  }

  return {
    ...conversation,
    snippet: truncateText(message.content, 88),
    lastMessageDateTime: message.dateTime,
    lastMessageLabel: formatConversationTimestamp(message.dateTime, locale, t),
    sortTimestamp: message.dateTime ? new Date(message.dateTime).getTime() : conversation.sortTimestamp,
    hasLoadedMessages: true
  };
}

export function filterConversations(conversations = [], search = "") {
  const needle = normalizeText(search);

  if (!needle) {
    return conversations;
  }

  return conversations.filter((conversation) =>
    normalizeText([
      conversation.title,
      conversation.participantName,
      conversation.location,
      conversation.reservationStateLabel,
      conversation.snippet
    ].join(" ")).includes(needle)
  );
}

export function sortConversations(conversations = []) {
  return [...conversations].sort((left, right) => right.sortTimestamp - left.sortTimestamp);
}

export function formatConversationTimestamp(value, locale, t) {
  if (!value) {
    return "";
  }

  const date = new Date(value);
  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);

  if (isSameDay(date, today)) {
    return formatMessageTime(value, locale);
  }

  if (isSameDay(date, yesterday)) {
    return t("messages.dates.yesterday");
  }

  return formatDateTime(value, locale);
}

export function formatMessageTime(value, locale) {
  if (!value) {
    return "--";
  }

  return new Intl.DateTimeFormat(locale, {
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

export function formatMessageDay(value, locale, t) {
  if (!value) {
    return "";
  }

  const date = new Date(value);
  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);

  if (isSameDay(date, today)) {
    return t("messages.dates.today");
  }

  if (isSameDay(date, yesterday)) {
    return t("messages.dates.yesterday");
  }

  return new Intl.DateTimeFormat(locale, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(date);
}

function resolveConversationTimestamp(reservation, lastMessageDateTime) {
  if (lastMessageDateTime) {
    return new Date(lastMessageDateTime).getTime();
  }

  if (!reservation?.sessionDate) {
    return 0;
  }

  const startTime = normalizeTimeString(reservation.startTime) || "00:00";
  return new Date(`${reservation.sessionDate}T${startTime}:00`).getTime();
}

function formatReservationTime(reservation) {
  const startTime = normalizeTimeString(reservation?.startTime);
  const endTime = normalizeTimeString(reservation?.endTime);

  if (!startTime && !endTime) {
    return "";
  }

  return [startTime, endTime].filter(Boolean).join(" - ");
}

function buildParticipantName(user) {
  return [user?.name, user?.firstSurname].filter(Boolean).join(" ").trim();
}

function buildReservationParticipantName(reservation, scope) {
  if (scope === CONVERSATION_SCOPE_MANAGED || scope === CONVERSATION_SCOPE_BOTH) {
    return buildParticipantName(reservation?.user);
  }

  return "";
}

function truncateText(value, maxLength) {
  const text = (value || "").trim();

  if (text.length <= maxLength) {
    return text;
  }

  return `${text.slice(0, maxLength - 1)}…`;
}

function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}

function isSameDay(left, right) {
  return left.getFullYear() === right.getFullYear()
    && left.getMonth() === right.getMonth()
    && left.getDate() === right.getDate();
}

function addReservationsWithScope(reservationsById, reservations, scope) {
  for (const reservation of reservations ?? []) {
    const reservationId = Number(reservation?.id);

    if (!Number.isFinite(reservationId)) {
      continue;
    }

    const previousReservation = reservationsById.get(reservationId);
    const nextScope = mergeConversationScopes(previousReservation?.conversationScope, scope);

    reservationsById.set(reservationId, {
      ...(previousReservation ?? {}),
      ...reservation,
      conversationScope: nextScope
    });
  }
}

function mergeConversationScopes(leftScope, rightScope) {
  const left = isKnownConversationScope(leftScope) ? leftScope : null;
  const right = isKnownConversationScope(rightScope) ? rightScope : null;

  if (!left) {
    return right || CONVERSATION_SCOPE_MINE;
  }

  if (!right) {
    return left;
  }

  if (left === right) {
    return left;
  }

  return CONVERSATION_SCOPE_BOTH;
}

function normalizeConversationScope(scope) {
  if (isKnownConversationScope(scope)) {
    return scope;
  }

  return CONVERSATION_SCOPE_MINE;
}

function isKnownConversationScope(scope) {
  return scope === CONVERSATION_SCOPE_MANAGED
    || scope === CONVERSATION_SCOPE_BOTH
    || scope === CONVERSATION_SCOPE_MINE;
}
