package es.udc.tfg.fios_rest.reservationsession.service.dto;

import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationSessionRequest(
  @NotNull
  @Positive
  Long musicalSpaceId,

  @NotNull
  @FutureOrPresent
  LocalDate sessionDate,

  @NotNull
  LocalTime startTime,

  @NotNull
  LocalTime endTime,

  @NotNull
  @Min(1)
  Integer attendeesCount,

  @NotNull
  ReservationSessionType sessionType,

  @Size(max = 1000)
  String observations,

  @Positive
  Long bandId
) {
  @AssertTrue(message = "validation.timeRange")
  public boolean isTimeRangeValid() {
    return startTime == null || endTime == null || startTime.isBefore(endTime);
  }
}
