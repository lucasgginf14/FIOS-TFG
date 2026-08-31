package es.udc.tfg.fios_rest.admin.service.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserActiveRequest(
  @NotNull
  Boolean active
) {
}
