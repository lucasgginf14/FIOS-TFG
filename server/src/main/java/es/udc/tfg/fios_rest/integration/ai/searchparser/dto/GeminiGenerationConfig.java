package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiGenerationConfig(
  @JsonProperty("responseMimeType")
  String responseMimeType,

  @JsonProperty("maxOutputTokens")
  Integer maxOutputTokens,

  @JsonProperty("responseSchema")
  Object responseSchema,

  Double temperature
) {
}
