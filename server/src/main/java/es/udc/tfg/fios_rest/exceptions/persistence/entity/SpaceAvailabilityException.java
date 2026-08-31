package es.udc.tfg.fios_rest.exceptions.persistence.entity;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
  name = "space_availability_exception",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_space_exception_space_date_time_type",
      columnNames = {"musical_space_id", "exception_date", "start_time", "end_time", "exception_type"}
    )
  },
  indexes = {
    @Index(name = "idx_space_exception_musical_space", columnList = "musical_space_id"),
    @Index(name = "idx_space_exception_date", columnList = "exception_date"),
    @Index(name = "idx_space_exception_type", columnList = "exception_type"),
    @Index(name = "idx_space_exception_time_range", columnList = "start_time,end_time")
  }
)
public class SpaceAvailabilityException {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "exception_date", nullable = false)
  private LocalDate date;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "exception_type", nullable = false, length = 40)
  private SpaceAvailabilityExceptionType exceptionType;

  @Column(name = "reason", length = 500)
  private String reason;

  @Column(name = "price", precision = 10, scale = 2)
  private BigDecimal price;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  protected SpaceAvailabilityException() {
    // Constructor vacio requerido por JPA
  }

  public SpaceAvailabilityException(
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    SpaceAvailabilityExceptionType exceptionType,
    String reason,
    MusicalSpace musicalSpace
  ) {
    this(date, startTime, endTime, exceptionType, reason, null, musicalSpace);
  }

  public SpaceAvailabilityException(
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    SpaceAvailabilityExceptionType exceptionType,
    String reason,
    BigDecimal price,
    MusicalSpace musicalSpace
  ) {
    this.date = Objects.requireNonNull(date, "date no puede ser null");
    setTimeRange(startTime, endTime);
    this.exceptionType = Objects.requireNonNull(exceptionType, "exceptionType no puede ser null");
    this.reason = normalizeNullable(reason);
    setPrice(price);
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public Long getId() {
    return id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = Objects.requireNonNull(date, "date no puede ser null");
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setTimeRange(LocalTime startTime, LocalTime endTime) {
    Objects.requireNonNull(startTime, "startTime no puede ser null");
    Objects.requireNonNull(endTime, "endTime no puede ser null");

    if (!startTime.isBefore(endTime)) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }

    this.startTime = startTime;
    this.endTime = endTime;
  }

  public SpaceAvailabilityExceptionType getExceptionType() {
    return exceptionType;
  }

  public void setExceptionType(SpaceAvailabilityExceptionType exceptionType) {
    this.exceptionType = Objects.requireNonNull(exceptionType, "exceptionType no puede ser null");
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = normalizeNullable(reason);
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    if (price == null) {
      this.price = null;
      return;
    }

    if (price.signum() <= 0) {
      throw new IllegalArgumentException("The custom availability price must be greater than zero");
    }

    this.price = price.setScale(2, RoundingMode.HALF_UP);
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public void setMusicalSpace(MusicalSpace musicalSpace) {
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public boolean overlaps(LocalDate date, LocalTime startTime, LocalTime endTime) {
    return this.date.equals(date)
      && this.startTime.isBefore(endTime)
      && startTime.isBefore(this.endTime);
  }

  public boolean hasSameSlot(
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    SpaceAvailabilityExceptionType exceptionType
  ) {
    return this.date.equals(date)
      && this.startTime.equals(startTime)
      && this.endTime.equals(endTime)
      && this.exceptionType.equals(exceptionType);
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SpaceAvailabilityException other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
