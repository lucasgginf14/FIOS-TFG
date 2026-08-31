import { getApiErrorMessage } from "../../common/apiErrors.js";

const EVENT_TYPE_KEYS = ["CONCERT", "FESTIVAL", "WORKSHOP", "OPEN_MIC", "JAM_SESSION", "SHOWCASE", "OTHER"];
const EVENT_SOURCE_KEYS = ["INTERNAL", "EXTERNAL"];
const EVENT_STATUS_KEYS = ["DRAFT", "PUBLISHED", "CANCELLED", "ARCHIVED"];
const TICKETMASTER_MIN_START_DATE = getTodayString();

export {
  EVENT_SOURCE_KEYS,
  EVENT_STATUS_KEYS,
  EVENT_TYPE_KEYS,
  TICKETMASTER_MIN_START_DATE,
  buildAdminEventPayload,
  buildBandEventPayload,
  buildEventDetailMeta,
  buildEventQueryFromRoute,
  buildEventRouteQuery,
  buildMapIntersection,
  createEmptyEventForm,
  createEventFilters,
  filterEvents,
  formatEventDate,
  formatEventDateTime,
  formatEventMoney,
  formatEventTimeRange,
  getEventBadgeTone,
  getEventImage,
  getEventLocationLabel,
  getEventMapSubtitle,
  getEventStatusTone,
  getEventTicketValidationKey,
  hasEventCoordinates,
  hasMeaningfulEventFilters,
  mapEventToForm,
  matchesQuickTab,
  normalizeTicketmasterSearch,
  resolveTicketmasterErrorMessage,
  sortEvents,
  toEventDateTime
};

function createEventFilters(overrides = {}) {
  return {
    text: "",
    city: "",
    date: "",
    musicalGenre: "",
    eventType: "",
    source: "",
    order: "nearest",
    freeOnly: false,
    ...overrides
  };
}

function buildEventQueryFromRoute(query = {}) {
  return createEventFilters({
    text: normalizeString(query.q || query.text),
    city: normalizeString(query.city),
    date: normalizeString(query.date),
    musicalGenre: normalizeString(query.musicalGenre || query.genre),
    eventType: normalizeString(query.eventType),
    source: normalizeString(query.source),
    order: normalizeString(query.order) || "nearest",
    freeOnly: query.freeOnly === "true" || query.freeOnly === true
  });
}

function buildEventRouteQuery(filters, view, quickTab) {
  const query = {};

  if (filters.text) query.q = filters.text;
  if (filters.city) query.city = filters.city;
  if (filters.date) query.date = filters.date;
  if (filters.musicalGenre) query.musicalGenre = filters.musicalGenre;
  if (filters.eventType) query.eventType = filters.eventType;
  if (filters.source) query.source = filters.source;
  if (filters.order && filters.order !== "nearest") query.order = filters.order;
  if (filters.freeOnly) query.freeOnly = "true";
  if (view === "map") query.view = "map";
  if (quickTab) query.quick = quickTab;

  return query;
}

function hasMeaningfulEventFilters(filters) {
  return Boolean(
    filters.text ||
      filters.city ||
      filters.date ||
      filters.musicalGenre ||
      filters.eventType ||
      filters.source ||
      filters.freeOnly ||
      (filters.order && filters.order !== "nearest")
  );
}

function filterEvents(events, filters, quickTab) {
  return (events ?? []).filter((event) => {
    if (!matchesQuickTab(event, quickTab)) {
      return false;
    }

    const text = normalizeString(filters.text).toLowerCase();

    if (text) {
      const haystack = [
        event.title,
        event.city,
        event.venueName,
        event.musicalGenre,
        event.location,
        event.description
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      if (!haystack.includes(text)) {
        return false;
      }
    }

    if (filters.city && normalizeString(event.city).toLowerCase() !== filters.city.toLowerCase()) {
      return false;
    }

    if (filters.date && event.eventDate !== filters.date) {
      return false;
    }

    if (
      filters.musicalGenre &&
      normalizeString(event.musicalGenre).toLowerCase() !== filters.musicalGenre.toLowerCase()
    ) {
      return false;
    }

    if (filters.eventType && event.eventType !== filters.eventType) {
      return false;
    }

    if (filters.source && event.source !== filters.source) {
      return false;
    }

    if (filters.freeOnly && !isFreeEvent(event)) {
      return false;
    }

    return true;
  });
}

function sortEvents(events, order = "nearest") {
  const list = [...(events ?? [])];

  return list.sort((left, right) => {
    if (order === "priceAsc" || order === "priceDesc") {
      const leftPrice = toSortablePrice(left.ticketPrice);
      const rightPrice = toSortablePrice(right.ticketPrice);
      return order === "priceAsc" ? leftPrice - rightPrice : rightPrice - leftPrice;
    }

    if (order === "farthest") {
      return toEventDateTime(right) - toEventDateTime(left);
    }

    return toEventDateTime(left) - toEventDateTime(right);
  });
}

function matchesQuickTab(event, quickTab) {
  if (!quickTab) {
    return true;
  }

  const eventDate = event?.eventDate;
  if (!eventDate) {
    return quickTab !== "today" && quickTab !== "week";
  }

  const today = getTodayString();

  if (quickTab === "upcoming") {
    return eventDate >= today;
  }

  if (quickTab === "today") {
    return eventDate === today;
  }

  if (quickTab === "week") {
    return eventDate >= today && eventDate <= getWeekEndString();
  }

  if (quickTab === "free") {
    return isFreeEvent(event);
  }

  if (quickTab === "external") {
    return event.source === "EXTERNAL";
  }

  return true;
}

function buildMapIntersection(mapItems, visibleEvents) {
  const allowedIds = new Set((visibleEvents ?? []).map((item) => item.id));
  return (mapItems ?? []).filter((item) => allowedIds.has(item.id));
}

function formatEventDate(value, locale) {
  if (!value) return "--";

  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat(locale || "es", {
    day: "numeric",
    month: "short",
    year: "numeric"
  }).format(date);
}

function formatEventDateTime(event, locale) {
  const dateLabel = formatEventDate(event?.eventDate, locale);
  const timeLabel = formatEventTimeRange(event);
  return timeLabel === "--" ? dateLabel : `${dateLabel} · ${timeLabel}`;
}

function formatEventTimeRange(event) {
  const start = normalizeTimeString(event?.startTime);
  const end = normalizeTimeString(event?.endTime);
  if (!start && !end) return "--";
  return [start, end].filter(Boolean).join(" - ");
}

function formatEventMoney(value, locale, fallback = "--", freeLabel = "Gratis") {
  const amount = toNumber(value);
  if (amount == null) return fallback;
  if (amount === 0) return freeLabel;

  return new Intl.NumberFormat(locale || "es", {
    style: "currency",
    currency: "EUR",
    minimumFractionDigits: amount % 1 === 0 ? 0 : 2,
    maximumFractionDigits: 2
  }).format(amount);
}

function hasEventCoordinates(event) {
  return typeof event?.latitude === "number" && typeof event?.longitude === "number";
}

function getEventImage(event) {
  return event?.posterImage || event?.mainImage || "";
}

function getEventLocationLabel(event) {
  return [event?.venueName, event?.city, event?.province].filter(Boolean).join(" - ");
}

function getEventMapSubtitle(event) {
  return [event?.location, event?.city, event?.province, event?.country].filter(Boolean).join(", ");
}

function getEventBadgeTone(source) {
  return source === "EXTERNAL" ? "event-badge--external" : "event-badge--internal";
}

function getEventStatusTone(status) {
  const tones = {
    PUBLISHED: "event-badge--published",
    DRAFT: "event-badge--draft",
    CANCELLED: "event-badge--cancelled",
    ARCHIVED: "event-badge--archived"
  };

  return tones[status] || "event-badge--draft";
}

function buildEventDetailMeta(event, locale, t) {
  return [
    {
      icon: "bi bi-calendar-event",
      label: formatEventDate(event.eventDate, locale)
    },
    {
      icon: "bi bi-clock",
      label: formatEventTimeRange(event)
    },
    {
      icon: "bi bi-geo-alt",
      label: [event.city, event.country].filter(Boolean).join(", ") || t("events.detail.noLocation")
    },
    {
      icon: "bi bi-shop",
      label: event.venueName || t("events.detail.noVenue")
    }
  ];
}

function createEmptyEventForm() {
  return {
    title: "",
    description: "",
    eventDate: "",
    startTime: "",
    endTime: "",
    musicalGenre: "",
    capacity: "",
    ticketPrice: "",
    posterImage: "",
    status: "PUBLISHED",
    eventType: "CONCERT",
    source: "INTERNAL",
    venueName: "",
    latitude: "",
    longitude: "",
    city: "",
    province: "",
    country: "Spain",
    location: "",
    externalUrl: "",
    musicalSpaceId: "",
    bandId: ""
  };
}

function mapEventToForm(event) {
  return {
    title: event?.title || "",
    description: event?.description || "",
    eventDate: event?.eventDate || "",
    startTime: normalizeTimeString(event?.startTime),
    endTime: normalizeTimeString(event?.endTime),
    musicalGenre: event?.musicalGenre || "",
    capacity: event?.capacity != null ? String(event.capacity) : "",
    ticketPrice: event?.ticketPrice != null ? String(event.ticketPrice) : "",
    posterImage: event?.posterImage || "",
    status: event?.status || "PUBLISHED",
    eventType: event?.eventType || "CONCERT",
    source: event?.source || "INTERNAL",
    venueName: event?.venueName || "",
    latitude: event?.latitude != null ? String(event.latitude) : "",
    longitude: event?.longitude != null ? String(event.longitude) : "",
    city: event?.city || "",
    province: event?.province || "",
    country: event?.country || "Spain",
    location: event?.location || "",
    externalUrl: event?.externalUrl || "",
    musicalSpaceId: event?.musicalSpace?.id != null ? String(event.musicalSpace.id) : "",
    bandId: event?.band?.id != null ? String(event.band.id) : ""
  };
}

function buildAdminEventPayload(form) {
  return {
    title: normalizeString(form.title),
    description: normalizeString(form.description),
    eventDate: normalizeString(form.eventDate) || null,
    startTime: normalizeString(form.startTime) || null,
    endTime: normalizeString(form.endTime) || null,
    musicalGenre: normalizeString(form.musicalGenre) || null,
    capacity: toInteger(form.capacity),
    ticketPrice: form.ticketPrice === "" ? null : toNumber(form.ticketPrice),
    posterImage: normalizeString(form.posterImage) || null,
    status: normalizeString(form.status) || null,
    eventType: normalizeString(form.eventType) || null,
    source: normalizeString(form.source) || "INTERNAL",
    venueName: normalizeString(form.venueName),
    latitude: form.latitude === "" ? null : toNumber(form.latitude),
    longitude: form.longitude === "" ? null : toNumber(form.longitude),
    city: normalizeString(form.city),
    province: normalizeString(form.province) || null,
    country: normalizeString(form.country),
    location: normalizeString(form.location) || null,
    externalSource: form.source === "EXTERNAL" ? "TICKETMASTER" : null,
    externalId: null,
    externalUrl: normalizeString(form.externalUrl) || null,
    musicalSpaceId: toInteger(form.musicalSpaceId),
    bandId: toInteger(form.bandId)
  };
}

function buildBandEventPayload(form) {
  return {
    ...buildAdminEventPayload({
      ...form,
      status: "DRAFT",
      source: "INTERNAL",
      externalUrl: ""
    }),
    status: "DRAFT",
    source: "INTERNAL",
    externalSource: null,
    externalId: null,
    externalUrl: null,
    musicalSpaceId: toInteger(form.musicalSpaceId),
    bandId: toInteger(form.bandId)
  };
}

function getEventTicketValidationKey(payload) {
  const capacity = toNumber(payload?.capacity);
  const ticketPrice = toNumber(payload?.ticketPrice);

  if (payload?.source === "INTERNAL" && (capacity == null || capacity <= 0)) {
    return "events.admin.capacityValidation";
  }

  if (capacity != null && capacity <= 0) {
    return "events.admin.capacityValidation";
  }

  if (ticketPrice != null && ticketPrice < 0) {
    return "events.admin.priceValidation";
  }

  return "";
}

function normalizeTicketmasterSearch(filters) {
  const startDate = normalizeDateAtLeast(
    normalizeString(filters.startDate),
    TICKETMASTER_MIN_START_DATE
  );
  const endDate = normalizeString(filters.endDate);

  return {
    city: normalizeString(filters.city) || undefined,
    keyword: normalizeString(filters.keyword) || undefined,
    musicalGenre: normalizeString(filters.musicalGenre) || undefined,
    startDate,
    endDate: endDate && endDate >= startDate ? endDate : undefined,
    countryCode: normalizeString(filters.countryCode) || "ES",
    size: 12
  };
}

function resolveTicketmasterErrorMessage(error, translate, fallback) {
  const message = error?.response?.data?.message || "";

  if (message.includes("API key")) {
    return translate("events.ticketmaster.errors.missingKey");
  }

  if (message.includes("disabled")) {
    return translate("events.ticketmaster.errors.disabled");
  }

  if (message.includes("did not respond in time")) {
    return translate("events.ticketmaster.errors.timeout");
  }

  if (message.includes("(429)")) {
    return translate("events.ticketmaster.errors.rateLimit");
  }

  if (message.includes("HTTP error")) {
    return translate("events.ticketmaster.errors.provider");
  }

  return getApiErrorMessage(error, translate, fallback);
}

function normalizeDateAtLeast(value, minimumDate) {
  if (!value || value < minimumDate) {
    return minimumDate;
  }

  return value;
}

function isFreeEvent(event) {
  const amount = toNumber(event?.ticketPrice);
  return amount != null && amount <= 0;
}

function toEventDateTime(event) {
  const date = event?.eventDate || "9999-12-31";
  const time = normalizeTimeString(event?.startTime) || "23:59";
  return new Date(`${date}T${time}:00`).getTime();
}

function toSortablePrice(value) {
  const amount = toNumber(value);
  return amount == null ? Number.POSITIVE_INFINITY : amount;
}

function toNumber(value) {
  if (value == null || value === "") return null;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
}

function toInteger(value) {
  if (value == null || value === "") return null;
  const parsed = Number.parseInt(value, 10);
  return Number.isFinite(parsed) ? parsed : null;
}

function normalizeString(value) {
  return typeof value === "string" ? value.trim() : "";
}

function normalizeTimeString(value) {
  if (!value) return "";
  return String(value).slice(0, 5);
}

function getTodayString() {
  return toLocalDateString(new Date());
}

function getWeekEndString() {
  const date = new Date();
  date.setDate(date.getDate() + 7);
  return toLocalDateString(date);
}

function toLocalDateString(date) {
  const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}
