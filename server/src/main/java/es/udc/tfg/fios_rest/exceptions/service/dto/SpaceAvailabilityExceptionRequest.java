package es.udc.tfg.fios_rest.exceptions.service.dto;

import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SpaceAvailabilityExceptionRequest(
  @NotNull
  LocalDate date,

  @NotNull
  LocalTime startTime,

  @NotNull
  LocalTime endTime,

  @NotNull
  SpaceAvailabilityExceptionType exceptionType,

  @Size(max = 500)
  String reason,

  @DecimalMin(value = "0.01")
  BigDecimal price
) {
  @AssertTrue(message = "validation.timeRange")
  public boolean isTimeRangeValid() {
    return startTime == null || endTime == null || startTime.isBefore(endTime);
  }
}
