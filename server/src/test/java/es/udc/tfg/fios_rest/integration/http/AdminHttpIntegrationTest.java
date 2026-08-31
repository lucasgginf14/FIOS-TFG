package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void adminEndpointsRejectNonAdminUsers() throws Exception {
    var user = createUser("plain-user@example.com", "password123");

    mockMvc.perform(get("/api/admin/users")
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isForbidden());
  }

  @Test
  void adminCanListUsersAndDeactivateUser() throws Exception {
    var admin = createAdmin("admin-users@example.com", "password123");
    var user = createUser("managed-user@example.com", "password123");

    mockMvc.perform(get("/api/admin/users")
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.email=='managed-user@example.com')]").exists());

    mockMvc.perform(patch("/api/admin/users/{id}/active", user.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("active", false))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.active").value(false));

    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "managed-user@example.com",
          "password", "password123"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"));
  }

  @Test
  void adminCannotDeactivateOwnAccount() throws Exception {
    var admin = createAdmin("self-admin@example.com", "password123");

    mockMvc.perform(patch("/api/admin/users/{id}/active", admin.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("active", false))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    assertThat(userDao.findById(admin.getId()).orElseThrow().isActive()).isTrue();
  }

  @Test
  void adminCanPromoteUserToAdmin() throws Exception {
    var admin = createAdmin("role-admin@example.com", "password123");
    var user = createUser("promoted-user@example.com", "password123");

    mockMvc.perform(patch("/api/admin/users/{id}/admin-role", user.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.platformRole").value("ADMIN"));

    assertThat(userDao.findById(user.getId()).orElseThrow().getPlatformRole())
      .isEqualTo(PlatformRole.ADMIN);
  }

  @Test
  void adminCanRevokeAdminRoleFromAnotherAccount() throws Exception {
    var admin = createAdmin("revoke-role-admin@example.com", "password123");
    var targetAdmin = createAdmin("revoked-admin@example.com", "password123");
    var targetAdminToken = tokenFor(targetAdmin);

    mockMvc.perform(delete("/api/admin/users/{id}/admin-role", targetAdmin.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.platformRole").value("USER"));

    assertThat(userDao.findById(targetAdmin.getId()).orElseThrow().getPlatformRole())
      .isEqualTo(PlatformRole.USER);

    mockMvc.perform(get("/api/admin/users")
        .header("Authorization", "Bearer " + targetAdminToken))
      .andExpect(status().isForbidden());
  }

  @Test
  void adminCannotRevokeOwnAdminRole() throws Exception {
    var admin = createAdmin("self-role-admin@example.com", "password123");

    mockMvc.perform(delete("/api/admin/users/{id}/admin-role", admin.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    assertThat(userDao.findById(admin.getId()).orElseThrow().getPlatformRole())
      .isEqualTo(PlatformRole.ADMIN);
  }

  @Test
  void adminCanCancelReservationWithReason() throws Exception {
    var admin = createAdmin("admin-reservation@example.com", "password123");
    var manager = createUser("manager-admin-reservation@example.com", "password123");
    var customer = createUser("customer-admin-reservation@example.com", "password123");
    var space = createSpace(manager, es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus.APPROVED, true);
    createSchedule(space, LocalDate.now().plusDays(1).getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(1),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      3,
      ReservationSessionState.PENDING
    );

    mockMvc.perform(patch("/api/admin/reservations/{id}/cancel", reservation.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "cancellationReason", "Cancelled by admin after moderation review"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.state").value("CANCELLED"))
      .andExpect(jsonPath("$.cancellationReason").value("Cancelled by admin after moderation review"));
  }

  @Test
  void adminCanApproveAndRejectMusicalSpace() throws Exception {
    var admin = createAdmin("admin-space-approval@example.com", "password123");
    var manager = createUser("manager-space-approval@example.com", "password123");
    var pendingSpace = createSpace(manager, es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus.PENDING, true);

    mockMvc.perform(patch("/api/admin/musical-spaces/{id}/approval-status", pendingSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("approvalStatus", "APPROVED"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.approvalStatus").value("APPROVED"));

    mockMvc.perform(patch("/api/admin/musical-spaces/{id}/approval-status", pendingSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("approvalStatus", "REJECTED"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.approvalStatus").value("REJECTED"));
  }

  @Test
  void adminCanDeleteSpaceReview() throws Exception {
    var admin = createAdmin("admin-review@example.com", "password123");
    var manager = createUser("manager-review@example.com", "password123");
    var customer = createUser("customer-review@example.com", "password123");
    var space = createSpace(manager, es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(1),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    SpaceReview review = createSpaceReview(reservation, customer);

    mockMvc.perform(delete("/api/admin/space-reviews/{id}", review.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isNoContent());

    assertThat(spaceReviewDao.findById(review.getId())).isEmpty();
  }

  @Test
  void adminCanListDraftEventsThroughAdminArea() throws Exception {
    var admin = createAdmin("admin-events@example.com", "password123");
    Event draftEvent = new Event(
      "Draft Event",
      "Hidden draft",
      LocalDate.now().plusDays(15),
      LocalTime.of(19, 0),
      LocalTime.of(21, 0),
      "jazz",
      80,
      BigDecimal.valueOf(12.00),
      "draft.png",
      EventStatus.DRAFT,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Draft",
      42.88,
      -8.54,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Draft",
      null,
      null,
      null,
      null,
      null,
      admin
    );
    eventDao.save(draftEvent);

    mockMvc.perform(get("/api/admin/events")
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.title=='Draft Event')]").exists());
  }
}
