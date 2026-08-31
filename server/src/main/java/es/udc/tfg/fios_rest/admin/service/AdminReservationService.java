package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.reservationsession.service.ReservationSessionService;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationCancellationRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminReservationService {

  @Autowired
  private ReservationSessionService reservationSessionService;

  @Transactional(readOnly = true)
  public List<ReservationSessionView> findAll() throws NotFoundException {
    return reservationSessionService.findReservationsForMySpaces();
  }

  @Transactional(readOnly = true)
  public ReservationSessionView findById(Long id) throws NotFoundException, OperationNotAllowed {
    return reservationSessionService.findById(id);
  }

  public ReservationSessionView cancel(Long id, ReservationCancellationRequest request)
    throws NotFoundException, OperationNotAllowed {
    return reservationSessionService.cancelAdmin(id, request);
  }
}
