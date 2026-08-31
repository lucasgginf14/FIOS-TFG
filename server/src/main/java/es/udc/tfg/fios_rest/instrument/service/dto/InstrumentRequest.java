package es.udc.tfg.fios_rest.instrument.service.dto;

import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstrumentRequest(
  @NotBlank
  @Size(max = 100)
  String name,

  @NotNull
  InstrumentCategory category
) {
}
