import {
  buildAddress,
  formatDate,
  formatMoney,
  normalizeTimeString
} from "../musical-spaces/spaceDetailUtils.js";

const ACTIVE_STATES = ["PENDING", "ACCEPTED"];
const CANCELLED_STATES = ["CANCELLED", "REJECTED"];

export function getReservationStartDate(reservation) {
  return buildReservationDate(reservation, normalizeTimeString(reservation?.startTime) || "00:00");
}

export function getReservationEndDate(reservation) {
  return buildReservationDate(
    reservation,
    normalizeTimeString(reservation?.endTime) || normalizeTimeString(reservation?.startTime) || "00:00"
  );
}

export function hasReservationStarted(reservation) {
  if (!reservation?.sessionDate) {
    return false;
  }

  const startDate = getReservationStartDate(reservation);

  if (!startDate) {
    return false;
  }

  return startDate.getTime() <= Date.now();
}

export function isFutureReservation(reservation) {
  const startDate = getReservationStartDate(reservation);

  if (!startDate) {
    return false;
  }

  return startDate.getTime() > Date.now();
}

export function isPastReservation(reservation) {
  const endDate = getReservationEndDate(reservation);

  if (!endDate) {
    return false;
  }

  return endDate.getTime() <= Date.now();
}

export function isActiveReservation(reservation) {
  return ACTIVE_STATES.includes(reservation?.state) && !isPastReservation(reservation);
}

export function isCancelledReservation(reservation) {
  return CANCELLED_STATES.includes(reservation?.state);
}

export function isCompletedReservation(reservation) {
  return reservation?.state === "COMPLETED";
}

export function getReservationStats(reservations = []) {
  return {
    active: reservations.filter(isActiveReservation).length,
    upcoming: reservations.filter((reservation) =>
      isActiveReservation(reservation) && isFutureReservation(reservation)
    ).length,
    completed: reservations.filter(isCompletedReservation).length,
    cancelled: reservations.filter(isCancelledReservation).length
  };
}

export function getUpcomingReservation(reservations = []) {
  return reservations
    .filter((reservation) => isActiveReservation(reservation) && isFutureReservation(reservation))
    .sort((left, right) => getReservationTimestamp(left) - getReservationTimestamp(right))[0] || null;
}

export function filterReservations(reservations = [], filters = {}) {
  const text = normalizeText(filters.searchText);

  return reservations.filter((reservation) => {
    const statusMatch = matchesStatusTab(reservation, filters.statusTab);
    const textMatch = !text || buildSearchHaystack(reservation).includes(text);
    const dateMatch = !filters.date || reservation.sessionDate === filters.date;
    const typeMatch = !filters.sessionType || reservation.sessionType === filters.sessionType;

    return statusMatch && textMatch && dateMatch && typeMatch;
  });
}

export function sortReservations(reservations = [], order = "nearest") {
  const items = [...reservations];

  return items.sort((left, right) => {
    if (order === "farthest") {
      return getReservationTimestamp(right) - getReservationTimestamp(left);
    }

    if (order === "priceDesc") {
      return getReservationPrice(right) - getReservationPrice(left);
    }

    if (order === "priceAsc") {
      return getReservationPrice(left) - getReservationPrice(right);
    }

    return getReservationTimestamp(left) - getReservationTimestamp(right);
  });
}

export function matchesStatusTab(reservation, statusTab = "all") {
  if (statusTab === "active") {
    return isActiveReservation(reservation);
  }

  if (statusTab === "pending") {
    return reservation?.state === "PENDING";
  }

  if (statusTab === "completed") {
    return reservation?.state === "COMPLETED";
  }

  if (statusTab === "cancelled") {
    return isCancelledReservation(reservation);
  }

  return true;
}

export function mapReservationForCard(reservation, locale, t) {
  const location = getReservationLocation(reservation);
  const dateLabel = formatDate(reservation?.sessionDate, locale);
  const startTime = normalizeTimeString(reservation?.startTime) || "--:--";
  const endTime = normalizeTimeString(reservation?.endTime) || "--:--";
  const hasStarted = hasReservationStarted(reservation);
  const isPast = isPastReservation(reservation);
  const spaceId = reservation?.musicalSpace?.id || null;

  return {
    ...reservation,
    image: reservation?.musicalSpace?.mainImage || "",
    name: reservation?.musicalSpace?.name || t("reservations.cards.fallbackSpace"),
    location,
    dateLabel,
    timeRangeLabel: `${startTime} - ${endTime}`,
    priceLabel: formatMoney(reservation?.finalPrice, locale) || t("common.labels.onRequest"),
    sessionTypeLabel: t(`reservations.sessionTypes.${reservation?.sessionType || "OTHER"}`),
    statusLabel: t(`reservations.statuses.${reservation?.state || "PENDING"}`),
    attendeesLabel: t("reservations.cards.attendeesValue", {
      count: Number(reservation?.attendeesCount || 0)
    }),
    canModify: reservation?.state === "PENDING" && !hasStarted,
    canCancel: ACTIVE_STATES.includes(reservation?.state) && !hasStarted,
    canContact: ACTIVE_STATES.includes(reservation?.state),
    canShowReason: Boolean(reservation?.cancellationReason),
    canRebook: Boolean(spaceId),
    spaceId,
    hasStarted,
    isPast,
    statusTone: getReservationStatusTone(reservation?.state),
    timingLabel: getReservationTimingLabel(reservation, t),
    timingTone: getReservationTimingTone(reservation),
    daysUntil: getReservationDaysUntil(reservation)
  };
}

export function getReservationStatusTone(state) {
  const mapping = {
    PENDING: "warning",
    ACCEPTED: "success",
    COMPLETED: "muted",
    CANCELLED: "danger",
    REJECTED: "danger"
  };

  return mapping[state] || "muted";
}

export function getReservationLocation(reservation) {
  const musicalSpace = reservation?.musicalSpace;
  const location = musicalSpace?.location;
  const shortLocation = [
    location?.city ?? musicalSpace?.city,
    location?.province ?? musicalSpace?.province
  ].filter(Boolean).join(", ");

  return shortLocation || buildAddress(location) || "";
}

export function getReservationDaysUntil(reservation) {
  const startDate = getReservationStartDate(reservation);

  if (!startDate) {
    return null;
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const target = new Date(startDate);
  target.setHours(0, 0, 0, 0);

  return Math.round((target.getTime() - today.getTime()) / 86400000);
}

export function getReservationTimingLabel(reservation, t) {
  if (isPastReservation(reservation)) {
    if (reservation?.state === "ACCEPTED") {
      return t("reservations.timing.needsClosure");
    }

    if (reservation?.state === "PENDING") {
      return t("reservations.timing.expiredPending");
    }

    return t("reservations.timing.past");
  }

  const daysUntil = getReservationDaysUntil(reservation);

  if (daysUntil == null) {
    return "";
  }

  if (daysUntil === 0) {
    return t("reservations.timing.today");
  }

  if (daysUntil === 1) {
    return t("reservations.timing.tomorrow");
  }

  if (daysUntil > 1) {
    return t("reservations.timing.inDays", { count: daysUntil });
  }

  return "";
}

export function getReservationTimingTone(reservation) {
  if (isPastReservation(reservation)) {
    if (reservation?.state === "ACCEPTED") {
      return "attention";
    }

    if (reservation?.state === "PENDING") {
      return "danger";
    }

    return "muted";
  }

  const daysUntil = getReservationDaysUntil(reservation);

  if (daysUntil != null && daysUntil <= 1) {
    return "attention";
  }

  return "success";
}

function buildReservationDate(reservation, time) {
  if (!reservation?.sessionDate) {
    return null;
  }

  const date = new Date(`${reservation.sessionDate}T${time}:00`);
  return Number.isNaN(date.getTime()) ? null : date;
}

function getReservationTimestamp(reservation) {
  const startDate = getReservationStartDate(reservation);
  return startDate ? startDate.getTime() : Number.MAX_SAFE_INTEGER;
}

function getReservationPrice(reservation) {
  if (reservation?.finalPrice == null) {
    return Number.MAX_SAFE_INTEGER;
  }

  return Number(reservation.finalPrice);
}

function buildSearchHaystack(reservation) {
  return normalizeText([
    reservation?.musicalSpace?.name,
    reservation?.musicalSpace?.location?.city ?? reservation?.musicalSpace?.city,
    reservation?.musicalSpace?.location?.province ?? reservation?.musicalSpace?.province
  ].join(" "));
}

function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}
