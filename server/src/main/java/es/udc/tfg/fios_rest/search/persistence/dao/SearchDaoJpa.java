package es.udc.tfg.fios_rest.search.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.search.persistence.entity.Search;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SearchDaoJpa extends GenericDaoJpa implements SearchDao {

  @Override
  public Collection<Search> findByUser(Long userId) {
    return entityManager.createQuery(
        """
        from Search s
        where s.user.id = :userId
        order by s.searchDate desc, s.id desc
        """,
        Search.class
      )
      .setParameter("userId", userId)
      .getResultList();
  }

  @Override
  public Search save(Search search) {
    entityManager.persist(search);
    return search;
  }
}
