package es.udc.tfg.fios_rest.common.util.web;

import es.udc.tfg.fios_rest.common.exceptions.model.EventAlreadyPurchasedException;
import es.udc.tfg.fios_rest.common.exceptions.model.ModelException;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.model.SpaceEquipmentAlreadyExistsException;
import es.udc.tfg.fios_rest.common.exceptions.model.UserEmailExistsException;
import es.udc.tfg.fios_rest.common.exceptions.model.UserPhoneExistsException;
import es.udc.tfg.fios_rest.common.exceptions.web.AccountDisabledException;
import es.udc.tfg.fios_rest.common.exceptions.web.CredentialsAreNotValidException;
import es.udc.tfg.fios_rest.common.exceptions.web.ResourceException;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;
import es.udc.tfg.fios_rest.integration.geocoding.service.GeocodingException;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
  private static final String INTERNAL_SERVER_ERROR_MESSAGE =
    "Ha ocurrido un problema inesperado. Int\u00e9ntalo de nuevo m\u00e1s tarde.";
  private static final String BAD_REQUEST_MESSAGE =
    "Revisa los datos introducidos. Hay algo que no parece correcto.";
  private static final String FORBIDDEN_MESSAGE =
    "No tienes permiso para hacer esta acci\u00f3n.";
  private static final String NOT_FOUND_MESSAGE =
    "No encontramos lo que intentas abrir. Puede que ya no est\u00e9 disponible.";
  private static final String BAD_CREDENTIALS_MESSAGE =
    "El email o la contrase\u00f1a no son correctos.";
  private static final String ACCOUNT_DISABLED_MESSAGE =
    "Esta cuenta est\u00e1 desactivada. Contacta con administraci\u00f3n si crees que es un error.";
  private static final String AUTHENTICATION_REQUIRED_MESSAGE =
    "Debes iniciar sesi\u00f3n para continuar.";
  private static final String MALFORMED_JSON_MESSAGE =
    "No pudimos leer la informaci\u00f3n enviada. Revisa los datos e int\u00e9ntalo otra vez.";
  private static final String CONFLICT_MESSAGE =
    "No se pudo guardar porque ya existe un dato igual o relacionado.";
  private static final String INVALID_FILE_MESSAGE =
    "El archivo enviado no es v\u00e1lido.";
  private static final String FILE_TOO_LARGE_MESSAGE =
    "El archivo supera el tama\u00f1o m\u00e1ximo permitido.";

  private final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDTO> notFoundExceptionHandler(NotFoundException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", NOT_FOUND_MESSAGE, request);
  }

  @ExceptionHandler(CredentialsAreNotValidException.class)
  public ResponseEntity<ErrorDTO> badCredentialsExceptionHandler(Exception e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", BAD_CREDENTIALS_MESSAGE, request);
  }

  @ExceptionHandler({AccountDisabledException.class, DisabledException.class})
  public ResponseEntity<ErrorDTO> accountDisabledExceptionHandler(Exception e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED", ACCOUNT_DISABLED_MESSAGE, request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorDTO> authenticationExceptionHandler(AuthenticationException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", AUTHENTICATION_REQUIRED_MESSAGE, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorDTO> accessDeniedExceptionHandler(AccessDeniedException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", FORBIDDEN_MESSAGE, request);
  }

  @ExceptionHandler({UserEmailExistsException.class, UserPhoneExistsException.class})
  public ResponseEntity<ErrorDTO> duplicateUserExceptionHandler(ModelException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.CONFLICT, resolveBusinessCode(e.getMessage()), resolveClientMessage(e.getMessage()), request);
  }

  @ExceptionHandler(OperationNotAllowed.class)
  public ResponseEntity<ErrorDTO> operationNotAllowedExceptionHandler(OperationNotAllowed e, HttpServletRequest request) {
    logger.info(e.getMessage());
    HttpStatus status = resolveOperationStatus(e.getMessage());
    return error(status, resolveBusinessCode(e.getMessage()), resolveClientMessage(e.getMessage()), request);
  }

  @ExceptionHandler(EventAlreadyPurchasedException.class)
  public ResponseEntity<ErrorDTO> eventAlreadyPurchasedExceptionHandler(
    EventAlreadyPurchasedException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    return error(HttpStatus.CONFLICT, "EVENT_ALREADY_RESERVED", "Ya tienes una reserva activa para este evento.", request);
  }

  @ExceptionHandler(SpaceEquipmentAlreadyExistsException.class)
  public ResponseEntity<ErrorDTO> spaceEquipmentAlreadyExistsExceptionHandler(
    SpaceEquipmentAlreadyExistsException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    return error(
      HttpStatus.CONFLICT,
      "SPACE_EQUIPMENT_ALREADY_EXISTS",
      "Este equipamiento ya está asociado al espacio.",
      request
    );
  }

  @ExceptionHandler({ModelException.class, ResourceException.class})
  public ResponseEntity<ErrorDTO> badRequestExceptionHandler(Exception e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.BAD_REQUEST, resolveBusinessCode(e.getMessage()), resolveClientMessage(e.getMessage()), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDTO> illegalArgumentExceptionHandler(IllegalArgumentException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    HttpStatus status = resolveOperationStatus(e.getMessage());
    return error(status, resolveBusinessCode(e.getMessage()), resolveClientMessage(e.getMessage()), request);
  }

  @ExceptionHandler(GeocodingException.class)
  public ResponseEntity<ErrorDTO> geocodingExceptionHandler(GeocodingException e, HttpServletRequest request) {
    logger.warn(e.getMessage(), e);
    return error(e.getStatus(), "GEOCODING_ERROR", resolveGeocodingMessage(e), request);
  }

  @ExceptionHandler(TicketmasterException.class)
  public ResponseEntity<ErrorDTO> ticketmasterExceptionHandler(TicketmasterException e, HttpServletRequest request) {
    logger.warn(e.getMessage(), e);
    return error(e.getStatus(), "TICKETMASTER_ERROR", resolveTicketmasterMessage(e), request);
  }

  @ExceptionHandler(AiSearchParserException.class)
  public ResponseEntity<ErrorDTO> aiSearchParserExceptionHandler(AiSearchParserException e, HttpServletRequest request) {
    logger.warn(e.getMessage(), e);
    return error(e.getStatus(), "AI_SEARCH_PARSER_ERROR", resolveAiSearchParserMessage(e), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDTO> methodArgumentNotValidExceptionHandler(
    MethodArgumentNotValidException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    List<FieldErrorDTO> fieldErrors = e.getBindingResult().getAllErrors().stream()
      .map(this::toFieldError)
      .sorted(Comparator.comparing(FieldErrorDTO::field))
      .toList();

    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", BAD_REQUEST_MESSAGE, request, fieldErrors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDTO> httpMessageNotReadableExceptionHandler(
    HttpMessageNotReadableException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    return error(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", MALFORMED_JSON_MESSAGE, request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorDTO> constraintViolationExceptionHandler(
    ConstraintViolationException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    List<FieldErrorDTO> fieldErrors = e.getConstraintViolations().stream()
      .map(this::toFieldError)
      .sorted(Comparator.comparing(FieldErrorDTO::field))
      .toList();

    return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", BAD_REQUEST_MESSAGE, request, fieldErrors);
  }

  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorDTO> requestParameterExceptionHandler(Exception e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_PARAMETER", BAD_REQUEST_MESSAGE, request);
  }

  @ExceptionHandler({MultipartException.class, HttpMediaTypeNotSupportedException.class})
  public ResponseEntity<ErrorDTO> invalidFileExceptionHandler(Exception e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.BAD_REQUEST, "INVALID_FILE", INVALID_FILE_MESSAGE, request);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorDTO> fileTooLargeExceptionHandler(MaxUploadSizeExceededException e, HttpServletRequest request) {
    logger.info(e.getMessage());
    return error(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", FILE_TOO_LARGE_MESSAGE, request);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDTO> dataIntegrityViolationExceptionHandler(
    DataIntegrityViolationException e,
    HttpServletRequest request
  ) {
    logger.warn(e.getMessage(), e);
    return error(HttpStatus.CONFLICT, "DATA_CONFLICT", CONFLICT_MESSAGE, request);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorDTO> methodNotSupportedExceptionHandler(
    HttpRequestMethodNotSupportedException e,
    HttpServletRequest request
  ) {
    logger.info(e.getMessage());
    return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Metodo HTTP no permitido.", request);
  }

  @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class})
  public void clientDisconnectedExceptionHandler(Exception e, HttpServletRequest request) {
    logger.debug("Client disconnected before the response could be completed for {}", request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> exceptionHandler(Exception e, HttpServletRequest request) {
    logger.error("Unhandled server error", e);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", INTERNAL_SERVER_ERROR_MESSAGE, request);
  }

  private ResponseEntity<ErrorDTO> error(
    HttpStatus status,
    String code,
    String message,
    HttpServletRequest request
  ) {
    return error(status, code, message, request, List.of());
  }

  private ResponseEntity<ErrorDTO> error(
    HttpStatus status,
    String code,
    String message,
    HttpServletRequest request,
    List<FieldErrorDTO> fieldErrors
  ) {
    return ResponseEntity
      .status(status)
      .body(ErrorDTO.of(status, code, message, request.getRequestURI(), fieldErrors));
  }

  private FieldErrorDTO toFieldError(ObjectError error) {
    String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
    String rawCode = firstNonBlank(error.getDefaultMessage(), error.getCode(), "validation.invalid");
    String code = resolveValidationCode(field, rawCode, error.getCode());
    return new FieldErrorDTO(normalizeFieldName(field), code, resolveValidationMessage(code));
  }

  private FieldErrorDTO toFieldError(ConstraintViolation<?> violation) {
    String field = normalizeConstraintPath(violation.getPropertyPath().toString());
    String constraintName = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    String rawCode = firstNonBlank(violation.getMessage(), constraintName, "validation.invalid");
    String code = resolveValidationCode(field, rawCode, constraintName);
    return new FieldErrorDTO(normalizeFieldName(field), code, resolveValidationMessage(code));
  }

  private String normalizeConstraintPath(String path) {
    int lastDot = path.lastIndexOf('.');
    return lastDot >= 0 ? path.substring(lastDot + 1) : path;
  }

  private String normalizeFieldName(String field) {
    if (field == null || field.isBlank()) {
      return "request";
    }

    if (field.startsWith("valid") && field.length() > 5) {
      return Character.toLowerCase(field.charAt(5)) + field.substring(6);
    }

    if (field.endsWith("Valid") && field.length() > 5) {
      return field.substring(0, field.length() - 5);
    }

    return field;
  }

  private String resolveValidationCode(String field, String rawCode, String constraintName) {
    String normalizedField = normalize(field);
    String normalizedRawCode = normalize(rawCode);
    String normalizedConstraint = normalize(constraintName);

    if (normalizedRawCode.contains("validation.phone") || normalizedField.contains("phone")) {
      return "INVALID_PHONE";
    }

    if (normalizedRawCode.contains("validation.timerange") || normalizedField.contains("timerange")) {
      return "INVALID_TIME_RANGE";
    }

    if (normalizedRawCode.contains("validation.daterange") || normalizedField.contains("daterange")) {
      return "INVALID_DATE_RANGE";
    }

    if (normalizedRawCode.contains("validation.coordinates") || normalizedField.contains("coordinates")) {
      return "INVALID_COORDINATES";
    }

    if (normalizedConstraint.contains("notblank")
      || normalizedConstraint.contains("notnull")
      || normalizedConstraint.contains("notempty")) {
      return "FIELD_REQUIRED";
    }

    if (normalizedConstraint.contains("email") || normalizedField.contains("email")) {
      return "INVALID_EMAIL";
    }

    if (normalizedConstraint.contains("size")) {
      return "INVALID_LENGTH";
    }

    if (normalizedConstraint.contains("min")
      || normalizedConstraint.contains("max")
      || normalizedConstraint.contains("positive")
      || normalizedConstraint.contains("decimal")) {
      if (normalizedField.contains("latitude") || normalizedField.contains("longitude")) {
        return "INVALID_COORDINATES";
      }
      return "INVALID_NUMBER";
    }

    if (normalizedConstraint.contains("past")
      || normalizedConstraint.contains("future")
      || normalizedField.contains("date")) {
      return "INVALID_DATE";
    }

    return "INVALID_VALUE";
  }

  private String resolveValidationMessage(String code) {
    return switch (code) {
      case "FIELD_REQUIRED" -> "Completa este campo.";
      case "INVALID_EMAIL" -> "Escribe un email válido.";
      case "INVALID_PHONE" -> "Introduce un teléfono válido.";
      case "INVALID_LENGTH" -> "Revisa la longitud de este campo.";
      case "INVALID_NUMBER" -> "Revisa este número.";
      case "INVALID_DATE" -> "La fecha elegida no es válida.";
      case "INVALID_TIME_RANGE" -> "La hora de inicio debe ser anterior a la hora de fin.";
      case "INVALID_DATE_RANGE" -> "La fecha de inicio debe ser anterior o igual a la fecha de fin.";
      case "INVALID_COORDINATES" -> "Revisa las coordenadas introducidas.";
      default -> "Revisa este campo.";
    };
  }

  private HttpStatus resolveOperationStatus(String message) {
    String normalized = normalize(message);

    if (normalized.contains("cannot manage")
      || normalized.contains("cannot access")
      || normalized.contains("cannot view")
      || normalized.contains("cannot submit")
      || normalized.contains("cannot publish")
      || normalized.contains("cannot mutate")
      || normalized.contains("cannot remove their own")
      || normalized.contains("cannot deactivate their own")
      || normalized.contains("not an active member")
      || normalized.contains("only the reservation owner")
      || normalized.contains("only the ticket reservation owner")
      || normalized.contains("only the event creator")
      || normalized.contains("administrators cannot")) {
      return HttpStatus.FORBIDDEN;
    }

    if (normalized.contains("overlap")
      || normalized.contains("already")
      || normalized.contains("not available")
      || normalized.contains("no tickets")
      || normalized.contains("inactive")
      || normalized.contains("closed")
      || normalized.contains("only pending")
      || normalized.contains("only completed")
      || normalized.contains("already started")
      || normalized.contains("already ended")
      || normalized.contains("already cancelled")
      || normalized.contains("invalid reservation state")
      || normalized.contains("active leader")
      || normalized.contains("pending or accepted reservations")
      || normalized.contains("not published")
      || normalized.contains("only published")
      || normalized.contains("cannot be purchased")
      || normalized.contains("cannot accept")
      || normalized.contains("capacity cannot be lower")
      || normalized.contains("active ticket reservations")) {
      return HttpStatus.CONFLICT;
    }

    return HttpStatus.BAD_REQUEST;
  }

  private String resolveBusinessCode(String message) {
    String normalized = normalize(message);

    if (containsAll(normalized, "email", "already exists")) {
      return "EMAIL_ALREADY_EXISTS";
    }

    if (containsAll(normalized, "phone", "already exists")) {
      return "PHONE_ALREADY_EXISTS";
    }

    if (containsAll(normalized, "email", "not valid")) {
      return "INVALID_EMAIL";
    }

    if (containsAll(normalized, "email", "cannot be changed")) {
      return "BAD_REQUEST";
    }

    if (normalized.contains("tamano maximo")
      || normalized.contains("tamaño máximo")
      || normalized.contains("supera el tamano")
      || normalized.contains("supera el tamaño")) {
      return "FILE_TOO_LARGE";
    }

    if (normalized.contains("imagen")
      || normalized.contains("image")
      || normalized.contains("archivo")
      || normalized.contains("extension")) {
      return "INVALID_FILE";
    }

    if (containsAll(normalized, "current password", "incorrect")) {
      return "CURRENT_PASSWORD_INCORRECT";
    }

    if (containsAll(normalized, "password", "do not match")) {
      return "PASSWORD_MISMATCH";
    }

    if (containsAll(normalized, "password", "letter", "number")) {
      return "PASSWORD_WEAK";
    }

    if (containsAll(normalized, "start time", "before", "end time")) {
      return "INVALID_TIME_RANGE";
    }

    if (normalized.contains("overlaps") || normalized.contains("not available")) {
      return "TIME_NOT_AVAILABLE";
    }

    if (normalized.contains("not found")) {
      return "RESOURCE_NOT_FOUND";
    }

    if (normalized.contains("cannot manage")
      || normalized.contains("cannot access")
      || normalized.contains("cannot view")
      || normalized.contains("cannot submit")
      || normalized.contains("cannot publish")
      || normalized.contains("cannot mutate")
      || normalized.contains("cannot update")
      || normalized.contains("cannot remove")
      || normalized.contains("cannot deactivate")
      || normalized.contains("not an active member")
      || normalized.contains("only the reservation owner")
      || normalized.contains("only the ticket reservation owner")
      || normalized.contains("only the event creator")
      || normalized.contains("administrators cannot")) {
      return "ACCESS_DENIED";
    }

    if (normalized.contains("already exists")
      || normalized.contains("already saved")
      || normalized.contains("already reserved")
      || normalized.contains("already has")
      || normalized.contains("already an active member")) {
      return "DUPLICATE_RESOURCE";
    }

    if (normalized.contains("no tickets available")) {
      return "NO_TICKETS_AVAILABLE";
    }

    if (normalized.contains("cannot be")
      || normalized.contains("only pending")
      || normalized.contains("only completed")
      || normalized.contains("already started")
      || normalized.contains("already ended")
      || normalized.contains("already cancelled")
      || normalized.contains("active leader")
      || normalized.contains("pending or accepted reservations")
      || normalized.contains("inactive")
      || normalized.contains("closed")
      || normalized.contains("endpoint")
      || normalized.contains("invalid reservation state")
      || normalized.contains("not published")
      || normalized.contains("only published")
      || normalized.contains("cannot be purchased")
      || normalized.contains("cannot accept")
      || normalized.contains("active ticket reservations")
      || normalized.contains("capacity cannot be lower")) {
      return "ACTION_NOT_AVAILABLE";
    }

    if (normalized.contains("latitude")
      || normalized.contains("longitude")
      || normalized.contains("geocoding")) {
      return "INVALID_LOCATION";
    }

    return "BAD_REQUEST";
  }

  private String resolveClientMessage(String message) {
    String normalized = normalize(message);

    if (containsAll(normalized, "email", "already exists")) {
      return "Ya existe una cuenta con ese email.";
    }

    if (containsAll(normalized, "phone", "already exists")) {
      return "Ya existe una cuenta con ese teléfono.";
    }

    if (containsAll(normalized, "email", "not valid")) {
      return "Escribe un email válido.";
    }

    if (containsAll(normalized, "email", "cannot be changed")) {
      return "No puedes cambiar el email desde el perfil.";
    }

    if (normalized.contains("tamano maximo")
      || normalized.contains("tamaño máximo")
      || normalized.contains("supera el tamano")
      || normalized.contains("supera el tamaño")) {
      return FILE_TOO_LARGE_MESSAGE;
    }

    if (normalized.contains("imagen")
      || normalized.contains("image")
      || normalized.contains("archivo")
      || normalized.contains("extension")) {
      return INVALID_FILE_MESSAGE;
    }

    if (containsAll(normalized, "administrators", "public user flow")) {
      return "Los administradores solo pueden ver esta pantalla.";
    }

    if (containsAll(normalized, "reservation price", "could not be calculated")) {
      return "No se pudo calcular el importe de la reserva. Revisa la tarifa de esa franja.";
    }

    if (containsAll(normalized, "custom availability", "price")) {
      return "Incluye un precio para la disponibilidad personalizada.";
    }

    if (containsAll(normalized, "birth date", "future")) {
      return "La fecha de nacimiento no puede estar en el futuro.";
    }

    if (containsAll(normalized, "current password", "incorrect")) {
      return "La contraseña actual no es correcta.";
    }

    if (containsAll(normalized, "password", "do not match")) {
      return "Las contraseñas no coinciden.";
    }

    if (containsAll(normalized, "password", "letter", "number")) {
      return "La contraseña debe tener al menos una letra y un número.";
    }

    if (containsAll(normalized, "new password", "different")) {
      return "La nueva contraseña debe ser distinta de la actual.";
    }

    if (normalized.contains("obligatory") || normalized.contains("required") || normalized.contains("empty")) {
      return "Completa los campos obligatorios antes de continuar.";
    }

    if (containsAll(normalized, "start time", "before", "end time")) {
      return "La hora de inicio debe ser anterior a la hora de fin.";
    }

    if (normalized.contains("past") || normalized.contains("today or later")) {
      return "La fecha elegida no es válida.";
    }

    if (normalized.contains("greater than zero")
      || normalized.contains("cannot be negative")
      || normalized.contains("must be between")) {
      return "Revisa los números introducidos. Alguno está fuera del rango permitido.";
    }

    if (containsAll(normalized, "attendees count", "capacity")) {
      return "El número de asistentes supera la capacidad del espacio.";
    }

    if (normalized.contains("not available") || normalized.contains("overlaps")) {
      return "Ese horario no está disponible. Elige otra franja.";
    }

    if (normalized.contains("no tickets available")) {
      return "No quedan entradas disponibles para este evento.";
    }

    if (normalized.contains("already reserved")) {
      return "Ya tienes una reserva activa para este evento.";
    }

    if (normalized.contains("capacity cannot be lower")) {
      return "El aforo no puede ser menor que las reservas activas.";
    }

    if (normalized.contains("already exists")
      || normalized.contains("already saved")
      || normalized.contains("already has")
      || normalized.contains("already an active member")) {
      return "Ya existe un elemento igual o relacionado.";
    }

    if (containsAll(normalized, "pending or accepted reservations")) {
      return "Solo puedes enviar mensajes en reservas pendientes o aceptadas.";
    }

    if (containsAll(normalized, "active leader")) {
      return "La banda debe tener al menos una persona líder activa.";
    }

    if (normalized.contains("cannot manage")
      || normalized.contains("cannot access")
      || normalized.contains("cannot view")
      || normalized.contains("cannot submit")
      || normalized.contains("not an active member")
      || normalized.contains("only the reservation owner")
      || normalized.contains("only the ticket reservation owner")
      || normalized.contains("only the event creator")) {
      return "No puedes hacer esta acción con esta cuenta.";
    }

    if (normalized.contains("cannot be")
      || normalized.contains("only pending")
      || normalized.contains("already started")
      || normalized.contains("already ended")
      || normalized.contains("already cancelled")
      || normalized.contains("inactive")
      || normalized.contains("closed")
      || normalized.contains("endpoint")
      || normalized.contains("only published")
      || normalized.contains("cannot accept")
      || normalized.contains("active ticket reservations")) {
      return "Esta acción no está disponible ahora mismo.";
    }

    if (normalized.contains("latitude")
      || normalized.contains("longitude")
      || normalized.contains("geocoding")) {
      return "No pudimos comprobar la ubicación. Revisa la dirección o las coordenadas.";
    }

    return BAD_REQUEST_MESSAGE;
  }

  private String resolveTicketmasterMessage(TicketmasterException e) {
    String normalized = normalize(e.getMessage());

    if (normalized.contains("api key")) {
      return "La conexión con Ticketmaster no está lista. Avísale a la persona responsable.";
    }

    if (normalized.contains("disabled")) {
      return "La conexión con Ticketmaster está desactivada.";
    }

    if (normalized.contains("did not respond in time")) {
      return "Ticketmaster está tardando demasiado. Prueba de nuevo en unos segundos.";
    }

    if (normalized.contains("not found")) {
      return "No encontramos ese evento en Ticketmaster.";
    }

    return "No se pudo consultar Ticketmaster. Revisa los filtros e inténtalo otra vez.";
  }

  private String resolveAiSearchParserMessage(AiSearchParserException e) {
    String normalized = normalize(e.getMessage());

    if (normalized.contains("disabled") || normalized.contains("api key") || normalized.contains("not supported")) {
      return "La búsqueda inteligente no está disponible ahora mismo.";
    }

    if (normalized.contains("did not respond in time")) {
      return "La búsqueda inteligente está tardando demasiado. Prueba de nuevo en unos segundos.";
    }

    return "No pudimos interpretar la búsqueda. Prueba con una frase más sencilla.";
  }

  private String resolveGeocodingMessage(GeocodingException e) {
    String normalized = normalize(e.getMessage());

    if (normalized.contains("did not respond in time")) {
      return "La comprobación de ubicación está tardando demasiado. Prueba de nuevo en unos segundos.";
    }

    return "No pudimos comprobar la ubicación. Revisa la dirección o las coordenadas.";
  }

  private String normalize(String message) {
    return message == null ? "" : message.toLowerCase();
  }

  private boolean containsAll(String value, String... fragments) {
    for (String fragment : fragments) {
      if (!value.contains(fragment)) {
        return false;
      }
    }

    return true;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }

    return "";
  }
}
