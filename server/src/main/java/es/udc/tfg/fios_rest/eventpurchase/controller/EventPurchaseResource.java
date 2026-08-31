package es.udc.tfg.fios_rest.eventpurchase.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.EventAlreadyPurchasedException;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.eventpurchase.service.EventPurchaseService;
import es.udc.tfg.fios_rest.eventpurchase.service.dto.EventPurchaseStatusView;
import es.udc.tfg.fios_rest.eventpurchase.service.dto.EventPurchaseView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventPurchaseResource {

  private final EventPurchaseService eventPurchaseService;

  public EventPurchaseResource(EventPurchaseService eventPurchaseService) {
    this.eventPurchaseService = eventPurchaseService;
  }

  @PostMapping("/events/{eventId}/purchase")
  public ResponseEntity<EventPurchaseView> purchase(@PathVariable Long eventId)
    throws NotFoundException, OperationNotAllowed, EventAlreadyPurchasedException {
    EventPurchaseView reservation = eventPurchaseService.purchase(eventId);
    return ResponseEntity
      .created(URI.create("/api/events/" + eventId + "/purchase"))
      .body(reservation);
  }

  @PostMapping("/events/{eventId}/reserve")
  public ResponseEntity<EventPurchaseView> reserve(@PathVariable Long eventId)
    throws NotFoundException, OperationNotAllowed, EventAlreadyPurchasedException {
    EventPurchaseView reservation = eventPurchaseService.reserve(eventId);
    return ResponseEntity
      .created(URI.create("/api/events/" + eventId + "/reserve"))
      .body(reservation);
  }

  @GetMapping("/event-purchases/me")
  public ResponseEntity<List<EventPurchaseView>> findMyReservations() throws NotFoundException {
    return ResponseEntity.ok(eventPurchaseService.findMyReservations());
  }

  @PatchMapping("/event-purchases/{purchaseId}/cancel")
  public ResponseEntity<EventPurchaseView> cancel(@PathVariable Long purchaseId)
    throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(eventPurchaseService.cancel(purchaseId));
  }

  @GetMapping("/event-purchases/events/{eventId}/status")
  public ResponseEntity<EventPurchaseStatusView> findMyEventStatus(@PathVariable Long eventId)
    throws NotFoundException {
    return ResponseEntity.ok(eventPurchaseService.findMyEventStatus(eventId));
  }

  @GetMapping("/events/{eventId}/reservation-status")
  public ResponseEntity<EventPurchaseStatusView> findEventReservationStatus(@PathVariable Long eventId)
    throws NotFoundException {
    return ResponseEntity.ok(eventPurchaseService.findEventReservationStatus(eventId));
  }

  @GetMapping("/events/{eventId}/reservations")
  public ResponseEntity<List<EventPurchaseView>> findActiveReservationsForEvent(@PathVariable Long eventId)
    throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(eventPurchaseService.findActiveReservationsForEvent(eventId));
  }
}
