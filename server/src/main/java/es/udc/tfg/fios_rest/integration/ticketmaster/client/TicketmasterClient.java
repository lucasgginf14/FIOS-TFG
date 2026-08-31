package es.udc.tfg.fios_rest.integration.ticketmaster.client;

import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventItem;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventSearchRequest;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterException;

import java.util.List;

public interface TicketmasterClient {

  List<TicketmasterEventItem> searchEvents(TicketmasterEventSearchRequest request) throws TicketmasterException;

  TicketmasterEventItem findEventByExternalId(String externalId) throws TicketmasterException;
}
