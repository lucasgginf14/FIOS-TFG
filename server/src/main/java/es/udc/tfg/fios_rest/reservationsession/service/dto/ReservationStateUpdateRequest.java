package es.udc.tfg.fios_rest.reservationsession.service.dto;

import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import jakarta.validation.constraints.NotNull;

public record ReservationStateUpdateRequest(
  @NotNull
  ReservationSessionState state
) {
}
