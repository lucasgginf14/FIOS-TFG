import assert from "node:assert/strict";
import test from "node:test";

import {
  buildExceptionPayload,
  buildSchedulePayload,
  buildSchedulePayloads,
  canManageApprovedAvailability,
  sortExceptions,
  sortSchedules
} from "./spaceAvailabilityManageUtils.js";

test("builds valid schedule payloads and rejects invalid time ranges", () => {
  assert.deepEqual(
    buildSchedulePayload({
      dayOfWeek: "MONDAY",
      startTime: "10:00",
      endTime: "12:00",
      price: "24"
    }).payload,
    {
      dayOfWeek: "MONDAY",
      startTime: "10:00",
      endTime: "12:00",
      price: 24
    }
  );

  assert.equal(
    buildSchedulePayload({
      dayOfWeek: "MONDAY",
      startTime: "12:00",
      endTime: "12:00",
      price: "24"
    }).errorKey,
    "timeRange"
  );
});

test("builds one schedule payload per selected weekday", () => {
  const result = buildSchedulePayloads({
    dayOfWeeks: ["MONDAY", "TUESDAY", "WEDNESDAY", "FRIDAY"],
    startTime: "10:00",
    endTime: "12:00",
    price: "24"
  });

  assert.deepEqual(
    result.payloads.map((payload) => payload.dayOfWeek),
    ["MONDAY", "TUESDAY", "WEDNESDAY", "FRIDAY"]
  );
  assert.equal(result.payloads.every((payload) => payload.startTime === "10:00" && payload.price === 24), true);
});

test("rejects empty schedule day selections", () => {
  assert.equal(
    buildSchedulePayloads({
      dayOfWeeks: [],
      startTime: "10:00",
      endTime: "12:00",
      price: "24"
    }).errorKey,
    "scheduleDay"
  );
});

test("normalizes exception prices according to the selected type", () => {
  assert.deepEqual(
    buildExceptionPayload({
      date: "2099-01-10",
      startTime: "18:00",
      endTime: "19:00",
      exceptionType: "BLOCKED",
      reason: "Maintenance",
      price: "50"
    }).payload,
    {
      date: "2099-01-10",
      startTime: "18:00",
      endTime: "19:00",
      exceptionType: "BLOCKED",
      reason: "Maintenance",
      price: null
    }
  );

  assert.equal(
    buildExceptionPayload({
      date: "2099-01-11",
      startTime: "18:00",
      endTime: "19:00",
      exceptionType: "CUSTOM_AVAILABILITY",
      reason: "",
      price: ""
    }).errorKey,
    "customPrice"
  );
});

test("allows approved active spaces to be managed only by owners or admins", () => {
  const space = {
    active: true,
    approvalStatus: "APPROVED",
    manager: { id: 7 }
  };

  assert.equal(canManageApprovedAvailability(space, { logged: true, id: 7, platformRole: "USER" }), true);
  assert.equal(canManageApprovedAvailability(space, { logged: true, id: 2, platformRole: "ADMIN" }), true);
  assert.equal(canManageApprovedAvailability(space, { logged: true, id: 3, platformRole: "USER" }), false);
  assert.equal(canManageApprovedAvailability({ ...space, approvalStatus: "PENDING" }, { logged: true, id: 7 }), false);
});

test("sorts schedules and exceptions in calendar order", () => {
  assert.deepEqual(
    sortSchedules([
      { id: 1, dayOfWeek: "FRIDAY", startTime: "18:00:00" },
      { id: 2, dayOfWeek: "MONDAY", startTime: "12:00:00" },
      { id: 3, dayOfWeek: "MONDAY", startTime: "09:00:00" }
    ]).map((item) => item.id),
    [3, 2, 1]
  );

  assert.deepEqual(
    sortExceptions([
      { id: 1, date: "2099-02-01", startTime: "12:00:00" },
      { id: 2, date: "2099-01-31", startTime: "18:00:00" },
      { id: 3, date: "2099-01-31", startTime: "10:00:00" }
    ]).map((item) => item.id),
    [3, 2, 1]
  );
});
