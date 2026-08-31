package es.udc.tfg.fios_rest.eventpurchase.service.dto;

import es.udc.tfg.fios_rest.event.service.dto.EventRef;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchase;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchaseState;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventPurchaseView(
  Long id,
  LocalDateTime purchasedAt,
  LocalDateTime reservedAt,
  LocalDateTime cancelledAt,
  EventPurchaseState state,
  boolean active,
  BigDecimal amountDue,
  BigDecimal pricePaid,
  EventRef event,
  UserPublicRef user
) {

  public static EventPurchaseView from(EventPurchase eventPurchase) {
    return new EventPurchaseView(
      eventPurchase.getId(),
      eventPurchase.getPurchasedAt(),
      eventPurchase.getPurchasedAt(),
      eventPurchase.getCancelledAt(),
      eventPurchase.getState(),
      eventPurchase.isActive(),
      eventPurchase.getAmountDue(),
      eventPurchase.getPricePaid(),
      EventRef.from(eventPurchase.getEvent()),
      UserPublicRef.from(eventPurchase.getUser())
    );
  }
}
