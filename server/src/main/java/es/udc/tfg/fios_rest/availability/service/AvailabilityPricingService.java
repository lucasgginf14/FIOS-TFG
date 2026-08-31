package es.udc.tfg.fios_rest.availability.service;

import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.exceptions.persistence.dao.SpaceAvailabilityExceptionDao;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AvailabilityPricingService {

  @Autowired
  private ScheduleDao scheduleDao;

  @Autowired
  private SpaceAvailabilityExceptionDao spaceAvailabilityExceptionDao;

  @Transactional(readOnly = true)
  public BigDecimal calculatePrice(
    Long spaceId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime
  ) throws OperationNotAllowed {
    BigDecimal price = estimatePrice(spaceId, date, startTime, endTime);

    if (price == null) {
      throw new OperationNotAllowed("The reservation price could not be calculated for the selected time range");
    }

    return price;
  }

  @Transactional(readOnly = true)
  public BigDecimal estimatePrice(
    Long spaceId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime
  ) {
    if (spaceId == null || date == null || startTime == null || endTime == null || !startTime.isBefore(endTime)) {
      return null;
    }

    List<PricedTimeRange> priceRanges = findPricedRanges(spaceId, date);

    if (priceRanges.isEmpty()) {
      return null;
    }

    List<LocalTime> breakpoints = buildBreakpoints(priceRanges, startTime, endTime);
    BigDecimal total = BigDecimal.ZERO;

    for (int index = 0; index < breakpoints.size() - 1; index++) {
      LocalTime segmentStart = breakpoints.get(index);
      LocalTime segmentEnd = breakpoints.get(index + 1);

      if (!segmentStart.isBefore(segmentEnd)) {
        continue;
      }

      PricedTimeRange source = findPriceSource(priceRanges, segmentStart, segmentEnd);

      if (source == null) {
        return null;
      }

      BigDecimal contribution = calculateContribution(source, segmentStart, segmentEnd);

      if (contribution == null || contribution.signum() <= 0) {
        return null;
      }

      total = total.add(contribution);
    }

    BigDecimal roundedTotal = total.setScale(2, RoundingMode.HALF_UP);
    return roundedTotal.signum() > 0 ? roundedTotal : null;
  }

  public boolean hasPositivePrice(BigDecimal price) {
    return price != null && price.signum() > 0;
  }

  public boolean isReservableCustomAvailability(SpaceAvailabilityException spaceException) {
    return SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY.equals(spaceException.getExceptionType())
      && hasPositivePrice(spaceException.getPrice());
  }

  private List<PricedTimeRange> findPricedRanges(Long spaceId, LocalDate date) {
    List<PricedTimeRange> priceRanges = new ArrayList<>();

    scheduleDao.findByMusicalSpaceAndDay(spaceId, date.getDayOfWeek()).stream()
      .filter(schedule -> hasPositivePrice(schedule.getPrice()))
      .map(this::toPricedTimeRange)
      .forEach(priceRanges::add);

    spaceAvailabilityExceptionDao.findByMusicalSpaceAndDate(spaceId, date).stream()
      .filter(this::isReservableCustomAvailability)
      .map(this::toPricedTimeRange)
      .forEach(priceRanges::add);

    return priceRanges;
  }

  private PricedTimeRange toPricedTimeRange(Schedule schedule) {
    return new PricedTimeRange(
      schedule.getStartTime(),
      schedule.getEndTime(),
      schedule.getPrice(),
      false
    );
  }

  private PricedTimeRange toPricedTimeRange(SpaceAvailabilityException spaceException) {
    return new PricedTimeRange(
      spaceException.getStartTime(),
      spaceException.getEndTime(),
      spaceException.getPrice(),
      true
    );
  }

  private List<LocalTime> buildBreakpoints(
    List<PricedTimeRange> priceRanges,
    LocalTime startTime,
    LocalTime endTime
  ) {
    List<LocalTime> breakpoints = new ArrayList<>();
    breakpoints.add(startTime);
    breakpoints.add(endTime);

    priceRanges.stream()
      .filter(range -> range.overlaps(startTime, endTime))
      .forEach(range -> {
        if (range.startTime().isAfter(startTime) && range.startTime().isBefore(endTime)) {
          breakpoints.add(range.startTime());
        }

        if (range.endTime().isAfter(startTime) && range.endTime().isBefore(endTime)) {
          breakpoints.add(range.endTime());
        }
      });

    return breakpoints.stream()
      .distinct()
      .sorted()
      .toList();
  }

  private PricedTimeRange findPriceSource(
    List<PricedTimeRange> priceRanges,
    LocalTime segmentStart,
    LocalTime segmentEnd
  ) {
    return priceRanges.stream()
      .filter(PricedTimeRange::custom)
      .filter(range -> range.covers(segmentStart, segmentEnd))
      .min(Comparator.comparingLong(PricedTimeRange::durationMinutes))
      .or(() -> priceRanges.stream()
        .filter(range -> !range.custom())
        .filter(range -> range.covers(segmentStart, segmentEnd))
        .min(Comparator.comparingLong(PricedTimeRange::durationMinutes)))
      .orElse(null);
  }

  private BigDecimal calculateContribution(
    PricedTimeRange source,
    LocalTime segmentStart,
    LocalTime segmentEnd
  ) {
    long segmentMinutes = Duration.between(segmentStart, segmentEnd).toMinutes();
    long sourceMinutes = source.durationMinutes();

    if (segmentMinutes <= 0 || sourceMinutes <= 0) {
      return null;
    }

    return source.price()
      .multiply(BigDecimal.valueOf(segmentMinutes))
      .divide(BigDecimal.valueOf(sourceMinutes), 4, RoundingMode.HALF_UP);
  }

  private record PricedTimeRange(
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal price,
    boolean custom
  ) {
    private boolean overlaps(LocalTime otherStart, LocalTime otherEnd) {
      return startTime.isBefore(otherEnd) && otherStart.isBefore(endTime);
    }

    private boolean covers(LocalTime otherStart, LocalTime otherEnd) {
      return !otherStart.isBefore(startTime) && !otherEnd.isAfter(endTime);
    }

    private long durationMinutes() {
      return Duration.between(startTime, endTime).toMinutes();
    }
  }
}
