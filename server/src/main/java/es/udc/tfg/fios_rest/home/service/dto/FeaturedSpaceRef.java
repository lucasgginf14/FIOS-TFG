package es.udc.tfg.fios_rest.home.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewRatingStats;

import java.math.BigDecimal;

public record FeaturedSpaceRef(
  Long id,
  String name,
  MusicalSpaceType spaceType,
  int capacity,
  String mainImage,
  String city,
  String province,
  String country,
  Double latitude,
  Double longitude,
  double squareMeters,
  boolean soundproofed,
  long reviewsCount,
  BigDecimal rating,
  BigDecimal estimatedPrice,
  double featuredScore,
  String featuredReason
) {

  public static FeaturedSpaceRef from(
    MusicalSpace musicalSpace,
    BigDecimal estimatedPrice,
    SpaceReviewRatingStats ratingStats,
    double featuredScore,
    String featuredReason
  ) {
    return new FeaturedSpaceRef(
      musicalSpace.getId(),
      musicalSpace.getName(),
      musicalSpace.getSpaceType(),
      musicalSpace.getCapacity(),
      musicalSpace.getMainImage(),
      musicalSpace.getLocation().getCity(),
      musicalSpace.getLocation().getProvince(),
      musicalSpace.getLocation().getCountry(),
      musicalSpace.getLocation().getLatitude(),
      musicalSpace.getLocation().getLongitude(),
      musicalSpace.getSquareMeters(),
      musicalSpace.isSoundproofed(),
      ratingStats == null ? 0 : ratingStats.reviewsCount(),
      ratingStats == null ? BigDecimal.ZERO : ratingStats.averageOverallRating(),
      estimatedPrice,
      featuredScore,
      featuredReason
    );
  }
}
