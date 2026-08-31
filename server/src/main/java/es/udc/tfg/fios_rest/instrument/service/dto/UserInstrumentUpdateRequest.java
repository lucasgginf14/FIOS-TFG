package es.udc.tfg.fios_rest.instrument.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserInstrumentUpdateRequest(
  @NotNull
  @Size(max = 1)
  Set<@Positive Long> instrumentIds
) {
}
