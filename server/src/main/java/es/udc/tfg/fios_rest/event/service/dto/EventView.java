package es.udc.tfg.fios_rest.event.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventView(
  Long id,
  String title,
  String description,
  LocalDate eventDate,
  LocalTime startTime,
  LocalTime endTime,
  String musicalGenre,
  Integer capacity,
  Long reservedTickets,
  Integer availableTickets,
  Boolean soldOut,
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
  LocalDateTime importDate,
  MusicalSpaceRef musicalSpace,
  BandRef band,
  UserPublicRef createdBy
) {

  public static EventView from(Event event) {
    return from(event, null);
  }

  public static EventView from(Event event, Long reservedTickets) {
    Integer availableTickets = calculateAvailableTickets(event, reservedTickets);

    return new EventView(
      event.getId(),
      event.getTitle(),
      event.getDescription(),
      event.getEventDate(),
      event.getStartTime(),
      event.getEndTime(),
      event.getMusicalGenre(),
      event.getCapacity(),
      reservedTickets,
      availableTickets,
      availableTickets == null ? null : availableTickets <= 0,
      event.getTicketPrice(),
      event.getPosterImage(),
      event.getStatus(),
      event.getEventType(),
      event.getSource(),
      event.getVenueName(),
      event.getLatitude(),
      event.getLongitude(),
      event.getCity(),
      event.getProvince(),
      event.getCountry(),
      event.getLocation(),
      event.getExternalSource(),
      event.getExternalId(),
      event.getExternalUrl(),
      event.getImportDate(),
      event.getMusicalSpace() == null ? null : MusicalSpaceRef.from(event.getMusicalSpace()),
      event.getBand() == null ? null : BandRef.from(event.getBand()),
      event.getCreatedBy() == null ? null : UserPublicRef.from(event.getCreatedBy())
    );
  }

  private static Integer calculateAvailableTickets(Event event, Long reservedTickets) {
    if (event.getCapacity() == null || reservedTickets == null) {
      return null;
    }

    long availableTickets = event.getCapacity() - reservedTickets;
    return (int) Math.max(availableTickets, 0);
  }
}
