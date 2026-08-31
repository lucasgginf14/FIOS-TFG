package es.udc.tfg.fios_rest.event.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDaoJpa extends GenericDaoJpa implements EventDao {

  @Override
  public Collection<Event> findPublished(EventFilterParams filters) {
    return findByCriteria(filters, true, false, false);
  }

  @Override
  public Collection<Event> findAll(EventFilterParams filters) {
    return findByCriteria(filters, false, false, false);
  }

  @Override
  public Collection<Event> findUpcomingPublished() {
    return findByCriteria(null, true, false, true);
  }

  @Override
  public Collection<Event> findPublishedForMap(EventFilterParams filters) {
    return findByCriteria(filters, true, true, false);
  }

  @Override
  public Optional<Event> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Event.class, id));
  }

  @Override
  public Optional<Event> findByIdForUpdate(Long id) {
    return Optional.ofNullable(entityManager.find(Event.class, id, LockModeType.PESSIMISTIC_WRITE));
  }

  @Override
  public Optional<Event> findByExternalSourceAndExternalId(String externalSource, String externalId) {
    List<Event> results = entityManager.createQuery(
        """
        select e
        from Event e
        where e.externalSource = :externalSource
          and e.externalId = :externalId
        """,
        Event.class
      )
      .setParameter("externalSource", externalSource)
      .setParameter("externalId", externalId)
      .setMaxResults(1)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  public Optional<Event> findByTitleAndEventDateAndCity(String title, LocalDate eventDate, String city) {
    List<Event> results = entityManager.createQuery(
        """
        select e
        from Event e
        where lower(e.title) = :title
          and e.eventDate = :eventDate
          and lower(e.city) = :city
        """,
        Event.class
      )
      .setParameter("title", title.trim().toLowerCase())
      .setParameter("eventDate", eventDate)
      .setParameter("city", city.trim().toLowerCase())
      .setMaxResults(1)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  public Event save(Event event) {
    entityManager.persist(event);
    return event;
  }

  @Override
  public Event update(Event event) {
    return entityManager.merge(event);
  }

  @Override
  public void delete(Event event) {
    entityManager.remove(entityManager.contains(event) ? event : entityManager.merge(event));
  }

  private Collection<Event> findByCriteria(
    EventFilterParams filters,
    boolean onlyPublished,
    boolean onlyWithCoordinates,
    boolean onlyUpcoming
  ) {
    StringBuilder queryBuilder = new StringBuilder("select e from Event e");
    List<String> predicates = new ArrayList<>();

    if (onlyPublished) {
      predicates.add("e.status = :publishedStatus");
    } else if (filters != null && filters.status() != null) {
      predicates.add("e.status = :status");
    }

    if (filters != null && filters.date() != null) {
      predicates.add("e.eventDate = :eventDate");
    } else {
      if (filters != null && filters.dateFrom() != null) {
        predicates.add("e.eventDate >= :dateFrom");
      }

      if (filters != null && filters.dateTo() != null) {
        predicates.add("e.eventDate <= :dateTo");
      }
    }

    if (filters != null && filters.city() != null) {
      predicates.add("lower(e.city) like :city");
    }

    if (filters != null && filters.province() != null) {
      predicates.add("lower(e.province) like :province");
    }

    if (filters != null && filters.musicalGenre() != null) {
      predicates.add("lower(e.musicalGenre) like :musicalGenre");
    }

    if (filters != null && filters.source() != null) {
      predicates.add("e.source = :source");
    }

    if (filters != null && filters.eventType() != null) {
      predicates.add("e.eventType = :eventType");
    }

    if (filters != null && filters.bandId() != null) {
      predicates.add("e.band.id = :bandId");
    }

    if (onlyWithCoordinates) {
      predicates.add("e.latitude is not null");
      predicates.add("e.longitude is not null");
    }

    if (onlyUpcoming) {
      predicates.add(
        """
        (
          e.eventDate > :today
          or (
            e.eventDate = :today
            and (
              (e.endTime is not null and e.endTime >= :nowTime)
              or (e.endTime is null and e.startTime is not null and e.startTime >= :nowTime)
              or (e.startTime is null and e.endTime is null)
            )
          )
        )
        """
      );
    }

    if (!predicates.isEmpty()) {
      queryBuilder.append(" where ").append(String.join(" and ", predicates));
    }

    queryBuilder.append(
      """
       order by e.eventDate asc,
       case when e.startTime is null then 1 else 0 end asc,
       e.startTime asc,
       e.id asc
      """
    );

    TypedQuery<Event> query = entityManager.createQuery(queryBuilder.toString(), Event.class);

    if (onlyPublished) {
      query.setParameter("publishedStatus", EventStatus.PUBLISHED);
    } else if (filters != null && filters.status() != null) {
      query.setParameter("status", filters.status());
    }

    if (filters != null && filters.date() != null) {
      query.setParameter("eventDate", filters.date());
    } else {
      if (filters != null && filters.dateFrom() != null) {
        query.setParameter("dateFrom", filters.dateFrom());
      }

      if (filters != null && filters.dateTo() != null) {
        query.setParameter("dateTo", filters.dateTo());
      }
    }

    if (filters != null && filters.city() != null) {
      query.setParameter("city", "%" + filters.city().toLowerCase() + "%");
    }

    if (filters != null && filters.province() != null) {
      query.setParameter("province", "%" + filters.province().toLowerCase() + "%");
    }

    if (filters != null && filters.musicalGenre() != null) {
      query.setParameter("musicalGenre", "%" + filters.musicalGenre().toLowerCase() + "%");
    }

    if (filters != null && filters.source() != null) {
      query.setParameter("source", filters.source());
    }

    if (filters != null && filters.eventType() != null) {
      query.setParameter("eventType", filters.eventType());
    }

    if (filters != null && filters.bandId() != null) {
      query.setParameter("bandId", filters.bandId());
    }

    if (onlyUpcoming) {
      query.setParameter("today", LocalDate.now());
      query.setParameter("nowTime", LocalTime.now());
    }

    return query.getResultList();
  }
}
