package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import java.time.LocalDate;
import java.util.List;

public record AiSearchParserContext(
  LocalDate currentDate,
  String timezone,
  List<String> allowedIntents,
  List<String> allowedNeedTypes,
  List<String> allowedSpaceTypes
) {
}
