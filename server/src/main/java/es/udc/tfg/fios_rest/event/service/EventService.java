package es.udc.tfg.fios_rest.event.service;

import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.event.service.dto.EventMapView;
import es.udc.tfg.fios_rest.event.service.dto.EventRef;
import es.udc.tfg.fios_rest.event.service.dto.EventRequest;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import es.udc.tfg.fios_rest.eventpurchase.persistence.dao.EventPurchaseDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Locale;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class EventService {

  @Autowired
  private EventDao eventDao;

  @Autowired
  private EventPurchaseDao eventPurchaseDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private BandDao bandDao;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<EventRef> findPublished(EventFilterParams filters) {
    EventFilterParams normalizedFilters = normalizePublicFilters(filters);
    return eventDao.findPublished(withoutTextFilters(normalizedFilters)).stream()
      .filter(event -> matchesTextFilter(event.getCity(), normalizedFilters.city()))
      .filter(event -> matchesTextFilter(event.getProvince(), normalizedFilters.province()))
      .filter(event -> matchesTextFilter(event.getMusicalGenre(), normalizedFilters.musicalGenre()))
      .map(EventRef::from)
      .toList();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = true)
  public List<EventRef> findAll(EventFilterParams filters) {
    EventFilterParams normalizedFilters = normalizeAdminFilters(filters);
    return eventDao.findAll(withoutTextFilters(normalizedFilters)).stream()
      .filter(event -> matchesTextFilter(event.getCity(), normalizedFilters.city()))
      .filter(event -> matchesTextFilter(event.getProvince(), normalizedFilters.province()))
      .filter(event -> matchesTextFilter(event.getMusicalGenre(), normalizedFilters.musicalGenre()))
      .map(EventRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public EventView findById(Long eventId) throws NotFoundException {
    Event event = findEvent(eventId);

    if (!event.isPubliclyVisible() && !findOptionalCurrentUser().map(User::isAdmin).orElse(false)) {
      throw new NotFoundException(eventId.toString(), Event.class);
    }

    return EventView.from(event, countInternalActiveReservations(event));
  }

  @Transactional(readOnly = true)
  public List<EventRef> findUpcomingPublished() {
    return eventDao.findUpcomingPublished().stream()
      .map(EventRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<EventMapView> findPublishedForMap(EventFilterParams filters) {
    EventFilterParams normalizedFilters = normalizePublicFilters(filters);
    return eventDao.findPublishedForMap(withoutTextFilters(normalizedFilters)).stream()
      .filter(event -> matchesTextFilter(event.getCity(), normalizedFilters.city()))
      .filter(event -> matchesTextFilter(event.getProvince(), normalizedFilters.province()))
      .filter(event -> matchesTextFilter(event.getMusicalGenre(), normalizedFilters.musicalGenre()))
      .map(EventMapView::from)
      .toList();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public EventView create(EventRequest request) throws NotFoundException {
    validateRequest(request);
    validateNewEventDate(request.eventDate());

    Event event = new Event(
      request.title(),
      request.description(),
      request.eventDate(),
      request.startTime(),
      request.endTime(),
      request.musicalGenre(),
      request.capacity(),
      request.ticketPrice(),
      request.posterImage(),
      request.status(),
      request.eventType(),
      request.source(),
      request.venueName(),
      request.latitude(),
      request.longitude(),
      request.city(),
      request.province(),
      request.country(),
      request.location(),
      request.externalSource(),
      request.externalId(),
      request.externalUrl(),
      findMusicalSpace(request.musicalSpaceId()),
      findBand(request.bandId()),
      findCurrentUser()
    );

    applySourceSpecificData(event, request);
    eventDao.save(event);

    return EventView.from(event, countInternalActiveReservations(event));
  }

  public EventView createForBand(Long bandId, EventRequest request)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    User currentUser = findCurrentUser();
    validateCanManage(band, currentUser);

    validateRequest(request);
    validateBandEventSubmission(request);
    validateNewEventDate(request.eventDate());

    Event event = new Event(
      request.title(),
      request.description(),
      request.eventDate(),
      request.startTime(),
      request.endTime(),
      request.musicalGenre(),
      request.capacity(),
      request.ticketPrice(),
      request.posterImage(),
      EventStatus.DRAFT,
      request.eventType(),
      EventSource.INTERNAL,
      request.venueName(),
      request.latitude(),
      request.longitude(),
      request.city(),
      request.province(),
      request.country(),
      request.location(),
      null,
      null,
      null,
      findPublicMusicalSpace(request.musicalSpaceId()),
      band,
      currentUser
    );

    event.clearImportData();
    eventDao.save(event);

    return EventView.from(event, countInternalActiveReservations(event));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public EventView update(Long eventId, EventRequest request) throws NotFoundException, OperationNotAllowed {
    validateRequest(request);

    Event event = findEvent(eventId);
    long activeReservations = eventPurchaseDao.countActiveByEvent(event.getId());
    validateUpdateAgainstActiveReservations(request, activeReservations);
    event.setTitle(request.title());
    event.setDescription(request.description());
    event.setEventDate(request.eventDate());
    event.setStartTime(request.startTime());
    event.setEndTime(request.endTime());
    event.setMusicalGenre(request.musicalGenre());
    event.setCapacity(request.capacity());
    event.setTicketPrice(request.ticketPrice());
    event.setPosterImage(request.posterImage());
    event.setStatus(request.status());
    event.setEventType(request.eventType());
    event.setSource(request.source());
    event.setVenueName(request.venueName());
    event.setLatitude(request.latitude());
    event.setLongitude(request.longitude());
    event.setCity(request.city());
    event.setProvince(request.province());
    event.setCountry(request.country());
    event.setLocation(request.location());
    event.setExternalSource(request.externalSource());
    event.setExternalId(request.externalId());
    event.setExternalUrl(request.externalUrl());
    event.setMusicalSpace(findMusicalSpace(request.musicalSpaceId()));
    event.setBand(findBand(request.bandId()));
    applySourceSpecificData(event, request);

    return EventView.from(eventDao.update(event), countInternalActiveReservations(event));
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public void delete(Long eventId) throws NotFoundException {
    Event event = findEvent(eventId);
    if (!EventStatus.ARCHIVED.equals(event.getStatus())) {
      event.archive();
      eventDao.update(event);
    }
  }

  private Event findEvent(Long eventId) throws NotFoundException {
    return eventDao.findById(eventId)
      .orElseThrow(() -> new NotFoundException(eventId.toString(), Event.class));
  }

  private MusicalSpace findMusicalSpace(Long musicalSpaceId) throws NotFoundException {
    if (musicalSpaceId == null) {
      return null;
    }

    return musicalSpaceDao.findById(musicalSpaceId)
      .orElseThrow(() -> new NotFoundException(musicalSpaceId.toString(), MusicalSpace.class));
  }

  private MusicalSpace findPublicMusicalSpace(Long musicalSpaceId) throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(musicalSpaceId);

    if (musicalSpace == null || musicalSpace.isPubliclyVisible()) {
      return musicalSpace;
    }

    throw new NotFoundException(musicalSpaceId.toString(), MusicalSpace.class);
  }

  private Band findBand(Long bandId) throws NotFoundException {
    if (bandId == null) {
      return null;
    }

    Band band = bandDao.findById(bandId)
      .orElseThrow(() -> new NotFoundException(bandId.toString(), Band.class));

    if (!band.isActive()) {
      throw new IllegalArgumentException("The band must be active");
    }

    return band;
  }

  private Band findActiveBand(Long bandId) throws NotFoundException {
    Band band = bandDao.findById(bandId)
      .orElseThrow(() -> new NotFoundException(bandId.toString(), Band.class));

    if (!band.isActive()) {
      throw new NotFoundException(bandId.toString(), Band.class);
    }

    return band;
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private Optional<User> findOptionalCurrentUser() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();

    if (currentUserLogin == null) {
      return Optional.empty();
    }

    return userDao.findByEmail(currentUserLogin.toLowerCase());
  }

  private EventFilterParams normalizePublicFilters(EventFilterParams filters) {
    if (filters == null) {
      return new EventFilterParams(null, null, null, null, null, null, null);
    }

    return new EventFilterParams(
      filters.date(),
      filters.dateFrom(),
      filters.dateTo(),
      normalizeFilterValue(filters.city()),
      normalizeFilterValue(filters.province()),
      normalizeFilterValue(filters.musicalGenre()),
      filters.source(),
      filters.eventType(),
      null,
      filters.bandId()
    );
  }

  private EventFilterParams normalizeAdminFilters(EventFilterParams filters) {
    if (filters == null) {
      return new EventFilterParams(null, null, null, null, null, null, null);
    }

    return new EventFilterParams(
      filters.date(),
      filters.dateFrom(),
      filters.dateTo(),
      normalizeFilterValue(filters.city()),
      normalizeFilterValue(filters.province()),
      normalizeFilterValue(filters.musicalGenre()),
      filters.source(),
      filters.eventType(),
      filters.status(),
      filters.bandId()
    );
  }

  private String normalizeFilterValue(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private EventFilterParams withoutTextFilters(EventFilterParams filters) {
    return new EventFilterParams(
      filters.date(),
      filters.dateFrom(),
      filters.dateTo(),
      null,
      null,
      null,
      filters.source(),
      filters.eventType(),
      filters.status(),
      filters.bandId()
    );
  }

  private boolean matchesTextFilter(String source, String requested) {
    String normalizedRequested = normalizeForMatching(requested);
    return normalizedRequested.isEmpty() || normalizeForMatching(source).contains(normalizedRequested);
  }

  private String normalizeForMatching(String value) {
    if (value == null) {
      return "";
    }

    String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);

    return normalized.replaceAll("\\s+", " ").trim();
  }

  private void validateRequest(EventRequest request) {
    if (request.endTime() != null && request.startTime() == null) {
      throw new IllegalArgumentException("The start time is obligatory when the end time is informed");
    }

    if (request.startTime() != null && request.endTime() != null && !request.startTime().isBefore(request.endTime())) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }

    boolean hasLatitude = request.latitude() != null;
    boolean hasLongitude = request.longitude() != null;
    if (hasLatitude != hasLongitude) {
      throw new IllegalArgumentException("Latitude and longitude must be informed together");
    }

    validateExternalUrl(request.externalUrl());

    if (EventSource.INTERNAL.equals(request.source()) && request.capacity() == null) {
      throw new IllegalArgumentException("Internal events must include capacity");
    }

    if (EventSource.EXTERNAL.equals(request.source())
      && isBlank(request.externalSource())
      && isBlank(request.externalId())
      && isBlank(request.externalUrl())) {
      throw new IllegalArgumentException("External events must include externalSource, externalId or externalUrl");
    }
  }

  private void validateBandEventSubmission(EventRequest request) {
    if (!EventSource.INTERNAL.equals(request.source())) {
      throw new IllegalArgumentException("Band events must be internal");
    }
  }

  private void validateUpdateAgainstActiveReservations(EventRequest request, long activeReservations)
    throws OperationNotAllowed {
    if (activeReservations <= 0) {
      return;
    }

    if (EventSource.EXTERNAL.equals(request.source())) {
      throw new OperationNotAllowed("Events with active ticket reservations cannot become external");
    }

    if (request.capacity() == null || request.capacity() < activeReservations) {
      throw new OperationNotAllowed("The event capacity cannot be lower than active ticket reservations");
    }
  }

  private Long countInternalActiveReservations(Event event) {
    if (!EventSource.INTERNAL.equals(event.getSource())) {
      return null;
    }

    return eventPurchaseDao.countActiveByEvent(event.getId());
  }

  private void validateNewEventDate(LocalDate eventDate) {
    if (eventDate != null && eventDate.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("The event date must be today or later");
    }
  }

  private void applySourceSpecificData(Event event, EventRequest request) {
    if (EventSource.EXTERNAL.equals(request.source())) {
      event.markImportedNow();
      return;
    }

    event.clearImportData();
  }

  private void validateExternalUrl(String externalUrl) {
    if (isBlank(externalUrl)) {
      return;
    }

    try {
      URI uri = new URI(externalUrl.trim());
      String scheme = uri.getScheme();

      if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
        throw new IllegalArgumentException("The external URL must start with http or https");
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("The external URL format is not valid");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void validateCanManage(Band band, User currentUser) throws OperationNotAllowed {
    if (currentUser.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot submit band events from the public user flow");
    }

    boolean canManage = bandMemberDao.findActiveByBandAndUser(band.getId(), currentUser.getId())
      .map(BandMember::isLeader)
      .orElse(false);

    if (!canManage) {
      throw new OperationNotAllowed("The user cannot submit events for this band");
    }
  }
}
