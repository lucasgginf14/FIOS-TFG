export const ALL_RESULT_TYPES = ["spaces", "events", "bands", "recruitments"];

export function createDefaultSearchFilters() {
  return {
    city: "",
    province: "",
    autonomousCommunity: "",
    date: "",
    dateFrom: "",
    dateTo: "",
    datePreset: "",
    resultTypes: [...ALL_RESULT_TYPES],
    spaceType: "",
    musicalGenre: "",
    maxBudget: 0,
    peopleCount: 0,
    timeOfDay: "",
    startTime: "",
    endTime: "",
    view: "list"
  };
}

export function normalizeSearchFilters(filters = {}) {
  const defaults = createDefaultSearchFilters();
  const resultTypes = Array.isArray(filters.resultTypes)
    ? filters.resultTypes.filter((type) => ALL_RESULT_TYPES.includes(type))
    : typeof filters.resultTypes === "string" && filters.resultTypes.length
      ? filters.resultTypes
          .split(",")
          .map((type) => type.trim())
          .filter((type) => ALL_RESULT_TYPES.includes(type))
      : defaults.resultTypes;

  return {
    ...defaults,
    ...filters,
    city: normalizeString(filters.city),
    province: normalizeString(filters.province),
    autonomousCommunity: normalizeString(filters.autonomousCommunity),
    date: normalizeString(filters.date),
    dateFrom: normalizeString(filters.dateFrom),
    dateTo: normalizeString(filters.dateTo),
    datePreset: normalizeString(filters.datePreset),
    resultTypes: resultTypes.length ? resultTypes : [...ALL_RESULT_TYPES],
    spaceType: normalizeString(filters.spaceType),
    musicalGenre: normalizeString(filters.musicalGenre),
    maxBudget: normalizePositiveNumber(filters.maxBudget),
    peopleCount: normalizePositiveNumber(filters.peopleCount),
    timeOfDay: normalizeString(filters.timeOfDay),
    startTime: normalizeString(filters.startTime),
    endTime: normalizeString(filters.endTime),
    view: filters.view === "map" ? "map" : "list"
  };
}

function normalizeString(value) {
  return typeof value === "string" ? value.trim() : "";
}

function normalizePositiveNumber(value) {
  const parsed = Number(value);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 0;
}
