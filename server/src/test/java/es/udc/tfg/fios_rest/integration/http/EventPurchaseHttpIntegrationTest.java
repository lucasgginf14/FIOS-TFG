package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.eventpurchase.persistence.dao.EventPurchaseDao;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventPurchaseHttpIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private EventPurchaseDao eventPurchaseDao;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Test
  void authenticatedUserCanReserveInternalFiosEventAndSeeCounters() throws Exception {
    var user = createUser("event-reserve-user@example.com", "password123");
    var event = createEvent("FIOS Concert", EventSource.INTERNAL, EventStatus.PUBLISHED, 100, BigDecimal.valueOf(15));
    String token = tokenFor(user);

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/events/" + event.getId() + "/reserve"))
      .andExpect(jsonPath("$.event.id").value(event.getId()))
      .andExpect(jsonPath("$.event.title").value("FIOS Concert"))
      .andExpect(jsonPath("$.state").value("RESERVED"))
      .andExpect(jsonPath("$.active").value(true))
      .andExpect(jsonPath("$.amountDue").value(15.0))
      .andExpect(jsonPath("$.pricePaid").value(15.0));

    mockMvc.perform(get("/api/events/{eventId}/reservation-status", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.reserved").value(true))
      .andExpect(jsonPath("$.purchased").value(true))
      .andExpect(jsonPath("$.capacity").value(100))
      .andExpect(jsonPath("$.reservedTickets").value(1))
      .andExpect(jsonPath("$.availableTickets").value(99))
      .andExpect(jsonPath("$.soldOut").value(false));

    mockMvc.perform(get("/api/events/{id}", event.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.capacity").value(100))
      .andExpect(jsonPath("$.reservedTickets").value(1))
      .andExpect(jsonPath("$.availableTickets").value(99));

    mockMvc.perform(get("/api/event-purchases/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].event.id").value(event.getId()))
      .andExpect(jsonPath("$[0].amountDue").value(15.0))
      .andExpect(jsonPath("$[0].pricePaid").value(15.0));
  }

  @Test
  void reservationKeepsAmountDueWhenEventPriceChanges() throws Exception {
    var user = createUser("event-reserve-price-user@example.com", "password123");
    var event = createEvent("Immutable Price Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 50, BigDecimal.valueOf(12.50));
    String token = tokenFor(user);

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
      .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.amountDue").value(12.5))
      .andExpect(jsonPath("$.pricePaid").value(12.5));

    event.setTicketPrice(BigDecimal.valueOf(40));
    eventDao.update(event);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(get("/api/event-purchases/me")
      .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].amountDue").value(12.5))
      .andExpect(jsonPath("$[0].pricePaid").value(12.5))
      .andExpect(jsonPath("$[0].event.ticketPrice").value(40));
  }

  @Test
  void reservingSameEventTwiceReturnsConflictWithoutDuplicatingIt() throws Exception {
    var user = createUser("event-reserve-duplicate-user@example.com", "password123");
    var event = createEvent("Duplicate Safe Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 100, BigDecimal.valueOf(15));
    String token = tokenFor(user);

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("EVENT_ALREADY_RESERVED"))
      .andExpect(jsonPath("$.message").value("Ya tienes una reserva activa para este evento."));

    assertThat(eventPurchaseDao.countActiveByEvent(event.getId())).isEqualTo(1);
  }

  @Test
  void sameEventCanBeReservedByAnotherUser() throws Exception {
    var firstUser = createUser("event-reserve-shared-first@example.com", "password123");
    var secondUser = createUser("event-reserve-shared-second@example.com", "password123");
    var event = createEvent("Shared Reservation Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 100, BigDecimal.valueOf(15));

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(firstUser)))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(secondUser)))
      .andExpect(status().isCreated());

    assertThat(eventPurchaseDao.countActiveByEvent(event.getId())).isEqualTo(2);
  }

  @Test
  void soldOutEventCannotBeReservedByAnotherUser() throws Exception {
    var firstUser = createUser("event-reserve-first-user@example.com", "password123");
    var secondUser = createUser("event-reserve-second-user@example.com", "password123");
    var event = createEvent("Small Capacity Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 1, BigDecimal.valueOf(15));

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(firstUser)))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(secondUser)))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("NO_TICKETS_AVAILABLE"));

    mockMvc.perform(get("/api/events/{eventId}/reservation-status", event.getId())
        .header("Authorization", "Bearer " + tokenFor(secondUser)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.reserved").value(false))
      .andExpect(jsonPath("$.availableTickets").value(0))
      .andExpect(jsonPath("$.soldOut").value(true));
  }

  @Test
  void externalEventsCannotBeReservedInsideFios() throws Exception {
    var user = createUser("event-reserve-external-user@example.com", "password123");
    var event = createEvent("External Concert", EventSource.EXTERNAL, EventStatus.PUBLISHED, null, null);

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }

  @Test
  void pastArchivedOrUnpublishedEventsCannotBeReserved() throws Exception {
    var user = createUser("event-reserve-invalid-state-user@example.com", "password123");
    String token = tokenFor(user);
    var past = createEvent(
      "Past Event",
      EventSource.INTERNAL,
      EventStatus.PUBLISHED,
      100,
      BigDecimal.valueOf(15),
      LocalDate.now().minusDays(1),
      LocalTime.of(20, 0)
    );
    var draft = createEvent("Draft Event", EventSource.INTERNAL, EventStatus.DRAFT, 100, BigDecimal.valueOf(15));
    var archived = createEvent("Archived Event", EventSource.INTERNAL, EventStatus.ARCHIVED, 100, BigDecimal.valueOf(15));

    mockMvc.perform(post("/api/events/{eventId}/reserve", past.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    mockMvc.perform(post("/api/events/{eventId}/reserve", draft.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    mockMvc.perform(post("/api/events/{eventId}/reserve", archived.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }

  @Test
  void userCanCancelOwnReservationAndRecoverAvailableTicket() throws Exception {
    var user = createUser("event-cancel-user@example.com", "password123");
    var event = createEvent("Cancelable Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 2, BigDecimal.valueOf(15));
    String token = tokenFor(user);

    var result = mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated())
      .andReturn();
    Long reservationId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(patch("/api/event-purchases/{id}/cancel", reservationId)
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.state").value("CANCELLED"))
      .andExpect(jsonPath("$.active").value(false))
      .andExpect(jsonPath("$.cancelledAt").exists());

    mockMvc.perform(get("/api/events/{eventId}/reservation-status", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.reserved").value(false))
      .andExpect(jsonPath("$.reservedTickets").value(0))
      .andExpect(jsonPath("$.availableTickets").value(2));

    mockMvc.perform(get("/api/event-purchases/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void userCannotCancelAnotherUsersReservation() throws Exception {
    var owner = createUser("event-cancel-owner@example.com", "password123");
    var otherUser = createUser("event-cancel-other@example.com", "password123");
    var event = createEvent("Protected Reservation Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 2, BigDecimal.valueOf(15));

    var result = mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(owner)))
      .andExpect(status().isCreated())
      .andReturn();
    Long reservationId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(patch("/api/event-purchases/{id}/cancel", reservationId)
        .header("Authorization", "Bearer " + tokenFor(otherUser)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    assertThat(eventPurchaseDao.countActiveByEvent(event.getId())).isEqualTo(1);
  }

  @Test
  void userCannotCancelReservationTwice() throws Exception {
    var user = createUser("event-cancel-twice-user@example.com", "password123");
    var event = createEvent("Cancel Once Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 2, BigDecimal.valueOf(15));
    String token = tokenFor(user);

    var result = mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated())
      .andReturn();
    Long reservationId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(patch("/api/event-purchases/{id}/cancel", reservationId)
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk());

    mockMvc.perform(patch("/api/event-purchases/{id}/cancel", reservationId)
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }

  @Test
  void creatorAndAdminCanViewActiveReservationsButOtherUsersCannot() throws Exception {
    var creator = createUser("event-reservations-creator@example.com", "password123");
    var attendee = createUser("event-reservations-attendee@example.com", "password123");
    var otherUser = createUser("event-reservations-outsider@example.com", "password123");
    var admin = createAdmin("event-reservations-admin@example.com", "password123");
    var event = createEvent(
      "Managed Reservation Event",
      EventSource.INTERNAL,
      EventStatus.PUBLISHED,
      10,
      BigDecimal.valueOf(15),
      LocalDate.now().plusDays(8),
      LocalTime.of(20, 0),
      creator
    );

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(attendee)))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/api/events/{eventId}/reservations", event.getId())
        .header("Authorization", "Bearer " + tokenFor(creator)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].user.id").value(attendee.getId()))
      .andExpect(jsonPath("$[0].user.email").doesNotExist())
      .andExpect(jsonPath("$[0].amountDue").value(15.0))
      .andExpect(jsonPath("$[0].pricePaid").value(15.0));

    mockMvc.perform(get("/api/events/{eventId}/reservations", event.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1));

    mockMvc.perform(get("/api/events/{eventId}/reservations", event.getId())
        .header("Authorization", "Bearer " + tokenFor(otherUser)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void eventCapacityCannotBeReducedBelowActiveReservations() throws Exception {
    var admin = createAdmin("event-capacity-admin@example.com", "password123");
    var firstUser = createUser("event-capacity-first@example.com", "password123");
    var secondUser = createUser("event-capacity-second@example.com", "password123");
    var event = createEvent("Capacity Protected Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 5, BigDecimal.valueOf(15), LocalDate.now().plusDays(10), LocalTime.of(20, 0), admin);

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(firstUser)))
      .andExpect(status().isCreated());
    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(secondUser)))
      .andExpect(status().isCreated());

    var payload = validEventPayload(event);
    payload.put("capacity", 1);

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/events/{id}", event.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payload)))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.message").value("El aforo no puede ser menor que las reservas activas."));
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void concurrentReservationsCannotOversellEventCapacity() throws Exception {
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    ReservationRaceFixture fixture = transactionTemplate.execute(status -> {
      var firstUser = createUser("event-reserve-race-first@example.com", "password123");
      var secondUser = createUser("event-reserve-race-second@example.com", "password123");
      var event = createEvent("Race Capacity Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 1, BigDecimal.valueOf(15));
      return new ReservationRaceFixture(event.getId(), tokenFor(firstUser), tokenFor(secondUser));
    });

    var executor = Executors.newFixedThreadPool(2);
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);

    try {
      List<Callable<Integer>> reservationAttempts = List.of(
        () -> attemptReservation(fixture.eventId(), fixture.firstToken(), ready, start),
        () -> attemptReservation(fixture.eventId(), fixture.secondToken(), ready, start)
      );

      var results = reservationAttempts.stream()
        .map(executor::submit)
        .toList();

      assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
      start.countDown();

      List<Integer> responseStatuses = results.stream()
        .map(result -> {
          try {
            return result.get(10, TimeUnit.SECONDS);
          } catch (Exception e) {
            throw new AssertionError("Concurrent reservation request did not finish", e);
          }
        })
        .toList();

      assertThat(responseStatuses)
        .containsExactlyInAnyOrder(HttpStatus.CREATED.value(), HttpStatus.CONFLICT.value());
    } finally {
      start.countDown();
      executor.shutdownNow();
    }

    Long reservedTickets = transactionTemplate.execute(status -> eventPurchaseDao.countActiveByEvent(fixture.eventId()));
    assertThat(reservedTickets).isEqualTo(1L);
  }

  @Test
  void unauthenticatedUserCannotReserveEvent() throws Exception {
    var event = createEvent("Authentication Required Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 100, BigDecimal.valueOf(15));

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId()))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void adminCannotReserveEventFromPublicFlow() throws Exception {
    var admin = createAdmin("event-reserve-admin@example.com", "password123");
    var event = createEvent("Admin Read Only Event", EventSource.INTERNAL, EventStatus.PUBLISHED, 100, BigDecimal.valueOf(15));

    mockMvc.perform(post("/api/events/{eventId}/reserve", event.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  private int attemptReservation(
    Long eventId,
    String token,
    CountDownLatch ready,
    CountDownLatch start
  ) throws Exception {
    ready.countDown();
    assertThat(start.await(5, TimeUnit.SECONDS)).isTrue();
    return mockMvc.perform(post("/api/events/{eventId}/reserve", eventId)
        .header("Authorization", "Bearer " + token))
      .andReturn()
      .getResponse()
      .getStatus();
  }

  private Event createEvent(
    String title,
    EventSource source,
    EventStatus status,
    Integer capacity,
    BigDecimal ticketPrice
  ) {
    return createEvent(
      title,
      source,
      status,
      capacity,
      ticketPrice,
      LocalDate.now().plusDays(10),
      LocalTime.of(20, 0)
    );
  }

  private Event createEvent(
    String title,
    EventSource source,
    EventStatus status,
    Integer capacity,
    BigDecimal ticketPrice,
    LocalDate eventDate,
    LocalTime startTime
  ) {
    var creator = createUser(
      "event-reserve-creator-" + Math.abs((title + source + status + capacity + eventDate).hashCode()) + "@example.com",
      "password123"
    );
    return createEvent(title, source, status, capacity, ticketPrice, eventDate, startTime, creator);
  }

  private Event createEvent(
    String title,
    EventSource source,
    EventStatus status,
    Integer capacity,
    BigDecimal ticketPrice,
    LocalDate eventDate,
    LocalTime startTime,
    es.udc.tfg.fios_rest.user.persistence.entity.User creator
  ) {
    Event event = new Event(
      title,
      "Test event",
      eventDate,
      startTime,
      startTime == null ? null : startTime.plusHours(2),
      "rock",
      capacity,
      ticketPrice,
      "poster.png",
      status,
      EventType.CONCERT,
      source,
      "Sala Test",
      42.8782,
      -8.5448,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Event 1",
      EventSource.EXTERNAL.equals(source) ? "TICKETMASTER" : null,
      EventSource.EXTERNAL.equals(source) ? "tm-test-event-" + Math.abs(title.hashCode()) : null,
      EventSource.EXTERNAL.equals(source) ? "https://ticketmaster.example/event" : null,
      null,
      null,
      creator
    );
    return eventDao.save(event);
  }

  private java.util.Map<String, Object> validEventPayload(Event event) {
    java.util.Map<String, Object> payload = new java.util.LinkedHashMap<>();
    payload.put("title", event.getTitle());
    payload.put("description", event.getDescription());
    payload.put("eventDate", event.getEventDate().toString());
    payload.put("startTime", event.getStartTime() == null ? null : event.getStartTime().toString());
    payload.put("endTime", event.getEndTime() == null ? null : event.getEndTime().toString());
    payload.put("musicalGenre", event.getMusicalGenre());
    payload.put("capacity", event.getCapacity());
    payload.put("ticketPrice", event.getTicketPrice());
    payload.put("posterImage", event.getPosterImage());
    payload.put("status", event.getStatus().name());
    payload.put("eventType", event.getEventType().name());
    payload.put("source", event.getSource().name());
    payload.put("venueName", event.getVenueName());
    payload.put("latitude", event.getLatitude());
    payload.put("longitude", event.getLongitude());
    payload.put("city", event.getCity());
    payload.put("province", event.getProvince());
    payload.put("country", event.getCountry());
    payload.put("location", event.getLocation());
    return payload;
  }

  private record ReservationRaceFixture(Long eventId, String firstToken, String secondToken) {
  }
}
