package es.udc.tfg.fios_rest.favoritespace.persistence.dao;

import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;

import java.util.Collection;
import java.util.Optional;

public interface FavoriteSpaceDao {

  Collection<FavoriteSpace> findByUser(Long userId);

  Optional<FavoriteSpace> findByUserAndMusicalSpace(Long userId, Long musicalSpaceId);

  FavoriteSpace save(FavoriteSpace favoriteSpace);

  void delete(FavoriteSpace favoriteSpace);
}
