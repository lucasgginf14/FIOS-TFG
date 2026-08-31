package es.udc.tfg.fios_rest.spaceequipment.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class SpaceEquipmentDaoJpa extends GenericDaoJpa implements SpaceEquipmentDao {

  @Override
  public Collection<SpaceEquipment> findByMusicalSpace(Long musicalSpaceId) {
    return entityManager
      .createQuery(
        """
        from SpaceEquipment se
        where se.musicalSpace.id = :musicalSpaceId
        order by lower(se.equipment.name)
        """,
        SpaceEquipment.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .getResultList();
  }

  @Override
  public Optional<SpaceEquipment> findById(Long id) {
    return Optional.ofNullable(entityManager.find(SpaceEquipment.class, id));
  }

  @Override
  public Optional<SpaceEquipment> findByMusicalSpaceAndEquipment(Long musicalSpaceId, Long equipmentId) {
    try {
      return Optional.of(entityManager
        .createQuery(
          """
          from SpaceEquipment se
          where se.musicalSpace.id = :musicalSpaceId
            and se.equipment.id = :equipmentId
          """,
          SpaceEquipment.class
        )
        .setParameter("musicalSpaceId", musicalSpaceId)
        .setParameter("equipmentId", equipmentId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public SpaceEquipment save(SpaceEquipment spaceEquipment) {
    entityManager.persist(spaceEquipment);
    return spaceEquipment;
  }

  @Override
  public SpaceEquipment update(SpaceEquipment spaceEquipment) {
    return entityManager.merge(spaceEquipment);
  }

  @Override
  public void delete(SpaceEquipment spaceEquipment) {
    entityManager.remove(entityManager.contains(spaceEquipment) ? spaceEquipment : entityManager.merge(spaceEquipment));
  }
}
