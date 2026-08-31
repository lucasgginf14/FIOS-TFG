package es.udc.tfg.fios_rest.reservationsession.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReservationCancellationRequest(
  @NotBlank
  @Size(max = 1000)
  String cancellationReason
) {
}
