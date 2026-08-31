package es.udc.tfg.fios_rest.musicalspace.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class MusicalSpaceLocation {

  @Column(name = "country", nullable = false, length = 100)
  private String country;

  @Column(name = "province", nullable = false, length = 100)
  private String province;

  @Column(name = "city", nullable = false, length = 120)
  private String city;

  @Column(name = "street", nullable = false, length = 150)
  private String street;

  @Column(name = "portal", length = 30)
  private String portal;

  @Column(name = "floor", length = 30)
  private String floor;

  @Column(name = "postal_code", nullable = false, length = 20)
  private String postalCode;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  protected MusicalSpaceLocation() {
    // Constructor vacio requerido por JPA
  }

  public MusicalSpaceLocation(
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
    this.country = normalizeRequired(country, "country");
    this.province = normalizeRequired(province, "province");
    this.city = normalizeRequired(city, "city");
    this.street = normalizeRequired(street, "street");
    this.portal = normalizeNullable(portal);
    this.floor = normalizeNullable(floor);
    this.postalCode = normalizeRequired(postalCode, "postal code");
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = normalizeRequired(country, "country");
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = normalizeRequired(province, "province");
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = normalizeRequired(city, "city");
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = normalizeRequired(street, "street");
  }

  public String getPortal() {
    return portal;
  }

  public void setPortal(String portal) {
    this.portal = normalizeNullable(portal);
  }

  public String getFloor() {
    return floor;
  }

  public void setFloor(String floor) {
    this.floor = normalizeNullable(floor);
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = normalizeRequired(postalCode, "postal code");
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    if (latitude != null && (latitude < -90 || latitude > 90)) {
      throw new IllegalArgumentException("The latitude must be between -90 and 90");
    }
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    if (longitude != null && (longitude < -180 || longitude > 180)) {
      throw new IllegalArgumentException("The longitude must be between -180 and 180");
    }
    this.longitude = longitude;
  }

  private String normalizeRequired(String value, String fieldName) {
    Objects.requireNonNull(value, fieldName + " no puede ser null");
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("The " + fieldName + " is obligatory");
    }
    return normalized;
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }
}
