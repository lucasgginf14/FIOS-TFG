package es.udc.tfg.fios_rest.admin.service.dto;

import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.dto.UserProfileImages;

import java.time.LocalDateTime;

public record AdminUserRef(
  Long id,
  String email,
  String name,
  String firstSurname,
  String secondSurname,
  String profileImage,
  String platformRole,
  boolean active,
  LocalDateTime createdAt
) {

  public static AdminUserRef from(User user) {
    return new AdminUserRef(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getFirstSurname(),
      user.getSecondSurname(),
      UserProfileImages.visibleProfileImage(user),
      user.getPlatformRole().name(),
      user.isActive(),
      user.getCreatedAt()
    );
  }
}
