package es.udc.tfg.fios_rest.event.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.event.service.EventService;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.event.service.dto.EventMapView;
import es.udc.tfg.fios_rest.event.service.dto.EventRef;
import es.udc.tfg.fios_rest.event.service.dto.EventRequest;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@Validated
public class EventResource {

  private final EventService eventService;

  public EventResource(EventService eventService) {
    this.eventService = eventService;
  }

  @GetMapping
  public ResponseEntity<List<EventRef>> findPublished(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    @RequestParam(required = false) @Size(max = 120) String city,
    @RequestParam(required = false) @Size(max = 120) String province,
    @RequestParam(required = false) @Size(max = 100) String musicalGenre,
    @RequestParam(required = false) EventSource source,
    @RequestParam(required = false) EventType eventType,
    @RequestParam(required = false) EventStatus status,
    @RequestParam(required = false) @Positive Long bandId
  ) {
    return ResponseEntity.ok(eventService.findPublished(new EventFilterParams(
      date,
      dateFrom,
      dateTo,
      city,
      province,
      musicalGenre,
      source,
      eventType,
      status,
      bandId
    )));
  }

  @GetMapping("/upcoming")
  public ResponseEntity<List<EventRef>> findUpcomingPublished() {
    return ResponseEntity.ok(eventService.findUpcomingPublished());
  }

  @GetMapping("/map")
  public ResponseEntity<List<EventMapView>> findPublishedForMap(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    @RequestParam(required = false) @Size(max = 120) String city,
    @RequestParam(required = false) @Size(max = 120) String province,
    @RequestParam(required = false) @Size(max = 100) String musicalGenre,
    @RequestParam(required = false) EventSource source,
    @RequestParam(required = false) EventType eventType,
    @RequestParam(required = false) EventStatus status,
    @RequestParam(required = false) @Positive Long bandId
  ) {
    return ResponseEntity.ok(eventService.findPublishedForMap(new EventFilterParams(
      date,
      dateFrom,
      dateTo,
      city,
      province,
      musicalGenre,
      source,
      eventType,
      status,
      bandId
    )));
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<EventView> findById(@PathVariable Long eventId) throws NotFoundException {
    return ResponseEntity.ok(eventService.findById(eventId));
  }

  @PostMapping
  public ResponseEntity<EventView> create(@Valid @RequestBody EventRequest request) throws NotFoundException {
    EventView event = eventService.create(request);
    return ResponseEntity
      .created(URI.create("/api/events/" + event.id()))
      .body(event);
  }

  @PutMapping("/{eventId}")
  public ResponseEntity<EventView> update(
    @PathVariable Long eventId,
    @Valid @RequestBody EventRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(eventService.update(eventId, request));
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> delete(@PathVariable Long eventId) throws NotFoundException {
    eventService.delete(eventId);
    return ResponseEntity.noContent().build();
  }
}
