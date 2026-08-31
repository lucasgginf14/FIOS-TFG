package es.udc.tfg.fios_rest.event.service.dto;

import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequest(
  @NotBlank
  @Size(max = 200)
  String title,

  @Size(max = 2000)
  String description,

  @NotNull
  LocalDate eventDate,

  LocalTime startTime,

  LocalTime endTime,

  @Size(max = 100)
  String musicalGenre,

  @Min(1)
  Integer capacity,

  @DecimalMin(value = "0.00")
  BigDecimal ticketPrice,

  @Size(max = 500)
  String posterImage,

  @NotNull
  EventStatus status,

  @NotNull
  EventType eventType,

  @NotNull
  EventSource source,

  @NotBlank
  @Size(max = 200)
  String venueName,

  @DecimalMin(value = "-90.0")
  @DecimalMax(value = "90.0")
  Double latitude,

  @DecimalMin(value = "-180.0")
  @DecimalMax(value = "180.0")
  Double longitude,

  @NotBlank
  @Size(max = 120)
  String city,

  @Size(max = 120)
  String province,

  @NotBlank
  @Size(max = 120)
  String country,

  @Size(max = 300)
  String location,

  @Size(max = 100)
  String externalSource,

  @Size(max = 150)
  String externalId,

  @Size(max = 500)
  String externalUrl,

  @Positive
  Long musicalSpaceId,

  @Positive
  Long bandId
) {
  @AssertTrue(message = "validation.timeRange")
  public boolean isTimeRangeValid() {
    return startTime == null || endTime == null || startTime.isBefore(endTime);
  }

  @AssertTrue(message = "validation.coordinatesPair")
  public boolean isCoordinatesPairValid() {
    return (latitude == null && longitude == null) || (latitude != null && longitude != null);
  }
}
