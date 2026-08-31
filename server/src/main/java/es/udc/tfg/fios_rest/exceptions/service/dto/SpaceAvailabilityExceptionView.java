package es.udc.tfg.fios_rest.exceptions.service.dto;

import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SpaceAvailabilityExceptionView(
  Long id,
  LocalDate date,
  LocalTime startTime,
  LocalTime endTime,
  SpaceAvailabilityExceptionType exceptionType,
  String reason,
  BigDecimal price,
  MusicalSpaceRef musicalSpace
) {

  public static SpaceAvailabilityExceptionView from(SpaceAvailabilityException spaceAvailabilityException) {
    return new SpaceAvailabilityExceptionView(
      spaceAvailabilityException.getId(),
      spaceAvailabilityException.getDate(),
      spaceAvailabilityException.getStartTime(),
      spaceAvailabilityException.getEndTime(),
      spaceAvailabilityException.getExceptionType(),
      spaceAvailabilityException.getReason(),
      spaceAvailabilityException.getPrice(),
      MusicalSpaceRef.from(spaceAvailabilityException.getMusicalSpace())
    );
  }
}
