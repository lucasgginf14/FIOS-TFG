package es.udc.tfg.fios_rest.integration.geocoding.dto;

public record GeocodingResultView(
  String displayName,
  Double latitude,
  Double longitude,
  String country,
  String province,
  String city,
  String postalCode,
  GeocodingProvider provider
) {
}
