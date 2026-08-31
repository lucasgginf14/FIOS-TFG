package es.udc.tfg.fios_rest.musicalspace.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

public record MusicalSpaceView(
  Long id,
  String name,
  String description,
  MusicalSpaceType spaceType,
  int capacity,
  String mainImage,
  MusicalSpaceApprovalStatus approvalStatus,
  double squareMeters,
  boolean soundproofed,
  MusicalSpaceLocationView location,
  UserPublicRef manager,
  boolean active
) {

  public static MusicalSpaceView from(MusicalSpace musicalSpace) {
    return new MusicalSpaceView(
      musicalSpace.getId(),
      musicalSpace.getName(),
      musicalSpace.getDescription(),
      musicalSpace.getSpaceType(),
      musicalSpace.getCapacity(),
      musicalSpace.getMainImage(),
      musicalSpace.getApprovalStatus(),
      musicalSpace.getSquareMeters(),
      musicalSpace.isSoundproofed(),
      MusicalSpaceLocationView.from(musicalSpace.getLocation()),
      UserPublicRef.from(musicalSpace.getManager()),
      musicalSpace.isActive()
    );
  }
}
