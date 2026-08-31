package es.udc.tfg.fios_rest.search.service.dto;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SearchResultEventRef(
  Long id,
  String title,
  LocalDate eventDate,
  LocalTime startTime,
  LocalTime endTime,
  String musicalGenre,
  EventType eventType,
  EventSource source,
  String venueName,
  String city,
  String province,
  String country,
  String location,
  Double latitude,
  Double longitude,
  Integer capacity,
  BigDecimal ticketPrice,
  String posterImage,
  String externalUrl
) {

  public static SearchResultEventRef from(Event event) {
    return new SearchResultEventRef(
      event.getId(),
      event.getTitle(),
      event.getEventDate(),
      event.getStartTime(),
      event.getEndTime(),
      event.getMusicalGenre(),
      event.getEventType(),
      event.getSource(),
      event.getVenueName(),
      event.getCity(),
      event.getProvince(),
      event.getCountry(),
      event.getLocation(),
      event.getLatitude(),
      event.getLongitude(),
      event.getCapacity(),
      event.getTicketPrice(),
      event.getPosterImage(),
      event.getExternalUrl()
    );
  }
}
