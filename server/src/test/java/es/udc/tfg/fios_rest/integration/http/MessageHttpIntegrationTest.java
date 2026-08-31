package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void reservationParticipantsCanReadConversationButOutsiderCannotAndViewingMarksMessagesAsRead() throws Exception {
    var manager = createUser("message-http-manager@example.com", "password123");
    var customer = createUser("message-http-customer@example.com", "password123");
    var outsider = createUser("message-http-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );

    var customerMessage = createMessage("Customer question", reservation, customer);
    var managerMessage = createMessage("Manager answer", reservation, manager);

    mockMvc.perform(get("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(get("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.content=='Customer question')]").exists())
      .andExpect(jsonPath("$[?(@.content=='Manager answer')]").exists());

    entityManager.flush();
    entityManager.clear();

    assertThat(messageDao.findById(managerMessage.getId())).get().matches(message -> message.isRead());
    assertThat(messageDao.findById(customerMessage.getId())).get().matches(message -> !message.isRead());

    mockMvc.perform(get("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.content=='Customer question')]").exists());

    entityManager.flush();
    entityManager.clear();

    assertThat(messageDao.findById(customerMessage.getId())).get().matches(message -> message.isRead());
  }

  @Test
  void nonParticipantCannotSendMessagesAndAdminIsNotImplicitParticipant() throws Exception {
    var manager = createUser("message-access-manager@example.com", "password123");
    var customer = createUser("message-access-customer@example.com", "password123");
    var outsider = createUser("message-access-outsider@example.com", "password123");
    var admin = createAdmin("message-access-admin@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Outsider hello"))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(get("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Admin hello"))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    assertThat(messageDao.findByReservationSession(reservation.getId())).isEmpty();
  }

  @Test
  void sentMessageAlwaysUsesAuthenticatedUserAsSender() throws Exception {
    var manager = createUser("message-sender-manager@example.com", "password123");
    var customer = createUser("message-sender-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "content", "This is mine",
          "senderId", manager.getId()
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").value("This is mine"))
      .andExpect(jsonPath("$.user.id").value(customer.getId()));

    assertThat(messageDao.findByReservationSession(reservation.getId()))
      .singleElement()
      .matches(message -> message.getUser().getId().equals(customer.getId()));
  }

  @Test
  void unreadSummaryOnlyCountsCurrentUsersUnreadMessages() throws Exception {
    var manager = createUser("message-unread-manager@example.com", "password123");
    var customer = createUser("message-unread-customer@example.com", "password123");
    var otherManager = createUser("message-unread-other-manager@example.com", "password123");
    var otherCustomer = createUser("message-unread-other-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var otherSpace = createSpace(otherManager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );
    var otherReservation = createReservation(
      otherSpace,
      otherCustomer,
      LocalDate.now().plusDays(3),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );
    createMessage("Unread for current customer", reservation, manager);
    createMessage("Unread for somebody else", otherReservation, otherManager);

    mockMvc.perform(get("/api/messages/unread")
        .header("Authorization", "Bearer " + tokenFor(customer)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalUnreadMessages").value(1))
      .andExpect(jsonPath("$.reservations.length()").value(1))
      .andExpect(jsonPath("$.reservations[0].reservationId").value(reservation.getId()));
  }

  @Test
  void messageContentMustNotBeBlankOrTooLong() throws Exception {
    var manager = createUser("message-validation-manager@example.com", "password123");
    var customer = createUser("message-validation-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "   "))))
      .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "x".repeat(2001)))))
      .andExpect(status().isBadRequest());

    assertThat(messageDao.findByReservationSession(reservation.getId())).isEmpty();
  }

  @Test
  void messagesCanBeSentOnlyForPendingOrAcceptedReservations() throws Exception {
    var manager = createUser("message-send-manager@example.com", "password123");
    var customer = createUser("message-send-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var pendingReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(3),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );
    var acceptedReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(4),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );
    var cancelledReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(5),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.CANCELLED
    );
    var rejectedReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(6),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.REJECTED
    );
    var completedReservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(1),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", pendingReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Pending hello"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").value("Pending hello"));

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", acceptedReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Accepted hello"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").value("Accepted hello"));

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", cancelledReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Cancelled hello"))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", rejectedReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Rejected hello"))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    mockMvc.perform(post("/api/reservations/{reservationId}/messages", completedReservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("content", "Completed hello"))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }
}
