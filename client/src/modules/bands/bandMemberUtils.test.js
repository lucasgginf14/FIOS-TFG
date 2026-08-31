import assert from "node:assert/strict";
import test from "node:test";

import { formatMemberInstruments } from "./bandMemberUtils.js";

test("formats band member instruments for empty single and multiple values", () => {
  assert.equal(formatMemberInstruments(null, "Sen instrumentos asociados"), "Sen instrumentos asociados");
  assert.equal(formatMemberInstruments({ instruments: [] }, "Sen instrumentos asociados"), "Sen instrumentos asociados");
  assert.equal(
    formatMemberInstruments({ instruments: [{ name: "Guitarra" }] }, "Sen instrumentos asociados"),
    "Guitarra"
  );
  assert.equal(
    formatMemberInstruments(
      { instruments: [{ name: "Guitarra" }, { name: "Baixo" }, { name: "Bater\u00eda" }] },
      "Sen instrumentos asociados"
    ),
    "Guitarra, Baixo, Bater\u00eda"
  );
});

test("ignores blank or malformed band member instruments", () => {
  assert.equal(
    formatMemberInstruments(
      { instruments: [{ name: "  Voz  " }, { name: "" }, null, { id: 9 }] },
      "Sen instrumentos asociados"
    ),
    "Voz"
  );
});
