import { formatDateTime } from "@/modules/musical-spaces/spaceDetailUtils";

export function createBandFilters() {
  return {
    searchText: "",
    genre: "",
    state: "",
    order: "recent"
  };
}

export function buildBandViewModels({
  bands = [],
  membersByBand = {},
  recruitments = [],
  currentUserId,
  locale,
  t
}) {
  const recruitmentsByBand = groupRecruitmentsByBand(recruitments);

  return (bands ?? []).map((band) => {
    const members = membersByBand[band.id] ?? [];
    const activeRecruitments = recruitmentsByBand[band.id] ?? [];
    const myMembership = members.find((member) => Number(member.user?.id) === Number(currentUserId)) || null;
    const memberCount = members.length;
    const status = resolveBandStatus(band, memberCount, activeRecruitments.length);
    const lastRecruitmentDate = activeRecruitments[0]?.publicationDate || null;
    const activityTimestamp = Math.max(
      band.creationDate ? new Date(band.creationDate).getTime() : 0,
      lastRecruitmentDate ? new Date(lastRecruitmentDate).getTime() : 0
    );

    return {
      ...band,
      members,
      myMembership,
      memberCount,
      activeRecruitments,
      activeRecruitmentsCount: activeRecruitments.length,
      status,
      statusLabel: t(`bands.statuses.${status}`),
      genreLabel: band.mainGenre || t("bands.cards.noGenre"),
      cityLabel: band.baseCity || t("bands.cards.noCity"),
      createdAtLabel: band.creationDate ? formatDateTime(band.creationDate, locale) : "--",
      memberCountLabel: t("bands.cards.membersValue", { count: memberCount }),
      previewDescription: band.description || t("bands.cards.noDescription"),
      isLeader: myMembership?.roleInBand === "LEADER",
      heroMembers: members.slice(0, 4),
      activityTimestamp
    };
  });
}

export function chooseMainBand(bands = []) {
  return bands.find((band) => band.active && band.isLeader)
    || bands.find((band) => band.active)
    || bands[0]
    || null;
}

export function filterBands(bands = [], filters = {}, activeTab = "all") {
  const needle = normalizeText(filters.searchText);

  return bands.filter((band) => {
    const tabMatch = matchesBandTab(band, activeTab);
    const textMatch = !needle || normalizeText([
      band.name,
      band.baseCity,
      band.description,
      band.mainGenre
    ].join(" ")).includes(needle);
    const genreMatch = !filters.genre || band.mainGenre === filters.genre;
    const stateMatch = !filters.state || band.status === filters.state;

    return tabMatch && textMatch && genreMatch && stateMatch;
  });
}

export function sortBands(bands = [], order = "recent") {
  return [...bands].sort((left, right) => {
    if (order === "name") {
      return `${left.name}`.localeCompare(`${right.name}`);
    }

    if (order === "city") {
      return `${left.baseCity}`.localeCompare(`${right.baseCity}`);
    }

    if (order === "members") {
      return Number(right.memberCount || 0) - Number(left.memberCount || 0);
    }

    return Number(right.activityTimestamp || 0) - Number(left.activityTimestamp || 0);
  });
}

export function matchesBandTab(band, tab = "all") {
  if (tab === "active") {
    return band.active;
  }

  if (tab === "forming") {
    return band.status === "forming";
  }

  if (tab === "recruiting") {
    return band.status === "recruiting";
  }

  return true;
}

export function buildGenreOptions(bands = []) {
  return [...new Set(bands.map((band) => band.mainGenre).filter(Boolean))].sort((a, b) => a.localeCompare(b));
}

export function mapBandPayload(form) {
  return {
    name: form.name.trim(),
    description: form.description.trim(),
    mainGenre: form.mainGenre.trim(),
    baseCity: form.baseCity.trim(),
    image: form.image.trim()
  };
}

export function mapRecruitmentPayload(form) {
  return {
    title: form.title.trim(),
    description: form.description.trim(),
    roleWanted: form.roleWanted.trim(),
    levelRequired: form.levelRequired,
    city: form.city.trim(),
    vacancies: Number(form.vacancies),
    instrumentId: Number(form.instrumentId)
  };
}

function groupRecruitmentsByBand(recruitments = []) {
  return (recruitments ?? [])
    .filter((item) => item.status === "OPEN")
    .sort((left, right) => new Date(right.publicationDate || 0) - new Date(left.publicationDate || 0))
    .reduce((accumulator, item) => {
      const bandId = item.band?.id;

      if (!bandId) {
        return accumulator;
      }

      if (!accumulator[bandId]) {
        accumulator[bandId] = [];
      }

      accumulator[bandId].push(item);
      return accumulator;
    }, {});
}

function resolveBandStatus(band, memberCount, activeRecruitmentsCount) {
  if (!band.active) {
    return "inactive";
  }

  if (activeRecruitmentsCount > 0) {
    return "recruiting";
  }

  if (memberCount < 4) {
    return "forming";
  }

  return "active";
}

function normalizeText(value) {
  return (value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}
