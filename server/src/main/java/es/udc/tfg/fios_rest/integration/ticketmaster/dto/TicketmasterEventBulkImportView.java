package es.udc.tfg.fios_rest.integration.ticketmaster.dto;

import java.util.List;

public record TicketmasterEventBulkImportView(
  int importedCount,
  int alreadyImportedCount,
  List<TicketmasterEventImportView> events
) {
}
