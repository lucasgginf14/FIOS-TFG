package es.udc.tfg.fios_rest.availability.service.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record AvailabilitySlotView(
  LocalTime startTime,
  LocalTime endTime,
  BigDecimal price
) {
}
