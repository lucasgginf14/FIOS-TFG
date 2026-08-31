package es.udc.tfg.fios_rest.search.service.dto;

import es.udc.tfg.fios_rest.search.persistence.entity.Search;

import java.time.LocalDateTime;

public record SearchHistoryView(
  Long id,
  String originalText,
  LocalDateTime searchDate,
  DetectedSearchDataView detectedData
) {

  public static SearchHistoryView from(Search search) {
    return new SearchHistoryView(
      search.getId(),
      search.getOriginalText(),
      search.getSearchDate(),
      DetectedSearchDataView.from(search)
    );
  }
}
