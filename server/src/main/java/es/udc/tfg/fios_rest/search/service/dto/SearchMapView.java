package es.udc.tfg.fios_rest.search.service.dto;

import java.util.List;

public record SearchMapView(
  List<SearchMapItemView> items
) {
}
