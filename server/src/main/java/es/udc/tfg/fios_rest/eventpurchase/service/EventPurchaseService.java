package es.udc.tfg.fios_rest.eventpurchase.service;

import es.udc.tfg.fios_rest.common.exceptions.model.EventAlreadyPurchasedException;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.eventpurchase.persistence.dao.EventPurchaseDao;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchase;
import es.udc.tfg.fios_rest.eventpurchase.service.dto.EventPurchaseStatusView;
import es.udc.tfg.fios_rest.eventpurchase.service.dto.EventPurchaseView;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class EventPurchaseService {

  @Autowired
  private EventPurchaseDao eventPurchaseDao;

  @Autowired
  private EventDao eventDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<EventPurchaseView> findMyReservations() throws NotFoundException {
    User currentUser = findCurrentUser();

    return eventPurchaseDao.findActiveByUser(currentUser.getId()).stream()
      .filter(purchase -> EventSource.INTERNAL.equals(purchase.getEvent().getSource()))
      .map(EventPurchaseView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<EventPurchaseView> findMyPurchases() throws NotFoundException {
    return findMyReservations();
  }

  @Transactional(readOnly = true)
  public EventPurchaseStatusView findEventReservationStatus(Long eventId) throws NotFoundException {
    Optional<User> currentUser = findOptionalCurrentUser();
    Event event = findVisibleEvent(eventId);
    return buildStatus(currentUser.orElse(null), event);
  }

  @Transactional(readOnly = true)
  public EventPurchaseStatusView findMyEventStatus(Long eventId) throws NotFoundException {
    User currentUser = findCurrentUser();
    Event event = findVisibleEvent(eventId);
    return buildStatus(currentUser, event);
  }

  @Transactional(readOnly = true)
  public List<EventPurchaseView> findActiveReservationsForEvent(Long eventId)
    throws NotFoundException, OperationNotAllowed {
    Event event = eventDao.findById(eventId)
      .orElseThrow(() -> new NotFoundException(eventId.toString(), Event.class));
    User currentUser = findCurrentUser();
    validateCanViewEventReservations(event, currentUser);

    return eventPurchaseDao.findActiveByEvent(event.getId()).stream()
      .map(EventPurchaseView::from)
      .toList();
  }

  public EventPurchaseView reserve(Long eventId)
    throws NotFoundException, OperationNotAllowed, EventAlreadyPurchasedException {
    User currentUser = findCurrentUser();
    Event event = findEventForUpdate(eventId);
    validatePublicUserFlow(currentUser);
    validateCanReserve(event);

    EventPurchase existingReservation = eventPurchaseDao
      .findActiveByUserAndEvent(currentUser.getId(), event.getId())
      .orElse(null);

    if (existingReservation != null) {
      throw new EventAlreadyPurchasedException();
    }

    if (isSoldOut(event)) {
      throw new IllegalArgumentException("The event has no tickets available");
    }

    EventPurchase eventPurchase = new EventPurchase(currentUser, event, event.getTicketPrice());
    eventPurchaseDao.save(eventPurchase);

    return EventPurchaseView.from(eventPurchase);
  }

  public EventPurchaseView purchase(Long eventId)
    throws NotFoundException, OperationNotAllowed, EventAlreadyPurchasedException {
    return reserve(eventId);
  }

  public EventPurchaseView cancel(Long purchaseId) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    EventPurchase purchase = eventPurchaseDao.findById(purchaseId)
      .orElseThrow(() -> new NotFoundException(purchaseId.toString(), EventPurchase.class));
    Event event = findEventForUpdate(purchase.getEvent().getId());

    validateCanCancel(purchase, event, currentUser);
    purchase.cancel();

    return EventPurchaseView.from(eventPurchaseDao.update(purchase));
  }

  private EventPurchaseStatusView buildStatus(User user, Event event) {
    EventPurchase reservation = user == null
      ? null
      : eventPurchaseDao.findActiveByUserAndEvent(user.getId(), event.getId()).orElse(null);
    Long reservedTickets = EventSource.INTERNAL.equals(event.getSource())
      ? eventPurchaseDao.countActiveByEvent(event.getId())
      : null;
    Integer availableTickets = calculateAvailableTickets(event, reservedTickets);
    boolean soldOut = availableTickets != null && availableTickets <= 0;
    EventPurchaseView reservationView = reservation == null ? null : EventPurchaseView.from(reservation);

    return new EventPurchaseStatusView(
      reservation != null,
      reservationView,
      reservation != null,
      reservationView,
      EventSource.INTERNAL.equals(event.getSource()) ? event.getCapacity() : null,
      reservedTickets == null ? 0L : reservedTickets,
      reservedTickets == null ? 0L : reservedTickets,
      availableTickets,
      availableTickets,
      soldOut
    );
  }

  private Event findVisibleEvent(Long eventId) throws NotFoundException {
    Event event = eventDao.findById(eventId)
      .orElseThrow(() -> new NotFoundException(eventId.toString(), Event.class));

    if (!event.isPubliclyVisible()) {
      throw new NotFoundException(eventId.toString(), Event.class);
    }

    return event;
  }

  private Event findEventForUpdate(Long eventId) throws NotFoundException {
    return eventDao.findByIdForUpdate(eventId)
      .orElseThrow(() -> new NotFoundException(eventId.toString(), Event.class));
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

  private void validateCanReserve(Event event) throws OperationNotAllowed {
    if (!EventSource.INTERNAL.equals(event.getSource())) {
      throw new OperationNotAllowed("External events cannot accept internal reservations");
    }

    if (!EventStatus.PUBLISHED.equals(event.getStatus())) {
      throw new OperationNotAllowed("Only published events can accept ticket reservations");
    }

    if (event.getCapacity() == null || event.getCapacity() < 1) {
      throw new OperationNotAllowed("Internal events must include capacity");
    }

    if (hasAlreadyStarted(event)) {
      throw new OperationNotAllowed("Events that have already started cannot accept ticket reservations");
    }
  }

  private void validateCanCancel(EventPurchase purchase, Event event, User currentUser) throws OperationNotAllowed {
    if (!purchase.getUser().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("Only the ticket reservation owner can cancel it");
    }

    if (!purchase.isActive()) {
      throw new OperationNotAllowed("The ticket reservation is already cancelled");
    }

    if (!EventSource.INTERNAL.equals(event.getSource())) {
      throw new OperationNotAllowed("External events do not have internal ticket reservations");
    }

    if (hasAlreadyStarted(event)) {
      throw new OperationNotAllowed("Events that have already started cannot cancel ticket reservations");
    }
  }

  private void validateCanViewEventReservations(Event event, User currentUser) throws OperationNotAllowed {
    boolean isCreator = event.getCreatedBy() != null && event.getCreatedBy().getId().equals(currentUser.getId());

    if (!currentUser.isAdmin() && !isCreator) {
      throw new OperationNotAllowed("Only the event creator or administrators can view ticket reservations");
    }
  }

  private void validatePublicUserFlow(User user) throws OperationNotAllowed {
    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot reserve events from the public user flow");
    }
  }

  private boolean hasAlreadyStarted(Event event) {
    LocalDate today = LocalDate.now();

    if (event.getEventDate().isBefore(today)) {
      return true;
    }

    if (event.getEventDate().isAfter(today)) {
      return false;
    }

    LocalTime startTime = event.getStartTime();
    return startTime == null || !startTime.isAfter(LocalTime.now());
  }

  private boolean isSoldOut(Event event) {
    Integer availableTickets = calculateAvailableTickets(event, eventPurchaseDao.countActiveByEvent(event.getId()));
    return availableTickets != null && availableTickets <= 0;
  }

  private Integer calculateAvailableTickets(Event event, Long reservedTickets) {
    if (!EventSource.INTERNAL.equals(event.getSource()) || event.getCapacity() == null || reservedTickets == null) {
      return null;
    }

    long availableTickets = event.getCapacity() - reservedTickets;
    return (int) Math.max(availableTickets, 0);
  }
}
