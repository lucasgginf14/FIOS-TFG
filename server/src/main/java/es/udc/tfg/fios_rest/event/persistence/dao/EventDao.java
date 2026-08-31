package es.udc.tfg.fios_rest.event.persistence.dao;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface EventDao {

  Collection<Event> findPublished(EventFilterParams filters);

  Collection<Event> findAll(EventFilterParams filters);

  Collection<Event> findUpcomingPublished();

  Collection<Event> findPublishedForMap(EventFilterParams filters);

  Optional<Event> findById(Long id);

  Optional<Event> findByIdForUpdate(Long id);

  Optional<Event> findByExternalSourceAndExternalId(String externalSource, String externalId);

  Optional<Event> findByTitleAndEventDateAndCity(String title, LocalDate eventDate, String city);

  Event save(Event event);

  Event update(Event event);

  void delete(Event event);
}
