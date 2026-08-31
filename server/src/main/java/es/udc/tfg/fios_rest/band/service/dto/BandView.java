package es.udc.tfg.fios_rest.band.service.dto;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;

import java.time.LocalDateTime;

public record BandView(
  Long id,
  String name,
  String description,
  String mainGenre,
  String baseCity,
  LocalDateTime creationDate,
  String image,
  boolean active
) {

  public static BandView from(Band band) {
    return new BandView(
      band.getId(),
      band.getName(),
      band.getDescription(),
      band.getMainGenre(),
      band.getBaseCity(),
      band.getCreationDate(),
      band.getImage(),
      band.isActive()
    );
  }
}
