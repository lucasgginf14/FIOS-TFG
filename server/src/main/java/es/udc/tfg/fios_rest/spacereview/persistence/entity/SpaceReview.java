package es.udc.tfg.fios_rest.spacereview.persistence.entity;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "space_review",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_space_review_reservation", columnNames = "reservation_session_id")
  },
  indexes = {
    @Index(name = "idx_space_review_space", columnList = "musical_space_id"),
    @Index(name = "idx_space_review_user", columnList = "user_id"),
    @Index(name = "idx_space_review_created_at", columnList = "created_at")
  }
)
public class SpaceReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "overall_rating", nullable = false)
  private int overallRating;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "cleanliness_rating", nullable = false)
  private int cleanlinessRating;

  @Column(name = "sound_quality_rating", nullable = false)
  private int soundQualityRating;

  @Column(name = "equipment_rating", nullable = false)
  private int equipmentRating;

  @Column(name = "location_rating", nullable = false)
  private int locationRating;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToOne(optional = false)
  @JoinColumn(name = "reservation_session_id", nullable = false)
  private ReservationSession reservationSession;

  protected SpaceReview() {
    // Constructor vacio requerido por JPA
  }

  public SpaceReview(
    String comment,
    int overallRating,
    int cleanlinessRating,
    int soundQualityRating,
    int equipmentRating,
    int locationRating,
    MusicalSpace musicalSpace,
    User user,
    ReservationSession reservationSession
  ) {
    this.comment = normalizeNullable(comment);
    this.overallRating = overallRating;
    this.cleanlinessRating = cleanlinessRating;
    this.soundQualityRating = soundQualityRating;
    this.equipmentRating = equipmentRating;
    this.locationRating = locationRating;
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.reservationSession = Objects.requireNonNull(reservationSession, "reservationSession no puede ser null");
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getComment() {
    return comment;
  }

  public int getOverallRating() {
    return overallRating;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public int getCleanlinessRating() {
    return cleanlinessRating;
  }

  public int getSoundQualityRating() {
    return soundQualityRating;
  }

  public int getEquipmentRating() {
    return equipmentRating;
  }

  public int getLocationRating() {
    return locationRating;
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public User getUser() {
    return user;
  }

  public ReservationSession getReservationSession() {
    return reservationSession;
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
    if (!(o instanceof SpaceReview other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
