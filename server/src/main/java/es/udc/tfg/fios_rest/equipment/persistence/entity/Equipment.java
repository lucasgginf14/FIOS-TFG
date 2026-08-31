package es.udc.tfg.fios_rest.equipment.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(
  name = "equipment",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_equipment_name", columnNames = "name")
  },
  indexes = {
    @Index(name = "idx_equipment_name", columnList = "name"),
    @Index(name = "idx_equipment_category", columnList = "category")
  }
)
public class Equipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 120)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 40)
  private EquipmentCategory category;

  @Column(name = "description", length = 1000)
  private String description;

  protected Equipment() {
    // Constructor vacio requerido por JPA
  }

  public Equipment(String name, EquipmentCategory category, String description) {
    this.name = normalizeRequired(name, "name");
    this.category = Objects.requireNonNull(category, "category no puede ser null");
    this.description = normalizeNullable(description);
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

  public EquipmentCategory getCategory() {
    return category;
  }

  public void setCategory(EquipmentCategory category) {
    this.category = Objects.requireNonNull(category, "category no puede ser null");
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = normalizeNullable(description);
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
    if (!(o instanceof Equipment other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
