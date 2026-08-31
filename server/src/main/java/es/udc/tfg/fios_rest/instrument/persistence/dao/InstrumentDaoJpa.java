package es.udc.tfg.fios_rest.instrument.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class InstrumentDaoJpa extends GenericDaoJpa implements InstrumentDao {

  @Override
  public Collection<Instrument> findAll() {
    return entityManager
      .createQuery("from Instrument i order by lower(i.name)", Instrument.class)
      .getResultList();
  }

  @Override
  public Optional<Instrument> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Instrument.class, id));
  }

  @Override
  public Optional<Instrument> findByName(String name) {
    try {
      return Optional.of(entityManager
        .createQuery("from Instrument i where lower(i.name) = lower(:name)", Instrument.class)
        .setParameter("name", name.trim())
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Instrument save(Instrument instrument) {
    entityManager.persist(instrument);
    return instrument;
  }

  @Override
  public Instrument update(Instrument instrument) {
    return entityManager.merge(instrument);
  }
}
