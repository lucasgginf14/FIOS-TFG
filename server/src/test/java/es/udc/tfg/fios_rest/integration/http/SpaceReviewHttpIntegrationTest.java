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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpaceReviewHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void completedReservationAppearsAsPendingReviewAndCanBeReviewed() throws Exception {
    var manager = createUser("review-http-manager@example.com", "password123");
    var customer = createUser("review-http-customer@example.com", "password123");
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

    mockMvc.perform(get("/api/space-reviews/pending")
        .header("Authorization", "Bearer " + tokenFor(customer)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.reservationId==" + reservation.getId() + ")]").exists());

    mockMvc.perform(post("/api/reservations/{reservationId}/space-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Very good room",
          "overallRating", 5,
          "cleanlinessRating", 5,
          "soundQualityRating", 4,
          "equipmentRating", 5,
          "locationRating", 4
        ))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/reservations/" + reservation.getId() + "/space-review"))
      .andExpect(jsonPath("$.overallRating").value(5))
      .andExpect(jsonPath("$.user.id").value(customer.getId()));

    assertThat(spaceReviewDao.findByReservationSession(reservation.getId())).isPresent();
  }

  @Test
  void reservationCannotBeReviewedTwice() throws Exception {
    var manager = createUser("review-duplicate-manager@example.com", "password123");
    var customer = createUser("review-duplicate-customer@example.com", "password123");
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
    createSpaceReview(reservation, customer);

    mockMvc.perform(post("/api/reservations/{reservationId}/space-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(customer))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Duplicate review",
          "overallRating", 4,
          "cleanlinessRating", 4,
          "soundQualityRating", 4,
          "equipmentRating", 4,
          "locationRating", 4
        ))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
  }

  @Test
  void userCannotReviewAnotherUsersReservation() throws Exception {
    var manager = createUser("review-foreign-manager@example.com", "password123");
    var owner = createUser("review-owner@example.com", "password123");
    var outsider = createUser("review-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      owner,
      LocalDate.now().minusDays(4),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/space-review", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Not my reservation",
          "overallRating", 4,
          "cleanlinessRating", 4,
          "soundQualityRating", 4,
          "equipmentRating", 4,
          "locationRating", 4
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void ratingEndpointReturnsCorrectAverageOverallRating() throws Exception {
    var manager = createUser("review-rating-manager@example.com", "password123");
    var customerOne = createUser("review-rating-customer1@example.com", "password123");
    var customerTwo = createUser("review-rating-customer2@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    var reservationOne = createReservation(
      space,
      customerOne,
      LocalDate.now().minusDays(5),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    var reservationTwo = createReservation(
      space,
      customerTwo,
      LocalDate.now().minusDays(6),
      LocalTime.of(19, 0),
      LocalTime.of(20, 0),
      2,
      ReservationSessionState.COMPLETED
    );

    mockMvc.perform(post("/api/reservations/{reservationId}/space-review", reservationOne.getId())
        .header("Authorization", "Bearer " + tokenFor(customerOne))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Excellent",
          "overallRating", 5,
          "cleanlinessRating", 5,
          "soundQualityRating", 5,
          "equipmentRating", 4,
          "locationRating", 4
        ))))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/reservations/{reservationId}/space-review", reservationTwo.getId())
        .header("Authorization", "Bearer " + tokenFor(customerTwo))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "comment", "Good enough",
          "overallRating", 3,
          "cleanlinessRating", 3,
          "soundQualityRating", 3,
          "equipmentRating", 3,
          "locationRating", 3
        ))))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/rating", space.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.reviewsCount").value(2))
      .andExpect(jsonPath("$.averageOverallRating").value(4.0));
  }
}
