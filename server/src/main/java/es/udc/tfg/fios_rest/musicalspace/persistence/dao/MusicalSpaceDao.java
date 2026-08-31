package es.udc.tfg.fios_rest.musicalspace.persistence.dao;

import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;

import java.util.Collection;
import java.util.Optional;

public interface MusicalSpaceDao {

  Collection<MusicalSpace> findAll();

  Collection<MusicalSpace> findPublic();

  Collection<MusicalSpace> findPublic(String city, MusicalSpaceType spaceType, Integer minCapacity);

  Collection<MusicalSpace> findByManager(Long managerId);

  Optional<MusicalSpace> findById(Long id);

  MusicalSpace save(MusicalSpace musicalSpace);

  MusicalSpace update(MusicalSpace musicalSpace);
}
