package es.udc.tfg.fios_rest.reservationsession.service;

import es.udc.tfg.fios_rest.availability.service.AvailabilityPricingService;
import es.udc.tfg.fios_rest.availability.service.CalculatedAvailabilityService;
import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationCancellationRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionView;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationStateUpdateRequest;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReservationSessionService {

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private BandDao bandDao;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private UserService userService;

  @Autowired
  private CalculatedAvailabilityService calculatedAvailabilityService;

  @Autowired
  private AvailabilityPricingService availabilityPricingService;

  @Transactional(readOnly = true)
  public List<ReservationSessionView> findMyReservations() throws NotFoundException {
    User currentUser = findCurrentUser();

    return reservationSessionDao.findByUser(currentUser.getId()).stream()
      .map(ReservationSessionView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<ReservationSessionView> findReservationsForMySpaces() throws NotFoundException {
    User currentUser = findCurrentUser();

    return (currentUser.isAdmin()
      ? reservationSessionDao.findAll()
      : reservationSessionDao.findByManager(currentUser.getId())).stream()
      .map(ReservationSessionView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public ReservationSessionView findById(Long id) throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(id);
    validateCanView(reservationSession);
    return ReservationSessionView.from(reservationSession);
  }

  public ReservationSessionView create(ReservationSessionRequest request)
    throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    MusicalSpace musicalSpace = findReservableSpace(request.musicalSpaceId());
    Band band = findAndValidateBand(request.bandId(), currentUser);
    validateReservationRequest(request, musicalSpace);
    validateNoOverlappingReservation(request.musicalSpaceId(), null, request.sessionDate(), request.startTime(), request.endTime());
    validateFitsCalculatedAvailability(request.musicalSpaceId(), null, request.sessionDate(), request.startTime(), request.endTime());

    BigDecimal finalPrice = availabilityPricingService.calculatePrice(
      request.musicalSpaceId(),
      request.sessionDate(),
      request.startTime(),
      request.endTime()
    );

    ReservationSession reservationSession = new ReservationSession(
      request.sessionDate(),
      request.startTime(),
      request.endTime(),
      request.attendeesCount(),
      request.sessionType(),
      request.observations(),
      finalPrice,
      musicalSpace,
      currentUser,
      band
    );

    reservationSessionDao.save(reservationSession);
    return ReservationSessionView.from(reservationSession);
  }

  public ReservationSessionView update(Long id, ReservationSessionRequest request)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(id);
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    validateCanUpdate(reservationSession, currentUser);

    MusicalSpace musicalSpace = findReservableSpace(request.musicalSpaceId());
    Band band = findAndValidateBand(request.bandId(), currentUser);
    validateReservationRequest(request, musicalSpace);
    validateNoOverlappingReservation(request.musicalSpaceId(), id, request.sessionDate(), request.startTime(), request.endTime());
    validateFitsCalculatedAvailability(request.musicalSpaceId(), id, request.sessionDate(), request.startTime(), request.endTime());

    reservationSession.setMusicalSpace(musicalSpace);
    reservationSession.setSessionDate(request.sessionDate());
    reservationSession.setTimeRange(request.startTime(), request.endTime());
    reservationSession.setAttendeesCount(request.attendeesCount());
    reservationSession.setSessionType(request.sessionType());
    reservationSession.setObservations(request.observations());
    reservationSession.setBand(band);
    reservationSession.setFinalPrice(availabilityPricingService.calculatePrice(
      request.musicalSpaceId(),
      request.sessionDate(),
      request.startTime(),
      request.endTime()
    ));

    return ReservationSessionView.from(reservationSessionDao.update(reservationSession));
  }

  public ReservationSessionView cancel(Long id, ReservationCancellationRequest request)
    throws NotFoundException, OperationNotAllowed {
    return cancel(id, request, true);
  }

  public ReservationSessionView cancelAdmin(Long id, ReservationCancellationRequest request)
    throws NotFoundException, OperationNotAllowed {
    return cancel(id, request, false);
  }

  private ReservationSessionView cancel(Long id, ReservationCancellationRequest request, boolean rejectAdminPublicFlow)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(id);
    User currentUser = findCurrentUser();
    if (rejectAdminPublicFlow) {
      validatePublicUserFlow(currentUser);
    }

    if (!isOwner(reservationSession, currentUser) && !canManageSpace(reservationSession.getMusicalSpace(), currentUser)) {
      throw new OperationNotAllowed("The user cannot cancel this reservation");
    }

    if (!ReservationSessionState.PENDING.equals(reservationSession.getState())
      && !ReservationSessionState.ACCEPTED.equals(reservationSession.getState())) {
      throw new OperationNotAllowed("Only pending or accepted reservations can be cancelled");
    }

    if (hasStarted(reservationSession)) {
      throw new OperationNotAllowed("Reservations that have already started cannot be cancelled");
    }

    reservationSession.setState(ReservationSessionState.CANCELLED);
    reservationSession.setCancellationReason(request.cancellationReason());

    return ReservationSessionView.from(reservationSessionDao.update(reservationSession));
  }

  public ReservationSessionView updateState(Long id, ReservationStateUpdateRequest request)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(id);
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);

    if (!canManageSpace(reservationSession.getMusicalSpace(), currentUser)) {
      throw new OperationNotAllowed("The user cannot manage this reservation");
    }

    validateStateTransition(reservationSession, request.state());
    reservationSession.setState(request.state());

    return ReservationSessionView.from(reservationSessionDao.update(reservationSession));
  }

  private ReservationSession findReservationSession(Long id) throws NotFoundException {
    return reservationSessionDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), ReservationSession.class));
  }

  private void validatePublicUserFlow(User user) throws OperationNotAllowed {
    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot mutate reservations from the public user flow");
    }
  }

  private MusicalSpace findReservableSpace(Long id) throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = musicalSpaceDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), MusicalSpace.class));

    if (!musicalSpace.isPubliclyVisible()) {
      throw new OperationNotAllowed("The musical space is not available for reservations");
    }

    return musicalSpace;
  }

  private Band findAndValidateBand(Long bandId, User currentUser) throws NotFoundException, OperationNotAllowed {
    if (bandId == null) {
      return null;
    }

    Band band = bandDao.findById(bandId)
      .orElseThrow(() -> new NotFoundException(bandId.toString(), Band.class));

    if (!band.isActive()) {
      throw new OperationNotAllowed("Inactive bands cannot be associated with reservations");
    }

    boolean currentUserBelongsToBand = bandMemberDao.findActiveByBandAndUser(bandId, currentUser.getId()).isPresent();

    if (!currentUserBelongsToBand) {
      throw new OperationNotAllowed("The user is not an active member of the band");
    }

    return band;
  }

  private void validateReservationRequest(ReservationSessionRequest request, MusicalSpace musicalSpace) {
    if (!request.startTime().isBefore(request.endTime())) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }

    if (request.sessionDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("The reservation date cannot be in the past");
    }

    if (request.attendeesCount() < 1) {
      throw new IllegalArgumentException("The attendees count must be greater than zero");
    }

    if (request.attendeesCount() > musicalSpace.getCapacity()) {
      throw new IllegalArgumentException("The attendees count cannot exceed the musical space capacity");
    }
  }

  private void validateFitsCalculatedAvailability(
    Long spaceId,
    Long currentReservationId,
    LocalDate sessionDate,
    LocalTime startTime,
    LocalTime endTime
  ) throws NotFoundException, OperationNotAllowed {
    boolean fitsAvailability =
      calculatedAvailabilityService.isRangeAvailable(spaceId, sessionDate, startTime, endTime, currentReservationId);

    if (!fitsAvailability) {
      throw new OperationNotAllowed("The requested time range is not available");
    }
  }

  private void validateNoOverlappingReservation(
    Long spaceId,
    Long currentReservationId,
    LocalDate sessionDate,
    LocalTime startTime,
    LocalTime endTime
  ) throws OperationNotAllowed {
    boolean overlaps = reservationSessionDao.findByMusicalSpaceAndDate(spaceId, sessionDate).stream()
      .filter(ReservationSession::isBlockingAvailability)
      .filter(reservation -> currentReservationId == null || !reservation.getId().equals(currentReservationId))
      .anyMatch(reservation -> reservation.overlaps(sessionDate, startTime, endTime));

    if (overlaps) {
      throw new OperationNotAllowed("The requested time range overlaps with another active reservation");
    }
  }

  private void validateCanView(ReservationSession reservationSession) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    if (!isOwner(reservationSession, currentUser) && !canManageSpace(reservationSession.getMusicalSpace(), currentUser)) {
      throw new OperationNotAllowed("The user cannot view this reservation");
    }
  }

  private void validateCanUpdate(ReservationSession reservationSession, User currentUser) throws OperationNotAllowed {
    if (!ReservationSessionState.PENDING.equals(reservationSession.getState())) {
      throw new OperationNotAllowed("Only pending reservations can be updated");
    }

    if (!isOwner(reservationSession, currentUser)) {
      throw new OperationNotAllowed("The user cannot update this reservation");
    }

    if (hasStarted(reservationSession)) {
      throw new OperationNotAllowed("Reservations that have already started cannot be updated");
    }
  }

  private void validateStateTransition(
    ReservationSession reservationSession,
    ReservationSessionState nextState
  ) throws OperationNotAllowed {
    ReservationSessionState currentState = reservationSession.getState();

    if (currentState.equals(nextState)) {
      return;
    }

    if (ReservationSessionState.CANCELLED.equals(nextState)) {
      throw new OperationNotAllowed("Use the cancel endpoint to cancel a reservation");
    }

    boolean allowed = switch (currentState) {
      case PENDING -> ReservationSessionState.ACCEPTED.equals(nextState)
        || ReservationSessionState.REJECTED.equals(nextState);
      case ACCEPTED -> ReservationSessionState.COMPLETED.equals(nextState);
      case REJECTED, CANCELLED, COMPLETED -> false;
    };

    if (!allowed) {
      throw new OperationNotAllowed("Invalid reservation state transition for the state management endpoint");
    }

    if (ReservationSessionState.ACCEPTED.equals(nextState) && hasStarted(reservationSession)) {
      throw new OperationNotAllowed("Reservations that have already started cannot be accepted");
    }

    if (ReservationSessionState.COMPLETED.equals(nextState) && !hasEnded(reservationSession)) {
      throw new OperationNotAllowed("Only reservations that have already ended can be completed");
    }
  }

  private boolean hasStarted(ReservationSession reservationSession) {
    return !LocalDateTime.of(
      reservationSession.getSessionDate(),
      reservationSession.getStartTime()
    ).isAfter(LocalDateTime.now());
  }

  private boolean hasEnded(ReservationSession reservationSession) {
    return !LocalDateTime.of(
      reservationSession.getSessionDate(),
      reservationSession.getEndTime()
    ).isAfter(LocalDateTime.now());
  }

  private boolean isOwner(ReservationSession reservationSession, User user) {
    return reservationSession.getUser().getId().equals(user.getId());
  }

  private boolean canManageSpace(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }
}
