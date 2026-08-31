package es.udc.tfg.fios_rest.user.persistence.dao;

import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@Transactional
public class UserDaoJpa implements UserDao {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(entityManager.find(User.class, id));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    try {
      TypedQuery<User> query = entityManager.createQuery(
        "SELECT u FROM User u WHERE lower(u.email) = lower(:email)",
        User.class
      );
      query.setParameter("email", email);
      return Optional.of(query.getSingleResult());
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> findByPhone(String phone) {
    if (phone == null || phone.trim().isEmpty()) {
      return Optional.empty();
    }

    try {
      TypedQuery<User> query = entityManager.createQuery(
        "SELECT u FROM User u WHERE u.phone = :phone",
        User.class
      );
      query.setParameter("phone", phone.trim());
      return Optional.of(query.getSingleResult());
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Override
  public List<User> findAll() {
    return entityManager.createQuery(
      "SELECT u FROM User u ORDER BY u.createdAt DESC",
      User.class
    ).getResultList();
  }

  @Override
  public List<User> findAllActive() {
    return entityManager.createQuery(
      "SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC",
      User.class
    ).getResultList();
  }

  @Override
  public List<User> searchActiveUsers(String searchTerm, Collection<Long> excludedUserIds, int limit) {
    if (searchTerm == null || searchTerm.isBlank() || limit < 1) {
      return List.of();
    }

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = builder.createQuery(User.class);
    Root<User> user = query.from(User.class);
    String pattern = "%" + escapeLike(searchTerm.trim().toLowerCase(Locale.ROOT)) + "%";

    Expression<String> secondSurname = builder.coalesce(user.<String>get("secondSurname"), "");
    Expression<String> fullName = builder.lower(
      builder.concat(
        builder.concat(
          builder.concat(user.<String>get("name"), " "),
          user.<String>get("firstSurname")
        ),
        builder.concat(" ", secondSurname)
      )
    );

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(builder.isTrue(user.get("active")));
    predicates.add(builder.equal(user.get("platformRole"), PlatformRole.USER));
    predicates.add(builder.or(
      builder.like(builder.lower(user.<String>get("email")), pattern, '\\'),
      builder.like(builder.lower(user.<String>get("name")), pattern, '\\'),
      builder.like(builder.lower(user.<String>get("firstSurname")), pattern, '\\'),
      builder.like(builder.lower(secondSurname), pattern, '\\'),
      builder.like(fullName, pattern, '\\')
    ));

    if (excludedUserIds != null && !excludedUserIds.isEmpty()) {
      predicates.add(builder.not(user.get("id").in(excludedUserIds)));
    }

    query
      .select(user)
      .where(predicates.toArray(Predicate[]::new))
      .orderBy(
        builder.asc(builder.lower(user.<String>get("name"))),
        builder.asc(builder.lower(user.<String>get("firstSurname"))),
        builder.asc(builder.lower(user.<String>get("email")))
      );

    return entityManager
      .createQuery(query)
      .setMaxResults(limit)
      .getResultList();
  }

  @Override
  public boolean existsByEmail(String email) {
    Long count = entityManager.createQuery(
        "SELECT COUNT(u) FROM User u WHERE lower(u.email) = lower(:email)",
        Long.class
      ).setParameter("email", email)
      .getSingleResult();

    return count != null && count > 0;
  }

  @Override
  public boolean existsByPhone(String phone) {
    if (phone == null || phone.trim().isEmpty()) {
      return false;
    }

    Long count = entityManager.createQuery(
        "SELECT COUNT(u) FROM User u WHERE u.phone = :phone",
        Long.class
      ).setParameter("phone", phone.trim())
      .getSingleResult();

    return count != null && count > 0;
  }

  @Override
  public User save(User user) {
    entityManager.persist(user);
    return user;
  }

  @Override
  public User update(User user) {
    return entityManager.merge(user);
  }

  private String escapeLike(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("%", "\\%")
      .replace("_", "\\_");
  }
}
