<template>
  <div class="bands-page">
    <section class="bands-shell">
      <div class="container py-5">
        <header class="bands-header">
          <div class="bands-header__copy">
            <h1>{{ headerTitle }}</h1>
            <p>{{ headerSubtitle }}</p>
          </div>

          <button
            v-if="canUseUserFeatures"
            type="button"
            class="btn btn-success bands-header__cta"
            @click="openCreateModal"
          >
            <i class="bi bi-plus-lg" aria-hidden="true"></i>
            {{ t("bands.header.create") }}
          </button>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("bands.states.loading") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error">
          <strong>{{ t("bands.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadPage">
            {{ t("bands.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <div v-if="canUseUserFeatures" class="bands-view-switch" role="group">
            <button
              type="button"
              :class="{ 'is-active': !isMineView }"
              @click="showExploreView"
            >
              <i class="bi bi-compass"></i>
              {{ t("bands.views.explore") }}
            </button>
            <button type="button" :class="{ 'is-active': isMineView }" @click="showMineView">
              <i class="bi bi-person-lines-fill"></i>
              {{ t("bands.views.mine") }}
            </button>
          </div>

          <template v-if="listBandModels.length">
            <section v-if="!isMineView" class="bands-public-intro section-spacing">
              <div>
                <span class="bands-public-eyebrow">{{ t("bands.public.eyebrow") }}</span>
                <h2>{{ t("bands.public.title") }}</h2>
                <p>{{ t("bands.public.text") }}</p>
              </div>
              <div class="bands-public-intro__actions">
                <button
                  v-if="!isLogged"
                  type="button"
                  class="btn btn-success"
                  @click="goToRegister"
                >
                  {{ t("bands.public.register") }}
                </button>
                <button
                  v-if="!isLogged"
                  type="button"
                  class="btn btn-outline-light"
                  @click="goToLogin"
                >
                  {{ t("bands.public.login") }}
                </button>
                <button
                  v-if="canUseUserFeatures"
                  type="button"
                  class="btn btn-success"
                  @click="openCreateModal"
                >
                  {{ t("bands.header.create") }}
                </button>
                <RouterLink class="btn btn-outline-light" :to="{ name: 'BandRecruitmentList' }">
                  {{ t("bands.public.recruitments") }}
                </RouterLink>
              </div>
            </section>

            <MainBandCard
              v-if="mainBand"
              class="section-spacing"
              :band="mainBand"
              :view-label="t('bands.actions.viewBand')"
              :members-label="t('bands.actions.manageMembers')"
              :can-manage-members="canManageMembers(mainBand)"
              :can-publish-recruitment="canManageMembers(mainBand)"
              :can-publish-event="canPublishEvent(mainBand)"
              :recruitment-label="t('bands.actions.publishRecruitment')"
              :event-label="t('bands.actions.publishEvent')"
              @view="handleViewBand"
              @members="handleManageMembers"
              @recruitment="openRecruitmentModalForBand"
              @event="openEventModalForBand"
            />

            <div v-if="isMineView" class="bands-mid-grid section-spacing">
              <BandMembersPreview
                :eyebrow="t('bands.members.eyebrow')"
                :title="t('bands.members.title')"
                :count-label="t('bands.members.count', { count: mainBand?.members.length || 0 })"
                :empty-label="t('bands.members.empty')"
                :members="mainBand?.members.slice(0, 4) || []"
              />

              <ActiveRecruitmentsPreview
                :eyebrow="t('bands.recruitments.eyebrow')"
                :title="t('bands.recruitments.title')"
                :count-label="t('bands.recruitments.count', { count: previewRecruitments.length })"
                :empty-label="t('bands.recruitments.empty')"
                :view-label="t('bands.actions.viewRecruitment')"
                :manage-label="t('bands.actions.manageRecruitment')"
                :close-label="t('bands.actions.closeRecruitment')"
                :closing-label="t('bands.states.closingRecruitment')"
                :recruitments="previewRecruitments.slice(0, 4)"
                :closing-id="closingRecruitmentId"
                @view="handleViewRecruitment"
                @manage="handleManageRecruitment"
                @close="handleCloseRecruitment"
              />
            </div>

            <div v-else class="bands-mid-grid section-spacing">
              <section class="bands-public-card">
                <span class="bands-public-eyebrow">{{ t("bands.public.howEyebrow") }}</span>
                <h3>{{ t("bands.public.howTitle") }}</h3>
                <p>{{ t("bands.public.howText") }}</p>
                <div class="bands-public-card__steps">
                  <span><i class="bi bi-search"></i>{{ t("bands.public.stepExplore") }}</span>
                  <span><i class="bi bi-vinyl"></i>{{ t("bands.public.stepOpen") }}</span>
                  <span><i class="bi bi-megaphone"></i>{{ t("bands.public.stepRecruit") }}</span>
                </div>
              </section>

              <ActiveRecruitmentsPreview
                :eyebrow="t('bands.recruitments.publicEyebrow')"
                :title="t('bands.recruitments.publicTitle')"
                :count-label="t('bands.recruitments.count', { count: previewRecruitments.length })"
                :empty-label="t('bands.recruitments.publicEmpty')"
                :view-label="t('bands.actions.viewRecruitment')"
                :manage-label="t('bands.actions.manageRecruitment')"
                :close-label="t('bands.actions.closeRecruitment')"
                :closing-label="t('bands.states.closingRecruitment')"
                :recruitments="previewRecruitments.slice(0, 4)"
                :closing-id="closingRecruitmentId"
                @view="handleViewRecruitment"
                @manage="handleManageRecruitment"
                @close="handleCloseRecruitment"
              />
            </div>

            <section class="bands-panel section-spacing">
              <div class="bands-panel__header">
                <div>
                  <span class="bands-panel__eyebrow">{{ listEyebrow }}</span>
                  <h2>{{ listTitle }}</h2>
                </div>
                <span class="bands-panel__count">{{
                  t("bands.list.total", { count: listBandModels.length })
                }}</span>
              </div>

              <BandFiltersBar
                v-model="filters"
                :genre-options="genreOptions"
                :search-placeholder="t('bands.filters.searchPlaceholder')"
                :genre-label="t('bands.filters.genre')"
                :all-genres-label="t('bands.filters.allGenres')"
                :status-label="t('bands.filters.status')"
                :all-states-label="t('bands.filters.allStates')"
                :status-active-label="t('bands.statuses.active')"
                :status-inactive-label="t('bands.statuses.inactive')"
                :status-forming-label="t('bands.statuses.forming')"
                :status-recruiting-label="t('bands.statuses.recruiting')"
                :order-label="t('bands.filters.order')"
                :order-recent-label="t('bands.filters.orderOptions.recent')"
                :order-name-label="t('bands.filters.orderOptions.name')"
                :order-city-label="t('bands.filters.orderOptions.city')"
                :order-members-label="t('bands.filters.orderOptions.members')"
                :clear-label="t('bands.filters.clear')"
              />

              <BandStatusTabs
                class="bands-panel__tabs"
                :tabs="tabs"
                :active-tab="activeTab"
                @change="activeTab = $event"
              />

              <div v-if="visibleBands.length" class="bands-stack">
                <BandListCard
                  v-for="band in visibleBands"
                  :key="band.id"
                  :band="band"
                  :view-label="t('bands.actions.viewDetail')"
                  :members-label="t('bands.actions.manageMembers')"
                  :can-manage-members="canManageMembers(band)"
                  :can-edit="canManageMembers(band)"
                  :can-publish-recruitment="canManageMembers(band)"
                  :can-publish-event="canPublishEvent(band)"
                  :edit-label="t('bands.actions.edit')"
                  :recruitment-label="t('bands.actions.searchMembers')"
                  :event-label="t('bands.actions.publishEvent')"
                  @view="handleViewBand"
                  @members="handleManageMembers"
                  @edit="openEditModal"
                  @recruitment="openRecruitmentModalForBand"
                  @event="openEventModalForBand"
                />
              </div>

              <div v-else class="state-card state-card--soft">
                <strong>{{ t("bands.empty.filteredTitle") }}</strong>
                <p>{{ t("bands.empty.filteredText") }}</p>
              </div>
            </section>
          </template>

          <BandEmptyState
            v-else
            class="section-spacing"
            :title="emptyTitle"
            :text="emptyText"
            :create-label="emptyCreateLabel"
            :explore-label="emptyExploreLabel"
            @create="openCreateModal"
            @explore="exploreBands"
          />
        </template>
      </div>
    </section>

    <BandCreateModal
      :open="bandModalOpen"
      :model-value="bandForm"
      :eyebrow="bandModalEyebrow"
      :title="bandModalTitle"
      :name-label="t('bands.modals.band.name')"
      :genre-label="t('bands.modals.band.genre')"
      :city-label="t('bands.modals.band.city')"
      :image-label="t('bands.modals.band.image')"
      :description-label="t('bands.modals.band.description')"
      :submit-label="bandModalSubmitLabel"
      :submitting-label="t('bands.states.savingBand')"
      :cancel-label="t('bands.actions.cancel')"
      :error-message="bandModalError"
      :submitting="bandSubmitting"
      @close="closeBandModal"
      @submit="submitBand"
      @update:modelValue="bandForm = $event"
    />

    <BandRecruitmentCreateModal
      :open="recruitmentModalOpen"
      :model-value="recruitmentForm"
      :bands="leaderBands"
      :instruments="instruments"
      :eyebrow="t('bands.modals.recruitment.eyebrow')"
      :title="t('bands.modals.recruitment.title')"
      :band-label="t('bands.modals.recruitment.band')"
      :select-band-label="t('bands.modals.recruitment.selectBand')"
      :instrument-label="t('bands.modals.recruitment.instrument')"
      :select-instrument-label="t('bands.modals.recruitment.selectInstrument')"
      :title-label="t('bands.modals.recruitment.positionTitle')"
      :role-label="t('bands.modals.recruitment.role')"
      :level-label="t('bands.modals.recruitment.level')"
      :city-label="t('bands.modals.recruitment.city')"
      :vacancies-label="t('bands.modals.recruitment.vacancies')"
      :description-label="t('bands.modals.recruitment.description')"
      :submit-label="t('bands.modals.recruitment.submit')"
      :submitting-label="t('bands.states.publishingRecruitment')"
      :cancel-label="t('bands.actions.cancel')"
      :error-message="recruitmentModalError"
      :submitting="recruitmentSubmitting"
      @close="closeRecruitmentModal"
      @submit="submitRecruitment"
      @update:modelValue="recruitmentForm = $event"
    />

    <BandEventCreateModal
      :open="eventModalOpen"
      :model-value="eventForm"
      :bands="eventPublishBands"
      :spaces="publicSpaces"
      :error-message="eventModalError"
      :submitting="eventSubmitting"
      @close="closeEventModal"
      @submit="submitEvent"
      @update:modelValue="eventForm = $event"
    />

    <div v-if="memberModalOpen" class="member-modal-backdrop" @click.self="closeMemberModal">
      <section class="member-modal" role="dialog" aria-modal="true">
        <header class="member-modal__header">
          <div>
            <span class="member-modal__eyebrow">{{ t("bands.memberManagement.eyebrow") }}</span>
            <h2>{{ memberModalBand?.name }}</h2>
          </div>
          <button
            type="button"
            class="member-modal__close"
            :aria-label="t('common.actions.close')"
            @click="closeMemberModal"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <div v-if="memberModalNotice" class="member-modal__notice" :class="memberModalNotice.type">
          {{ memberModalNotice.message }}
        </div>

        <div v-if="memberLoading" class="member-modal__loading">
          <div class="spinner-border text-success" role="status"></div>
          <span>{{ t("bands.memberManagement.loading") }}</span>
        </div>

        <template v-else>
          <form class="member-form" @submit.prevent="submitMember">
            <div class="member-form__field member-form__field--search">
              <label for="member-candidate-search">
                {{ t("bands.memberManagement.userSearch") }}
              </label>
              <div class="member-search-input">
                <i class="bi bi-search" aria-hidden="true"></i>
                <input
                  id="member-candidate-search"
                  v-model="memberSearchQuery"
                  type="search"
                  :placeholder="t('bands.memberManagement.userSearchPlaceholder')"
                  autocomplete="off"
                  role="combobox"
                  aria-controls="member-candidate-results"
                  :aria-expanded="memberSearchResults.length > 0"
                  :aria-busy="memberSearchLoading"
                />
              </div>
              <small v-if="selectedMemberCandidate" class="member-search-status">
                {{
                  t("bands.memberManagement.selectedUser", {
                    name: memberCandidateName(selectedMemberCandidate)
                  })
                }}
              </small>
              <small v-else class="member-search-status">
                {{
                  memberSearchQuery.trim().length > 0 && memberSearchQuery.trim().length < 2
                    ? t("bands.memberManagement.userSearchMinLength")
                    : t("bands.memberManagement.userSearchHint")
                }}
              </small>

              <div
                v-if="memberSearchLoading || memberSearchError || memberSearchResults.length || memberSearchIsEmpty"
                id="member-candidate-results"
                class="member-search-results"
                role="listbox"
              >
                <div v-if="memberSearchLoading" class="member-search-results__state">
                  {{ t("bands.memberManagement.searchingUsers") }}
                </div>
                <div v-else-if="memberSearchError" class="member-search-results__state is-error">
                  {{ memberSearchError }}
                </div>
                <template v-else-if="memberSearchResults.length">
                  <button
                    v-for="candidate in memberSearchResults"
                    :key="candidate.id"
                    type="button"
                    class="member-search-result"
                    role="option"
                    :aria-selected="Number(memberForm.userId) === Number(candidate.id)"
                    @click="selectMemberCandidate(candidate)"
                  >
                    <span class="member-search-result__avatar">
                      <AppImage
                        :src="candidate.profileImage"
                        :alt="memberCandidateName(candidate)"
                        :fallback-src="avatarPlaceholder"
                        icon-class="bi bi-person"
                      />
                    </span>
                    <span class="member-search-result__copy">
                      <strong>{{ memberCandidateName(candidate) }}</strong>
                      <span>{{ candidate.email }}</span>
                    </span>
                  </button>
                </template>
                <div v-else-if="memberSearchIsEmpty" class="member-search-results__state">
                  {{ t("bands.memberManagement.userSearchEmpty") }}
                </div>
              </div>
            </div>

            <div class="member-form__field">
              <label>{{ t("bands.detail.fields.role") }}</label>
              <AppSelect
                :model-value="memberForm.roleInBand"
                :options="memberRoleSelectOptions"
                :label="t('bands.detail.fields.role')"
                @update:model-value="memberForm.roleInBand = $event"
              />
            </div>

            <button type="submit" class="btn btn-success" :disabled="memberSaving || !memberForm.userId">
              {{ memberSaving ? t("bands.memberManagement.adding") : t("bands.memberManagement.add") }}
            </button>
          </form>

          <div v-if="memberModalMembers.length" class="member-list">
            <article v-for="member in memberModalMembers" :key="member.id" class="member-row">
              <div class="member-row__avatar">
                <AppImage
                  :src="member.user?.profileImage"
                  :alt="memberName(member)"
                  :fallback-src="avatarPlaceholder"
                  icon-class="bi bi-person"
                />
              </div>

              <div class="member-row__copy">
                <strong>{{ memberName(member) }}</strong>
                <span>{{ formatMemberJoinDate(member.joinDate) }}</span>
              </div>

              <AppSelect
                class="member-row__role"
                :model-value="member.roleInBand"
                :options="memberRoleSelectOptions"
                :label="t('bands.detail.fields.role')"
                :disabled="memberBusyId === member.id"
                @update:model-value="handleMemberRoleChange(member, $event)"
              />

              <button
                v-if="!isCurrentUserMember(member)"
                type="button"
                class="btn btn-outline-danger btn-sm"
                :disabled="memberBusyId === member.id"
                @click="deactivateMember(member)"
              >
                {{
                  memberBusyId === member.id
                    ? t("bands.memberManagement.removing")
                    : t("bands.memberManagement.remove")
                }}
              </button>
            </article>
          </div>

          <div v-else class="member-modal__empty">{{ t("bands.memberManagement.empty") }}</div>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import AppImage from "@/common/components/AppImage.vue";
import { getStore } from "@/common/store";
import AppSelect from "@/common/components/AppSelect.vue";
import {
  buildBandEventPayload,
  createEmptyEventForm,
  getEventTicketValidationKey
} from "@/modules/events/eventUtils";
import { formatDateTime } from "@/modules/musical-spaces/spaceDetailUtils";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import BandMemberRepository from "@/repositories/BandMemberRepository";
import BandRecruitmentRepository from "@/repositories/BandRecruitmentRepository";
import BandRepository from "@/repositories/BandRepository";
import EventRepository from "@/repositories/EventRepository";
import InstrumentRepository from "@/repositories/InstrumentRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import ActiveRecruitmentsPreview from "../components/ActiveRecruitmentsPreview.vue";
import BandCreateModal from "../components/BandCreateModal.vue";
import BandEmptyState from "../components/BandEmptyState.vue";
import BandEventCreateModal from "../components/BandEventCreateModal.vue";
import BandFiltersBar from "../components/BandFiltersBar.vue";
import BandListCard from "../components/BandListCard.vue";
import BandMembersPreview from "../components/BandMembersPreview.vue";
import BandRecruitmentCreateModal from "../components/BandRecruitmentCreateModal.vue";
import BandStatusTabs from "../components/BandStatusTabs.vue";
import MainBandCard from "../components/MainBandCard.vue";
import {
  buildBandViewModels,
  buildGenreOptions,
  chooseMainBand,
  createBandFilters,
  filterBands,
  mapBandPayload,
  mapRecruitmentPayload,
  matchesBandTab,
  sortBands
} from "../bandUtils";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { locale, t } = useI18n();

const loading = ref(true);
const fatalError = ref("");
const pageNotice = ref(null);

const bands = ref([]);
const membersByBand = ref({});
const recruitments = ref([]);
const instruments = ref([]);
const publicSpaces = ref([]);

const filters = ref(createBandFilters());
const activeTab = ref("all");

const bandModalOpen = ref(false);
const bandModalError = ref("");
const bandSubmitting = ref(false);
const editingBandId = ref(null);
const bandForm = ref(createEmptyBandForm());

const recruitmentModalOpen = ref(false);
const recruitmentModalError = ref("");
const recruitmentSubmitting = ref(false);
const recruitmentForm = ref(createEmptyRecruitmentForm());
const closingRecruitmentId = ref(null);
const eventModalOpen = ref(false);
const eventModalError = ref("");
const eventSubmitting = ref(false);
const eventForm = ref(createEmptyEventForm());
const memberModalOpen = ref(false);
const memberModalBand = ref(null);
const memberLoading = ref(false);
const memberSaving = ref(false);
const memberBusyId = ref(null);
const memberModalNotice = ref(null);
const memberForm = ref(createEmptyMemberForm());
const memberSearchQuery = ref("");
const memberSearchResults = ref([]);
const memberSearchLoading = ref(false);
const memberSearchError = ref("");
const selectedMemberCandidate = ref(null);
let memberSearchRequestId = 0;
let suppressMemberSearch = false;

const currentUserId = computed(() => store.state.user.id);
const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);
const isMineView = computed(() => canUseUserFeatures.value && route.query.view === "mine");
const memberRoleOptions = ["LEADER", "MEMBER"];

const memberRoleSelectOptions = computed(() =>
  memberRoleOptions.map((role) => ({
    value: role,
    label: t(`bands.roles.${role}`)
  }))
);

const bandModels = computed(() =>
  buildBandViewModels({
    bands: bands.value,
    membersByBand: membersByBand.value,
    recruitments: recruitments.value,
    currentUserId: currentUserId.value,
    locale: locale.value,
    t
  })
);

const myBandIds = computed(
  () => new Set(bandModels.value.filter((band) => band.myMembership).map((band) => Number(band.id)))
);
const listBandModels = computed(() =>
  isMineView.value
    ? bandModels.value.filter((band) => myBandIds.value.has(Number(band.id)))
    : bandModels.value
);
const mainBand = computed(() => chooseMainBand(listBandModels.value));
const genreOptions = computed(() => buildGenreOptions(listBandModels.value));

const activeRecruitments = computed(() =>
  recruitments.value
    .filter((item) => item.status === "OPEN")
    .map((item) => ({
      ...item,
      canManage: canManageBandId(item.band?.id),
      band: item.band,
      instrument: item.instrument
    }))
);

const previewRecruitments = computed(() =>
  isMineView.value
    ? activeRecruitments.value.filter((item) => myBandIds.value.has(Number(item.band?.id)))
    : activeRecruitments.value
);
const leaderBands = computed(() => bandModels.value.filter((band) => band.isLeader));
const eventPublishBands = computed(() =>
  leaderBands.value.filter((band) => band.active)
);

const memberModalMembers = computed(() =>
  memberModalBand.value ? (membersByBand.value[memberModalBand.value.id] ?? []) : []
);

const memberSearchIsEmpty = computed(
  () =>
    memberSearchQuery.value.trim().length >= 2 &&
    !selectedMemberCandidate.value &&
    !memberSearchLoading.value &&
    !memberSearchError.value &&
    memberSearchResults.value.length === 0
);

const visibleBands = computed(() => {
  const filtered = filterBands(listBandModels.value, filters.value, activeTab.value);
  return sortBands(filtered, filters.value.order);
});

const tabs = computed(() => [
  { id: "all", label: t("bands.tabs.all"), count: listBandModels.value.length },
  {
    id: "active",
    label: t("bands.tabs.active"),
    count: listBandModels.value.filter((band) => matchesBandTab(band, "active")).length
  },
  {
    id: "forming",
    label: t("bands.tabs.forming"),
    count: listBandModels.value.filter((band) => matchesBandTab(band, "forming")).length
  },
  {
    id: "recruiting",
    label: t("bands.tabs.recruiting"),
    count: listBandModels.value.filter((band) => matchesBandTab(band, "recruiting")).length
  }
]);

const headerTitle = computed(() =>
  isMineView.value ? t("bands.header.mineTitle") : t("bands.header.title")
);
const headerSubtitle = computed(() =>
  isMineView.value ? t("bands.header.mineSubtitle") : t("bands.header.subtitle")
);
const listEyebrow = computed(() =>
  isMineView.value ? t("bands.list.mineEyebrow") : t("bands.list.publicEyebrow")
);
const listTitle = computed(() =>
  isMineView.value ? t("bands.list.mineTitle") : t("bands.list.publicTitle")
);
const emptyTitle = computed(() =>
  isMineView.value ? t("bands.empty.title") : t("bands.empty.publicTitle")
);
const emptyText = computed(() =>
  isMineView.value ? t("bands.empty.text") : t("bands.empty.publicText")
);
const emptyCreateLabel = computed(() => {
  if (isMineView.value && canUseUserFeatures.value) {
    return t("bands.empty.create");
  }

  if (!isLogged.value) {
    return t("bands.public.login");
  }

  if (!canUseUserFeatures.value) {
    return "";
  }

  return t("bands.empty.create");
});
const emptyExploreLabel = computed(() =>
  isMineView.value ? t("bands.empty.explore") : t("bands.public.recruitments")
);

const bandModalTitle = computed(() =>
  editingBandId.value ? t("bands.modals.band.editTitle") : t("bands.modals.band.createTitle")
);

const bandModalEyebrow = computed(() =>
  editingBandId.value ? t("bands.modals.band.editEyebrow") : t("bands.modals.band.createEyebrow")
);

const bandModalSubmitLabel = computed(() =>
  editingBandId.value ? t("bands.modals.band.update") : t("bands.modals.band.submit")
);

onMounted(() => {
  loadPage();
});

watch(
  () => route.query.mode,
  async (mode) => {
    if (mode !== "create") {
      return;
    }

    if (!isLogged.value) {
      goToLogin();
      return;
    }

    if (!canUseUserFeatures.value) {
      await clearCreateQuery();
      return;
    }

    openCreateModal();
  },
  { immediate: true }
);

watch(memberSearchQuery, (query, _previousQuery, onCleanup) => {
  memberSearchError.value = "";

  if (suppressMemberSearch) {
    suppressMemberSearch = false;
    return;
  }

  if (selectedMemberCandidate.value && query !== formatMemberCandidate(selectedMemberCandidate.value)) {
    selectedMemberCandidate.value = null;
    memberForm.value.userId = "";
  }

  const requestId = ++memberSearchRequestId;
  const normalizedQuery = query.trim();
  if (!memberModalOpen.value || !memberModalBand.value || normalizedQuery.length < 2) {
    memberSearchResults.value = [];
    memberSearchLoading.value = false;
    return;
  }

  memberSearchLoading.value = true;
  const timeoutId = window.setTimeout(async () => {
    try {
      const results = await BandMemberRepository.searchCandidates(
        memberModalBand.value.id,
        normalizedQuery
      );

      if (requestId === memberSearchRequestId) {
        memberSearchResults.value = results ?? [];
      }
    } catch (error) {
      if (requestId === memberSearchRequestId) {
        memberSearchResults.value = [];
        memberSearchError.value = getApiErrorMessage(error, t, "bands.memberManagement.userSearchError");
      }
    } finally {
      if (requestId === memberSearchRequestId) {
        memberSearchLoading.value = false;
      }
    }
  }, 250);

  onCleanup(() => window.clearTimeout(timeoutId));
});

async function loadPage() {
  loading.value = true;
  fatalError.value = "";
  pageNotice.value = null;

  try {
    const [bandsResult, recruitmentsResult, instrumentsResult, spacesResult] = await Promise.allSettled([
      BandRepository.getAll(),
      BandRecruitmentRepository.getAll(),
      InstrumentRepository.getAll(),
      MusicalSpaceRepository.getAll()
    ]);

    if (bandsResult.status !== "fulfilled") {
      throw bandsResult.reason;
    }

    bands.value = bandsResult.value ?? [];
    recruitments.value =
      recruitmentsResult.status === "fulfilled" ? (recruitmentsResult.value ?? []) : [];
    instruments.value =
      instrumentsResult.status === "fulfilled" ? (instrumentsResult.value ?? []) : [];
    publicSpaces.value = spacesResult.status === "fulfilled" ? (spacesResult.value ?? []) : [];

    if (
      recruitmentsResult.status === "rejected" ||
      instrumentsResult.status === "rejected" ||
      spacesResult.status === "rejected"
    ) {
      pageNotice.value = { type: "warning", message: t("bands.states.partialData") };
    }

    await loadMembersForBands();
  } catch (error) {
    fatalError.value = getApiErrorMessage(error, t, "bands.states.error");
  } finally {
    loading.value = false;
  }
}

async function loadMembersForBands() {
  const entries = await Promise.allSettled(
    bands.value.map(async (band) => [band.id, await BandMemberRepository.getByBand(band.id)])
  );

  const nextMap = {};
  let hasPartialErrors = false;

  entries.forEach((result) => {
    if (result.status === "fulfilled") {
      const [bandId, members] = result.value;
      nextMap[bandId] = members ?? [];
    } else {
      hasPartialErrors = true;
    }
  });

  membersByBand.value = nextMap;

  if (hasPartialErrors) {
    pageNotice.value = { type: "warning", message: t("bands.states.partialMembers") };
  }
}

function showExploreView() {
  router.push({ name: "BandList" });
}

function showMineView() {
  if (!canUseUserFeatures.value) {
    if (!isLogged.value) {
      goToLogin();
    }
    return;
  }

  router.push({ name: "BandList", query: { view: "mine" } });
}

function goToLogin() {
  router.push({ name: "Login", query: { redirect: route.fullPath } });
}

function goToRegister() {
  router.push({ name: "Register" });
}

function openCreateModal() {
  if (!canUseUserFeatures.value) {
    if (!isLogged.value) {
      router.push({ name: "Login", query: { redirect: route.fullPath } });
    }
    return;
  }

  editingBandId.value = null;
  bandForm.value = createEmptyBandForm();
  bandModalError.value = "";
  bandModalOpen.value = true;
}

function openEditModal(band) {
  if (!canManageMembers(band)) {
    pageNotice.value = {
      type: "warning",
      message: t("bands.memberManagement.editPermission")
    };
    return;
  }

  editingBandId.value = band.id;
  bandForm.value = {
    name: band.name || "",
    description: band.description || "",
    mainGenre: band.mainGenre || "",
    baseCity: band.baseCity || "",
    image: band.image || ""
  };
  bandModalError.value = "";
  bandModalOpen.value = true;
}

async function closeBandModal() {
  bandModalOpen.value = false;
  bandModalError.value = "";

  if (route.query.mode === "create") {
    await clearCreateQuery();
  }
}

async function submitBand() {
  if (!canUseUserFeatures.value) {
    return;
  }

  const payload = mapBandPayload(bandForm.value);

  if (!payload.name || !payload.mainGenre || !payload.baseCity) {
    bandModalError.value = t("bands.modals.band.validation");
    return;
  }

  bandSubmitting.value = true;
  bandModalError.value = "";

  try {
    if (editingBandId.value) {
      await BandRepository.update(editingBandId.value, payload);
      pageNotice.value = { type: "success", message: t("bands.modals.band.updated") };
    } else {
      await BandRepository.create(payload);
      pageNotice.value = { type: "success", message: t("bands.modals.band.created") };
    }

    await closeBandModal();
    await loadPage();
  } catch (error) {
    bandModalError.value = getApiErrorMessage(error, t, "bands.modals.band.error");
  } finally {
    bandSubmitting.value = false;
  }
}

function openRecruitmentModalForBand(band) {
  if (!canManageMembers(band)) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderBand") };
    return;
  }

  if (!leaderBands.value.length) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderBand") };
    return;
  }

  router.push({
    name: "BandRecruitmentList",
    query: {
      view: "mine",
      mode: "create",
      ...(band?.id ? { bandId: String(band.id) } : {})
    }
  });
}

function closeRecruitmentModal() {
  recruitmentModalOpen.value = false;
  recruitmentModalError.value = "";
}

async function submitRecruitment() {
  if (!canUseUserFeatures.value) {
    return;
  }

  const bandId = Number(recruitmentForm.value.bandId);
  const payload = mapRecruitmentPayload(recruitmentForm.value);

  if (
    !bandId ||
    !payload.title ||
    !payload.roleWanted ||
    !payload.city ||
    !payload.instrumentId ||
    !payload.vacancies
  ) {
    recruitmentModalError.value = t("bands.modals.recruitment.validation");
    return;
  }

  recruitmentSubmitting.value = true;
  recruitmentModalError.value = "";

  try {
    await BandRecruitmentRepository.createForBand(bandId, payload);
    pageNotice.value = { type: "success", message: t("bands.modals.recruitment.created") };
    closeRecruitmentModal();
    await loadRecruitments();
  } catch (error) {
    recruitmentModalError.value = getApiErrorMessage(
      error,
      t,
      "bands.modals.recruitment.error"
    );
  } finally {
    recruitmentSubmitting.value = false;
  }
}

function openEventModalForBand(band) {
  if (!canPublishEvent(band)) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderEventBand") };
    return;
  }

  if (!eventPublishBands.value.length) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderEventBand") };
    return;
  }

  eventForm.value = createEmptyBandEventForm(band);
  eventModalError.value = "";
  eventModalOpen.value = true;
}

function closeEventModal() {
  eventModalOpen.value = false;
  eventModalError.value = "";
}

async function submitEvent() {
  if (!canUseUserFeatures.value) {
    return;
  }

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

  const ticketValidationKey = getEventTicketValidationKey(payload);

  if (ticketValidationKey) {
    eventModalError.value = t(ticketValidationKey);
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

async function clearCreateQuery() {
  const nextQuery = { ...route.query };
  delete nextQuery.mode;

  await router.replace({
    name: "BandList",
    query: nextQuery
  });
}

async function loadRecruitments() {
  try {
    recruitments.value = await BandRecruitmentRepository.getAll();
  } catch {
    recruitments.value = [];
  }
}

async function handleCloseRecruitment(item) {
  if (!item.canManage) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderBand") };
    return;
  }

  closingRecruitmentId.value = item.id;

  try {
    await BandRecruitmentRepository.close(item.id);
    pageNotice.value = { type: "success", message: t("bands.recruitments.closed") };
    await loadRecruitments();
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "bands.recruitments.closeError")
    };
  } finally {
    closingRecruitmentId.value = null;
  }
}

function handleViewBand(band) {
  router.push({ name: "BandDetail", params: { id: band.id } });
}

function canManageMembers(band) {
  return Boolean(band?.isLeader);
}

function canPublishEvent(band) {
  return Boolean(band?.active && canManageMembers(band));
}

function canManageBandId(bandId) {
  return Boolean(
    bandModels.value.some((band) => Number(band.id) === Number(bandId) && band.isLeader)
  );
}

async function handleManageMembers(band) {
  if (!canManageMembers(band)) {
    pageNotice.value = {
      type: "warning",
      message: t("bands.memberManagement.managePermission")
    };
    return;
  }

  memberModalBand.value = band;
  memberModalOpen.value = true;
  memberModalNotice.value = null;
  memberForm.value = createEmptyMemberForm();
  resetMemberSearch();
  await refreshMembersForBand(band.id);
}

function closeMemberModal() {
  memberModalOpen.value = false;
  memberModalBand.value = null;
  memberModalNotice.value = null;
  memberForm.value = createEmptyMemberForm();
  resetMemberSearch();
}

function resetMemberSearch() {
  memberSearchRequestId += 1;
  selectedMemberCandidate.value = null;
  const shouldSuppressSearch = memberSearchQuery.value !== "";
  suppressMemberSearch = shouldSuppressSearch;
  memberSearchQuery.value = "";
  memberSearchResults.value = [];
  memberSearchError.value = "";
  memberSearchLoading.value = false;
}

function selectMemberCandidate(candidate) {
  selectedMemberCandidate.value = candidate;
  memberForm.value.userId = candidate.id;
  memberSearchResults.value = [];
  memberSearchError.value = "";
  memberSearchLoading.value = false;
  memberSearchRequestId += 1;
  const nextQuery = formatMemberCandidate(candidate);
  suppressMemberSearch = memberSearchQuery.value !== nextQuery;
  memberSearchQuery.value = nextQuery;
}

async function refreshMembersForBand(bandId) {
  memberLoading.value = true;

  try {
    const members = await BandMemberRepository.getByBand(bandId);
    membersByBand.value = {
      ...membersByBand.value,
      [bandId]: members ?? []
    };
  } catch (error) {
    memberModalNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "bands.memberManagement.loadError")
    };
  } finally {
    memberLoading.value = false;
  }
}

async function submitMember() {
  if (!memberModalBand.value) {
    return;
  }

  if (
    !selectedMemberCandidate.value ||
    !memberForm.value.userId ||
    Number(memberForm.value.userId) < 1
  ) {
    memberModalNotice.value = {
      type: "error",
      message: t("bands.memberManagement.invalidUserSelection")
    };
    return;
  }

  memberSaving.value = true;
  memberModalNotice.value = null;

  try {
    await BandMemberRepository.addMember(memberModalBand.value.id, {
      userId: Number(memberForm.value.userId),
      roleInBand: memberForm.value.roleInBand
    });
    memberModalNotice.value = { type: "success", message: t("bands.memberManagement.addSuccess") };
    memberForm.value = createEmptyMemberForm();
    resetMemberSearch();
    await refreshMembersForBand(memberModalBand.value.id);
  } catch (error) {
    memberModalNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "bands.memberManagement.addError")
    };
  } finally {
    memberSaving.value = false;
  }
}

async function handleMemberRoleChange(member, roleInBand) {
  if (!memberModalBand.value || member.roleInBand === roleInBand) {
    return;
  }

  memberBusyId.value = member.id;
  memberModalNotice.value = null;

  try {
    await BandMemberRepository.updateRole(memberModalBand.value.id, member.id, { roleInBand });
    memberModalNotice.value = { type: "success", message: t("bands.memberManagement.roleUpdated") };
    await refreshMembersForBand(memberModalBand.value.id);
  } catch (error) {
    memberModalNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "bands.memberManagement.roleError")
    };
    await refreshMembersForBand(memberModalBand.value.id);
  } finally {
    memberBusyId.value = null;
  }
}

async function deactivateMember(member) {
  if (!memberModalBand.value) {
    return;
  }

  const confirmed = window.confirm(
    t("bands.memberManagement.removeConfirm", { name: memberName(member) })
  );

  if (!confirmed) {
    return;
  }

  memberBusyId.value = member.id;
  memberModalNotice.value = null;

  try {
    await BandMemberRepository.deactivateMember(memberModalBand.value.id, member.id);
    memberModalNotice.value = { type: "success", message: t("bands.memberManagement.removed") };
    await refreshMembersForBand(memberModalBand.value.id);
  } catch (error) {
    memberModalNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "bands.memberManagement.removeError")
    };
  } finally {
    memberBusyId.value = null;
  }
}

function memberName(member) {
  return (
    [member.user?.name, member.user?.firstSurname, member.user?.secondSurname]
      .filter(Boolean)
      .join(" ") || t("bands.memberManagement.userFallback", { id: member.user?.id || member.id })
  );
}

function memberCandidateName(candidate) {
  return (
    [candidate?.name, candidate?.firstSurname, candidate?.secondSurname]
      .filter(Boolean)
      .join(" ") || candidate?.email || t("bands.memberManagement.userFallback", { id: candidate?.id })
  );
}

function formatMemberCandidate(candidate) {
  const name = memberCandidateName(candidate);
  return candidate?.email && candidate.email !== name ? `${name} (${candidate.email})` : name;
}

function formatMemberJoinDate(value) {
  return value ? formatDateTime(value, locale.value) : "--";
}

function isCurrentUserMember(member) {
  return Number(member.user?.id) === Number(currentUserId.value);
}

function handleViewRecruitment(item) {
  const query = { highlight: String(item.id) };

  if (isMineView.value) {
    query.view = "mine";
  }

  router.push({
    name: "BandRecruitmentList",
    query
  });
}

function handleManageRecruitment(item) {
  if (!item.canManage) {
    pageNotice.value = { type: "warning", message: t("bands.placeholders.noLeaderBand") };
    return;
  }

  router.push({
    name: "BandRecruitmentList",
    query: {
      view: "mine",
      highlight: String(item.id)
    }
  });
}

function exploreBands() {
  if (isMineView.value) {
    router.push({ name: "BandList" });
    return;
  }

  router.push({ name: "BandRecruitmentList" });
}

function createEmptyBandForm() {
  return {
    name: "",
    description: "",
    mainGenre: "",
    baseCity: "",
    image: ""
  };
}

function createEmptyRecruitmentForm(bandId = "", city = "") {
  return {
    bandId: bandId ? String(bandId) : "",
    title: "",
    description: "",
    roleWanted: "",
    levelRequired: "INTERMEDIATE",
    city: city || "",
    vacancies: "1",
    instrumentId: ""
  };
}

function createEmptyBandEventForm(band = null) {
  const selectedBand = band?.id
    ? eventPublishBands.value.find((item) => Number(item.id) === Number(band.id))
    : eventPublishBands.value[0];

  return {
    ...createEmptyEventForm(),
    bandId: selectedBand?.id ? String(selectedBand.id) : "",
    musicalGenre: selectedBand?.mainGenre || "",
    city: selectedBand?.baseCity || "",
    country: "Spain",
    status: "DRAFT",
    source: "INTERNAL"
  };
}

function createEmptyMemberForm() {
  return {
    userId: "",
    roleInBand: "MEMBER"
  };
}
</script>

<style scoped>
.bands-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 22%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.bands-shell {
  padding-bottom: 3rem;
}

.bands-header {
  display: grid;
  grid-template-columns: 1fr minmax(0, 760px) 1fr;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1.35rem;
}

.bands-header__copy {
  grid-column: 2;
  text-align: center;
}

.bands-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.bands-header__copy h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: 0;
}

.bands-header__copy p {
  max-width: 720px;
  margin: 0.6rem auto 0;
  color: #b8b8b8;
  line-height: 1.65;
}

.bands-header__cta {
  grid-column: 3;
  justify-self: end;
  min-height: 46px;
  padding-inline: 1.15rem;
  border-radius: 16px;
}

.bands-view-switch {
  display: inline-flex;
  gap: 0.35rem;
  margin-bottom: 1rem;
  padding: 0.35rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.bands-view-switch button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  min-height: 40px;
  padding: 0 0.95rem;
  border: 0;
  border-radius: 14px;
  background: transparent;
  color: #bdbdbd;
  font-weight: 700;
}

.bands-view-switch button.is-active {
  background: rgba(29, 185, 84, 0.16);
  color: #dfffe9;
}

.bands-view-switch i {
  color: #1db954;
}

.section-spacing {
  margin-top: 1.25rem;
}

.bands-public-intro,
.bands-public-card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.14), transparent 34%),
    linear-gradient(180deg, #111111 0%, #181818 100%);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.bands-public-intro {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.35rem;
  border-radius: 30px;
}

.bands-public-eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.bands-public-intro h2,
.bands-public-card h3 {
  margin: 0.35rem 0 0;
}

.bands-public-intro p,
.bands-public-card p {
  max-width: 720px;
  margin: 0.55rem 0 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.bands-public-intro__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.7rem;
}

.bands-public-intro__actions .btn {
  min-height: 44px;
  border-radius: 14px;
  font-weight: 700;
}

.bands-public-card {
  padding: 1.2rem;
  border-radius: 28px;
}

.bands-public-card__steps {
  display: grid;
  gap: 0.7rem;
  margin-top: 1rem;
}

.bands-public-card__steps span {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  min-height: 44px;
  padding: 0 0.9rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  color: #d8d8d8;
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0;
  text-transform: none;
}

.bands-public-card__steps i {
  color: #1db954;
}

.bands-mid-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.bands-panel {
  padding: 1.35rem;
  border-radius: 30px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.bands-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.15rem;
}

.bands-panel__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.4rem;
}

.bands-panel__count {
  color: #b9b9b9;
}

.bands-panel__tabs {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.bands-stack {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.page-notice,
.state-card {
  margin-bottom: 1rem;
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
  color: #f4d06f;
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.page-notice.info,
.state-card--soft {
  color: #d6d6d6;
}

.page-notice.error,
.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  text-align: center;
}

.member-modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1050;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.72);
}

.member-modal {
  width: min(840px, 100%);
  max-height: min(860px, calc(100vh - 2rem));
  overflow: auto;
  padding: 1.25rem;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.38);
}

.member-modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.member-modal__header h2 {
  margin: 0.3rem 0 0;
  font-size: 1.45rem;
}

.member-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.member-modal__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.member-modal__notice,
.member-modal__empty,
.member-modal__loading {
  margin-bottom: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.member-modal__notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.member-modal__notice.error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.member-modal__loading {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.member-form {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(160px, 220px) auto;
  gap: 0.85rem;
  align-items: end;
  margin-bottom: 1rem;
}

.member-form .btn {
  min-height: 44px;
  min-width: 150px;
  border-radius: 14px;
  white-space: nowrap;
}

.member-form__field {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.member-form__field--search {
  min-width: 0;
}

.member-form__field > label {
  color: #a9a9a9;
  font-size: 0.82rem;
}

.member-search-input {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
  padding: 0 0.75rem;
}

.member-search-input i {
  color: #1db954;
}

.member-search-input input {
  width: 100%;
  min-width: 0;
  border: 0;
  outline: 0;
  background: transparent;
  color: #ffffff;
  padding: 0.65rem 0;
}

.member-search-input input::placeholder {
  color: rgba(255, 255, 255, 0.48);
}

.member-search-status {
  min-height: 1rem;
  color: #a9a9a9;
  font-size: 0.78rem;
}

.member-search-results {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  max-height: 230px;
  overflow: auto;
  padding: 0.45rem;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.member-search-result {
  display: grid;
  grid-template-columns: 36px minmax(0, 1fr);
  gap: 0.65rem;
  align-items: center;
  width: 100%;
  padding: 0.55rem;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  color: #ffffff;
  text-align: left;
}

.member-search-result:hover,
.member-search-result[aria-selected="true"] {
  border-color: rgba(29, 185, 84, 0.35);
  background: rgba(29, 185, 84, 0.1);
}

.member-search-result__avatar {
  display: block;
  overflow: hidden;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-search-result__avatar :deep(.app-image),
.member-search-result__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.member-search-result__copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 0.1rem;
}

.member-search-result__copy strong,
.member-search-result__copy span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-search-result__copy span,
.member-search-results__state {
  color: #a9a9a9;
  font-size: 0.82rem;
}

.member-search-results__state {
  padding: 0.55rem;
}

.member-search-results__state.is-error {
  color: #ffb3bd;
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.member-row {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) 160px auto;
  gap: 0.85rem;
  align-items: center;
  padding: 0.85rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.member-row__avatar {
  display: block;
  overflow: hidden;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-row__avatar :deep(.app-image),
.member-row__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.member-row__copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 0.25rem;
}

.member-row__copy strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-row__copy span,
.member-modal__empty {
  color: #a9a9a9;
}

@media (max-width: 991.98px) {
  .bands-header,
  .bands-mid-grid {
    grid-template-columns: 1fr;
  }

  .bands-header__copy,
  .bands-header__cta {
    grid-column: auto;
  }

  .bands-header__copy {
    text-align: left;
  }

  .bands-header__copy p {
    margin-left: 0;
  }

  .bands-header__cta {
    justify-self: stretch;
  }

  .bands-public-intro {
    flex-direction: column;
    align-items: flex-start;
  }

  .bands-public-intro__actions {
    justify-content: flex-start;
  }

  .bands-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .member-form,
  .member-row {
    grid-template-columns: 1fr;
  }

  .member-form .btn,
  .member-row .btn {
    width: 100%;
  }
}

@media (max-width: 575.98px) {
  .member-modal {
    padding: 1rem;
    border-radius: 24px;
  }

  .member-modal__header {
    align-items: flex-start;
  }
}
</style>
