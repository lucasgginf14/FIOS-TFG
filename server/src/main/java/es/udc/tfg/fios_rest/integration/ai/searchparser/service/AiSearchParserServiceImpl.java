package es.udc.tfg.fios_rest.integration.ai.searchparser.service;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.common.geo.SpanishAutonomousCommunities;
import es.udc.tfg.fios_rest.integration.ai.searchparser.client.AiSearchParserClient;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParserContext;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiSearchParsePayload;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AiSearchParserServiceImpl implements AiSearchParserService {

  private static final String GEMINI_PROVIDER = "GEMINI";
  private static final String UNKNOWN_INTENT = "UNKNOWN";
  private static final Map<String, SearchIntent> INTENT_ALIASES = Map.ofEntries(
    Map.entry("ESPACIO", SearchIntent.SPACE),
    Map.entry("ESPACIOS", SearchIntent.SPACE),
    Map.entry("ESPAZO", SearchIntent.SPACE),
    Map.entry("ESPAZOS", SearchIntent.SPACE),
    Map.entry("SALA", SearchIntent.SPACE),
    Map.entry("LOCAL", SearchIntent.SPACE),
    Map.entry("LOCAIS", SearchIntent.SPACE),
    Map.entry("ROOM", SearchIntent.SPACE),
    Map.entry("ROOMS", SearchIntent.SPACE),
    Map.entry("SPACE", SearchIntent.SPACE),
    Map.entry("SPACES", SearchIntent.SPACE),
    Map.entry("VENUE", SearchIntent.SPACE),
    Map.entry("EVENTO", SearchIntent.EVENT),
    Map.entry("EVENTOS", SearchIntent.EVENT),
    Map.entry("CONCIERTO", SearchIntent.EVENT),
    Map.entry("CONCIERTOS", SearchIntent.EVENT),
    Map.entry("CONCERTO", SearchIntent.EVENT),
    Map.entry("CONCERTOS", SearchIntent.EVENT),
    Map.entry("CONCERT", SearchIntent.EVENT),
    Map.entry("CONCERTS", SearchIntent.EVENT),
    Map.entry("GIG", SearchIntent.EVENT),
    Map.entry("AMBOS", SearchIntent.BOTH),
    Map.entry("TODO", SearchIntent.BOTH),
    Map.entry("TODOS", SearchIntent.BOTH),
    Map.entry("BOTH", SearchIntent.BOTH),
    Map.entry("ALL", SearchIntent.BOTH)
  );
  private static final Map<String, MusicalSpaceType> SPACE_TYPE_ALIASES = Map.ofEntries(
    Map.entry("ENSAYO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("ENSAIO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("ENSAIAR", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("SALA_DE_ENSAYO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("LOCAL_DE_ENSAYO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("SALA_DE_ENSAIO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("LOCAL_DE_ENSAIO", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("REHEARSAL", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("REHEARSAL_SPACE", MusicalSpaceType.REHEARSAL_ROOM),
    Map.entry("ESTUDIO", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("ESTUDIO_DE_GRABACION", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("ESTUDO", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("ESTUDO_DE_GRAVACION", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("GRABACION", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("GRAVACION", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("GRABAR", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("GRAVAR", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("STUDIO", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("RECORDING", MusicalSpaceType.RECORDING_STUDIO),
    Map.entry("SALA_DE_CONCIERTOS", MusicalSpaceType.CONCERT_HALL),
    Map.entry("SALA_CONCIERTO", MusicalSpaceType.CONCERT_HALL),
    Map.entry("SALA_DE_CONCERTOS", MusicalSpaceType.CONCERT_HALL),
    Map.entry("SALA_CONCERTO", MusicalSpaceType.CONCERT_HALL),
    Map.entry("AUDITORIO", MusicalSpaceType.CONCERT_HALL),
    Map.entry("DIRECTO", MusicalSpaceType.CONCERT_HALL),
    Map.entry("CONCERT_HALL", MusicalSpaceType.CONCERT_HALL),
    Map.entry("LIVE_ROOM", MusicalSpaceType.CONCERT_HALL),
    Map.entry("VENUE", MusicalSpaceType.CONCERT_HALL),
    Map.entry("AULA", MusicalSpaceType.CLASSROOM),
    Map.entry("CLASE", MusicalSpaceType.CLASSROOM),
    Map.entry("TALLER", MusicalSpaceType.CLASSROOM),
    Map.entry("OBRADOIRO", MusicalSpaceType.CLASSROOM),
    Map.entry("WORKSHOP", MusicalSpaceType.CLASSROOM),
    Map.entry("LESSON", MusicalSpaceType.CLASSROOM),
    Map.entry("MULTIUSOS", MusicalSpaceType.MULTIPURPOSE),
    Map.entry("POLIVALENTE", MusicalSpaceType.MULTIPURPOSE),
    Map.entry("SALA_POLIVALENTE", MusicalSpaceType.MULTIPURPOSE),
    Map.entry("MULTIPURPOSE", MusicalSpaceType.MULTIPURPOSE),
    Map.entry("MULTI_PURPOSE", MusicalSpaceType.MULTIPURPOSE),
    Map.entry("ESPACIO_MUSICAL", MusicalSpaceType.OTHER),
    Map.entry("ESPAZO_MUSICAL", MusicalSpaceType.OTHER)
  );
  private static final Map<String, SearchNeedType> NEED_TYPE_ALIASES = Map.ofEntries(
    Map.entry("ENSAYO", SearchNeedType.REHEARSAL),
    Map.entry("ENSAYAR", SearchNeedType.REHEARSAL),
    Map.entry("ENSAIO", SearchNeedType.REHEARSAL),
    Map.entry("ENSAIAR", SearchNeedType.REHEARSAL),
    Map.entry("REHEARSAL", SearchNeedType.REHEARSAL),
    Map.entry("GRABACION", SearchNeedType.RECORDING),
    Map.entry("GRABAR", SearchNeedType.RECORDING),
    Map.entry("GRAVACION", SearchNeedType.RECORDING),
    Map.entry("GRAVAR", SearchNeedType.RECORDING),
    Map.entry("RECORDING", SearchNeedType.RECORDING),
    Map.entry("CONCIERTO", SearchNeedType.PERFORMANCE),
    Map.entry("CONCERTO", SearchNeedType.PERFORMANCE),
    Map.entry("DIRECTO", SearchNeedType.PERFORMANCE),
    Map.entry("LIVE", SearchNeedType.PERFORMANCE),
    Map.entry("PERFORMANCE", SearchNeedType.PERFORMANCE),
    Map.entry("CLASE", SearchNeedType.CLASS),
    Map.entry("TALLER", SearchNeedType.CLASS),
    Map.entry("OBRADOIRO", SearchNeedType.CLASS),
    Map.entry("CLASS", SearchNeedType.CLASS),
    Map.entry("WORKSHOP", SearchNeedType.CLASS),
    Map.entry("DESCUBRIR", SearchNeedType.GENERAL_DISCOVERY),
    Map.entry("BUSCAR", SearchNeedType.GENERAL_DISCOVERY),
    Map.entry("ATOPAR", SearchNeedType.GENERAL_DISCOVERY),
    Map.entry("DISCOVER", SearchNeedType.GENERAL_DISCOVERY),
    Map.entry("SEARCH", SearchNeedType.GENERAL_DISCOVERY)
  );
  private static final Map<String, String> MUSICAL_GENRE_ALIASES = Map.ofEntries(
    Map.entry("HIP_HOP", "hip hop"),
    Map.entry("HIPHOP", "hip hop"),
    Map.entry("R_B", "r&b"),
    Map.entry("RNB", "r&b"),
    Map.entry("ELECTRONICA", "electronica"),
    Map.entry("ELECTRONIC", "electronic"),
    Map.entry("CLASICA", "clasica"),
    Map.entry("CLASSICAL", "classical"),
    Map.entry("FUNK", "funk"),
    Map.entry("JAZZ", "jazz"),
    Map.entry("ROCK", "rock"),
    Map.entry("POP", "pop"),
    Map.entry("PUNK", "punk"),
    Map.entry("METAL", "metal"),
    Map.entry("INDIE", "indie"),
    Map.entry("FOLK", "folk"),
    Map.entry("BLUES", "blues"),
    Map.entry("SOUL", "soul"),
    Map.entry("TRAP", "trap"),
    Map.entry("REGGAETON", "reggaeton"),
    Map.entry("FLAMENCO", "flamenco"),
    Map.entry("REGGAE", "reggae"),
    Map.entry("SKA", "ska"),
    Map.entry("SALSA", "salsa"),
    Map.entry("TECHNO", "techno"),
    Map.entry("HOUSE", "house")
  );

  private final AiSearchParserClient aiSearchParserClient;
  private final Properties.SearchParser searchParserProperties;

  public AiSearchParserServiceImpl(AiSearchParserClient aiSearchParserClient, Properties properties) {
    this.aiSearchParserClient = aiSearchParserClient;
    this.searchParserProperties = properties.getAi().getSearchParser();
  }

  @Override
  public AiSearchParseResult parse(String text) throws AiSearchParserException {
    String normalizedText = normalizeRequiredText(text);
    GeminiSearchParsePayload payload = aiSearchParserClient.parse(normalizedText, buildContext());
    return normalize(payload);
  }

  @Override
  public boolean isEnabled() {
    return searchParserProperties.isEnabled();
  }

  private AiSearchParserContext buildContext() {
    return new AiSearchParserContext(
      LocalDate.now(),
      ZoneId.systemDefault().getId(),
      enumValues(SearchIntent.values()),
      enumValues(SearchNeedType.values()),
      enumValues(MusicalSpaceType.values())
    );
  }

  private AiSearchParseResult normalize(GeminiSearchParsePayload payload) {
    SearchIntent intent = normalizeIntent(payload == null ? null : payload.intent());
    String city = normalizeNullableText(payload == null ? null : payload.city());
    String province = normalizeNullableText(payload == null ? null : payload.province());
    String autonomousCommunity = normalizeAutonomousCommunity(payload == null ? null : payload.autonomousCommunity());
    LocationData locationData = normalizeLocationData(city, province, autonomousCommunity);
    LocalDate date = parseDate(payload == null ? null : payload.date());
    DateRange dateRange = normalizeDateRange(
      parseDate(payload == null ? null : payload.dateFrom()),
      parseDate(payload == null ? null : payload.dateTo())
    );
    LocalTime startTime = parseTime(payload == null ? null : payload.startTime());
    LocalTime endTime = parseTime(payload == null ? null : payload.endTime());
    Integer peopleCount = normalizePeopleCount(payload == null ? null : payload.peopleCount());
    BigDecimal maxBudget = normalizeBudget(payload == null ? null : payload.maxBudget());
    MusicalSpaceType spaceType = normalizeSpaceType(payload == null ? null : payload.spaceType());
    String musicalGenre = normalizeMusicalGenre(payload == null ? null : payload.musicalGenre());
    SearchNeedType needType = normalizeNeedType(payload == null ? null : payload.needType());
    Double confidence = normalizeConfidence(payload == null ? null : payload.confidence());

    if (endTime != null && startTime == null) {
      endTime = null;
    }

    if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
      startTime = null;
      endTime = null;
    }

    if (date != null) {
      dateRange = new DateRange(null, null);
    }

    return new AiSearchParseResult(
      intent,
      locationData.city(),
      locationData.province(),
      locationData.autonomousCommunity(),
      date,
      dateRange.dateFrom(),
      dateRange.dateTo(),
      startTime,
      endTime,
      peopleCount,
      maxBudget,
      spaceType,
      musicalGenre,
      needType,
      confidence,
      GEMINI_PROVIDER,
      GEMINI_PROVIDER
    );
  }

  private String normalizeRequiredText(String value) {
    String normalized = normalizeNullableText(value);

    if (normalized == null) {
      throw new IllegalArgumentException("The text is obligatory");
    }

    return normalized;
  }

  private String normalizeNullableText(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private SearchIntent normalizeIntent(String value) {
    String normalized = normalizeEnumValue(value);
    if (normalized == null || UNKNOWN_INTENT.equals(normalized)) {
      return null;
    }

    SearchIntent alias = INTENT_ALIASES.get(normalized);
    return alias == null ? normalizeEnum(normalized, SearchIntent.class) : alias;
  }

  private MusicalSpaceType normalizeSpaceType(String value) {
    String normalized = normalizeEnumValue(value);
    if (normalized == null) {
      return null;
    }

    MusicalSpaceType alias = SPACE_TYPE_ALIASES.get(normalized);
    return alias == null ? normalizeEnum(normalized, MusicalSpaceType.class) : alias;
  }

  private SearchNeedType normalizeNeedType(String value) {
    String normalized = normalizeEnumValue(value);
    if (normalized == null) {
      return null;
    }

    SearchNeedType alias = NEED_TYPE_ALIASES.get(normalized);
    return alias == null ? normalizeEnum(normalized, SearchNeedType.class) : alias;
  }

  private String normalizeMusicalGenre(String value) {
    String normalized = normalizeNullableText(value);
    if (normalized == null) {
      return null;
    }

    String alias = MUSICAL_GENRE_ALIASES.get(normalizeEnumValue(normalized));
    return alias == null ? normalized : alias;
  }

  private String normalizeAutonomousCommunity(String value) {
    return SpanishAutonomousCommunities.canonicalName(value).orElse(null);
  }

  private LocationData normalizeLocationData(
    String city,
    String province,
    String autonomousCommunity
  ) {
    String normalizedAutonomousCommunity = autonomousCommunity;
    String normalizedCity = city;
    String normalizedProvince = province;

    if (normalizedAutonomousCommunity == null) {
      OptionalCommunity promotedCity = promotedCommunity(normalizedCity);
      if (promotedCity.autonomousCommunity() != null) {
        normalizedAutonomousCommunity = promotedCity.autonomousCommunity();
        normalizedCity = null;
      }
    }

    if (normalizedAutonomousCommunity == null) {
      OptionalCommunity promotedProvince = promotedCommunity(normalizedProvince);
      if (promotedProvince.autonomousCommunity() != null) {
        normalizedAutonomousCommunity = promotedProvince.autonomousCommunity();
        normalizedProvince = null;
      }
    }

    if (sameCommunity(normalizedCity, normalizedAutonomousCommunity)) {
      normalizedCity = null;
    }

    if (sameCommunity(normalizedProvince, normalizedAutonomousCommunity)) {
      normalizedProvince = null;
    }

    return new LocationData(normalizedCity, normalizedProvince, normalizedAutonomousCommunity);
  }

  private OptionalCommunity promotedCommunity(String value) {
    return new OptionalCommunity(SpanishAutonomousCommunities.canonicalName(value).orElse(null));
  }

  private boolean sameCommunity(String value, String autonomousCommunity) {
    if (value == null || autonomousCommunity == null) {
      return false;
    }

    return SpanishAutonomousCommunities.canonicalName(value)
      .map(community -> community.equals(autonomousCommunity))
      .orElse(false);
  }

  private <T extends Enum<T>> T normalizeEnum(String value, Class<T> enumClass) {
    String normalized = normalizeEnumValue(value);
    if (normalized == null) {
      return null;
    }

    try {
      return Enum.valueOf(enumClass, normalized);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private String normalizeEnumValue(String value) {
    String normalized = normalizeNullableText(value);
    if (normalized == null) {
      return null;
    }

    String withoutAccents = Normalizer.normalize(normalized, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "");

    return withoutAccents
      .toUpperCase(Locale.ROOT)
      .replaceAll("[^A-Z0-9]+", "_")
      .replaceAll("^_+|_+$", "");
  }

  private LocalDate parseDate(String value) {
    String normalized = normalizeNullableText(value);
    if (normalized == null) {
      return null;
    }

    try {
      return LocalDate.parse(normalized);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private DateRange normalizeDateRange(LocalDate dateFrom, LocalDate dateTo) {
    if (dateFrom == null && dateTo == null) {
      return new DateRange(null, null);
    }

    LocalDate normalizedDateFrom = dateFrom == null ? dateTo : dateFrom;
    LocalDate normalizedDateTo = dateTo == null ? dateFrom : dateTo;

    if (normalizedDateFrom != null
      && normalizedDateTo != null
      && normalizedDateTo.isBefore(normalizedDateFrom)) {
      return new DateRange(null, null);
    }

    return new DateRange(normalizedDateFrom, normalizedDateTo);
  }

  private LocalTime parseTime(String value) {
    String normalized = normalizeNullableText(value);
    if (normalized == null) {
      return null;
    }

    try {
      if (normalized.matches("\\d{1,2}")) {
        return LocalTime.of(Integer.parseInt(normalized), 0);
      }

      if (normalized.matches("\\d{1,2}h(?:\\d{2})?")) {
        String[] parts = normalized.split("h", -1);
        return LocalTime.of(
          Integer.parseInt(parts[0]),
          parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) : 0
        );
      }

      if (normalized.matches("\\d{1,2}[\\.]\\d{2}")) {
        String[] parts = normalized.split("\\.", -1);
        return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
      }

      return LocalTime.parse(normalized);
    } catch (DateTimeParseException e) {
      return null;
    } catch (RuntimeException e) {
      return null;
    }
  }

  private Integer normalizePeopleCount(Integer value) {
    if (value == null || value <= 0) {
      return null;
    }

    return value;
  }

  private BigDecimal normalizeBudget(BigDecimal value) {
    if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
      return null;
    }

    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private Double normalizeConfidence(Double value) {
    if (value == null || value.isNaN() || value.isInfinite()) {
      return 0.0d;
    }

    if (value < 0d) {
      return 0.0d;
    }

    if (value > 1d) {
      return 1.0d;
    }

    return value;
  }

  private List<String> enumValues(Enum<?>[] values) {
    return Arrays.stream(values)
      .map(Enum::name)
      .toList();
  }

  private record LocationData(String city, String province, String autonomousCommunity) {
  }

  private record OptionalCommunity(String autonomousCommunity) {
  }

  private record DateRange(LocalDate dateFrom, LocalDate dateTo) {
  }
}
