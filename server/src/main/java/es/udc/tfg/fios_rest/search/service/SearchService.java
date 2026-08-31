package es.udc.tfg.fios_rest.search.service;

import es.udc.tfg.fios_rest.availability.service.CalculatedAvailabilityService;
import es.udc.tfg.fios_rest.availability.service.AvailabilityPricingService;
import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.geo.SpanishAutonomousCommunities;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserService;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import es.udc.tfg.fios_rest.search.persistence.dao.SearchDao;
import es.udc.tfg.fios_rest.search.persistence.entity.Search;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;
import es.udc.tfg.fios_rest.search.service.dto.DetectedSearchDataView;
import es.udc.tfg.fios_rest.search.service.dto.NaturalLanguageSearchRequest;
import es.udc.tfg.fios_rest.search.service.dto.NaturalLanguageSearchView;
import es.udc.tfg.fios_rest.search.service.dto.SearchCriteria;
import es.udc.tfg.fios_rest.search.service.dto.SearchHistoryView;
import es.udc.tfg.fios_rest.search.service.dto.SearchMapItemType;
import es.udc.tfg.fios_rest.search.service.dto.SearchMapItemView;
import es.udc.tfg.fios_rest.search.service.dto.SearchMapView;
import es.udc.tfg.fios_rest.search.service.dto.SearchResultEventRef;
import es.udc.tfg.fios_rest.search.service.dto.SearchResultSpaceRef;
import es.udc.tfg.fios_rest.search.service.dto.SearchView;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewDao;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewRatingStats;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@Transactional(rollbackFor = Exception.class)
public class SearchService {

  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

  private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\b(\\d{4})-(\\d{1,2})-(\\d{1,2})\\b");
  private static final Pattern DMY_DATE_PATTERN = Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{2,4}))?\\b");
  private static final Pattern RANGE_COLON_PATTERN =
    Pattern.compile("\\b(\\d{1,2}):(\\d{2})\\s*(?:-|a|ata|hasta|to)\\s*(\\d{1,2}):(\\d{2})\\b");
  private static final Pattern RANGE_HOUR_PATTERN =
    Pattern.compile("\\b(\\d{1,2})\\s*h\\s*(?:-|a|ata|hasta|to)\\s*(\\d{1,2})\\s*h\\b");
  private static final Pattern RANGE_PLAIN_HOUR_PATTERN =
    Pattern.compile("\\b(?:de|desde|dende|from)\\s+(\\d{1,2})(?::(\\d{2}))?\\s*(?:-|a|ata|hasta|to)\\s*(?:las\\s*|as\\s*)?(\\d{1,2})(?::(\\d{2}))?\\b");
  private static final Pattern START_TIME_PATTERN =
    Pattern.compile("(?:a las|as|desde las|desde as|desde|dende as|dende|sobre las|sobre as|a partir de las|a partir das|at|from)\\s*(\\d{1,2})(?::(\\d{2}))?");
  private static final Pattern END_TIME_PATTERN =
    Pattern.compile("(?:hasta las|hasta as|hasta|ata as|ata|until|to)\\s*(\\d{1,2})(?::(\\d{2}))?");
  private static final Pattern PEOPLE_PATTERN =
    Pattern.compile("\\b(?:para|de|somos|seremos|grupo de|capacidad para|for|capacity for)?\\s*(\\d{1,3})\\s*(?:personas|persona|persoas|persoa|asistentes|people|musicos|musicas|integrantes|members|membros)\\b");
  private static final Pattern PEOPLE_PREFIX_PATTERN =
    Pattern.compile("\\b(?:para|somos|seremos|grupo de|capacidad para|for|capacity for)\\s*(\\d{1,3})\\b");
  private static final List<Pattern> BUDGET_PATTERNS = List.of(
    Pattern.compile("\\b(?:hasta|ata|maximo|max|menos de|por menos de|por debajo de|presupuesto(?: maximo)?(?: de)?|budget(?: up to)?|under|up to)\\s*(\\d+(?:[\\.,]\\d{1,2})?)\\s*(?:\\u20ac|eur|euros?)?(?:\\s*/?\\s*h|\\s*por hora|\\s*hour)?\\b"),
    Pattern.compile("\\b(\\d+(?:[\\.,]\\d{1,2})?)\\s*(?:\\u20ac|eur|euros?)(?:\\s*/?\\s*h|\\s*por hora)?\\s*(?:maximo|max|o menos|como mucho)?\\b")
  );

  private static final List<SpaceTypeKeywordGroup> SPACE_TYPE_KEYWORDS = List.of(
    new SpaceTypeKeywordGroup(MusicalSpaceType.REHEARSAL_ROOM, List.of(
      "sala de ensayo", "local de ensayo", "ensayo", "ensayar",
      "sala de ensaio", "local de ensaio", "ensaio", "ensaiar",
      "rehearsal room", "rehearsal space", "rehearsal"
    )),
    new SpaceTypeKeywordGroup(MusicalSpaceType.RECORDING_STUDIO, List.of(
      "estudio de grabacion", "estudio", "grabar", "grabacion",
      "estudo de gravacion", "estudo", "gravar",
      "recording studio", "studio", "recording"
    )),
    new SpaceTypeKeywordGroup(MusicalSpaceType.CONCERT_HALL, List.of(
      "sala de conciertos", "sala de concierto", "sala concerto", "sala de concertos",
      "sala para conciertos", "espacio para conciertos", "local para conciertos",
      "auditorio", "directo", "concert hall", "live room", "venue"
    )),
    new SpaceTypeKeywordGroup(MusicalSpaceType.CLASSROOM, List.of(
      "aula", "clase", "taller", "formacion", "obradoiro", "classroom", "workshop", "lesson"
    )),
    new SpaceTypeKeywordGroup(MusicalSpaceType.MULTIPURPOSE, List.of(
      "multiusos", "multipurpose", "evento privado", "polivalente", "sala polivalente", "multi-purpose"
    )),
    new SpaceTypeKeywordGroup(MusicalSpaceType.OTHER, List.of("espacio musical", "local musical", "espazo musical"))
  );

  private static final Map<String, DayOfWeek> WEEKDAY_KEYWORDS = Map.ofEntries(
    Map.entry("lunes", DayOfWeek.MONDAY),
    Map.entry("martes", DayOfWeek.TUESDAY),
    Map.entry("miercoles", DayOfWeek.WEDNESDAY),
    Map.entry("jueves", DayOfWeek.THURSDAY),
    Map.entry("viernes", DayOfWeek.FRIDAY),
    Map.entry("sabado", DayOfWeek.SATURDAY),
    Map.entry("domingo", DayOfWeek.SUNDAY)
  );

  private static final List<String> EVENT_KEYWORDS =
    List.of("evento", "eventos", "concierto", "conciertos", "concerto", "concertos", "festival", "show", "actuacion", "actuacions", "jam", "open mic", "gig");
  private static final List<String> SPACE_KEYWORDS =
    List.of("espacio", "espazos", "espazo", "sala", "local", "estudio", "estudo", "room", "space", "venue");
  private static final List<String> SPACE_REQUEST_KEYWORDS =
    List.of("busco", "buscamos", "necesito", "necesitamos", "quiero reservar", "reservar", "alquilar", "alugar", "onde tocar", "donde tocar", "where to play");
  private static final List<String> EVENT_DISCOVERY_KEYWORDS =
    List.of("conciertos de", "concertos de", "ver conciertos", "ver concertos", "ir a", "entradas", "tickets", "agenda", "cartel", "lineup", "festival");
  private static final List<String> PERFORMANCE_SPACE_KEYWORDS =
    List.of("para concierto", "para conciertos", "para concerto", "para concertos", "para tocar", "donde tocar", "onde tocar", "sala de conciertos", "sala de concertos", "concert hall", "live room");
  private static final Set<String> BAND_SEARCH_STOP_WORDS = Set.of(
    "a", "al", "algo", "algun", "alguna", "banda", "bandas", "busca", "busco", "buscar",
    "buscando", "con", "de", "del", "el", "en", "grupo", "grupos", "la", "las", "lo",
    "los", "mi", "mis", "necesito", "o", "para", "por", "que", "quiero", "se", "sin",
    "un", "una", "unos", "unas", "y"
  );
  private static final List<String> BAND_TERMS =
    List.of("band", "bands", "banda", "bandas", "grupo", "grupos", "project", "projects", "proyecto", "proyectos");
  private static final List<String> BAND_PHRASES =
    List.of("banda de", "bandas de", "grupo de", "grupos de", "buscar banda", "busco banda", "bandas en", "banda en", "band in", "bands in");
  private static final List<String> RECRUITMENT_TERMS =
    List.of("bajista", "bateria", "cantante", "guitarra", "guitarrista", "integrante", "integrantes", "miembro", "miembros", "saxofon", "saxofonista", "teclado", "teclista", "vacante", "vocalista");
  private static final List<String> RECRUITMENT_PHRASES =
    List.of("busca miembros", "buscan miembros", "buscamos miembros", "busco banda", "buscando banda", "formar banda", "se busca musico", "unirme a banda");
  private static final List<String> TODAY_KEYWORDS =
    List.of("hoy", "hoxe", "today", "esta tarde", "esta noche", "esta noite", "tonight", "this afternoon");
  private static final List<String> DAY_AFTER_TOMORROW_KEYWORDS =
    List.of("pasado manana", "pasado mana", "day after tomorrow");
  private static final List<String> TOMORROW_KEYWORDS =
    List.of("manana", "mana", "tomorrow");
  private static final List<String> MORNING_TIME_KEYWORDS =
    List.of("por la manana", "de manana", "pola mana", "de mana", "in the morning", "morning");
  private static final List<String> AFTERNOON_TIME_KEYWORDS =
    List.of("por la tarde", "de tarde", "pola tarde", "esta tarde", "afternoon", "this afternoon");
  private static final List<String> NIGHT_TIME_KEYWORDS =
    List.of("por la noche", "de noche", "pola noite", "esta noche", "esta noite", "tonight", "night");
  private static final List<String> WEEKEND_KEYWORDS =
    List.of("fin de semana", "finde", "esta finde", "weekend");
  private static final int UPCOMING_DAYS_RANGE_DAYS = 7;
  private static final int MAX_SPACE_DATE_RANGE_DAYS = 31;
  private static final List<String> UPCOMING_DAYS_KEYWORDS =
    List.of(
      "proximos dias",
      "os proximos dias",
      "nos proximos dias",
      "en los proximos dias",
      "para los proximos dias",
      "para os proximos dias",
      "vindeiros dias",
      "nos vindeiros dias",
      "next days",
      "next few days"
    );
  private static final Map<String, String> MUSICAL_GENRE_KEYWORDS = Map.ofEntries(
    Map.entry("rock", "rock"),
    Map.entry("pop", "pop"),
    Map.entry("punk", "punk"),
    Map.entry("jazz", "jazz"),
    Map.entry("funk", "funk"),
    Map.entry("soul", "soul"),
    Map.entry("metal", "metal"),
    Map.entry("hip hop", "hip hop"),
    Map.entry("hip-hop", "hip hop"),
    Map.entry("rap", "rap"),
    Map.entry("trap", "trap"),
    Map.entry("blues", "blues"),
    Map.entry("clasica", "clasica"),
    Map.entry("classical", "classical"),
    Map.entry("techno", "techno"),
    Map.entry("house", "house"),
    Map.entry("electronic", "electronic"),
    Map.entry("electronica", "electronica"),
    Map.entry("indie", "indie"),
    Map.entry("reggaeton", "reggaeton"),
    Map.entry("folk", "folk"),
    Map.entry("flamenco", "flamenco"),
    Map.entry("reggae", "reggae"),
    Map.entry("ska", "ska"),
    Map.entry("salsa", "salsa"),
    Map.entry("r&b", "r&b"),
    Map.entry("rnb", "r&b")
  );
  private static final Map<String, Integer> NUMBER_WORDS = Map.ofEntries(
    Map.entry("un", 1),
    Map.entry("uno", 1),
    Map.entry("una", 1),
    Map.entry("dos", 2),
    Map.entry("tres", 3),
    Map.entry("cuatro", 4),
    Map.entry("cinco", 5),
    Map.entry("seis", 6),
    Map.entry("siete", 7),
    Map.entry("ocho", 8),
    Map.entry("nueve", 9),
    Map.entry("diez", 10),
    Map.entry("veintiuno", 21),
    Map.entry("veintidos", 22),
    Map.entry("veintitres", 23),
    Map.entry("veinticuatro", 24),
    Map.entry("veinticinco", 25),
    Map.entry("unha", 1),
    Map.entry("duas", 2),
    Map.entry("dous", 2),
    Map.entry("catro", 4),
    Map.entry("sete", 7),
    Map.entry("oito", 8),
    Map.entry("nove", 9),
    Map.entry("dez", 10),
    Map.entry("vinte", 20),
    Map.entry("one", 1),
    Map.entry("two", 2),
    Map.entry("three", 3),
    Map.entry("four", 4),
    Map.entry("five", 5),
    Map.entry("six", 6),
    Map.entry("seven", 7),
    Map.entry("eight", 8),
    Map.entry("nine", 9),
    Map.entry("ten", 10),
    Map.entry("once", 11),
    Map.entry("doce", 12),
    Map.entry("trece", 13),
    Map.entry("catorce", 14),
    Map.entry("quince", 15),
    Map.entry("dieciseis", 16),
    Map.entry("diecisiete", 17),
    Map.entry("dieciocho", 18),
    Map.entry("diecinueve", 19),
    Map.entry("veinte", 20),
    Map.entry("eleven", 11),
    Map.entry("twelve", 12)
  );

  @Autowired
  private SearchDao searchDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private EventDao eventDao;

  @Autowired
  private BandDao bandDao;

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  @Autowired
  private ScheduleDao scheduleDao;

  @Autowired
  private SpaceReviewDao spaceReviewDao;

  @Autowired
  private CalculatedAvailabilityService calculatedAvailabilityService;

  @Autowired
  private AvailabilityPricingService availabilityPricingService;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Autowired
  private AiSearchParserService aiSearchParserService;

  public SearchView search(SearchCriteria criteria) {
    SearchCriteria normalizedCriteria = normalizeCriteria(criteria);
    SearchView results = executeSearch(normalizedCriteria, SearchIntent.BOTH, null);
    saveStructuredSearchIfPossible(normalizedCriteria);
    return results;
  }

  public NaturalLanguageSearchView searchNaturalLanguage(NaturalLanguageSearchRequest request) {
    String originalText = normalizeRequiredText(request.text(), "text");
    NaturalLanguageParsingOutcome parsingOutcome = parseNaturalLanguage(originalText);
    DetectedSearchDataView detectedData = parsingOutcome.detectedData();
    SearchCriteria detectedCriteria = criteriaFromDetectedData(detectedData);
    SearchCriteria explicitCriteria = criteriaFromRequest(request);
    SearchCriteria criteria = mergeWithExplicitFilters(detectedCriteria, explicitCriteria);

    SearchIntent intent = detectedData.detectedIntent() == null ? SearchIntent.BOTH : detectedData.detectedIntent();
    SearchView results = executeSearch(normalizeCriteria(criteria), intent, originalText);
    saveNaturalLanguageSearchIfPossible(originalText, detectedData);

    return new NaturalLanguageSearchView(
      originalText,
      detectedData,
      parsingOutcome.parserUsed(),
      parsingOutcome.parserConfidence(),
      results.spaces(),
      results.events(),
      results.bands(),
      results.recruitments()
    );
  }

  private SearchCriteria criteriaFromDetectedData(DetectedSearchDataView detectedData) {
    return new SearchCriteria(
      detectedData.detectedCity(),
      detectedData.detectedProvince(),
      detectedData.detectedAutonomousCommunity(),
      detectedData.detectedDate(),
      detectedData.detectedDateFrom(),
      detectedData.detectedDateTo(),
      detectedData.detectedStartTime(),
      detectedData.detectedEndTime(),
      detectedData.detectedSpaceType(),
      detectedData.detectedPeopleCount(),
      detectedData.detectedMusicalGenre(),
      detectedData.detectedMaxBudget()
    );
  }

  private SearchCriteria criteriaFromRequest(NaturalLanguageSearchRequest request) {
    return new SearchCriteria(
      request.city(),
      request.province(),
      request.autonomousCommunity(),
      request.date(),
      request.dateFrom(),
      request.dateTo(),
      request.startTime(),
      request.endTime(),
      request.spaceType(),
      request.peopleCount(),
      request.musicalGenre(),
      request.maxBudget()
    );
  }

  private SearchCriteria mergeWithExplicitFilters(SearchCriteria detectedCriteria, SearchCriteria explicitCriteria) {
    SearchCriteria normalizedDetected = normalizeCriteria(detectedCriteria);
    SearchCriteria normalizedExplicit = normalizeCriteria(explicitCriteria);
    boolean explicitLocation = normalizedExplicit.city() != null
      || normalizedExplicit.province() != null
      || normalizedExplicit.autonomousCommunity() != null;
    boolean explicitDate = normalizedExplicit.date() != null
      || normalizedExplicit.dateFrom() != null
      || normalizedExplicit.dateTo() != null;

    return new SearchCriteria(
      explicitLocation ? normalizedExplicit.city() : normalizedDetected.city(),
      explicitLocation ? normalizedExplicit.province() : normalizedDetected.province(),
      explicitLocation ? normalizedExplicit.autonomousCommunity() : normalizedDetected.autonomousCommunity(),
      explicitDate ? normalizedExplicit.date() : normalizedDetected.date(),
      explicitDate ? normalizedExplicit.dateFrom() : normalizedDetected.dateFrom(),
      explicitDate ? normalizedExplicit.dateTo() : normalizedDetected.dateTo(),
      firstNonNull(normalizedExplicit.startTime(), normalizedDetected.startTime()),
      firstNonNull(normalizedExplicit.endTime(), normalizedDetected.endTime()),
      firstNonNull(normalizedExplicit.spaceType(), normalizedDetected.spaceType()),
      firstNonNull(normalizedExplicit.peopleCount(), normalizedDetected.peopleCount()),
      firstNonNull(normalizedExplicit.musicalGenre(), normalizedDetected.musicalGenre()),
      firstNonNull(normalizedExplicit.maxBudget(), normalizedDetected.maxBudget())
    );
  }

  @Transactional(readOnly = true)
  public List<SearchHistoryView> findHistory() throws NotFoundException {
    User currentUser = findCurrentUser();

    return searchDao.findByUser(currentUser.getId()).stream()
      .map(SearchHistoryView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public SearchMapView searchMap(SearchCriteria criteria) {
    SearchCriteria normalizedCriteria = normalizeCriteria(criteria);
    List<SearchMapItemView> items = new ArrayList<>();

    findSpaceCandidates(normalizedCriteria).stream()
      .filter(space -> space.musicalSpace().getLocation().getLatitude() != null
        && space.musicalSpace().getLocation().getLongitude() != null)
      .map(space -> new SearchMapItemView(
        SearchMapItemType.MUSICAL_SPACE,
        space.musicalSpace().getId(),
        space.musicalSpace().getName(),
        space.musicalSpace().getLocation().getCity(),
        space.musicalSpace().getLocation().getProvince(),
        space.musicalSpace().getLocation().getCountry(),
        space.musicalSpace().getName(),
        space.musicalSpace().getLocation().getLatitude(),
        space.musicalSpace().getLocation().getLongitude(),
        normalizedCriteria.date(),
        normalizedCriteria.startTime(),
        space.estimatedPrice(),
        null
      ))
      .forEach(items::add);

    findEvents(normalizedCriteria).stream()
      .filter(event -> event.latitude() != null && event.longitude() != null)
      .map(event -> new SearchMapItemView(
        SearchMapItemType.EVENT,
        event.id(),
        event.title(),
        event.city(),
        event.province(),
        event.country(),
        event.location() == null ? event.venueName() : event.location(),
        event.latitude(),
        event.longitude(),
        event.eventDate(),
        event.startTime(),
        event.ticketPrice(),
        event.externalUrl()
      ))
      .forEach(items::add);

    return new SearchMapView(items);
  }

  private SearchView executeSearch(SearchCriteria criteria, SearchIntent intent, String originalText) {
    return new SearchView(
      DetectedSearchDataView.from(criteria, intent),
      SearchIntent.EVENT.equals(intent) ? List.of() : findSpaces(criteria),
      SearchIntent.SPACE.equals(intent) ? List.of() : findEvents(criteria),
      findBands(criteria, intent, originalText),
      findRecruitments(criteria, intent, originalText)
    );
  }

  private List<SearchResultSpaceRef> findSpaces(SearchCriteria criteria) {
    List<SpaceSearchCandidate> candidates = findSpaceCandidates(criteria);
    Map<Long, SpaceReviewRatingStats> ratingStatsBySpaceId = spaceReviewDao.findRatingStatsByMusicalSpaceIds(
      candidates.stream()
        .map(candidate -> candidate.musicalSpace().getId())
        .toList()
    );

    return candidates.stream()
      .map(candidate -> SearchResultSpaceRef.from(
        candidate.musicalSpace(),
        candidate.estimatedPrice(),
        ratingStatsBySpaceId.get(candidate.musicalSpace().getId())
      ))
      .sorted(spaceResultComparator())
      .toList();
  }

  private List<SpaceSearchCandidate> findSpaceCandidates(SearchCriteria criteria) {
    return musicalSpaceDao.findPublic(null, criteria.spaceType(), criteria.peopleCount()).stream()
      .filter(space -> matchesTextValue(space.getLocation().getCity(), criteria.city()))
      .filter(space -> matchesTextValue(space.getLocation().getProvince(), criteria.province()))
      .filter(space -> matchesAutonomousCommunity(space.getLocation().getProvince(), criteria.autonomousCommunity()))
      .map(space -> buildSpaceCandidate(space, criteria))
      .filter(Objects::nonNull)
      .toList();
  }

  private List<SearchResultEventRef> findEvents(SearchCriteria criteria) {
    Collection<Event> publishedEvents = eventDao.findPublished(new EventFilterParams(
      criteria.date(),
      criteria.dateFrom(),
      criteria.dateTo(),
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ));

    return publishedEvents.stream()
      .filter(event -> matchesTextValue(event.getCity(), criteria.city()))
      .filter(event -> matchesTextValue(event.getProvince(), criteria.province()))
      .filter(event -> matchesAutonomousCommunity(event.getProvince(), criteria.autonomousCommunity()))
      .filter(event -> matchesTextValue(event.getMusicalGenre(), criteria.musicalGenre()))
      .filter(event -> matchesEventDate(event, criteria))
      .filter(event -> matchesEventTime(event, criteria))
      .filter(event -> matchesEventCapacity(event, criteria.peopleCount()))
      .filter(event -> matchesEventBudget(event, criteria.maxBudget()))
      .map(SearchResultEventRef::from)
      .toList();
  }

  private List<BandRef> findBands(SearchCriteria criteria, SearchIntent intent, String originalText) {
    if (criteria.province() != null || criteria.autonomousCommunity() != null) {
      return List.of();
    }

    if (!shouldIncludeBands(originalText, criteria, intent)) {
      return List.of();
    }

    return bandDao.findActive(null, null).stream()
      .filter(band -> matchesTextValue(band.getBaseCity(), criteria.city()))
      .filter(band -> matchesTextValue(band.getMainGenre(), criteria.musicalGenre()))
      .filter(band -> matchesBandSearchText(band, originalText))
      .map(BandRef::from)
      .toList();
  }

  private List<BandRecruitmentRef> findRecruitments(SearchCriteria criteria, SearchIntent intent, String originalText) {
    if (criteria.province() != null || criteria.autonomousCommunity() != null) {
      return List.of();
    }

    if (!shouldIncludeRecruitments(originalText, criteria, intent)) {
      return List.of();
    }

    return bandRecruitmentDao.findOpen(null, null).stream()
      .filter(recruitment -> matchesTextValue(recruitment.getCity(), criteria.city())
        || matchesTextValue(recruitment.getBand().getBaseCity(), criteria.city()))
      .filter(recruitment -> matchesTextValue(recruitment.getBand().getMainGenre(), criteria.musicalGenre()))
      .filter(recruitment -> matchesRecruitmentSearchText(recruitment, originalText))
      .map(BandRecruitmentRef::from)
      .toList();
  }

  private boolean shouldIncludeBands(String originalText, SearchCriteria criteria, SearchIntent intent) {
    if (hasBandPhrase(originalText) || hasBandTerm(originalText)) {
      return true;
    }

    if (SearchIntent.SPACE.equals(intent) || SearchIntent.EVENT.equals(intent)) {
      return false;
    }

    if (hasSpaceOrEventIntent(originalText)) {
      return false;
    }

    return !hasSpaceSpecificFilters(criteria);
  }

  private boolean shouldIncludeRecruitments(String originalText, SearchCriteria criteria, SearchIntent intent) {
    if (hasRecruitmentPhrase(originalText)) {
      return true;
    }

    if (SearchIntent.SPACE.equals(intent) || SearchIntent.EVENT.equals(intent)) {
      return false;
    }

    if (hasSpaceOrEventIntent(originalText)) {
      return false;
    }

    if (hasRecruitmentTerm(originalText)) {
      return true;
    }

    return !hasSpaceSpecificFilters(criteria);
  }

  private boolean hasSpaceSpecificFilters(SearchCriteria criteria) {
    return criteria.spaceType() != null
      || criteria.province() != null
      || criteria.autonomousCommunity() != null
      || criteria.date() != null
      || criteria.dateFrom() != null
      || criteria.dateTo() != null
      || criteria.startTime() != null
      || criteria.endTime() != null
      || criteria.peopleCount() != null
      || criteria.maxBudget() != null;
  }

  private boolean matchesBandSearchText(Band band, String originalText) {
    List<String> tokens = tokenizeSearchText(originalText);

    if (tokens.isEmpty()) {
      return true;
    }

    String haystack = normalizeForMatching(Stream.of(
        band.getName(),
        band.getDescription(),
        band.getMainGenre(),
        band.getBaseCity()
      )
      .filter(Objects::nonNull)
      .reduce("", (left, right) -> left + " " + right));

    return tokens.stream().anyMatch(haystack::contains);
  }

  private boolean matchesRecruitmentSearchText(BandRecruitment recruitment, String originalText) {
    List<String> tokens = tokenizeSearchText(originalText);

    if (tokens.isEmpty()) {
      return true;
    }

    String haystack = normalizeForMatching(Stream.of(
        recruitment.getTitle(),
        recruitment.getDescription(),
        recruitment.getRoleWanted(),
        recruitment.getInstrument().getName(),
        recruitment.getBand().getName(),
        recruitment.getBand().getMainGenre(),
        recruitment.getCity(),
        recruitment.getBand().getBaseCity()
      )
      .filter(Objects::nonNull)
      .reduce("", (left, right) -> left + " " + right));

    return tokens.stream().anyMatch(haystack::contains);
  }

  private List<String> tokenizeSearchText(String originalText) {
    return Stream.of(normalizeForMatching(originalText).split(" "))
      .filter(token -> token.length() > 2)
      .filter(token -> !BAND_SEARCH_STOP_WORDS.contains(token))
      .toList();
  }

  private boolean matchesTextValue(String source, String requested) {
    String normalizedRequested = normalizeForMatching(requested);

    return normalizedRequested.isEmpty() || normalizeForMatching(source).contains(normalizedRequested);
  }

  private boolean hasBandPhrase(String originalText) {
    String normalizedText = normalizeForMatching(originalText);

    return !normalizedText.isEmpty() && containsAny(normalizedText, BAND_PHRASES);
  }

  private boolean hasBandTerm(String originalText) {
    return Stream.of(normalizeForMatching(originalText).split(" "))
      .filter(token -> token.length() > 2)
      .anyMatch(BAND_TERMS::contains);
  }

  private boolean hasRecruitmentPhrase(String originalText) {
    String normalizedText = normalizeForMatching(originalText);

    return !normalizedText.isEmpty() && containsAny(normalizedText, RECRUITMENT_PHRASES);
  }

  private boolean hasRecruitmentTerm(String originalText) {
    return tokenizeSearchText(originalText).stream().anyMatch(RECRUITMENT_TERMS::contains);
  }

  private boolean hasSpaceOrEventIntent(String originalText) {
    List<String> tokens = tokenizeSearchText(originalText);

    return tokens.stream().anyMatch(token -> SPACE_KEYWORDS.contains(token) || EVENT_KEYWORDS.contains(token));
  }

  private SpaceSearchCandidate buildSpaceCandidate(MusicalSpace musicalSpace, SearchCriteria criteria) {
    BigDecimal estimatedPrice = estimateSpacePrice(musicalSpace, criteria);

    if (hasDateFilter(criteria) && estimatedPrice == null) {
      return null;
    }

    if (criteria.maxBudget() != null && estimatedPrice == null) {
      return null;
    }

    if (criteria.maxBudget() != null && estimatedPrice.compareTo(criteria.maxBudget()) > 0) {
      return null;
    }

    return new SpaceSearchCandidate(musicalSpace, estimatedPrice);
  }

  private BigDecimal estimateSpacePrice(MusicalSpace musicalSpace, SearchCriteria criteria) {
    if (criteria.date() != null) {
      return estimateAvailableDatedSpacePrice(musicalSpace.getId(), criteria);
    }

    if (criteria.dateFrom() != null && criteria.dateTo() != null) {
      return estimateAvailableSpacePriceInRange(musicalSpace.getId(), criteria);
    }

    return scheduleDao.findByMusicalSpace(musicalSpace.getId()).stream()
      .map(Schedule::getPrice)
      .filter(Objects::nonNull)
      .min(Comparator.naturalOrder())
      .map(price -> price.setScale(2, RoundingMode.HALF_UP))
      .orElse(null);
  }

  private BigDecimal estimateAvailableDatedSpacePrice(Long spaceId, SearchCriteria criteria) {
    try {
      var availability = calculatedAvailabilityService.findAvailability(spaceId, criteria.date());

      if (criteria.startTime() != null && criteria.endTime() != null) {
        boolean rangeAvailable = availability.slots().stream()
          .anyMatch(slot -> !criteria.startTime().isBefore(slot.startTime())
            && !criteria.endTime().isAfter(slot.endTime()));

        if (!rangeAvailable) {
          return null;
        }

        return availabilityPricingService.estimatePrice(
          spaceId,
          criteria.date(),
          criteria.startTime(),
          criteria.endTime()
        );
      }

      return availability.slots().stream()
        .map(slot -> slot.price())
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .map(price -> price.setScale(2, RoundingMode.HALF_UP))
        .orElse(null);
    } catch (NotFoundException e) {
      return null;
    }
  }

  private BigDecimal estimateAvailableSpacePriceInRange(Long spaceId, SearchCriteria criteria) {
    long days = ChronoUnit.DAYS.between(criteria.dateFrom(), criteria.dateTo()) + 1;
    return Stream.iterate(criteria.dateFrom(), date -> date.plusDays(1))
      .limit(days)
      .map(date -> estimateAvailableDatedSpacePrice(spaceId, new SearchCriteria(
        criteria.city(),
        criteria.province(),
        criteria.autonomousCommunity(),
        date,
        null,
        null,
        criteria.startTime(),
        criteria.endTime(),
        criteria.spaceType(),
        criteria.peopleCount(),
        criteria.musicalGenre(),
        criteria.maxBudget()
      )))
      .filter(Objects::nonNull)
      .min(Comparator.naturalOrder())
      .orElse(null);
  }

  private Comparator<SearchResultSpaceRef> spaceResultComparator() {
    return Comparator
      .comparing((SearchResultSpaceRef space) -> space.estimatedPrice() == null ? BigDecimal.valueOf(Double.MAX_VALUE) : space.estimatedPrice())
      .thenComparing(SearchResultSpaceRef::city)
      .thenComparing(SearchResultSpaceRef::name);
  }

  private boolean matchesEventTime(Event event, SearchCriteria criteria) {
    if (criteria.startTime() == null && criteria.endTime() == null) {
      return true;
    }

    LocalTime eventStart = event.getStartTime();
    LocalTime eventEnd = event.getEndTime() == null ? eventStart : event.getEndTime();

    if (eventStart == null) {
      return false;
    }

    if (criteria.startTime() != null && criteria.endTime() != null) {
      if (eventEnd == null) {
        eventEnd = eventStart;
      }
      return !eventStart.isAfter(criteria.endTime()) && !eventEnd.isBefore(criteria.startTime());
    }

    if (criteria.startTime() != null) {
      return !eventStart.isBefore(criteria.startTime());
    }

    return !eventStart.isAfter(criteria.endTime());
  }

  private boolean matchesEventCapacity(Event event, Integer peopleCount) {
    if (peopleCount == null) {
      return true;
    }

    return event.getCapacity() != null && event.getCapacity() >= peopleCount;
  }

  private boolean matchesEventBudget(Event event, BigDecimal maxBudget) {
    if (maxBudget == null) {
      return true;
    }

    return event.getTicketPrice() != null && event.getTicketPrice().compareTo(maxBudget) <= 0;
  }

  private boolean matchesEventDate(Event event, SearchCriteria criteria) {
    if (criteria.date() != null) {
      return event.getEventDate().equals(criteria.date());
    }

    if (criteria.dateFrom() != null && event.getEventDate().isBefore(criteria.dateFrom())) {
      return false;
    }

    if (criteria.dateTo() != null && event.getEventDate().isAfter(criteria.dateTo())) {
      return false;
    }

    return criteria.dateFrom() != null || criteria.dateTo() != null || isUpcomingEvent(event);
  }

  private boolean isUpcomingEvent(Event event) {
    LocalDate today = LocalDate.now();
    if (event.getEventDate().isAfter(today)) {
      return true;
    }

    if (!event.getEventDate().equals(today)) {
      return false;
    }

    LocalTime now = LocalTime.now();
    if (event.getEndTime() != null) {
      return !event.getEndTime().isBefore(now);
    }

    if (event.getStartTime() != null) {
      return !event.getStartTime().isBefore(now);
    }

    return true;
  }

  private boolean hasDateFilter(SearchCriteria criteria) {
    return criteria.date() != null || criteria.dateFrom() != null || criteria.dateTo() != null;
  }

  private boolean matchesAutonomousCommunity(String province, String autonomousCommunity) {
    if (autonomousCommunity == null) {
      return true;
    }

    return SpanishAutonomousCommunities.provinceBelongsTo(autonomousCommunity, province);
  }

  private boolean matchesCity(String sourceCity, String requestedCity) {
    if (requestedCity == null) {
      return true;
    }

    return normalizeForMatching(sourceCity).contains(normalizeForMatching(requestedCity));
  }

  private SearchCriteria normalizeCriteria(SearchCriteria criteria) {
    SearchCriteria safeCriteria = criteria == null
      ? new SearchCriteria(null, null, null, null, null, null, null, null, null, null, null, null)
      : criteria;

    if (safeCriteria.endTime() != null && safeCriteria.startTime() == null) {
      throw new IllegalArgumentException("The start time is obligatory when the end time is informed");
    }

    if (safeCriteria.startTime() != null && safeCriteria.endTime() != null
      && !safeCriteria.startTime().isBefore(safeCriteria.endTime())) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }

    if (safeCriteria.peopleCount() != null && safeCriteria.peopleCount() < 1) {
      throw new IllegalArgumentException("The people count must be greater than zero");
    }

    if (safeCriteria.maxBudget() != null && safeCriteria.maxBudget().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("The maximum budget cannot be negative");
    }

    LocalDate dateFrom = safeCriteria.dateFrom();
    LocalDate dateTo = safeCriteria.dateTo();

    if (safeCriteria.date() != null) {
      dateFrom = null;
      dateTo = null;
    } else if (dateFrom != null || dateTo != null) {
      if (dateFrom == null) {
        dateFrom = dateTo;
      }

      if (dateTo == null) {
        dateTo = dateFrom;
      }

      if (dateTo.isBefore(dateFrom)) {
        throw new IllegalArgumentException("The start date must be before or equal to the end date");
      }

      if (ChronoUnit.DAYS.between(dateFrom, dateTo) + 1 > MAX_SPACE_DATE_RANGE_DAYS) {
        throw new IllegalArgumentException("The date range is too long");
      }
    }

    String normalizedAutonomousCommunity = normalizeAutonomousCommunity(safeCriteria.autonomousCommunity());

    return new SearchCriteria(
      normalizeNullableText(safeCriteria.city()),
      normalizeNullableText(safeCriteria.province()),
      normalizedAutonomousCommunity,
      safeCriteria.date(),
      dateFrom,
      dateTo,
      safeCriteria.startTime(),
      safeCriteria.endTime(),
      safeCriteria.spaceType(),
      safeCriteria.peopleCount(),
      normalizeNullableText(safeCriteria.musicalGenre()),
      safeCriteria.maxBudget() == null ? null : safeCriteria.maxBudget().setScale(2, RoundingMode.HALF_UP)
    );
  }

  private DetectedSearchDataView detectSearchData(String originalText) {
    String normalizedText = normalizeForMatching(originalText);
    String autonomousCommunity = SpanishAutonomousCommunities.detectIn(originalText).orElse(null);
    String normalizedLocationText = SpanishAutonomousCommunities.removeCommunityAliases(originalText, autonomousCommunity);
    MusicalSpaceType spaceType = detectSpaceType(normalizedText);
    SearchIntent intent = detectIntent(normalizedText, spaceType);
    SearchNeedType needType = detectNeedType(normalizedText, intent, spaceType);
    TimeRange timeRange = normalizeDetectedTimeRange(detectTimeRange(originalText));
    DateRange dateRange = detectDateRange(normalizedText);
    LocalDate exactDate = dateRange.dateFrom() == null && dateRange.dateTo() == null
      ? detectDate(originalText, normalizedText)
      : null;

    return new DetectedSearchDataView(
      detectCity(normalizedLocationText),
      detectProvince(normalizedLocationText),
      autonomousCommunity,
      intent,
      timeRange.startTime(),
      timeRange.endTime(),
      exactDate,
      dateRange.dateFrom(),
      dateRange.dateTo(),
      needType,
      detectPeopleCount(normalizedText),
      detectMaxBudget(normalizedText),
      spaceType,
      detectMusicalGenre(normalizedText)
    );
  }

  private NaturalLanguageParsingOutcome parseNaturalLanguage(String originalText) {
    DetectedSearchDataView localDetectedData = detectSearchData(originalText);

    if (!shouldUseAiParser()) {
      return localParsingOutcome(localDetectedData);
    }

    try {
      AiSearchParseResult aiResult = aiSearchParserService.parse(originalText);
      DetectedSearchDataView aiDetectedData = DetectedSearchDataView.from(aiResult);
      return new NaturalLanguageParsingOutcome(
        mergeDetectedData(aiDetectedData, localDetectedData),
        parserLabel(aiResult.usedProvider(), aiDetectedData, localDetectedData),
        aiResult.confidence()
      );
    } catch (AiSearchParserException e) {
      logger.warn("Falling back to local natural language parser: {}", e.getMessage());
      return localParsingOutcome(localDetectedData);
    }
  }

  private NaturalLanguageParsingOutcome localParsingOutcome(DetectedSearchDataView detectedData) {
    return new NaturalLanguageParsingOutcome(
      detectedData,
      "LOCAL",
      null
    );
  }

  private DetectedSearchDataView mergeDetectedData(
    DetectedSearchDataView primary,
    DetectedSearchDataView fallback
  ) {
    if (primary == null) {
      return fallback;
    }

    if (fallback == null) {
      return primary;
    }

    LocalDate detectedDateFrom = firstNonNull(primary.detectedDateFrom(), fallback.detectedDateFrom());
    LocalDate detectedDateTo = firstNonNull(primary.detectedDateTo(), fallback.detectedDateTo());
    LocalDate detectedDate = detectedDateFrom != null || detectedDateTo != null
      ? null
      : firstNonNull(primary.detectedDate(), fallback.detectedDate());

    return new DetectedSearchDataView(
      firstNonNull(primary.detectedCity(), fallback.detectedCity()),
      firstNonNull(primary.detectedProvince(), fallback.detectedProvince()),
      firstNonNull(primary.detectedAutonomousCommunity(), fallback.detectedAutonomousCommunity()),
      mergeIntent(primary.detectedIntent(), fallback.detectedIntent()),
      firstNonNull(primary.detectedStartTime(), fallback.detectedStartTime()),
      firstNonNull(primary.detectedEndTime(), fallback.detectedEndTime()),
      detectedDate,
      detectedDateFrom,
      detectedDateTo,
      firstNonNull(primary.detectedNeedType(), fallback.detectedNeedType()),
      firstNonNull(primary.detectedPeopleCount(), fallback.detectedPeopleCount()),
      firstNonNull(primary.detectedMaxBudget(), fallback.detectedMaxBudget()),
      firstNonNull(primary.detectedSpaceType(), fallback.detectedSpaceType()),
      firstNonNull(primary.detectedMusicalGenre(), fallback.detectedMusicalGenre())
    );
  }

  private SearchIntent mergeIntent(SearchIntent primary, SearchIntent fallback) {
    if (primary == null) {
      return fallback;
    }

    if (SearchIntent.BOTH.equals(primary)
      && fallback != null
      && !SearchIntent.BOTH.equals(fallback)) {
      return fallback;
    }

    return primary;
  }

  private String parserLabel(
    String primaryParser,
    DetectedSearchDataView primary,
    DetectedSearchDataView fallback
  ) {
    if (primaryParser == null || primary == null || fallback == null) {
      return primaryParser;
    }

    return hasFallbackContribution(primary, fallback) ? primaryParser + "+LOCAL" : primaryParser;
  }

  private boolean hasFallbackContribution(DetectedSearchDataView primary, DetectedSearchDataView fallback) {
    return (primary.detectedCity() == null && fallback.detectedCity() != null)
      || (primary.detectedProvince() == null && fallback.detectedProvince() != null)
      || (primary.detectedAutonomousCommunity() == null && fallback.detectedAutonomousCommunity() != null)
      || intentNeedsFallbackContribution(primary.detectedIntent(), fallback.detectedIntent())
      || (primary.detectedStartTime() == null && fallback.detectedStartTime() != null)
      || (primary.detectedEndTime() == null && fallback.detectedEndTime() != null)
      || (primary.detectedDate() == null && fallback.detectedDate() != null)
      || (primary.detectedDateFrom() == null && fallback.detectedDateFrom() != null)
      || (primary.detectedDateTo() == null && fallback.detectedDateTo() != null)
      || (primary.detectedNeedType() == null && fallback.detectedNeedType() != null)
      || (primary.detectedPeopleCount() == null && fallback.detectedPeopleCount() != null)
      || (primary.detectedMaxBudget() == null && fallback.detectedMaxBudget() != null)
      || (primary.detectedSpaceType() == null && fallback.detectedSpaceType() != null)
      || (primary.detectedMusicalGenre() == null && fallback.detectedMusicalGenre() != null);
  }

  private boolean intentNeedsFallbackContribution(SearchIntent primary, SearchIntent fallback) {
    return (primary == null && fallback != null)
      || (SearchIntent.BOTH.equals(primary)
        && fallback != null
        && !SearchIntent.BOTH.equals(fallback));
  }

  private <T> T firstNonNull(T primary, T fallback) {
    return primary == null ? fallback : primary;
  }

  private boolean shouldUseAiParser() {
    return aiSearchParserService.isEnabled();
  }

  private SearchIntent detectIntent(String normalizedText, MusicalSpaceType spaceType) {
    boolean mentionsEvent = containsAny(normalizedText, EVENT_KEYWORDS);
    boolean mentionsSpace = spaceType != null || containsAny(normalizedText, SPACE_KEYWORDS);

    if (mentionsEvent && mentionsSpace) {
      if (containsAny(normalizedText, PERFORMANCE_SPACE_KEYWORDS)
        || (containsAny(normalizedText, SPACE_REQUEST_KEYWORDS) && containsAny(normalizedText, SPACE_KEYWORDS))) {
        return SearchIntent.SPACE;
      }

      if (containsAny(normalizedText, EVENT_DISCOVERY_KEYWORDS)) {
        return SearchIntent.EVENT;
      }

      if (spaceType != null) {
        return SearchIntent.SPACE;
      }

      return SearchIntent.BOTH;
    }

    if (mentionsEvent && !mentionsSpace) {
      return SearchIntent.EVENT;
    }

    if (mentionsSpace && !mentionsEvent) {
      return SearchIntent.SPACE;
    }

    return SearchIntent.BOTH;
  }

  private SearchNeedType detectNeedType(String normalizedText, SearchIntent intent, MusicalSpaceType spaceType) {
    if (spaceType == MusicalSpaceType.REHEARSAL_ROOM || containsAny(normalizedText, List.of("ensayo", "ensaiar", "ensaio", "rehearsal"))) {
      return SearchNeedType.REHEARSAL;
    }

    if (spaceType == MusicalSpaceType.RECORDING_STUDIO || containsAny(normalizedText, List.of("grabacion", "gravacion", "grabar", "gravar", "recording"))) {
      return SearchNeedType.RECORDING;
    }

    if (spaceType == MusicalSpaceType.CLASSROOM || containsAny(normalizedText, List.of("clase", "taller", "obradoiro", "formacion", "class", "workshop"))) {
      return SearchNeedType.CLASS;
    }

    if (intent == SearchIntent.EVENT || containsAny(normalizedText, List.of("concierto", "concerto", "festival", "directo", "gig"))) {
      return SearchNeedType.PERFORMANCE;
    }

    if (containsAny(normalizedText, List.of("buscar", "busco", "descubrir", "atopar", "find", "search"))) {
      return SearchNeedType.GENERAL_DISCOVERY;
    }

    return null;
  }

  private MusicalSpaceType detectSpaceType(String normalizedText) {
    return SPACE_TYPE_KEYWORDS.stream()
      .filter(group -> containsAny(normalizedText, group.keywords()))
      .map(SpaceTypeKeywordGroup::spaceType)
      .findFirst()
      .orElse(null);
  }

  private String detectMusicalGenre(String normalizedText) {
    return MUSICAL_GENRE_KEYWORDS.entrySet().stream()
      .filter(entry -> containsWholeTermAny(normalizedText, List.of(entry.getKey())))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElse(null);
  }

  private String detectCity(String normalizedText) {
    return getKnownPublicCities().stream()
      .sorted(Comparator.comparingInt(String::length).reversed())
      .filter(city -> cityMatches(normalizedText, city))
      .findFirst()
      .orElse(null);
  }

  private String detectProvince(String normalizedText) {
    return getKnownPublicProvinces().stream()
      .sorted(Comparator.comparingInt(String::length).reversed())
      .filter(province -> cityMatches(normalizedText, province))
      .findFirst()
      .orElse(null);
  }

  private DateRange detectDateRange(String normalizedText) {
    LocalDate today = LocalDate.now();

    if (containsAny(normalizedText, UPCOMING_DAYS_KEYWORDS)) {
      return new DateRange(today, today.plusDays(UPCOMING_DAYS_RANGE_DAYS));
    }

    return new DateRange(null, null);
  }

  private LocalDate detectDate(String originalText, String normalizedText) {
    LocalDate today = LocalDate.now();

    if (containsAny(normalizedText, DAY_AFTER_TOMORROW_KEYWORDS)) {
      return today.plusDays(2);
    }

    if (mentionsTomorrowDate(normalizedText)) {
      return today.plusDays(1);
    }

    if (containsAny(normalizedText, TODAY_KEYWORDS)) {
      return today;
    }

    if (containsAny(normalizedText, WEEKEND_KEYWORDS)) {
      return today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    Matcher isoMatcher = ISO_DATE_PATTERN.matcher(originalText);
    if (isoMatcher.find()) {
      try {
        return LocalDate.of(
          Integer.parseInt(isoMatcher.group(1)),
          Integer.parseInt(isoMatcher.group(2)),
          Integer.parseInt(isoMatcher.group(3))
        );
      } catch (DateTimeException e) {
        return null;
      }
    }

    Matcher dmyMatcher = DMY_DATE_PATTERN.matcher(originalText);
    if (dmyMatcher.find()) {
      int day = Integer.parseInt(dmyMatcher.group(1));
      int month = Integer.parseInt(dmyMatcher.group(2));
      String yearGroup = dmyMatcher.group(3);

      if (yearGroup != null) {
        int parsedYear = Integer.parseInt(yearGroup);
        if (yearGroup.length() == 2) {
          parsedYear += 2000;
        }
        try {
          return LocalDate.of(parsedYear, month, day);
        } catch (DateTimeException e) {
          return null;
        }
      }

      try {
        LocalDate candidate = LocalDate.of(today.getYear(), month, day);
        return candidate.isBefore(today) ? candidate.plusYears(1) : candidate;
      } catch (DateTimeException e) {
        return null;
      }
    }

    return WEEKDAY_KEYWORDS.entrySet().stream()
      .filter(entry -> normalizedText.contains(entry.getKey()))
      .map(entry -> today.with(TemporalAdjusters.nextOrSame(entry.getValue())))
      .findFirst()
      .orElse(null);
  }

  private TimeRange detectTimeRange(String originalText) {
    String normalizedText = normalizeForMatching(originalText);

    Matcher colonRangeMatcher = RANGE_COLON_PATTERN.matcher(normalizedText);
    if (colonRangeMatcher.find()) {
      return new TimeRange(
        parseTime(colonRangeMatcher.group(1), colonRangeMatcher.group(2)),
        parseTime(colonRangeMatcher.group(3), colonRangeMatcher.group(4))
      );
    }

    Matcher hourRangeMatcher = RANGE_HOUR_PATTERN.matcher(normalizedText);
    if (hourRangeMatcher.find()) {
      return new TimeRange(
        parseTime(hourRangeMatcher.group(1), null),
        parseTime(hourRangeMatcher.group(2), null)
      );
    }

    Matcher plainHourRangeMatcher = RANGE_PLAIN_HOUR_PATTERN.matcher(normalizedText);
    if (plainHourRangeMatcher.find()) {
      return new TimeRange(
        parseTime(plainHourRangeMatcher.group(1), plainHourRangeMatcher.group(2)),
        parseTime(plainHourRangeMatcher.group(3), plainHourRangeMatcher.group(4))
      );
    }

    Matcher startMatcher = START_TIME_PATTERN.matcher(normalizedText);
    LocalTime startTime = startMatcher.find() ? parseTime(startMatcher.group(1), startMatcher.group(2)) : null;

    Matcher endMatcher = END_TIME_PATTERN.matcher(normalizedText);
    LocalTime endTime = endMatcher.find() ? parseTime(endMatcher.group(1), endMatcher.group(2)) : null;

    if (startTime != null || endTime != null) {
      return new TimeRange(startTime, endTime);
    }

    if (containsAny(normalizedText, MORNING_TIME_KEYWORDS)) {
      return new TimeRange(LocalTime.of(9, 0), LocalTime.of(14, 0));
    }

    if (containsAny(normalizedText, AFTERNOON_TIME_KEYWORDS)) {
      return new TimeRange(LocalTime.of(16, 0), LocalTime.of(20, 0));
    }

    if (containsAny(normalizedText, NIGHT_TIME_KEYWORDS)) {
      return new TimeRange(LocalTime.of(20, 0), LocalTime.of(23, 59));
    }

    return new TimeRange(null, null);
  }

  private Integer detectPeopleCount(String normalizedText) {
    Matcher matcher = PEOPLE_PATTERN.matcher(normalizedText);
    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    Matcher prefixMatcher = PEOPLE_PREFIX_PATTERN.matcher(normalizedText);
    if (prefixMatcher.find()) {
      return Integer.parseInt(prefixMatcher.group(1));
    }

    return NUMBER_WORDS.entrySet().stream()
      .filter(entry -> containsAny(normalizedText, List.of(
        "para " + entry.getKey(),
        "somos " + entry.getKey(),
        "seremos " + entry.getKey(),
        "grupo de " + entry.getKey(),
        "group of " + entry.getKey(),
        "for " + entry.getKey(),
        entry.getKey() + " personas",
        entry.getKey() + " persoas",
        entry.getKey() + " musicos",
        entry.getKey() + " integrantes",
        entry.getKey() + " people",
        entry.getKey() + " members",
        entry.getKey() + " membros"
      )))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElse(null);
  }

  private BigDecimal detectMaxBudget(String normalizedText) {
    for (Pattern pattern : BUDGET_PATTERNS) {
      Matcher matcher = pattern.matcher(normalizedText);
      if (matcher.find()) {
        return new BigDecimal(matcher.group(1).replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
      }
    }

    return null;
  }

  private LocalTime parseTime(String hourGroup, String minuteGroup) {
    try {
      int hour = Integer.parseInt(hourGroup);
      int minute = minuteGroup == null ? 0 : Integer.parseInt(minuteGroup);
      return LocalTime.of(hour, minute);
    } catch (DateTimeException e) {
      return null;
    }
  }

  private TimeRange normalizeDetectedTimeRange(TimeRange timeRange) {
    if (timeRange == null) {
      return new TimeRange(null, null);
    }

    if (timeRange.endTime() != null && timeRange.startTime() == null) {
      return new TimeRange(null, null);
    }

    if (timeRange.startTime() != null
      && timeRange.endTime() != null
      && !timeRange.startTime().isBefore(timeRange.endTime())) {
      return new TimeRange(null, null);
    }

    return timeRange;
  }

  private boolean mentionsTomorrowDate(String normalizedText) {
    String withoutMorningPhrases = removeKnownPhrases(normalizedText, MORNING_TIME_KEYWORDS);
    return containsWholeTermAny(withoutMorningPhrases, TOMORROW_KEYWORDS);
  }

  private String removeKnownPhrases(String normalizedText, List<String> phrases) {
    String result = normalizedText;

    for (String phrase : phrases) {
      result = result.replace(normalizeForMatching(phrase), " ");
    }

    return normalizeForMatching(result);
  }

  private boolean cityMatches(String normalizedText, String city) {
    String normalizedCity = normalizeForMatching(city);
    if (normalizedCity.isEmpty()) {
      return false;
    }

    if (normalizedText.contains(normalizedCity)) {
      return true;
    }

    String withoutArticle = normalizedCity.replaceFirst("^(a|o|el|la|os|as)\\s+", "");
    if (!withoutArticle.equals(normalizedCity) && normalizedText.contains(withoutArticle)) {
      return true;
    }

    String firstSignificantToken = Stream.of(normalizedCity.split("\\s+"))
      .filter(token -> token.length() >= 4)
      .findFirst()
      .orElse("");

    return !firstSignificantToken.isEmpty() && normalizedText.contains(firstSignificantToken);
  }

  private List<String> getKnownPublicCities() {
    return Stream.concat(
        musicalSpaceDao.findPublic().stream().map(space -> space.getLocation().getCity()),
        eventDao.findPublished(new EventFilterParams(null, null, null, null, null, null, null)).stream().map(Event::getCity)
      )
      .filter(Objects::nonNull)
      .map(String::trim)
      .filter(city -> !city.isEmpty())
      .distinct()
      .toList();
  }

  private List<String> getKnownPublicProvinces() {
    return Stream.concat(
        musicalSpaceDao.findPublic().stream().map(space -> space.getLocation().getProvince()),
        eventDao.findPublished(new EventFilterParams(null, null, null, null, null, null, null)).stream().map(Event::getProvince)
      )
      .filter(Objects::nonNull)
      .map(String::trim)
      .filter(province -> !province.isEmpty())
      .distinct()
      .toList();
  }

  private void saveStructuredSearchIfPossible(SearchCriteria criteria) {
    if (!hasAnyFilter(criteria)) {
      return;
    }

    findOptionalCurrentUser().ifPresent(user -> searchDao.save(new Search(
      buildStructuredOriginalText(criteria),
      criteria.city(),
      SearchIntent.BOTH,
      criteria.startTime(),
      criteria.endTime(),
      criteria.date(),
      null,
      criteria.peopleCount(),
      criteria.maxBudget(),
      criteria.spaceType(),
      criteria.musicalGenre(),
      user
    )));
  }

  private void saveNaturalLanguageSearchIfPossible(String originalText, DetectedSearchDataView detectedData) {
    findOptionalCurrentUser().ifPresent(user -> searchDao.save(new Search(
      originalText,
      detectedData.detectedCity(),
      detectedData.detectedIntent(),
      detectedData.detectedStartTime(),
      detectedData.detectedEndTime(),
      detectedData.detectedDate(),
      detectedData.detectedNeedType(),
      detectedData.detectedPeopleCount(),
      detectedData.detectedMaxBudget(),
      detectedData.detectedSpaceType(),
      detectedData.detectedMusicalGenre(),
      user
    )));
  }

  private String buildStructuredOriginalText(SearchCriteria criteria) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("city", criteria.city());
    values.put("province", criteria.province());
    values.put("autonomousCommunity", criteria.autonomousCommunity());
    values.put("date", criteria.date());
    values.put("dateFrom", criteria.dateFrom());
    values.put("dateTo", criteria.dateTo());
    values.put("startTime", criteria.startTime());
    values.put("endTime", criteria.endTime());
    values.put("spaceType", criteria.spaceType());
    values.put("peopleCount", criteria.peopleCount());
    values.put("musicalGenre", criteria.musicalGenre());
    values.put("maxBudget", criteria.maxBudget());

    return values.entrySet().stream()
      .filter(entry -> entry.getValue() != null)
      .map(entry -> entry.getKey() + "=" + entry.getValue())
      .reduce((left, right) -> left + "; " + right)
      .orElse(null);
  }

  private boolean hasAnyFilter(SearchCriteria criteria) {
    return criteria.city() != null
      || criteria.province() != null
      || criteria.autonomousCommunity() != null
      || criteria.date() != null
      || criteria.dateFrom() != null
      || criteria.dateTo() != null
      || criteria.startTime() != null
      || criteria.endTime() != null
      || criteria.spaceType() != null
      || criteria.peopleCount() != null
      || criteria.musicalGenre() != null
      || criteria.maxBudget() != null;
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private Optional<User> findOptionalCurrentUser() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();

    if (currentUserLogin == null) {
      return Optional.empty();
    }

    return userDao.findByEmail(currentUserLogin.toLowerCase());
  }

  private String normalizeRequiredText(String value, String fieldName) {
    String normalized = normalizeNullableText(value);

    if (normalized == null) {
      throw new IllegalArgumentException("The " + fieldName + " is obligatory");
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

  private String normalizeAutonomousCommunity(String value) {
    String normalized = normalizeNullableText(value);
    if (normalized == null) {
      return null;
    }

    return SpanishAutonomousCommunities.canonicalName(normalized).orElse(normalized);
  }

  private String normalizeForMatching(String value) {
    if (value == null) {
      return "";
    }

    String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase();

    return normalized.replaceAll("\\s+", " ").trim();
  }

  private boolean containsAny(String normalizedText, List<String> candidates) {
    return candidates.stream().map(this::normalizeForMatching).anyMatch(normalizedText::contains);
  }

  private boolean containsWholeTermAny(String normalizedText, List<String> candidates) {
    return candidates.stream()
      .map(this::normalizeForMatching)
      .filter(candidate -> !candidate.isEmpty())
      .anyMatch(candidate -> Pattern.compile("(^|\\s)" + Pattern.quote(candidate) + "(\\s|$)").matcher(normalizedText).find());
  }

  private record TimeRange(LocalTime startTime, LocalTime endTime) {
  }

  private record DateRange(LocalDate dateFrom, LocalDate dateTo) {
  }

  private record SpaceTypeKeywordGroup(MusicalSpaceType spaceType, List<String> keywords) {
  }

  private record SpaceSearchCandidate(MusicalSpace musicalSpace, BigDecimal estimatedPrice) {
  }

  private record NaturalLanguageParsingOutcome(
    DetectedSearchDataView detectedData,
    String parserUsed,
    Double parserConfidence
  ) {
  }
}
