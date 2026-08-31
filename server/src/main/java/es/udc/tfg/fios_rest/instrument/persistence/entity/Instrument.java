package es.udc.tfg.fios_rest.instrument.persistence.entity;

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
  name = "instrument",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_instrument_name", columnNames = "name")
  },
  indexes = {
    @Index(name = "idx_instrument_name", columnList = "name"),
    @Index(name = "idx_instrument_category", columnList = "category")
  }
)
public class Instrument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 30)
  private InstrumentCategory category;

  protected Instrument() {
    // Constructor vacio requerido por JPA
  }

  public Instrument(String name, InstrumentCategory category) {
    this.name = normalize(name);
    this.category = Objects.requireNonNull(category, "category no puede ser null");
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = normalize(name);
  }

  public InstrumentCategory getCategory() {
    return category;
  }

  public void setCategory(InstrumentCategory category) {
    this.category = Objects.requireNonNull(category, "category no puede ser null");
  }

  private String normalize(String value) {
    Objects.requireNonNull(value, "El valor no puede ser null");
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("El valor no puede estar vacio");
    }
    return normalized;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Instrument other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
