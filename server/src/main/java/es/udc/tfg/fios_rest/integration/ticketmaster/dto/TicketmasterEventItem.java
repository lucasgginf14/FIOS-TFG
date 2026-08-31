package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterEventItem(
  String id,
  String name,
  String description,
  String info,
  String pleaseNote,
  String url,
  TicketmasterDates dates,
  List<TicketmasterImage> images,
  List<TicketmasterPriceRange> priceRanges,
  List<TicketmasterClassification> classifications,
  @JsonProperty("_embedded")
  TicketmasterEventEmbedded embedded
) {
}
