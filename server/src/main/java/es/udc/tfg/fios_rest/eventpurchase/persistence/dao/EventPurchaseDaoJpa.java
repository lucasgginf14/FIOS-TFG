package es.udc.tfg.fios_rest.eventpurchase.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchase;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchaseState;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class EventPurchaseDaoJpa extends GenericDaoJpa implements EventPurchaseDao {

  @Override
  public Collection<EventPurchase> findByUser(Long userId) {
    return entityManager.createQuery(
        """
        from EventPurchase ep
        where ep.user.id = :userId
        order by ep.purchasedAt desc, ep.id desc
        """,
        EventPurchase.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Collection<EventPurchase> findActiveByUser(Long userId) {
    return entityManager.createQuery(
        """
        from EventPurchase ep
        where ep.user.id = :userId
          and ep.state = :state
        order by ep.purchasedAt desc, ep.id desc
        """,
        EventPurchase.class
      )
      .setParameter("userId", userId)
      .setParameter("state", EventPurchaseState.RESERVED)
      .getResultList();
  }

  @Override
  public Collection<EventPurchase> findActiveByEvent(Long eventId) {
    return entityManager.createQuery(
        """
        from EventPurchase ep
        where ep.event.id = :eventId
          and ep.state = :state
        order by ep.purchasedAt asc, ep.id asc
        """,
        EventPurchase.class
      )
      .setParameter("eventId", eventId)
      .setParameter("state", EventPurchaseState.RESERVED)
      .getResultList();
  }

  @Override
  public Optional<EventPurchase> findById(Long id) {
    return Optional.ofNullable(entityManager.find(EventPurchase.class, id));
  }

  @Override
  public Optional<EventPurchase> findActiveByUserAndEvent(Long userId, Long eventId) {
    try {
      return Optional.of(entityManager.createQuery(
          """
          from EventPurchase ep
          where ep.user.id = :userId
            and ep.event.id = :eventId
            and ep.state = :state
          """,
          EventPurchase.class
        )
        .setParameter("userId", userId)
        .setParameter("eventId", eventId)
        .setParameter("state", EventPurchaseState.RESERVED)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public long countActiveByEvent(Long eventId) {
    return entityManager.createQuery(
        """
        select count(ep)
        from EventPurchase ep
        where ep.event.id = :eventId
          and ep.state = :state
        """,
        Long.class
      )
      .setParameter("eventId", eventId)
      .setParameter("state", EventPurchaseState.RESERVED)
      .getSingleResult();
  }

  @Override
  public EventPurchase save(EventPurchase eventPurchase) {
    entityManager.persist(eventPurchase);
    return eventPurchase;
  }

  @Override
  public EventPurchase update(EventPurchase eventPurchase) {
    return entityManager.merge(eventPurchase);
  }
}
