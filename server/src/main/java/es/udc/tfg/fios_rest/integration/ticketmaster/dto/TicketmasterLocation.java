package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterLocation(
  String latitude,
  String longitude
) {
}
