package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BandHttpIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private InstrumentDao instrumentDao;

  @Test
  void authenticatedUserCanCreateBandAndBecomesLeader() throws Exception {
    var leader = createUser("band-create-leader@example.com", "password123");
    var guitar = instrumentDao.save(new Instrument("Guitarra", InstrumentCategory.STRINGS));
    var voice = instrumentDao.save(new Instrument("Voz", InstrumentCategory.VOICE));
    leader.getInstruments().addAll(List.of(voice, guitar));
    userDao.update(leader);

    var result = mockMvc.perform(post("/api/bands")
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "The Backend Notes",
          "description", "Fusion rehearsal band",
          "mainGenre", "Fusion",
          "baseCity", "A Coruna",
          "image", "band.png"
        ))))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/bands/")))
      .andExpect(jsonPath("$.name").value("The Backend Notes"))
      .andExpect(jsonPath("$.description").value("Fusion rehearsal band"))
      .andExpect(jsonPath("$.creationDate").exists())
      .andExpect(jsonPath("$.active").value(true))
      .andReturn();

    var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(get("/api/bands/{id}/members", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].user.id").value(leader.getId()))
      .andExpect(jsonPath("$[0].roleInBand").value("LEADER"))
      .andExpect(jsonPath("$[0].instruments.length()").value(1))
      .andExpect(jsonPath("$[0].instruments[0].name").value("Guitarra"))
      .andExpect(jsonPath("$[0].user.email").doesNotExist());
  }

  @Test
  void adminCannotCreateBandFromPublicFlow() throws Exception {
    var admin = createAdmin("band-create-admin@example.com", "password123");

    mockMvc.perform(post("/api/bands")
        .header("Authorization", "Bearer " + tokenFor(admin))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Admin Should Not Lead",
          "description", "Read only public flow",
          "mainGenre", "Rock",
          "baseCity", "A Coruna",
          "image", "band.png"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void myBandsReturnsFieldsExpectedByFrontendCards() throws Exception {
    var leader = createUser("band-cards-leader@example.com", "password123");
    var band = createBandWithLeader(leader, "Card Contract Band");

    mockMvc.perform(get("/api/bands/me")
        .header("Authorization", "Bearer " + tokenFor(leader)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==" + band.getId() + ")].description").value("Band description"))
      .andExpect(jsonPath("$[?(@.id==" + band.getId() + ")].creationDate").exists());
  }

  @Test
  void authenticatedNonLeaderCannotUpdateBandOrManageMembers() throws Exception {
    var leader = createUser("band-auth-leader@example.com", "password123");
    var outsider = createUser("band-auth-outsider@example.com", "password123");
    var candidate = createUser("band-auth-candidate@example.com", "password123");
    var band = createBandWithLeader(leader, "Authorization Band");

    mockMvc.perform(put("/api/bands/{id}", band.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Hijacked Band",
          "description", "Should not update",
          "mainGenre", "Rock",
          "baseCity", "Lugo"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(post("/api/bands/{bandId}/members", band.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "userId", candidate.getId(),
          "roleInBand", "MEMBER"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  void leaderCanManageMembersButCannotRemoveLastLeader() throws Exception {
    var leader = createUser("band-member-leader@example.com", "password123");
    var member = createUser("band-member-user@example.com", "password123");
    var band = createBandWithLeader(leader, "Member Rules Band");
    var leaderMembership = bandMemberDao.findActiveByBandAndUser(band.getId(), leader.getId()).orElseThrow();

    mockMvc.perform(post("/api/bands/{bandId}/members", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "userId", member.getId(),
          "roleInBand", "MEMBER"
        ))))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.user.id").value(member.getId()))
      .andExpect(jsonPath("$.roleInBand").value("MEMBER"));

    mockMvc.perform(patch("/api/bands/{bandId}/members/{memberId}/role", band.getId(), leaderMembership.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("roleInBand", "MEMBER"))))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    var memberMembership = bandMemberDao.findActiveByBandAndUser(band.getId(), member.getId()).orElseThrow();

    mockMvc.perform(patch("/api/bands/{bandId}/members/{memberId}/role", band.getId(), memberMembership.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("roleInBand", "LEADER"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.roleInBand").value("LEADER"));
  }

  @Test
  void leaderCanSearchMemberCandidatesByEmailOrName() throws Exception {
    var leader = createUser("band-candidates-leader@example.com", "password123");
    var candidate = createUser("band-candidates-target@example.com", "password123");
    var existingMember = createUser("band-candidates-existing@example.com", "password123");
    var outsider = createUser("band-search-outsider@example.com", "password123");
    var admin = createAdmin("band-candidates-admin@example.com", "password123");
    var band = createBandWithLeader(leader, "Candidate Selector Band");
    bandMemberDao.save(new BandMember(band, existingMember, BandMemberRole.MEMBER));
    entityManager.flush();

    mockMvc.perform(get("/api/bands/{bandId}/members/candidates", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .param("query", "band-candidates"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].id").value(candidate.getId()))
      .andExpect(jsonPath("$[0].email").value(candidate.getEmail()))
      .andExpect(jsonPath("$[0].name").value(candidate.getName()));

    mockMvc.perform(get("/api/bands/{bandId}/members/candidates", band.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider))
        .param("query", candidate.getEmail()))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(post("/api/bands/{bandId}/members", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "userId", admin.getId(),
          "roleInBand", "MEMBER"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

    mockMvc.perform(get("/api/bands/{bandId}/members/candidates", band.getId())
        .param("query", candidate.getEmail()))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void deactivatingBandHidesItAndDeactivatesMembers() throws Exception {
    var leader = createUser("band-deactivate-leader@example.com", "password123");
    var band = createBandWithLeader(leader, "Deactivation Band");

    mockMvc.perform(patch("/api/bands/{id}/deactivate", band.getId())
        .header("Authorization", "Bearer " + tokenFor(leader)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.active").value(false));

    mockMvc.perform(get("/api/bands/{id}", band.getId()))
      .andExpect(status().isNotFound());

    mockMvc.perform(get("/api/bands/me")
        .header("Authorization", "Bearer " + tokenFor(leader)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.id==" + band.getId() + ")]").isEmpty());
  }

  private Band createBandWithLeader(User leader, String name) {
    Band band = bandDao.save(new Band(name, "Band description", "Rock", "A Coruna", "band.png"));
    bandMemberDao.save(new BandMember(band, leader, BandMemberRole.LEADER));
    entityManager.flush();
    return band;
  }
}
