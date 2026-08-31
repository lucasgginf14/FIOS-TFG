package es.udc.tfg.fios_rest.search.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record NaturalLanguageSearchRequest(
  @NotBlank
  @Size(max = 1000)
  String text,

  @Size(max = 120)
  String city,

  @Size(max = 120)
  String province,

  @Size(max = 120)
  String autonomousCommunity,

  LocalDate date,
  LocalDate dateFrom,
  LocalDate dateTo,
  LocalTime startTime,
  LocalTime endTime,
  MusicalSpaceType spaceType,

  @Positive
  Integer peopleCount,

  @Size(max = 100)
  String musicalGenre,

  @PositiveOrZero
  BigDecimal maxBudget
) {
}
