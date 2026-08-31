export function formatInstrumentNames(instruments, fallback) {
  const names = Array.isArray(instruments)
    ? instruments
        .map((instrument) => instrument?.name)
        .filter((name) => typeof name === "string" && name.trim())
        .map((name) => name.trim())
    : [];

  return names.length ? names.join(", ") : fallback;
}

export function getPrimaryInstrumentFromAccount(account = {}) {
  if (hasInstrumentName(account.instrument)) {
    return account.instrument;
  }

  if (!Array.isArray(account.instruments)) {
    return null;
  }

  return account.instruments.find(hasInstrumentName) || null;
}

export function formatPrimaryInstrumentName(account, fallback) {
  const primaryInstrument = getPrimaryInstrumentFromAccount(account);
  return primaryInstrument?.name?.trim() || fallback;
}

export function buildPrimaryInstrumentIds(instrumentId) {
  const normalizedId = Number(instrumentId);
  return Number.isFinite(normalizedId) && normalizedId > 0 ? [normalizedId] : [];
}

function hasInstrumentName(instrument) {
  return typeof instrument?.name === "string" && instrument.name.trim().length > 0;
}
