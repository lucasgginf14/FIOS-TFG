package es.udc.tfg.fios_rest.user.service.dto;

import es.udc.tfg.fios_rest.user.persistence.entity.User;

import java.time.LocalDateTime;

public record UserPublicRef(
  Long id,
  String name,
  String firstSurname,
  String secondSurname,
  String profileImage,
  LocalDateTime createdAt
) {

  public static UserPublicRef from(User user) {
    return new UserPublicRef(
      user.getId(),
      user.getName(),
      user.getFirstSurname(),
      user.getSecondSurname(),
      UserProfileImages.visibleProfileImage(user),
      user.getCreatedAt()
    );
  }
}
