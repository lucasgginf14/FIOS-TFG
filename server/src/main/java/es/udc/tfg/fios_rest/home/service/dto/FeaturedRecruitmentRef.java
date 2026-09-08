package es.udc.tfg.fios_rest.home.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentStatus;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record FeaturedRecruitmentRef(
  Long id,
  String title,
  String description,
  String roleWanted,
  BandRecruitmentLevel levelRequired,
  String city,
  BandRecruitmentStatus status,
  LocalDateTime publicationDate,
  int vacancies,
  BandRef band,
  InstrumentRef instrument,
  UserPublicRef publishedBy,
  String publishedByEmail,
  double featuredScore
) {

  public static FeaturedRecruitmentRef from(BandRecruitment bandRecruitment, double featuredScore) {
    return new FeaturedRecruitmentRef(
      bandRecruitment.getId(),
      bandRecruitment.getTitle(),
      bandRecruitment.getDescription(),
      bandRecruitment.getRoleWanted(),
      bandRecruitment.getLevelRequired(),
      bandRecruitment.getCity(),
      bandRecruitment.getStatus(),
      bandRecruitment.getPublicationDate(),
      bandRecruitment.getVacancies(),
      BandRef.from(bandRecruitment.getBand()),
      InstrumentRef.from(bandRecruitment.getInstrument()),
      UserPublicRef.from(bandRecruitment.getPublishedBy()),
      bandRecruitment.getPublishedBy().getEmail(),
      featuredScore
    );
  }
}
