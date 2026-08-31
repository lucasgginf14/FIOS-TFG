package es.udc.tfg.fios_rest.musicalspace.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;

public record MusicalSpaceRef(
  Long id,
  String name,
  MusicalSpaceType spaceType,
  int capacity,
  String mainImage,
  MusicalSpaceApprovalStatus approvalStatus,
  String city,
  String province,
  boolean active
) {

  public static MusicalSpaceRef from(MusicalSpace musicalSpace) {
    return new MusicalSpaceRef(
      musicalSpace.getId(),
      musicalSpace.getName(),
      musicalSpace.getSpaceType(),
      musicalSpace.getCapacity(),
      musicalSpace.getMainImage(),
      musicalSpace.getApprovalStatus(),
      musicalSpace.getLocation().getCity(),
      musicalSpace.getLocation().getProvince(),
      musicalSpace.isActive()
    );
  }
}
