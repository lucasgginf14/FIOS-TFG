package es.udc.tfg.fios_rest.common.exceptions.model;

public class EventAlreadyPurchasedException extends ModelException {

  public EventAlreadyPurchasedException() {
    super("Event already reserved by current user");
  }
}
