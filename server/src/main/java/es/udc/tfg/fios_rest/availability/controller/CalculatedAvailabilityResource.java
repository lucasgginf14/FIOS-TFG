package es.udc.tfg.fios_rest.availability.controller;

import es.udc.tfg.fios_rest.availability.service.CalculatedAvailabilityService;
import es.udc.tfg.fios_rest.availability.service.dto.CalculatedAvailabilityView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/musical-spaces/{spaceId}/availability")
public class CalculatedAvailabilityResource {

  private final CalculatedAvailabilityService calculatedAvailabilityService;

  public CalculatedAvailabilityResource(CalculatedAvailabilityService calculatedAvailabilityService) {
    this.calculatedAvailabilityService = calculatedAvailabilityService;
  }

  @GetMapping
  public ResponseEntity<CalculatedAvailabilityView> findAvailability(
    @PathVariable Long spaceId,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) throws NotFoundException {
    return ResponseEntity.ok(calculatedAvailabilityService.findAvailability(spaceId, date));
  }
}
