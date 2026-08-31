package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AvailabilityHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void availabilityReturnsBaseScheduleForPublicSpace() throws Exception {
    var manager = createUser("availability-http-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    createSchedule(space, monday.getDayOfWeek(), LocalTime.of(10, 0), LocalTime.of(14, 0), BigDecimal.valueOf(35));

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/availability", space.getId())
        .param("date", monday.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.spaceId").value(space.getId()))
      .andExpect(jsonPath("$.slots.length()").value(1))
      .andExpect(jsonPath("$.slots[0].startTime").value("10:00:00"))
      .andExpect(jsonPath("$.slots[0].endTime").value("14:00:00"));
  }

  @Test
  void availabilitySplitsBlockedRangesAndAddsCustomAvailability() throws Exception {
    var manager = createUser("availability-split-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    createSchedule(space, monday.getDayOfWeek(), LocalTime.of(10, 0), LocalTime.of(14, 0), BigDecimal.valueOf(35));
    createException(space, monday, LocalTime.of(11, 0), LocalTime.of(12, 0), SpaceAvailabilityExceptionType.BLOCKED);
    createException(space, monday, LocalTime.of(16, 0), LocalTime.of(18, 0), SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY);

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/availability", space.getId())
        .param("date", monday.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.slots.length()").value(3))
      .andExpect(jsonPath("$.slots[0].startTime").value("10:00:00"))
      .andExpect(jsonPath("$.slots[0].endTime").value("11:00:00"))
      .andExpect(jsonPath("$.slots[1].startTime").value("12:00:00"))
      .andExpect(jsonPath("$.slots[1].endTime").value("14:00:00"))
      .andExpect(jsonPath("$.slots[2].startTime").value("16:00:00"))
      .andExpect(jsonPath("$.slots[2].endTime").value("18:00:00"));
  }

  @Test
  void customAvailabilityCreationRequiresPriceAndReturnsIt() throws Exception {
    var manager = createUser("availability-custom-price-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(3);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/exceptions", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "date", date.toString(),
          "startTime", "16:00:00",
          "endTime", "18:00:00",
          "exceptionType", "CUSTOM_AVAILABILITY",
          "reason", "Special opening"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Incluye un precio para la disponibilidad personalizada."));

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/exceptions", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "date", date.toString(),
          "startTime", "16:00:00",
          "endTime", "18:00:00",
          "exceptionType", "CUSTOM_AVAILABILITY",
          "reason", "Special opening",
          "price", 28
        ))))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.exceptionType").value("CUSTOM_AVAILABILITY"))
      .andExpect(jsonPath("$.price").value(28.0));
  }

  @Test
  @SuppressWarnings("unchecked")
  void managerCanCreateUpdateAndDeleteSchedulesThroughHttpEndpoints() throws Exception {
    var manager = createUser("availability-schedule-crud-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    MvcResult createResult = mockMvc.perform(post("/api/musical-spaces/{spaceId}/schedules", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "dayOfWeek", "MONDAY",
          "startTime", "10:00:00",
          "endTime", "12:00:00",
          "price", 24
        ))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/schedules/")))
      .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
      .andExpect(jsonPath("$.startTime").value("10:00:00"))
      .andReturn();

    Map<String, Object> createdSchedule =
      objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
    Long scheduleId = ((Number) createdSchedule.get("id")).longValue();

    mockMvc.perform(put("/api/schedules/{id}", scheduleId)
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "dayOfWeek", "TUESDAY",
          "startTime", "14:00:00",
          "endTime", "16:00:00",
          "price", 30
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.dayOfWeek").value("TUESDAY"))
      .andExpect(jsonPath("$.startTime").value("14:00:00"))
      .andExpect(jsonPath("$.price").value(30.0));

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/schedules", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].dayOfWeek").value("TUESDAY"));

    mockMvc.perform(delete("/api/schedules/{id}", scheduleId)
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/schedules", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void outsiderCannotMutateScheduleOrAvailabilityExceptions() throws Exception {
    var manager = createUser("availability-permission-manager@example.com", "password123");
    var outsider = createUser("availability-permission-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var schedule = createSchedule(
      space,
      DayOfWeek.MONDAY,
      LocalTime.of(10, 0),
      LocalTime.of(12, 0),
      BigDecimal.valueOf(24)
    );
    var spaceException = createException(
      space,
      LocalDate.now().plusDays(5),
      LocalTime.of(14, 0),
      LocalTime.of(15, 0),
      SpaceAvailabilityExceptionType.BLOCKED
    );

    mockMvc.perform(put("/api/schedules/{id}", schedule.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "dayOfWeek", "MONDAY",
          "startTime", "11:00:00",
          "endTime", "13:00:00",
          "price", 30
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/exceptions", space.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "date", LocalDate.now().plusDays(6).toString(),
          "startTime", "16:00:00",
          "endTime", "17:00:00",
          "exceptionType", "BLOCKED"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(delete("/api/exceptions/{id}", spaceException.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void availabilityIncludesBookedSlotsWithoutPersonalData() throws Exception {
    var manager = createUser("availability-booked-manager@example.com", "password123");
    var customer = createUser("availability-booked-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    createSchedule(space, monday.getDayOfWeek(), LocalTime.of(10, 0), LocalTime.of(14, 0), BigDecimal.valueOf(35));
    createReservation(space, customer, monday, LocalTime.of(11, 30), LocalTime.of(13, 30), 2, ReservationSessionState.PENDING);

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/availability", space.getId())
        .param("date", monday.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.slots.length()").value(2))
      .andExpect(jsonPath("$.slots[0].startTime").value("10:00:00"))
      .andExpect(jsonPath("$.slots[0].endTime").value("11:30:00"))
      .andExpect(jsonPath("$.slots[1].startTime").value("13:30:00"))
      .andExpect(jsonPath("$.slots[1].endTime").value("14:00:00"))
      .andExpect(jsonPath("$.bookedSlots.length()").value(1))
      .andExpect(jsonPath("$.bookedSlots[0].startTime").value("11:30:00"))
      .andExpect(jsonPath("$.bookedSlots[0].endTime").value("13:30:00"))
      .andExpect(jsonPath("$.bookedSlots[0].state").value("PENDING"))
      .andExpect(jsonPath("$.bookedSlots[0].user").doesNotExist());
  }

  @Test
  void availabilityForNonPublicSpaceIsHiddenFromAnonymousUsers() throws Exception {
    var manager = createUser("availability-private-manager@example.com", "password123");
    var privateSpace = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/availability", privateSpace.getId())
      .param("date", LocalDate.now().plusDays(2).toString()))
      .andExpect(status().isNotFound());
  }

  @Test
  void rawAvailabilityExceptionsAreOnlyVisibleToSpaceManager() throws Exception {
    var manager = createUser("availability-exceptions-manager@example.com", "password123");
    var outsider = createUser("availability-exceptions-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var date = LocalDate.now().plusDays(3);
    createException(space, date, LocalTime.of(12, 0), LocalTime.of(13, 0), SpaceAvailabilityExceptionType.BLOCKED);

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/exceptions", space.getId()))
      .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/exceptions", space.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/exceptions", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].exceptionType").value("BLOCKED"))
      .andExpect(jsonPath("$[0].reason").value("Test exception"));
  }
}
