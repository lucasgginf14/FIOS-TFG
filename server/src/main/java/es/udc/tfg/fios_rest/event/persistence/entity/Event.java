package es.udc.tfg.fios_rest.event.persistence.entity;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
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
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
  name = "musical_event",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_musical_event_external_source_id",
      columnNames = {"external_source", "external_id"}
    )
  },
  indexes = {
    @Index(name = "idx_musical_event_date", columnList = "event_date"),
    @Index(name = "idx_musical_event_city", columnList = "city"),
    @Index(name = "idx_musical_event_status", columnList = "status"),
    @Index(name = "idx_musical_event_source", columnList = "source"),
    @Index(name = "idx_musical_event_type", columnList = "event_type"),
    @Index(name = "idx_musical_event_space", columnList = "musical_space_id"),
    @Index(name = "idx_musical_event_band", columnList = "band_id"),
    @Index(name = "idx_musical_event_creator", columnList = "created_by_user_id"),
    @Index(name = "idx_musical_event_external", columnList = "external_source, external_id")
  }
)
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", length = 2000)
  private String description;

  @Column(name = "event_date", nullable = false)
  private LocalDate eventDate;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @Column(name = "musical_genre", length = 100)
  private String musicalGenre;

  @Column(name = "capacity")
  private Integer capacity;

  @Column(name = "ticket_price", precision = 10, scale = 2)
  private BigDecimal ticketPrice;

  @Column(name = "poster_image", length = 500)
  private String posterImage;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private EventStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false, length = 30)
  private EventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", nullable = false, length = 20)
  private EventSource source;

  @Column(name = "venue_name", nullable = false, length = 200)
  private String venueName;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "city", nullable = false, length = 120)
  private String city;

  @Column(name = "province", length = 120)
  private String province;

  @Column(name = "country", nullable = false, length = 120)
  private String country;

  @Column(name = "location", length = 300)
  private String location;

  @Column(name = "external_source", length = 100)
  private String externalSource;

  @Column(name = "external_id", length = 150)
  private String externalId;

  @Column(name = "external_url", length = 500)
  private String externalUrl;

  @Column(name = "import_date")
  private LocalDateTime importDate;

  @ManyToOne
  @JoinColumn(name = "musical_space_id")
  private MusicalSpace musicalSpace;

  @ManyToOne
  @JoinColumn(name = "band_id")
  private Band band;

  @ManyToOne(optional = false)
  @JoinColumn(name = "created_by_user_id", nullable = false)
  private User createdBy;

  protected Event() {
    // Constructor vacio requerido por JPA
  }

  public Event(
    String title,
    String description,
    LocalDate eventDate,
    LocalTime startTime,
    LocalTime endTime,
    String musicalGenre,
    Integer capacity,
    BigDecimal ticketPrice,
    String posterImage,
    EventStatus status,
    EventType eventType,
    EventSource source,
    String venueName,
    Double latitude,
    Double longitude,
    String city,
    String province,
    String country,
    String location,
    String externalSource,
    String externalId,
    String externalUrl,
    MusicalSpace musicalSpace,
    Band band,
    User createdBy
  ) {
    setTitle(title);
    setDescription(description);
    setEventDate(eventDate);
    setStartTime(startTime);
    setEndTime(endTime);
    setMusicalGenre(musicalGenre);
    setCapacity(capacity);
    setTicketPrice(ticketPrice);
    setPosterImage(posterImage);
    setStatus(status);
    setEventType(eventType);
    setSource(source);
    setVenueName(venueName);
    setLatitude(latitude);
    setLongitude(longitude);
    setCity(city);
    setProvince(province);
    setCountry(country);
    setLocation(location);
    setExternalSource(externalSource);
    setExternalId(externalId);
    setExternalUrl(externalUrl);
    setMusicalSpace(musicalSpace);
    setBand(band);
    setCreatedBy(createdBy);
    validateTimeRange();
  }

  @PrePersist
  protected void onCreate() {
    if (status == null) {
      status = EventStatus.DRAFT;
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

  public LocalDate getEventDate() {
    return eventDate;
  }

  public void setEventDate(LocalDate eventDate) {
    this.eventDate = Objects.requireNonNull(eventDate, "eventDate no puede ser null");
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
    validateTimeRange();
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
    validateTimeRange();
  }

  public String getMusicalGenre() {
    return musicalGenre;
  }

  public void setMusicalGenre(String musicalGenre) {
    this.musicalGenre = normalizeNullable(musicalGenre);
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    if (capacity != null && capacity < 1) {
      throw new IllegalArgumentException("The capacity must be greater than zero");
    }
    this.capacity = capacity;
  }

  public BigDecimal getTicketPrice() {
    return ticketPrice;
  }

  public void setTicketPrice(BigDecimal ticketPrice) {
    if (ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("The ticket price cannot be negative");
    }
    this.ticketPrice = ticketPrice;
  }

  public String getPosterImage() {
    return posterImage;
  }

  public void setPosterImage(String posterImage) {
    this.posterImage = normalizeNullable(posterImage);
  }

  public EventStatus getStatus() {
    return status;
  }

  public void setStatus(EventStatus status) {
    this.status = Objects.requireNonNull(status, "status no puede ser null");
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = Objects.requireNonNull(eventType, "eventType no puede ser null");
  }

  public EventSource getSource() {
    return source;
  }

  public void setSource(EventSource source) {
    this.source = Objects.requireNonNull(source, "source no puede ser null");
  }

  public String getVenueName() {
    return venueName;
  }

  public void setVenueName(String venueName) {
    this.venueName = normalizeRequired(venueName, "venue name");
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    if (latitude != null && (latitude < -90d || latitude > 90d)) {
      throw new IllegalArgumentException("The latitude must be between -90 and 90");
    }
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    if (longitude != null && (longitude < -180d || longitude > 180d)) {
      throw new IllegalArgumentException("The longitude must be between -180 and 180");
    }
    this.longitude = longitude;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = normalizeRequired(city, "city");
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = normalizeNullable(province);
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = normalizeRequired(country, "country");
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = normalizeNullable(location);
  }

  public String getExternalSource() {
    return externalSource;
  }

  public void setExternalSource(String externalSource) {
    this.externalSource = normalizeNullable(externalSource);
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = normalizeNullable(externalId);
  }

  public String getExternalUrl() {
    return externalUrl;
  }

  public void setExternalUrl(String externalUrl) {
    this.externalUrl = normalizeNullable(externalUrl);
  }

  public LocalDateTime getImportDate() {
    return importDate;
  }

  public void markImportedNow() {
    this.importDate = LocalDateTime.now();
  }

  public void clearImportData() {
    this.externalSource = null;
    this.externalId = null;
    this.externalUrl = null;
    this.importDate = null;
  }

  public MusicalSpace getMusicalSpace() {
    return musicalSpace;
  }

  public void setMusicalSpace(MusicalSpace musicalSpace) {
    this.musicalSpace = musicalSpace;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Band getBand() {
    return band;
  }

  public void setBand(Band band) {
    this.band = band;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = Objects.requireNonNull(createdBy, "createdBy no puede ser null");
  }

  public boolean isPubliclyVisible() {
    return EventStatus.PUBLISHED.equals(status);
  }

  public void archive() {
    this.status = EventStatus.ARCHIVED;
  }

  private void validateTimeRange() {
    if (endTime != null && startTime == null) {
      throw new IllegalArgumentException("The start time is obligatory when the end time is informed");
    }

    if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
      throw new IllegalArgumentException("The start time must be before the end time");
    }
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
    if (!(o instanceof Event other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
