package es.udc.tfg.fios_rest.bandrecruitment.persistence.entity;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
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
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "band_recruitment",
  indexes = {
    @Index(name = "idx_band_recruitment_band", columnList = "band_id"),
    @Index(name = "idx_band_recruitment_instrument", columnList = "instrument_id"),
    @Index(name = "idx_band_recruitment_published_by", columnList = "published_by_user_id"),
    @Index(name = "idx_band_recruitment_status", columnList = "status"),
    @Index(name = "idx_band_recruitment_city", columnList = "city"),
    @Index(name = "idx_band_recruitment_publication_date", columnList = "publication_date")
  }
)
public class BandRecruitment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, length = 150)
  private String title;

  @Column(name = "description", length = 1500)
  private String description;

  @Column(name = "role_wanted", nullable = false, length = 120)
  private String roleWanted;

  @Enumerated(EnumType.STRING)
  @Column(name = "level_required", nullable = false, length = 30)
  private BandRecruitmentLevel levelRequired;

  @Column(name = "city", nullable = false, length = 120)
  private String city;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private BandRecruitmentStatus status;

  @Column(name = "publication_date", nullable = false, updatable = false)
  private LocalDateTime publicationDate;

  @Column(name = "vacancies", nullable = false)
  private int vacancies;

  @ManyToOne(optional = false)
  @JoinColumn(name = "band_id", nullable = false)
  private Band band;

  @ManyToOne(optional = false)
  @JoinColumn(name = "instrument_id", nullable = false)
  private Instrument instrument;

  @ManyToOne(optional = false)
  @JoinColumn(name = "published_by_user_id", nullable = false)
  private User publishedBy;

  protected BandRecruitment() {
    // Constructor vacio requerido por JPA
  }

  public BandRecruitment(
    String title,
    String description,
    String roleWanted,
    BandRecruitmentLevel levelRequired,
    String city,
    int vacancies,
    Band band,
    Instrument instrument,
    User publishedBy
  ) {
    this.title = normalizeRequired(title, "title");
    this.description = normalizeNullable(description);
    this.roleWanted = normalizeRequired(roleWanted, "role wanted");
    this.levelRequired = Objects.requireNonNull(levelRequired, "levelRequired no puede ser null");
    this.city = normalizeRequired(city, "city");
    setVacancies(vacancies);
    this.band = Objects.requireNonNull(band, "band no puede ser null");
    this.instrument = Objects.requireNonNull(instrument, "instrument no puede ser null");
    this.publishedBy = Objects.requireNonNull(publishedBy, "publishedBy no puede ser null");
    this.status = BandRecruitmentStatus.OPEN;
  }

  @PrePersist
  protected void onCreate() {
    if (publicationDate == null) {
      publicationDate = LocalDateTime.now();
    }
    if (status == null) {
      status = BandRecruitmentStatus.OPEN;
    }
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = normalizeRequired(title, "title");
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = normalizeNullable(description);
  }

  public String getRoleWanted() {
    return roleWanted;
  }

  public void setRoleWanted(String roleWanted) {
    this.roleWanted = normalizeRequired(roleWanted, "role wanted");
  }

  public BandRecruitmentLevel getLevelRequired() {
    return levelRequired;
  }

  public void setLevelRequired(BandRecruitmentLevel levelRequired) {
    this.levelRequired = Objects.requireNonNull(levelRequired, "levelRequired no puede ser null");
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = normalizeRequired(city, "city");
  }

  public BandRecruitmentStatus getStatus() {
    return status;
  }

  public LocalDateTime getPublicationDate() {
    return publicationDate;
  }

  public int getVacancies() {
    return vacancies;
  }

  public void setVacancies(int vacancies) {
    if (vacancies < 1) {
      throw new IllegalArgumentException("The vacancies value must be greater than zero");
    }
    this.vacancies = vacancies;
  }

  public Band getBand() {
    return band;
  }

  public void setBand(Band band) {
    this.band = Objects.requireNonNull(band, "band no puede ser null");
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = Objects.requireNonNull(instrument, "instrument no puede ser null");
  }

  public User getPublishedBy() {
    return publishedBy;
  }

  public void setPublishedBy(User publishedBy) {
    this.publishedBy = Objects.requireNonNull(publishedBy, "publishedBy no puede ser null");
  }

  public boolean isOpen() {
    return BandRecruitmentStatus.OPEN.equals(status);
  }

  public void close() {
    this.status = BandRecruitmentStatus.CLOSED;
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
    if (!(o instanceof BandRecruitment other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
