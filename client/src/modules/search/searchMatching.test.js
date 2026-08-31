import assert from "node:assert/strict";
import test from "node:test";

import {
  searchTextMatchesBand,
  searchTextMatchesRecruitment,
  shouldIncludeBands,
  shouldIncludeRecruitments,
  tokenizeSearchText
} from "./searchMatching.js";

test("ignores common words when tokenizing natural search text", () => {
  assert.deepEqual(tokenizeSearchText("busco un local para ensayar"), ["local", "ensayar"]);
});

test("does not include band recruitments for a rehearsal space search", () => {
  assert.equal(
    shouldIncludeRecruitments("local para ensayar", {
      intent: "SPACE",
      spaceType: "REHEARSAL_ROOM"
    }),
    false
  );
});

test("does not include band recruitments when space-only filters are active", () => {
  assert.equal(shouldIncludeRecruitments("", { spaceType: "REHEARSAL_ROOM" }), false);
  assert.equal(shouldIncludeRecruitments("", { date: "2026-07-13" }), false);
  assert.equal(shouldIncludeRecruitments("", { dateFrom: "2026-07-13", dateTo: "2026-07-20" }), false);
  assert.equal(shouldIncludeRecruitments("", { autonomousCommunity: "Galicia" }), false);
  assert.equal(shouldIncludeRecruitments("", { maxBudget: 40 }), false);
});

test("keeps band recruitments for member-search wording", () => {
  assert.equal(shouldIncludeRecruitments("busco guitarrista para banda", {}), true);
});

test("keeps bands for band wording", () => {
  assert.equal(shouldIncludeBands("banda de funk en Vigo", {}), true);
  assert.equal(shouldIncludeBands("bandas en Santiago", {}), true);
});

test("does not include bands for a clear space search", () => {
  assert.equal(
    shouldIncludeBands("local para ensayar", {
      intent: "SPACE",
      spaceType: "REHEARSAL_ROOM"
    }),
    false
  );
  assert.equal(shouldIncludeBands("", { province: "A Coruna" }), false);
});

test("matches bands by genre city name or description without requiring generic band words", () => {
  const band = {
    name: "Metro Noroeste",
    description: "Proyecto con bajo bailable y teclados",
    mainGenre: "Funk",
    baseCity: "Vigo"
  };

  assert.equal(searchTextMatchesBand(band, "banda de funk en Vigo"), true);
  assert.equal(searchTextMatchesBand(band, "bandas"), true);
  assert.equal(searchTextMatchesBand(band, "jazz en Lugo"), false);
});

test("prefers a clear space search over a standalone instrument word", () => {
  assert.equal(shouldIncludeRecruitments("busco local con bateria para ensayar", {}), false);
});

test("does not match recruitments only because the query contains filler words", () => {
  const recruitment = {
    title: "Buscamos guitarrista para nuevo proyecto",
    roleWanted: "Guitarra",
    instrument: { name: "Guitarra" },
    band: { name: "Metro Noroeste", mainGenre: "Synth Pop", baseCity: "A Coruna" },
    city: "A Coruna"
  };

  assert.equal(searchTextMatchesRecruitment(recruitment, "local para ensayar"), false);
});
