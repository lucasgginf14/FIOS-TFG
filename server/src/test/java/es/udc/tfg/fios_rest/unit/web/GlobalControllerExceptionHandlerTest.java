package es.udc.tfg.fios_rest.unit.web;

import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.web.AccountDisabledException;
import es.udc.tfg.fios_rest.common.util.web.ErrorDTO;
import es.udc.tfg.fios_rest.common.util.web.GlobalControllerExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalControllerExceptionHandlerTest {

  private final GlobalControllerExceptionHandler handler = new GlobalControllerExceptionHandler();

  @Test
  void genericExceptionDoesNotExposeInternalDetails() {
    RuntimeException exception = new RuntimeException("SQL grammar error near table users");

    ResponseEntity<ErrorDTO> response = handler.exceptionHandler(exception, request("/api/test"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(500);
    assertThat(response.getBody().getCode()).isEqualTo("INTERNAL_ERROR");
    assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    assertThat(response.getBody().getMessage()).isEqualTo(
      "Ha ocurrido un problema inesperado. Int\u00e9ntalo de nuevo m\u00e1s tarde."
    );
    assertThat(response.getBody().getMessage()).doesNotContain("SQL", "users");
  }

  @Test
  void controlledPermissionExceptionReturnsForbidden() {
    OperationNotAllowed exception = new OperationNotAllowed("The user cannot manage this reservation");

    ResponseEntity<ErrorDTO> response = handler.operationNotAllowedExceptionHandler(exception, request("/api/reservations/1"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(403);
    assertThat(response.getBody().getCode()).isEqualTo("ACCESS_DENIED");
    assertThat(response.getBody().getMessage()).isEqualTo("No puedes hacer esta acción con esta cuenta.");
  }

  @Test
  void controlledConflictExceptionReturnsConflict() {
    OperationNotAllowed exception = new OperationNotAllowed("The requested time range overlaps with another active reservation");

    ResponseEntity<ErrorDTO> response = handler.operationNotAllowedExceptionHandler(exception, request("/api/reservations"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(409);
    assertThat(response.getBody().getCode()).isEqualTo("TIME_NOT_AVAILABLE");
    assertThat(response.getBody().getMessage()).isEqualTo("Ese horario no está disponible. Elige otra franja.");
  }

  @Test
  void disabledAccountReturnsForbiddenWithoutCredentialLeak() {
    ResponseEntity<ErrorDTO> response = handler.accountDisabledExceptionHandler(
      new AccountDisabledException(),
      request("/api/account/login")
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getCode()).isEqualTo("ACCOUNT_DISABLED");
    assertThat(response.getBody().getMessage()).doesNotContain("Bad Credentials");
  }

  private MockHttpServletRequest request(String path) {
    return new MockHttpServletRequest("GET", path);
  }
}
