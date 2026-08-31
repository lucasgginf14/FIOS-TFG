package es.udc.tfg.fios_rest.bandrecruitment.persistence.dao;

import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentStatus;
import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class BandRecruitmentDaoJpa extends GenericDaoJpa implements BandRecruitmentDao {

  @Override
  public Collection<BandRecruitment> findAll() {
    return entityManager
      .createQuery(
        """
        from BandRecruitment br
        order by br.publicationDate desc, br.id desc
        """,
        BandRecruitment.class
      )
      .getResultList();
  }

  @Override
  public Collection<BandRecruitment> findOpen() {
    return entityManager
      .createQuery(
        """
        from BandRecruitment br
        where br.status = :status
          and br.band.active = true
        order by br.publicationDate desc
        """,
        BandRecruitment.class
      )
      .setParameter("status", BandRecruitmentStatus.OPEN)
      .getResultList();
  }

  @Override
  public Collection<BandRecruitment> findOpen(String city, String genre) {
    StringBuilder queryBuilder = new StringBuilder(
      """
      select br
      from BandRecruitment br
      join fetch br.band b
      join fetch br.instrument
      join fetch br.publishedBy
      where br.status = :status
        and b.active = true
      """
    );
    List<String> predicates = new ArrayList<>();

    if (city != null) {
      predicates.add("(lower(br.city) like :city or lower(b.baseCity) like :city)");
    }

    if (genre != null) {
      predicates.add("lower(b.mainGenre) like :genre");
    }

    if (!predicates.isEmpty()) {
      queryBuilder.append(" and ").append(String.join(" and ", predicates));
    }

    queryBuilder.append(" order by br.publicationDate desc");

    TypedQuery<BandRecruitment> query = entityManager.createQuery(queryBuilder.toString(), BandRecruitment.class)
      .setParameter("status", BandRecruitmentStatus.OPEN);

    if (city != null) {
      query.setParameter("city", "%" + city.toLowerCase() + "%");
    }

    if (genre != null) {
      query.setParameter("genre", "%" + genre.toLowerCase() + "%");
    }

    return query.getResultList();
  }

  @Override
  public Collection<BandRecruitment> findByBandIds(Collection<Long> bandIds) {
    return entityManager
      .createQuery(
        """
        from BandRecruitment br
        where br.band.id in :bandIds
        order by br.publicationDate desc
        """,
        BandRecruitment.class
      )
      .setParameter("bandIds", bandIds)
      .getResultList();
  }

  @Override
  public Optional<BandRecruitment> findById(Long id) {
    return Optional.ofNullable(entityManager.find(BandRecruitment.class, id));
  }

  @Override
  public BandRecruitment save(BandRecruitment bandRecruitment) {
    entityManager.persist(bandRecruitment);
    return bandRecruitment;
  }

  @Override
  public BandRecruitment update(BandRecruitment bandRecruitment) {
    return entityManager.merge(bandRecruitment);
  }

  @Override
  public void delete(BandRecruitment bandRecruitment) {
    entityManager.remove(entityManager.contains(bandRecruitment) ? bandRecruitment : entityManager.merge(bandRecruitment));
  }
}
