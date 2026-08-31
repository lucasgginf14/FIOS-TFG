package es.udc.tfg.fios_rest.spacereview.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record SpaceReviewView(
  Long id,
  String comment,
  int overallRating,
  LocalDateTime createdAt,
  int cleanlinessRating,
  int soundQualityRating,
  int equipmentRating,
  int locationRating,
  MusicalSpaceRef musicalSpace,
  UserPublicRef user,
  Long reservationSessionId
) {

  public static SpaceReviewView from(SpaceReview spaceReview) {
    return new SpaceReviewView(
      spaceReview.getId(),
      spaceReview.getComment(),
      spaceReview.getOverallRating(),
      spaceReview.getCreatedAt(),
      spaceReview.getCleanlinessRating(),
      spaceReview.getSoundQualityRating(),
      spaceReview.getEquipmentRating(),
      spaceReview.getLocationRating(),
      MusicalSpaceRef.from(spaceReview.getMusicalSpace()),
      UserPublicRef.from(spaceReview.getUser()),
      spaceReview.getReservationSession().getId()
    );
  }
}
