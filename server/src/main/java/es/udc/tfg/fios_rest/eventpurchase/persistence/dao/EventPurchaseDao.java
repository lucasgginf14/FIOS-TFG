package es.udc.tfg.fios_rest.eventpurchase.persistence.dao;

import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchase;

import java.util.Collection;
import java.util.Optional;

public interface EventPurchaseDao {

  Collection<EventPurchase> findByUser(Long userId);

  Collection<EventPurchase> findActiveByUser(Long userId);

  Collection<EventPurchase> findActiveByEvent(Long eventId);

  Optional<EventPurchase> findById(Long id);

  Optional<EventPurchase> findActiveByUserAndEvent(Long userId, Long eventId);

  long countActiveByEvent(Long eventId);

  EventPurchase save(EventPurchase eventPurchase);

  EventPurchase update(EventPurchase eventPurchase);
}
