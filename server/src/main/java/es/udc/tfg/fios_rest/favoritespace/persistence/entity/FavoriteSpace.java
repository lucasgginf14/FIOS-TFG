package es.udc.tfg.fios_rest.favoritespace.persistence.entity;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "favorite_space",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_favorite_space_user_space", columnNames = {"user_id", "musical_space_id"})
  },
  indexes = {
    @Index(name = "idx_favorite_space_user", columnList = "user_id"),
    @Index(name = "idx_favorite_space_musical_space", columnList = "musical_space_id"),
    @Index(name = "idx_favorite_space_saved_at", columnList = "saved_at")
  }
)
public class FavoriteSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "saved_at", nullable = false, updatable = false)
  private LocalDateTime savedAt;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  protected FavoriteSpace() {
    // Constructor vacio requerido por JPA
  }

  public FavoriteSpace(User user, MusicalSpace musicalSpace) {
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  @PrePersist
  protected void onCreate() {
    if (savedAt == null) {
      savedAt = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getSavedAt() {
    return savedAt;
  }

  public User getUser() {
    return user;
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FavoriteSpace other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
