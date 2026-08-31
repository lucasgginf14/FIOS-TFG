package es.udc.tfg.fios_rest.integration.ai.searchparser.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AiSearchParserException extends Exception {

  private final HttpStatus status;

  private AiSearchParserException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public static AiSearchParserException disabled() {
    return new AiSearchParserException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "AI search parser integration is disabled",
      null
    );
  }

  public static AiSearchParserException missingApiKey() {
    return new AiSearchParserException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "AI search parser API key is not configured",
      null
    );
  }

  public static AiSearchParserException unsupportedProvider(String provider) {
    return new AiSearchParserException(
      HttpStatus.SERVICE_UNAVAILABLE,
      "AI search parser provider is not supported: " + provider,
      null
    );
  }

  public static AiSearchParserException timeout(Throwable cause) {
    return new AiSearchParserException(
      HttpStatus.GATEWAY_TIMEOUT,
      "AI search parser did not respond in time",
      cause
    );
  }

  public static AiSearchParserException externalHttp(HttpStatusCode status, String message, Throwable cause) {
    return new AiSearchParserException(
      HttpStatus.BAD_GATEWAY,
      "AI search parser provider returned an HTTP error" + formatDetail(message, status.value()),
      cause
    );
  }

  public static AiSearchParserException invalidResponse(String message, Throwable cause) {
    return new AiSearchParserException(
      HttpStatus.BAD_GATEWAY,
      message,
      cause
    );
  }

  public static AiSearchParserException externalFailure(String message, Throwable cause) {
    return new AiSearchParserException(
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
