package es.udc.tfg.fios_rest.musicalspace.persistence.entity;

import es.udc.tfg.fios_rest.user.persistence.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

import java.util.Objects;

@Entity
@Table(
  name = "musical_space",
  indexes = {
    @Index(name = "idx_musical_space_name", columnList = "name"),
    @Index(name = "idx_musical_space_city", columnList = "city"),
    @Index(name = "idx_musical_space_type", columnList = "space_type"),
    @Index(name = "idx_musical_space_approval_status", columnList = "approval_status"),
    @Index(name = "idx_musical_space_active", columnList = "active"),
    @Index(name = "idx_musical_space_manager", columnList = "manager_user_id")
  }
)
public class MusicalSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "description", length = 1500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "space_type", nullable = false, length = 40)
  private MusicalSpaceType spaceType;

  @Column(name = "capacity", nullable = false)
  private int capacity;

  @Column(name = "main_image", length = 500)
  private String mainImage;

  @Enumerated(EnumType.STRING)
  @Column(name = "approval_status", nullable = false, length = 30)
  private MusicalSpaceApprovalStatus approvalStatus;

  @Column(name = "square_meters", nullable = false)
  private double squareMeters;

  @Column(name = "soundproofed", nullable = false)
  private boolean soundproofed;

  @Embedded
  private MusicalSpaceLocation location;

  @ManyToOne(optional = false)
  @JoinColumn(name = "manager_user_id", nullable = false)
  private User manager;

  @Column(name = "active", nullable = false)
  private boolean active;

  protected MusicalSpace() {
    // Constructor vacio requerido por JPA
  }

  public MusicalSpace(
    String name,
    String description,
    MusicalSpaceType spaceType,
    int capacity,
    String mainImage,
    double squareMeters,
    boolean soundproofed,
    MusicalSpaceLocation location,
    User manager
  ) {
    this.name = normalizeRequired(name, "name");
    this.description = normalizeNullable(description);
    this.spaceType = Objects.requireNonNull(spaceType, "spaceType no puede ser null");
    setCapacity(capacity);
    this.mainImage = normalizeNullable(mainImage);
    this.approvalStatus = MusicalSpaceApprovalStatus.PENDING;
    setSquareMeters(squareMeters);
    this.soundproofed = soundproofed;
    this.location = Objects.requireNonNull(location, "location no puede ser null");
    this.manager = Objects.requireNonNull(manager, "manager no puede ser null");
    this.active = true;
  }

  @PrePersist
  protected void onCreate() {
    if (approvalStatus == null) {
      approvalStatus = MusicalSpaceApprovalStatus.PENDING;
    }
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = normalizeRequired(name, "name");
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = normalizeNullable(description);
  }

  public MusicalSpaceType getSpaceType() {
    return spaceType;
  }

  public void setSpaceType(MusicalSpaceType spaceType) {
    this.spaceType = Objects.requireNonNull(spaceType, "spaceType no puede ser null");
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException("The capacity must be greater than zero");
    }
    this.capacity = capacity;
  }

  public String getMainImage() {
    return mainImage;
  }

  public void setMainImage(String mainImage) {
    this.mainImage = normalizeNullable(mainImage);
  }

  public MusicalSpaceApprovalStatus getApprovalStatus() {
    return approvalStatus;
  }

  public void setApprovalStatus(MusicalSpaceApprovalStatus approvalStatus) {
    this.approvalStatus = Objects.requireNonNull(approvalStatus, "approvalStatus no puede ser null");
  }

  public double getSquareMeters() {
    return squareMeters;
  }

  public void setSquareMeters(double squareMeters) {
    if (squareMeters <= 0) {
      throw new IllegalArgumentException("The square meters value must be greater than zero");
    }
    this.squareMeters = squareMeters;
  }

  public boolean isSoundproofed() {
    return soundproofed;
  }

  public void setSoundproofed(boolean soundproofed) {
    this.soundproofed = soundproofed;
  }

  public MusicalSpaceLocation getLocation() {
    return location;
  }

  public void setLocation(MusicalSpaceLocation location) {
    this.location = Objects.requireNonNull(location, "location no puede ser null");
  }

  public User getManager() {
    return manager;
  }

  public void setManager(User manager) {
    this.manager = Objects.requireNonNull(manager, "manager no puede ser null");
  }

  public boolean isActive() {
    return active;
  }

  public boolean isPubliclyVisible() {
    return active && MusicalSpaceApprovalStatus.APPROVED.equals(approvalStatus);
  }

  public void deactivate() {
    active = false;
  }

  private String normalizeRequired(String value, String fieldName) {
    Objects.requireNonNull(value, fieldName + " no puede ser null");
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("The " + fieldName + " is obligatory");
    }
    return normalized;
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
    if (!(o instanceof MusicalSpace other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
