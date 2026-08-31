package es.udc.tfg.fios_rest.equipment.service.dto;

import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;

public record EquipmentRef(
  Long id,
  String name,
  EquipmentCategory category
) {

  public static EquipmentRef from(Equipment equipment) {
    return new EquipmentRef(
      equipment.getId(),
      equipment.getName(),
      equipment.getCategory()
    );
  }
}
