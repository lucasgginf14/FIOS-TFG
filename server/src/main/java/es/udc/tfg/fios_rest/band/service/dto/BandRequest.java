package es.udc.tfg.fios_rest.band.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BandRequest(
  @NotBlank
  @Size(max = 150)
  String name,

  @Size(max = 1000)
  String description,

  @NotBlank
  @Size(max = 100)
  String mainGenre,

  @NotBlank
  @Size(max = 120)
  String baseCity,

  @Size(max = 500)
  String image
) {
}
