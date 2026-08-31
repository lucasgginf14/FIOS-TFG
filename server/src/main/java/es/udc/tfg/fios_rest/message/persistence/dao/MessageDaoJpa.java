package es.udc.tfg.fios_rest.message.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.message.persistence.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageDaoJpa extends GenericDaoJpa implements MessageDao {

  @Override
  public Collection<Message> findByReservationSession(Long reservationSessionId) {
    return entityManager.createQuery(
        """
        from Message m
        where m.reservationSession.id = :reservationSessionId
        order by m.dateTime asc, m.id asc
        """,
        Message.class
      )
      .setParameter("reservationSessionId", reservationSessionId)
      .getResultList();
  }

  @Override
  public Collection<Message> findByReservationSessionIds(Collection<Long> reservationSessionIds) {
    if (reservationSessionIds == null || reservationSessionIds.isEmpty()) {
      return List.of();
    }

    return entityManager.createQuery(
        """
        from Message m
        where m.reservationSession.id in :reservationSessionIds
        order by m.reservationSession.id asc, m.dateTime desc, m.id desc
        """,
        Message.class
      )
      .setParameter("reservationSessionIds", reservationSessionIds)
      .getResultList();
  }

  @Override
  public Optional<Message> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Message.class, id));
  }

  @Override
  public Message save(Message message) {
    entityManager.persist(message);
    return message;
  }

  @Override
  public int markMessagesAsReadForReservationUser(Long reservationSessionId, Long reservationUserId) {
    return entityManager.createQuery(
        """
        update Message m
        set m.read = true
        where m.reservationSession.id = :reservationSessionId
          and m.read = false
          and m.user.id <> :reservationUserId
        """
      )
      .setParameter("reservationSessionId", reservationSessionId)
      .setParameter("reservationUserId", reservationUserId)
      .executeUpdate();
  }

  @Override
  public int markMessagesAsReadForManagementSide(Long reservationSessionId, Long reservationUserId) {
    return entityManager.createQuery(
        """
        update Message m
        set m.read = true
        where m.reservationSession.id = :reservationSessionId
          and m.read = false
          and m.user.id = :reservationUserId
        """
      )
      .setParameter("reservationSessionId", reservationSessionId)
      .setParameter("reservationUserId", reservationUserId)
      .executeUpdate();
  }
}
