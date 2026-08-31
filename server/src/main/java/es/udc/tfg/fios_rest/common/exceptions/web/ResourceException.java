package es.udc.tfg.fios_rest.common.exceptions.web;

public class ResourceException extends Exception {
  public ResourceException(String errorMsg) {
    super(errorMsg);
  }
}
