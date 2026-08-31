package es.udc.tfg.fios_rest.band.persistence.dao;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;

import java.util.Collection;
import java.util.Optional;

public interface BandDao {

  Collection<Band> findAllActive();

  Collection<Band> findActive(String city, String genre);

  Optional<Band> findById(Long id);

  Band save(Band band);

  Band update(Band band);
}
