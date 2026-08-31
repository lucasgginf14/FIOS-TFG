package es.udc.tfg.fios_rest.common.util.web;

public record FieldErrorDTO(
  String field,
  String code,
  String message
) {
}
