package es.udc.tfg.fios_rest.userreview.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDate;
import java.time.LocalTime;

public record PendingUserReviewView(
  Long reservationId,
  LocalDate sessionDate,
  LocalTime startTime,
  LocalTime endTime,
  ReservationSessionType sessionType,
  MusicalSpaceRef musicalSpace,
  UserPublicRef reviewedUser
) {

  public static PendingUserReviewView from(ReservationSession reservationSession) {
    return new PendingUserReviewView(
      reservationSession.getId(),
      reservationSession.getSessionDate(),
      reservationSession.getStartTime(),
      reservationSession.getEndTime(),
      reservationSession.getSessionType(),
      MusicalSpaceRef.from(reservationSession.getMusicalSpace()),
      UserPublicRef.from(reservationSession.getUser())
    );
  }
}
