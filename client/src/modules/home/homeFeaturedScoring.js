const FEATURED_LIMIT = 3;
const MS_PER_DAY = 24 * 60 * 60 * 1000;
const SPACE_REVIEW_CONFIDENCE_TARGET = 20;

const SPACE_WEIGHTS = {
  rating: 0.45,
  reviewConfidence: 0.25,
  capacity: 0.15,
  soundproofed: 0.1,
  image: 0.05
};

const EVENT_WEIGHTS = {
  proximity: 0.4,
  image: 0.2,
  capacity: 0.15,
  accessiblePrice: 0.1,
  completeness: 0.1,
  fiosLink: 0.05
};

const RECRUITMENT_WEIGHTS = {
  recency: 0.35,
  completeness: 0.25,
  vacancies: 0.15,
  bandImage: 0.1,
  bandGenre: 0.1,
  instrument: 0.05
};

export { selectFeaturedEvents, selectFeaturedRecruitments, selectFeaturedSpaces };

function selectFeaturedSpaces(spaces, limit = FEATURED_LIMIT) {
  return (spaces ?? [])
    .map((space) => ({
      ...space,
      featuredScore: scoreSpace(space),
      featuredReason: getSpaceFeaturedReason(space)
    }))
    .sort(compareSpaces)
    .slice(0, limit);
}

function selectFeaturedEvents(events, limit = FEATURED_LIMIT, now = new Date()) {
  return (events ?? [])
    .filter((event) => isUpcomingPublishedEvent(event, now))
    .map((event) => ({
      ...event,
      featuredScore: scoreEvent(event, now),
      featuredReason: getEventFeaturedReason(event, now)
    }))
    .sort((left, right) => compareEvents(left, right, now))
    .slice(0, limit);
}

function selectFeaturedRecruitments(recruitments, limit = FEATURED_LIMIT, now = new Date()) {
  return (recruitments ?? [])
    .filter((recruitment) => recruitment?.status === "OPEN" && recruitment?.band?.active !== false)
    .map((recruitment) => ({
      ...recruitment,
      featuredScore: scoreRecruitment(recruitment, now)
    }))
    .sort(compareRecruitments)
    .slice(0, limit);
}

function scoreSpace(space) {
  const rating = clamp01(toNumber(space?.rating) / 5);
  const reviewConfidence = normalizeReviewConfidence(space?.reviewsCount);
  const capacity = clamp01(toNumber(space?.capacity) / 50);
  const soundproofed = space?.soundproofed === true ? 1 : 0;
  const image = hasText(space?.mainImage) ? 1 : 0;

  return toScore(
    SPACE_WEIGHTS.rating * rating +
      SPACE_WEIGHTS.reviewConfidence * reviewConfidence +
      SPACE_WEIGHTS.capacity * capacity +
      SPACE_WEIGHTS.soundproofed * soundproofed +
      SPACE_WEIGHTS.image * image
  );
}

function scoreEvent(event, now) {
  const proximity = normalizeEventProximity(event, now);
  const image = hasAnyText(event?.posterImage, event?.mainImage, event?.image) ? 1 : 0;
  const capacity = clamp01(toNumber(event?.capacity) / 500);
  const accessiblePrice = normalizeAccessiblePrice(event?.ticketPrice);
  const completeness = normalizeCompleteness([
    event?.musicalGenre,
    event?.city,
    event?.venueName,
    event?.startTime || event?.endTime
  ]);
  const fiosLink = event?.musicalSpace?.id || event?.band?.id ? 1 : 0;

  return toScore(
    EVENT_WEIGHTS.proximity * proximity +
      EVENT_WEIGHTS.image * image +
      EVENT_WEIGHTS.capacity * capacity +
      EVENT_WEIGHTS.accessiblePrice * accessiblePrice +
      EVENT_WEIGHTS.completeness * completeness +
      EVENT_WEIGHTS.fiosLink * fiosLink
  );
}

function scoreRecruitment(recruitment, now) {
  const recency = normalizeRecency(recruitment?.publicationDate, now);
  const completeness = normalizeCompleteness([
    recruitment?.title,
    recruitment?.description,
    recruitment?.city,
    recruitment?.levelRequired,
    recruitment?.roleWanted
  ]);
  const vacancies = clamp01(toNumber(recruitment?.vacancies) / 5);
  const bandImage = hasText(recruitment?.band?.image) ? 1 : 0;
  const bandGenre = hasText(recruitment?.band?.mainGenre) ? 1 : 0;
  const instrument = recruitment?.instrument?.id || hasText(recruitment?.instrument?.name) ? 1 : 0;

  return toScore(
    RECRUITMENT_WEIGHTS.recency * recency +
      RECRUITMENT_WEIGHTS.completeness * completeness +
      RECRUITMENT_WEIGHTS.vacancies * vacancies +
      RECRUITMENT_WEIGHTS.bandImage * bandImage +
      RECRUITMENT_WEIGHTS.bandGenre * bandGenre +
      RECRUITMENT_WEIGHTS.instrument * instrument
  );
}

function getSpaceFeaturedReason(space) {
  if (toNumber(space?.rating) >= 4.5 && toNumber(space?.reviewsCount) >= 3) {
    return "highRating";
  }

  if (space?.soundproofed === true) {
    return "soundproofed";
  }

  if (toNumber(space?.capacity) >= 20) {
    return "large";
  }

  if (hasText(space?.mainImage)) {
    return "completeProfile";
  }

  return "balanced";
}

function getEventFeaturedReason(event, now) {
  const days = daysUntil(event?.eventDate, now);

  if (days === 0) {
    return "today";
  }

  if (Number.isFinite(days) && days <= 7) {
    return "soon";
  }

  if (toOptionalNumber(event?.ticketPrice) == null || toOptionalNumber(event?.ticketPrice) <= 15) {
    return "accessible";
  }

  if (toNumber(event?.capacity) >= 500) {
    return "large";
  }

  if (event?.musicalSpace?.id || event?.band?.id) {
    return "linked";
  }

  return "upcoming";
}

function compareSpaces(left, right) {
  return (
    compareScore(left, right) ||
    compareNumbersDesc(left.rating, right.rating) ||
    compareNumbersDesc(left.reviewsCount, right.reviewsCount) ||
    compareText(left.name, right.name) ||
    compareNumbersAsc(left.id, right.id)
  );
}

function compareEvents(left, right, now) {
  return (
    compareScore(left, right) ||
    compareNumbersAsc(daysUntil(left.eventDate, now), daysUntil(right.eventDate, now)) ||
    compareTimeAsc(left.startTime, right.startTime) ||
    compareNumbersAsc(left.id, right.id)
  );
}

function compareRecruitments(left, right) {
  return (
    compareScore(left, right) ||
    compareDatesDesc(left.publicationDate, right.publicationDate) ||
    compareNumbersDesc(left.id, right.id)
  );
}

function compareScore(left, right) {
  return compareNumbersDesc(left.featuredScore, right.featuredScore);
}

function normalizeReviewConfidence(reviewsCount) {
  const count = Math.max(0, toNumber(reviewsCount));

  if (!count) {
    return 0;
  }

  return clamp01(Math.log1p(count) / Math.log1p(SPACE_REVIEW_CONFIDENCE_TARGET));
}

function normalizeEventProximity(event, now) {
  const days = daysUntil(event?.eventDate, now);

  if (!Number.isFinite(days)) {
    return 0;
  }

  return 1 / (1 + Math.max(0, days));
}

function normalizeAccessiblePrice(ticketPrice) {
  const price = toOptionalNumber(ticketPrice);

  if (price == null || price <= 15) {
    return 1;
  }

  if (price <= 30) {
    return 0.5;
  }

  return 0;
}

function normalizeCompleteness(values) {
  if (!values.length) {
    return 0;
  }

  return values.filter(hasText).length / values.length;
}

function normalizeRecency(value, now) {
  const date = parseDateTime(value);

  if (!date) {
    return 0;
  }

  const days = Math.max(0, Math.floor((now.getTime() - date.getTime()) / MS_PER_DAY));
  return 1 / (1 + days);
}

function isUpcomingPublishedEvent(event, now) {
  if (!event || event.status !== "PUBLISHED") {
    return false;
  }

  const eventDate = parseLocalDate(event.eventDate);

  if (!eventDate) {
    return false;
  }

  const today = startOfDay(now);

  if (eventDate.getTime() > today.getTime()) {
    return true;
  }

  if (eventDate.getTime() < today.getTime()) {
    return false;
  }

  const nowMinutes = now.getHours() * 60 + now.getMinutes();
  const endMinutes = toTimeMinutes(event.endTime);
  const startMinutes = toTimeMinutes(event.startTime);

  if (endMinutes != null) {
    return endMinutes >= nowMinutes;
  }

  if (startMinutes != null) {
    return startMinutes >= nowMinutes;
  }

  return true;
}

function daysUntil(value, now) {
  const date = parseLocalDate(value);

  if (!date) {
    return Number.POSITIVE_INFINITY;
  }

  return Math.max(0, Math.round((date.getTime() - startOfDay(now).getTime()) / MS_PER_DAY));
}

function parseLocalDate(value) {
  if (!hasText(value)) {
    return null;
  }

  const [year, month, day] = String(value).slice(0, 10).split("-").map(Number);

  if (!year || !month || !day) {
    return null;
  }

  return new Date(year, month - 1, day);
}

function parseDateTime(value) {
  if (!hasText(value)) {
    return null;
  }

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function startOfDay(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

function toTimeMinutes(value) {
  if (!hasText(value)) {
    return null;
  }

  const [hours, minutes] = String(value).split(":").map(Number);

  if (!Number.isFinite(hours) || !Number.isFinite(minutes)) {
    return null;
  }

  return hours * 60 + minutes;
}

function toScore(value) {
  return Math.round(clamp01(value) * 10000) / 100;
}

function clamp01(value) {
  if (!Number.isFinite(value)) {
    return 0;
  }

  return Math.min(1, Math.max(0, value));
}

function toNumber(value) {
  return toOptionalNumber(value) ?? 0;
}

function toOptionalNumber(value) {
  const numberValue = Number(value);
  return Number.isFinite(numberValue) ? numberValue : null;
}

function hasAnyText(...values) {
  return values.some(hasText);
}

function hasText(value) {
  return typeof value === "string" ? value.trim().length > 0 : value != null && value !== false;
}

function compareNumbersDesc(left, right) {
  return toNumber(right) - toNumber(left);
}

function compareNumbersAsc(left, right) {
  return toNumber(left) - toNumber(right);
}

function compareText(left, right) {
  return String(left ?? "").localeCompare(String(right ?? ""), undefined, { sensitivity: "base" });
}

function compareTimeAsc(left, right) {
  const leftMinutes = toTimeMinutes(left);
  const rightMinutes = toTimeMinutes(right);

  if (leftMinutes == null && rightMinutes == null) {
    return 0;
  }

  if (leftMinutes == null) {
    return 1;
  }

  if (rightMinutes == null) {
    return -1;
  }

  return leftMinutes - rightMinutes;
}

function compareDatesDesc(left, right) {
  return (parseDateTime(right)?.getTime() ?? 0) - (parseDateTime(left)?.getTime() ?? 0);
}
