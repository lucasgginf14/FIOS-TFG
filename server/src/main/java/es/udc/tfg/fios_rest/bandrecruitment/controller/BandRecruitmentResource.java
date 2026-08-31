package es.udc.tfg.fios_rest.bandrecruitment.controller;

import es.udc.tfg.fios_rest.bandrecruitment.service.BandRecruitmentService;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRequest;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentView;
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
@RequestMapping("/api")
public class BandRecruitmentResource {

  private final BandRecruitmentService bandRecruitmentService;

  public BandRecruitmentResource(BandRecruitmentService bandRecruitmentService) {
    this.bandRecruitmentService = bandRecruitmentService;
  }

  @GetMapping("/band-recruitments")
  public ResponseEntity<List<BandRecruitmentRef>> findOpen() {
    return ResponseEntity.ok(bandRecruitmentService.findOpen());
  }

  @GetMapping("/band-recruitments/me")
  public ResponseEntity<List<BandRecruitmentRef>> findMyRecruitments() throws NotFoundException {
    return ResponseEntity.ok(bandRecruitmentService.findMyRecruitments());
  }

  @GetMapping("/band-recruitments/{id}")
  public ResponseEntity<BandRecruitmentView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(bandRecruitmentService.findById(id));
  }

  @PostMapping("/bands/{bandId}/recruitments")
  public ResponseEntity<BandRecruitmentView> create(
    @PathVariable Long bandId,
    @Valid @RequestBody BandRecruitmentRequest request
  ) throws NotFoundException, OperationNotAllowed {
    BandRecruitmentView bandRecruitment = bandRecruitmentService.create(bandId, request);
    return ResponseEntity
      .created(URI.create("/api/band-recruitments/" + bandRecruitment.id()))
      .body(bandRecruitment);
  }

  @PutMapping("/band-recruitments/{id}")
  public ResponseEntity<BandRecruitmentView> update(
    @PathVariable Long id,
    @Valid @RequestBody BandRecruitmentRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandRecruitmentService.update(id, request));
  }

  @PatchMapping("/band-recruitments/{id}/close")
  public ResponseEntity<BandRecruitmentView> close(
    @PathVariable Long id
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandRecruitmentService.close(id));
  }
}
