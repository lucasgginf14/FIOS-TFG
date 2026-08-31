package es.udc.tfg.fios_rest.reservationsession.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public class ReservationSessionDaoJpa extends GenericDaoJpa implements ReservationSessionDao {

  @Override
  public Collection<ReservationSession> findAll() {
    return entityManager
      .createQuery(
        "from ReservationSession rs order by rs.sessionDate desc, rs.startTime",
        ReservationSession.class
      )
      .getResultList();
  }

  @Override
  public Collection<ReservationSession> findByUser(Long userId) {
    return entityManager
      .createQuery(
        """
        from ReservationSession rs
        where rs.user.id = :userId
        order by rs.sessionDate desc, rs.startTime
        """,
        ReservationSession.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Collection<ReservationSession> findByManager(Long managerId) {
    return entityManager
      .createQuery(
        """
        from ReservationSession rs
        where rs.musicalSpace.manager.id = :managerId
        order by rs.sessionDate desc, rs.startTime
        """,
        ReservationSession.class
      )
      .setParameter("managerId", managerId)
      .getResultList();
  }

  @Override
  public Collection<ReservationSession> findByMusicalSpaceAndDate(Long musicalSpaceId, LocalDate sessionDate) {
    return entityManager
      .createQuery(
        """
        from ReservationSession rs
        where rs.musicalSpace.id = :musicalSpaceId
          and rs.sessionDate = :sessionDate
        order by rs.startTime
        """,
        ReservationSession.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .setParameter("sessionDate", sessionDate)
      .getResultList();
  }

  @Override
  public Optional<ReservationSession> findById(Long id) {
    return Optional.ofNullable(entityManager.find(ReservationSession.class, id));
  }

  @Override
  public ReservationSession save(ReservationSession reservationSession) {
    entityManager.persist(reservationSession);
    return reservationSession;
  }

  @Override
  public ReservationSession update(ReservationSession reservationSession) {
    return entityManager.merge(reservationSession);
  }
}
