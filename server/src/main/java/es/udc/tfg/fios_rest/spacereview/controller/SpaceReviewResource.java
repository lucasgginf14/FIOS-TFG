package es.udc.tfg.fios_rest.spacereview.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.spacereview.service.SpaceReviewService;
import es.udc.tfg.fios_rest.spacereview.service.dto.PendingSpaceReviewView;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceRatingView;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewRequest;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SpaceReviewResource {

  private final SpaceReviewService spaceReviewService;

  public SpaceReviewResource(SpaceReviewService spaceReviewService) {
    this.spaceReviewService = spaceReviewService;
  }

  @GetMapping("/space-reviews/pending")
  public ResponseEntity<List<PendingSpaceReviewView>> findPendingReviews() throws NotFoundException {
    return ResponseEntity.ok(spaceReviewService.findPendingReviews());
  }

  @PostMapping("/reservations/{reservationId}/space-review")
  public ResponseEntity<SpaceReviewView> create(
    @PathVariable Long reservationId,
    @Valid @RequestBody SpaceReviewRequest request
  ) throws NotFoundException, OperationNotAllowed {
    SpaceReviewView spaceReview = spaceReviewService.create(reservationId, request);
    return ResponseEntity
      .created(URI.create("/api/reservations/" + reservationId + "/space-review"))
      .body(spaceReview);
  }

  @GetMapping("/space-reviews/me")
  public ResponseEntity<List<SpaceReviewView>> findMyReviews() throws NotFoundException {
    return ResponseEntity.ok(spaceReviewService.findMyReviews());
  }

  @GetMapping("/musical-spaces/{spaceId}/reviews")
  public ResponseEntity<List<SpaceReviewView>> findByMusicalSpace(@PathVariable Long spaceId) throws NotFoundException {
    return ResponseEntity.ok(spaceReviewService.findByMusicalSpace(spaceId));
  }

  @GetMapping("/musical-spaces/{spaceId}/rating")
  public ResponseEntity<SpaceRatingView> getRating(@PathVariable Long spaceId) throws NotFoundException {
    return ResponseEntity.ok(spaceReviewService.getRating(spaceId));
  }
}
