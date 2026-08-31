package es.udc.tfg.fios_rest.favoritespace.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FavoriteSpaceDaoJpa extends GenericDaoJpa implements FavoriteSpaceDao {

  @Override
  public Collection<FavoriteSpace> findByUser(Long userId) {
    return entityManager.createQuery(
        """
        from FavoriteSpace fs
        where fs.user.id = :userId
        order by fs.savedAt desc, fs.id desc
        """,
        FavoriteSpace.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Optional<FavoriteSpace> findByUserAndMusicalSpace(Long userId, Long musicalSpaceId) {
    try {
      return Optional.of(entityManager.createQuery(
          """
          from FavoriteSpace fs
          where fs.user.id = :userId
            and fs.musicalSpace.id = :musicalSpaceId
          """,
          FavoriteSpace.class
        )
        .setParameter("userId", userId)
        .setParameter("musicalSpaceId", musicalSpaceId)
        .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public FavoriteSpace save(FavoriteSpace favoriteSpace) {
    entityManager.persist(favoriteSpace);
    return favoriteSpace;
  }

  @Override
  public void delete(FavoriteSpace favoriteSpace) {
    entityManager.remove(entityManager.contains(favoriteSpace) ? favoriteSpace : entityManager.merge(favoriteSpace));
  }
}
