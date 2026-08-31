const STOP_WORDS = new Set([
  "a",
  "al",
  "algo",
  "algun",
  "alguna",
  "banda",
  "bandas",
  "busca",
  "busco",
  "buscar",
  "buscando",
  "con",
  "de",
  "del",
  "el",
  "en",
  "grupo",
  "grupos",
  "la",
  "las",
  "lo",
  "los",
  "mi",
  "mis",
  "necesito",
  "o",
  "para",
  "por",
  "que",
  "quiero",
  "se",
  "sin",
  "un",
  "una",
  "unos",
  "unas",
  "y"
]);

const SPACE_TERMS = [
  "aula",
  "clase",
  "ensayar",
  "ensayo",
  "espacio",
  "estudio",
  "grabar",
  "grabacion",
  "local",
  "sala"
];

const EVENT_TERMS = [
  "actuacion",
  "concierto",
  "directo",
  "evento",
  "festival",
  "jam"
];

const RECRUITMENT_TERMS = [
  "bajista",
  "bateria",
  "cantante",
  "guitarra",
  "guitarrista",
  "integrante",
  "integrantes",
  "miembro",
  "miembros",
  "saxofon",
  "saxofonista",
  "teclado",
  "teclista",
  "vacante",
  "vocalista"
];

const RECRUITMENT_PHRASES = [
  "busca miembros",
  "buscan miembros",
  "buscamos miembros",
  "busco banda",
  "buscando banda",
  "formar banda",
  "se busca musico",
  "unirme a banda"
];

const BAND_TERMS = [
  "band",
  "bands",
  "banda",
  "bandas",
  "grupo",
  "grupos",
  "project",
  "projects",
  "proyecto",
  "proyectos"
];

const BAND_PHRASES = [
  "banda de",
  "bandas de",
  "grupo de",
  "grupos de",
  "buscar banda",
  "busco banda",
  "bandas en",
  "banda en",
  "band in",
  "bands in"
];

export function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}

export function tokenizeSearchText(text) {
  return normalizeText(text)
    .split(" ")
    .filter((token) => token.length > 2 && !STOP_WORDS.has(token));
}

export function shouldIncludeRecruitments(text, criteria = {}) {
  if (hasRecruitmentPhrase(text)) {
    return true;
  }

  if (criteria.intent === "SPACE" || criteria.intent === "EVENT") {
    return false;
  }

  if (hasSpaceOrEventIntent(text)) {
    return false;
  }

  if (hasRecruitmentTerm(text)) {
    return true;
  }

  if (
    hasSpaceSpecificFilters(criteria)
  ) {
    return false;
  }

  return true;
}

export function shouldIncludeBands(text, criteria = {}) {
  if (hasBandPhrase(text) || hasBandTerm(text)) {
    return true;
  }

  if (criteria.intent === "SPACE" || criteria.intent === "EVENT") {
    return false;
  }

  if (hasSpaceOrEventIntent(text)) {
    return false;
  }

  if (
    hasSpaceSpecificFilters(criteria)
  ) {
    return false;
  }

  return true;
}

export function searchTextMatchesBand(band, text) {
  const tokens = tokenizeSearchText(text);

  if (!tokens.length) {
    return true;
  }

  const haystack = normalizeText([
    band?.name,
    band?.description,
    band?.mainGenre,
    band?.baseCity
  ].join(" "));

  return tokens.some((token) => haystack.includes(token));
}

export function searchTextMatchesRecruitment(recruitment, text) {
  const tokens = tokenizeSearchText(text);

  if (!tokens.length) {
    return true;
  }

  const haystack = normalizeText([
    recruitment?.title,
    recruitment?.roleWanted,
    recruitment?.instrument?.name,
    recruitment?.band?.name,
    recruitment?.band?.mainGenre,
    recruitment?.city,
    recruitment?.band?.baseCity
  ].join(" "));

  return tokens.some((token) => haystack.includes(token));
}

function hasRecruitmentPhrase(text) {
  const normalized = normalizeText(text);

  if (!normalized) {
    return false;
  }

  return RECRUITMENT_PHRASES.some((phrase) => normalized.includes(phrase));
}

function hasRecruitmentTerm(text) {
  const tokens = tokenizeSearchText(text);
  return tokens.some((token) => RECRUITMENT_TERMS.includes(token));
}

function hasBandPhrase(text) {
  const normalized = normalizeText(text);

  if (!normalized) {
    return false;
  }

  return BAND_PHRASES.some((phrase) => normalized.includes(phrase));
}

function hasBandTerm(text) {
  const normalized = normalizeText(text);
  const tokens = normalized
    .split(" ")
    .filter((token) => token.length > 2);

  return tokens.some((token) => BAND_TERMS.includes(token));
}

function hasSpaceOrEventIntent(text) {
  const tokens = tokenizeSearchText(text);
  return tokens.some((token) => SPACE_TERMS.includes(token) || EVENT_TERMS.includes(token));
}

function hasSpaceSpecificFilters(criteria = {}) {
  return Boolean(
    criteria.spaceType ||
      criteria.province ||
      criteria.autonomousCommunity ||
      criteria.date ||
      criteria.dateFrom ||
      criteria.dateTo ||
      criteria.datePreset ||
      criteria.startTime ||
      criteria.endTime ||
      Number(criteria.peopleCount) > 0 ||
      Number(criteria.maxBudget) > 0
  );
}
