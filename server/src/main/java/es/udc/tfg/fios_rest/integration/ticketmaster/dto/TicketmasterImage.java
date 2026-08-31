package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterImage(
  String url,
  Integer width,
  Integer height,
  String ratio
) {
}
