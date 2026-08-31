package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminReservationService;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationCancellationRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminReservationResource {

  private final AdminReservationService adminReservationService;

  public AdminReservationResource(AdminReservationService adminReservationService) {
    this.adminReservationService = adminReservationService;
  }

  @GetMapping
  public ResponseEntity<List<ReservationSessionView>> findAll() throws NotFoundException {
    return ResponseEntity.ok(adminReservationService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReservationSessionView> findById(@PathVariable Long id)
    throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(adminReservationService.findById(id));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<ReservationSessionView> cancel(
    @PathVariable Long id,
    @Valid @RequestBody ReservationCancellationRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(adminReservationService.cancel(id, request));
  }
}
