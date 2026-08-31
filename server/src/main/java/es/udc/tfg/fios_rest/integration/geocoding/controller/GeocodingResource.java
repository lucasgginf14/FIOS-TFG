package es.udc.tfg.fios_rest.integration.geocoding.controller;

import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingFreeTextRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingResultView;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingStructuredRequest;
import es.udc.tfg.fios_rest.integration.geocoding.service.GeocodingException;
import es.udc.tfg.fios_rest.integration.geocoding.service.GeocodingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geocoding")
@Validated
public class GeocodingResource {

  private final GeocodingService geocodingService;

  public GeocodingResource(GeocodingService geocodingService) {
    this.geocodingService = geocodingService;
  }

  @GetMapping("/search")
  public ResponseEntity<List<GeocodingResultView>> searchByQuery(
    @RequestParam @NotBlank @Size(max = 250) String query
  ) throws GeocodingException {
    return ResponseEntity.ok(geocodingService.search(new GeocodingFreeTextRequest(query)));
  }

  @PostMapping("/search")
  public ResponseEntity<List<GeocodingResultView>> searchByStructuredAddress(
    @Valid @RequestBody GeocodingStructuredRequest request
  ) throws GeocodingException {
    return ResponseEntity.ok(geocodingService.search(request));
  }
}
