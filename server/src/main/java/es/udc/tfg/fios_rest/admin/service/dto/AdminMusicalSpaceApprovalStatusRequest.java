package es.udc.tfg.fios_rest.admin.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import jakarta.validation.constraints.NotNull;

public record AdminMusicalSpaceApprovalStatusRequest(
  @NotNull
  MusicalSpaceApprovalStatus approvalStatus
) {
}
