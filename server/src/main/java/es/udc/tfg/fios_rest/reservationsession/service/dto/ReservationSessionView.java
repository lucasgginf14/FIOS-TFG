package es.udc.tfg.fios_rest.reservationsession.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservationSessionView(
  Long id,
  LocalDate sessionDate,
  LocalTime startTime,
  LocalTime endTime,
  int attendeesCount,
  ReservationSessionType sessionType,
  String observations,
  String cancellationReason,
  LocalDateTime createdAt,
  BigDecimal finalPrice,
  ReservationSessionState state,
  MusicalSpaceRef musicalSpace,
  UserPublicRef user,
  BandRef band
) {

  public static ReservationSessionView from(ReservationSession reservationSession) {
    return new ReservationSessionView(
      reservationSession.getId(),
      reservationSession.getSessionDate(),
      reservationSession.getStartTime(),
      reservationSession.getEndTime(),
      reservationSession.getAttendeesCount(),
      reservationSession.getSessionType(),
      reservationSession.getObservations(),
      reservationSession.getCancellationReason(),
      reservationSession.getCreatedAt(),
      reservationSession.getFinalPrice(),
      reservationSession.getState(),
      MusicalSpaceRef.from(reservationSession.getMusicalSpace()),
      UserPublicRef.from(reservationSession.getUser()),
      reservationSession.getBand() == null ? null : BandRef.from(reservationSession.getBand())
    );
  }
}
