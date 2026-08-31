package es.udc.tfg.fios_rest.musicalspace.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MusicalSpaceRequest(
  @NotBlank
  @Size(max = 150)
  String name,

  @Size(max = 1500)
  String description,

  @NotNull
  MusicalSpaceType spaceType,

  @NotNull
  @Min(1)
  Integer capacity,

  @Size(max = 500)
  String mainImage,

  @NotNull
  @DecimalMin(value = "0.1")
  Double squareMeters,

  @NotNull
  Boolean soundproofed,

  @Valid
  @NotNull
  MusicalSpaceLocationRequest location
) {
}
