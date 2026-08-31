package es.udc.tfg.fios_rest.spaceequipment.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.model.SpaceEquipmentAlreadyExistsException;
import es.udc.tfg.fios_rest.spaceequipment.service.SpaceEquipmentService;
import es.udc.tfg.fios_rest.spaceequipment.service.dto.SpaceEquipmentRequest;
import es.udc.tfg.fios_rest.spaceequipment.service.dto.SpaceEquipmentView;
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
public class SpaceEquipmentResource {

  private final SpaceEquipmentService spaceEquipmentService;

  public SpaceEquipmentResource(SpaceEquipmentService spaceEquipmentService) {
    this.spaceEquipmentService = spaceEquipmentService;
  }

  @GetMapping("/musical-spaces/{spaceId}/equipment")
  public ResponseEntity<List<SpaceEquipmentView>> findByMusicalSpace(
    @PathVariable Long spaceId
  ) throws NotFoundException {
    return ResponseEntity.ok(spaceEquipmentService.findByMusicalSpace(spaceId));
  }

  @PostMapping("/musical-spaces/{spaceId}/equipment")
  public ResponseEntity<SpaceEquipmentView> create(
    @PathVariable Long spaceId,
    @Valid @RequestBody SpaceEquipmentRequest request
  ) throws NotFoundException, OperationNotAllowed, SpaceEquipmentAlreadyExistsException {
    SpaceEquipmentView spaceEquipment = spaceEquipmentService.create(spaceId, request);
    return ResponseEntity.created(URI.create("/api/space-equipment/" + spaceEquipment.id())).body(spaceEquipment);
  }

  @PutMapping("/space-equipment/{id}")
  public ResponseEntity<SpaceEquipmentView> update(
    @PathVariable Long id,
    @Valid @RequestBody SpaceEquipmentRequest request
  ) throws NotFoundException, OperationNotAllowed, SpaceEquipmentAlreadyExistsException {
    return ResponseEntity.ok(spaceEquipmentService.update(id, request));
  }

  @DeleteMapping("/space-equipment/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    spaceEquipmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
