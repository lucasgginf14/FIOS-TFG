package es.udc.tfg.fios_rest.integration.ticketmaster.controller;

import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventSearchRequest;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventView;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterException;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/ticketmaster/events")
@PreAuthorize("hasAuthority('ADMIN')")
@Validated
public class TicketmasterResource {

  private final TicketmasterService ticketmasterService;

  public TicketmasterResource(TicketmasterService ticketmasterService) {
    this.ticketmasterService = ticketmasterService;
  }

  @GetMapping
  public ResponseEntity<List<TicketmasterEventView>> searchEvents(
    @RequestParam(required = false) @Size(max = 120) String city,
    @RequestParam(required = false) @Size(max = 2) String countryCode,
    @RequestParam(required = false) @Size(max = 200) String keyword,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    @RequestParam(required = false) @Size(max = 100) String musicalGenre,
    @RequestParam(required = false) @Min(1) @Max(50) Integer size
  ) throws TicketmasterException {
    return ResponseEntity.ok(ticketmasterService.searchEvents(new TicketmasterEventSearchRequest(
      city,
      countryCode,
      keyword,
      startDate,
      endDate,
      musicalGenre,
      size
    )));
  }

  @GetMapping("/{externalId}")
  public ResponseEntity<TicketmasterEventView> findEventByExternalId(
    @PathVariable String externalId
  ) throws TicketmasterException {
    return ResponseEntity.ok(ticketmasterService.findEventByExternalId(externalId));
  }
}
