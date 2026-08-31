package es.udc.tfg.fios_rest.availability.service.dto;

import java.time.LocalDate;
import java.util.List;

public record CalculatedAvailabilityView(
  Long spaceId,
  LocalDate date,
  List<AvailabilitySlotView> slots,
  List<BookedAvailabilitySlotView> bookedSlots
) {
}
