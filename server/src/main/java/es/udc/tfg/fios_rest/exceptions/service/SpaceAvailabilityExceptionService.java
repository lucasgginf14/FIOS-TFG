package es.udc.tfg.fios_rest.exceptions.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.exceptions.persistence.dao.SpaceAvailabilityExceptionDao;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.exceptions.service.dto.SpaceAvailabilityExceptionRequest;
import es.udc.tfg.fios_rest.exceptions.service.dto.SpaceAvailabilityExceptionView;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SpaceAvailabilityExceptionService {

  @Autowired
  private SpaceAvailabilityExceptionDao spaceAvailabilityExceptionDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<SpaceAvailabilityExceptionView> findByMusicalSpace(Long spaceId)
    throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanManage(musicalSpace);

    return spaceAvailabilityExceptionDao.findByMusicalSpace(spaceId).stream()
      .map(SpaceAvailabilityExceptionView::from)
      .toList();
  }

  public SpaceAvailabilityExceptionView create(Long spaceId, SpaceAvailabilityExceptionRequest request)
    throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateTimeRange(request.startTime(), request.endTime());
    validatePriceForType(request);
    validateNoDuplicateOrOverlap(musicalSpace.getId(), null, request);

    SpaceAvailabilityException spaceAvailabilityException = new SpaceAvailabilityException(
      request.date(),
      request.startTime(),
      request.endTime(),
      request.exceptionType(),
      request.reason(),
      request.price(),
      musicalSpace
    );

    spaceAvailabilityExceptionDao.save(spaceAvailabilityException);
    return SpaceAvailabilityExceptionView.from(spaceAvailabilityException);
  }

  public SpaceAvailabilityExceptionView update(Long id, SpaceAvailabilityExceptionRequest request)
    throws NotFoundException, OperationNotAllowed {
    SpaceAvailabilityException spaceAvailabilityException = findException(id);
    MusicalSpace musicalSpace = spaceAvailabilityException.getMusicalSpace();
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateTimeRange(request.startTime(), request.endTime());
    validatePriceForType(request);
    validateNoDuplicateOrOverlap(musicalSpace.getId(), id, request);

    spaceAvailabilityException.setDate(request.date());
    spaceAvailabilityException.setTimeRange(request.startTime(), request.endTime());
    spaceAvailabilityException.setExceptionType(request.exceptionType());
    spaceAvailabilityException.setReason(request.reason());
    spaceAvailabilityException.setPrice(request.price());

    return SpaceAvailabilityExceptionView.from(spaceAvailabilityExceptionDao.update(spaceAvailabilityException));
  }

  public void delete(Long id) throws NotFoundException, OperationNotAllowed {
    SpaceAvailabilityException spaceAvailabilityException = findException(id);
    validateCanManage(spaceAvailabilityException.getMusicalSpace());
    spaceAvailabilityExceptionDao.delete(spaceAvailabilityException);
  }

  private SpaceAvailabilityException findException(Long id) throws NotFoundException {
    return spaceAvailabilityExceptionDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), SpaceAvailabilityException.class));
  }

  private MusicalSpace findMusicalSpace(Long id) throws NotFoundException {
    return musicalSpaceDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), MusicalSpace.class));
  }

  private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
    if (!startTime.isBefore(endTime)) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }
  }

  private void validateActiveSpace(MusicalSpace musicalSpace) throws OperationNotAllowed {
    if (!musicalSpace.isActive()) {
      throw new OperationNotAllowed("Exceptions cannot be changed for inactive musical spaces");
    }
  }

  private void validatePriceForType(SpaceAvailabilityExceptionRequest request) {
    BigDecimal price = request.price();

    if (SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY.equals(request.exceptionType())) {
      if (price == null) {
        throw new IllegalArgumentException("Custom availability must include a price");
      }

      if (price.signum() <= 0) {
        throw new IllegalArgumentException("The custom availability price must be greater than zero");
      }

      return;
    }

    if (price != null) {
      throw new IllegalArgumentException("Blocked availability cannot include a price");
    }
  }

  private void validateNoDuplicateOrOverlap(
    Long spaceId,
    Long currentExceptionId,
    SpaceAvailabilityExceptionRequest request
  ) {
    spaceAvailabilityExceptionDao.findByMusicalSpace(spaceId).stream()
      .filter(spaceException -> currentExceptionId == null || !spaceException.getId().equals(currentExceptionId))
      .filter(spaceException -> spaceException.getDate().equals(request.date()))
      .forEach(spaceException -> {
        if (spaceException.hasSameSlot(
          request.date(),
          request.startTime(),
          request.endTime(),
          request.exceptionType()
        )) {
          throw new IllegalArgumentException("An exception with the same date, time range and type already exists");
        }

        if (spaceException.overlaps(request.date(), request.startTime(), request.endTime())) {
          throw new IllegalArgumentException("The exception overlaps with another exception for this musical space");
        }
      });
  }

  private void validateCanManage(MusicalSpace musicalSpace) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    if (!canManage(musicalSpace, currentUser)) {
      throw new OperationNotAllowed("The user cannot manage exceptions for this musical space");
    }
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
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
}
