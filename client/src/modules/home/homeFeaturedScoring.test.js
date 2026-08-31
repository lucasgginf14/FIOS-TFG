import assert from "node:assert/strict";
import test from "node:test";

import { selectFeaturedEvents, selectFeaturedSpaces } from "./homeFeaturedScoring.js";

test("adds a clear reason to featured spaces", () => {
  const [space] = selectFeaturedSpaces([
    {
      id: 1,
      name: "Local Centro",
      rating: 4.8,
      reviewsCount: 8,
      capacity: 12,
      soundproofed: true,
      mainImage: "space.jpg"
    }
  ]);

  assert.equal(space.featuredReason, "highRating");
});

test("uses practical space reasons when rating is not decisive", () => {
  const [space] = selectFeaturedSpaces([
    {
      id: 2,
      name: "Sala Ensayo",
      rating: 3.8,
      reviewsCount: 1,
      capacity: 18,
      soundproofed: true,
      mainImage: ""
    }
  ]);

  assert.equal(space.featuredReason, "soundproofed");
});

test("adds timing reasons to featured events", () => {
  const now = new Date("2026-07-13T10:00:00");
  const [event] = selectFeaturedEvents(
    [
      {
        id: 3,
        title: "Concierto cercano",
        status: "PUBLISHED",
        eventDate: "2026-07-16",
        startTime: "21:00",
        ticketPrice: 18,
        capacity: 200,
        posterImage: "event.jpg",
        city: "A Coruna"
      }
    ],
    3,
    now
  );

  assert.equal(event.featuredReason, "soon");
});
