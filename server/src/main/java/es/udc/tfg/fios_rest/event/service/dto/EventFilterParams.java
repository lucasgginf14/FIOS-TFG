package es.udc.tfg.fios_rest.event.service.dto;

import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;

import java.time.LocalDate;

public record EventFilterParams(
  LocalDate date,
  LocalDate dateFrom,
  LocalDate dateTo,
  String city,
  String province,
  String musicalGenre,
  EventSource source,
  EventType eventType,
  EventStatus status,
  Long bandId
) {
  public EventFilterParams(
    LocalDate date,
    String city,
    String musicalGenre,
    EventSource source,
    EventType eventType,
    EventStatus status,
    Long bandId
  ) {
    this(date, null, null, city, null, musicalGenre, source, eventType, status, bandId);
  }

  public EventFilterParams(
    LocalDate date,
    String city,
    String province,
    String musicalGenre,
    EventSource source,
    EventType eventType,
    EventStatus status,
    Long bandId
  ) {
    this(date, null, null, city, province, musicalGenre, source, eventType, status, bandId);
  }
}
