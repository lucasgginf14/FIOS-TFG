package es.udc.tfg.fios_rest.spacereview.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SpaceReviewRequest(
  @Size(max = 1000)
  String comment,

  @NotNull
  @Min(1)
  @Max(5)
  Integer overallRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer cleanlinessRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer soundQualityRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer equipmentRating,

  @NotNull
  @Min(1)
  @Max(5)
  Integer locationRating
) {
}
