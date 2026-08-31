package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.integration.ticketmaster.client.TicketmasterClient;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterDates;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventEmbedded;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventItem;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterImage;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterLocation;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterNamedValue;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterStart;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterVenue;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterFeaturedEventSeedService;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventHttpIntegrationTest extends IntegrationTestSupport {

  @MockBean
  private TicketmasterClient ticketmasterClient;

  @Autowired
  private TicketmasterFeaturedEventSeedService ticketmasterFeaturedEventSeedService;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Test
  void publicEventEndpointsOnlyExposePublishedEvents() throws Exception {
    var manager = createUser("events-public-manager@example.com", "password123");
    var publishedEvent = createPublishedEvent("Public Event", "Santiago de Compostela", true);
    var noCoordinatesEvent = createPublishedEvent("Public Event Without Coordinates", "Santiago de Compostela", false);
    var draftEvent = createEvent("Draft Event", EventStatus.DRAFT, manager, true);
    var cancelledEvent = createEvent("Cancelled Event", EventStatus.CANCELLED, manager, true);

    mockMvc.perform(get("/api/events").param("status", EventStatus.DRAFT.name()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==" + publishedEvent.getId() + ")]").exists())
      .andExpect(jsonPath("$[?(@.id==" + publishedEvent.getId() + ")].description").value("Test event"))
      .andExpect(jsonPath("$[?(@.id==" + publishedEvent.getId() + ")].capacity").value(100))
      .andExpect(jsonPath("$[?(@.id==" + publishedEvent.getId() + ")].province").value("A Coruna"))
      .andExpect(jsonPath("$[?(@.id==" + draftEvent.getId() + ")]").isEmpty())
      .andExpect(jsonPath("$[?(@.id==" + cancelledEvent.getId() + ")]").isEmpty());

    mockMvc.perform(get("/api/events/{id}", draftEvent.getId()))
      .andExpect(status().isNotFound());

    mockMvc.perform(get("/api/events/map"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==" + publishedEvent.getId() + ")]").exists())
      .andExpect(jsonPath("$[?(@.id==" + noCoordinatesEvent.getId() + ")]").isEmpty())
      .andExpect(jsonPath("$[?(@.id==" + draftEvent.getId() + ")]").isEmpty());
  }

  @Test
  void adminCanCreateUpdateAndArchiveEvent() throws Exception {
    var admin = createAdmin("events-admin@example.com", "password123");

    var createResult = mockMvc.perform(post("/api/events")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validEventPayload("Admin Created Event"))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/events/")))
      .andExpect(jsonPath("$.title").value("Admin Created Event"))
      .andExpect(jsonPath("$.createdBy.id").value(admin.getId()))
      .andReturn();

    var eventId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();
    var updatePayload = validEventPayload("Admin Updated Event");
    updatePayload.put("city", "A Coruna");

    mockMvc.perform(put("/api/events/{id}", eventId)
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatePayload)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("Admin Updated Event"))
      .andExpect(jsonPath("$.city").value("A Coruna"));

    mockMvc.perform(delete("/api/events/{id}", eventId)
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isNoContent());

    entityManager.flush();
    entityManager.clear();
    var archivedEvent = eventDao.findById(eventId).orElseThrow();
    assertThat(archivedEvent.getStatus()).isEqualTo(EventStatus.ARCHIVED);
  }

  @Test
  void adminCannotCreateEventBeforeToday() throws Exception {
    var admin = createAdmin("events-admin-past-date@example.com", "password123");
    var payload = validEventPayload("Past Event");
    payload.put("eventDate", LocalDate.now().minusDays(1).toString());

    mockMvc.perform(post("/api/events")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void nonAdminCannotCreateUpdateOrArchiveEvents() throws Exception {
    var user = createUser("events-user@example.com", "password123");
    var event = createPublishedEvent("Protected Event", "Santiago de Compostela", true);

    mockMvc.perform(post("/api/events")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validEventPayload("Unauthorized Created Event"))))
      .andExpect(status().isForbidden());

    mockMvc.perform(put("/api/events/{id}", event.getId())
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validEventPayload("Unauthorized Updated Event"))))
      .andExpect(status().isForbidden());

    mockMvc.perform(delete("/api/events/{id}", event.getId())
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isForbidden());

    entityManager.clear();
    var unchangedEvent = eventDao.findById(event.getId()).orElseThrow();
    assertThat(unchangedEvent.getTitle()).isEqualTo("Protected Event");
    assertThat(unchangedEvent.getStatus()).isEqualTo(EventStatus.PUBLISHED);
  }

  @Test
  void bandLeaderCanSubmitPendingEventForBand() throws Exception {
    var leader = createUser("events-band-leader@example.com", "password123");
    var band = createBandWithLeader(leader, "Leader Events Band");
    var payload = validEventPayload("Leader Pending Event");
    payload.put("bandId", 999L);

    var result = mockMvc.perform(post("/api/bands/{bandId}/events", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/events/")))
      .andExpect(jsonPath("$.title").value("Leader Pending Event"))
      .andExpect(jsonPath("$.band.id").value(band.getId()))
      .andExpect(jsonPath("$.createdBy.id").value(leader.getId()))
      .andExpect(jsonPath("$.status").value(EventStatus.DRAFT.name()))
      .andExpect(jsonPath("$.source").value(EventSource.INTERNAL.name()))
      .andReturn();

    var eventId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    entityManager.flush();
    entityManager.clear();

    Event event = eventDao.findById(eventId).orElseThrow();
    assertThat(event.getBand().getId()).isEqualTo(band.getId());
    assertThat(event.getCreatedBy().getId()).isEqualTo(leader.getId());
    assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
    assertThat(event.getSource()).isEqualTo(EventSource.INTERNAL);
  }

  @Test
  void bandLeaderCanPublishEventForBandWithExistingMusicalSpace() throws Exception {
    var leader = createUser("events-band-space-leader@example.com", "password123");
    var manager = createUser("events-band-space-manager@example.com", "password123");
    var band = createBandWithLeader(leader, "Leader Space Events Band");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var payload = validEventPayload("Leader Space Event");
    payload.put("musicalSpaceId", space.getId());

    var result = mockMvc.perform(post("/api/bands/{bandId}/events", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.band.id").value(band.getId()))
      .andExpect(jsonPath("$.musicalSpace.id").value(space.getId()))
      .andReturn();

    var eventId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    entityManager.flush();
    entityManager.clear();

    Event event = eventDao.findById(eventId).orElseThrow();
    assertThat(event.getMusicalSpace().getId()).isEqualTo(space.getId());
  }

  @Test
  void bandLeaderCannotPublishEventLinkedToNonPublicMusicalSpace() throws Exception {
    var leader = createUser("events-band-private-space-leader@example.com", "password123");
    var manager = createUser("events-band-private-space-manager@example.com", "password123");
    var band = createBandWithLeader(leader, "Leader Private Space Events Band");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);
    var payload = validEventPayload("Leader Private Space Event");
    payload.put("musicalSpaceId", space.getId());

    mockMvc.perform(post("/api/bands/{bandId}/events", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)))
      .andExpect(status().isNotFound());
  }

  @Test
  void nonLeaderCannotPublishEventForBand() throws Exception {
    var leader = createUser("events-band-owner@example.com", "password123");
    var otherUser = createUser("events-band-outsider@example.com", "password123");
    var band = createBandWithLeader(leader, "Protected Band Events");

    mockMvc.perform(post("/api/bands/{bandId}/events", band.getId())
        .header("Authorization", "Bearer " + tokenFor(otherUser))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validEventPayload("Outsider Event"))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    entityManager.flush();
    entityManager.clear();

    Long storedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.title = :title
        """,
        Long.class
      )
      .setParameter("title", "Outsider Event")
      .getSingleResult();

    assertThat(storedCount).isZero();
  }

  @Test
  void adminCannotPublishEventForBandFromPublicFlow() throws Exception {
    var leader = createUser("events-band-admin-block-leader@example.com", "password123");
    var admin = createAdmin("events-band-admin-block@example.com", "password123");
    var band = createBandWithLeader(leader, "Admin Blocked Band Events");

    mockMvc.perform(post("/api/bands/{bandId}/events", band.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validEventPayload("Admin Band Event"))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void ticketmasterSearchRequiresAdminRole() throws Exception {
    var user = createUser("events-ticketmaster-user@example.com", "password123");

    mockMvc.perform(get("/api/ticketmaster/events")
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isForbidden());
  }

  @Test
  void ticketmasterSearchStartsTodayAndFiltersEarlierResponses() throws Exception {
    var admin = createAdmin("events-ticketmaster-search-admin@example.com", "password123");
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    given(ticketmasterClient.searchEvents(any()))
      .willReturn(List.of(
        ticketmasterEvent("tm-yesterday", yesterday),
        ticketmasterEvent("tm-today", today)
      ));

    mockMvc.perform(get("/api/ticketmaster/events")
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].externalId").value("tm-today"))
      .andExpect(jsonPath("$[0].description").value(ticketmasterDescription("tm-today")))
      .andExpect(jsonPath("$[0].eventDate").value(today.toString()))
      .andExpect(jsonPath("$[0].posterImage").value(ticketmasterPosterImage("tm-today")));

    verify(ticketmasterClient).searchEvents(argThat(
      request -> !request.startDate().isBefore(today)
    ));
  }

  @Test
  void adminCanBulkImportTicketmasterEventsFromToday() throws Exception {
    var admin = createAdmin("events-ticketmaster-bulk-import-admin@example.com", "password123");
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate futureDate = today.plusDays(14);
    given(ticketmasterClient.searchEvents(any()))
      .willReturn(List.of(
        ticketmasterEvent("tm-bulk-yesterday", yesterday),
        ticketmasterEvent("tm-bulk-today", today),
        ticketmasterEvent("tm-bulk-future", futureDate),
        ticketmasterEvent("tm-bulk-future", futureDate)
      ));

    mockMvc.perform(post("/api/admin/events/import/ticketmaster")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .param("countryCode", "ES"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.importedCount").value(2))
      .andExpect(jsonPath("$.alreadyImportedCount").value(0))
      .andExpect(jsonPath("$.events", hasSize(2)))
      .andExpect(jsonPath("$.events[0].externalId").value("tm-bulk-today"))
      .andExpect(jsonPath("$.events[1].externalId").value("tm-bulk-future"));

    entityManager.flush();
    entityManager.clear();

    Long importedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.externalSource = :externalSource
          and e.externalId in :externalIds
        """,
        Long.class
      )
      .setParameter("externalSource", "TICKETMASTER")
      .setParameter("externalIds", List.of("tm-bulk-today", "tm-bulk-future"))
      .getSingleResult();

    Long yesterdayImportedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.externalSource = :externalSource
          and e.externalId = :externalId
        """,
        Long.class
      )
      .setParameter("externalSource", "TICKETMASTER")
      .setParameter("externalId", "tm-bulk-yesterday")
      .getSingleResult();

    assertThat(importedCount).isEqualTo(2L);
    assertThat(yesterdayImportedCount).isZero();
    verify(ticketmasterClient).searchEvents(argThat(
      request -> !request.startDate().isBefore(today)
    ));
  }

  @Test
  void importingSameTicketmasterEventTwiceDoesNotCreateDuplicateEvents() throws Exception {
    var admin = createAdmin("events-ticketmaster-import-admin@example.com", "password123");
    var externalId = "tm-123";
    given(ticketmasterClient.findEventByExternalId(externalId))
      .willReturn(ticketmasterEvent(externalId));

    var firstImport = mockMvc.perform(post("/api/admin/events/import/ticketmaster/{externalId}", externalId)
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.alreadyImported").value(false))
      .andExpect(jsonPath("$.externalId").value(externalId))
      .andExpect(jsonPath("$.provider").value("TICKETMASTER"))
      .andExpect(jsonPath("$.event.description").value(ticketmasterDescription(externalId)))
      .andExpect(jsonPath("$.event.posterImage").value(ticketmasterPosterImage(externalId)))
      .andReturn();

    var importedEventId = objectMapper.readTree(firstImport.getResponse().getContentAsString())
      .get("event")
      .get("id")
      .asLong();

    mockMvc.perform(post("/api/admin/events/import/ticketmaster/{externalId}", externalId)
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.alreadyImported").value(true))
      .andExpect(jsonPath("$.event.id").value(importedEventId));

    entityManager.flush();
    entityManager.clear();

    Long importedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.externalSource = :externalSource
          and e.externalId = :externalId
        """,
        Long.class
      )
      .setParameter("externalSource", "TICKETMASTER")
      .setParameter("externalId", externalId)
      .getSingleResult();

    assertThat(importedCount).isEqualTo(1L);
    verify(ticketmasterClient, times(1)).findEventByExternalId(externalId);
  }

  @Test
  void importingTicketmasterEventBeforeTodayIsRejected() throws Exception {
    var admin = createAdmin("events-ticketmaster-import-date-admin@example.com", "password123");
    var externalId = "tm-yesterday-import";
    given(ticketmasterClient.findEventByExternalId(externalId))
      .willReturn(ticketmasterEvent(externalId, LocalDate.now().minusDays(1)));

    mockMvc.perform(post("/api/admin/events/import/ticketmaster/{externalId}", externalId)
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isBadRequest());

    Long importedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.externalSource = :externalSource
          and e.externalId = :externalId
        """,
        Long.class
      )
      .setParameter("externalSource", "TICKETMASTER")
      .setParameter("externalId", externalId)
      .getSingleResult();

    assertThat(importedCount).isZero();
  }

  @Test
  void featuredTicketmasterSeedCreatesPublishedEventsAndIsIdempotent() throws Exception {
    var admin = createAdmin("events-ticketmaster-featured-seed-admin@example.com", "password123");

    int createdCount = ticketmasterFeaturedEventSeedService.ensureFeaturedEvents(admin);

    entityManager.flush();
    entityManager.clear();

    Event oldUrlShakira = eventDao.findByExternalSourceAndExternalId(
      "TICKETMASTER",
      "tm-es-2026-featured-shakira-2026-10-02-madrid"
    ).orElseThrow();
    oldUrlShakira.setExternalUrl("https://www.ticketmaster.es/search?q=shakira+2026+10+02+madrid");
    oldUrlShakira.setDescription("Shakira. Prioridad: alta. Tags: residencia. Fuente: Ticketmaster.");
    eventDao.update(oldUrlShakira);

    int duplicatedCount = ticketmasterFeaturedEventSeedService.ensureFeaturedEvents(admin);

    entityManager.flush();
    entityManager.clear();

    Long storedCount = entityManager.createQuery(
        """
        select count(e)
        from Event e
        where e.externalSource = :externalSource
          and e.externalId like :externalIdPattern
        """,
        Long.class
      )
      .setParameter("externalSource", "TICKETMASTER")
      .setParameter("externalIdPattern", "tm-es-2026-featured-%")
      .getSingleResult();

    assertThat(createdCount).isEqualTo(ticketmasterFeaturedEventSeedService.featuredEventCount());
    assertThat(duplicatedCount).isZero();
    assertThat(storedCount).isEqualTo(ticketmasterFeaturedEventSeedService.featuredEventCount());

    Event shakira = eventDao.findByExternalSourceAndExternalId(
      "TICKETMASTER",
      "tm-es-2026-featured-shakira-2026-10-02-madrid"
    ).orElseThrow();

    assertThat(shakira.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    assertThat(shakira.getSource()).isEqualTo(EventSource.EXTERNAL);
    assertThat(shakira.getCity()).isEqualTo("Madrid");
    assertThat(shakira.getMusicalGenre()).isEqualTo("Pop");
    assertThat(shakira.getExternalUrl()).isEqualTo("https://www.ticketmaster.es/artist/shakira-entradas/7142");
    assertThat(shakira.getExternalUrl()).doesNotContain("2026");
    assertThat(shakira.getCountry()).isEqualTo("España");
    assertThat(shakira.getDescription())
      .contains("Concierto de Shakira", "Ticketmaster España")
      .doesNotContain("Prioridad", "Tags", "Selección inicial");

    mockMvc.perform(get("/api/events")
        .param("city", "Madrid")
        .param("musicalGenre", "Pop")
        .param("source", EventSource.EXTERNAL.name())
        .param("date", "2026-10-02"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.title=='Shakira - Las Mujeres Ya No Lloran, Residencia Europea')]").exists())
      .andExpect(jsonPath("$[?(@.venueName=='ESTADIO SHAKIRA(Iberdrola Music)')]").exists())
      .andExpect(jsonPath("$[?(@.source=='EXTERNAL')]").exists());
  }

  private Map<String, Object> validEventPayload(String title) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("title", title);
    payload.put("description", "Integration test event");
    payload.put("eventDate", LocalDate.now().plusDays(20).toString());
    payload.put("startTime", "20:00");
    payload.put("endTime", "22:00");
    payload.put("musicalGenre", "rock");
    payload.put("capacity", 150);
    payload.put("ticketPrice", BigDecimal.valueOf(12.50));
    payload.put("posterImage", "poster.png");
    payload.put("status", EventStatus.PUBLISHED.name());
    payload.put("eventType", EventType.CONCERT.name());
    payload.put("source", EventSource.INTERNAL.name());
    payload.put("venueName", "Sala Test");
    payload.put("latitude", 42.8782);
    payload.put("longitude", -8.5448);
    payload.put("city", "Santiago de Compostela");
    payload.put("province", "A Coruna");
    payload.put("country", "Spain");
    payload.put("location", "Rua Test 1");
    return payload;
  }

  private Event createEvent(String title, EventStatus status, es.udc.tfg.fios_rest.user.persistence.entity.User creator, boolean withCoordinates) {
    Event event = new Event(
      title,
      "Test event",
      LocalDate.now().plusDays(10),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "rock",
      100,
      BigDecimal.valueOf(15.00),
      "poster.png",
      status,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Test",
      withCoordinates ? 42.8782 : null,
      withCoordinates ? -8.5448 : null,
      "Santiago de Compostela",
      "A Coruna",
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

  private Band createBandWithLeader(User leader, String name) {
    Band band = bandDao.save(new Band(name, "Band description", "Rock", "Santiago de Compostela", "band.png"));
    bandMemberDao.save(new BandMember(band, leader, BandMemberRole.LEADER));
    entityManager.flush();
    return band;
  }

  private TicketmasterEventItem ticketmasterEvent(String externalId) {
    return ticketmasterEvent(externalId, LocalDate.now().plusDays(14));
  }

  private TicketmasterEventItem ticketmasterEvent(String externalId, LocalDate eventDate) {
    return new TicketmasterEventItem(
      externalId,
      "Ticketmaster Duplicate Guard",
      ticketmasterDescription(externalId),
      "Imported event used to verify deduplication",
      null,
      "https://ticketmaster.example/events/" + externalId,
      new TicketmasterDates(new TicketmasterStart(eventDate.toString(), "20:30:00", null)),
      List.of(
        new TicketmasterImage(
          "https://ticketmaster.example/images/" + externalId + "-small.jpg",
          320,
          240,
          "4_3"
        ),
        new TicketmasterImage(
          ticketmasterPosterImage(externalId),
          1024,
          576,
          "16_9"
        )
      ),
      null,
      null,
      new TicketmasterEventEmbedded(List.of(new TicketmasterVenue(
        "Ticketmaster Venue",
        new TicketmasterNamedValue("Santiago de Compostela"),
        new TicketmasterNamedValue("A Coruna"),
        new TicketmasterNamedValue("Spain"),
        new TicketmasterLocation("42.8782", "-8.5448"),
        null
      )))
    );
  }

  private String ticketmasterPosterImage(String externalId) {
    return "https://ticketmaster.example/images/" + externalId + "-poster.jpg";
  }

  private String ticketmasterDescription(String externalId) {
    return "Official Ticketmaster description for " + externalId;
  }
}
