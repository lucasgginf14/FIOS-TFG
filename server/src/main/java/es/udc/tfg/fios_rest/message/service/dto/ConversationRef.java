package es.udc.tfg.fios_rest.message.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record ConversationRef(
  Long reservationId,
  MusicalSpaceRef musicalSpace,
  ReservationSessionState reservationState,
  UserPublicRef otherUser,
  int unreadMessagesCount,
  LocalDateTime lastMessageDateTime
) {
}
