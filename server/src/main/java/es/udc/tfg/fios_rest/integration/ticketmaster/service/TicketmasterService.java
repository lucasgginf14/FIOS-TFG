package es.udc.tfg.fios_rest.integration.ticketmaster.service;

import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventBulkImportView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventImportView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventSearchRequest;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventView;

import java.util.List;

public interface TicketmasterService {

  List<TicketmasterEventView> searchEvents(TicketmasterEventSearchRequest request) throws TicketmasterException;

  TicketmasterEventView findEventByExternalId(String externalId) throws TicketmasterException;

  TicketmasterEventImportView importEvent(String externalId) throws TicketmasterException;

  TicketmasterEventBulkImportView importEvents(TicketmasterEventSearchRequest request) throws TicketmasterException;
}
