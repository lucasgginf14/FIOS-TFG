package es.udc.tfg.fios_rest.musicalspace.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.musicalspace.service.MusicalSpaceService;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRequest;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceView;
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
@RequestMapping("/api/musical-spaces")
public class MusicalSpaceResource {

  private final MusicalSpaceService musicalSpaceService;

  public MusicalSpaceResource(MusicalSpaceService musicalSpaceService) {
    this.musicalSpaceService = musicalSpaceService;
  }

  @GetMapping
  public ResponseEntity<List<MusicalSpaceRef>> findPublic() {
    return ResponseEntity.ok(musicalSpaceService.findPublic());
  }

  @GetMapping("/me")
  public ResponseEntity<List<MusicalSpaceRef>> findMySpaces() throws NotFoundException {
    return ResponseEntity.ok(musicalSpaceService.findMySpaces());
  }

  @GetMapping("/{id}")
  public ResponseEntity<MusicalSpaceView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(musicalSpaceService.findById(id));
  }

  @PostMapping
  public ResponseEntity<MusicalSpaceView> create(
    @Valid @RequestBody MusicalSpaceRequest request
  ) throws NotFoundException, OperationNotAllowed {
    MusicalSpaceView musicalSpace = musicalSpaceService.create(request);
    return ResponseEntity
      .created(URI.create("/api/musical-spaces/" + musicalSpace.id()))
      .body(musicalSpace);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MusicalSpaceView> update(
    @PathVariable Long id,
    @Valid @RequestBody MusicalSpaceRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(musicalSpaceService.update(id, request));
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<MusicalSpaceView> deactivate(
    @PathVariable Long id
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(musicalSpaceService.deactivate(id));
  }
}
