package es.udc.tfg.fios_rest.spaceequipment.persistence.dao;

import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;

import java.util.Collection;
import java.util.Optional;

public interface SpaceEquipmentDao {

  Collection<SpaceEquipment> findByMusicalSpace(Long musicalSpaceId);

  Optional<SpaceEquipment> findById(Long id);

  Optional<SpaceEquipment> findByMusicalSpaceAndEquipment(Long musicalSpaceId, Long equipmentId);

  SpaceEquipment save(SpaceEquipment spaceEquipment);

  SpaceEquipment update(SpaceEquipment spaceEquipment);

  void delete(SpaceEquipment spaceEquipment);
}
