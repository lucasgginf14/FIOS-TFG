package es.udc.tfg.fios_rest.user.persistence.dao;

import es.udc.tfg.fios_rest.user.persistence.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDao {

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  Optional<User> findByPhone(String phone);

  List<User> findAll();

  List<User> findAllActive();

  List<User> searchActiveUsers(String searchTerm, Collection<Long> excludedUserIds, int limit);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  User save(User user);

  User update(User user);
}
