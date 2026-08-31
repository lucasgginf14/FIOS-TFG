package es.udc.tfg.fios_rest.search.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;

import java.util.List;

public record SearchView(
  DetectedSearchDataView appliedFilters,
  List<SearchResultSpaceRef> spaces,
  List<SearchResultEventRef> events,
  List<BandRef> bands,
  List<BandRecruitmentRef> recruitments
) {
}
