package es.udc.tfg.fios_rest.musicalspace.service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MusicalSpaceLocationRequest(
  @NotBlank
  @Size(max = 100)
  String country,

  @NotBlank
  @Size(max = 100)
  String province,

  @NotBlank
  @Size(max = 120)
  String city,

  @NotBlank
  @Size(max = 150)
  String street,

  @Size(max = 30)
  String portal,

  @Size(max = 30)
  String floor,

  @NotBlank
  @Size(max = 20)
  String postalCode,

  @DecimalMin("-90.0")
  @DecimalMax("90.0")
  Double latitude,

  @DecimalMin("-180.0")
  @DecimalMax("180.0")
  Double longitude
) {
}
