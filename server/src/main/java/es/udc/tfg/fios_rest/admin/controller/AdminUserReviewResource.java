package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminUserReviewService;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user-reviews")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserReviewResource {

  private final AdminUserReviewService adminUserReviewService;

  public AdminUserReviewResource(AdminUserReviewService adminUserReviewService) {
    this.adminUserReviewService = adminUserReviewService;
  }

  @GetMapping
  public ResponseEntity<List<UserReviewView>> findAll() {
    return ResponseEntity.ok(adminUserReviewService.findAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
    adminUserReviewService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
