package es.udc.tfg.fios_rest.userreview.persistence.dao;

import java.math.BigDecimal;

public record UserReviewRatingStats(
  Long userId,
  long reviewsCount,
  BigDecimal averageOverallRating
) {
}
