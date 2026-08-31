package es.udc.tfg.fios_rest.favoritespace.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.favoritespace.service.FavoriteSpaceService;
import es.udc.tfg.fios_rest.favoritespace.service.dto.FavoriteSpaceView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FavoriteSpaceResource {

  private final FavoriteSpaceService favoriteSpaceService;

  public FavoriteSpaceResource(FavoriteSpaceService favoriteSpaceService) {
    this.favoriteSpaceService = favoriteSpaceService;
  }

  @PostMapping("/musical-spaces/{spaceId}/favorite")
  public ResponseEntity<FavoriteSpaceView> save(@PathVariable Long spaceId)
    throws NotFoundException, OperationNotAllowed {
    FavoriteSpaceView favoriteSpace = favoriteSpaceService.save(spaceId);
    return ResponseEntity
      .created(URI.create("/api/musical-spaces/" + spaceId + "/favorite"))
      .body(favoriteSpace);
  }

  @GetMapping("/favorites/me")
  public ResponseEntity<List<FavoriteSpaceView>> findMyFavorites() throws NotFoundException {
    return ResponseEntity.ok(favoriteSpaceService.findMyFavorites());
  }

  @DeleteMapping("/musical-spaces/{spaceId}/favorite")
  public ResponseEntity<Void> delete(@PathVariable Long spaceId) throws NotFoundException, OperationNotAllowed {
    favoriteSpaceService.delete(spaceId);
    return ResponseEntity.noContent().build();
  }
}
