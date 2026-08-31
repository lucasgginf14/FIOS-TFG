package es.udc.tfg.fios_rest.equipment.persistence.dao;

import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;

import java.util.Collection;
import java.util.Optional;

public interface EquipmentDao {

  Collection<Equipment> findAll();

  Optional<Equipment> findById(Long id);

  Optional<Equipment> findByName(String name);

  Equipment save(Equipment equipment);

  Equipment update(Equipment equipment);
}
