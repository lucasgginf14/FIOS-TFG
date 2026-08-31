package es.udc.tfg.fios_rest.userreview.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import es.udc.tfg.fios_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;
import es.udc.tfg.fios_rest.userreview.service.dto.PendingUserReviewView;
import es.udc.tfg.fios_rest.userreview.service.dto.UserRatingView;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewRequest;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewView;
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
public class UserReviewService {

  @Autowired
  private UserReviewDao userReviewDao;

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<PendingUserReviewView> findPendingReviews() throws NotFoundException {
    User currentUser = findCurrentUser();
    List<ReservationSession> reservations = reservationSessionDao.findByManager(currentUser.getId()).stream()
      .filter(reservation -> ReservationSessionState.COMPLETED.equals(reservation.getState()))
      .filter(reservation -> !reservation.getUser().getId().equals(currentUser.getId()))
      .toList();

    Set<Long> reviewedReservationIds = Set.copyOf(userReviewDao.findReviewedReservationIds(
      reservations.stream().map(ReservationSession::getId).toList()
    ));

    return reservations.stream()
      .filter(reservation -> !reviewedReservationIds.contains(reservation.getId()))
      .map(PendingUserReviewView::from)
      .toList();
  }

  public UserReviewView create(Long reservationId, UserReviewRequest request)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(reservationId);
    User currentUser = findCurrentUser();

    validateCanReview(reservationSession, currentUser);
    validateRatings(request);

    if (userReviewDao.findByReservationSession(reservationId).isPresent()) {
      throw new IllegalArgumentException("The reservation already has a user review");
    }

    UserReview userReview = new UserReview(
      request.comment(),
      request.overallRating(),
      request.communicationRating(),
      request.punctualityRating(),
      request.careRating(),
      currentUser,
      reservationSession.getUser(),
      reservationSession.getMusicalSpace(),
      reservationSession
    );

    userReviewDao.save(userReview);
    return UserReviewView.from(userReview);
  }

  @Transactional(readOnly = true)
  public List<UserReviewView> findMyReviews() throws NotFoundException {
    User currentUser = findCurrentUser();

    return userReviewDao.findByReviewer(currentUser.getId()).stream()
      .map(UserReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<UserReviewView> findReceivedReviews() throws NotFoundException {
    User currentUser = findCurrentUser();

    return userReviewDao.findByReviewedUser(currentUser.getId()).stream()
      .map(UserReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<UserReviewView> findAllAdmin() {
    return userReviewDao.findAll().stream()
      .map(UserReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<UserReviewView> findByReviewedUser(Long userId) throws NotFoundException {
    User reviewedUser = findVisibleReviewedUser(userId);

    return userReviewDao.findByReviewedUser(reviewedUser.getId()).stream()
      .map(UserReviewView::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public UserRatingView getRating(Long userId) throws NotFoundException {
    User reviewedUser = findVisibleReviewedUser(userId);
    long reviewsCount = userReviewDao.countByReviewedUser(reviewedUser.getId());
    Double average = userReviewDao.findAverageOverallRatingByReviewedUser(reviewedUser.getId());

    return new UserRatingView(
      reviewedUser.getId(),
      reviewsCount,
      average == null ? BigDecimal.ZERO : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP)
    );
  }

  public void deleteAdmin(Long reviewId) throws NotFoundException {
    UserReview userReview = userReviewDao.findById(reviewId)
      .orElseThrow(() -> new NotFoundException(reviewId.toString(), UserReview.class));

    userReviewDao.delete(userReview);
  }

  private ReservationSession findReservationSession(Long reservationId) throws NotFoundException {
    return reservationSessionDao.findById(reservationId)
      .orElseThrow(() -> new NotFoundException(reservationId.toString(), ReservationSession.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private User findVisibleReviewedUser(Long userId) throws NotFoundException {
    User reviewedUser = userDao.findById(userId)
      .orElseThrow(() -> new NotFoundException(userId.toString(), User.class));

    boolean canViewInactive = findOptionalCurrentUser()
      .map(user -> user.isAdmin() || user.getId().equals(reviewedUser.getId()))
      .orElse(false);

    if (!reviewedUser.isActive() && !canViewInactive) {
      throw new NotFoundException(userId.toString(), User.class);
    }

    return reviewedUser;
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
      throw new OperationNotAllowed("Administrators cannot create user reviews from the public user flow");
    }

    if (!reservationSession.getMusicalSpace().getManager().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("The user cannot manage user reviews for this reservation");
    }

    if (reservationSession.getUser().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("The user cannot manage user reviews for their own reservation");
    }

    if (!ReservationSessionState.COMPLETED.equals(reservationSession.getState())) {
      throw new OperationNotAllowed("Only completed reservations can be reviewed");
    }
  }

  private void validateRatings(UserReviewRequest request) {
    validateRating("overallRating", request.overallRating());
    validateRating("communicationRating", request.communicationRating());
    validateRating("punctualityRating", request.punctualityRating());
    validateRating("careRating", request.careRating());
  }

  private void validateRating(String field, Integer rating) {
    if (rating == null || rating < 1 || rating > 5) {
      throw new IllegalArgumentException(field + " must be between 1 and 5");
    }
  }
}
