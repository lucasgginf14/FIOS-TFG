package es.udc.tfg.fios_rest.common.exceptions.model;

public class SpaceEquipmentAlreadyExistsException extends ModelException {

  public SpaceEquipmentAlreadyExistsException() {
    super("Space equipment already exists for this musical space");
  }
}
