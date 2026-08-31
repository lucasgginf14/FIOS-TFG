package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminMusicalSpaceService;
import es.udc.tfg.fios_rest.admin.service.dto.AdminMusicalSpaceApprovalStatusRequest;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/musical-spaces")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminMusicalSpaceResource {

  private final AdminMusicalSpaceService adminMusicalSpaceService;

  public AdminMusicalSpaceResource(AdminMusicalSpaceService adminMusicalSpaceService) {
    this.adminMusicalSpaceService = adminMusicalSpaceService;
  }

  @GetMapping
  public ResponseEntity<List<MusicalSpaceRef>> findAll() {
    return ResponseEntity.ok(adminMusicalSpaceService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<MusicalSpaceView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(adminMusicalSpaceService.findById(id));
  }

  @PatchMapping("/{id}/approval-status")
  public ResponseEntity<MusicalSpaceView> updateApprovalStatus(
    @PathVariable Long id,
    @Valid @RequestBody AdminMusicalSpaceApprovalStatusRequest request
  ) throws NotFoundException {
    return ResponseEntity.ok(adminMusicalSpaceService.updateApprovalStatus(id, request.approvalStatus()));
  }
}
