export const DAY_KEYS = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY"
];

export const EXCEPTION_TYPE_KEYS = ["BLOCKED", "CUSTOM_AVAILABILITY"];

export function createEmptyScheduleForm() {
  return {
    dayOfWeek: "MONDAY",
    dayOfWeeks: ["MONDAY"],
    startTime: "",
    endTime: "",
    price: ""
  };
}

export function createEmptyExceptionForm(date = "") {
  return {
    date,
    startTime: "",
    endTime: "",
    exceptionType: "BLOCKED",
    reason: "",
    price: ""
  };
}

export function mapScheduleToForm(schedule = {}) {
  const dayOfWeek = schedule.dayOfWeek || "MONDAY";

  return {
    dayOfWeek,
    dayOfWeeks: [dayOfWeek],
    startTime: normalizeTimeForInput(schedule.startTime),
    endTime: normalizeTimeForInput(schedule.endTime),
    price: schedule.price == null ? "" : String(schedule.price)
  };
}

export function mapExceptionToForm(spaceException = {}) {
  return {
    date: spaceException.date || "",
    startTime: normalizeTimeForInput(spaceException.startTime),
    endTime: normalizeTimeForInput(spaceException.endTime),
    exceptionType: spaceException.exceptionType || "BLOCKED",
    reason: spaceException.reason || "",
    price: spaceException.price == null ? "" : String(spaceException.price)
  };
}

export function buildSchedulePayload(form = {}) {
  const result = buildSchedulePayloads({ ...form, dayOfWeeks: [form.dayOfWeek] });

  return {
    payload: result.payloads[0] ?? null,
    errorKey: result.errorKey
  };
}

export function buildSchedulePayloads(form = {}) {
  const dayOfWeeks = normalizeScheduleDaySelection(form);
  const startTime = normalizeTimeForInput(form.startTime);
  const endTime = normalizeTimeForInput(form.endTime);
  const price = parsePositiveNumber(form.price);

  if (!dayOfWeeks.length || dayOfWeeks.some((dayOfWeek) => !DAY_KEYS.includes(dayOfWeek))) {
    return invalidPayloads("scheduleDay");
  }

  if (!isTimeRangeValid(startTime, endTime)) {
    return invalidPayloads("timeRange");
  }

  if (price == null) {
    return invalidPayloads("schedulePrice");
  }

  return {
    payloads: dayOfWeeks.map((dayOfWeek) => ({
      dayOfWeek,
      startTime,
      endTime,
      price
    })),
    errorKey: ""
  };
}

export function buildExceptionPayload(form = {}) {
  const date = String(form.date || "").trim();
  const startTime = normalizeTimeForInput(form.startTime);
  const endTime = normalizeTimeForInput(form.endTime);
  const exceptionType = String(form.exceptionType || "").trim();
  const reason = String(form.reason || "").trim();
  const price = parsePositiveNumber(form.price);

  if (!isIsoDate(date)) {
    return invalid("exceptionDate");
  }

  if (!isTimeRangeValid(startTime, endTime)) {
    return invalid("timeRange");
  }

  if (!EXCEPTION_TYPE_KEYS.includes(exceptionType)) {
    return invalid("exceptionType");
  }

  if (exceptionType === "CUSTOM_AVAILABILITY" && price == null) {
    return invalid("customPrice");
  }

  return {
    payload: {
      date,
      startTime,
      endTime,
      exceptionType,
      reason: reason || null,
      price: exceptionType === "CUSTOM_AVAILABILITY" ? price : null
    },
    errorKey: ""
  };
}

export function hasAvailabilityManagementPermission(space, user) {
  if (!space || !user?.logged) {
    return false;
  }

  return user.platformRole === "ADMIN" || sameId(space.manager?.id, user.id);
}

export function canManageApprovedAvailability(space, user) {
  return hasAvailabilityManagementPermission(space, user)
    && space.active !== false
    && space.approvalStatus === "APPROVED";
}

export function sortSchedules(schedules = []) {
  return [...schedules].sort((left, right) => {
    const dayDiff = DAY_KEYS.indexOf(left.dayOfWeek) - DAY_KEYS.indexOf(right.dayOfWeek);
    return dayDiff || normalizeTimeForInput(left.startTime).localeCompare(normalizeTimeForInput(right.startTime));
  });
}

export function sortExceptions(exceptions = []) {
  return [...exceptions].sort((left, right) => {
    const dateDiff = String(left.date || "").localeCompare(String(right.date || ""));
    return dateDiff || normalizeTimeForInput(left.startTime).localeCompare(normalizeTimeForInput(right.startTime));
  });
}

export function normalizeTimeForInput(value) {
  if (!value) {
    return "";
  }

  return String(value).slice(0, 5);
}

export function isTimeRangeValid(startTime, endTime) {
  if (!isTimeValue(startTime) || !isTimeValue(endTime)) {
    return false;
  }

  return timeToMinutes(startTime) < timeToMinutes(endTime);
}

function invalid(errorKey) {
  return { payload: null, errorKey };
}

function invalidPayloads(errorKey) {
  return { payloads: [], errorKey };
}

function normalizeScheduleDaySelection(form = {}) {
  const rawDays = Array.isArray(form.dayOfWeeks) ? form.dayOfWeeks : [form.dayOfWeek];
  const normalizedDays = rawDays.map((dayOfWeek) => String(dayOfWeek || "").trim()).filter(Boolean);
  return [...new Set(normalizedDays)];
}

function isIsoDate(value) {
  return /^\d{4}-\d{2}-\d{2}$/.test(value);
}

function isTimeValue(value) {
  return /^([01]\d|2[0-3]):[0-5]\d$/.test(normalizeTimeForInput(value));
}

function timeToMinutes(value) {
  const [hours, minutes] = normalizeTimeForInput(value).split(":").map(Number);
  return (hours * 60) + minutes;
}

function parsePositiveNumber(value) {
  if (value === "" || value == null) {
    return null;
  }

  const numericValue = Number(value);
  return Number.isFinite(numericValue) && numericValue > 0 ? numericValue : null;
}

function sameId(left, right) {
  return left != null && right != null && String(left) === String(right);
}
