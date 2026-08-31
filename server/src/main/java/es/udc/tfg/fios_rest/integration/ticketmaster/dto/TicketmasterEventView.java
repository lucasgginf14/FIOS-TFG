package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TicketmasterEventView(
  String externalId,
  String title,
  String description,
  LocalDate eventDate,
  LocalTime startTime,
  String venueName,
  String city,
  String province,
  String country,
  Double latitude,
  Double longitude,
  String posterImage,
  String externalUrl,
  String musicalGenre,
  BigDecimal priceMin,
  BigDecimal priceMax,
  TicketmasterProvider provider
) {
}
