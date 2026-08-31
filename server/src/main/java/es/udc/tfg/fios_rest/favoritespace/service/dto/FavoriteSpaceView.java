package es.udc.tfg.fios_rest.favoritespace.service.dto;

import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;

import java.time.LocalDateTime;

public record FavoriteSpaceView(
  Long id,
  LocalDateTime savedAt,
  MusicalSpaceRef musicalSpace
) {

  public static FavoriteSpaceView from(FavoriteSpace favoriteSpace) {
    return new FavoriteSpaceView(
      favoriteSpace.getId(),
      favoriteSpace.getSavedAt(),
      MusicalSpaceRef.from(favoriteSpace.getMusicalSpace())
    );
  }
}
