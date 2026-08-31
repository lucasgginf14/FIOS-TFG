package es.udc.tfg.fios_rest.common.exceptions.model;

public class UserPhoneExistsException extends ModelException {
  public UserPhoneExistsException(String phone) {
    super("User phone " + phone + " already exists");
  }
}
