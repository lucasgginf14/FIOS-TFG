package es.udc.tfg.fios_rest.integration.geocoding.service;

import es.udc.tfg.fios_rest.integration.geocoding.client.GeocodingClient;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingFreeTextRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingResultView;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingStructuredRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GeocodingServiceImpl implements GeocodingService {

  private final GeocodingClient geocodingClient;

  public GeocodingServiceImpl(GeocodingClient geocodingClient) {
    this.geocodingClient = geocodingClient;
  }

  @Override
  public List<GeocodingResultView> search(GeocodingFreeTextRequest request) throws GeocodingException {
    validate(request);
    return geocodingClient.search(normalize(request));
  }

  @Override
  public List<GeocodingResultView> search(GeocodingStructuredRequest request) throws GeocodingException {
    validate(request);
    return geocodingClient.search(normalize(request));
  }

  @Override
  public Optional<GeocodingResultView> findFirstValidResult(GeocodingStructuredRequest request) throws GeocodingException {
    return search(request).stream()
      .filter(this::isValidResult)
      .findFirst();
  }

  private void validate(GeocodingFreeTextRequest request) {
    if (request == null || !StringUtils.hasText(request.query())) {
      throw new IllegalArgumentException("The geocoding query cannot be null or empty");
    }
  }

  private void validate(GeocodingStructuredRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("The structured geocoding request cannot be null");
    }

    if (!request.hasMinimumData()) {
      throw new IllegalArgumentException(
        "The structured geocoding request must contain at least two address fields, including city, province, street or postalCode"
      );
    }
  }

  private GeocodingFreeTextRequest normalize(GeocodingFreeTextRequest request) {
    return new GeocodingFreeTextRequest(request.query().trim());
  }

  private GeocodingStructuredRequest normalize(GeocodingStructuredRequest request) {
    return new GeocodingStructuredRequest(
      normalizeNullable(request.country()),
      normalizeNullable(request.province()),
      normalizeNullable(request.city()),
      normalizeNullable(request.street()),
      normalizeNullable(request.portal()),
      normalizeNullable(request.postalCode())
    );
  }

  private boolean isValidResult(GeocodingResultView result) {
    return result != null
      && StringUtils.hasText(result.displayName())
      && result.latitude() != null
      && result.longitude() != null;
  }

  private String normalizeNullable(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
