package es.udc.tfg.fios_rest.bandmember.service.dto;

import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.dto.UserProfileImages;

public record BandMemberCandidateRef(
  Long id,
  String name,
  String firstSurname,
  String secondSurname,
  String email,
  String profileImage
) {

  public static BandMemberCandidateRef from(User user) {
    return new BandMemberCandidateRef(
      user.getId(),
      user.getName(),
      user.getFirstSurname(),
      user.getSecondSurname(),
      user.getEmail(),
      UserProfileImages.visibleProfileImage(user)
    );
  }
}
