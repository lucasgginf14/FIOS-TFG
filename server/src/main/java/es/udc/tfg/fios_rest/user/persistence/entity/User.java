package es.udc.tfg.fios_rest.user.persistence.entity;

import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
  name = "users",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_users_phone", columnNames = "phone")
  },
  indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_active", columnList = "active"),
    @Index(name = "idx_users_role", columnList = "platform_role")
  }
)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "first_surname", nullable = false, length = 100)
  private String firstSurname;

  @Column(name = "second_surname", length = 100)
  private String secondSurname;

  @Column(name = "email", nullable = false, length = 150)
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "profile_image", length = 500)
  private String profileImage;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Enumerated(EnumType.STRING)
  @Column(name = "platform_role", nullable = false, length = 20)
  private PlatformRole platformRole;

  @ManyToMany
  @JoinTable(
    name = "user_instrument",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "instrument_id"),
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_user_instrument_user", columnNames = "user_id")
    }
  )
  private Set<Instrument> instruments = new HashSet<>();

  protected User() {
    // Constructor vacío requerido por JPA
  }

  public User(
    String name,
    String firstSurname,
    String secondSurname,
    String email,
    String password,
    String phone,
    String profileImage,
    LocalDate birthDate,
    boolean active,
    PlatformRole platformRole
  ) {
    this.name = normalize(name);
    this.firstSurname = normalize(firstSurname);
    this.secondSurname = normalizeNullable(secondSurname);
    this.email = normalizeEmail(email);
    this.password = password;
    this.phone = normalizeNullable(phone);
    this.profileImage = normalizeNullable(profileImage);
    this.birthDate = birthDate;
    this.active = active;
    this.platformRole = platformRole;
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (platformRole == null) {
      platformRole = PlatformRole.USER;
    }
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

  public String getFirstSurname() {
    return firstSurname;
  }

  public void setFirstSurname(String firstSurname) {
    this.firstSurname = normalize(firstSurname);
  }

  public String getSecondSurname() {
    return secondSurname;
  }

  public void setSecondSurname(String secondSurname) {
    this.secondSurname = normalizeNullable(secondSurname);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = normalizeEmail(email);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = Objects.requireNonNull(password, "password no puede ser null");
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = normalizeNullable(phone);
  }

  public String getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(String profileImage) {
    this.profileImage = normalizeNullable(profileImage);
  }

  public void removeProfileImage() {
    this.profileImage = null;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public boolean isActive() {
    return active;
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public PlatformRole getPlatformRole() {
    return platformRole;
  }

  public void setPlatformRole(PlatformRole platformRole) {
    this.platformRole = Objects.requireNonNull(platformRole, "platformRole no puede ser null");
  }

  public boolean isAdmin() {
    return PlatformRole.ADMIN.equals(platformRole);
  }

  public Set<Instrument> getInstruments() {
    return instruments;
  }

  public Instrument getPrimaryInstrument() {
    return instruments.stream()
      .min(Comparator
        .comparing(Instrument::getName, String.CASE_INSENSITIVE_ORDER)
        .thenComparing(Instrument::getId, Comparator.nullsLast(Long::compareTo)))
      .orElse(null);
  }

  public void setPrimaryInstrument(Instrument instrument) {
    this.instruments.clear();

    if (instrument != null) {
      this.instruments.add(instrument);
    }
  }

  public void setInstruments(Set<Instrument> instruments) {
    this.instruments.clear();
    if (instruments != null) {
      this.instruments.addAll(instruments);
    }
  }

  private String normalize(String value) {
    Objects.requireNonNull(value, "El valor no puede ser null");
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("El valor no puede estar vacío");
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

  private String normalizeEmail(String value) {
    return normalize(value).toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
