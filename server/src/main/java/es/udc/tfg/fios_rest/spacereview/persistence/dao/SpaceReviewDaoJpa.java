package es.udc.tfg.fios_rest.spacereview.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SpaceReviewDaoJpa extends GenericDaoJpa implements SpaceReviewDao {

  @Override
  public Collection<SpaceReview> findAll() {
    return entityManager.createQuery(
        """
        from SpaceReview sr
        order by sr.createdAt desc, sr.id desc
        """,
        SpaceReview.class
      )
      .getResultList();
  }

  @Override
  public Collection<SpaceReview> findByUser(Long userId) {
    return entityManager.createQuery(
        """
        from SpaceReview sr
        where sr.user.id = :userId
        order by sr.createdAt desc, sr.id desc
        """,
        SpaceReview.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Collection<SpaceReview> findByMusicalSpace(Long spaceId) {
    return entityManager.createQuery(
        """
        from SpaceReview sr
        where sr.musicalSpace.id = :spaceId
        order by sr.createdAt desc, sr.id desc
        """,
        SpaceReview.class
      )
      .setParameter("spaceId", spaceId)
      .getResultList();
  }

  @Override
  public Optional<SpaceReview> findByReservationSession(Long reservationSessionId) {
    try {
      return Optional.of(entityManager.createQuery(
          """
          from SpaceReview sr
          where sr.reservationSession.id = :reservationSessionId
          """,
          SpaceReview.class
        )
        .setParameter("reservationSessionId", reservationSessionId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SpaceReview> findById(Long id) {
    return Optional.ofNullable(entityManager.find(SpaceReview.class, id));
  }

  @Override
  public Collection<Long> findReviewedReservationIds(Collection<Long> reservationSessionIds) {
    if (reservationSessionIds == null || reservationSessionIds.isEmpty()) {
      return List.of();
    }

    return entityManager.createQuery(
        """
        select sr.reservationSession.id
        from SpaceReview sr
        where sr.reservationSession.id in :reservationSessionIds
        """,
        Long.class
      )
      .setParameter("reservationSessionIds", reservationSessionIds)
      .getResultList();
  }

  @Override
  public long countByMusicalSpace(Long spaceId) {
    return entityManager.createQuery(
        """
        select count(sr)
        from SpaceReview sr
        where sr.musicalSpace.id = :spaceId
        """,
        Long.class
      )
      .setParameter("spaceId", spaceId)
      .getSingleResult();
  }

  @Override
  public Double findAverageOverallRatingByMusicalSpace(Long spaceId) {
    return entityManager.createQuery(
        """
        select avg(sr.overallRating)
        from SpaceReview sr
        where sr.musicalSpace.id = :spaceId
        """,
        Double.class
      )
      .setParameter("spaceId", spaceId)
      .getSingleResult();
  }

  @Override
  public Map<Long, SpaceReviewRatingStats> findRatingStatsByMusicalSpaceIds(Collection<Long> spaceIds) {
    if (spaceIds == null || spaceIds.isEmpty()) {
      return Map.of();
    }

    List<Object[]> rows = entityManager.createQuery(
        """
        select sr.musicalSpace.id, count(sr), avg(sr.overallRating)
        from SpaceReview sr
        where sr.musicalSpace.id in :spaceIds
        group by sr.musicalSpace.id
        """,
        Object[].class
      )
      .setParameter("spaceIds", spaceIds)
      .getResultList();

    Map<Long, SpaceReviewRatingStats> statsBySpaceId = new HashMap<>();

    for (Object[] row : rows) {
      Long spaceId = (Long) row[0];
      long reviewsCount = ((Number) row[1]).longValue();
      BigDecimal averageOverallRating = row[2] == null
        ? BigDecimal.ZERO
        : BigDecimal.valueOf(((Number) row[2]).doubleValue()).setScale(2, RoundingMode.HALF_UP);

      statsBySpaceId.put(spaceId, new SpaceReviewRatingStats(spaceId, reviewsCount, averageOverallRating));
    }

    return statsBySpaceId;
  }

  @Override
  public SpaceReview save(SpaceReview spaceReview) {
    entityManager.persist(spaceReview);
    return spaceReview;
  }

  @Override
  public void delete(SpaceReview spaceReview) {
    entityManager.remove(entityManager.contains(spaceReview) ? spaceReview : entityManager.merge(spaceReview));
  }
}
