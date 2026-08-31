export function formatMemberInstruments(member, fallback) {
  const names = Array.isArray(member?.instruments)
    ? member.instruments
        .map((instrument) => instrument?.name)
        .filter((name) => typeof name === "string" && name.trim())
        .map((name) => name.trim())
    : [];

  return names.length ? names.join(", ") : fallback;
}
