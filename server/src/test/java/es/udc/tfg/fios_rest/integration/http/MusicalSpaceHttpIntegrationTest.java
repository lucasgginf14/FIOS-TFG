package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MusicalSpaceHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void managerCanCreateSpaceButItIsNotPublicUntilApproved() throws Exception {
    var manager = createUser("manager-create@example.com", "password123");

    mockMvc.perform(post("/api/musical-spaces")
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Local Ensayo Centro",
          "description", "Sala equipada",
          "spaceType", MusicalSpaceType.REHEARSAL_ROOM.name(),
          "capacity", 6,
          "mainImage", "space.png",
          "squareMeters", 28.5,
          "soundproofed", true,
          "location", Map.of(
            "country", "Spain",
            "province", "A Coruna",
            "city", "Santiago de Compostela",
            "street", "Rua Nova",
            "portal", "12",
            "floor", "1A",
            "postalCode", "15701",
            "latitude", 42.8782,
            "longitude", -8.5448
          )
        ))))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.approvalStatus").value("PENDING"))
      .andExpect(jsonPath("$.active").value(true));

    mockMvc.perform(get("/api/musical-spaces"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$[?(@.name=='Local Ensayo Centro')]").isEmpty());
  }

  @Test
  void adminCannotCreateSpaceFromPublicFlow() throws Exception {
    var admin = createAdmin("admin-create-space-readonly@example.com", "password123");

    mockMvc.perform(post("/api/musical-spaces")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Admin Read Only Space",
          "description", "Sala equipada",
          "spaceType", MusicalSpaceType.REHEARSAL_ROOM.name(),
          "capacity", 6,
          "mainImage", "space.png",
          "squareMeters", 28.5,
          "soundproofed", true,
          "location", Map.of(
            "country", "Spain",
            "province", "A Coruna",
            "city", "Santiago de Compostela",
            "street", "Rua Nova",
            "portal", "12",
            "floor", "1A",
            "postalCode", "15701",
            "latitude", 42.8782,
            "longitude", -8.5448
          )
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void nonPublicSpaceIsVisibleForManagerButHiddenFromAnonymousUsers() throws Exception {
    var manager = createUser("manager-private@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);

    mockMvc.perform(get("/api/musical-spaces/{id}", space.getId()))
      .andExpect(status().isNotFound());

    mockMvc.perform(get("/api/musical-spaces/{id}", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(space.getId()))
      .andExpect(jsonPath("$.approvalStatus").value("PENDING"));
  }

  @Test
  void adminCanApproveSpaceAndThenItBecomesPubliclyVisible() throws Exception {
    var manager = createUser("manager-approval@example.com", "password123");
    var admin = createAdmin("admin-approval@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);

    mockMvc.perform(patch("/api/admin/musical-spaces/{id}/approval-status", space.getId())
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "approvalStatus", MusicalSpaceApprovalStatus.APPROVED.name()
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.approvalStatus").value("APPROVED"));

    mockMvc.perform(get("/api/musical-spaces"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==%s)]".formatted(space.getId())).exists());
  }

  @Test
  void deactivatedApprovedSpaceStopsBeingPubliclyVisible() throws Exception {
    var manager = createUser("manager-deactivate@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(patch("/api/musical-spaces/{id}/deactivate", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.active").value(false));

    mockMvc.perform(get("/api/musical-spaces"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==%s)]".formatted(space.getId())).isEmpty());
  }

  @Test
  void authenticatedNonOwnerCannotUpdateMusicalSpace() throws Exception {
    var manager = createUser("manager-update-owner@example.com", "password123");
    var outsider = createUser("manager-update-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(put("/api/musical-spaces/{id}", space.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Updated by outsider",
          "description", "This update should be rejected",
          "spaceType", MusicalSpaceType.RECORDING_STUDIO.name(),
          "capacity", 12,
          "mainImage", "updated.png",
          "squareMeters", 50.0,
          "soundproofed", false,
          "location", Map.of(
            "country", "Spain",
            "province", "A Coruna",
            "city", "Santiago de Compostela",
            "street", "Rua Unauthorized",
            "portal", "9",
            "floor", "2B",
            "postalCode", "15702",
            "latitude", 42.8800,
            "longitude", -8.5450
          )
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    entityManager.clear();
    var unchangedSpace = musicalSpaceDao.findById(space.getId()).orElseThrow();
    assertThat(unchangedSpace.getName()).isEqualTo(space.getName());
    assertThat(unchangedSpace.getDescription()).isEqualTo(space.getDescription());
    assertThat(unchangedSpace.getSpaceType()).isEqualTo(space.getSpaceType());
    assertThat(unchangedSpace.getCapacity()).isEqualTo(space.getCapacity());
    assertThat(unchangedSpace.getMainImage()).isEqualTo(space.getMainImage());
    assertThat(unchangedSpace.getSquareMeters()).isEqualTo(space.getSquareMeters());
    assertThat(unchangedSpace.isSoundproofed()).isEqualTo(space.isSoundproofed());
    assertThat(unchangedSpace.getLocation().getStreet()).isEqualTo(space.getLocation().getStreet());
    assertThat(unchangedSpace.getLocation().getLatitude()).isEqualTo(space.getLocation().getLatitude());
    assertThat(unchangedSpace.getLocation().getLongitude()).isEqualTo(space.getLocation().getLongitude());
  }
}
