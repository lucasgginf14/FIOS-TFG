package es.udc.tfg.fios_rest.integration.ticketmaster.client;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventItem;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventSearchRequest;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterSearchResponse;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TicketmasterDiscoveryClient implements TicketmasterClient {

  private static final int MAX_SAFE_SIZE = 50;
  private static final String MUSIC_SEGMENT = "Music";
  private static final String ALL_LOCALES = "*";

  private final RestTemplate restTemplate;
  private final Properties.Ticketmaster ticketmasterProperties;

  public TicketmasterDiscoveryClient(RestTemplateBuilder restTemplateBuilder, Properties properties) {
    this.ticketmasterProperties = properties.getTicketmaster();
    this.restTemplate = restTemplateBuilder
      .setConnectTimeout(Duration.ofMillis(this.ticketmasterProperties.getTimeout()))
      .setReadTimeout(Duration.ofMillis(this.ticketmasterProperties.getTimeout()))
      .build();
  }

  @Override
  public List<TicketmasterEventItem> searchEvents(TicketmasterEventSearchRequest request) throws TicketmasterException {
    ensureEnabledAndConfigured();

    URI uri = UriComponentsBuilder
      .fromUriString(trimTrailingSlash(ticketmasterProperties.getBaseUrl()))
      .path("/events.json")
      .queryParam("apikey", ticketmasterProperties.getApiKey().trim())
      .queryParam("segmentName", MUSIC_SEGMENT)
      .queryParam("locale", ALL_LOCALES)
      .queryParam("size", normalizedSize(request.size()))
      .queryParam("sort", "date,asc")
      .queryParamIfPresent("countryCode", optionalText(normalizedCountryCode(request.countryCode())))
      .queryParamIfPresent("city", optionalText(request.city()))
      .queryParamIfPresent("keyword", optionalText(request.keyword()))
      .queryParamIfPresent("classificationName", optionalText(request.musicalGenre()))
      .queryParamIfPresent("startDateTime", optionalStartDate(request.startDate()))
      .queryParamIfPresent("endDateTime", optionalEndDate(request.endDate()))
      .build()
      .encode()
      .toUri();

    try {
      ResponseEntity<TicketmasterSearchResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        null,
        TicketmasterSearchResponse.class
      );

      TicketmasterSearchResponse body = response.getBody();
      if (body == null || body.embedded() == null || body.embedded().events() == null) {
        return Collections.emptyList();
      }

      return body.embedded().events();
    } catch (HttpStatusCodeException e) {
      throw TicketmasterException.externalHttp(e.getStatusCode(), null, e);
    } catch (ResourceAccessException e) {
      if (isTimeout(e)) {
        throw TicketmasterException.timeout(e);
      }
      throw TicketmasterException.externalFailure("Ticketmaster could not be reached", e);
    } catch (RestClientException e) {
      throw TicketmasterException.invalidResponse("Ticketmaster returned an invalid response", e);
    }
  }

  @Override
  public TicketmasterEventItem findEventByExternalId(String externalId) throws TicketmasterException {
    ensureEnabledAndConfigured();

    URI uri = UriComponentsBuilder
      .fromUriString(trimTrailingSlash(ticketmasterProperties.getBaseUrl()))
      .path("/events/{externalId}.json")
      .queryParam("apikey", ticketmasterProperties.getApiKey().trim())
      .queryParam("locale", ALL_LOCALES)
      .buildAndExpand(externalId.trim())
      .encode()
      .toUri();

    try {
      ResponseEntity<TicketmasterEventItem> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        null,
        TicketmasterEventItem.class
      );

      TicketmasterEventItem body = response.getBody();
      if (body == null || !StringUtils.hasText(body.id())) {
        throw TicketmasterException.invalidResponse("Ticketmaster returned an empty event response", null);
      }

      return body;
    } catch (HttpStatusCodeException e) {
      throw TicketmasterException.externalHttp(e.getStatusCode(), null, e);
    } catch (ResourceAccessException e) {
      if (isTimeout(e)) {
        throw TicketmasterException.timeout(e);
      }
      throw TicketmasterException.externalFailure("Ticketmaster could not be reached", e);
    } catch (RestClientException e) {
      throw TicketmasterException.invalidResponse("Ticketmaster returned an invalid response", e);
    }
  }

  private void ensureEnabledAndConfigured() throws TicketmasterException {
    if (!ticketmasterProperties.isEnabled()) {
      throw TicketmasterException.disabled();
    }

    if (!StringUtils.hasText(ticketmasterProperties.getApiKey())) {
      throw TicketmasterException.missingApiKey();
    }
  }

  private String normalizedCountryCode(String countryCode) {
    String value = StringUtils.hasText(countryCode)
      ? countryCode.trim()
      : ticketmasterProperties.getDefaultCountryCode();

    return value == null ? null : value.trim().toUpperCase();
  }

  private int normalizedSize(Integer requestedSize) {
    int candidate = requestedSize != null ? requestedSize : ticketmasterProperties.getDefaultSize();
    return Math.max(1, Math.min(candidate, MAX_SAFE_SIZE));
  }

  private Optional<String> optionalText(String value) {
    if (!StringUtils.hasText(value)) {
      return Optional.empty();
    }

    return Optional.of(value.trim());
  }

  private Optional<String> optionalStartDate(LocalDate date) {
    if (date == null) {
      return Optional.empty();
    }

    return Optional.of(date.atStartOfDay().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  private Optional<String> optionalEndDate(LocalDate date) {
    if (date == null) {
      return Optional.empty();
    }

    return Optional.of(
      date.atTime(LocalTime.of(23, 59, 59))
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    );
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
