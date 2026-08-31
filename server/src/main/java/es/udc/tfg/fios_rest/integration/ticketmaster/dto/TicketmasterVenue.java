package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterVenue(
  String name,
  TicketmasterNamedValue city,
  TicketmasterNamedValue state,
  TicketmasterNamedValue country,
  TicketmasterLocation location,
  TicketmasterAddress address
) {
}
