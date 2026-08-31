package es.udc.tfg.fios_rest.event.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.event.service.EventService;
import es.udc.tfg.fios_rest.event.service.dto.EventRequest;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/bands/{bandId}/events")
public class BandEventResource {

  private final EventService eventService;

  public BandEventResource(EventService eventService) {
    this.eventService = eventService;
  }

  @PostMapping
  public ResponseEntity<EventView> createForBand(
    @PathVariable Long bandId,
    @Valid @RequestBody EventRequest request
  ) throws NotFoundException, OperationNotAllowed {
    EventView event = eventService.createForBand(bandId, request);
    return ResponseEntity
      .created(URI.create("/api/events/" + event.id()))
      .body(event);
  }
}
