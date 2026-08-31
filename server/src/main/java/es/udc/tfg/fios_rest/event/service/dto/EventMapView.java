package es.udc.tfg.fios_rest.event.service.dto;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventMapView(
  Long id,
  String title,
  LocalDate eventDate,
  LocalTime startTime,
  String venueName,
  String city,
  String province,
  String country,
  String location,
  Double latitude,
  Double longitude,
  EventType eventType,
  EventSource source,
  String externalUrl
) {

  public static EventMapView from(Event event) {
    return new EventMapView(
      event.getId(),
      event.getTitle(),
      event.getEventDate(),
      event.getStartTime(),
      event.getVenueName(),
      event.getCity(),
      event.getProvince(),
      event.getCountry(),
      event.getLocation(),
      event.getLatitude(),
      event.getLongitude(),
      event.getEventType(),
      event.getSource(),
      event.getExternalUrl()
    );
  }
}
