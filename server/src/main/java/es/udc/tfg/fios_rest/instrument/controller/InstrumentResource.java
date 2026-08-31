package es.udc.tfg.fios_rest.instrument.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.instrument.service.InstrumentService;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRequest;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentResource {

  private final InstrumentService instrumentService;

  public InstrumentResource(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @GetMapping
  public ResponseEntity<List<InstrumentRef>> findAll() {
    return ResponseEntity.ok(instrumentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<InstrumentView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(instrumentService.findById(id));
  }

  @PostMapping
  public ResponseEntity<InstrumentView> create(
    @Valid @RequestBody InstrumentRequest request
  ) {
    InstrumentView instrument = instrumentService.create(request);
    return ResponseEntity.created(URI.create("/api/instruments/" + instrument.id())).body(instrument);
  }

  @PutMapping("/{id}")
  public ResponseEntity<InstrumentView> update(
    @PathVariable Long id,
    @Valid @RequestBody InstrumentRequest request
  ) throws NotFoundException {
    return ResponseEntity.ok(instrumentService.update(id, request));
  }
}
