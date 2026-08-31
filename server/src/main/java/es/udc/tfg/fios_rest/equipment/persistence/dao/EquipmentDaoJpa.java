package es.udc.tfg.fios_rest.equipment.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class EquipmentDaoJpa extends GenericDaoJpa implements EquipmentDao {

  @Override
  public Collection<Equipment> findAll() {
    return entityManager
      .createQuery("from Equipment e order by lower(e.name)", Equipment.class)
      .getResultList();
  }

  @Override
  public Optional<Equipment> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Equipment.class, id));
  }

  @Override
  public Optional<Equipment> findByName(String name) {
    try {
      return Optional.of(entityManager
        .createQuery("from Equipment e where lower(e.name) = lower(:name)", Equipment.class)
        .setParameter("name", name.trim())
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Equipment save(Equipment equipment) {
    entityManager.persist(equipment);
    return equipment;
  }

  @Override
  public Equipment update(Equipment equipment) {
    return entityManager.merge(equipment);
  }
}
