package es.udc.tfg.fios_rest.schedule.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import es.udc.tfg.fios_rest.schedule.service.dto.ScheduleRequest;
import es.udc.tfg.fios_rest.schedule.service.dto.ScheduleView;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ScheduleService {

  @Autowired
  private ScheduleDao scheduleDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<ScheduleView> findByMusicalSpace(Long spaceId) throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);

    boolean canViewPrivateSpace = findOptionalCurrentUser()
      .map(user -> canManage(musicalSpace, user))
      .orElse(false);

    if (!musicalSpace.isPubliclyVisible() && !canViewPrivateSpace) {
      throw new NotFoundException(spaceId.toString(), MusicalSpace.class);
    }

    return scheduleDao.findByMusicalSpace(spaceId).stream()
      .map(ScheduleView::from)
      .toList();
  }

  public ScheduleView create(Long spaceId, ScheduleRequest request)
    throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateTimeRange(request.startTime(), request.endTime());
    validateNoDuplicateOrOverlap(musicalSpace.getId(), null, request);

    Schedule schedule = new Schedule(
      request.dayOfWeek(),
      request.startTime(),
      request.endTime(),
      request.price(),
      musicalSpace
    );

    scheduleDao.save(schedule);
    return ScheduleView.from(schedule);
  }

  public ScheduleView update(Long id, ScheduleRequest request)
    throws NotFoundException, OperationNotAllowed {
    Schedule schedule = findSchedule(id);
    MusicalSpace musicalSpace = schedule.getMusicalSpace();
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateTimeRange(request.startTime(), request.endTime());
    validateNoDuplicateOrOverlap(musicalSpace.getId(), id, request);

    schedule.setDayOfWeek(request.dayOfWeek());
    schedule.setTimeRange(request.startTime(), request.endTime());
    schedule.setPrice(request.price());

    return ScheduleView.from(scheduleDao.update(schedule));
  }

  public void delete(Long id) throws NotFoundException, OperationNotAllowed {
    Schedule schedule = findSchedule(id);
    validateCanManage(schedule.getMusicalSpace());
    scheduleDao.delete(schedule);
  }

  private Schedule findSchedule(Long id) throws NotFoundException {
    return scheduleDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Schedule.class));
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
      throw new OperationNotAllowed("Schedules cannot be changed for inactive musical spaces");
    }
  }

  private void validateNoDuplicateOrOverlap(Long spaceId, Long currentScheduleId, ScheduleRequest request) {
    scheduleDao.findByMusicalSpace(spaceId).stream()
      .filter(schedule -> currentScheduleId == null || !schedule.getId().equals(currentScheduleId))
      .filter(schedule -> schedule.getDayOfWeek().equals(request.dayOfWeek()))
      .forEach(schedule -> {
        if (schedule.hasSameSlot(request.dayOfWeek(), request.startTime(), request.endTime())) {
          throw new IllegalArgumentException("A schedule with the same day and time range already exists");
        }

        if (schedule.overlaps(request.dayOfWeek(), request.startTime(), request.endTime())) {
          throw new IllegalArgumentException("The schedule overlaps with another schedule for this musical space");
        }
      });
  }

  private void validateCanManage(MusicalSpace musicalSpace) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    if (!canManage(musicalSpace, currentUser)) {
      throw new OperationNotAllowed("The user cannot manage schedules for this musical space");
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
