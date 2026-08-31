package es.udc.tfg.fios_rest.common.security;

import es.udc.tfg.fios_rest.common.util.web.SecurityErrorWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyUnauthorizedEntryPoint implements AuthenticationEntryPoint {
  private static final String UNAUTHORIZED_MESSAGE = "Debes iniciar sesión para continuar.";

  private final SecurityErrorWriter securityErrorWriter;

  public MyUnauthorizedEntryPoint(SecurityErrorWriter securityErrorWriter) {
    this.securityErrorWriter = securityErrorWriter;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {

    securityErrorWriter.write(
      request,
      response,
      HttpStatus.UNAUTHORIZED,
      "AUTHENTICATION_REQUIRED",
      UNAUTHORIZED_MESSAGE
    );
  }
}
