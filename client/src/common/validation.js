const PHONE_PATTERN = /^\+?[0-9][0-9\s-]*[0-9]$/;
const PHONE_MIN_DIGITS = 9;
const PHONE_MAX_DIGITS = 15;

export function countDigits(value = "") {
  return value.replace(/\D/g, "").length;
}

export function isValidPhone(value = "") {
  const trimmed = value.trim();

  if (!trimmed) {
    return false;
  }

  const digits = countDigits(trimmed);
  return PHONE_PATTERN.test(trimmed) && digits >= PHONE_MIN_DIGITS && digits <= PHONE_MAX_DIGITS;
}
