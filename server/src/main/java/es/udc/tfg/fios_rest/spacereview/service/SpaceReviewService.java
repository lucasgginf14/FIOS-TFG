package es.udc.tfg.fios_rest.spacereview.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewDao;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import es.udc.tfg.fios_rest.spacereview.service.dto.PendingSpaceReviewView;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceRatingView;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewRequest;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewView;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(rollbackFor = Exception.class)
public class SpaceReviewService {

  @Autowired
  private SpaceReviewDao spaceReviewDao;

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<PendingSpaceReviewView> findPendingReviews() throws NotFoundException {
    User currentUser = findCurrentUser();
    List<ReservationSession> reservations = reservationSessionDao.findByUser(currentUser.getId()).stream()
      .filter(reservation -> ReservationSessionState.COMPLETED.equals(reservation.getState()))
      .toList();

    Set<Long> reviewedReservationIds = Set.copyOf(spaceReviewDao.findReviewedReservationIds(
      reservations.stream().map(ReservationSession::getId).toList()
    ));

    return reservations.stream()
      .filter(reservation -> !reviewedReservationIds.contains(reservation.getId()))
      .map(PendingSpaceReviewView::from)
      .toList();
  }

  public SpaceReviewView create(Long reservationId, SpaceReviewRequest request)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(reservationId);
    User currentUser = findCurrentUser();

    validateCanReview(reservationSession, currentUser);
    validateRatings(request);

    if (spaceReviewDao.findByReservationSession(reservationId).isPresent()) {
      throw new IllegalArgumentException("The reservation already has a space review");
    }

    SpaceReview spaceReview = new SpaceReview(
      request.comment(),
      request.overallRating(),
      request.cleanlinessRating(),
      request.soundQualityRating(),
      request.equipmentRating(),
      request.locationRating(),
      reservationSession.getMusicalSpace(),
      currentUser,
      reservationSession
    );

    spaceReviewDao.save(spaceReview);
    return SpaceReviewView.from(spaceReview);
  }

  @Transactional(readOnly = true)
  public List<SpaceReviewView> findMyReviews() throws NotFoundException {
    User currentUser = findCurrentUser();

    return spaceReviewDao.findByUser(currentUser.getId()).stream()
      .map(SpaceReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<SpaceReviewView> findAllAdmin() {
    return spaceReviewDao.findAll().stream()
      .map(SpaceReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<SpaceReviewView> findByMusicalSpace(Long spaceId) throws NotFoundException {
    MusicalSpace musicalSpace = findAndValidateVisibleMusicalSpace(spaceId);

    return spaceReviewDao.findByMusicalSpace(musicalSpace.getId()).stream()
      .map(SpaceReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public SpaceRatingView getRating(Long spaceId) throws NotFoundException {
    MusicalSpace musicalSpace = findAndValidateVisibleMusicalSpace(spaceId);
    long reviewsCount = spaceReviewDao.countByMusicalSpace(musicalSpace.getId());
    Double average = spaceReviewDao.findAverageOverallRatingByMusicalSpace(musicalSpace.getId());

    return new SpaceRatingView(
      musicalSpace.getId(),
      reviewsCount,
      average == null ? BigDecimal.ZERO : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP)
    );
  }

  public void deleteAdmin(Long reviewId) throws NotFoundException {
    SpaceReview spaceReview = spaceReviewDao.findById(reviewId)
      .orElseThrow(() -> new NotFoundException(reviewId.toString(), SpaceReview.class));

    spaceReviewDao.delete(spaceReview);
  }

  private ReservationSession findReservationSession(Long reservationId) throws NotFoundException {
    return reservationSessionDao.findById(reservationId)
      .orElseThrow(() -> new NotFoundException(reservationId.toString(), ReservationSession.class));
  }

  private MusicalSpace findAndValidateVisibleMusicalSpace(Long spaceId) throws NotFoundException {
    MusicalSpace musicalSpace = musicalSpaceDao.findById(spaceId)
      .orElseThrow(() -> new NotFoundException(spaceId.toString(), MusicalSpace.class));

    boolean canViewPrivateReviews = findOptionalCurrentUser()
      .map(user -> canManage(musicalSpace, user))
      .orElse(false);

    if (!musicalSpace.isPubliclyVisible() && !canViewPrivateReviews) {
      throw new NotFoundException(spaceId.toString(), MusicalSpace.class);
    }

    return musicalSpace;
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

  private void validateCanReview(ReservationSession reservationSession, User currentUser) throws OperationNotAllowed {
    if (currentUser.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot create space reviews from the public user flow");
    }

    if (!reservationSession.getUser().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("Only the reservation owner can create the space review");
    }

    if (!ReservationSessionState.COMPLETED.equals(reservationSession.getState())) {
      throw new OperationNotAllowed("Only completed reservations can be reviewed");
    }
  }

  private void validateRatings(SpaceReviewRequest request) {
    validateRating("overallRating", request.overallRating());
    validateRating("cleanlinessRating", request.cleanlinessRating());
    validateRating("soundQualityRating", request.soundQualityRating());
    validateRating("equipmentRating", request.equipmentRating());
    validateRating("locationRating", request.locationRating());
  }

  private void validateRating(String field, Integer rating) {
    if (rating == null || rating < 1 || rating > 5) {
      throw new IllegalArgumentException(field + " must be between 1 and 5");
    }
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
  }
}
