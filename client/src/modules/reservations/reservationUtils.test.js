import assert from "node:assert/strict";
import test from "node:test";

import {
  filterReservations,
  getReservationStats,
  getUpcomingReservation,
  matchesStatusTab,
  sortReservations
} from "./reservationUtils.js";

function reservation(overrides = {}) {
  return {
    id: overrides.id ?? 1,
    sessionDate: overrides.sessionDate ?? "2099-01-10",
    startTime: overrides.startTime ?? "10:00:00",
    endTime: overrides.endTime ?? "12:00:00",
    state: overrides.state ?? "PENDING",
    sessionType: overrides.sessionType ?? "REHEARSAL",
    finalPrice: overrides.finalPrice ?? 25,
    musicalSpace: {
      name: overrides.spaceName ?? "Sala FIOS",
      city: overrides.city ?? "Santiago de Compostela",
      province: overrides.province ?? "A Coruna"
    }
  };
}

const sampleReservations = [
  reservation({ id: 1, sessionDate: "2099-01-05", state: "ACCEPTED", finalPrice: 30 }),
  reservation({
    id: 2,
    sessionDate: "2099-01-03",
    state: "PENDING",
    sessionType: "RECORDING",
    finalPrice: 45,
    spaceName: "Estudio Atlántico",
    city: "A Coruña"
  }),
  reservation({ id: 3, sessionDate: "2000-01-01", state: "COMPLETED", finalPrice: 10 }),
  reservation({ id: 4, sessionDate: "2099-01-20", state: "CANCELLED", finalPrice: 15 })
];

test("calculates reservation stats from state and timing", () => {
  assert.deepEqual(getReservationStats(sampleReservations), {
    active: 2,
    upcoming: 2,
    completed: 1,
    cancelled: 1
  });
});

test("finds the nearest future active reservation", () => {
  assert.equal(getUpcomingReservation(sampleReservations).id, 2);
});

test("filters reservations by status, text, date and session type", () => {
  const filtered = filterReservations(sampleReservations, {
    statusTab: "pending",
    searchText: "coruna",
    date: "2099-01-03",
    sessionType: "RECORDING"
  });

  assert.deepEqual(filtered.map((item) => item.id), [2]);
});

test("sorts reservations by price and date", () => {
  assert.deepEqual(sortReservations(sampleReservations, "priceAsc").map((item) => item.id), [
    3,
    4,
    1,
    2
  ]);
  assert.deepEqual(sortReservations(sampleReservations, "nearest").map((item) => item.id), [
    3,
    2,
    1,
    4
  ]);
});

test("keeps rejected and cancelled reservations under cancelled tab", () => {
  assert.equal(matchesStatusTab(reservation({ state: "REJECTED" }), "cancelled"), true);
});
