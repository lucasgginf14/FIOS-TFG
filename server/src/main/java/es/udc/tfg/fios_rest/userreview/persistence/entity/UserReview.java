package es.udc.tfg.fios_rest.userreview.persistence.entity;

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
  name = "user_review",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_review_reservation", columnNames = "reservation_session_id")
  },
  indexes = {
    @Index(name = "idx_user_review_reviewer", columnList = "reviewer_user_id"),
    @Index(name = "idx_user_review_reviewed", columnList = "reviewed_user_id"),
    @Index(name = "idx_user_review_space", columnList = "musical_space_id"),
    @Index(name = "idx_user_review_created_at", columnList = "created_at")
  }
)
public class UserReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "comment", length = 1000)
  private String comment;

  @Column(name = "overall_rating", nullable = false)
  private int overallRating;

  @Column(name = "communication_rating", nullable = false)
  private int communicationRating;

  @Column(name = "punctuality_rating", nullable = false)
  private int punctualityRating;

  @Column(name = "care_rating", nullable = false)
  private int careRating;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reviewer_user_id", nullable = false)
  private User reviewer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reviewed_user_id", nullable = false)
  private User reviewedUser;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  @OneToOne(optional = false)
  @JoinColumn(name = "reservation_session_id", nullable = false)
  private ReservationSession reservationSession;

  protected UserReview() {
    // Constructor vacio requerido por JPA
  }

  public UserReview(
    String comment,
    int overallRating,
    int communicationRating,
    int punctualityRating,
    int careRating,
    User reviewer,
    User reviewedUser,
    MusicalSpace musicalSpace,
    ReservationSession reservationSession
  ) {
    this.comment = normalizeNullable(comment);
    this.overallRating = overallRating;
    this.communicationRating = communicationRating;
    this.punctualityRating = punctualityRating;
    this.careRating = careRating;
    this.reviewer = Objects.requireNonNull(reviewer, "reviewer no puede ser null");
    this.reviewedUser = Objects.requireNonNull(reviewedUser, "reviewedUser no puede ser null");
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
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

  public int getCommunicationRating() {
    return communicationRating;
  }

  public int getPunctualityRating() {
    return punctualityRating;
  }

  public int getCareRating() {
    return careRating;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public User getReviewer() {
    return reviewer;
  }

  public User getReviewedUser() {
    return reviewedUser;
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
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
    if (!(o instanceof UserReview other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
