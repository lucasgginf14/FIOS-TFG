package es.udc.tfg.fios_rest.event.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventRef(
  Long id,
  String title,
  String description,
  LocalDate eventDate,
  LocalTime startTime,
  LocalTime endTime,
  String musicalGenre,
  Integer capacity,
  EventType eventType,
  EventSource source,
  EventStatus status,
  String venueName,
  String city,
  String province,
  String country,
  BigDecimal ticketPrice,
  String posterImage,
  String externalUrl,
  MusicalSpaceRef musicalSpace,
  BandRef band
) {

  public static EventRef from(Event event) {
    return new EventRef(
      event.getId(),
      event.getTitle(),
      event.getDescription(),
      event.getEventDate(),
      event.getStartTime(),
      event.getEndTime(),
      event.getMusicalGenre(),
      event.getCapacity(),
      event.getEventType(),
      event.getSource(),
      event.getStatus(),
      event.getVenueName(),
      event.getCity(),
      event.getProvince(),
      event.getCountry(),
      event.getTicketPrice(),
      event.getPosterImage(),
      event.getExternalUrl(),
      event.getMusicalSpace() == null ? null : MusicalSpaceRef.from(event.getMusicalSpace()),
      event.getBand() == null ? null : BandRef.from(event.getBand())
    );
  }
}
