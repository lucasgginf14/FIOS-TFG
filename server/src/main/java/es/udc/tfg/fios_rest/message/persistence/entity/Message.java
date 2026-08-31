package es.udc.tfg.fios_rest.message.persistence.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "message",
  indexes = {
    @Index(name = "idx_message_reservation", columnList = "reservation_session_id"),
    @Index(name = "idx_message_user", columnList = "user_id"),
    @Index(name = "idx_message_datetime", columnList = "date_time"),
    @Index(name = "idx_message_read", columnList = "read")
  }
)
public class Message {

  private static final int MAX_CONTENT_LENGTH = 2000;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "content", nullable = false, length = 2000)
  private String content;

  @Column(name = "date_time", nullable = false, updatable = false)
  private LocalDateTime dateTime;

  @Column(name = "read", nullable = false)
  private boolean read;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reservation_session_id", nullable = false)
  private ReservationSession reservationSession;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  protected Message() {
    // Constructor vacio requerido por JPA
  }

  public Message(String content, ReservationSession reservationSession, User user) {
    this.content = normalizeRequired(content);
    this.reservationSession = Objects.requireNonNull(reservationSession, "reservationSession no puede ser null");
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.read = false;
  }

  @PrePersist
  protected void onCreate() {
    if (dateTime == null) {
      dateTime = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = normalizeRequired(content);
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public boolean isRead() {
    return read;
  }

  public void markAsRead() {
    this.read = true;
  }

  public ReservationSession getReservationSession() {
    return reservationSession;
  }

  public User getUser() {
    return user;
  }

  private String normalizeRequired(String value) {
    if (value == null) {
      throw new IllegalArgumentException("The message content is obligatory");
    }
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("The message content is obligatory");
    }
    if (normalized.length() > MAX_CONTENT_LENGTH) {
      throw new IllegalArgumentException("The message content cannot exceed 2000 characters");
    }
    return normalized;
  }
}
