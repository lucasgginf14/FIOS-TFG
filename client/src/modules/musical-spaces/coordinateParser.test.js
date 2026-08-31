import assert from "node:assert/strict";
import test from "node:test";

import { formatCoordinate, parseCoordinates } from "./coordinateParser.js";

function assertCoordinates(input, expectedLatitude, expectedLongitude) {
  const coordinates = parseCoordinates(input);

  assert.notEqual(coordinates, null);
  assert.equal(formatCoordinate(coordinates.latitude), expectedLatitude);
  assert.equal(formatCoordinate(coordinates.longitude), expectedLongitude);
}

test("parses already supported decimal coordinate formats", () => {
  assertCoordinates("43.3623, -8.4115", "43.362300", "-8.411500");
  assertCoordinates(
    "https://www.google.com/maps/@43.3623,-8.4115,17z",
    "43.362300",
    "-8.411500"
  );
  assertCoordinates(
    "https://www.openstreetmap.org/#map=16/43.3623/-8.4115",
    "43.362300",
    "-8.411500"
  );
  assertCoordinates(
    "https://www.openstreetmap.org/?mlon=-8.4115&mlat=43.3623#map=16/43.3623/-8.4115",
    "43.362300",
    "-8.411500"
  );
});

test("parses DMS coordinates copied from Google Maps", () => {
  assertCoordinates("43°20'46.7\"N 8°24'44.1\"W", "43.346306", "-8.412250");
  assertCoordinates("43°20'46\"N 8°24'44\"W", "43.346111", "-8.412222");
  assertCoordinates("43°20'46.7\"n 8°24'44.1\"w", "43.346306", "-8.412250");
  assertCoordinates("43° 20' 46.7\" N   8° 24' 44.1\" W", "43.346306", "-8.412250");
  assertCoordinates("40°25'0\"S 3°42'0\"E", "-40.416667", "3.700000");
});

test("rejects invalid DMS minutes and seconds", () => {
  assert.equal(parseCoordinates("43°60'46.7\"N 8°24'44.1\"W"), null);
  assert.equal(parseCoordinates("43°20'60\"N 8°24'44\"W"), null);
  assert.equal(parseCoordinates("43°20'60.1\"N 8°24'44\"W"), null);
});

test("rejects DMS coordinates outside latitude and longitude ranges", () => {
  assert.equal(parseCoordinates("91°0'0\"N 8°24'44.1\"W"), null);
  assert.equal(parseCoordinates("43°20'46.7\"N 181°0'0\"E"), null);
});
