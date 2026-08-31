package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import java.util.List;

public record GeminiContent(
  List<GeminiPart> parts
) {
}
