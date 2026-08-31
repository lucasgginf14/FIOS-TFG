package es.udc.tfg.fios_rest.userreview.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;
import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;

import java.time.LocalDateTime;

public record UserReviewView(
  Long id,
  String comment,
  int overallRating,
  LocalDateTime createdAt,
  int communicationRating,
  int punctualityRating,
  int careRating,
  MusicalSpaceRef musicalSpace,
  UserPublicRef reviewer,
  UserPublicRef reviewedUser,
  Long reservationSessionId
) {

  public static UserReviewView from(UserReview userReview) {
    return new UserReviewView(
      userReview.getId(),
      userReview.getComment(),
      userReview.getOverallRating(),
      userReview.getCreatedAt(),
      userReview.getCommunicationRating(),
      userReview.getPunctualityRating(),
      userReview.getCareRating(),
      MusicalSpaceRef.from(userReview.getMusicalSpace()),
      UserPublicRef.from(userReview.getReviewer()),
      UserPublicRef.from(userReview.getReviewedUser()),
      userReview.getReservationSession().getId()
    );
  }
}
