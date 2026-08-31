package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminSpaceReviewService;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/space-reviews")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminSpaceReviewResource {

  private final AdminSpaceReviewService adminSpaceReviewService;

  public AdminSpaceReviewResource(AdminSpaceReviewService adminSpaceReviewService) {
    this.adminSpaceReviewService = adminSpaceReviewService;
  }

  @GetMapping
  public ResponseEntity<List<SpaceReviewView>> findAll() {
    return ResponseEntity.ok(adminSpaceReviewService.findAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
    adminSpaceReviewService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
