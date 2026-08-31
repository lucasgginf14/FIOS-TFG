package es.udc.tfg.fios_rest.userreview.persistence.dao;

import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;

import java.util.Collection;
import java.util.Optional;

public interface UserReviewDao {

  Collection<UserReview> findAll();

  Collection<UserReview> findByReviewer(Long reviewerId);

  Collection<UserReview> findByReviewedUser(Long reviewedUserId);

  Optional<UserReview> findById(Long id);

  Optional<UserReview> findByReservationSession(Long reservationSessionId);

  Collection<Long> findReviewedReservationIds(Collection<Long> reservationSessionIds);

  long countByReviewedUser(Long userId);

  Double findAverageOverallRatingByReviewedUser(Long userId);

  UserReview save(UserReview userReview);

  void delete(UserReview userReview);
}
