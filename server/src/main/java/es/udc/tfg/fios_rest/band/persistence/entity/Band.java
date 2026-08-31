package es.udc.tfg.fios_rest.band.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "band",
  indexes = {
    @Index(name = "idx_band_name", columnList = "name"),
    @Index(name = "idx_band_base_city", columnList = "base_city"),
    @Index(name = "idx_band_active", columnList = "active")
  }
)
public class Band {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "description", length = 1000)
  private String description;

  @Column(name = "main_genre", nullable = false, length = 100)
  private String mainGenre;

  @Column(name = "base_city", nullable = false, length = 120)
  private String baseCity;

  @Column(name = "creation_date", nullable = false, updatable = false)
  private LocalDateTime creationDate;

  @Column(name = "image", length = 500)
  private String image;

  @Column(name = "active", nullable = false)
  private boolean active;

  protected Band() {
    // Constructor vacio requerido por JPA
  }

  public Band(String name, String description, String mainGenre, String baseCity, String image) {
    this.name = normalizeRequired(name, "name");
    this.description = normalizeNullable(description);
    this.mainGenre = normalizeRequired(mainGenre, "main genre");
    this.baseCity = normalizeRequired(baseCity, "base city");
    this.image = normalizeNullable(image);
    this.active = true;
  }

  @PrePersist
  protected void onCreate() {
    if (creationDate == null) {
      creationDate = LocalDateTime.now();
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

  public String getMainGenre() {
    return mainGenre;
  }

  public void setMainGenre(String mainGenre) {
    this.mainGenre = normalizeRequired(mainGenre, "main genre");
  }

  public String getBaseCity() {
    return baseCity;
  }

  public void setBaseCity(String baseCity) {
    this.baseCity = normalizeRequired(baseCity, "base city");
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = normalizeNullable(image);
  }

  public boolean isActive() {
    return active;
  }

  public void deactivate() {
    this.active = false;
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
    if (!(o instanceof Band other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
