package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiSearchParseRequest(
  @NotBlank
  @Size(max = 1000)
  String text
) {
}
