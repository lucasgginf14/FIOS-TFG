package es.udc.tfg.fios_rest.exceptions.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.exceptions.service.SpaceAvailabilityExceptionService;
import es.udc.tfg.fios_rest.exceptions.service.dto.SpaceAvailabilityExceptionRequest;
import es.udc.tfg.fios_rest.exceptions.service.dto.SpaceAvailabilityExceptionView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api")
public class SpaceAvailabilityExceptionResource {

  private final SpaceAvailabilityExceptionService spaceAvailabilityExceptionService;

  public SpaceAvailabilityExceptionResource(SpaceAvailabilityExceptionService spaceAvailabilityExceptionService) {
    this.spaceAvailabilityExceptionService = spaceAvailabilityExceptionService;
  }

  @GetMapping("/musical-spaces/{spaceId}/exceptions")
  public ResponseEntity<List<SpaceAvailabilityExceptionView>> findByMusicalSpace(
    @PathVariable Long spaceId
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(spaceAvailabilityExceptionService.findByMusicalSpace(spaceId));
  }

  @PostMapping("/musical-spaces/{spaceId}/exceptions")
  public ResponseEntity<SpaceAvailabilityExceptionView> create(
    @PathVariable Long spaceId,
    @Valid @RequestBody SpaceAvailabilityExceptionRequest request
  ) throws NotFoundException, OperationNotAllowed {
    SpaceAvailabilityExceptionView spaceAvailabilityException =
      spaceAvailabilityExceptionService.create(spaceId, request);
    return ResponseEntity
      .created(URI.create("/api/exceptions/" + spaceAvailabilityException.id()))
      .body(spaceAvailabilityException);
  }

  @PutMapping("/exceptions/{id}")
  public ResponseEntity<SpaceAvailabilityExceptionView> update(
    @PathVariable Long id,
    @Valid @RequestBody SpaceAvailabilityExceptionRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(spaceAvailabilityExceptionService.update(id, request));
  }

  @DeleteMapping("/exceptions/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    spaceAvailabilityExceptionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
