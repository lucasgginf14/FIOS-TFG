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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserReviewHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void completedReservationAppearsAsPendingUserReviewForManagerAndCanBeReviewed() throws Exception {
    var manager = createUser("user-review-manager@example.com", "password123");
    var customer = createUser("user-review-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(get("/api/user-reviews/pending")
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.reservationId==" + reservation.getId() + ")]").exists())
      .andExpect(jsonPath("$[?(@.reviewedUser.id==" + customer.getId() + ")]").exists());

    mockMvc.perform(post("/api/reservations/{reservationId}/user-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Reliable musician",
          "overallRating", 5,
          "communicationRating", 5,
          "punctualityRating", 4,
          "careRating", 5
        ))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/reservations/" + reservation.getId() + "/user-review"))
      .andExpect(jsonPath("$.overallRating").value(5))
      .andExpect(jsonPath("$.reviewer.id").value(manager.getId()))
      .andExpect(jsonPath("$.reviewedUser.id").value(customer.getId()));

    assertThat(userReviewDao.findByReservationSession(reservation.getId())).isPresent();

    mockMvc.perform(get("/api/user-reviews/pending")
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.reservationId==" + reservation.getId() + ")]").doesNotExist());
  }

  @Test
  void reservationCannotReceiveUserReviewTwice() throws Exception {
    var manager = createUser("user-review-duplicate-manager@example.com", "password123");
    var customer = createUser("user-review-duplicate-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(3),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    createUserReview(reservation, manager);

    mockMvc.perform(post("/api/reservations/{reservationId}/user-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Duplicate review",
          "overallRating", 4,
          "communicationRating", 4,
          "punctualityRating", 4,
          "careRating", 4
        ))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
  }

  @Test
  void nonManagerCannotReviewReservationUser() throws Exception {
    var manager = createUser("user-review-owner-manager@example.com", "password123");
    var customer = createUser("user-review-owner-customer@example.com", "password123");
    var outsider = createUser("user-review-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(4),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/user-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Not my space",
          "overallRating", 4,
          "communicationRating", 4,
          "punctualityRating", 4,
          "careRating", 4
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void receivedUserRatingReturnsCorrectAverageOverallRating() throws Exception {
    var managerOne = createUser("user-review-rating-manager1@example.com", "password123");
    var managerTwo = createUser("user-review-rating-manager2@example.com", "password123");
    var customer = createUser("user-review-rating-customer@example.com", "password123");
    var spaceOne = createSpace(managerOne, MusicalSpaceApprovalStatus.APPROVED, true);
    var spaceTwo = createSpace(managerTwo, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservationOne = createReservation(
      spaceOne,
      customer,
      LocalDate.now().minusDays(5),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    var reservationTwo = createReservation(
      spaceTwo,
      customer,
      LocalDate.now().minusDays(6),
      LocalTime.of(19, 0),
      LocalTime.of(20, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/user-review", reservationOne.getId())
        .header("Authorization", "Bearer " + tokenFor(managerOne))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Excellent guest",
          "overallRating", 5,
          "communicationRating", 5,
          "punctualityRating", 5,
          "careRating", 4
        ))))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/reservations/{reservationId}/user-review", reservationTwo.getId())
        .header("Authorization", "Bearer " + tokenFor(managerTwo))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Good guest",
          "overallRating", 3,
          "communicationRating", 3,
          "punctualityRating", 4,
          "careRating", 3
        ))))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users/{userId}/rating", customer.getId())
        .header("Authorization", "Bearer " + tokenFor(customer)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.reviewsCount").value(2))
      .andExpect(jsonPath("$.averageOverallRating").value(4.0));
  }

  @Test
  void adminCanDeleteUserReview() throws Exception {
    var admin = createAdmin("admin-user-review@example.com", "password123");
    var manager = createUser("admin-user-review-manager@example.com", "password123");
    var customer = createUser("admin-user-review-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(1),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    var review = createUserReview(reservation, manager);

    mockMvc.perform(delete("/api/admin/user-reviews/{id}", review.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isNoContent());

    assertThat(userReviewDao.findById(review.getId())).isEmpty();
  }
}
