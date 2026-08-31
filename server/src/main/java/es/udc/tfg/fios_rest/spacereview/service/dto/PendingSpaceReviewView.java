package es.udc.tfg.fios_rest.spacereview.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;

import java.time.LocalDate;
import java.time.LocalTime;

public record PendingSpaceReviewView(
  Long reservationId,
  LocalDate sessionDate,
  LocalTime startTime,
  LocalTime endTime,
  ReservationSessionType sessionType,
  MusicalSpaceRef musicalSpace
) {

  public static PendingSpaceReviewView from(ReservationSession reservationSession) {
    return new PendingSpaceReviewView(
      reservationSession.getId(),
      reservationSession.getSessionDate(),
      reservationSession.getStartTime(),
      reservationSession.getEndTime(),
      reservationSession.getSessionType(),
      MusicalSpaceRef.from(reservationSession.getMusicalSpace())
    );
  }
}
