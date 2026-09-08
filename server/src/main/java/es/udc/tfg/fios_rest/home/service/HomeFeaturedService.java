package es.udc.tfg.fios_rest.home.service;

import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentStatus;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.home.service.dto.FeaturedEventRef;
import es.udc.tfg.fios_rest.home.service.dto.FeaturedRecruitmentRef;
import es.udc.tfg.fios_rest.home.service.dto.FeaturedSpaceRef;
import es.udc.tfg.fios_rest.home.service.dto.HomeFeaturedView;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewDao;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewRatingStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class HomeFeaturedService {

  private static final int FEATURED_LIMIT = 3;
  private static final int SPACE_REVIEW_CONFIDENCE_TARGET = 20;

  private static final double SPACE_RATING_WEIGHT = 0.45;
  private static final double SPACE_REVIEW_CONFIDENCE_WEIGHT = 0.25;
  private static final double SPACE_CAPACITY_WEIGHT = 0.15;
  private static final double SPACE_SOUNDPROOFED_WEIGHT = 0.1;
  private static final double SPACE_IMAGE_WEIGHT = 0.05;

  private static final double EVENT_PROXIMITY_WEIGHT = 0.4;
  private static final double EVENT_IMAGE_WEIGHT = 0.2;
  private static final double EVENT_CAPACITY_WEIGHT = 0.15;
  private static final double EVENT_ACCESSIBLE_PRICE_WEIGHT = 0.1;
  private static final double EVENT_COMPLETENESS_WEIGHT = 0.1;
  private static final double EVENT_FIOS_LINK_WEIGHT = 0.05;

  private static final double RECRUITMENT_RECENCY_WEIGHT = 0.35;
  private static final double RECRUITMENT_COMPLETENESS_WEIGHT = 0.25;
  private static final double RECRUITMENT_VACANCIES_WEIGHT = 0.15;
  private static final double RECRUITMENT_BAND_IMAGE_WEIGHT = 0.1;
  private static final double RECRUITMENT_BAND_GENRE_WEIGHT = 0.1;
  private static final double RECRUITMENT_INSTRUMENT_WEIGHT = 0.05;

  private final MusicalSpaceDao musicalSpaceDao;
  private final EventDao eventDao;
  private final BandRecruitmentDao bandRecruitmentDao;
  private final ScheduleDao scheduleDao;
  private final SpaceReviewDao spaceReviewDao;
  private final Collator textCollator;

  public HomeFeaturedService(
    MusicalSpaceDao musicalSpaceDao,
    EventDao eventDao,
    BandRecruitmentDao bandRecruitmentDao,
    ScheduleDao scheduleDao,
    SpaceReviewDao spaceReviewDao
  ) {
    this.musicalSpaceDao = musicalSpaceDao;
    this.eventDao = eventDao;
    this.bandRecruitmentDao = bandRecruitmentDao;
    this.scheduleDao = scheduleDao;
    this.spaceReviewDao = spaceReviewDao;
    this.textCollator = Collator.getInstance(Locale.ROOT);
    this.textCollator.setStrength(Collator.PRIMARY);
  }

  @Transactional(readOnly = true)
  public HomeFeaturedView findFeatured() {
    LocalDate today = LocalDate.now();
    LocalTime nowTime = LocalTime.now();
    LocalDateTime now = LocalDateTime.now();

    return new HomeFeaturedView(
      findFeaturedSpaces(),
      findFeaturedEvents(today, nowTime),
      findFeaturedRecruitments(now)
    );
  }

  private List<FeaturedSpaceRef> findFeaturedSpaces() {
    List<MusicalSpace> spaces = musicalSpaceDao.findPublic().stream().toList();
    Map<Long, SpaceReviewRatingStats> ratingStatsBySpaceId = spaceReviewDao.findRatingStatsByMusicalSpaceIds(
      spaces.stream()
        .map(MusicalSpace::getId)
        .toList()
    );

    return spaces.stream()
      .map(space -> FeaturedSpaceRef.from(
        space,
        estimateSpacePrice(space),
        ratingStatsBySpaceId.get(space.getId()),
        scoreSpace(space, ratingStatsBySpaceId.get(space.getId())),
        getSpaceFeaturedReason(space, ratingStatsBySpaceId.get(space.getId()))
      ))
      .sorted(this::compareSpaces)
      .limit(FEATURED_LIMIT)
      .toList();
  }

  private List<FeaturedEventRef> findFeaturedEvents(LocalDate today, LocalTime nowTime) {
    return eventDao.findPublished(new EventFilterParams(null, null, null, null, null, null, null)).stream()
      .filter(event -> isUpcomingPublishedEvent(event, today, nowTime))
      .map(event -> FeaturedEventRef.from(
        event,
        scoreEvent(event, today),
        getEventFeaturedReason(event, today)
      ))
      .sorted((left, right) -> compareEvents(left, right, today))
      .limit(FEATURED_LIMIT)
      .toList();
  }

  private List<FeaturedRecruitmentRef> findFeaturedRecruitments(LocalDateTime now) {
    return bandRecruitmentDao.findOpen().stream()
      .filter(recruitment -> BandRecruitmentStatus.OPEN.equals(recruitment.getStatus())
        && recruitment.getBand().isActive())
      .map(recruitment -> FeaturedRecruitmentRef.from(recruitment, scoreRecruitment(recruitment, now)))
      .sorted(this::compareRecruitments)
      .limit(FEATURED_LIMIT)
      .toList();
  }

  private double scoreSpace(MusicalSpace space, SpaceReviewRatingStats ratingStats) {
    double rating = clamp01(toNumber(ratingStats == null ? null : ratingStats.averageOverallRating()) / 5);
    double reviewConfidence = normalizeReviewConfidence(ratingStats == null ? 0 : ratingStats.reviewsCount());
    double capacity = clamp01(toNumber(space.getCapacity()) / 50);
    double soundproofed = space.isSoundproofed() ? 1 : 0;
    double image = hasText(space.getMainImage()) ? 1 : 0;

    return toScore(
      SPACE_RATING_WEIGHT * rating
        + SPACE_REVIEW_CONFIDENCE_WEIGHT * reviewConfidence
        + SPACE_CAPACITY_WEIGHT * capacity
        + SPACE_SOUNDPROOFED_WEIGHT * soundproofed
        + SPACE_IMAGE_WEIGHT * image
    );
  }

  private double scoreEvent(Event event, LocalDate today) {
    double proximity = normalizeEventProximity(event, today);
    double image = hasText(event.getPosterImage()) ? 1 : 0;
    double capacity = clamp01(toNumber(event.getCapacity()) / 500);
    double accessiblePrice = normalizeAccessiblePrice(event.getTicketPrice());
    double completeness = normalizeCompleteness(
      event.getMusicalGenre(),
      event.getCity(),
      event.getVenueName(),
      event.getStartTime() == null ? event.getEndTime() : event.getStartTime()
    );
    double fiosLink = hasFiosLink(event) ? 1 : 0;

    return toScore(
      EVENT_PROXIMITY_WEIGHT * proximity
        + EVENT_IMAGE_WEIGHT * image
        + EVENT_CAPACITY_WEIGHT * capacity
        + EVENT_ACCESSIBLE_PRICE_WEIGHT * accessiblePrice
        + EVENT_COMPLETENESS_WEIGHT * completeness
        + EVENT_FIOS_LINK_WEIGHT * fiosLink
    );
  }

  private double scoreRecruitment(BandRecruitment recruitment, LocalDateTime now) {
    double recency = normalizeRecency(recruitment.getPublicationDate(), now);
    double completeness = normalizeCompleteness(
      recruitment.getTitle(),
      recruitment.getDescription(),
      recruitment.getCity(),
      recruitment.getLevelRequired(),
      recruitment.getRoleWanted()
    );
    double vacancies = clamp01(toNumber(recruitment.getVacancies()) / 5);
    double bandImage = hasText(recruitment.getBand().getImage()) ? 1 : 0;
    double bandGenre = hasText(recruitment.getBand().getMainGenre()) ? 1 : 0;
    double instrument = recruitment.getInstrument().getId() != null
      || hasText(recruitment.getInstrument().getName()) ? 1 : 0;

    return toScore(
      RECRUITMENT_RECENCY_WEIGHT * recency
        + RECRUITMENT_COMPLETENESS_WEIGHT * completeness
        + RECRUITMENT_VACANCIES_WEIGHT * vacancies
        + RECRUITMENT_BAND_IMAGE_WEIGHT * bandImage
        + RECRUITMENT_BAND_GENRE_WEIGHT * bandGenre
        + RECRUITMENT_INSTRUMENT_WEIGHT * instrument
    );
  }

  private String getSpaceFeaturedReason(MusicalSpace space, SpaceReviewRatingStats ratingStats) {
    if (toNumber(ratingStats == null ? null : ratingStats.averageOverallRating()) >= 4.5
      && toNumber(ratingStats == null ? null : ratingStats.reviewsCount()) >= 3) {
      return "highRating";
    }

    if (space.isSoundproofed()) {
      return "soundproofed";
    }

    if (toNumber(space.getCapacity()) >= 20) {
      return "large";
    }

    if (hasText(space.getMainImage())) {
      return "completeProfile";
    }

    return "balanced";
  }

  private String getEventFeaturedReason(Event event, LocalDate today) {
    long days = daysUntil(event.getEventDate(), today);

    if (days == 0) {
      return "today";
    }

    if (days <= 7) {
      return "soon";
    }

    if (event.getTicketPrice() == null || toNumber(event.getTicketPrice()) <= 15) {
      return "accessible";
    }

    if (toNumber(event.getCapacity()) >= 500) {
      return "large";
    }

    if (hasFiosLink(event)) {
      return "linked";
    }

    return "upcoming";
  }

  private int compareSpaces(FeaturedSpaceRef left, FeaturedSpaceRef right) {
    return firstNonZero(
      compareNumbersDesc(left.featuredScore(), right.featuredScore()),
      compareNumbersDesc(left.rating(), right.rating()),
      compareNumbersDesc(left.reviewsCount(), right.reviewsCount()),
      textCollator.compare(nullSafeText(left.name()), nullSafeText(right.name())),
      compareNumbersAsc(left.id(), right.id())
    );
  }

  private int compareEvents(FeaturedEventRef left, FeaturedEventRef right, LocalDate today) {
    return firstNonZero(
      compareNumbersDesc(left.featuredScore(), right.featuredScore()),
      compareNumbersAsc(daysUntil(left.eventDate(), today), daysUntil(right.eventDate(), today)),
      compareTimesAsc(left.startTime(), right.startTime()),
      compareNumbersAsc(left.id(), right.id())
    );
  }

  private int compareRecruitments(FeaturedRecruitmentRef left, FeaturedRecruitmentRef right) {
    return firstNonZero(
      compareNumbersDesc(left.featuredScore(), right.featuredScore()),
      compareDatesDesc(left.publicationDate(), right.publicationDate()),
      compareNumbersDesc(left.id(), right.id())
    );
  }

  private BigDecimal estimateSpacePrice(MusicalSpace musicalSpace) {
    return scheduleDao.findByMusicalSpace(musicalSpace.getId()).stream()
      .map(Schedule::getPrice)
      .filter(Objects::nonNull)
      .min(Comparator.naturalOrder())
      .map(price -> price.setScale(2, RoundingMode.HALF_UP))
      .orElse(null);
  }

  private double normalizeReviewConfidence(long reviewsCount) {
    double count = Math.max(0, toNumber(reviewsCount));

    if (count == 0) {
      return 0;
    }

    return clamp01(Math.log1p(count) / Math.log1p(SPACE_REVIEW_CONFIDENCE_TARGET));
  }

  private double normalizeEventProximity(Event event, LocalDate today) {
    return 1d / (1 + Math.max(0, daysUntil(event.getEventDate(), today)));
  }

  private double normalizeAccessiblePrice(BigDecimal ticketPrice) {
    if (ticketPrice == null || toNumber(ticketPrice) <= 15) {
      return 1;
    }

    if (toNumber(ticketPrice) <= 30) {
      return 0.5;
    }

    return 0;
  }

  private double normalizeCompleteness(Object... values) {
    if (values.length == 0) {
      return 0;
    }

    long presentValues = Arrays.stream(values)
      .filter(this::hasText)
      .count();

    return (double) presentValues / values.length;
  }

  private double normalizeRecency(LocalDateTime value, LocalDateTime now) {
    if (value == null) {
      return 0;
    }

    long days = Math.max(0, ChronoUnit.DAYS.between(value, now));
    return 1d / (1 + days);
  }

  private boolean isUpcomingPublishedEvent(Event event, LocalDate today, LocalTime nowTime) {
    if (event == null || !EventStatus.PUBLISHED.equals(event.getStatus()) || event.getEventDate() == null) {
      return false;
    }

    if (event.getEventDate().isAfter(today)) {
      return true;
    }

    if (event.getEventDate().isBefore(today)) {
      return false;
    }

    int nowMinutes = toTimeMinutesValue(nowTime);
    Integer endMinutes = toTimeMinutes(event.getEndTime());
    Integer startMinutes = toTimeMinutes(event.getStartTime());

    if (endMinutes != null) {
      return endMinutes >= nowMinutes;
    }

    if (startMinutes != null) {
      return startMinutes >= nowMinutes;
    }

    return true;
  }

  private long daysUntil(LocalDate value, LocalDate today) {
    if (value == null) {
      return Long.MAX_VALUE;
    }

    return Math.max(0, ChronoUnit.DAYS.between(today, value));
  }

  private Integer toTimeMinutes(LocalTime value) {
    if (value == null) {
      return null;
    }

    return value.getHour() * 60 + value.getMinute();
  }

  private int toTimeMinutesValue(LocalTime value) {
    return value == null ? 0 : value.getHour() * 60 + value.getMinute();
  }

  private double toScore(double value) {
    return Math.round(clamp01(value) * 10000d) / 100d;
  }

  private double clamp01(double value) {
    if (!Double.isFinite(value)) {
      return 0;
    }

    return Math.min(1, Math.max(0, value));
  }

  private double toNumber(Number value) {
    return value == null ? 0 : value.doubleValue();
  }

  private boolean hasFiosLink(Event event) {
    return event.getMusicalSpace() != null && event.getMusicalSpace().getId() != null
      || event.getBand() != null && event.getBand().getId() != null;
  }

  private boolean hasText(Object value) {
    if (value instanceof String stringValue) {
      return !stringValue.trim().isEmpty();
    }

    return value != null && !Boolean.FALSE.equals(value);
  }

  private int compareNumbersDesc(Number left, Number right) {
    return Double.compare(toNumber(right), toNumber(left));
  }

  private int compareNumbersAsc(Number left, Number right) {
    return Double.compare(toNumber(left), toNumber(right));
  }

  private int compareDatesDesc(LocalDateTime left, LocalDateTime right) {
    long leftTime = left == null ? 0 : left.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    long rightTime = right == null ? 0 : right.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    return Long.compare(rightTime, leftTime);
  }

  private int compareTimesAsc(LocalTime left, LocalTime right) {
    Integer leftMinutes = left == null ? null : toTimeMinutes(left);
    Integer rightMinutes = right == null ? null : toTimeMinutes(right);

    if (leftMinutes == null && rightMinutes == null) {
      return 0;
    }

    if (leftMinutes == null) {
      return 1;
    }

    if (rightMinutes == null) {
      return -1;
    }

    return leftMinutes.compareTo(rightMinutes);
  }

  private String nullSafeText(String value) {
    return value == null ? "" : value;
  }

  private int firstNonZero(int... values) {
    for (int value : values) {
      if (value != 0) {
        return value;
      }
    }

    return 0;
  }
}
