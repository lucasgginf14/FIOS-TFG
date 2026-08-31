package es.udc.tfg.fios_rest.common.exceptions.model;

public class UserEmailExistsException extends ModelException {
  public UserEmailExistsException(String email) {
    super("User email " + email + " already exists");
  }
}
