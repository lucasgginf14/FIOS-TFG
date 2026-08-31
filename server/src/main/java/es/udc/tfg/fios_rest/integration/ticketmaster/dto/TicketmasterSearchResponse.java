package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterSearchResponse(
  @JsonProperty("_embedded")
  TicketmasterSearchEmbedded embedded
) {
}
