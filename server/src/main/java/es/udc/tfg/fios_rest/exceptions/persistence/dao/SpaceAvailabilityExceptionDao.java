package es.udc.tfg.fios_rest.exceptions.persistence.dao;

import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface SpaceAvailabilityExceptionDao {

  Collection<SpaceAvailabilityException> findByMusicalSpace(Long musicalSpaceId);

  Collection<SpaceAvailabilityException> findByMusicalSpaceAndDate(Long musicalSpaceId, LocalDate date);

  Optional<SpaceAvailabilityException> findById(Long id);

  SpaceAvailabilityException save(SpaceAvailabilityException spaceAvailabilityException);

  SpaceAvailabilityException update(SpaceAvailabilityException spaceAvailabilityException);

  void delete(SpaceAvailabilityException spaceAvailabilityException);
}
