package es.udc.tfg.fios_rest.common.security;

import es.udc.tfg.fios_rest.common.util.web.SecurityErrorWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
  private static final String FORBIDDEN_MESSAGE = "No tienes permiso para hacer esta acción.";

  private final SecurityErrorWriter securityErrorWriter;

  public MyAccessDeniedHandler(SecurityErrorWriter securityErrorWriter) {
    this.securityErrorWriter = securityErrorWriter;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    securityErrorWriter.write(
      request,
      response,
      HttpStatus.FORBIDDEN,
      "ACCESS_DENIED",
      FORBIDDEN_MESSAGE
    );
  }
}
