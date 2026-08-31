package es.udc.tfg.fios_rest.userreview.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.userreview.service.UserReviewService;
import es.udc.tfg.fios_rest.userreview.service.dto.PendingUserReviewView;
import es.udc.tfg.fios_rest.userreview.service.dto.UserRatingView;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewRequest;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewView;
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
public class UserReviewResource {

  private final UserReviewService userReviewService;

  public UserReviewResource(UserReviewService userReviewService) {
    this.userReviewService = userReviewService;
  }

  @GetMapping("/user-reviews/pending")
  public ResponseEntity<List<PendingUserReviewView>> findPendingReviews() throws NotFoundException {
    return ResponseEntity.ok(userReviewService.findPendingReviews());
  }

  @PostMapping("/reservations/{reservationId}/user-review")
  public ResponseEntity<UserReviewView> create(
    @PathVariable Long reservationId,
    @Valid @RequestBody UserReviewRequest request
  ) throws NotFoundException, OperationNotAllowed {
    UserReviewView userReview = userReviewService.create(reservationId, request);
    return ResponseEntity
      .created(URI.create("/api/reservations/" + reservationId + "/user-review"))
      .body(userReview);
  }

  @GetMapping("/user-reviews/me")
  public ResponseEntity<List<UserReviewView>> findMyReviews() throws NotFoundException {
    return ResponseEntity.ok(userReviewService.findMyReviews());
  }

  @GetMapping("/user-reviews/received")
  public ResponseEntity<List<UserReviewView>> findReceivedReviews() throws NotFoundException {
    return ResponseEntity.ok(userReviewService.findReceivedReviews());
  }

  @GetMapping("/users/{userId}/reviews")
  public ResponseEntity<List<UserReviewView>> findByReviewedUser(@PathVariable Long userId) throws NotFoundException {
    return ResponseEntity.ok(userReviewService.findByReviewedUser(userId));
  }

  @GetMapping("/users/{userId}/rating")
  public ResponseEntity<UserRatingView> getRating(@PathVariable Long userId) throws NotFoundException {
    return ResponseEntity.ok(userReviewService.getRating(userId));
  }
}
