package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationSessionHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void createReservationReturnsPendingStateAndComputedPrice() throws Exception {
    var manager = createUser("reservation-http-manager@example.com", "password123");
    var customer = createUser("reservation-http-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(2);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "18:30:00",
          "endTime", "20:00:00",
          "attendeesCount", 4,
          "sessionType", "REHEARSAL",
          "observations", "Band rehearsal"
        ))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/reservations/")))
      .andExpect(jsonPath("$.state").value("PENDING"))
      .andExpect(jsonPath("$.finalPrice").value(15.0))
      .andExpect(jsonPath("$.user.id").value(customer.getId()));
  }

  @Test
  void createReservationReturnsComputedPriceForCustomAvailability() throws Exception {
    var manager = createUser("reservation-custom-price-manager@example.com", "password123");
    var customer = createUser("reservation-custom-price-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(2);
    createException(
      space,
      date,
      LocalTime.of(16, 0),
      LocalTime.of(18, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      BigDecimal.valueOf(28)
    );

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "16:30:00",
          "endTime", "17:30:00",
          "attendeesCount", 2,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.state").value("PENDING"))
      .andExpect(jsonPath("$.finalPrice").value(14.0));
  }

  @Test
  void createReservationRejectsRequestedTimeOutsideAvailability() throws Exception {
    var manager = createUser("reservation-outside-manager@example.com", "password123");
    var customer = createUser("reservation-outside-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(3);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(20, 0), BigDecimal.valueOf(30));

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "20:00:00",
          "endTime", "21:00:00",
          "attendeesCount", 2,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("TIME_NOT_AVAILABLE"));
  }

  @Test
  void createReservationRejectsAdminsBecausePublicFlowIsReadOnlyForThem() throws Exception {
    var manager = createUser("reservation-admin-readonly-manager@example.com", "password123");
    var admin = createAdmin("reservation-admin-readonly@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(3);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "18:30:00",
          "endTime", "20:00:00",
          "attendeesCount", 4,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(403))
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void createReservationRejectsOverlappingActiveReservation() throws Exception {
    var manager = createUser("reservation-overlap-manager@example.com", "password123");
    var customer = createUser("reservation-overlap-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(4);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    createReservation(space, customer, date, LocalTime.of(18, 30), LocalTime.of(19, 30), 2, ReservationSessionState.ACCEPTED);

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "19:00:00",
          "endTime", "20:00:00",
          "attendeesCount", 2,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("TIME_NOT_AVAILABLE"));
  }

  @Test
  void createReservationRejectsInvalidTimeRangeAndPastDateWithFieldErrors() throws Exception {
    var manager = createUser("reservation-invalid-dto-manager@example.com", "password123");
    var customer = createUser("reservation-invalid-dto-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", LocalDate.now().plusDays(2).toString(),
          "startTime", "20:00:00",
          "endTime", "19:00:00",
          "attendeesCount", 2,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.code=='INVALID_TIME_RANGE')]").exists());

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", LocalDate.now().minusDays(1).toString(),
          "startTime", "18:00:00",
          "endTime", "19:00:00",
          "attendeesCount", 2,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='sessionDate' && @.code=='INVALID_DATE')]").exists());
  }

  @Test
  void createReservationRejectsAttendeesAboveCapacity() throws Exception {
    var manager = createUser("reservation-capacity-manager@example.com", "password123");
    var customer = createUser("reservation-capacity-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(5);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(20, 0), BigDecimal.valueOf(30));

    mockMvc.perform(post("/api/reservations")
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "musicalSpaceId", space.getId(),
          "sessionDate", date.toString(),
          "startTime", "18:00:00",
          "endTime", "19:00:00",
          "attendeesCount", 99,
          "sessionType", "REHEARSAL"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void reservationOwnerCanCancelWithReason() throws Exception {
    var manager = createUser("reservation-cancel-manager@example.com", "password123");
    var customer = createUser("reservation-cancel-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(6),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );

    mockMvc.perform(patch("/api/reservations/{id}/cancel", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "cancellationReason", "The band cannot attend anymore"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.state").value("CANCELLED"))
      .andExpect(jsonPath("$.cancellationReason").value("The band cannot attend anymore"));
  }

  @Test
  void reservationStateEndpointAcceptsValidTransitionAndRejectsInvalidOne() throws Exception {
    var manager = createUser("reservation-state-manager@example.com", "password123");
    var customer = createUser("reservation-state-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    var validReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(7),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );
    var invalidReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(8),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );

    mockMvc.perform(patch("/api/reservations/{id}/state", validReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("state", "ACCEPTED"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.state").value("ACCEPTED"));

    mockMvc.perform(patch("/api/reservations/{id}/state", invalidReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("state", "COMPLETED"))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }

  @Test
  void managerCanManageReservationsReceivedForSpacesAndAdminPublicFlowIsReadOnly() throws Exception {
    var manager = createUser("reservation-manage-manager@example.com", "password123");
    var admin = createAdmin("reservation-manage-admin@example.com", "password123");
    var customer = createUser("reservation-manage-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    var managerReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(9),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );
    var adminReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(10),
      LocalTime.of(19, 0),
      LocalTime.of(20, 0),
      2,
      ReservationSessionState.PENDING
    );

    mockMvc.perform(get("/api/musical-spaces/me/reservations")
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==" + managerReservation.getId() + ")]").exists());

    mockMvc.perform(patch("/api/reservations/{id}/state", managerReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("state", "ACCEPTED"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.state").value("ACCEPTED"));

    mockMvc.perform(patch("/api/reservations/{id}/state", adminReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("state", "ACCEPTED"))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(403))
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }
}
