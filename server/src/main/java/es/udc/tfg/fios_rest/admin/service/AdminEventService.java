package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.event.service.EventService;
import es.udc.tfg.fios_rest.event.service.dto.EventFilterParams;
import es.udc.tfg.fios_rest.event.service.dto.EventRef;
import es.udc.tfg.fios_rest.event.service.dto.EventView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminEventService {

  @Autowired
  private EventService eventService;

  public List<EventRef> findAll(EventFilterParams filters) {
    return eventService.findAll(filters);
  }

  public EventView findById(Long id) throws NotFoundException {
    return eventService.findById(id);
  }
}
