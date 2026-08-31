package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import es.udc.tfg.fios_rest.event.service.dto.EventView;

public record TicketmasterEventImportView(
  boolean alreadyImported,
  String externalId,
  TicketmasterProvider provider,
  EventView event
) {
}
