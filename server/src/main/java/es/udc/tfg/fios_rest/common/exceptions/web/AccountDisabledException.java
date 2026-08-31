package es.udc.tfg.fios_rest.common.exceptions.web;

public class AccountDisabledException extends ResourceException {

  public AccountDisabledException() {
    super("The user account is disabled");
  }
}
