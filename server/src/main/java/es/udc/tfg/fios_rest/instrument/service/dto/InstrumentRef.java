package es.udc.tfg.fios_rest.instrument.service.dto;

import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;

public record InstrumentRef(
  Long id,
  String name,
  InstrumentCategory category
) {

  public static InstrumentRef from(Instrument instrument) {
    return new InstrumentRef(
      instrument.getId(),
      instrument.getName(),
      instrument.getCategory()
    );
  }
}
