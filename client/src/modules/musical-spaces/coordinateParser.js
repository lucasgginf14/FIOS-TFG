const COORDINATE_NUMBER_PATTERN = "[-+]?\\d{1,3}(?:[.,]\\d+)?";
const GOOGLE_AT_PATTERN = new RegExp(
  `@(${COORDINATE_NUMBER_PATTERN}),\\s*(${COORDINATE_NUMBER_PATTERN})`,
  "i"
);
const GOOGLE_QUERY_PATTERN = new RegExp(
  `[?&](?:q|ll)=(${COORDINATE_NUMBER_PATTERN}),\\s*(${COORDINATE_NUMBER_PATTERN})`,
  "i"
);
const OSM_HASH_PATTERN = new RegExp(
  `#map=\\d+\\/(${COORDINATE_NUMBER_PATTERN})\\/(${COORDINATE_NUMBER_PATTERN})`,
  "i"
);
const DMS_COORDINATE_PATTERN =
  /([-+]?\d{1,3})\s*(?:\u00b0|\u00ba)\s*(\d{1,2})\s*(?:'|\u2032|\u2019)\s*(\d{1,2}(?:[.,]\d+)?)\s*(?:"|\u2033|\u201d)?\s*([NSEW])/gi;

export function parseCoordinates(value) {
  const text = normalizeCoordinatesText(value);

  if (!text) {
    return null;
  }

  const knownMapCoordinates = parseKnownMapCoordinates(text);

  if (knownMapCoordinates) {
    return knownMapCoordinates;
  }

  const dmsCoordinates = parseDmsCoordinates(text);

  if (dmsCoordinates) {
    return dmsCoordinates;
  }

  if (looksLikeDms(text)) {
    return null;
  }

  const numericMatches = text.match(/[-+]?\d{1,3}(?:[.,]\d+)?/g) ?? [];

  for (let index = 0; index < numericMatches.length - 1; index += 1) {
    const coordinates = buildCoordinates(numericMatches[index], numericMatches[index + 1]);

    if (coordinates) {
      return coordinates;
    }
  }

  return null;
}

export function formatCoordinate(value) {
  return value.toFixed(6);
}

function parseKnownMapCoordinates(text) {
  const patterns = [GOOGLE_AT_PATTERN, GOOGLE_QUERY_PATTERN, OSM_HASH_PATTERN];

  for (const pattern of patterns) {
    const match = text.match(pattern);
    const coordinates = match ? buildCoordinates(match[1], match[2]) : null;

    if (coordinates) {
      return coordinates;
    }
  }

  const osmLatitude = findParameterValue(text, "mlat");
  const osmLongitude = findParameterValue(text, "mlon");

  if (osmLatitude != null && osmLongitude != null) {
    return buildCoordinates(osmLatitude, osmLongitude);
  }

  return null;
}

function parseDmsCoordinates(text) {
  DMS_COORDINATE_PATTERN.lastIndex = 0;

  const matches = [...text.matchAll(DMS_COORDINATE_PATTERN)]
    .map((match) => buildDmsCoordinate(match))
    .filter(Boolean);

  if (matches.length < 2) {
    return null;
  }

  const latitudeMatch = matches.find((match) => match.axis === "latitude");
  const longitudeMatch = matches.find((match) => match.axis === "longitude");

  if (!latitudeMatch || !longitudeMatch) {
    return null;
  }

  return buildCoordinates(latitudeMatch.value, longitudeMatch.value);
}

function buildDmsCoordinate(match) {
  const degrees = parseCoordinateNumber(match[1]);
  const minutes = parseCoordinateNumber(match[2]);
  const seconds = parseCoordinateNumber(match[3]);
  const direction = match[4].toUpperCase();

  if (
    !Number.isFinite(degrees) ||
    !Number.isFinite(minutes) ||
    !Number.isFinite(seconds) ||
    minutes < 0 ||
    minutes > 59 ||
    seconds < 0 ||
    seconds >= 60
  ) {
    return null;
  }

  const axis = direction === "N" || direction === "S" ? "latitude" : "longitude";
  const sign = direction === "S" || direction === "W" ? -1 : 1;
  const absoluteValue = Math.abs(degrees) + minutes / 60 + seconds / 3600;
  const value = absoluteValue * sign;

  if (axis === "latitude" && Math.abs(value) > 90) {
    return null;
  }

  if (axis === "longitude" && Math.abs(value) > 180) {
    return null;
  }

  return { axis, value };
}

function findParameterValue(text, key) {
  const pattern = new RegExp(`[?&#]${key}=(${COORDINATE_NUMBER_PATTERN})`, "i");
  const match = text.match(pattern);
  return match?.[1] ?? null;
}

function normalizeCoordinatesText(value) {
  try {
    return decodeURIComponent(String(value || ""))
      .replaceAll(/[−–—]/g, "-")
      .trim();
  } catch {
    return String(value || "")
      .replaceAll(/[−–—]/g, "-")
      .trim();
  }
}

function buildCoordinates(latitudeValue, longitudeValue) {
  const latitude = parseCoordinateNumber(latitudeValue);
  const longitude = parseCoordinateNumber(longitudeValue);

  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
    return null;
  }

  if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
    return null;
  }

  return { latitude, longitude };
}

function parseCoordinateNumber(value) {
  return Number(String(value).replace(",", "."));
}

function looksLikeDms(text) {
  return /(?:\u00b0|\u00ba).*[NSEW]/i.test(text);
}
