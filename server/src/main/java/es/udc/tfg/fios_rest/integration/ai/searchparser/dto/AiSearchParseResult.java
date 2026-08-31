package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record AiSearchParseResult(
  SearchIntent intent,
  String city,
  String province,
  String autonomousCommunity,
  LocalDate date,
  LocalDate dateFrom,
  LocalDate dateTo,
  LocalTime startTime,
  LocalTime endTime,
  Integer peopleCount,
  BigDecimal maxBudget,
  MusicalSpaceType spaceType,
  String musicalGenre,
  SearchNeedType needType,
  Double confidence,
  String rawProvider,
  String usedProvider
) {
  public AiSearchParseResult(
    SearchIntent intent,
    String city,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    Integer peopleCount,
    BigDecimal maxBudget,
    MusicalSpaceType spaceType,
    String musicalGenre,
    SearchNeedType needType,
    Double confidence,
    String rawProvider,
    String usedProvider
  ) {
    this(
      intent,
      city,
      null,
      null,
      date,
      null,
      null,
      startTime,
      endTime,
      peopleCount,
      maxBudget,
      spaceType,
      musicalGenre,
      needType,
      confidence,
      rawProvider,
      usedProvider
    );
  }
}
