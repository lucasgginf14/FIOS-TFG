package es.udc.tfg.fios_rest.availability.service;

import es.udc.tfg.fios_rest.availability.service.dto.BookedAvailabilitySlotView;
import es.udc.tfg.fios_rest.availability.service.dto.AvailabilitySlotView;
import es.udc.tfg.fios_rest.availability.service.dto.CalculatedAvailabilityView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.exceptions.persistence.dao.SpaceAvailabilityExceptionDao;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class CalculatedAvailabilityService {

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private ScheduleDao scheduleDao;

  @Autowired
  private SpaceAvailabilityExceptionDao spaceAvailabilityExceptionDao;

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private AvailabilityPricingService availabilityPricingService;

  @Transactional(readOnly = true)
  public CalculatedAvailabilityView findAvailability(Long spaceId, LocalDate date) throws NotFoundException {
    return findAvailability(spaceId, date, null);
  }

  private CalculatedAvailabilityView findAvailability(Long spaceId, LocalDate date, Long excludedReservationId)
    throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanViewAvailability(musicalSpace, spaceId);

    List<TimeRange> availableRanges = new ArrayList<>();

    scheduleDao.findByMusicalSpaceAndDay(spaceId, date.getDayOfWeek()).stream()
      .filter(schedule -> availabilityPricingService.hasPositivePrice(schedule.getPrice()))
      .map(schedule -> new TimeRange(schedule.getStartTime(), schedule.getEndTime()))
      .forEach(availableRanges::add);

    List<SpaceAvailabilityException> exceptions =
      spaceAvailabilityExceptionDao.findByMusicalSpaceAndDate(spaceId, date).stream().toList();

    exceptions.stream()
      .filter(availabilityPricingService::isReservableCustomAvailability)
      .map(spaceException -> new TimeRange(spaceException.getStartTime(), spaceException.getEndTime()))
      .forEach(availableRanges::add);

    List<TimeRange> normalizedAvailability = mergeRanges(availableRanges);

    List<TimeRange> blockedRanges = new ArrayList<>();

    exceptions.stream()
      .filter(spaceException -> SpaceAvailabilityExceptionType.BLOCKED.equals(spaceException.getExceptionType()))
      .map(spaceException -> new TimeRange(spaceException.getStartTime(), spaceException.getEndTime()))
      .forEach(blockedRanges::add);

    List<ReservationSession> bookedReservations = reservationSessionDao.findByMusicalSpaceAndDate(spaceId, date).stream()
      .filter(ReservationSession::isBlockingAvailability)
      .filter(reservation -> excludedReservationId == null || !reservation.getId().equals(excludedReservationId))
      .sorted(Comparator.comparing(ReservationSession::getStartTime).thenComparing(ReservationSession::getEndTime))
      .toList();

    bookedReservations.stream()
      .map(reservation -> new TimeRange(reservation.getStartTime(), reservation.getEndTime()))
      .forEach(blockedRanges::add);

    for (TimeRange blockedRange : blockedRanges) {
      normalizedAvailability = subtractRange(normalizedAvailability, blockedRange);
    }

    List<AvailabilitySlotView> slots = mergeRanges(normalizedAvailability).stream()
      .map(range -> new AvailabilitySlotView(
        range.startTime(),
        range.endTime(),
        availabilityPricingService.estimatePrice(spaceId, date, range.startTime(), range.endTime())
      ))
      .toList();

    List<BookedAvailabilitySlotView> bookedSlots = bookedReservations.stream()
      .map(reservation -> new BookedAvailabilitySlotView(
        reservation.getStartTime(),
        reservation.getEndTime(),
        reservation.getState()
      ))
      .toList();

    return new CalculatedAvailabilityView(spaceId, date, slots, bookedSlots);
  }

  @Transactional(readOnly = true)
  public boolean isRangeAvailable(
    Long spaceId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime
  ) throws NotFoundException {
    return isRangeAvailable(spaceId, date, startTime, endTime, null);
  }

  @Transactional(readOnly = true)
  public boolean isRangeAvailable(
    Long spaceId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    Long excludedReservationId
  ) throws NotFoundException {
    return findAvailability(spaceId, date, excludedReservationId).slots().stream()
      .anyMatch(slot -> !startTime.isBefore(slot.startTime()) && !endTime.isAfter(slot.endTime()));
  }

  private MusicalSpace findMusicalSpace(Long id) throws NotFoundException {
    return musicalSpaceDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), MusicalSpace.class));
  }

  private void validateCanViewAvailability(MusicalSpace musicalSpace, Long spaceId) throws NotFoundException {
    boolean canViewPrivateSpace = findOptionalCurrentUser()
      .map(user -> canManage(musicalSpace, user))
      .orElse(false);

    if (!musicalSpace.isPubliclyVisible() && !canViewPrivateSpace) {
      throw new NotFoundException(spaceId.toString(), MusicalSpace.class);
    }
  }

  private Optional<User> findOptionalCurrentUser() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();

    if (currentUserLogin == null) {
      return Optional.empty();
    }

    return userDao.findByEmail(currentUserLogin.toLowerCase());
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
  }

  private List<TimeRange> mergeRanges(List<TimeRange> ranges) {
    List<TimeRange> sortedRanges = ranges.stream()
      .sorted(Comparator.comparing(TimeRange::startTime).thenComparing(TimeRange::endTime))
      .toList();

    List<TimeRange> mergedRanges = new ArrayList<>();

    for (TimeRange range : sortedRanges) {
      if (mergedRanges.isEmpty()) {
        mergedRanges.add(range);
        continue;
      }

      TimeRange lastRange = mergedRanges.get(mergedRanges.size() - 1);

      if (!range.startTime().isAfter(lastRange.endTime())) {
        mergedRanges.set(
          mergedRanges.size() - 1,
          new TimeRange(lastRange.startTime(), max(lastRange.endTime(), range.endTime()))
        );
      } else {
        mergedRanges.add(range);
      }
    }

    return mergedRanges;
  }

  private List<TimeRange> subtractRange(List<TimeRange> availableRanges, TimeRange blockedRange) {
    List<TimeRange> result = new ArrayList<>();

    for (TimeRange availableRange : availableRanges) {
      if (!availableRange.overlaps(blockedRange)) {
        result.add(availableRange);
        continue;
      }

      if (blockedRange.startTime().isAfter(availableRange.startTime())) {
        result.add(new TimeRange(availableRange.startTime(), min(blockedRange.startTime(), availableRange.endTime())));
      }

      if (blockedRange.endTime().isBefore(availableRange.endTime())) {
        result.add(new TimeRange(max(blockedRange.endTime(), availableRange.startTime()), availableRange.endTime()));
      }
    }

    return result.stream()
      .filter(TimeRange::isValid)
      .toList();
  }

  private LocalTime min(LocalTime left, LocalTime right) {
    return left.isBefore(right) ? left : right;
  }

  private LocalTime max(LocalTime left, LocalTime right) {
    return left.isAfter(right) ? left : right;
  }

  private record TimeRange(LocalTime startTime, LocalTime endTime) {
    private boolean overlaps(TimeRange other) {
      return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    private boolean isValid() {
      return startTime.isBefore(endTime);
    }
  }
}
