package es.udc.tfg.fios_rest.reservationsession.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.reservationsession.service.ReservationSessionService;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationCancellationRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionView;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationStateUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationSessionResource {

  private final ReservationSessionService reservationSessionService;

  public ReservationSessionResource(ReservationSessionService reservationSessionService) {
    this.reservationSessionService = reservationSessionService;
  }

  @PostMapping("/reservations")
  public ResponseEntity<ReservationSessionView> create(
    @Valid @RequestBody ReservationSessionRequest request
  ) throws NotFoundException, OperationNotAllowed {
    ReservationSessionView reservationSession = reservationSessionService.create(request);
    return ResponseEntity
      .created(URI.create("/api/reservations/" + reservationSession.id()))
      .body(reservationSession);
  }

  @GetMapping("/reservations/me")
  public ResponseEntity<List<ReservationSessionView>> findMyReservations() throws NotFoundException {
    return ResponseEntity.ok(reservationSessionService.findMyReservations());
  }

  @GetMapping("/reservations/{id}")
  public ResponseEntity<ReservationSessionView> findById(
    @PathVariable Long id
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(reservationSessionService.findById(id));
  }

  @PutMapping("/reservations/{id}")
  public ResponseEntity<ReservationSessionView> update(
    @PathVariable Long id,
    @Valid @RequestBody ReservationSessionRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(reservationSessionService.update(id, request));
  }

  @PatchMapping("/reservations/{id}/cancel")
  public ResponseEntity<ReservationSessionView> cancel(
    @PathVariable Long id,
    @Valid @RequestBody ReservationCancellationRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(reservationSessionService.cancel(id, request));
  }

  @GetMapping("/musical-spaces/me/reservations")
  public ResponseEntity<List<ReservationSessionView>> findReservationsForMySpaces() throws NotFoundException {
    return ResponseEntity.ok(reservationSessionService.findReservationsForMySpaces());
  }

  @PatchMapping("/reservations/{id}/state")
  public ResponseEntity<ReservationSessionView> updateState(
    @PathVariable Long id,
    @Valid @RequestBody ReservationStateUpdateRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(reservationSessionService.updateState(id, request));
  }
}
