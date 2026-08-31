package es.udc.tfg.fios_rest.spaceequipment.service.dto;

import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipmentState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SpaceEquipmentRequest(
  @Positive
  Long equipmentId,

  @Size(max = 120)
  String customEquipmentName,

  @Min(1)
  Integer quantity,

  SpaceEquipmentState state,

  @Size(max = 1000)
  String observations
) {
  public SpaceEquipmentRequest(
    Long equipmentId,
    Integer quantity,
    SpaceEquipmentState state,
    String observations
  ) {
    this(equipmentId, null, quantity, state, observations);
  }
}
