package es.udc.tfg.fios_rest.reservationsession.persistence.entity;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
  name = "reservation_session",
  indexes = {
    @Index(name = "idx_reservation_session_space", columnList = "musical_space_id"),
    @Index(name = "idx_reservation_session_user", columnList = "user_id"),
    @Index(name = "idx_reservation_session_band", columnList = "band_id"),
    @Index(name = "idx_reservation_session_date", columnList = "session_date"),
    @Index(name = "idx_reservation_session_state", columnList = "state"),
    @Index(name = "idx_reservation_session_time_range", columnList = "start_time,end_time")
  }
)
public class ReservationSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "attendees_count", nullable = false)
  private int attendeesCount;

  @Enumerated(EnumType.STRING)
  @Column(name = "session_type", nullable = false, length = 40)
  private ReservationSessionType sessionType;

  @Column(name = "observations", length = 1000)
  private String observations;

  @Column(name = "cancellation_reason", length = 1000)
  private String cancellationReason;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal finalPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 40)
  private ReservationSessionState state;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "band_id")
  private Band band;

  protected ReservationSession() {
    // Constructor vacio requerido por JPA
  }

  public ReservationSession(
    LocalDate sessionDate,
    LocalTime startTime,
    LocalTime endTime,
    int attendeesCount,
    ReservationSessionType sessionType,
    String observations,
    BigDecimal finalPrice,
    MusicalSpace musicalSpace,
    User user,
    Band band
  ) {
    this.sessionDate = Objects.requireNonNull(sessionDate, "sessionDate no puede ser null");
    setTimeRange(startTime, endTime);
    setAttendeesCount(attendeesCount);
    this.sessionType = Objects.requireNonNull(sessionType, "sessionType no puede ser null");
    this.observations = normalizeNullable(observations);
    setFinalPrice(finalPrice);
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.band = band;
    this.state = ReservationSessionState.PENDING;
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (state == null) {
      state = ReservationSessionState.PENDING;
    }
  }

  public Long getId() {
    return id;
  }

  public LocalDate getSessionDate() {
    return sessionDate;
  }

  public void setSessionDate(LocalDate sessionDate) {
    this.sessionDate = Objects.requireNonNull(sessionDate, "sessionDate no puede ser null");
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

  public int getAttendeesCount() {
    return attendeesCount;
  }

  public void setAttendeesCount(int attendeesCount) {
    if (attendeesCount < 1) {
      throw new IllegalArgumentException("The attendees count must be greater than zero");
    }
    this.attendeesCount = attendeesCount;
  }

  public ReservationSessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(ReservationSessionType sessionType) {
    this.sessionType = Objects.requireNonNull(sessionType, "sessionType no puede ser null");
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = normalizeNullable(observations);
  }

  public String getCancellationReason() {
    return cancellationReason;
  }

  public void setCancellationReason(String cancellationReason) {
    this.cancellationReason = normalizeNullable(cancellationReason);
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public BigDecimal getFinalPrice() {
    return finalPrice;
  }

  public void setFinalPrice(BigDecimal finalPrice) {
    Objects.requireNonNull(finalPrice, "finalPrice no puede ser null");
    if (finalPrice.signum() <= 0) {
      throw new IllegalArgumentException("The final price must be greater than zero");
    }
    this.finalPrice = finalPrice;
  }

  public ReservationSessionState getState() {
    return state;
  }

  public void setState(ReservationSessionState state) {
    this.state = Objects.requireNonNull(state, "state no puede ser null");
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public void setMusicalSpace(MusicalSpace musicalSpace) {
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public User getUser() {
    return user;
  }

  public Band getBand() {
    return band;
  }

  public void setBand(Band band) {
    this.band = band;
  }

  public boolean overlaps(LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
    return this.sessionDate.equals(sessionDate)
      && this.startTime.isBefore(endTime)
      && startTime.isBefore(this.endTime);
  }

  public boolean isBlockingAvailability() {
    return ReservationSessionState.PENDING.equals(state) || ReservationSessionState.ACCEPTED.equals(state);
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
    if (!(o instanceof ReservationSession other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
