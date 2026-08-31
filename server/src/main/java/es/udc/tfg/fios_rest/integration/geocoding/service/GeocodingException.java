package es.udc.tfg.fios_rest.integration.geocoding.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class GeocodingException extends Exception {

  private final HttpStatus status;

  private GeocodingException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public static GeocodingException timeout(Throwable cause) {
    return new GeocodingException(
      HttpStatus.GATEWAY_TIMEOUT,
      "The geocoding provider did not respond in time",
      cause
    );
  }

  public static GeocodingException externalHttp(HttpStatusCode status, String message, Throwable cause) {
    return new GeocodingException(
      HttpStatus.BAD_GATEWAY,
      "The geocoding provider returned an HTTP error" + formatDetail(message, status.value()),
      cause
    );
  }

  public static GeocodingException invalidResponse(String message, Throwable cause) {
    return new GeocodingException(
      HttpStatus.BAD_GATEWAY,
      message,
      cause
    );
  }

  public static GeocodingException externalFailure(String message, Throwable cause) {
    return new GeocodingException(
      HttpStatus.BAD_GATEWAY,
      message,
      cause
    );
  }

  public HttpStatus getStatus() {
    return status;
  }

  private static String formatDetail(String message, int statusCode) {
    return " (" + statusCode + ")";
  }
}
