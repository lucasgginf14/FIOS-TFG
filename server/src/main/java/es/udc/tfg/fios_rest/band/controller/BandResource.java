package es.udc.tfg.fios_rest.band.controller;

import es.udc.tfg.fios_rest.band.service.BandService;
import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.band.service.dto.BandRequest;
import es.udc.tfg.fios_rest.band.service.dto.BandView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
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
@RequestMapping("/api/bands")
public class BandResource {

  private final BandService bandService;

  public BandResource(BandService bandService) {
    this.bandService = bandService;
  }

  @GetMapping
  public ResponseEntity<List<BandRef>> findAll() {
    return ResponseEntity.ok(bandService.findAll());
  }

  @GetMapping("/me")
  public ResponseEntity<List<BandRef>> findMyBands() throws NotFoundException {
    return ResponseEntity.ok(bandService.findMyBands());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BandView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(bandService.findById(id));
  }

  @PostMapping
  public ResponseEntity<BandView> create(
    @Valid @RequestBody BandRequest request
  ) throws NotFoundException, OperationNotAllowed {
    BandView band = bandService.create(request);
    return ResponseEntity.created(URI.create("/api/bands/" + band.id())).body(band);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BandView> update(
    @PathVariable Long id,
    @Valid @RequestBody BandRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandService.update(id, request));
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<BandView> deactivate(
    @PathVariable Long id
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandService.deactivate(id));
  }
}
