package es.udc.tfg.fios_rest.message.service.dto;

import java.util.List;

public record UnreadMessagesView(
  int totalUnreadMessages,
  List<ConversationRef> reservations
) {
}
