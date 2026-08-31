package es.udc.tfg.fios_rest.integration.geocoding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimSearchResult(
  @JsonProperty("display_name")
  String displayName,

  @JsonProperty("lat")
  String latitude,

  @JsonProperty("lon")
  String longitude,

  NominatimAddress address
) {
}
