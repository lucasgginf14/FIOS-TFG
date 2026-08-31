package es.udc.tfg.fios_rest.bandmember.persistence.dao;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class BandMemberDaoJpa extends GenericDaoJpa implements BandMemberDao {

  @Override
  public Collection<BandMember> findByBand(Long bandId) {
    return entityManager
      .createQuery("from BandMember bm where bm.band.id = :bandId order by bm.joinDate", BandMember.class)
      .setParameter("bandId", bandId)
      .getResultList();
  }

  @Override
  public Collection<BandMember> findActiveByBand(Long bandId) {
    return entityManager
      .createQuery("from BandMember bm where bm.band.id = :bandId and bm.active = true order by bm.joinDate", BandMember.class)
      .setParameter("bandId", bandId)
      .getResultList();
  }

  @Override
  public Collection<BandMember> findActiveByUser(Long userId) {
    return entityManager
      .createQuery(
        "from BandMember bm where bm.user.id = :userId and bm.active = true and bm.band.active = true order by bm.band.name",
        BandMember.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Optional<BandMember> findById(Long id) {
    return Optional.ofNullable(entityManager.find(BandMember.class, id));
  }

  @Override
  public Optional<BandMember> findByBandAndUser(Long bandId, Long userId) {
    try {
      return Optional.of(entityManager
        .createQuery(
          "from BandMember bm where bm.band.id = :bandId and bm.user.id = :userId",
          BandMember.class
        )
        .setParameter("bandId", bandId)
        .setParameter("userId", userId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<BandMember> findActiveByBandAndUser(Long bandId, Long userId) {
    try {
      return Optional.of(entityManager
        .createQuery(
          "from BandMember bm where bm.band.id = :bandId and bm.user.id = :userId and bm.active = true",
          BandMember.class
        )
        .setParameter("bandId", bandId)
        .setParameter("userId", userId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public BandMember save(BandMember bandMember) {
    entityManager.persist(bandMember);
    return bandMember;
  }

  @Override
  public BandMember update(BandMember bandMember) {
    return entityManager.merge(bandMember);
  }

  @Override
  public void deactivateActiveByBand(Band band) {
    findActiveByBand(band.getId()).forEach(bandMember -> {
      bandMember.deactivate();
      update(bandMember);
    });
  }
}
