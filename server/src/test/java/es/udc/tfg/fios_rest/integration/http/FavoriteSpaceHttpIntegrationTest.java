package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteSpaceHttpIntegrationTest extends IntegrationTestSupport {

  @Test
  void authenticatedUserCanAddVisibleSpaceToFavorites() throws Exception {
    var manager = createUser("favorite-add-manager@example.com", "password123");
    var user = createUser("favorite-add-user@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/musical-spaces/" + space.getId() + "/favorite"))
      .andExpect(jsonPath("$.musicalSpace.id").value(space.getId()))
      .andExpect(jsonPath("$.musicalSpace.name").value(space.getName()));
  }

  @Test
  void addingSameFavoriteTwiceReturnsControlledErrorAndDoesNotDuplicate() throws Exception {
    var manager = createUser("favorite-duplicate-manager@example.com", "password123");
    var user = createUser("favorite-duplicate-user@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    String token = tokenFor(user);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));

    mockMvc.perform(get("/api/favorites/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void authenticatedUserCanRemoveFavorite() throws Exception {
    var manager = createUser("favorite-remove-manager@example.com", "password123");
    var user = createUser("favorite-remove-user@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    String token = tokenFor(user);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated());

    mockMvc.perform(delete("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/favorites/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void favoritesListOnlyReturnsCurrentUsersFavorites() throws Exception {
    var manager = createUser("favorite-list-manager@example.com", "password123");
    var userOne = createUser("favorite-list-user-one@example.com", "password123");
    var userTwo = createUser("favorite-list-user-two@example.com", "password123");
    var spaceOne = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var spaceTwo = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", spaceOne.getId())
        .header("Authorization", "Bearer " + tokenFor(userOne)))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", spaceTwo.getId())
        .header("Authorization", "Bearer " + tokenFor(userTwo)))
      .andExpect(status().isCreated());

    mockMvc.perform(get("/api/favorites/me")
        .header("Authorization", "Bearer " + tokenFor(userOne)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[?(@.musicalSpace.id==" + spaceOne.getId() + ")]").exists())
      .andExpect(jsonPath("$[?(@.musicalSpace.id==" + spaceTwo.getId() + ")]").doesNotExist());
  }

  @Test
  void userCannotFavoriteNonVisibleSpaceUnlessCanManageIt() throws Exception {
    var manager = createUser("favorite-private-manager@example.com", "password123");
    var outsider = createUser("favorite-private-outsider@example.com", "password123");
    var pendingSpace = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);
    var inactiveSpace = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, false);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", pendingSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", inactiveSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(outsider)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("ACTION_NOT_AVAILABLE"));
  }

  @Test
  void hiddenFavoriteIsFilteredFromFavoritesList() throws Exception {
    var manager = createUser("favorite-hidden-manager@example.com", "password123");
    var user = createUser("favorite-hidden-user@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    String token = tokenFor(user);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", space.getId())
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isCreated());

    space.setApprovalStatus(MusicalSpaceApprovalStatus.REJECTED);
    musicalSpaceDao.update(space);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(get("/api/favorites/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0))
      .andExpect(jsonPath("$[?(@.musicalSpace.name=='" + space.getName() + "')]").doesNotExist());
  }

  @Test
  void managerCanFavoriteNonVisibleSpacesTheyManageButAdminCannotMutateFavorites() throws Exception {
    var manager = createUser("favorite-manage-manager@example.com", "password123");
    var admin = createAdmin("favorite-manage-admin@example.com", "password123");
    var pendingSpace = createSpace(manager, MusicalSpaceApprovalStatus.PENDING, true);
    var inactiveSpace = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, false);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", pendingSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.musicalSpace.id").value(pendingSpace.getId()));

    mockMvc.perform(get("/api/favorites/me")
        .header("Authorization", "Bearer " + tokenFor(manager)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.musicalSpace.id==" + pendingSpace.getId() + ")]").exists());

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/favorite", inactiveSpace.getId())
        .header("Authorization", "Bearer " + tokenFor(admin)))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }
}
