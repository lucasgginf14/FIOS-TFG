package es.udc.tfg.fios_rest.integration.geocoding.dto;

import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record GeocodingStructuredRequest(
  @Size(max = 100)
  String country,

  @Size(max = 100)
  String province,

  @Size(max = 120)
  String city,

  @Size(max = 150)
  String street,

  @Size(max = 30)
  String portal,

  @Size(max = 20)
  String postalCode
) {

  public boolean hasMinimumData() {
    int populatedFields = countIfPresent(country)
      + countIfPresent(province)
      + countIfPresent(city)
      + countIfPresent(street)
      + countIfPresent(portal)
      + countIfPresent(postalCode);

    boolean hasRelevantLocationField = StringUtils.hasText(street)
      || StringUtils.hasText(city)
      || StringUtils.hasText(province)
      || StringUtils.hasText(postalCode);

    return populatedFields >= 2 && hasRelevantLocationField;
  }

  public String streetLine() {
    if (!StringUtils.hasText(street)) {
      return null;
    }

    if (!StringUtils.hasText(portal)) {
      return street.trim();
    }

    return street.trim() + " " + portal.trim();
  }

  private int countIfPresent(String value) {
    return StringUtils.hasText(value) ? 1 : 0;
  }
}
