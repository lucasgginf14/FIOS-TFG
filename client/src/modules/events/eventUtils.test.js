import assert from "node:assert/strict";
import test from "node:test";

import { formatEventMoney, getEventTicketValidationKey } from "./eventUtils.js";

test("formats free event prices with the translated free label", () => {
  assert.equal(formatEventMoney(0, "es", "--", "Gratis"), "Gratis");
  assert.equal(formatEventMoney("0.00", "en", "--", "Free"), "Free");
});

test("formats paid event prices as euro amounts", () => {
  const formatted = formatEventMoney("12.50", "es", "--", "Gratis");

  assert.match(formatted, /12,50/);
  assert.match(formatted, /€/);
});

test("validates internal event capacity and ticket price", () => {
  assert.equal(
    getEventTicketValidationKey({
      source: "INTERNAL",
      capacity: null,
      ticketPrice: 0
    }),
    "events.admin.capacityValidation"
  );
  assert.equal(
    getEventTicketValidationKey({
      source: "INTERNAL",
      capacity: 0,
      ticketPrice: 0
    }),
    "events.admin.capacityValidation"
  );
  assert.equal(
    getEventTicketValidationKey({
      source: "INTERNAL",
      capacity: 10,
      ticketPrice: -1
    }),
    "events.admin.priceValidation"
  );
  assert.equal(
    getEventTicketValidationKey({
      source: "INTERNAL",
      capacity: 10,
      ticketPrice: 12.5
    }),
    ""
  );
});
