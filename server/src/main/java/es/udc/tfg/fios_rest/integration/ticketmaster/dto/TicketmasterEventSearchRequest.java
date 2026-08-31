package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TicketmasterEventSearchRequest(
  @Size(max = 120)
  String city,

  @Size(max = 2)
  String countryCode,

  @Size(max = 200)
  String keyword,

  LocalDate startDate,

  LocalDate endDate,

  @Size(max = 100)
  String musicalGenre,

  @Min(1)
  @Max(50)
  Integer size
) {
  @AssertTrue(message = "validation.dateRange")
  public boolean isDateRangeValid() {
    return startDate == null || endDate == null || !startDate.isAfter(endDate);
  }
}
