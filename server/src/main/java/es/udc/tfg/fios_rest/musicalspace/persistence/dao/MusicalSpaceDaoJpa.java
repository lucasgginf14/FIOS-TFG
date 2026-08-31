package es.udc.tfg.fios_rest.musicalspace.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MusicalSpaceDaoJpa extends GenericDaoJpa implements MusicalSpaceDao {

  @Override
  public Collection<MusicalSpace> findAll() {
    return entityManager
      .createQuery(
        """
        from MusicalSpace ms
        order by ms.active desc, lower(ms.name)
        """,
        MusicalSpace.class
      )
      .getResultList();
  }

  @Override
  public Collection<MusicalSpace> findPublic() {
    return entityManager
      .createQuery(
        """
        from MusicalSpace ms
        where ms.active = true
          and ms.approvalStatus = :approvalStatus
        order by lower(ms.name)
        """,
        MusicalSpace.class
      )
      .setParameter("approvalStatus", MusicalSpaceApprovalStatus.APPROVED)
      .getResultList();
  }

  @Override
  public Collection<MusicalSpace> findPublic(String city, MusicalSpaceType spaceType, Integer minCapacity) {
    StringBuilder queryBuilder = new StringBuilder(
      """
      from MusicalSpace ms
      where ms.active = true
        and ms.approvalStatus = :approvalStatus
      """
    );
    List<String> predicates = new ArrayList<>();

    if (city != null) {
      predicates.add("lower(ms.location.city) like :city");
    }

    if (spaceType != null) {
      predicates.add("ms.spaceType = :spaceType");
    }

    if (minCapacity != null) {
      predicates.add("ms.capacity >= :minCapacity");
    }

    if (!predicates.isEmpty()) {
      queryBuilder.append(" and ").append(String.join(" and ", predicates));
    }

    queryBuilder.append(" order by lower(ms.name)");

    TypedQuery<MusicalSpace> query = entityManager.createQuery(queryBuilder.toString(), MusicalSpace.class)
      .setParameter("approvalStatus", MusicalSpaceApprovalStatus.APPROVED);

    if (city != null) {
      query.setParameter("city", "%" + city.toLowerCase() + "%");
    }

    if (spaceType != null) {
      query.setParameter("spaceType", spaceType);
    }

    if (minCapacity != null) {
      query.setParameter("minCapacity", minCapacity);
    }

    return query.getResultList();
  }

  @Override
  public Collection<MusicalSpace> findByManager(Long managerId) {
    return entityManager
      .createQuery(
        "from MusicalSpace ms where ms.manager.id = :managerId order by lower(ms.name)",
        MusicalSpace.class
      )
      .setParameter("managerId", managerId)
      .getResultList();
  }

  @Override
  public Optional<MusicalSpace> findById(Long id) {
    return Optional.ofNullable(entityManager.find(MusicalSpace.class, id));
  }

  @Override
  public MusicalSpace save(MusicalSpace musicalSpace) {
    entityManager.persist(musicalSpace);
    return musicalSpace;
  }

  @Override
  public MusicalSpace update(MusicalSpace musicalSpace) {
    return entityManager.merge(musicalSpace);
  }
}
