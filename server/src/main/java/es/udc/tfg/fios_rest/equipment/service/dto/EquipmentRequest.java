package es.udc.tfg.fios_rest.equipment.service.dto;

import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EquipmentRequest(
  @NotBlank
  @Size(max = 120)
  String name,

  @NotNull
  EquipmentCategory category,

  @Size(max = 1000)
  String description
) {
}
