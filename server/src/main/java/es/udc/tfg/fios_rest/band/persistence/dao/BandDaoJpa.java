package es.udc.tfg.fios_rest.band.persistence.dao;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class BandDaoJpa extends GenericDaoJpa implements BandDao {

  @Override
  public Collection<Band> findAllActive() {
    return entityManager
      .createQuery("from Band b where b.active = true order by lower(b.name)", Band.class)
      .getResultList();
  }

  @Override
  public Collection<Band> findActive(String city, String genre) {
    StringBuilder queryBuilder = new StringBuilder(
      """
      from Band b
      where b.active = true
      """
    );
    List<String> predicates = new ArrayList<>();

    if (city != null) {
      predicates.add("lower(b.baseCity) like :city");
    }

    if (genre != null) {
      predicates.add("lower(b.mainGenre) like :genre");
    }

    if (!predicates.isEmpty()) {
      queryBuilder.append(" and ").append(String.join(" and ", predicates));
    }

    queryBuilder.append(" order by lower(b.name)");

    TypedQuery<Band> query = entityManager.createQuery(queryBuilder.toString(), Band.class);

    if (city != null) {
      query.setParameter("city", "%" + city.toLowerCase() + "%");
    }

    if (genre != null) {
      query.setParameter("genre", "%" + genre.toLowerCase() + "%");
    }

    return query.getResultList();
  }

  @Override
  public Optional<Band> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Band.class, id));
  }

  @Override
  public Band save(Band band) {
    entityManager.persist(band);
    return band;
  }

  @Override
  public Band update(Band band) {
    return entityManager.merge(band);
  }
}
