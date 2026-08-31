package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterStart(
  String localDate,
  String localTime,
  String dateTime
) {
}
