package es.udc.tfg.fios_rest.spacereview.persistence.dao;

import java.math.BigDecimal;

public record SpaceReviewRatingStats(
  Long musicalSpaceId,
  long reviewsCount,
  BigDecimal averageOverallRating
) {
}
