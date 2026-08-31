package es.udc.tfg.fios_rest.message.service.dto;

import es.udc.tfg.fios_rest.message.persistence.entity.Message;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record MessageView(
  Long id,
  String content,
  LocalDateTime dateTime,
  boolean read,
  UserPublicRef user
) {

  public static MessageView from(Message message) {
    return new MessageView(
      message.getId(),
      message.getContent(),
      message.getDateTime(),
      message.isRead(),
      UserPublicRef.from(message.getUser())
    );
  }
}
