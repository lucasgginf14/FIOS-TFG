package es.udc.tfg.fios_rest.admin.service.dto;

import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.dto.UserProfileImages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AdminUserView(
  Long id,
  String email,
  String name,
  String firstSurname,
  String secondSurname,
  String phone,
  String profileImage,
  LocalDate birthDate,
  LocalDateTime createdAt,
  String platformRole,
  boolean active,
  List<InstrumentRef> instruments
) {

  public static AdminUserView from(User user) {
    InstrumentRef primaryInstrument = user.getPrimaryInstrument() == null
      ? null
      : InstrumentRef.from(user.getPrimaryInstrument());

    return new AdminUserView(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getFirstSurname(),
      user.getSecondSurname(),
      user.getPhone(),
      UserProfileImages.visibleProfileImage(user),
      user.getBirthDate(),
      user.getCreatedAt(),
      user.getPlatformRole().name(),
      user.isActive(),
      primaryInstrument == null ? List.of() : List.of(primaryInstrument)
    );
  }
}
