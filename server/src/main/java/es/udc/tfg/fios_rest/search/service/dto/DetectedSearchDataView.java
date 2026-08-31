package es.udc.tfg.fios_rest.search.service.dto;

import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.persistence.entity.Search;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record DetectedSearchDataView(
  String detectedCity,
  String detectedProvince,
  String detectedAutonomousCommunity,
  SearchIntent detectedIntent,
  LocalTime detectedStartTime,
  LocalTime detectedEndTime,
  LocalDate detectedDate,
  LocalDate detectedDateFrom,
  LocalDate detectedDateTo,
  SearchNeedType detectedNeedType,
  Integer detectedPeopleCount,
  BigDecimal detectedMaxBudget,
  MusicalSpaceType detectedSpaceType,
  String detectedMusicalGenre
) {

  public static DetectedSearchDataView from(Search search) {
    return new DetectedSearchDataView(
      search.getDetectedCity(),
      null,
      null,
      search.getDetectedIntent(),
      search.getDetectedStartTime(),
      search.getDetectedEndTime(),
      search.getDetectedDate(),
      null,
      null,
      search.getDetectedNeedType(),
      search.getDetectedPeopleCount(),
      search.getDetectedMaxBudget(),
      search.getDetectedSpaceType(),
      search.getDetectedMusicalGenre()
    );
  }

  public static DetectedSearchDataView from(SearchCriteria criteria, SearchIntent intent) {
    return new DetectedSearchDataView(
      criteria.city(),
      criteria.province(),
      criteria.autonomousCommunity(),
      intent,
      criteria.startTime(),
      criteria.endTime(),
      criteria.date(),
      criteria.dateFrom(),
      criteria.dateTo(),
      null,
      criteria.peopleCount(),
      criteria.maxBudget(),
      criteria.spaceType(),
      criteria.musicalGenre()
    );
  }

  public static DetectedSearchDataView from(AiSearchParseResult result) {
    return new DetectedSearchDataView(
      result.city(),
      result.province(),
      result.autonomousCommunity(),
      result.intent(),
      result.startTime(),
      result.endTime(),
      result.date(),
      result.dateFrom(),
      result.dateTo(),
      result.needType(),
      result.peopleCount(),
      result.maxBudget(),
      result.spaceType(),
      result.musicalGenre()
    );
  }
}
