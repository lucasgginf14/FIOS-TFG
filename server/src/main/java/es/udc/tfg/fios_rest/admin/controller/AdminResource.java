package es.udc.tfg.fios_rest.admin.controller;

import es.udc.tfg.fios_rest.admin.service.AdminService;
import es.udc.tfg.fios_rest.admin.service.dto.AdminOverviewView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminResource {

  private final AdminService adminService;

  public AdminResource(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping
  public ResponseEntity<AdminOverviewView> getOverview() {
    return ResponseEntity.ok(adminService.getOverview());
  }
}
