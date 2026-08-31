package es.udc.tfg.fios_rest.instrument.persistence.dao;

import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;

import java.util.Collection;
import java.util.Optional;

public interface InstrumentDao {

  Collection<Instrument> findAll();

  Optional<Instrument> findById(Long id);

  Optional<Instrument> findByName(String name);

  Instrument save(Instrument instrument);

  Instrument update(Instrument instrument);
}
