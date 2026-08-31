package es.udc.tfg.fios_rest.message.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
  @NotBlank
  @Size(max = 2000)
  String content
) {
}
