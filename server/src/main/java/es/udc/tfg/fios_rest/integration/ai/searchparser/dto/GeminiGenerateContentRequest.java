package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiGenerateContentRequest(
  @JsonProperty("systemInstruction")
  GeminiContent systemInstruction,

  List<GeminiContent> contents,

  @JsonProperty("generationConfig")
  GeminiGenerationConfig generationConfig
) {
}
