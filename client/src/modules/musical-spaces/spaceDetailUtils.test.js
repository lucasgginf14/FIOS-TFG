import assert from "node:assert/strict";
import test from "node:test";

import { estimateReservationPrice, getDurationOptions, getStartTimeOptions } from "./spaceDetailUtils.js";

test("builds selectable start times inside an availability slot", () => {
  assert.deepEqual(
    getStartTimeOptions({ startTime: "10:00", endTime: "14:00" }),
    ["10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30"]
  );
});

test("does not offer a start time that cannot fit the minimum duration", () => {
  assert.deepEqual(
    getStartTimeOptions({ startTime: "12:00", endTime: "13:00" }),
    ["12:00", "12:30"]
  );
  assert.deepEqual(getStartTimeOptions({ startTime: "12:45", endTime: "13:00" }), []);
});

test("calculates durations from the selected start time to the slot end", () => {
  assert.deepEqual(
    getDurationOptions({ startTime: "12:00", endTime: "14:00" }),
    [30, 60, 90, 120]
  );
});

test("allows booking a partial two hour range inside a morning slot", () => {
  assert.ok(getStartTimeOptions({ startTime: "10:00", endTime: "14:00" }).includes("11:30"));
  assert.ok(getDurationOptions({ startTime: "11:30", endTime: "14:00" }).includes(120));
});

test("estimates reservation price from normal weekly schedules", () => {
  assert.equal(
    estimateReservationPrice({
      schedules: [{ dayOfWeek: "MONDAY", startTime: "18:00", endTime: "21:00", price: 30 }],
      sessionDate: "2026-07-27",
      startTime: "18:30",
      endTime: "20:00"
    }),
    15
  );
});

test("estimates custom availability price from backend slot price", () => {
  assert.equal(
    estimateReservationPrice({
      schedules: [{ dayOfWeek: "MONDAY", startTime: "16:00", endTime: "18:00", price: 10 }],
      slot: { startTime: "16:00", endTime: "18:00", price: 28 },
      sessionDate: "2026-07-27",
      startTime: "16:30",
      endTime: "17:30"
    }),
    14
  );
});

test("does not fall back to weekly schedules when a backend slot has no valid price", () => {
  assert.equal(
    estimateReservationPrice({
      schedules: [{ dayOfWeek: "MONDAY", startTime: "16:00", endTime: "18:00", price: 10 }],
      slot: { startTime: "16:00", endTime: "18:00", price: null },
      sessionDate: "2026-07-27",
      startTime: "16:30",
      endTime: "17:30"
    }),
    null
  );
});
