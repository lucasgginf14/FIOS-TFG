package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminUserService;
import es.udc.tfg.fios_rest.admin.service.dto.AdminUserActiveRequest;
import es.udc.tfg.fios_rest.admin.service.dto.AdminUserRef;
import es.udc.tfg.fios_rest.admin.service.dto.AdminUserView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserResource {

  private final AdminUserService adminUserService;

  public AdminUserResource(AdminUserService adminUserService) {
    this.adminUserService = adminUserService;
  }

  @GetMapping
  public ResponseEntity<List<AdminUserRef>> findAll() {
    return ResponseEntity.ok(adminUserService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AdminUserView> findById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(adminUserService.findById(id));
  }

  @PatchMapping("/{id}/active")
  public ResponseEntity<AdminUserView> setActive(
    @PathVariable Long id,
    @Valid @RequestBody AdminUserActiveRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(adminUserService.setActive(id, request.active()));
  }

  @PatchMapping("/{id}/admin-role")
  public ResponseEntity<AdminUserView> promoteToAdmin(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(adminUserService.promoteToAdmin(id));
  }

  @DeleteMapping("/{id}/admin-role")
  public ResponseEntity<AdminUserView> revokeAdminRole(
    @PathVariable Long id
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(adminUserService.revokeAdminRole(id));
  }
}
