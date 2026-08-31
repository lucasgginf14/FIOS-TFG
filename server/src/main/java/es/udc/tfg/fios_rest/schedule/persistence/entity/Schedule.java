package es.udc.tfg.fios_rest.schedule.persistence.entity;

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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
  name = "schedule",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_schedule_space_day_time_range",
      columnNames = {"musical_space_id", "day_of_week", "start_time", "end_time"}
    )
  },
  indexes = {
    @Index(name = "idx_schedule_musical_space", columnList = "musical_space_id"),
    @Index(name = "idx_schedule_day_of_week", columnList = "day_of_week"),
    @Index(name = "idx_schedule_time_range", columnList = "start_time,end_time")
  }
)
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", nullable = false, length = 20)
  private DayOfWeek dayOfWeek;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  protected Schedule() {
    // Constructor vacio requerido por JPA
  }

  public Schedule(
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal price,
    MusicalSpace musicalSpace
  ) {
    this.dayOfWeek = Objects.requireNonNull(dayOfWeek, "dayOfWeek no puede ser null");
    setTimeRange(startTime, endTime);
    setPrice(price);
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public Long getId() {
    return id;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = Objects.requireNonNull(dayOfWeek, "dayOfWeek no puede ser null");
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

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    Objects.requireNonNull(price, "price no puede ser null");

    if (price.signum() <= 0) {
      throw new IllegalArgumentException("The price must be greater than zero");
    }

    this.price = price;
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public void setMusicalSpace(MusicalSpace musicalSpace) {
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public boolean overlaps(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
    return this.dayOfWeek.equals(dayOfWeek)
      && this.startTime.isBefore(endTime)
      && startTime.isBefore(this.endTime);
  }

  public boolean hasSameSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
    return this.dayOfWeek.equals(dayOfWeek)
      && this.startTime.equals(startTime)
      && this.endTime.equals(endTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Schedule other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
