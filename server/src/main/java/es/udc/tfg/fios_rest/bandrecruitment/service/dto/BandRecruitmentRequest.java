package es.udc.tfg.fios_rest.bandrecruitment.service.dto;

import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BandRecruitmentRequest(
  @NotBlank
  @Size(max = 150)
  String title,

  @Size(max = 1500)
  String description,

  @NotBlank
  @Size(max = 120)
  String roleWanted,

  @NotNull
  BandRecruitmentLevel levelRequired,

  @NotBlank
  @Size(max = 120)
  String city,

  @NotNull
  @Min(1)
  Integer vacancies,

  @NotNull
  @Positive
  Long instrumentId
) {
}
