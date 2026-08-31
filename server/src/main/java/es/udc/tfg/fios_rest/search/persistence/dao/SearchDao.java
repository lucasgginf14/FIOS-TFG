package es.udc.tfg.fios_rest.search.persistence.dao;

import es.udc.tfg.fios_rest.search.persistence.entity.Search;

import java.util.Collection;

public interface SearchDao {

  Collection<Search> findByUser(Long userId);

  Search save(Search search);
}
