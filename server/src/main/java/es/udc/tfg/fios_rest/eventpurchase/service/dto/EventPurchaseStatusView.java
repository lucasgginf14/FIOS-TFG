package es.udc.tfg.fios_rest.eventpurchase.service.dto;

public record EventPurchaseStatusView(
  boolean reserved,
  EventPurchaseView reservation,
  boolean purchased,
  EventPurchaseView purchase,
  Integer capacity,
  long reservedTickets,
  long soldTickets,
  Integer availableTickets,
  Integer remainingTickets,
  boolean soldOut
) {
}
