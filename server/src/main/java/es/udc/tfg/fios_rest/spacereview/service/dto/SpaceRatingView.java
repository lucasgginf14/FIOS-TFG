package es.udc.tfg.fios_rest.spacereview.service.dto;

import java.math.BigDecimal;

public record SpaceRatingView(
  Long musicalSpaceId,
  long reviewsCount,
  BigDecimal averageOverallRating
) {
}
