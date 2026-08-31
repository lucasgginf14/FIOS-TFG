package es.udc.tfg.fios_rest.exceptions.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public class SpaceAvailabilityExceptionDaoJpa extends GenericDaoJpa implements SpaceAvailabilityExceptionDao {

  @Override
  public Collection<SpaceAvailabilityException> findByMusicalSpace(Long musicalSpaceId) {
    return entityManager
      .createQuery(
        """
        from SpaceAvailabilityException sae
        where sae.musicalSpace.id = :musicalSpaceId
        order by sae.date, sae.startTime
        """,
        SpaceAvailabilityException.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .getResultList();
  }

  @Override
  public Collection<SpaceAvailabilityException> findByMusicalSpaceAndDate(Long musicalSpaceId, LocalDate date) {
    return entityManager
      .createQuery(
        """
        from SpaceAvailabilityException sae
        where sae.musicalSpace.id = :musicalSpaceId
          and sae.date = :date
        order by sae.startTime
        """,
        SpaceAvailabilityException.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .setParameter("date", date)
      .getResultList();
  }

  @Override
  public Optional<SpaceAvailabilityException> findById(Long id) {
    return Optional.ofNullable(entityManager.find(SpaceAvailabilityException.class, id));
  }

  @Override
  public SpaceAvailabilityException save(SpaceAvailabilityException spaceAvailabilityException) {
    entityManager.persist(spaceAvailabilityException);
    return spaceAvailabilityException;
  }

  @Override
  public SpaceAvailabilityException update(SpaceAvailabilityException spaceAvailabilityException) {
    return entityManager.merge(spaceAvailabilityException);
  }

  @Override
  public void delete(SpaceAvailabilityException spaceAvailabilityException) {
    entityManager.remove(entityManager.contains(spaceAvailabilityException)
      ? spaceAvailabilityException
      : entityManager.merge(spaceAvailabilityException));
  }
}
