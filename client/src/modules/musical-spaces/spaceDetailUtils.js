const DAY_NAMES = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];

export function buildAddress(location = {}) {
  return [location.street, location.portal, location.floor, location.postalCode, location.city, location.province]
    .filter(Boolean)
    .join(", ");
}

export function formatDate(value, locale) {
  if (!value) {
    return "--";
  }

  return new Intl.DateTimeFormat(locale, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(new Date(`${value}T00:00:00`));
}

export function formatDateTime(value, locale) {
  if (!value) {
    return "--";
  }

  return new Intl.DateTimeFormat(locale, {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

export function formatMoney(value, locale, currency = "EUR") {
  if (value == null || value === "") {
    return null;
  }

  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    maximumFractionDigits: 2
  }).format(Number(value));
}

export function normalizeTimeString(value) {
  if (!value) {
    return "";
  }

  return typeof value === "string" ? value.slice(0, 5) : String(value).slice(0, 5);
}

export function timeToMinutes(value) {
  const normalized = normalizeTimeString(value);

  if (!normalized) {
    return 0;
  }

  const [hours, minutes] = normalized.split(":").map(Number);
  return (hours * 60) + minutes;
}

export function minutesToTime(totalMinutes) {
  const safeMinutes = Math.max(0, Number(totalMinutes) || 0);
  const hours = Math.floor(safeMinutes / 60) % 24;
  const minutes = safeMinutes % 60;
  return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}`;
}

export function addMinutesToTime(value, minutesToAdd) {
  return minutesToTime(timeToMinutes(value) + minutesToAdd);
}

export function getSlotDurationMinutes(slot) {
  if (!slot) {
    return 0;
  }

  return Math.max(0, timeToMinutes(slot.endTime) - timeToMinutes(slot.startTime));
}

export function getDurationOptions(slot, step = 30) {
  const totalMinutes = getSlotDurationMinutes(slot);
  const options = [];

  for (let current = step; current <= totalMinutes; current += step) {
    options.push(current);
  }

  return options;
}

export function getStartTimeOptions(slot, step = 30) {
  if (!slot) {
    return [];
  }

  const startMinutes = timeToMinutes(slot.startTime);
  const endMinutes = timeToMinutes(slot.endTime);
  const options = [];

  for (let current = startMinutes; current <= endMinutes - step; current += step) {
    options.push(minutesToTime(current));
  }

  return options;
}

export function formatDurationLabel(totalMinutes, t) {
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;

  if (hours > 0 && minutes > 0) {
    return t("spaceDetail.booking.durationMixed", { hours, minutes });
  }

  if (hours > 0) {
    return t("spaceDetail.booking.durationHours", { hours });
  }

  return t("spaceDetail.booking.durationMinutes", { minutes });
}

export function getScheduleSummary(schedules = []) {
  if (!Array.isArray(schedules) || !schedules.length) {
    return {
      openTime: "",
      closeTime: "",
      minHourlyRate: null,
      maxHourlyRate: null
    };
  }

  const minutesSorted = schedules
    .map((schedule) => ({
      startMinutes: timeToMinutes(schedule.startTime),
      endMinutes: timeToMinutes(schedule.endTime),
      hourlyRate: getScheduleHourlyRate(schedule)
    }))
    .sort((left, right) => left.startMinutes - right.startMinutes);

  const validRates = minutesSorted
    .map((schedule) => schedule.hourlyRate)
    .filter((value) => Number.isFinite(value));

  return {
    openTime: minutesToTime(minutesSorted[0].startMinutes),
    closeTime: minutesToTime(minutesSorted[minutesSorted.length - 1].endMinutes),
    minHourlyRate: validRates.length ? Math.min(...validRates) : null,
    maxHourlyRate: validRates.length ? Math.max(...validRates) : null
  };
}

export function estimateReservationPrice({ schedules = [], slot = null, sessionDate, startTime, endTime }) {
  if (!sessionDate || !startTime || !endTime) {
    return null;
  }

  const startMinutes = timeToMinutes(startTime);
  const endMinutes = timeToMinutes(endTime);

  if (endMinutes <= startMinutes) {
    return null;
  }

  if (slot) {
    return estimateReservationPriceFromSlot(slot, startMinutes, endMinutes);
  }

  const dayName = getDayName(sessionDate);
  if (!dayName) {
    return null;
  }

  const matchingSchedules = schedules.filter((schedule) => schedule.dayOfWeek === dayName);

  if (!matchingSchedules.length) {
    return null;
  }

  let total = 0;
  let coveredMinutes = 0;

  matchingSchedules.forEach((schedule) => {
    const scheduleStart = timeToMinutes(schedule.startTime);
    const scheduleEnd = timeToMinutes(schedule.endTime);
    const overlapStart = Math.max(startMinutes, scheduleStart);
    const overlapEnd = Math.min(endMinutes, scheduleEnd);

    if (overlapEnd <= overlapStart) {
      return;
    }

    const scheduleDuration = scheduleEnd - scheduleStart;
    const overlapDuration = overlapEnd - overlapStart;

    if (scheduleDuration <= 0 || schedule.price == null) {
      return;
    }

    total += (Number(schedule.price) * overlapDuration) / scheduleDuration;
    coveredMinutes += overlapDuration;
  });

  const requestedDuration = endMinutes - startMinutes;

  if (!coveredMinutes || coveredMinutes < requestedDuration) {
    return null;
  }

  return Number(total.toFixed(2));
}

function estimateReservationPriceFromSlot(slot, startMinutes, endMinutes) {
  const slotPrice = Number(slot?.price);
  if (!Number.isFinite(slotPrice) || slotPrice <= 0) {
    return null;
  }

  const slotStart = timeToMinutes(slot.startTime);
  const slotEnd = timeToMinutes(slot.endTime);

  if (slotEnd <= slotStart || startMinutes < slotStart || endMinutes > slotEnd) {
    return null;
  }

  const slotDuration = slotEnd - slotStart;
  const requestedDuration = endMinutes - startMinutes;
  return Number(((slotPrice * requestedDuration) / slotDuration).toFixed(2));
}

export function getDayName(sessionDate) {
  if (!sessionDate) {
    return "";
  }

  const date = new Date(`${sessionDate}T00:00:00`);
  return DAY_NAMES[date.getDay()] || "";
}

export function getScheduleHourlyRate(schedule) {
  if (!schedule || schedule.price == null) {
    return null;
  }

  const durationMinutes = timeToMinutes(schedule.endTime) - timeToMinutes(schedule.startTime);

  if (durationMinutes <= 0) {
    return null;
  }

  return Number(((Number(schedule.price) * 60) / durationMinutes).toFixed(2));
}

export function getInitialDate() {
  const today = new Date();
  return [
    today.getFullYear(),
    String(today.getMonth() + 1).padStart(2, "0"),
    String(today.getDate()).padStart(2, "0")
  ].join("-");
}

export function getTomorrowDate() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return [
    tomorrow.getFullYear(),
    String(tomorrow.getMonth() + 1).padStart(2, "0"),
    String(tomorrow.getDate()).padStart(2, "0")
  ].join("-");
}

export function getQuickServices(space) {
  const capacity = Number(space?.capacity || 0);
  const squareMeters = Number(space?.squareMeters || 0);

  return [
    { id: "wifi", icon: "bi bi-wifi", active: true },
    { id: "parking", icon: "bi bi-p-circle", active: Boolean(space?.location?.city) },
    { id: "rest", icon: "bi bi-cup-hot", active: squareMeters >= 30 || capacity >= 6 },
    { id: "access", icon: "bi bi-universal-access", active: Boolean(space?.active) }
  ].filter((item) => item.active);
}
