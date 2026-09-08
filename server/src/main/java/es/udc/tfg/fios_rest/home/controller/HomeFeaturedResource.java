package es.udc.tfg.fios_rest.home.controller;

import es.udc.tfg.fios_rest.home.service.HomeFeaturedService;
import es.udc.tfg.fios_rest.home.service.dto.HomeFeaturedView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeFeaturedResource {

  private final HomeFeaturedService homeFeaturedService;

  public HomeFeaturedResource(HomeFeaturedService homeFeaturedService) {
    this.homeFeaturedService = homeFeaturedService;
  }

  @GetMapping("/featured")
  public ResponseEntity<HomeFeaturedView> findFeatured() {
    return ResponseEntity.ok(homeFeaturedService.findFeatured());
  }
}
