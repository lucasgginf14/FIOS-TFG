package es.udc.tfg.fios_rest.user.service.dto;

import java.time.LocalDateTime;

public record UserAdminRef(
  Long id,
  String email,
  LocalDateTime createdAt,
  String platformRole,
  boolean active
) {
}
