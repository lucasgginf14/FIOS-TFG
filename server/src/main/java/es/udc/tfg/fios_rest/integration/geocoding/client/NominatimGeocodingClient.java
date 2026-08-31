package es.udc.tfg.fios_rest.integration.geocoding.client;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingFreeTextRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingProvider;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingResultView;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingStructuredRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.NominatimAddress;
import es.udc.tfg.fios_rest.integration.geocoding.dto.NominatimSearchResult;
import es.udc.tfg.fios_rest.integration.geocoding.service.GeocodingException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class NominatimGeocodingClient implements GeocodingClient {

  private static final String SEARCH_PATH = "/search";
  private static final int MAX_SAFE_LIMIT = 10;

  private final RestTemplate restTemplate;
  private final Properties.Geocoding geocodingProperties;

  public NominatimGeocodingClient(RestTemplateBuilder restTemplateBuilder, Properties properties) {
    this.geocodingProperties = properties.getGeocoding();
    this.restTemplate = restTemplateBuilder
      .setConnectTimeout(Duration.ofMillis(this.geocodingProperties.getTimeout()))
      .setReadTimeout(Duration.ofMillis(this.geocodingProperties.getTimeout()))
      .defaultHeader(HttpHeaders.USER_AGENT, this.geocodingProperties.getUserAgent())
      .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .build();
  }

  @Override
  public List<GeocodingResultView> search(GeocodingFreeTextRequest request) throws GeocodingException {
    URI uri = baseSearchUriBuilder()
      .queryParam("q", request.query())
      .build()
      .encode()
      .toUri();

    return executeSearch(uri);
  }

  @Override
  public List<GeocodingResultView> search(GeocodingStructuredRequest request) throws GeocodingException {
    UriComponentsBuilder builder = baseSearchUriBuilder();

    addQueryParamIfPresent(builder, "street", request.streetLine());
    addQueryParamIfPresent(builder, "city", request.city());
    addQueryParamIfPresent(builder, "state", request.province());
    addQueryParamIfPresent(builder, "country", request.country());
    addQueryParamIfPresent(builder, "postalcode", request.postalCode());

    return executeSearch(builder.build().encode().toUri());
  }

  @Override
  public GeocodingProvider getProvider() {
    return GeocodingProvider.NOMINATIM;
  }

  private UriComponentsBuilder baseSearchUriBuilder() {
    UriComponentsBuilder builder = UriComponentsBuilder
      .fromUriString(trimTrailingSlash(geocodingProperties.getBaseUrl()))
      .path(SEARCH_PATH)
      .queryParam("format", "jsonv2")
      .queryParam("addressdetails", 1)
      .queryParam("limit", normalizedLimit());

    if (StringUtils.hasText(geocodingProperties.getCountryCodes())) {
      builder.queryParam("countrycodes", geocodingProperties.getCountryCodes().trim());
    }

    return builder;
  }

  private List<GeocodingResultView> executeSearch(URI uri) throws GeocodingException {
    try {
      ResponseEntity<NominatimSearchResult[]> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        null,
        NominatimSearchResult[].class
      );

      NominatimSearchResult[] body = response.getBody();
      if (body == null) {
        throw GeocodingException.invalidResponse("The geocoding provider returned an empty body", null);
      }

      if (body.length == 0) {
        return Collections.emptyList();
      }

      return Arrays.stream(body)
        .map(this::toResultView)
        .toList();
    } catch (IllegalStateException e) {
      throw GeocodingException.invalidResponse(e.getMessage(), e);
    } catch (HttpStatusCodeException e) {
      throw GeocodingException.externalHttp(e.getStatusCode(), null, e);
    } catch (ResourceAccessException e) {
      if (isTimeout(e)) {
        throw GeocodingException.timeout(e);
      }
      throw GeocodingException.externalFailure("The geocoding provider could not be reached", e);
    } catch (RestClientException e) {
      throw GeocodingException.invalidResponse("The geocoding provider returned an invalid response", e);
    }
  }

  private GeocodingResultView toResultView(NominatimSearchResult result) {
    NominatimAddress address = result.address();

    return new GeocodingResultView(
      result.displayName(),
      parseCoordinate(result.latitude(), "latitude"),
      parseCoordinate(result.longitude(), "longitude"),
      address != null ? normalizeNullable(address.country()) : null,
      address != null ? extractProvince(address) : null,
      address != null ? extractCity(address) : null,
      address != null ? normalizeNullable(address.postalCode()) : null,
      getProvider()
    );
  }

  private Double parseCoordinate(String rawValue, String fieldName) {
    if (!StringUtils.hasText(rawValue)) {
      return null;
    }

    try {
      double parsed = Double.parseDouble(rawValue.trim());
      if ("latitude".equals(fieldName) && (parsed < -90 || parsed > 90)) {
        throw new IllegalArgumentException("Invalid latitude range");
      }
      if ("longitude".equals(fieldName) && (parsed < -180 || parsed > 180)) {
        throw new IllegalArgumentException("Invalid longitude range");
      }
      return parsed;
    } catch (RuntimeException e) {
      throw new IllegalStateException("Invalid " + fieldName + " value returned by the geocoding provider", e);
    }
  }

  private String extractProvince(NominatimAddress address) {
    return firstNonBlank(
      address.state(),
      address.province(),
      address.region(),
      address.county()
    );
  }

  private String extractCity(NominatimAddress address) {
    return firstNonBlank(
      address.city(),
      address.town(),
      address.village(),
      address.municipality(),
      address.county()
    );
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (StringUtils.hasText(value)) {
        return value.trim();
      }
    }

    return null;
  }

  private String normalizeNullable(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  private int normalizedLimit() {
    return Math.max(1, Math.min(geocodingProperties.getLimit(), MAX_SAFE_LIMIT));
  }

  private void addQueryParamIfPresent(UriComponentsBuilder builder, String key, String value) {
    if (StringUtils.hasText(value)) {
      builder.queryParam(key, value.trim());
    }
  }

  private boolean isTimeout(ResourceAccessException exception) {
    Throwable current = exception;

    while (current != null) {
      if (current instanceof SocketTimeoutException) {
        return true;
      }

      String simpleName = current.getClass().getSimpleName();
      if (simpleName.contains("Timeout")) {
        return true;
      }

      current = current.getCause();
    }

    return false;
  }

  private String trimTrailingSlash(String value) {
    if (value == null) {
      return "";
    }

    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
