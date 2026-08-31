package es.udc.tfg.fios_rest.eventpurchase.persistence.entity;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "event_purchase",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_event_purchase_active_user_event", columnNames = {"user_id", "event_id", "active"})
  },
  indexes = {
    @Index(name = "idx_event_purchase_user", columnList = "user_id"),
    @Index(name = "idx_event_purchase_event", columnList = "event_id"),
    @Index(name = "idx_event_purchase_purchased_at", columnList = "purchased_at"),
    @Index(name = "idx_event_purchase_state", columnList = "state")
  }
)
public class EventPurchase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "purchased_at", nullable = false, updatable = false)
  private LocalDateTime purchasedAt;

  @Column(name = "price_paid", nullable = false, precision = 10, scale = 2)
  private BigDecimal pricePaid;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 20)
  private EventPurchaseState state;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "active")
  private Boolean active;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  protected EventPurchase() {
    // Constructor vacio requerido por JPA
  }

  public EventPurchase(User user, Event event, BigDecimal amountDue) {
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.event = Objects.requireNonNull(event, "event no puede ser null");
    this.pricePaid = normalizeAmountDue(amountDue);
    this.state = EventPurchaseState.RESERVED;
    syncActiveFlag();
  }

  @PrePersist
  protected void onCreate() {
    if (purchasedAt == null) {
      purchasedAt = LocalDateTime.now();
    }

    if (pricePaid == null) {
      pricePaid = BigDecimal.ZERO;
    }

    if (state == null) {
      state = EventPurchaseState.RESERVED;
    }

    syncActiveFlag();
  }

  @PreUpdate
  protected void onUpdate() {
    syncActiveFlag();
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getPurchasedAt() {
    return purchasedAt;
  }

  public BigDecimal getPricePaid() {
    return pricePaid;
  }

  public BigDecimal getAmountDue() {
    return pricePaid;
  }

  public EventPurchaseState getState() {
    return state;
  }

  public LocalDateTime getCancelledAt() {
    return cancelledAt;
  }

  public boolean isActive() {
    return EventPurchaseState.RESERVED.equals(state);
  }

  public void cancel() {
    if (!isActive()) {
      throw new IllegalStateException("The ticket reservation is already cancelled");
    }

    this.state = EventPurchaseState.CANCELLED;
    this.cancelledAt = LocalDateTime.now();
    syncActiveFlag();
  }

  public User getUser() {
    return user;
  }

  public Event getEvent() {
    return event;
  }

  private BigDecimal normalizeAmountDue(BigDecimal amountDue) {
    if (amountDue == null) {
      return BigDecimal.ZERO;
    }

    if (amountDue.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("The amount due cannot be negative");
    }

    return amountDue;
  }

  private void syncActiveFlag() {
    active = EventPurchaseState.RESERVED.equals(state) ? Boolean.TRUE : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventPurchase other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
