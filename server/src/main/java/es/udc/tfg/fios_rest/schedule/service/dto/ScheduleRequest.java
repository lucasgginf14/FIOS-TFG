package es.udc.tfg.fios_rest.schedule.service.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleRequest(
  @NotNull
  DayOfWeek dayOfWeek,

  @NotNull
  LocalTime startTime,

  @NotNull
  LocalTime endTime,

  @NotNull
  @DecimalMin(value = "0.01")
  BigDecimal price
) {
  @AssertTrue(message = "validation.timeRange")
  public boolean isTimeRangeValid() {
    return startTime == null || endTime == null || startTime.isBefore(endTime);
  }
}
