const RAW_MESSAGE_RULES = [
  {
    key: "apiErrors.badCredentials",
    matches: ["bad credentials", "invalid credentials"]
  },
  {
    key: "apiErrors.badCredentials",
    matches: ["email", "contraseña", "no son correctos"]
  },
  {
    key: "apiErrors.malformed",
    matches: ["malformed request body"]
  },
  {
    key: "apiErrors.malformed",
    matches: ["no pudimos leer", "información enviada"]
  },
  {
    key: "apiErrors.forbidden",
    matches: ["forbidden", "access denied"]
  },
  {
    key: "apiErrors.forbidden",
    matches: ["no tienes permiso"]
  },
  {
    key: "apiErrors.notFound",
    matches: ["no encontramos"]
  },
  {
    key: "apiErrors.conflict",
    matches: ["conflicts with existing data", "database constraints"]
  },
  {
    key: "apiErrors.conflict",
    matches: ["no se pudo guardar", "ya existe"]
  },
  {
    key: "apiErrors.internal",
    matches: ["internal server error"]
  },
  {
    key: "apiErrors.internal",
    matches: ["problema inesperado"]
  },
  {
    key: "apiErrors.emailExists",
    matches: ["user email", "email already", "email", "already exists"]
  },
  {
    key: "apiErrors.emailExists",
    matches: ["cuenta", "email", "ya existe"]
  },
  {
    key: "apiErrors.phoneExists",
    matches: ["user phone", "phone", "already exists"]
  },
  {
    key: "apiErrors.phoneExists",
    matches: ["cuenta", "teléfono", "ya existe"]
  },
  {
    key: "apiErrors.invalidEmail",
    matches: ["email format", "email", "not valid"]
  },
  {
    key: "apiErrors.invalidEmail",
    matches: ["email válido"]
  },
  {
    key: "apiErrors.futureBirthDate",
    matches: ["birth date", "future"]
  },
  {
    key: "apiErrors.futureBirthDate",
    matches: ["fecha de nacimiento", "futuro"]
  },
  {
    key: "apiErrors.passwordMismatch",
    matches: ["password", "confirm password", "do not match"]
  },
  {
    key: "apiErrors.passwordMismatch",
    matches: ["contraseñas", "no coinciden"]
  },
  {
    key: "apiErrors.passwordWeak",
    matches: ["password", "letter", "number"]
  },
  {
    key: "apiErrors.passwordWeak",
    matches: ["contraseña", "letra", "número"]
  },
  {
    key: "apiErrors.currentPasswordIncorrect",
    matches: ["current password", "incorrect"]
  },
  {
    key: "apiErrors.currentPasswordIncorrect",
    matches: ["contraseña actual", "no es correcta"]
  },
  {
    key: "apiErrors.passwordSameAsCurrent",
    matches: ["new password", "different from the current password"]
  },
  {
    key: "apiErrors.passwordSameAsCurrent",
    matches: ["nueva contraseña", "distinta"]
  },
  {
    key: "apiErrors.requiredFields",
    matches: ["obligatory"]
  },
  {
    key: "apiErrors.requiredFields",
    matches: ["campos obligatorios"]
  },
  {
    key: "apiErrors.requiredFields",
    matches: ["required"]
  },
  {
    key: "apiErrors.requiredFields",
    matches: ["cannot be null"]
  },
  {
    key: "apiErrors.requiredFields",
    matches: ["cannot be empty"]
  },
  {
    key: "apiErrors.invalidTimeRange",
    matches: ["start time", "before", "end time"]
  },
  {
    key: "apiErrors.invalidTimeRange",
    matches: ["hora de inicio", "hora de fin"]
  },
  {
    key: "apiErrors.invalidDate",
    matches: ["date", "past"]
  },
  {
    key: "apiErrors.invalidDate",
    matches: ["event date", "today or later"]
  },
  {
    key: "apiErrors.invalidDate",
    matches: ["fecha elegida", "no es válida"]
  },
  {
    key: "apiErrors.invalidNumber",
    matches: ["must be greater than zero"]
  },
  {
    key: "apiErrors.invalidNumber",
    matches: ["cannot be negative"]
  },
  {
    key: "apiErrors.invalidNumber",
    matches: ["must be between"]
  },
  {
    key: "apiErrors.invalidNumber",
    matches: ["números introducidos"]
  },
  {
    key: "apiErrors.tooLong",
    matches: ["cannot exceed"]
  },
  {
    key: "apiErrors.timeNotAvailable",
    matches: ["time range", "not available"]
  },
  {
    key: "apiErrors.timeNotAvailable",
    matches: ["overlaps"]
  },
  {
    key: "apiErrors.timeNotAvailable",
    matches: ["horario no está disponible"]
  },
  {
    key: "apiErrors.noTicketsAvailable",
    matches: ["no tickets available"]
  },
  {
    key: "apiErrors.noTicketsAvailable",
    matches: ["no quedan entradas"]
  },
  {
    key: "apiErrors.eventAlreadyReserved",
    matches: ["already reserved"]
  },
  {
    key: "apiErrors.eventAlreadyReserved",
    matches: ["reserva activa"]
  },
  {
    key: "apiErrors.alreadyExists",
    matches: ["already exists"]
  },
  {
    key: "apiErrors.alreadyExists",
    matches: ["already saved"]
  },
  {
    key: "apiErrors.alreadyExists",
    matches: ["already has"]
  },
  {
    key: "apiErrors.alreadyExists",
    matches: ["already an active member"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["cannot manage"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["cannot access"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["cannot view"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["only the reservation owner"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["only the ticket reservation owner"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["only the event creator"]
  },
  {
    key: "apiErrors.noPermission",
    matches: ["not an active member"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot be updated"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot be cancelled"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot be accepted"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot be reviewed"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot be changed"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["only pending"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["only completed"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["only reservations"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["already started"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["already ended"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["already cancelled"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["only published"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["cannot accept"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["capacity cannot be lower"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["active ticket reservations"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["inactive"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["closed"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["endpoint"]
  },
  {
    key: "apiErrors.actionNotAvailable",
    matches: ["acción no está disponible"]
  },
  {
    key: "apiErrors.locationUnavailable",
    matches: ["geocoding"]
  },
  {
    key: "apiErrors.locationUnavailable",
    matches: ["latitude"]
  },
  {
    key: "apiErrors.locationUnavailable",
    matches: ["longitude"]
  },
  {
    key: "apiErrors.locationUnavailable",
    matches: ["no pudimos comprobar la ubicación"]
  },
  {
    key: "events.ticketmaster.errors.missingKey",
    matches: ["ticketmaster", "api key"]
  },
  {
    key: "events.ticketmaster.errors.missingKey",
    matches: ["ticketmaster", "no está lista"]
  },
  {
    key: "events.ticketmaster.errors.disabled",
    matches: ["ticketmaster", "disabled"]
  },
  {
    key: "events.ticketmaster.errors.disabled",
    matches: ["ticketmaster", "desactivada"]
  },
  {
    key: "events.ticketmaster.errors.timeout",
    matches: ["ticketmaster", "did not respond in time"]
  },
  {
    key: "events.ticketmaster.errors.timeout",
    matches: ["ticketmaster", "tardando demasiado"]
  },
  {
    key: "events.ticketmaster.errors.rateLimit",
    matches: ["ticketmaster", "429"]
  },
  {
    key: "events.ticketmaster.errors.provider",
    matches: ["ticketmaster", "http error"]
  },
  {
    key: "events.ticketmaster.errors.provider",
    matches: ["ticketmaster", "no se pudo consultar"]
  }
];

const STATUS_KEYS = {
  400: "apiErrors.badRequest",
  401: "apiErrors.unauthorized",
  403: "apiErrors.forbidden",
  404: "apiErrors.notFound",
  409: "apiErrors.conflict",
  422: "apiErrors.badRequest",
  429: "apiErrors.rateLimited",
  500: "apiErrors.internal",
  502: "apiErrors.unavailable",
  503: "apiErrors.unavailable",
  504: "apiErrors.unavailable"
};

const CODE_KEYS = {
  ACCESS_DENIED: "apiErrors.forbidden",
  ACCOUNT_DISABLED: "apiErrors.accountDisabled",
  ACTION_NOT_AVAILABLE: "apiErrors.actionNotAvailable",
  AUTHENTICATION_REQUIRED: "apiErrors.unauthorized",
  BAD_CREDENTIALS: "apiErrors.badCredentials",
  BAD_REQUEST: "apiErrors.badRequest",
  CURRENT_PASSWORD_INCORRECT: "apiErrors.currentPasswordIncorrect",
  DATA_CONFLICT: "apiErrors.conflict",
  DUPLICATE_RESOURCE: "apiErrors.alreadyExists",
  EMAIL_ALREADY_EXISTS: "apiErrors.emailExists",
  EVENT_ALREADY_RESERVED: "apiErrors.eventAlreadyReserved",
  EVENT_ALREADY_PURCHASED: "apiErrors.eventAlreadyPurchased",
  FIELD_REQUIRED: "apiErrors.requiredFields",
  FILE_TOO_LARGE: "apiErrors.fileTooLarge",
  INVALID_COORDINATES: "apiErrors.locationUnavailable",
  INVALID_DATE: "apiErrors.invalidDate",
  INVALID_DATE_RANGE: "apiErrors.invalidDate",
  INVALID_EMAIL: "apiErrors.invalidEmail",
  INVALID_FILE: "apiErrors.invalidFile",
  INVALID_LOCATION: "apiErrors.locationUnavailable",
  INVALID_NUMBER: "apiErrors.invalidNumber",
  INVALID_PHONE: "apiErrors.invalidPhone",
  INVALID_REQUEST_PARAMETER: "apiErrors.badRequest",
  INVALID_TIME_RANGE: "apiErrors.invalidTimeRange",
  MALFORMED_JSON: "apiErrors.malformed",
  METHOD_NOT_ALLOWED: "apiErrors.badRequest",
  NO_TICKETS_AVAILABLE: "apiErrors.noTicketsAvailable",
  PASSWORD_MISMATCH: "apiErrors.passwordMismatch",
  PASSWORD_WEAK: "apiErrors.passwordWeak",
  RESOURCE_NOT_FOUND: "apiErrors.notFound",
  SPACE_EQUIPMENT_ALREADY_EXISTS: "apiErrors.spaceEquipmentAlreadyExists",
  TIME_NOT_AVAILABLE: "apiErrors.timeNotAvailable",
  VALIDATION_ERROR: "apiErrors.badRequest"
};

const FIELD_ERROR_KEYS = {
  FIELD_REQUIRED: "apiFieldErrors.FIELD_REQUIRED",
  INVALID_COORDINATES: "apiFieldErrors.INVALID_COORDINATES",
  INVALID_DATE: "apiFieldErrors.INVALID_DATE",
  INVALID_DATE_RANGE: "apiFieldErrors.INVALID_DATE_RANGE",
  INVALID_EMAIL: "apiFieldErrors.INVALID_EMAIL",
  INVALID_LENGTH: "apiFieldErrors.INVALID_LENGTH",
  INVALID_NUMBER: "apiFieldErrors.INVALID_NUMBER",
  INVALID_PHONE: "apiFieldErrors.INVALID_PHONE",
  INVALID_TIME_RANGE: "apiFieldErrors.INVALID_TIME_RANGE",
  INVALID_VALUE: "apiFieldErrors.INVALID_VALUE"
};

export function getApiErrorMessage(error, t, fallbackKey = "apiErrors.generic") {
  if (error?.code === "ERR_NETWORK" || !error?.response) {
    return t("apiErrors.network");
  }

  const responseData = error.response.data ?? {};
  const codeKey = CODE_KEYS[responseData.code];

  if (codeKey) {
    if (responseData.code === "VALIDATION_ERROR") {
      const firstFieldError = getApiFieldErrorList(error, t)[0];

      if (firstFieldError?.message) {
        return firstFieldError.message;
      }
    }

    return t(codeKey);
  }

  const rawMessage = responseData.message?.trim();
  const rawKey = resolveRawMessageKey(rawMessage);

  if (rawKey) {
    return t(rawKey);
  }

  const statusKey = STATUS_KEYS[error.response.status];

  if (statusKey) {
    return t(statusKey);
  }

  return resolveFallback(t, fallbackKey);
}

export function getApiFieldErrors(error, t) {
  return getApiFieldErrorList(error, t).reduce((fieldErrors, fieldError) => {
    if (fieldError.field && !fieldErrors[fieldError.field]) {
      fieldErrors[fieldError.field] = fieldError.message;
    }

    return fieldErrors;
  }, {});
}

function getApiFieldErrorList(error, t) {
  const rawFieldErrors = error?.response?.data?.fieldErrors;

  if (!Array.isArray(rawFieldErrors)) {
    return [];
  }

  return rawFieldErrors
    .map((fieldError) => ({
      field: normalizeField(fieldError?.field),
      message: resolveFieldErrorMessage(fieldError, t)
    }))
    .filter((fieldError) => fieldError.field && fieldError.message);
}

function resolveFieldErrorMessage(fieldError, t) {
  const codeKey = FIELD_ERROR_KEYS[fieldError?.code] ?? CODE_KEYS[fieldError?.code];

  if (codeKey) {
    return t(codeKey);
  }

  const rawMessage = fieldError?.message?.trim();
  const rawKey = resolveRawMessageKey(rawMessage);

  if (rawKey) {
    return t(rawKey);
  }

  return rawMessage || t("apiFieldErrors.INVALID_VALUE");
}

function normalizeField(field = "") {
  if (typeof field !== "string" || !field.trim()) {
    return "";
  }

  return field.trim();
}

function resolveRawMessageKey(rawMessage = "") {
  const normalized = rawMessage.toLowerCase();

  if (!normalized) {
    return null;
  }

  const rule = RAW_MESSAGE_RULES.find(({ matches }) =>
    matches.every((fragment) => normalized.includes(fragment))
  );

  return rule?.key ?? null;
}

function resolveFallback(t, fallback) {
  if (!fallback) {
    return t("apiErrors.generic");
  }

  if (fallback.includes(".") && !fallback.includes(" ")) {
    return t(fallback);
  }

  return fallback;
}
