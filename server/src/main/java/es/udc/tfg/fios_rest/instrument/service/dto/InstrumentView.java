package es.udc.tfg.fios_rest.instrument.service.dto;

import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;

public record InstrumentView(
  Long id,
  String name,
  InstrumentCategory category
) {

  public static InstrumentView from(Instrument instrument) {
    return new InstrumentView(
      instrument.getId(),
      instrument.getName(),
      instrument.getCategory()
    );
  }
}
