package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminBandRecruitmentService;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/band-recruitments")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminBandRecruitmentResource {

  private final AdminBandRecruitmentService adminBandRecruitmentService;

  public AdminBandRecruitmentResource(AdminBandRecruitmentService adminBandRecruitmentService) {
    this.adminBandRecruitmentService = adminBandRecruitmentService;
  }

  @GetMapping
  public ResponseEntity<List<BandRecruitmentRef>> findAll() {
    return ResponseEntity.ok(adminBandRecruitmentService.findAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    adminBandRecruitmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
