package es.udc.tfg.fios_rest.integration.ai.searchparser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiSearchParsePayload(
  String intent,
  String city,
  String province,
  String autonomousCommunity,
  String date,
  String dateFrom,
  String dateTo,
  String startTime,
  String endTime,
  Integer peopleCount,
  BigDecimal maxBudget,
  String spaceType,
  String musicalGenre,
  String needType,
  Double confidence
) {
  public GeminiSearchParsePayload(
    String intent,
    String city,
    String date,
    String startTime,
    String endTime,
    Integer peopleCount,
    BigDecimal maxBudget,
    String spaceType,
    String musicalGenre,
    String needType,
    Double confidence
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
      confidence
    );
  }
}
