package es.udc.tfg.fios_rest.bandmember.service.dto;

import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import jakarta.validation.constraints.NotNull;

public record BandMemberRoleUpdateRequest(
  @NotNull
  BandMemberRole roleInBand
) {
}
