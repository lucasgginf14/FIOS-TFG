package es.udc.tfg.fios_rest.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileImageRequest(
  @NotBlank
  @Size(max = 500)
  String profileImage
) {
}
