package es.udc.tfg.fios_rest.schedule.service.dto;

import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleView(
  Long id,
  DayOfWeek dayOfWeek,
  LocalTime startTime,
  LocalTime endTime,
  BigDecimal price,
  MusicalSpaceRef musicalSpace
) {

  public static ScheduleView from(Schedule schedule) {
    return new ScheduleView(
      schedule.getId(),
      schedule.getDayOfWeek(),
      schedule.getStartTime(),
      schedule.getEndTime(),
      schedule.getPrice(),
      MusicalSpaceRef.from(schedule.getMusicalSpace())
    );
  }
}
