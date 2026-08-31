<template>
  <div class="band-detail-page">
    <section class="band-detail-shell">
      <div class="container py-5">
        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("bands.detail.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("bands.detail.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("bands.actions.retry") }}
          </button>
        </div>

        <template v-else-if="band">
          <header class="band-detail-header">
            <div class="band-detail-header__copy">
              <span class="band-detail-header__eyebrow">{{ t("bands.detail.eyebrow") }}</span>
              <h1>{{ band.name }}</h1>
              <p>{{ band.description || t("bands.cards.noDescription") }}</p>
            </div>

            <div class="band-detail-header__actions">
              <button
                v-if="canPublishEvent"
                type="button"
                class="btn btn-success"
                @click="openEventModal"
              >
                <i class="bi bi-calendar-plus" aria-hidden="true"></i>
                {{ t("bands.actions.publishEvent") }}
              </button>
              <RouterLink
                v-if="canPublishEvent"
                class="btn btn-success"
                :to="{ name: 'BandRecruitmentList', query: { view: 'mine', mode: 'create', bandId: String(band.id) } }"
              >
                <i class="bi bi-person-plus" aria-hidden="true"></i>
                {{ t("bands.actions.publishRecruitment") }}
              </RouterLink>
              <button
                v-if="canLeaveBand"
                type="button"
                class="btn btn-outline-danger"
                @click="openLeaveConfirm"
              >
                <i class="bi bi-box-arrow-right" aria-hidden="true"></i>
                {{ t("bands.actions.leaveBand") }}
              </button>
            </div>
          </header>

          <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
            {{ pageNotice.message }}
          </div>

          <section class="band-detail-hero section-spacing">
            <div class="band-detail-hero__media">
              <AppImage
                :src="band.image"
                :alt="band.name"
                :fallback-src="bandPlaceholder"
                :fallback-label="t('bands.detail.imageFallback')"
                icon-class="bi bi-vinyl"
              />
            </div>

            <div class="band-detail-hero__body">
              <div class="band-detail-hero__badges">
                <span class="detail-pill detail-pill--accent">{{ band.mainGenre || t("bands.cards.noGenre") }}</span>
                <span class="detail-pill">{{ statusLabel }}</span>
                <span class="detail-pill">{{ band.baseCity || t("bands.cards.noCity") }}</span>
              </div>

              <div class="band-detail-hero__summary">
                <p class="band-detail-hero__lead">{{ heroLead }}</p>

                <div class="band-detail-hero__meta" role="list">
                  <span role="listitem">
                    <i class="bi bi-calendar3" aria-hidden="true"></i>
                    {{ t("bands.detail.createdAtInline", { date: createdAtLabel }) }}
                  </span>
                  <span role="listitem">
                    <i class="bi bi-people" aria-hidden="true"></i>
                    {{ memberSummary }}
                  </span>
                </div>

                <RouterLink
                  v-if="primaryRecruitment"
                  class="band-recruitment-highlight"
                  :to="{ name: 'BandRecruitmentList', query: { highlight: String(primaryRecruitment.id) } }"
                  :aria-label="`${t('bands.actions.viewRecruitment')}: ${primaryRecruitment.title}`"
                >
                  <span class="band-recruitment-highlight__content">
                    <span class="band-recruitment-highlight__label">{{ t("bands.detail.activeRecruitment") }}</span>
                    <strong>{{ primaryRecruitment.title }}</strong>
                    <span>{{ primaryRecruitmentMeta }}</span>
                  </span>
                  <span class="band-recruitment-highlight__cta">
                    {{ t("bands.actions.viewRecruitment") }}
                    <i class="bi bi-arrow-up-right" aria-hidden="true"></i>
                  </span>
                </RouterLink>
              </div>
            </div>
          </section>

          <div class="band-detail-layout section-spacing">
            <section class="detail-panel">
              <div class="detail-panel__header">
                <span class="detail-panel__eyebrow">{{ t("bands.members.eyebrow") }}</span>
                <h2>{{ t("bands.detail.membersTitle") }}</h2>
              </div>

              <div v-if="members.length" class="member-list">
                <article v-for="member in members" :key="member.id" class="member-card">
                  <div class="member-card__avatar">
                    <AppImage
                      :src="member.user?.profileImage"
                      :alt="memberName(member)"
                      :fallback-src="avatarPlaceholder"
                      icon-class="bi bi-person"
                    />
                  </div>
                  <div class="member-card__copy">
                    <strong>{{ memberName(member) }}</strong>
                    <span>{{ memberInstrumentLabel(member) }}</span>
                  </div>
                </article>
              </div>
              <p v-else class="detail-empty">{{ t("bands.members.empty") }}</p>
            </section>

            <section class="detail-panel">
              <div class="detail-panel__header">
                <span class="detail-panel__eyebrow">{{ t("bands.recruitments.eyebrow") }}</span>
                <h2>{{ t("bands.detail.recruitmentsTitle") }}</h2>
              </div>

              <div v-if="activeRecruitments.length" class="recruitment-list">
                <RouterLink
                  v-for="recruitment in activeRecruitments"
                  :key="recruitment.id"
                  class="recruitment-card clickable-card"
                  :to="{ name: 'BandRecruitmentList', query: { highlight: String(recruitment.id) } }"
                  :aria-label="`${t('bands.actions.viewRecruitment')}: ${recruitment.title}`"
                >
                  <div>
                    <strong>{{ recruitment.title }}</strong>
                    <p>{{ recruitment.description || t("recruitmentBoard.detail.emptyDescription") }}</p>
                  </div>
                  <div class="recruitment-card__meta">
                    <span>{{ recruitment.instrument?.name || recruitment.roleWanted || "--" }}</span>
                    <span>{{ t(`bands.levels.${recruitment.levelRequired || 'BEGINNER'}`) }}</span>
                    <span>{{ t("recruitmentBoard.card.vacancies", { count: recruitment.vacancies ?? 0 }) }}</span>
                  </div>
                  <span class="clickable-card__open-indicator" aria-hidden="true">
                    <i class="bi bi-arrow-up-right"></i>
                  </span>
                </RouterLink>
              </div>
              <p v-else class="detail-empty">{{ t("bands.recruitments.empty") }}</p>
            </section>
          </div>
        </template>
      </div>
    </section>

    <BandEventCreateModal
      :open="eventModalOpen"
      :model-value="eventForm"
      :bands="band ? [band] : []"
      :spaces="publicSpaces"
      :error-message="eventModalError"
      :submitting="eventSubmitting"
      @close="closeEventModal"
      @submit="submitEvent"
      @update:modelValue="eventForm = $event"
    />

    <div v-if="leaveConfirmOpen" class="band-confirm-backdrop" @click.self="closeLeaveConfirm">
      <section class="band-confirm" role="dialog" aria-modal="true">
        <header class="band-confirm__header">
          <div>
            <span class="band-confirm__eyebrow">{{ t("bands.leave.eyebrow") }}</span>
            <h2>{{ t("bands.leave.title") }}</h2>
          </div>
          <button
            type="button"
            class="band-confirm__close"
            :aria-label="t('common.actions.close')"
            :disabled="leavingBand"
            @click="closeLeaveConfirm"
          >
            <i class="bi bi-x-lg" aria-hidden="true"></i>
          </button>
        </header>

        <p class="band-confirm__copy">
          {{ t("bands.leave.confirm", { name: band?.name || "" }) }}
        </p>

        <p v-if="leaveError" class="band-confirm__error">{{ leaveError }}</p>

        <footer class="band-confirm__footer">
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="leavingBand"
            @click="closeLeaveConfirm"
          >
            {{ t("bands.actions.cancel") }}
          </button>
          <button
            type="button"
            class="btn btn-danger"
            :disabled="leavingBand"
            @click="submitLeaveBand"
          >
            <span v-if="leavingBand" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ t("bands.leave.confirmAction") }}
          </button>
        </footer>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import { buildBandEventPayload, createEmptyEventForm } from "@/modules/events/eventUtils";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import bandPlaceholder from "@/assets/placeholders/band-placeholder.svg";
import BandMemberRepository from "@/repositories/BandMemberRepository";
import BandRecruitmentRepository from "@/repositories/BandRecruitmentRepository";
import BandRepository from "@/repositories/BandRepository";
import EventRepository from "@/repositories/EventRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import BandEventCreateModal from "../components/BandEventCreateModal.vue";
import { formatMemberInstruments } from "../bandMemberUtils";

const route = useRoute();
const store = getStore();
const { locale, t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const band = ref(null);
const members = ref([]);
const activeRecruitments = ref([]);
const publicSpaces = ref([]);
const eventModalOpen = ref(false);
const eventModalError = ref("");
const eventSubmitting = ref(false);
const eventForm = ref(createEmptyEventForm());
const leaveConfirmOpen = ref(false);
const leavingBand = ref(false);
const leaveError = ref("");

const currentUserId = computed(() => Number(store.state.user.id || 0));
const myMembership = computed(
  () => members.value.find((member) => Number(member.user?.id) === currentUserId.value) || null
);
const isLeader = computed(() => myMembership.value?.roleInBand === "LEADER");
const activeLeaderCount = computed(
  () => members.value.filter((member) => member.roleInBand === "LEADER").length
);
const canPublishEvent = computed(() => Boolean(band.value?.active && isLeader.value));
const canLeaveBand = computed(() =>
  Boolean(
    band.value?.active &&
    myMembership.value &&
    (!isLeader.value || activeLeaderCount.value > 1)
  )
);
const statusLabel = computed(() => t(`bands.statuses.${resolveStatus()}`));
const primaryRecruitment = computed(() => activeRecruitments.value[0] || null);
const heroLead = computed(() =>
  t("bands.detail.heroLead", {
    members: t("bands.cards.membersValue", { count: members.value.length }),
    city: band.value?.baseCity || t("bands.cards.noCity")
  })
);
const memberSummary = computed(() => {
  const names = members.value.map(memberName).filter(Boolean);

  if (!names.length) {
    return t("bands.members.empty");
  }

  const visibleNames = names.slice(0, 5).join(", ");
  const remainingCount = names.length - 5;

  return remainingCount > 0 ? `${visibleNames} +${remainingCount}` : visibleNames;
});
const primaryRecruitmentMeta = computed(() => {
  const recruitment = primaryRecruitment.value;

  if (!recruitment) {
    return "";
  }

  return [
    recruitment.instrument?.name || recruitment.roleWanted,
    t(`bands.levels.${recruitment.levelRequired || "BEGINNER"}`),
    t("recruitmentBoard.card.vacancies", { count: recruitment.vacancies ?? 0 })
  ]
    .filter(Boolean)
    .join(" · ");
});
const createdAtLabel = computed(() => {
  if (!band.value?.creationDate) {
    return "--";
  }

  return new Intl.DateTimeFormat(locale.value, {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(new Date(band.value.creationDate));
});
onMounted(loadPage);

watch(
  () => route.params.id,
  () => {
    loadPage();
  }
);

async function loadPage() {
  loading.value = true;
  errorMessage.value = "";
  pageNotice.value = null;
  leaveConfirmOpen.value = false;
  leaveError.value = "";

  try {
    const bandId = route.params.id;
    const [bandResponse, membersResponse, recruitmentsResponse] = await Promise.all([
      BandRepository.getById(bandId),
      BandMemberRepository.getByBand(bandId),
      BandRecruitmentRepository.getAll()
    ]);

    band.value = bandResponse;
    members.value = membersResponse ?? [];
    activeRecruitments.value = (recruitmentsResponse ?? []).filter(
      (item) => Number(item.band?.id) === Number(bandId) && item.status === "OPEN"
    );
    await loadPublicSpaces();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "bands.detail.error");
  } finally {
    loading.value = false;
  }
}

async function loadPublicSpaces() {
  try {
    publicSpaces.value = await MusicalSpaceRepository.getAll();
  } catch {
    publicSpaces.value = [];
  }
}

function openLeaveConfirm() {
  if (!canLeaveBand.value) {
    return;
  }

  leaveError.value = "";
  leaveConfirmOpen.value = true;
}

function closeLeaveConfirm() {
  if (leavingBand.value) {
    return;
  }

  leaveConfirmOpen.value = false;
  leaveError.value = "";
}

async function submitLeaveBand() {
  if (!band.value || !canLeaveBand.value) {
    return;
  }

  leavingBand.value = true;
  leaveError.value = "";

  try {
    await BandMemberRepository.leaveBand(band.value.id);
    await refreshBandMembership();
    leaveConfirmOpen.value = false;
    pageNotice.value = { type: "success", message: t("bands.leave.success") };
  } catch (error) {
    leaveError.value = getApiErrorMessage(error, t, "bands.leave.error");
    await refreshBandMembership().catch(() => {});
  } finally {
    leavingBand.value = false;
  }
}

async function refreshBandMembership() {
  const bandId = band.value?.id || route.params.id;

  if (!bandId) {
    return;
  }

  const [bandResponse, membersResponse] = await Promise.all([
    BandRepository.getById(bandId),
    BandMemberRepository.getByBand(bandId)
  ]);

  band.value = bandResponse;
  members.value = membersResponse ?? [];
}

function resolveStatus() {
  if (!band.value?.active) {
    return "inactive";
  }

  if (activeRecruitments.value.length) {
    return "recruiting";
  }

  if (members.value.length < 4) {
    return "forming";
  }

  return "active";
}

function openEventModal() {
  if (!canPublishEvent.value || !band.value) {
    return;
  }

  eventForm.value = {
    ...createEmptyEventForm(),
    bandId: String(band.value.id),
    musicalGenre: band.value.mainGenre || "",
    city: band.value.baseCity || "",
    country: "Spain",
    status: "DRAFT",
    source: "INTERNAL"
  };
  eventModalError.value = "";
  eventModalOpen.value = true;
}

function closeEventModal() {
  eventModalOpen.value = false;
  eventModalError.value = "";
}

async function submitEvent() {
  const bandId = Number(eventForm.value.bandId);
  const payload = buildBandEventPayload(eventForm.value);

  if (
    !bandId ||
    !payload.title ||
    !payload.eventDate ||
    !payload.eventType ||
    !payload.venueName ||
    !payload.city ||
    !payload.country
  ) {
    eventModalError.value = t("bands.modals.event.validation");
    return;
  }

  if (payload.startTime && payload.endTime && payload.startTime >= payload.endTime) {
    eventModalError.value = t("bands.modals.event.timeValidation");
    return;
  }

  eventSubmitting.value = true;
  eventModalError.value = "";

  try {
    await EventRepository.createForBand(bandId, payload);
    pageNotice.value = { type: "success", message: t("bands.modals.event.created") };
    closeEventModal();
  } catch (error) {
    eventModalError.value = getApiErrorMessage(error, t, "bands.modals.event.error");
  } finally {
    eventSubmitting.value = false;
  }
}

function memberName(member) {
  return [member.user?.name, member.user?.firstSurname].filter(Boolean).join(" ") || t("messages.chat.fallbackAuthor");
}

function memberInstrumentLabel(member) {
  return formatMemberInstruments(member, t("bands.members.noInstruments"));
}

</script>

<style scoped>
.band-detail-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 22%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.band-detail-shell {
  padding-bottom: 3rem;
}

.band-detail-header {
  display: grid;
  grid-template-columns: 1fr minmax(0, 780px) 1fr;
  gap: 1rem;
  align-items: start;
}

.band-detail-header__copy {
  grid-column: 2;
  text-align: center;
}

.band-detail-header__eyebrow,
.detail-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.band-detail-header__copy h1 {
  margin: 0.4rem 0 0.55rem;
  font-size: clamp(2rem, 4vw, 3.1rem);
  letter-spacing: 0;
}

.band-detail-header__copy p {
  margin: 0;
  color: #b6b6b6;
  line-height: 1.75;
}

.band-detail-header__actions {
  grid-column: 3;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.band-detail-header__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
}

.band-confirm-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1060;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(10px);
}

.band-confirm {
  width: min(100%, 560px);
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.32);
}

.band-confirm__header,
.band-confirm__footer {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.band-confirm__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.band-confirm__header h2 {
  margin: 0.3rem 0 0;
  color: #ffffff;
  font-size: 1.35rem;
}

.band-confirm__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.band-confirm__copy {
  margin: 1rem 0 0;
  color: #d7d7d7;
  line-height: 1.6;
}

.band-confirm__error {
  margin: 1rem 0 0;
  padding: 0.85rem 0.95rem;
  border-radius: 16px;
  border: 1px solid rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.band-confirm__footer {
  margin-top: 1.2rem;
  justify-content: flex-end;
}

.band-confirm__footer .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
}

.section-spacing {
  margin-top: 1.2rem;
}

.band-detail-hero,
.detail-panel,
.state-card {
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.band-detail-hero {
  display: grid;
  grid-template-columns: minmax(280px, 360px) minmax(0, 1fr);
}

.band-detail-hero__media {
  min-height: 0;
  height: clamp(260px, 28vw, 360px);
  overflow: hidden;
}

.band-detail-hero__media :deep(.app-image),
.band-detail-hero__media :deep(.app-image__img),
.band-detail-hero__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.band-detail-hero__body,
.detail-panel {
  padding: 1.25rem;
}

.band-detail-hero__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1.1rem;
}

.band-detail-hero__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
}

.detail-pill {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 0.8rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: #e0e0e0;
  font-size: 0.82rem;
  font-weight: 700;
}

.detail-pill--accent {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.band-detail-hero__summary {
  display: grid;
  gap: 1rem;
  max-width: 760px;
}

.band-detail-hero__lead {
  margin: 0;
  color: #ffffff;
  font-size: clamp(1.2rem, 2vw, 1.75rem);
  font-weight: 800;
  line-height: 1.25;
}

.band-detail-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem 1.15rem;
  color: #c7c7c7;
  line-height: 1.5;
}

.band-detail-hero__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 0;
}

.band-detail-hero__meta i {
  flex: 0 0 auto;
  color: #1db954;
}

.band-recruitment-highlight {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 1rem;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
  text-decoration: none;
}

.band-recruitment-highlight:hover,
.band-recruitment-highlight:focus-visible {
  color: #ffffff;
}

.band-recruitment-highlight:focus-visible {
  outline: 2px solid rgba(29, 185, 84, 0.75);
  outline-offset: 4px;
}

.band-recruitment-highlight__content {
  display: grid;
  gap: 0.25rem;
  min-width: 0;
}

.band-recruitment-highlight__label {
  color: #1db954;
  font-size: 0.78rem;
  font-weight: 800;
  text-transform: uppercase;
}

.band-recruitment-highlight__content strong {
  overflow-wrap: anywhere;
  font-size: 1.05rem;
}

.band-recruitment-highlight__content > span:last-child {
  color: #b6b6b6;
}

.band-recruitment-highlight__cta {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  white-space: nowrap;
  color: #dfffe9;
  font-weight: 800;
}

.member-card,
.recruitment-card {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.band-detail-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.detail-panel__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.35rem;
}

.member-list,
.recruitment-list {
  display: grid;
  gap: 0.85rem;
  margin-top: 1rem;
}

.member-card {
  display: flex;
  align-items: center;
  gap: 0.85rem;
}

.member-card__avatar {
  display: block;
  overflow: hidden;
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-card__avatar :deep(.app-image),
.member-card__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.member-card__copy {
  display: grid;
  gap: 0.2rem;
}

.member-card__copy span,
.detail-empty,
.recruitment-card p,
.recruitment-card__meta {
  color: #b6b6b6;
}

.recruitment-card {
  display: grid;
  gap: 0.85rem;
}

.recruitment-card p {
  margin: 0.45rem 0 0;
  line-height: 1.7;
}

.recruitment-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  padding: 1rem 1.1rem;
  text-align: center;
}

.page-notice {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice.warning {
  color: #ffe2b7;
  border-color: rgba(255, 193, 7, 0.22);
  background: rgba(255, 193, 7, 0.08);
}

.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

@media (max-width: 1199.98px) {
  .band-detail-hero,
  .band-detail-layout {
    grid-template-columns: 1fr;
  }

  .band-detail-hero__media {
    height: clamp(220px, 36vw, 340px);
  }
}

@media (max-width: 991.98px) {
  .band-detail-header {
    grid-template-columns: 1fr;
  }

  .band-detail-header__copy,
  .band-detail-header__actions {
    grid-column: auto;
  }

  .band-detail-header__copy {
    text-align: left;
  }

  .band-detail-header__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 767.98px) {
  .band-recruitment-highlight {
    grid-template-columns: 1fr;
    align-items: start;
  }

  .band-recruitment-highlight__cta {
    justify-self: start;
  }

  .band-confirm__header,
  .band-confirm__footer {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
