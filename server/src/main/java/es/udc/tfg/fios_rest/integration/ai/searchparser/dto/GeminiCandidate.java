package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiCandidate(
  GeminiContent content
) {
}
