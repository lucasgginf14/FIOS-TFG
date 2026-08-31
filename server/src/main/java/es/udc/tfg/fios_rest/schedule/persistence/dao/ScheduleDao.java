package es.udc.tfg.fios_rest.schedule.persistence.dao;

import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Optional;

public interface ScheduleDao {

  Collection<Schedule> findByMusicalSpace(Long musicalSpaceId);

  Collection<Schedule> findByMusicalSpaceAndDay(Long musicalSpaceId, DayOfWeek dayOfWeek);

  Optional<Schedule> findById(Long id);

  Schedule save(Schedule schedule);

  Schedule update(Schedule schedule);

  void delete(Schedule schedule);
}
