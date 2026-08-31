package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.equipment.persistence.dao.EquipmentDao;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipmentState;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpaceEquipmentHttpIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private EquipmentDao equipmentDao;

  @Test
  void duplicateSpaceEquipmentReturnsSpecificConflictResponse() throws Exception {
    var manager = createUser("space-equipment-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    Equipment equipment = equipmentDao.save(new Equipment("Mesa de mezclas test", EquipmentCategory.SOUND, "Test"));

    String body = objectMapper.writeValueAsString(Map.of(
      "equipmentId", equipment.getId(),
      "quantity", 1,
      "state", SpaceEquipmentState.AVAILABLE.name(),
      "observations", "Disponible"
    ));

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/equipment", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated());

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/equipment", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("SPACE_EQUIPMENT_ALREADY_EXISTS"))
      .andExpect(jsonPath("$.message").value("Este equipamiento ya está asociado al espacio."));
  }

  @Test
  void managerCanAddCustomOtherEquipmentWithObservations() throws Exception {
    var manager = createUser("space-equipment-custom-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);

    mockMvc.perform(post("/api/musical-spaces/{spaceId}/equipment", space.getId())
        .header("Authorization", "Bearer " + tokenFor(manager))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "customEquipmentName", "Pedalboard extra",
          "quantity", 2,
          "state", SpaceEquipmentState.LIMITED.name(),
          "observations", "Pedalboard con fuente aislada y cables cortos"
        ))))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.equipment.name").value("Pedalboard extra"))
      .andExpect(jsonPath("$.equipment.category").value(EquipmentCategory.OTHER.name()))
      .andExpect(jsonPath("$.quantity").value(2))
      .andExpect(jsonPath("$.state").value(SpaceEquipmentState.LIMITED.name()))
      .andExpect(jsonPath("$.observations").value("Pedalboard con fuente aislada y cables cortos"));

    assertThat(equipmentDao.findByName("Pedalboard extra"))
      .get()
      .extracting(Equipment::getCategory)
      .isEqualTo(EquipmentCategory.OTHER);

    mockMvc.perform(get("/api/musical-spaces/{spaceId}/equipment", space.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.equipment.name=='Pedalboard extra')]").exists());
  }
}
