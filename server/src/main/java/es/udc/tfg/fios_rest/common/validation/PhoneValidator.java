package es.udc.tfg.fios_rest.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

  private static final String ALLOWED_FORMAT = "^\\+?[0-9][0-9\\s-]*[0-9]$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return true;
    }

    String normalized = value.trim();
    if (!normalized.matches(ALLOWED_FORMAT)) {
      return false;
    }

    long digitCount = normalized.chars()
      .filter(Character::isDigit)
      .count();

    return digitCount >= 9 && digitCount <= 15;
  }
}
