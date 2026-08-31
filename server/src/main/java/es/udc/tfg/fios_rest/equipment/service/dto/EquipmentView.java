package es.udc.tfg.fios_rest.equipment.service.dto;

import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;

public record EquipmentView(
  Long id,
  String name,
  EquipmentCategory category,
  String description
) {

  public static EquipmentView from(Equipment equipment) {
    return new EquipmentView(
      equipment.getId(),
      equipment.getName(),
      equipment.getCategory(),
      equipment.getDescription()
    );
  }
}
