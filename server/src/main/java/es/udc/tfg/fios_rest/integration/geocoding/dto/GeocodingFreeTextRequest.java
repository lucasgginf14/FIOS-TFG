package es.udc.tfg.fios_rest.integration.geocoding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeocodingFreeTextRequest(
  @NotBlank
  @Size(max = 250)
  String query
) {
}
