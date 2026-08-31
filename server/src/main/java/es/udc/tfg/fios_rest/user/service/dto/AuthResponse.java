package es.udc.tfg.fios_rest.user.service.dto;

public record AuthResponse(
  String token,
  String tokenType,
  UserPrivateView user
) {
}
