package es.udc.tfg.fios_rest.home.service.dto;

import java.util.List;

public record HomeFeaturedView(
  List<FeaturedSpaceRef> featuredSpaces,
  List<FeaturedEventRef> featuredEvents,
  List<FeaturedRecruitmentRef> featuredRecruitments
) {
}
