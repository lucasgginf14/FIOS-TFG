package es.udc.tfg.fios_rest.integration.ai.searchparser.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParserContext;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiContent;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiGenerateContentRequest;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiGenerateContentResponse;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiGenerationConfig;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiPart;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiSearchParsePayload;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class GeminiAiSearchParserClient implements AiSearchParserClient {

  private static final String SUPPORTED_PROVIDER = "GEMINI";
  private static final String USER_PROMPT_TEMPLATE = """
    Current date: %s
    Timezone: %s

    Create one JSON object with these exact keys:
    intent, city, province, autonomousCommunity, date, dateFrom, dateTo, startTime, endTime, peopleCount, maxBudget, spaceType, musicalGenre, needType, confidence.

    Allowed intent values: %s, UNKNOWN
    Allowed needType values: %s
    Allowed spaceType values: %s
    Allowed autonomousCommunity values: Andalucia, Aragon, Principado de Asturias, Islas Baleares, Canarias, Cantabria, Castilla-La Mancha, Castilla y Leon, Cataluna, Comunidad Valenciana, Extremadura, Galicia, Comunidad de Madrid, Region de Murcia, Comunidad Foral de Navarra, Pais Vasco, La Rioja

    Rules:
    - Return only valid JSON, without markdown.
    - Use ISO dates: yyyy-MM-dd.
    - Use date for one exact day; use dateFrom and dateTo for ranges; do not fill date when a range is intended.
    - Use 24h times: HH:mm.
    - Confidence must be a number from 0 to 1.
    - Use intent SPACE for reserving or finding rooms, rehearsal spaces, studios, classrooms or venues.
    - Use intent EVENT for concerts, festivals, jams, open mics, agenda or tickets.
    - Use intent UNKNOWN for band/group discovery searches, but still extract city and musicalGenre.
    - Rehearsal/local de ensayo/local de ensaio -> spaceType REHEARSAL_ROOM, needType REHEARSAL.
    - Studio/estudio/estudo/grabacion/gravacion -> spaceType RECORDING_STUDIO, needType RECORDING.
    - Concert venue/auditorio/sala para conciertos -> spaceType CONCERT_HALL, needType PERFORMANCE.
    - Classroom/aula/clase/taller/workshop -> spaceType CLASSROOM, needType CLASS.
    - Multipurpose/multiusos/polivalente -> spaceType MULTIPURPOSE.
    - Extract explicit city, province, autonomous community, people count, budget, genre and time range when present.
    - Put Spanish autonomous communities only in autonomousCommunity, not in city or province.
    - Normalize autonomousCommunity to one allowed value; accept common Spanish/Galician aliases such as Euskadi, Catalunya, Comunitat Valenciana, Comunidade Valenciana, Galicia/Galiza and Asturias.
    - Do not extract countries or foreign regions as autonomousCommunity.
    - Interpret relative dates using the current date above.
    - "proximos dias", "os proximos dias", "proximos días", "os próximos días", "next few days" or "vindeiros dias" means dateFrom is the current date and dateTo is current date plus 7 days.

    Search text:
    %s
    """;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final Properties.SearchParser searchParserProperties;

  public GeminiAiSearchParserClient(
    RestTemplateBuilder restTemplateBuilder,
    ObjectMapper objectMapper,
    Properties properties
  ) {
    this.objectMapper = objectMapper;
    this.searchParserProperties = properties.getAi().getSearchParser();
    this.restTemplate = restTemplateBuilder
      .setConnectTimeout(Duration.ofMillis(this.searchParserProperties.getTimeout()))
      .setReadTimeout(Duration.ofMillis(this.searchParserProperties.getTimeout()))
      .build();
  }

  @Override
  public GeminiSearchParsePayload parse(String text, AiSearchParserContext context) throws AiSearchParserException {
    ensureEnabledAndConfigured();

    URI uri = UriComponentsBuilder
      .fromUriString(trimTrailingSlash(searchParserProperties.getBaseUrl()))
      .path("/models/{model}:generateContent")
      .buildAndExpand(searchParserProperties.getModel().trim())
      .encode()
      .toUri();

    GeminiGenerateContentRequest request = new GeminiGenerateContentRequest(
      new GeminiContent(List.of(new GeminiPart(systemInstruction()))),
      List.of(new GeminiContent(List.of(new GeminiPart(compactUserPrompt(text, context))))),
      new GeminiGenerationConfig(
        "application/json",
        searchParserProperties.getMaxOutputTokens(),
        null,
        searchParserProperties.getTemperature()
      )
    );

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-goog-api-key", searchParserProperties.getApiKey().trim());

      ResponseEntity<GeminiGenerateContentResponse> response = restTemplate.exchange(
        uri,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        GeminiGenerateContentResponse.class
      );

      GeminiGenerateContentResponse body = response.getBody();
      String json = extractJson(body);

      try {
        return objectMapper.readValue(json, GeminiSearchParsePayload.class);
      } catch (JsonProcessingException e) {
        throw AiSearchParserException.invalidResponse("AI search parser returned invalid JSON", e);
      }
    } catch (HttpStatusCodeException e) {
      throw AiSearchParserException.externalHttp(e.getStatusCode(), null, e);
    } catch (ResourceAccessException e) {
      if (isTimeout(e)) {
        throw AiSearchParserException.timeout(e);
      }
      throw AiSearchParserException.externalFailure("AI search parser provider could not be reached", e);
    } catch (RestClientException e) {
      throw AiSearchParserException.invalidResponse("AI search parser provider returned an invalid response", e);
    }
  }

  private void ensureEnabledAndConfigured() throws AiSearchParserException {
    if (!searchParserProperties.isEnabled()) {
      throw AiSearchParserException.disabled();
    }

    if (!SUPPORTED_PROVIDER.equalsIgnoreCase(searchParserProperties.getProvider())) {
      throw AiSearchParserException.unsupportedProvider(searchParserProperties.getProvider());
    }

    if (!StringUtils.hasText(searchParserProperties.getApiKey())) {
      throw AiSearchParserException.missingApiKey();
    }

    if (!StringUtils.hasText(searchParserProperties.getModel())) {
      throw AiSearchParserException.invalidResponse("AI search parser model is not configured", null);
    }
  }

  private String extractJson(GeminiGenerateContentResponse response) throws AiSearchParserException {
    if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
      throw AiSearchParserException.invalidResponse("AI search parser returned no candidates", null);
    }

    StringBuilder builder = new StringBuilder();
    response.candidates().stream()
      .filter(candidate -> candidate != null && candidate.content() != null && candidate.content().parts() != null)
      .flatMap(candidate -> candidate.content().parts().stream())
      .map(GeminiPart::text)
      .filter(StringUtils::hasText)
      .forEach(builder::append);

    if (!StringUtils.hasText(builder.toString())) {
      throw AiSearchParserException.invalidResponse("AI search parser returned an empty content payload", null);
    }

    return extractJsonObject(builder.toString().trim());
  }

  private String extractJsonObject(String content) throws AiSearchParserException {
    if (!StringUtils.hasText(content)) {
      throw AiSearchParserException.invalidResponse("AI search parser returned an empty content payload", null);
    }

    String normalized = content.trim();
    if (normalized.startsWith("```")) {
      int firstLineBreak = normalized.indexOf('\n');
      int lastFence = normalized.lastIndexOf("```");
      if (firstLineBreak >= 0 && lastFence > firstLineBreak) {
        normalized = normalized.substring(firstLineBreak + 1, lastFence).trim();
      }
    }

    if (normalized.startsWith("{") && normalized.endsWith("}")) {
      return normalized;
    }

    int start = normalized.indexOf('{');
    int end = normalized.lastIndexOf('}');
    if (start >= 0 && end > start) {
      return normalized.substring(start, end + 1).trim();
    }

    throw AiSearchParserException.invalidResponse("AI search parser returned invalid JSON", null);
  }

  private String systemInstruction() {
    return """
      You output machine-readable JSON only.
      The first character of your response must be { and the last character must be }.
      Never use markdown, code fences, explanations, comments or extra text.
      Extract explicit filters from Spanish, Galician or English search text.
      Do not invent data. Use JSON null when a field is not clearly present.
      """;
  }

  private Map<String, Object> responseSchema() {
    return Map.ofEntries(
      Map.entry("type", "object"),
      Map.entry("properties", Map.ofEntries(
        Map.entry("intent", nullableStringSchema()),
        Map.entry("city", nullableStringSchema()),
        Map.entry("province", nullableStringSchema()),
        Map.entry("autonomousCommunity", nullableStringSchema()),
        Map.entry("date", nullableStringSchema()),
        Map.entry("dateFrom", nullableStringSchema()),
        Map.entry("dateTo", nullableStringSchema()),
        Map.entry("startTime", nullableStringSchema()),
        Map.entry("endTime", nullableStringSchema()),
        Map.entry("peopleCount", Map.of("type", List.of("integer", "null"))),
        Map.entry("maxBudget", Map.of("type", List.of("number", "null"))),
        Map.entry("spaceType", nullableStringSchema()),
        Map.entry("musicalGenre", nullableStringSchema()),
        Map.entry("needType", nullableStringSchema()),
        Map.entry("confidence", Map.of("type", List.of("number", "null")))
      )),
      Map.entry("required", List.of(
        "intent",
        "city",
        "province",
        "autonomousCommunity",
        "date",
        "dateFrom",
        "dateTo",
        "startTime",
        "endTime",
        "peopleCount",
        "maxBudget",
        "spaceType",
        "musicalGenre",
        "needType",
        "confidence"
      ))
    );
  }

  private Map<String, Object> nullableStringSchema() {
    return Map.of("type", List.of("string", "null"));
  }

  private String compactUserPrompt(String text, AiSearchParserContext context) {
    return USER_PROMPT_TEMPLATE.formatted(
      context.currentDate(),
      context.timezone(),
      String.join(", ", context.allowedIntents()),
      String.join(", ", context.allowedNeedTypes()),
      String.join(", ", context.allowedSpaceTypes()),
      text
    );
  }

  private String userPrompt(String text, AiSearchParserContext context) {
    return """
      Current date: %s
      Timezone: %s

      Allowed intent values: %s and UNKNOWN
      Allowed needType values: %s
      Allowed spaceType values: %s

      Return exactly one JSON object with this shape:
      {
        "intent": "SPACE | EVENT | BOTH | UNKNOWN",
        "city": "string or null",
        "date": "yyyy-MM-dd or null",
        "startTime": "HH:mm or null",
        "endTime": "HH:mm or null",
        "peopleCount": "integer or null",
        "maxBudget": "number or null",
        "spaceType": "enum value or null",
        "musicalGenre": "string or null",
        "needType": "enum value or null",
        "confidence": "number"
      }

      Rules:
      - no inventes datos; only extract filters clearly present in the search text
      - extract every explicit filter in the text; do not stop after detecting intent
      - if a field is unknown, return null
      - intent may be UNKNOWN when not clear
      - never emit enum values outside the allowed lists
      - prefer intent EVENT for searches about attending/discovering concerts, festivals, jams, open mics, agenda or tickets
      - prefer intent SPACE for searches about reserving/finding/renting a local, sala, estudio, aula, room, venue or space
      - for searches about bands or groups, such as "banda de funk en Vigo", "bandas indie" or "grupo de rock", return intent UNKNOWN, keep spaceType null, and still extract city and musicalGenre
      - if the user asks for "conciertos de jazz", "concertos de rock", "festival", "entradas" or "agenda", intent is EVENT and spaceType is null
      - if the user asks for "sala para conciertos", "local para tocar", "donde tocar" or "auditorio", intent is SPACE, spaceType is CONCERT_HALL and needType is PERFORMANCE
      - "sala de ensayo", "local de ensayo", "local de ensaio", "ensayar", "ensaiar" or "rehearsal" means spaceType REHEARSAL_ROOM and needType REHEARSAL
      - "estudio", "estudo", "grabacion", "gravacion", "grabar", "gravar" or "recording" means spaceType RECORDING_STUDIO and needType RECORDING
      - "aula", "clase", "taller", "obradoiro", "workshop" or "lesson" means spaceType CLASSROOM and needType CLASS
      - "multiusos", "polivalente", "multipurpose" or "evento privado" means spaceType MULTIPURPOSE
      - "para cuatro personas", "somos 4", "grupo de cinco musicos", "catro persoas" or "four people" means peopleCount 4 or 5
      - "presupuesto de 40 euros", "menos de 40", "ata 40", "40 EUR" or "40 EUR/h" means maxBudget 40
      - "de 18 a 20", "18h-20h", "desde las 18 hasta las 20" or "from 18 to 20" means startTime 18:00 and endTime 20:00
      - interpret relative dates like hoy, hoxe, today, manana, mañá, tomorrow, pasado manana and weekend using the current date above
      - "esta tarde" means current date with startTime 16:00 and endTime 20:00 when no stricter time is present
      - "esta noche", "esta noite" or "tonight" means current date with startTime 20:00 and endTime 23:59 when no stricter time is present
      - preserve city names as user-facing place names, for example "A Coruña", "Santiago de Compostela" or "Vigo"
      - musicalGenre should be the explicit genre only, such as rock, jazz, funk, metal, indie, folk, hip hop, r&b, techno or flamenco
      - never include comments or explanations

      Examples:
      Text: "Conciertos de jazz en Santiago"
      JSON: {"intent":"EVENT","city":"Santiago","date":null,"startTime":null,"endTime":null,"peopleCount":null,"maxBudget":null,"spaceType":null,"musicalGenre":"jazz","needType":"PERFORMANCE","confidence":0.9}
      Text: "Busco local de ensaio en Vigo para catro persoas pola tarde ata 40 euros"
      JSON: {"intent":"SPACE","city":"Vigo","date":null,"startTime":"16:00","endTime":"20:00","peopleCount":4,"maxBudget":40,"spaceType":"REHEARSAL_ROOM","musicalGenre":null,"needType":"REHEARSAL","confidence":0.95}
      Text: "Sala para conciertos en A Coruña de 18 a 20"
      JSON: {"intent":"SPACE","city":"A Coruña","date":null,"startTime":"18:00","endTime":"20:00","peopleCount":null,"maxBudget":null,"spaceType":"CONCERT_HALL","musicalGenre":null,"needType":"PERFORMANCE","confidence":0.9}
      Text: "Banda de funk en Vigo"
      JSON: {"intent":"UNKNOWN","city":"Vigo","date":null,"startTime":null,"endTime":null,"peopleCount":null,"maxBudget":null,"spaceType":null,"musicalGenre":"funk","needType":"GENERAL_DISCOVERY","confidence":0.9}

      Search text:
      %s
      """.formatted(
      context.currentDate(),
      context.timezone(),
      String.join(", ", context.allowedIntents()),
      String.join(", ", context.allowedNeedTypes()),
      String.join(", ", context.allowedSpaceTypes()),
      text
    );
  }

  private boolean isTimeout(ResourceAccessException exception) {
    Throwable current = exception;

    while (current != null) {
      if (current instanceof SocketTimeoutException) {
        return true;
      }

      String simpleName = current.getClass().getSimpleName();
      if (simpleName.contains("Timeout")) {
        return true;
      }

      current = current.getCause();
    }

    return false;
  }

  private String trimTrailingSlash(String value) {
    if (value == null) {
      return "";
    }

    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
