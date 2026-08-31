package es.udc.tfg.fios_rest.common.util.web;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public class ErrorDTO {
  private Instant timestamp;
  private int status;
  private String error;
  private String code;
  private String message;
  private String path;
  private List<FieldErrorDTO> fieldErrors;

  public ErrorDTO() {
    this.fieldErrors = List.of();
  }

  public ErrorDTO(String message) {
    this();
    this.message = message;
  }

  public ErrorDTO(
    Instant timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    List<FieldErrorDTO> fieldErrors
  ) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.code = code;
    this.message = message;
    this.path = path;
    this.fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
  }

  public static ErrorDTO of(HttpStatus status, String code, String message, String path) {
    return of(status, code, message, path, List.of());
  }

  public static ErrorDTO of(
    HttpStatus status,
    String code,
    String message,
    String path,
    List<FieldErrorDTO> fieldErrors
  ) {
    return new ErrorDTO(
      Instant.now(),
      status.value(),
      status.getReasonPhrase(),
      code,
      message,
      path,
      fieldErrors
    );
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<FieldErrorDTO> getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(List<FieldErrorDTO> fieldErrors) {
    this.fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
  }
}
