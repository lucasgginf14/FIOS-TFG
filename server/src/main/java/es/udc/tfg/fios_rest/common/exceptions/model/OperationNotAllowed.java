package es.udc.tfg.fios_rest.common.exceptions.model;

public class OperationNotAllowed extends ModelException {

  public OperationNotAllowed(String msg) {
    super("Operation not allowed: " + msg);
  }
}
