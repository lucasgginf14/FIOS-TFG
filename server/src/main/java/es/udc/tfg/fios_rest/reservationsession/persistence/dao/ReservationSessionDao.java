package es.udc.tfg.fios_rest.reservationsession.persistence.dao;

import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface ReservationSessionDao {

  Collection<ReservationSession> findAll();

  Collection<ReservationSession> findByUser(Long userId);

  Collection<ReservationSession> findByManager(Long managerId);

  Collection<ReservationSession> findByMusicalSpaceAndDate(Long musicalSpaceId, LocalDate sessionDate);

  Optional<ReservationSession> findById(Long id);

  ReservationSession save(ReservationSession reservationSession);

  ReservationSession update(ReservationSession reservationSession);
}
