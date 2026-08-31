package es.udc.tfg.fios_rest.integration.ticketmaster.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TicketmasterException extends Exception {

  private final HttpStatus status;

  private TicketmasterException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public static TicketmasterException disabled() {
    return new TicketmasterException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "Ticketmaster integration is disabled",
      null
    );
  }

  public static TicketmasterException missingApiKey() {
    return new TicketmasterException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "Ticketmaster API key is not configured",
      null
    );
  }

  public static TicketmasterException timeout(Throwable cause) {
    return new TicketmasterException(
      HttpStatus.GATEWAY_TIMEOUT,
      "Ticketmaster did not respond in time",
      cause
    );
  }

  public static TicketmasterException notFound(String externalId) {
    return new TicketmasterException(
      HttpStatus.NOT_FOUND,
      "Ticketmaster event not found: " + externalId,
      null
    );
  }

  public static TicketmasterException externalHttp(HttpStatusCode status, String message, Throwable cause) {
    if (status.value() == HttpStatus.NOT_FOUND.value()) {
      return new TicketmasterException(
        HttpStatus.NOT_FOUND,
        "Ticketmaster event not found",
        cause
      );
    }

    return new TicketmasterException(
      HttpStatus.BAD_GATEWAY,
      "Ticketmaster returned an HTTP error" + formatDetail(message, status.value()),
      cause
    );
  }

  public static TicketmasterException invalidResponse(String message, Throwable cause) {
    return new TicketmasterException(
      HttpStatus.BAD_GATEWAY,
      message,
      cause
    );
  }

  public static TicketmasterException externalFailure(String message, Throwable cause) {
    return new TicketmasterException(
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
