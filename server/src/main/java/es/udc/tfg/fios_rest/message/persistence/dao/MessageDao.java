package es.udc.tfg.fios_rest.message.persistence.dao;

import es.udc.tfg.fios_rest.message.persistence.entity.Message;

import java.util.Collection;
import java.util.Optional;

public interface MessageDao {

  Collection<Message> findByReservationSession(Long reservationSessionId);

  Collection<Message> findByReservationSessionIds(Collection<Long> reservationSessionIds);

  Optional<Message> findById(Long id);

  Message save(Message message);

  int markMessagesAsReadForReservationUser(Long reservationSessionId, Long reservationUserId);

  int markMessagesAsReadForManagementSide(Long reservationSessionId, Long reservationUserId);
}
