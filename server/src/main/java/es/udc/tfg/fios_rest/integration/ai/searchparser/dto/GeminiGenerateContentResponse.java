package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiGenerateContentResponse(
  List<GeminiCandidate> candidates
) {
}
