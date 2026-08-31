package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminEventService;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.event.service.dto.EventRef;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasAuthority('ADMIN')")
@Validated
public class AdminEventResource {

  private final AdminEventService adminEventService;

  public AdminEventResource(AdminEventService adminEventService) {
    this.adminEventService = adminEventService;
  }

  @GetMapping
  public ResponseEntity<List<EventRef>> findAll(
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
    return ResponseEntity.ok(adminEventService.findAll(new EventFilterParams(
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

  @GetMapping("/{id}")
  public ResponseEntity<EventView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(adminEventService.findById(id));
  }
}
