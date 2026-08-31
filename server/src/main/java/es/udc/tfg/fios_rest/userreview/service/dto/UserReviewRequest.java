package es.udc.tfg.fios_rest.userreview.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserReviewRequest(
  @Size(max = 1000)
  String comment,

  @NotNull
  @Min(1)
  @Max(5)
  Integer overallRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer communicationRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer punctualityRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer careRating
) {
}
