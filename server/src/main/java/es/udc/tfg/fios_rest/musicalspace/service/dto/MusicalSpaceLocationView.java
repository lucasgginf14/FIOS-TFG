package es.udc.tfg.fios_rest.musicalspace.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;

public record MusicalSpaceLocationView(
  String country,
  String province,
  String city,
  String street,
  String portal,
  String floor,
  String postalCode,
  Double latitude,
  Double longitude
) {

  public static MusicalSpaceLocationView from(MusicalSpaceLocation location) {
    return new MusicalSpaceLocationView(
      location.getCountry(),
      location.getProvince(),
      location.getCity(),
      location.getStreet(),
      location.getPortal(),
      location.getFloor(),
      location.getPostalCode(),
      location.getLatitude(),
      location.getLongitude()
    );
  }
}
