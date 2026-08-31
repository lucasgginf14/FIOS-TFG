package es.udc.tfg.fios_rest.spacereview.persistence.dao;

import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SpaceReviewDao {

  Collection<SpaceReview> findAll();

  Collection<SpaceReview> findByUser(Long userId);

  Collection<SpaceReview> findByMusicalSpace(Long spaceId);

  Optional<SpaceReview> findById(Long id);

  Optional<SpaceReview> findByReservationSession(Long reservationSessionId);

  Collection<Long> findReviewedReservationIds(Collection<Long> reservationSessionIds);

  long countByMusicalSpace(Long spaceId);

  Double findAverageOverallRatingByMusicalSpace(Long spaceId);

  Map<Long, SpaceReviewRatingStats> findRatingStatsByMusicalSpaceIds(Collection<Long> spaceIds);

  SpaceReview save(SpaceReview spaceReview);

  void delete(SpaceReview spaceReview);
}
