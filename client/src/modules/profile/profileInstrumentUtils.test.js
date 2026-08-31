import assert from "node:assert/strict";
import test from "node:test";

import {
  buildPrimaryInstrumentIds,
  formatInstrumentNames,
  formatPrimaryInstrumentName,
  getPrimaryInstrumentFromAccount
} from "./profileInstrumentUtils.js";

test("formats profile instruments for empty missing single and multiple values", () => {
  assert.equal(formatInstrumentNames(null, "Sen instrumentos asociados"), "Sen instrumentos asociados");
  assert.equal(formatInstrumentNames([], "Sen instrumentos asociados"), "Sen instrumentos asociados");
  assert.equal(formatInstrumentNames([{ name: "Guitarra" }], "Sen instrumentos asociados"), "Guitarra");
  assert.equal(
    formatInstrumentNames([{ name: "Guitarra" }, { name: "Baixo" }, { name: "Bater\u00eda" }], "Sen instrumentos asociados"),
    "Guitarra, Baixo, Bater\u00eda"
  );
});

test("ignores blank or malformed profile instruments", () => {
  assert.equal(
    formatInstrumentNames([{ name: "  Guitarra  " }, { name: "" }, null, { id: 4 }], "Sen instrumentos asociados"),
    "Guitarra"
  );
});

test("formats the primary profile instrument from singular or legacy list values", () => {
  assert.deepEqual(
    getPrimaryInstrumentFromAccount({
      instrument: { id: 2, name: "Guitarra" },
      instruments: [{ id: 4, name: "Baixo" }]
    }),
    { id: 2, name: "Guitarra" }
  );
  assert.equal(
    formatPrimaryInstrumentName(
      { instruments: [{ id: 4 }, { id: 7, name: "  Saxof\u00f3n  " }] },
      "Sen instrumento"
    ),
    "Saxof\u00f3n"
  );
  assert.equal(formatPrimaryInstrumentName({ instruments: [] }, "Sen instrumento"), "Sen instrumento");
});

test("builds a single instrument id payload for profile updates", () => {
  assert.deepEqual(buildPrimaryInstrumentIds("2"), [2]);
  assert.deepEqual(buildPrimaryInstrumentIds(""), []);
  assert.deepEqual(buildPrimaryInstrumentIds("missing"), []);
});
