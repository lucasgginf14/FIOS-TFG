package es.udc.tfg.fios_rest.search.service.dto;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SearchCriteria(
  String city,
  String province,
  String autonomousCommunity,
  LocalDate date,
  LocalDate dateFrom,
  LocalDate dateTo,
  LocalTime startTime,
  LocalTime endTime,
  MusicalSpaceType spaceType,
  Integer peopleCount,
  String musicalGenre,
  BigDecimal maxBudget
) {
  public SearchCriteria(
    String city,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    MusicalSpaceType spaceType,
    Integer peopleCount,
    String musicalGenre,
    BigDecimal maxBudget
  ) {
    this(city, null, null, date, null, null, startTime, endTime, spaceType, peopleCount, musicalGenre, maxBudget);
  }

  public SearchCriteria(
    String city,
    String province,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    MusicalSpaceType spaceType,
    Integer peopleCount,
    String musicalGenre,
    BigDecimal maxBudget
  ) {
    this(city, province, null, date, null, null, startTime, endTime, spaceType, peopleCount, musicalGenre, maxBudget);
  }
}
