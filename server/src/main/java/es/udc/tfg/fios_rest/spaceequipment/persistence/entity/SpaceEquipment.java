package es.udc.tfg.fios_rest.spaceequipment.persistence.entity;

import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(
  name = "space_equipment",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_space_equipment_space_equipment",
      columnNames = {"musical_space_id", "equipment_id"}
    )
  },
  indexes = {
    @Index(name = "idx_space_equipment_space", columnList = "musical_space_id"),
    @Index(name = "idx_space_equipment_equipment", columnList = "equipment_id"),
    @Index(name = "idx_space_equipment_state", columnList = "state")
  }
)
public class SpaceEquipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 40)
  private SpaceEquipmentState state;

  @Column(name = "observations", length = 1000)
  private String observations;

  @ManyToOne(optional = false)
  @JoinColumn(name = "musical_space_id", nullable = false)
  private MusicalSpace musicalSpace;

  @ManyToOne(optional = false)
  @JoinColumn(name = "equipment_id", nullable = false)
  private Equipment equipment;

  protected SpaceEquipment() {
    // Constructor vacio requerido por JPA
  }

  public SpaceEquipment(
    int quantity,
    SpaceEquipmentState state,
    String observations,
    MusicalSpace musicalSpace,
    Equipment equipment
  ) {
    setQuantity(quantity);
    this.state = Objects.requireNonNull(state, "state no puede ser null");
    this.observations = normalizeNullable(observations);
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
    this.equipment = Objects.requireNonNull(equipment, "equipment no puede ser null");
  }

  public Long getId() {
    return id;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    if (quantity < 1) {
      throw new IllegalArgumentException("The quantity must be greater than zero");
    }
    this.quantity = quantity;
  }

  public SpaceEquipmentState getState() {
    return state;
  }

  public void setState(SpaceEquipmentState state) {
    this.state = Objects.requireNonNull(state, "state no puede ser null");
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = normalizeNullable(observations);
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public void setMusicalSpace(MusicalSpace musicalSpace) {
    this.musicalSpace = Objects.requireNonNull(musicalSpace, "musicalSpace no puede ser null");
  }

  public Equipment getEquipment() {
    return equipment;
  }

  public void setEquipment(Equipment equipment) {
    this.equipment = Objects.requireNonNull(equipment, "equipment no puede ser null");
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
    if (!(o instanceof SpaceEquipment other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
