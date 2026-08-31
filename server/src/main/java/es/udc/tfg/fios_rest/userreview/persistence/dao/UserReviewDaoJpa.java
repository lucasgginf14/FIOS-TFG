package es.udc.tfg.fios_rest.userreview.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class UserReviewDaoJpa extends GenericDaoJpa implements UserReviewDao {

  @Override
  public Collection<UserReview> findAll() {
    return entityManager.createQuery(
        """
        from UserReview ur
        order by ur.createdAt desc, ur.id desc
        """,
        UserReview.class
      )
      .getResultList();
  }

  @Override
  public Collection<UserReview> findByReviewer(Long reviewerId) {
    return entityManager.createQuery(
        """
        from UserReview ur
        where ur.reviewer.id = :reviewerId
        order by ur.createdAt desc, ur.id desc
        """,
        UserReview.class
      )
      .setParameter("reviewerId", reviewerId)
      .getResultList();
  }

  @Override
  public Collection<UserReview> findByReviewedUser(Long reviewedUserId) {
    return entityManager.createQuery(
        """
        from UserReview ur
        where ur.reviewedUser.id = :reviewedUserId
        order by ur.createdAt desc, ur.id desc
        """,
        UserReview.class
      )
      .setParameter("reviewedUserId", reviewedUserId)
      .getResultList();
  }

  @Override
  public Optional<UserReview> findById(Long id) {
    return Optional.ofNullable(entityManager.find(UserReview.class, id));
  }

  @Override
  public Optional<UserReview> findByReservationSession(Long reservationSessionId) {
    try {
      return Optional.of(entityManager.createQuery(
          """
          from UserReview ur
          where ur.reservationSession.id = :reservationSessionId
          """,
          UserReview.class
        )
        .setParameter("reservationSessionId", reservationSessionId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Collection<Long> findReviewedReservationIds(Collection<Long> reservationSessionIds) {
    if (reservationSessionIds == null || reservationSessionIds.isEmpty()) {
      return List.of();
    }

    return entityManager.createQuery(
        """
        select ur.reservationSession.id
        from UserReview ur
        where ur.reservationSession.id in :reservationSessionIds
        """,
        Long.class
      )
      .setParameter("reservationSessionIds", reservationSessionIds)
      .getResultList();
  }

  @Override
  public long countByReviewedUser(Long userId) {
    return entityManager.createQuery(
        """
        select count(ur)
        from UserReview ur
        where ur.reviewedUser.id = :userId
        """,
        Long.class
      )
      .setParameter("userId", userId)
      .getSingleResult();
  }

  @Override
  public Double findAverageOverallRatingByReviewedUser(Long userId) {
    return entityManager.createQuery(
        """
        select avg(ur.overallRating)
        from UserReview ur
        where ur.reviewedUser.id = :userId
        """,
        Double.class
      )
      .setParameter("userId", userId)
      .getSingleResult();
  }

  @Override
  public UserReview save(UserReview userReview) {
    entityManager.persist(userReview);
    return userReview;
  }

  @Override
  public void delete(UserReview userReview) {
    entityManager.remove(entityManager.contains(userReview) ? userReview : entityManager.merge(userReview));
  }
}
