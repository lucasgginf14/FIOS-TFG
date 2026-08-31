package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterSearchEmbedded(
  List<TicketmasterEventItem> events
) {
}
