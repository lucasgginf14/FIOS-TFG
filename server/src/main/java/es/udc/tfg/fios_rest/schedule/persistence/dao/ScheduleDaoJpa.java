package es.udc.tfg.fios_rest.schedule.persistence.dao;

import es.udc.tfg.fios_rest.common.util.repository.GenericDaoJpa;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Optional;

@Repository
public class ScheduleDaoJpa extends GenericDaoJpa implements ScheduleDao {

  @Override
  public Collection<Schedule> findByMusicalSpace(Long musicalSpaceId) {
    return entityManager
      .createQuery(
        """
        from Schedule s
        where s.musicalSpace.id = :musicalSpaceId
        order by s.dayOfWeek, s.startTime
        """,
        Schedule.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .getResultList();
  }

  @Override
  public Collection<Schedule> findByMusicalSpaceAndDay(Long musicalSpaceId, DayOfWeek dayOfWeek) {
    return entityManager
      .createQuery(
        """
        from Schedule s
        where s.musicalSpace.id = :musicalSpaceId
          and s.dayOfWeek = :dayOfWeek
        order by s.startTime
        """,
        Schedule.class
      )
      .setParameter("musicalSpaceId", musicalSpaceId)
      .setParameter("dayOfWeek", dayOfWeek)
      .getResultList();
  }

  @Override
  public Optional<Schedule> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Schedule.class, id));
  }

  @Override
  public Schedule save(Schedule schedule) {
    entityManager.persist(schedule);
    return schedule;
  }

  @Override
  public Schedule update(Schedule schedule) {
    return entityManager.merge(schedule);
  }

  @Override
  public void delete(Schedule schedule) {
    entityManager.remove(entityManager.contains(schedule) ? schedule : entityManager.merge(schedule));
  }
}
