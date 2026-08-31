package es.udc.tfg.fios_rest.common.util.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SecurityErrorWriter {

  private final ObjectMapper objectMapper;

  public SecurityErrorWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void write(
    HttpServletRequest request,
    HttpServletResponse response,
    HttpStatus status,
    String code,
    String message
  ) throws IOException {
    response.setStatus(status.value());
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
      response.getWriter(),
      ErrorDTO.of(status, code, message, request.getRequestURI())
    );
  }
}
