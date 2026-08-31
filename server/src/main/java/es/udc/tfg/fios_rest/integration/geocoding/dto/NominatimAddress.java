package es.udc.tfg.fios_rest.integration.geocoding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimAddress(
  String country,
  String state,
  String province,
  String region,
  String county,
  String city,
  String town,
  String village,
  String municipality,
  @JsonProperty("postcode")
  String postalCode
) {
}
