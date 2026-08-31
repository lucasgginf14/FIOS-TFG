package es.udc.tfg.fios_rest.userreview.service.dto;

import java.math.BigDecimal;

public record UserRatingView(
  Long userId,
  long reviewsCount,
  BigDecimal averageOverallRating
) {
}
