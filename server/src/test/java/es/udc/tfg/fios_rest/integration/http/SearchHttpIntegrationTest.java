package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserService;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchHttpIntegrationTest extends IntegrationTestSupport {

  @MockBean
  private AiSearchParserService aiSearchParserService;

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  @Autowired
  private InstrumentDao instrumentDao;

  @Test
  void publicSearchDoesNotReturnNonVisibleSpacesOrEvents() throws Exception {
    var manager = createUser("search-public-manager@example.com", "password123");
    var visibleSpace = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var hiddenSpace = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);
    createPublishedEvent("Visible Search Event", "Santiago de Compostela", true);

    Event draftEvent = new Event(
      "Hidden Draft Event",
      "This event should stay hidden from public search",
      LocalDate.now().plusDays(15),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "rock",
      100,
      BigDecimal.valueOf(12.00),
      "draft.png",
      EventStatus.DRAFT,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Oculta",
      42.88,
      -8.54,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Hidden",
      null,
      null,
      null,
      null,
      null,
      manager
    );
    eventDao.save(draftEvent);

    mockMvc.perform(get("/api/search")
        .param("city", "Santiago de Compostela"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces[?(@.name=='" + visibleSpace.getName() + "')]").exists())
      .andExpect(jsonPath("$.spaces[?(@.name=='" + hiddenSpace.getName() + "')]").doesNotExist())
      .andExpect(jsonPath("$.events[?(@.title=='Visible Search Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Hidden Draft Event')]").doesNotExist());
  }

  @Test
  void publicSearchUsesCustomAvailabilityPriceForDatedBudgetFilters() throws Exception {
    var manager = createUser("search-custom-price-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(5);
    createException(
      space,
      date,
      LocalTime.of(18, 0),
      LocalTime.of(20, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      BigDecimal.valueOf(30)
    );

    mockMvc.perform(get("/api/search")
        .param("city", "Santiago de Compostela")
        .param("date", date.toString())
        .param("startTime", "18:00")
        .param("endTime", "20:00")
        .param("maxBudget", "30"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces.length()").value(1))
      .andExpect(jsonPath("$.spaces[0].name").value(space.getName()))
      .andExpect(jsonPath("$.spaces[0].estimatedPrice").value(30.0));

    mockMvc.perform(get("/api/search")
        .param("city", "Santiago de Compostela")
        .param("date", date.toString())
        .param("startTime", "18:00")
        .param("endTime", "20:00")
        .param("maxBudget", "20"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces[?(@.name=='" + space.getName() + "')]").doesNotExist());
  }

  @Test
  void naturalLanguageSearchReturnsBandsAndRecruitmentsFromBackend() throws Exception {
    var leader = createUser("search-band-leader@example.com", "password123");
    Band band = bandDao.save(new Band(
      "Funk Vigo Search Band",
      "Proyecto abierto de funk",
      "funk",
      "Vigo",
      "band.png"
    ));
    Instrument instrument = instrumentDao.save(new Instrument(
      "Guitarra Search Backend " + Math.abs("search-band-leader@example.com".hashCode()),
      InstrumentCategory.STRINGS
    ));
    bandRecruitmentDao.save(new BandRecruitment(
      "Buscamos guitarrista en Vigo",
      "Vacante para tocar funk",
      "Guitarrista",
      BandRecruitmentLevel.INTERMEDIATE,
      "Vigo",
      1,
      band,
      instrument,
      leader
    ));

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "bandas de funk en Vigo"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.bands[?(@.name=='Funk Vigo Search Band')]").exists())
      .andExpect(jsonPath("$.recruitments[?(@.title=='Buscamos guitarrista en Vigo')]").exists());
  }

  @Test
  void anonymousNaturalLanguageSearchUsesLocalParser() throws Exception {
    var manager = createUser("search-anon-manager@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate expectedDate = LocalDate.now().plusDays(1);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Necesito local de ensayo en Santiago de Compostela para cuatro musicos presupuesto de 40 euros manana de 18 a 20"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Santiago de Compostela"))
      .andExpect(jsonPath("$.detectedData.detectedDate").value(expectedDate.toString()))
      .andExpect(jsonPath("$.detectedData.detectedStartTime").value("18:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedEndTime").value("20:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(4))
      .andExpect(jsonPath("$.detectedData.detectedMaxBudget").value(40.0))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value("REHEARSAL_ROOM"));
  }

  @Test
  void anonymousNaturalLanguageSearchDoesNotTreatMorningAsTomorrow() throws Exception {
    var manager = createUser("search-morning-manager@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco local de ensayo en Santiago por la mañana para cuatro musicos"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedDate").doesNotExist())
      .andExpect(jsonPath("$.detectedData.detectedStartTime").value("09:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedEndTime").value("14:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(4));
  }

  @Test
  void anonymousNaturalLanguageSearchFindsGalicianCorunaRehearsalRoomUnderBudgetWhenItExists() throws Exception {
    var manager = createUser("search-galician-coruna-manager@example.com", "password123");
    var matchingSpace = createApprovedSpaceWithLocation(
      manager,
      "Galician Coruna Budget Room",
      "A Coru\u00f1a",
      "A Coru\u00f1a",
      MusicalSpaceType.REHEARSAL_ROOM,
      6
    );
    var expensiveSpace = createApprovedSpaceWithLocation(
      manager,
      "Galician Coruna Expensive Room",
      "A Coru\u00f1a",
      "A Coru\u00f1a",
      MusicalSpaceType.REHEARSAL_ROOM,
      6
    );
    createSchedule(matchingSpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(19));
    createSchedule(expensiveSpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(24));

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco un local de ensaio na Coru\u00f1a para 5 persoas por menos de 20 euros"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("A Coru\u00f1a"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(5))
      .andExpect(jsonPath("$.detectedData.detectedMaxBudget").value(20.0))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value(MusicalSpaceType.REHEARSAL_ROOM.name()))
      .andExpect(jsonPath("$.spaces[?(@.id==" + matchingSpace.getId() + ")]").exists())
      .andExpect(jsonPath("$.spaces[?(@.id==" + expensiveSpace.getId() + ")]").doesNotExist())
      .andExpect(jsonPath("$.events.length()").value(0));
  }

  @Test
  void anonymousNaturalLanguageSearchTreatsConcertDiscoveryAsEvent() throws Exception {
    var manager = createUser("search-concert-event-manager@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    createPublishedEvent("Rock Search Concert", "Santiago de Compostela", true);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Conciertos de jazz en Santiago"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("EVENT"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Santiago de Compostela"))
      .andExpect(jsonPath("$.detectedData.detectedMusicalGenre").value("jazz"))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").doesNotExist())
      .andExpect(jsonPath("$.spaces.length()").value(0));
  }

  @Test
  void anonymousNaturalLanguageSearchTreatsPerformanceVenueAsSpace() throws Exception {
    var manager = createUser("search-concert-space-manager@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco sala para conciertos en Santiago de 18 a 20"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Santiago de Compostela"))
      .andExpect(jsonPath("$.detectedData.detectedStartTime").value("18:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedEndTime").value("20:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value(MusicalSpaceType.CONCERT_HALL.name()))
      .andExpect(jsonPath("$.events.length()").value(0));
  }

  @Test
  void authenticatedNaturalLanguageSearchMergesGeminiWithLocalForGalicianCorunaQuery() throws Exception {
    var manager = createUser("search-ai-galician-coruna-manager@example.com", "password123");
    var user = createUser("search-ai-galician-coruna-user@example.com", "password123");
    var matchingSpace = createApprovedSpaceWithLocation(
      manager,
      "Gemini Galician Coruna Budget Room",
      "A Coru\u00f1a",
      "A Coru\u00f1a",
      MusicalSpaceType.REHEARSAL_ROOM,
      6
    );
    createSchedule(matchingSpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(19));

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willReturn(new AiSearchParseResult(
        SearchIntent.SPACE,
        "Coru\u00f1a",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.88,
        "GEMINI",
        "GEMINI"
      ));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco un local de ensaio na Coru\u00f1a para 5 persoas por menos de 20 euros"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("GEMINI+LOCAL"))
      .andExpect(jsonPath("$.parserConfidence").value(0.88))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Coru\u00f1a"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(5))
      .andExpect(jsonPath("$.detectedData.detectedMaxBudget").value(20.0))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value(MusicalSpaceType.REHEARSAL_ROOM.name()))
      .andExpect(jsonPath("$.spaces[?(@.id==" + matchingSpace.getId() + ")]").exists())
      .andExpect(jsonPath("$.events.length()").value(0));

    verify(aiSearchParserService).parse(anyString());
  }

  @Test
  void authenticatedNaturalLanguageSearchMergesAiParserWithLocalFallback() throws Exception {
    var manager = createUser("search-ai-merge-manager@example.com", "password123");
    var user = createUser("search-ai-merge-user@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate expectedDate = LocalDate.now().plusDays(1);
    String query = "Busco local de ensayo en Santiago de Compostela para cinco musicos presupuesto de 45 euros manana de 18 a 20";

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willReturn(new AiSearchParseResult(
        SearchIntent.SPACE,
        "Santiago de Compostela",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.82,
        "GEMINI",
        "GEMINI"
      ));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", query))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("GEMINI+LOCAL"))
      .andExpect(jsonPath("$.parserConfidence").value(0.82))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Santiago de Compostela"))
      .andExpect(jsonPath("$.detectedData.detectedDate").value(expectedDate.toString()))
      .andExpect(jsonPath("$.detectedData.detectedStartTime").value("18:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedEndTime").value("20:00:00"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(5))
      .andExpect(jsonPath("$.detectedData.detectedMaxBudget").value(45.0))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value(MusicalSpaceType.REHEARSAL_ROOM.name()));

    verify(aiSearchParserService).parse(anyString());
  }

  @Test
  void authenticatedNaturalLanguageSearchPrefersLocalSpecificIntentOverAmbiguousAiIntent() throws Exception {
    var manager = createUser("search-ai-intent-manager@example.com", "password123");
    var user = createUser("search-ai-intent-user@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willReturn(new AiSearchParseResult(
        SearchIntent.BOTH,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.74,
        "GEMINI",
        "GEMINI"
      ));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco un local para ensayar"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("GEMINI+LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedSpaceType").value(MusicalSpaceType.REHEARSAL_ROOM.name()))
      .andExpect(jsonPath("$.events.length()").value(0));

    verify(aiSearchParserService).parse(anyString());
  }

  @Test
  void authenticatedNaturalLanguageSearchUsesLocalParserWhenAiIsDisabledAndStoresHistory() throws Exception {
    var manager = createUser("search-disabled-manager@example.com", "password123");
    var user = createUser("search-disabled-user@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    String query = "Busco una sala de ensayo en Santiago de Compostela para 3 personas";

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", query))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"));

    mockMvc.perform(get("/api/search/history")
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].originalText").value(query))
      .andExpect(jsonPath("$[0].detectedData.detectedIntent").value("SPACE"));

    assertThat(searchDao.findByUser(user.getId())).hasSize(1);
  }

  @Test
  void authenticatedNaturalLanguageSearchFallsBackToLocalParserWhenAiFails() throws Exception {
    var manager = createUser("search-fallback-manager@example.com", "password123");
    var user = createUser("search-fallback-user@example.com", "password123");
    createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willThrow(AiSearchParserException.externalFailure("simulated provider error", null));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco una sala de ensayo en Santiago de Compostela manana"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedCity").value("Santiago de Compostela"));

    verify(aiSearchParserService).parse(anyString());
  }

  @Test
  void publicSearchMatchesCityWithAndWithoutAccentsAcrossSpacesEventsAndBands() throws Exception {
    var manager = createUser("search-accent-manager@example.com", "password123");
    var space = createApprovedSpaceWithLocation(
      manager,
      "Accent Search Space",
      "A Coru\u00f1a",
      "A Coru\u00f1a"
    );
    createPublishedEvent("Accent Search Event", "A Coru\u00f1a", true);
    bandDao.save(new Band(
      "Accent Search Band",
      "Proyecto de rock en Galicia",
      "rock",
      "A Coru\u00f1a",
      "band.png"
    ));

    mockMvc.perform(get("/api/search")
        .param("city", "A Coruna"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces[?(@.id==" + space.getId() + ")]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Accent Search Event')]").exists())
      .andExpect(jsonPath("$.bands[?(@.name=='Accent Search Band')]").exists());

    mockMvc.perform(get("/api/search")
        .param("city", "A Coru\u00f1a"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces[?(@.id==" + space.getId() + ")]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Accent Search Event')]").exists())
      .andExpect(jsonPath("$.bands[?(@.name=='Accent Search Band')]").exists());
  }

  @Test
  void publicSearchFiltersByProvinceWithoutAccents() throws Exception {
    var manager = createUser("search-province-manager@example.com", "password123");
    var matchingSpace = createApprovedSpaceWithLocation(
      manager,
      "Province Search Space",
      "A Coru\u00f1a",
      "Sada"
    );
    var otherSpace = createApprovedSpaceWithLocation(
      manager,
      "Province Other Space",
      "Lugo",
      "Lugo"
    );
    createPublishedEvent("Province Search Event", "Sada", true);

    mockMvc.perform(get("/api/search")
        .param("province", "A Coruna"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaces[?(@.id==" + matchingSpace.getId() + ")]").exists())
      .andExpect(jsonPath("$.spaces[?(@.id==" + otherSpace.getId() + ")]").doesNotExist())
      .andExpect(jsonPath("$.events[?(@.title=='Province Search Event')]").exists());
  }

  @Test
  void anonymousNaturalLanguageSearchFiltersEventsByGaliciaAndUpcomingDays() throws Exception {
    LocalDate expectedFrom = LocalDate.now();
    LocalDate expectedTo = expectedFrom.plusDays(7);
    createPublishedEventWithProvince("Galicia Upcoming Days Event", "Sada", "A Coru\u00f1a", 3);
    createPublishedEventWithProvince("Galicia Outside Upcoming Days Event", "Sada", "A Coru\u00f1a", 10);
    createPublishedEventWithProvince("Andalucia Upcoming Days Event", "Sevilla", "Sevilla", 3);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco eventos musicais en Galicia para os pr\u00f3ximos d\u00edas"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("EVENT"))
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Galicia"))
      .andExpect(jsonPath("$.detectedData.detectedDateFrom").value(expectedFrom.toString()))
      .andExpect(jsonPath("$.detectedData.detectedDateTo").value(expectedTo.toString()))
      .andExpect(jsonPath("$.events[?(@.title=='Galicia Upcoming Days Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Galicia Outside Upcoming Days Event')]").doesNotExist())
      .andExpect(jsonPath("$.events[?(@.title=='Andalucia Upcoming Days Event')]").doesNotExist())
      .andExpect(jsonPath("$.spaces.length()").value(0));
  }

  @Test
  void anonymousNaturalLanguageSearchFiltersSpacesByCommunityWithPeopleAndBudget() throws Exception {
    var manager = createUser("search-community-spaces-manager@example.com", "password123");
    var matchingSpace = createApprovedSpaceWithLocation(
      manager,
      "Galicia Community Budget Room",
      "A Coru\u00f1a",
      "Sada",
      MusicalSpaceType.REHEARSAL_ROOM,
      6
    );
    var otherCommunitySpace = createApprovedSpaceWithLocation(
      manager,
      "Andalucia Community Budget Room",
      "Sevilla",
      "Sevilla",
      MusicalSpaceType.REHEARSAL_ROOM,
      6
    );
    var smallSpace = createApprovedSpaceWithLocation(
      manager,
      "Galicia Community Small Room",
      "Lugo",
      "Lugo",
      MusicalSpaceType.REHEARSAL_ROOM,
      3
    );
    createSchedule(matchingSpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(18));
    createSchedule(otherCommunitySpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(18));
    createSchedule(smallSpace, DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(23, 0), BigDecimal.valueOf(18));

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco un local de ensaio en Galicia para 5 persoas por menos de 20 euros"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedIntent").value("SPACE"))
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Galicia"))
      .andExpect(jsonPath("$.detectedData.detectedPeopleCount").value(5))
      .andExpect(jsonPath("$.detectedData.detectedMaxBudget").value(20.0))
      .andExpect(jsonPath("$.spaces[?(@.id==" + matchingSpace.getId() + ")]").exists())
      .andExpect(jsonPath("$.spaces[?(@.id==" + otherCommunitySpace.getId() + ")]").doesNotExist())
      .andExpect(jsonPath("$.spaces[?(@.id==" + smallSpace.getId() + ")]").doesNotExist())
      .andExpect(jsonPath("$.events.length()").value(0));
  }

  @Test
  void anonymousNaturalLanguageSearchSupportsUniprovincialCommunities() throws Exception {
    createPublishedEventWithProvince("Madrid Community Event", "Madrid", "Madrid", 4);
    createPublishedEventWithProvince("Asturias Community Event", "Oviedo", "Asturias", 4);
    createPublishedEventWithProvince("Outside Uniprovincial Event", "Sada", "A Coru\u00f1a", 4);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "Eventos en Madrid"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Comunidad de Madrid"))
      .andExpect(jsonPath("$.events[?(@.title=='Madrid Community Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Outside Uniprovincial Event')]").doesNotExist());

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "Concertos en Asturias"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Principado de Asturias"))
      .andExpect(jsonPath("$.events[?(@.title=='Asturias Community Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Outside Uniprovincial Event')]").doesNotExist());
  }

  @Test
  void anonymousNaturalLanguageSearchSupportsMultiprovincialAndAliasCommunities() throws Exception {
    createPublishedEventWithProvince("Andalucia Malaga Event", "M\u00e1laga", "M\u00e1laga", 5);
    createPublishedEventWithProvince("Andalucia Sevilla Event", "Sevilla", "Sevilla", 5);
    createPublishedEventWithProvince("Catalunya Alias Event", "Barcelona", "Barcelona", 5);
    createPublishedEventWithProvince("Euskadi Alias Event", "Bilbao", "Bizkaia", 5);
    createPublishedEventWithProvince("Valenciana Alias Event", "Valencia", "Valencia", 5);
    createPublishedEventWithProvince("Other Community Alias Event", "Sada", "A Coru\u00f1a", 5);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "CONCERTOS EN ANDALUCIA"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Andaluc\u00eda"))
      .andExpect(jsonPath("$.events[?(@.title=='Andalucia Malaga Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Andalucia Sevilla Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Other Community Alias Event')]").doesNotExist());

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "eventos en Catalunya"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Catalu\u00f1a"))
      .andExpect(jsonPath("$.events[?(@.title=='Catalunya Alias Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Other Community Alias Event')]").doesNotExist());

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "eventos en Euskadi"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Pa\u00eds Vasco"))
      .andExpect(jsonPath("$.events[?(@.title=='Euskadi Alias Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Other Community Alias Event')]").doesNotExist());

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("text", "eventos en Comunidade Valenciana"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Comunidad Valenciana"))
      .andExpect(jsonPath("$.events[?(@.title=='Valenciana Alias Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Other Community Alias Event')]").doesNotExist());
  }

  @Test
  void naturalLanguageSearchHonorsExplicitLocationOverDetectedCommunity() throws Exception {
    createPublishedEventWithProvince("Detected Galicia Event", "Sada", "A Coru\u00f1a", 3);
    createPublishedEventWithProvince("Explicit Sevilla Event", "Sevilla", "Sevilla", 3);

    mockMvc.perform(post("/api/search/natural-language")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Eventos en Galicia",
          "city", "Sevilla"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Galicia"))
      .andExpect(jsonPath("$.events[?(@.title=='Explicit Sevilla Event')]").exists())
      .andExpect(jsonPath("$.events[?(@.title=='Detected Galicia Event')]").doesNotExist());
  }

  @Test
  void authenticatedNaturalLanguageSearchMergesAiWithLocalCommunityAndUpcomingDays() throws Exception {
    var user = createUser("search-ai-community-user@example.com", "password123");
    LocalDate expectedFrom = LocalDate.now();
    LocalDate expectedTo = expectedFrom.plusDays(7);
    createPublishedEventWithProvince("Ai Local Galicia Upcoming Event", "Sada", "A Coru\u00f1a", 2);

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willReturn(new AiSearchParseResult(
        SearchIntent.EVENT,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.65,
        "GEMINI",
        "GEMINI"
      ));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco eventos musicais en Galicia para os proximos dias"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("GEMINI+LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Galicia"))
      .andExpect(jsonPath("$.detectedData.detectedDateFrom").value(expectedFrom.toString()))
      .andExpect(jsonPath("$.detectedData.detectedDateTo").value(expectedTo.toString()))
      .andExpect(jsonPath("$.events[?(@.title=='Ai Local Galicia Upcoming Event')]").exists());

    verify(aiSearchParserService).parse(anyString());
  }

  @Test
  void authenticatedNaturalLanguageSearchFallsBackToLocalCommunityAndUpcomingDaysWhenAiFails() throws Exception {
    var user = createUser("search-ai-community-fail-user@example.com", "password123");
    createPublishedEventWithProvince("Ai Failure Galicia Upcoming Event", "Sada", "A Coru\u00f1a", 2);

    given(aiSearchParserService.isEnabled()).willReturn(true);
    given(aiSearchParserService.parse(anyString()))
      .willThrow(AiSearchParserException.externalFailure("simulated provider error", null));

    mockMvc.perform(post("/api/search/natural-language")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "text", "Busco eventos musicais en Galicia para os proximos dias"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.parserUsed").value("LOCAL"))
      .andExpect(jsonPath("$.detectedData.detectedAutonomousCommunity").value("Galicia"))
      .andExpect(jsonPath("$.events[?(@.title=='Ai Failure Galicia Upcoming Event')]").exists());

    verify(aiSearchParserService).parse(anyString());
  }

  private MusicalSpace createApprovedSpaceWithLocation(
    User manager,
    String name,
    String province,
    String city
  ) {
    return createApprovedSpaceWithLocation(
      manager,
      name,
      province,
      city,
      MusicalSpaceType.REHEARSAL_ROOM,
      8
    );
  }

  private MusicalSpace createApprovedSpaceWithLocation(
    User manager,
    String name,
    String province,
    String city,
    MusicalSpaceType spaceType,
    int capacity
  ) {
    MusicalSpace space = new MusicalSpace(
      name,
      "Test musical space",
      spaceType,
      capacity,
      "image.png",
      35.0,
      true,
      new MusicalSpaceLocation("Spain", province, city, "Rua Test", "1", null, "15001", 43.3623, -8.4115),
      manager
    );
    space.setApprovalStatus(MusicalSpaceApprovalStatus.APPROVED);
    return musicalSpaceDao.save(space);
  }

  private Event createPublishedEventWithProvince(
    String title,
    String city,
    String province,
    int daysFromNow
  ) {
    User creator = createUser(
      "event-community-" + Math.abs((title + city + province + daysFromNow).hashCode()) + "@example.com",
      "password123"
    );

    Event event = new Event(
      title,
      "Community search event",
      LocalDate.now().plusDays(daysFromNow),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "indie",
      100,
      BigDecimal.valueOf(15.00),
      "poster.png",
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Community Test",
      42.8782,
      -8.5448,
      city,
      province,
      "Spain",
      "Rua Event 1",
      null,
      null,
      null,
      null,
      null,
      creator
    );

    return eventDao.save(event);
  }

  @Test
  void searchHistoryRequiresAuthentication() throws Exception {
    mockMvc.perform(get("/api/search/history"))
      .andExpect(status().isUnauthorized());
  }
}
