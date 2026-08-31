package es.udc.tfg.fios_rest.bandmember.service.dto;

import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BandMemberCreateRequest(
  @NotNull
  @Positive
  Long userId,

  @NotNull
  BandMemberRole roleInBand
) {
}
