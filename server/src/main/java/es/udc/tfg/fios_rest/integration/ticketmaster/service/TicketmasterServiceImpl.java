package es.udc.tfg.fios_rest.integration.ticketmaster.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import es.udc.tfg.fios_rest.integration.ticketmaster.client.TicketmasterClient;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventBulkImportView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventImportView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventItem;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventSearchRequest;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterProvider;
import es.udc.tfg.fios_rest.integration.ticketmaster.mapper.TicketmasterEventMapper;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(rollbackFor = Exception.class)
public class TicketmasterServiceImpl implements TicketmasterService {

  private final TicketmasterClient ticketmasterClient;
  private final TicketmasterEventMapper ticketmasterEventMapper;
  private final EventDao eventDao;
  private final UserService userService;
  private final UserDao userDao;

  public TicketmasterServiceImpl(
    TicketmasterClient ticketmasterClient,
    TicketmasterEventMapper ticketmasterEventMapper,
    EventDao eventDao,
    UserService userService,
    UserDao userDao
  ) {
    this.ticketmasterClient = ticketmasterClient;
    this.ticketmasterEventMapper = ticketmasterEventMapper;
    this.eventDao = eventDao;
    this.userService = userService;
    this.userDao = userDao;
  }

  @Override
  @Transactional(readOnly = true)
  public List<TicketmasterEventView> searchEvents(TicketmasterEventSearchRequest request) throws TicketmasterException {
    TicketmasterEventSearchRequest normalizedRequest = normalizeAndValidate(request);
    return ticketmasterClient.searchEvents(normalizedRequest).stream()
      .map(ticketmasterEventMapper::toView)
      .filter(event -> event.eventDate() != null && !event.eventDate().isBefore(normalizedRequest.startDate()))
      .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TicketmasterEventView findEventByExternalId(String externalId) throws TicketmasterException {
    validateExternalId(externalId);
    return ticketmasterEventMapper.toView(ticketmasterClient.findEventByExternalId(externalId.trim()));
  }

  @Override
  public TicketmasterEventImportView importEvent(String externalId) throws TicketmasterException {
    validateExternalId(externalId);
    String normalizedExternalId = externalId.trim();

    Event existingEvent = findImportedEvent(normalizedExternalId);
    if (existingEvent != null) {
      return importedView(true, normalizedExternalId, existingEvent);
    }

    TicketmasterEventItem externalEvent = ticketmasterClient.findEventByExternalId(normalizedExternalId);
    User currentUser = findCurrentUser();
    return importEventItem(normalizedExternalId, externalEvent, currentUser);
  }

  @Override
  public TicketmasterEventBulkImportView importEvents(TicketmasterEventSearchRequest request) throws TicketmasterException {
    TicketmasterEventSearchRequest normalizedRequest = normalizeAndValidate(request);
    User currentUser = findCurrentUser();
    List<TicketmasterEventImportView> importedEvents = new ArrayList<>();
    Set<String> seenExternalIds = new LinkedHashSet<>();

    for (TicketmasterEventItem externalEvent : ticketmasterClient.searchEvents(normalizedRequest)) {
      String externalId = normalizeNullable(externalEvent.id());
      if (externalId == null || !seenExternalIds.add(externalId)) {
        continue;
      }

      TicketmasterEventView eventView = ticketmasterEventMapper.toView(externalEvent);
      if (eventView.eventDate() == null || eventView.eventDate().isBefore(normalizedRequest.startDate())) {
        continue;
      }

      importedEvents.add(importEventItem(externalId, externalEvent, currentUser));
    }

    int alreadyImportedCount = (int) importedEvents.stream()
      .filter(TicketmasterEventImportView::alreadyImported)
      .count();

    return new TicketmasterEventBulkImportView(
      importedEvents.size() - alreadyImportedCount,
      alreadyImportedCount,
      importedEvents
    );
  }

  private TicketmasterEventImportView importEventItem(
    String normalizedExternalId,
    TicketmasterEventItem externalEvent,
    User currentUser
  ) throws TicketmasterException {
    Event existingEvent = findImportedEvent(normalizedExternalId);
    if (existingEvent != null) {
      return importedView(true, normalizedExternalId, existingEvent);
    }

    Event event = ticketmasterEventMapper.toImportedEvent(externalEvent, currentUser);
    validateImportMinimumDate(event);
    eventDao.save(event);

    return importedView(false, normalizedExternalId, event);
  }

  private Event findImportedEvent(String externalId) {
    return eventDao.findByExternalSourceAndExternalId(
      TicketmasterProvider.TICKETMASTER.name(),
      externalId
    ).orElse(null);
  }

  private TicketmasterEventImportView importedView(boolean alreadyImported, String externalId, Event event) {
    return new TicketmasterEventImportView(
      alreadyImported,
      externalId,
      TicketmasterProvider.TICKETMASTER,
      EventView.from(event)
    );
  }

  private TicketmasterEventSearchRequest normalizeAndValidate(TicketmasterEventSearchRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("The Ticketmaster search request cannot be null");
    }

    LocalDate startDate = normalizeStartDate(request.startDate());

    TicketmasterEventSearchRequest normalized = new TicketmasterEventSearchRequest(
      normalizeNullable(request.city()),
      normalizeCountryCode(request.countryCode()),
      normalizeNullable(request.keyword()),
      startDate,
      request.endDate(),
      normalizeNullable(request.musicalGenre()),
      request.size()
    );

    if (normalized.startDate() != null && normalized.endDate() != null
      && normalized.startDate().isAfter(normalized.endDate())) {
      throw new IllegalArgumentException("The startDate cannot be after endDate");
    }

    if (normalized.size() != null && (normalized.size() < 1 || normalized.size() > 50)) {
      throw new IllegalArgumentException("The size must be between 1 and 50");
    }

    return normalized;
  }

  private LocalDate normalizeStartDate(LocalDate startDate) {
    LocalDate minimumStartDate = minimumStartDate();

    if (startDate == null || startDate.isBefore(minimumStartDate)) {
      return minimumStartDate;
    }

    return startDate;
  }

  private void validateImportMinimumDate(Event event) {
    LocalDate minimumStartDate = minimumStartDate();

    if (event.getEventDate().isBefore(minimumStartDate)) {
      throw new IllegalArgumentException(
        "Ticketmaster event date must be on or after " + minimumStartDate
      );
    }
  }

  private LocalDate minimumStartDate() {
    return LocalDate.now();
  }

  private void validateExternalId(String externalId) {
    if (!StringUtils.hasText(externalId)) {
      throw new IllegalArgumentException("The Ticketmaster externalId cannot be null or empty");
    }
  }

  private User findCurrentUser() throws TicketmasterException {
    Long currentUserId;
    try {
      currentUserId = userService.getCurrentUserId();
    } catch (NotFoundException e) {
      throw TicketmasterException.invalidResponse("Current user not found for Ticketmaster import", e);
    }

    return userDao.findById(currentUserId)
      .orElseThrow(() -> TicketmasterException.invalidResponse("Current user not found for Ticketmaster import", null));
  }

  private String normalizeNullable(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  private String normalizeCountryCode(String value) {
    return StringUtils.hasText(value) ? value.trim().toUpperCase() : null;
  }
}
