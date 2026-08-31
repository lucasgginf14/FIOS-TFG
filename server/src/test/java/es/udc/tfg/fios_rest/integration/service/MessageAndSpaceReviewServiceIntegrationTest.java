package es.udc.tfg.fios_rest.integration.service;

import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.message.service.MessageService;
import es.udc.tfg.fios_rest.message.service.dto.MessageRequest;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.spacereview.service.SpaceReviewService;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewRequest;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageAndSpaceReviewServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private MessageService messageService;

  @Autowired
  private SpaceReviewService spaceReviewService;

  @Test
  void conversationAccessIsRestrictedAndReadingMarksOnlyReceivedMessagesAsRead() throws Exception {
    var manager = createUser("message-manager@example.com", "password123");
    var customer = createUser("message-customer@example.com", "password123");
    var outsider = createUser("message-outsider@example.com", "password123");
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

    authenticateAs(outsider);
    assertThatThrownBy(() -> messageService.getMessagesByReservationId(reservation.getId()))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("cannot access messages");

    authenticateAs(customer);
    messageService.getMessagesByReservationId(reservation.getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(messageDao.findById(managerMessage.getId()))
      .get()
      .matches(message -> message.isRead());
    assertThat(messageDao.findById(customerMessage.getId()))
      .get()
      .matches(message -> !message.isRead());
  }

  @Test
  void unreadSummaryCountsOnlyMessagesPendingForCurrentSide() throws Exception {
    var manager = createUser("summary-manager@example.com", "password123");
    var customer = createUser("summary-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(3),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );
    createMessage("Unread for manager", reservation, customer);

    authenticateAs(manager);
    var unread = messageService.getUnreadMessages();

    assertThat(unread.totalUnreadMessages()).isEqualTo(1);
    assertThat(unread.reservations()).hasSize(1);
    assertThat(unread.reservations().get(0).reservationId()).isEqualTo(reservation.getId());
  }

  @Test
  void messagesCanBeSentOnlyForPendingOrAcceptedReservations() throws Exception {
    var manager = createUser("send-manager@example.com", "password123");
    var customer = createUser("send-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var pendingReservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(4),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
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
    authenticateAs(customer);

    var sentMessage = messageService.sendMessage(pendingReservation.getId(), new MessageRequest("Hello there"));
    assertThat(sentMessage.content()).isEqualTo("Hello there");

    assertThatThrownBy(() -> messageService.sendMessage(cancelledReservation.getId(), new MessageRequest("Should fail")))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("pending or accepted reservations");
  }

  @Test
  void serviceRejectsInvalidMessageContentBeforePersistingMessage() throws Exception {
    var manager = createUser("send-invalid-message-manager@example.com", "password123");
    var customer = createUser("send-invalid-message-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(4),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );

    authenticateAs(customer);

    assertThatThrownBy(() -> messageService.sendMessage(reservation.getId(), new MessageRequest("x".repeat(2001))))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The message content cannot exceed 2000 characters");

    assertThat(messageDao.findByReservationSession(reservation.getId())).isEmpty();
  }

  @Test
  void completedReservationsCanBeReviewedOnlyOnceAndContributeToAverageRating() throws Exception {
    var manager = createUser("review-manager@example.com", "password123");
    var customerOne = createUser("review-customer1@example.com", "password123");
    var customerTwo = createUser("review-customer2@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    var completedOne = createReservation(
      space,
      customerOne,
      LocalDate.now().minusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    var completedTwo = createReservation(
      space,
      customerTwo,
      LocalDate.now().minusDays(1),
      LocalTime.of(19, 0),
      LocalTime.of(20, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    authenticateAs(customerOne);
    assertThat(spaceReviewService.findPendingReviews())
      .extracting(view -> view.reservationId())
      .contains(completedOne.getId());

    var firstReview = spaceReviewService.create(completedOne.getId(), new SpaceReviewRequest(
      "Very good",
      5,
      5,
      4,
      5,
      4
    ));
    assertThat(firstReview.overallRating()).isEqualTo(5);

    assertThatThrownBy(() -> spaceReviewService.create(completedOne.getId(), new SpaceReviewRequest(
      "Duplicate",
      4,
      4,
      4,
      4,
      4
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The reservation already has a space review");

    authenticateAs(customerTwo);
    spaceReviewService.create(completedTwo.getId(), new SpaceReviewRequest(
      "Good enough",
      3,
      3,
      3,
      3,
      3
    ));

    authenticateAs(manager);
    var rating = spaceReviewService.getRating(space.getId());
    assertThat(rating.reviewsCount()).isEqualTo(2);
    assertThat(rating.averageOverallRating()).isEqualByComparingTo("4.00");
  }

  @Test
  void serviceRejectsInvalidRatingsBeforePersistingReview() throws Exception {
    var manager = createUser("review-invalid-rating-manager@example.com", "password123");
    var customer = createUser("review-invalid-rating-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var completedReservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    authenticateAs(customer);

    assertThatThrownBy(() -> spaceReviewService.create(completedReservation.getId(), new SpaceReviewRequest(
      "Invalid overall rating",
      0,
      5,
      4,
      5,
      4
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("overallRating must be between 1 and 5");

    assertThat(spaceReviewDao.findByReservationSession(completedReservation.getId())).isEmpty();
  }
}
