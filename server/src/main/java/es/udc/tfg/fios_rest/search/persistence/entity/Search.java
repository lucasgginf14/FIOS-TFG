package es.udc.tfg.fios_rest.search.persistence.entity;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
  name = "search_entry",
  indexes = {
    @Index(name = "idx_search_entry_user", columnList = "user_id"),
    @Index(name = "idx_search_entry_date", columnList = "search_date"),
    @Index(name = "idx_search_entry_city", columnList = "detected_city"),
    @Index(name = "idx_search_entry_intent", columnList = "detected_intent"),
    @Index(name = "idx_search_entry_detected_date", columnList = "detected_date")
  }
)
public class Search {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_text", length = 1000)
  private String originalText;

  @Column(name = "search_date", nullable = false, updatable = false)
  private LocalDateTime searchDate;

  @Column(name = "detected_city", length = 120)
  private String detectedCity;

  @Enumerated(EnumType.STRING)
  @Column(name = "detected_intent", length = 20)
  private SearchIntent detectedIntent;

  @Column(name = "detected_start_time")
  private LocalTime detectedStartTime;

  @Column(name = "detected_end_time")
  private LocalTime detectedEndTime;

  @Column(name = "detected_date")
  private LocalDate detectedDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "detected_need_type", length = 30)
  private SearchNeedType detectedNeedType;

  @Column(name = "detected_people_count")
  private Integer detectedPeopleCount;

  @Column(name = "detected_max_budget", precision = 10, scale = 2)
  private BigDecimal detectedMaxBudget;

  @Enumerated(EnumType.STRING)
  @Column(name = "detected_space_type", length = 40)
  private MusicalSpaceType detectedSpaceType;

  @Column(name = "detected_musical_genre", length = 100)
  private String detectedMusicalGenre;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  protected Search() {
    // Constructor vacio requerido por JPA
  }

  public Search(
    String originalText,
    String detectedCity,
    SearchIntent detectedIntent,
    LocalTime detectedStartTime,
    LocalTime detectedEndTime,
    LocalDate detectedDate,
    SearchNeedType detectedNeedType,
    Integer detectedPeopleCount,
    BigDecimal detectedMaxBudget,
    MusicalSpaceType detectedSpaceType,
    String detectedMusicalGenre,
    User user
  ) {
    this.originalText = normalizeNullable(originalText);
    this.detectedCity = normalizeNullable(detectedCity);
    this.detectedIntent = detectedIntent;
    this.detectedStartTime = detectedStartTime;
    this.detectedEndTime = detectedEndTime;
    this.detectedDate = detectedDate;
    this.detectedNeedType = detectedNeedType;
    this.detectedPeopleCount = detectedPeopleCount;
    this.detectedMaxBudget = detectedMaxBudget;
    this.detectedSpaceType = detectedSpaceType;
    this.detectedMusicalGenre = normalizeNullable(detectedMusicalGenre);
    this.user = Objects.requireNonNull(user, "user no puede ser null");
  }

  @PrePersist
  protected void onCreate() {
    if (searchDate == null) {
      searchDate = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getOriginalText() {
    return originalText;
  }

  public LocalDateTime getSearchDate() {
    return searchDate;
  }

  public String getDetectedCity() {
    return detectedCity;
  }

  public SearchIntent getDetectedIntent() {
    return detectedIntent;
  }

  public LocalTime getDetectedStartTime() {
    return detectedStartTime;
  }

  public LocalTime getDetectedEndTime() {
    return detectedEndTime;
  }

  public LocalDate getDetectedDate() {
    return detectedDate;
  }

  public SearchNeedType getDetectedNeedType() {
    return detectedNeedType;
  }

  public Integer getDetectedPeopleCount() {
    return detectedPeopleCount;
  }

  public BigDecimal getDetectedMaxBudget() {
    return detectedMaxBudget;
  }

  public MusicalSpaceType getDetectedSpaceType() {
    return detectedSpaceType;
  }

  public String getDetectedMusicalGenre() {
    return detectedMusicalGenre;
  }

  public User getUser() {
    return user;
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
    if (!(o instanceof Search other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
