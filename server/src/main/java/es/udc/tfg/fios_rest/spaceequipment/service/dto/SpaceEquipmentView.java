package es.udc.tfg.fios_rest.spaceequipment.service.dto;

import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipmentState;

public record SpaceEquipmentView(
  Long id,
  int quantity,
  SpaceEquipmentState state,
  String observations,
  MusicalSpaceRef musicalSpace,
  EquipmentRef equipment
) {

  public static SpaceEquipmentView from(SpaceEquipment spaceEquipment) {
    return new SpaceEquipmentView(
      spaceEquipment.getId(),
      spaceEquipment.getQuantity(),
      spaceEquipment.getState(),
      spaceEquipment.getObservations(),
      MusicalSpaceRef.from(spaceEquipment.getMusicalSpace()),
      EquipmentRef.from(spaceEquipment.getEquipment())
    );
  }
}
