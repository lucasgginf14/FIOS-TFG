package es.udc.tfg.fios_rest.equipment.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.equipment.service.EquipmentService;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentRef;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentRequest;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentView;
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
@RequestMapping("/api/equipments")
public class EquipmentResource {

  private final EquipmentService equipmentService;

  public EquipmentResource(EquipmentService equipmentService) {
    this.equipmentService = equipmentService;
  }

  @GetMapping
  public ResponseEntity<List<EquipmentRef>> findAll() {
    return ResponseEntity.ok(equipmentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<EquipmentView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(equipmentService.findById(id));
  }

  @PostMapping
  public ResponseEntity<EquipmentView> create(@Valid @RequestBody EquipmentRequest request) {
    EquipmentView equipment = equipmentService.create(request);
    return ResponseEntity.created(URI.create("/api/equipments/" + equipment.id())).body(equipment);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EquipmentView> update(
    @PathVariable Long id,
    @Valid @RequestBody EquipmentRequest request
  ) throws NotFoundException {
    return ResponseEntity.ok(equipmentService.update(id, request));
  }
}
