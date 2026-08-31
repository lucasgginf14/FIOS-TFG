package es.udc.tfg.fios_rest.search.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SearchMapItemView(
  SearchMapItemType type,
  Long id,
  String title,
  String city,
  String province,
  String country,
  String location,
  Double latitude,
  Double longitude,
  LocalDate date,
  LocalTime startTime,
  BigDecimal price,
  String externalUrl
) {
}
