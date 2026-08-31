package es.udc.tfg.fios_rest.availability.service.dto;

import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;

import java.time.LocalTime;

public record BookedAvailabilitySlotView(
  LocalTime startTime,
  LocalTime endTime,
  ReservationSessionState state
) {
}
