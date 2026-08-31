<template>
  <div class="reservation-page">
    <section class="reservation-shell">
      <div class="container py-4">
        <header class="reservation-header">
          <div class="reservation-header__copy">
            <h1>{{ headerTitle }}</h1>
            <p>{{ headerSubtitle }}</p>

            <div class="reservation-header__mode" role="group" :aria-label="t('reservations.header.modeLabel')">
              <button
                type="button"
                :class="{ 'is-active': reservationMode === 'mine' }"
                @click="setReservationMode('mine')"
              >
                <i class="bi bi-person-lines-fill"></i>
                <span>{{ t("reservations.header.mine") }}</span>
              </button>
              <button
                type="button"
                :class="{ 'is-active': reservationMode === 'received' }"
                @click="setReservationMode('received')"
              >
                <i class="bi bi-inboxes"></i>
                <span>{{ t("reservations.header.received") }}</span>
              </button>
            </div>
          </div>

          <button
            type="button"
            class="btn btn-success reservation-header__cta"
            @click="goToPrimaryAction"
          >
            <i class="bi bi-plus-lg"></i>
            {{ primaryActionLabel }}
          </button>
        </header>

        <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
          {{ pageNotice.message }}
        </div>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("reservations.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("reservations.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadReservations">
            {{ t("reservations.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <ReservationStatsRow :stats="statsCards" />

          <UpcomingReservationCard
            v-if="upcomingReservation"
            class="section-spacing"
            :reservation="upcomingReservation"
            :busy="stateActionId === upcomingReservation.id"
            @view="handleView"
            @state="openStateActionModal"
            @contact="handleContact"
          />

          <section class="reservation-panel section-spacing">
            <div class="reservation-panel__header">
              <div>
                <span class="reservation-panel__eyebrow">{{ listEyebrow }}</span>
                <h2>{{ listTitle }}</h2>
              </div>

              <span class="reservation-panel__count">
                {{ t("reservations.list.total", { count: reservations.length }) }}
              </span>
            </div>

            <ReservationFiltersBar v-model="filters" />

            <ReservationStatusTabs
              class="reservation-panel__tabs"
              :tabs="tabs"
              :active-tab="activeTab"
              @change="activeTab = $event"
            />

            <ReservationEmptyState
              v-if="!reservations.length"
              :title="emptyStateTitle"
              :text="emptyStateText"
              :action-label="emptyStateActionLabel"
              @action="goToPrimaryAction"
            />

            <ReservationEmptyState
              v-else-if="!filteredReservations.length"
              icon="bi bi-funnel"
              :title="t('reservations.empty.filteredTitle')"
              :text="t('reservations.empty.filteredText')"
              :action-label="t('reservations.filters.clear')"
              @action="resetFilters"
            />

            <div v-else class="reservation-stack">
              <ReservationListCard
                v-for="reservation in filteredReservations"
                :key="reservation.id"
                :reservation="reservation"
                :busy="cancellingId === reservation.id || stateActionId === reservation.id"
                @view="handleView"
                @edit="handleEdit"
                @cancel="handleCancel"
                @state="openStateActionModal"
                @contact="handleContact"
                @rebook="handleRebook"
                @reason="handleReason"
              />
            </div>
          </section>
        </template>
      </div>
    </section>

    <div
      v-if="selectedReservation"
      class="reservation-modal-backdrop"
      @click.self="closeReservationDetail"
    >
      <section class="reservation-modal" role="dialog" aria-modal="true">
        <header class="reservation-modal__header">
          <div>
            <span class="reservation-modal__eyebrow">{{ t("reservations.detail.eyebrow") }}</span>
            <h2>{{ selectedReservation.name }}</h2>
          </div>
          <button
            type="button"
            class="reservation-modal__close"
            :aria-label="t('reservations.detail.close')"
            @click="closeReservationDetail"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <div class="reservation-modal__grid">
          <div
            v-for="row in selectedReservationDetails"
            :key="row.label"
            class="reservation-modal__field"
          >
            <span>{{ row.label }}</span>
            <strong>{{ row.value }}</strong>
          </div>
        </div>

        <div v-if="selectedReservation.observations" class="reservation-modal__notes">
          <span>{{ t("reservations.detail.notes") }}</span>
          <p>{{ selectedReservation.observations }}</p>
        </div>

        <div v-if="selectedReservation.cancellationReason" class="reservation-modal__notes">
          <span>{{ t("reservations.detail.cancellationReason") }}</span>
          <p>{{ selectedReservation.cancellationReason }}</p>
        </div>

        <footer class="reservation-modal__footer">
          <button type="button" class="btn btn-outline-light" @click="closeReservationDetail">
            {{ t("reservations.detail.close") }}
          </button>
        </footer>
      </section>
    </div>

    <div v-if="editingReservation" class="reservation-modal-backdrop" @click.self="closeEditModal">
      <form class="reservation-modal" role="dialog" aria-modal="true" @submit.prevent="submitEdit">
        <header class="reservation-modal__header">
          <div>
            <span class="reservation-modal__eyebrow">{{ t("reservations.edit.eyebrow") }}</span>
            <h2>{{ editingReservation.name }}</h2>
          </div>
          <button
            type="button"
            class="reservation-modal__close"
            :aria-label="t('reservations.detail.close')"
            @click="closeEditModal"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <div class="reservation-form-grid">
          <label>
            {{ t("reservations.edit.sessionDate") }}
            <input v-model="editForm.sessionDate" type="date" required />
          </label>

          <label>
            {{ t("reservations.edit.startTime") }}
            <input v-model="editForm.startTime" type="time" required />
          </label>

          <label>
            {{ t("reservations.edit.endTime") }}
            <input v-model="editForm.endTime" type="time" required />
          </label>

          <label>
            {{ t("reservations.edit.attendees") }}
            <input v-model.number="editForm.attendeesCount" type="number" min="1" required />
          </label>

          <label>
            {{ t("reservations.edit.sessionType") }}
            <AppSelect
              :model-value="editForm.sessionType"
              :options="sessionTypeSelectOptions"
              :label="t('reservations.edit.sessionType')"
              @update:model-value="editForm.sessionType = $event"
            />
          </label>

          <label class="reservation-form-grid__wide">
            {{ t("reservations.edit.notes") }}
            <textarea v-model="editForm.observations" rows="4" maxlength="1000"></textarea>
          </label>
        </div>

        <p v-if="editError" class="reservation-modal__error">{{ editError }}</p>

        <footer class="reservation-modal__footer">
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="savingEdit"
            @click="closeEditModal"
          >
            {{ t("reservations.edit.cancel") }}
          </button>
          <button type="submit" class="btn btn-success" :disabled="savingEdit">
            {{ savingEdit ? t("reservations.edit.saving") : t("reservations.edit.save") }}
          </button>
        </footer>
      </form>
    </div>

    <div
      v-if="cancelAction.reservation"
      class="reservation-modal-backdrop"
      @click.self="closeCancelModal"
    >
      <form
        class="reservation-modal reservation-modal--confirm"
        role="dialog"
        aria-modal="true"
        @submit.prevent="submitCancel"
      >
        <header class="reservation-modal__header">
          <div>
            <span class="reservation-modal__eyebrow">{{ t("reservations.cancel.eyebrow") }}</span>
            <h2>{{ t("reservations.cancel.title") }}</h2>
          </div>
          <button
            type="button"
            class="reservation-modal__close"
            :aria-label="t('reservations.cancel.close')"
            :disabled="Boolean(cancellingId)"
            @click="closeCancelModal"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <p class="reservation-modal__copy">
          {{
            t("reservations.cancel.text", {
              name: cancelAction.reservation?.name || t("reservations.cards.fallbackSpace")
            })
          }}
        </p>

        <label class="reservation-modal__field reservation-modal__field--stacked">
          <span>{{ t("reservations.cancel.reasonLabel") }}</span>
          <textarea
            v-model="cancelAction.reason"
            rows="4"
            maxlength="1000"
            :placeholder="t('reservations.cancel.reasonPlaceholder')"
          ></textarea>
        </label>

        <p v-if="cancelError" class="reservation-modal__error">{{ cancelError }}</p>

        <footer class="reservation-modal__footer">
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="Boolean(cancellingId)"
            @click="closeCancelModal"
          >
            {{ t("reservations.cancel.close") }}
          </button>
          <button type="submit" class="btn btn-outline-danger" :disabled="Boolean(cancellingId)">
            {{ cancellingId ? t("reservations.cancel.submitting") : t("reservations.cancel.confirm") }}
          </button>
        </footer>
      </form>
    </div>

    <div
      v-if="stateAction.reservation"
      class="reservation-modal-backdrop"
      @click.self="closeStateActionModal"
    >
      <section class="reservation-modal reservation-modal--confirm" role="dialog" aria-modal="true">
        <header class="reservation-modal__header">
          <div>
            <span class="reservation-modal__eyebrow">{{ t("reservations.stateAction.eyebrow") }}</span>
            <h2>{{ stateActionTitle }}</h2>
          </div>
          <button
            type="button"
            class="reservation-modal__close"
            :aria-label="t('reservations.stateAction.close')"
            :disabled="stateActionSubmitting"
            @click="closeStateActionModal"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <p class="reservation-modal__copy">
          {{ stateActionText }}
        </p>

        <p v-if="stateActionError" class="reservation-modal__error">{{ stateActionError }}</p>

        <footer class="reservation-modal__footer">
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="stateActionSubmitting"
            @click="closeStateActionModal"
          >
            {{ t("reservations.stateAction.cancel") }}
          </button>
          <button
            type="button"
            class="btn"
            :class="stateActionConfirmClass"
            :disabled="stateActionSubmitting"
            @click="submitStateAction"
          >
            {{ stateActionSubmitting ? t("reservations.states.updatingState") : stateActionConfirmLabel }}
          </button>
        </footer>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import AppSelect from "@/common/components/AppSelect.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import ReservationSessionRepository from "@/repositories/ReservationSessionRepository";
import { formatDateTime, normalizeTimeString } from "@/modules/musical-spaces/spaceDetailUtils";
import ReservationEmptyState from "../components/ReservationEmptyState.vue";
import ReservationFiltersBar from "../components/ReservationFiltersBar.vue";
import ReservationListCard from "../components/ReservationListCard.vue";
import ReservationStatsRow from "../components/ReservationStatsRow.vue";
import ReservationStatusTabs from "../components/ReservationStatusTabs.vue";
import UpcomingReservationCard from "../components/UpcomingReservationCard.vue";
import {
  filterReservations,
  getReservationStats,
  getUpcomingReservation,
  mapReservationForCard,
  matchesStatusTab,
  sortReservations
} from "../reservationUtils";

const route = useRoute();
const router = useRouter();
const { locale, t } = useI18n();

const reservations = ref([]);
const loading = ref(true);
const errorMessage = ref("");
const pageNotice = ref(null);
const activeTab = ref("all");
const cancellingId = ref(null);
const stateActionId = ref(null);
const selectedReservation = ref(null);
const editingReservation = ref(null);
const savingEdit = ref(false);
const editError = ref("");
const cancelAction = ref({
  reservation: null,
  reason: ""
});
const cancelError = ref("");
const stateActionSubmitting = ref(false);
const stateActionError = ref("");
const reservationMode = computed(() => (route.query.tab === "received" ? "received" : "mine"));
const isReceivedMode = computed(() => reservationMode.value === "received");
const stateAction = ref({
  reservation: null,
  state: ""
});

const filters = ref({
  searchText: "",
  date: "",
  sessionType: "",
  order: "nearest"
});

const sessionTypeOptions = ["REHEARSAL", "RECORDING", "CLASS", "EVENT_PREPARATION", "OTHER"];

const sessionTypeSelectOptions = computed(() =>
  sessionTypeOptions.map((type) => ({
    value: type,
    label: t(`reservations.sessionTypes.${type}`)
  }))
);

const editForm = ref({
  sessionDate: "",
  startTime: "",
  endTime: "",
  attendeesCount: 1,
  sessionType: "REHEARSAL",
  observations: ""
});

const stats = computed(() => getReservationStats(reservations.value));

const headerTitle = computed(() =>
  isReceivedMode.value ? t("reservations.header.receivedTitle") : t("reservations.header.mineTitle")
);

const headerSubtitle = computed(() =>
  isReceivedMode.value ? t("reservations.header.receivedSubtitle") : t("reservations.header.mineSubtitle")
);

const listEyebrow = computed(() =>
  isReceivedMode.value ? t("reservations.list.receivedEyebrow") : t("reservations.list.eyebrow")
);

const listTitle = computed(() =>
  isReceivedMode.value ? t("reservations.list.receivedTitle") : t("reservations.list.title")
);

const primaryActionLabel = computed(() =>
  isReceivedMode.value ? t("reservations.empty.receivedAction") : t("reservations.header.new")
);

const emptyStateTitle = computed(() =>
  isReceivedMode.value ? t("reservations.empty.receivedTitle") : t("reservations.empty.title")
);

const emptyStateText = computed(() =>
  isReceivedMode.value ? t("reservations.empty.receivedText") : t("reservations.empty.text")
);

const emptyStateActionLabel = computed(() =>
  isReceivedMode.value ? t("reservations.empty.receivedAction") : t("reservations.empty.action")
);

const statsCards = computed(() => [
  {
    id: "active",
    icon: "bi bi-calendar-check",
    value: stats.value.active,
    label: t("reservations.stats.active"),
    tone: "success"
  },
  {
    id: "completed",
    icon: "bi bi-check2-circle",
    value: stats.value.completed,
    label: t("reservations.stats.completed"),
    tone: "muted"
  },
  {
    id: "cancelled",
    icon: "bi bi-x-circle",
    value: stats.value.cancelled,
    label: t("reservations.stats.cancelled"),
    tone: "danger"
  }
]);

const upcomingReservation = computed(() => {
  const nextReservation = getUpcomingReservation(reservations.value);
  return nextReservation ? decorateReservationForMode(nextReservation) : null;
});

const tabs = computed(() => [
  {
    id: "all",
    label: t("reservations.tabs.all"),
    count: reservations.value.length
  },
  {
    id: "active",
    label: t("reservations.tabs.active"),
    count: reservations.value.filter((reservation) => matchesStatusTab(reservation, "active"))
      .length
  },
  {
    id: "pending",
    label: t("reservations.tabs.pending"),
    count: reservations.value.filter((reservation) => matchesStatusTab(reservation, "pending"))
      .length
  },
  {
    id: "completed",
    label: t("reservations.tabs.completed"),
    count: reservations.value.filter((reservation) => matchesStatusTab(reservation, "completed"))
      .length
  },
  {
    id: "cancelled",
    label: t("reservations.tabs.cancelled"),
    count: reservations.value.filter((reservation) => matchesStatusTab(reservation, "cancelled"))
      .length
  }
]);

const filteredReservations = computed(() => {
  const filtered = filterReservations(reservations.value, {
    ...filters.value,
    statusTab: activeTab.value
  });

  return sortReservations(filtered, filters.value.order).map(decorateReservationForMode);
});

const selectedReservationDetails = computed(() => {
  if (!selectedReservation.value) {
    return [];
  }

  return buildReservationDetails(selectedReservation.value);
});

const stateActionTitle = computed(() =>
  stateAction.value.state ? t(`reservations.stateAction.${stateAction.value.state}.title`) : ""
);

const stateActionText = computed(() =>
  stateAction.value.state
    ? t(`reservations.stateAction.${stateAction.value.state}.text`, {
        name: stateAction.value.reservation?.name || t("reservations.cards.fallbackSpace")
      })
    : ""
);

const stateActionConfirmLabel = computed(() =>
  stateAction.value.state ? t(`reservations.stateAction.${stateAction.value.state}.confirm`) : ""
);

const stateActionConfirmClass = computed(() =>
  stateAction.value.state === "REJECTED" ? "btn-outline-danger" : "btn-success"
);

watch(
  () => route.query.tab,
  () => {
    loadReservations();
  },
  { immediate: true }
);

watch(
  () => route.query.reservationId,
  () => {
    syncNoticeFromRoute();
  }
);

async function loadReservations() {
  loading.value = true;
  errorMessage.value = "";

  try {
    reservations.value =
      reservationMode.value === "received"
        ? await ReservationSessionRepository.getManagedSpacesReservations()
        : await ReservationSessionRepository.getMine();
    syncNoticeFromRoute();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "reservations.states.error");
  } finally {
    loading.value = false;
  }
}

function goToPrimaryAction() {
  if (isReceivedMode.value) {
    router.push({
      name: "MusicalSpaceList",
      query: { view: "mine", mode: "create" }
    });
    return;
  }

  router.push({ name: "SearchPage" });
}

function setReservationMode(mode) {
  if (reservationMode.value === mode) {
    return;
  }

  const query = { ...route.query };
  delete query.reservationId;

  if (mode === "received") {
    query.tab = "received";
  } else {
    delete query.tab;
  }

  router.push({ name: "ReservationList", query });
}

function resetFilters() {
  filters.value = {
    searchText: "",
    date: "",
    sessionType: "",
    order: "nearest"
  };
  activeTab.value = "all";
}

function decorateReservationForMode(reservation) {
  const mapped = mapReservationForCard(reservation, locale.value, t);
  const isReceived = reservationMode.value === "received";
  const isMine = reservationMode.value === "mine";

  return {
    ...mapped,
    canModify: isMine && mapped.canModify,
    canCancel: isMine && mapped.canCancel,
    canRebook: isMine && Boolean(mapped.spaceId),
    canAccept: isReceived && reservation.state === "PENDING" && !mapped.hasStarted,
    canReject: isReceived && reservation.state === "PENDING",
    canComplete: isReceived && reservation.state === "ACCEPTED" && mapped.isPast
  };
}

function syncNoticeFromRoute() {
  const reservationId = Number(route.query.reservationId);

  if (!reservationId) {
    return;
  }

  const reservation = reservations.value.find((item) => Number(item.id) === reservationId);

  if (reservation) {
    selectedReservation.value = mapReservationForCard(reservation, locale.value, t);
    pageNotice.value = null;
    return;
  }

  pageNotice.value = {
    type: "warning",
    message: t("reservations.placeholders.notFound")
  };
}

function handleView(reservation) {
  selectedReservation.value = reservation;
  pageNotice.value = null;
}

function openStateActionModal(reservation, state) {
  if (reservationMode.value !== "received") {
    return;
  }

  const allowed =
    (reservation.state === "PENDING" && state === "ACCEPTED" && !reservation.hasStarted) ||
    (reservation.state === "PENDING" && state === "REJECTED") ||
    (reservation.state === "ACCEPTED" && state === "COMPLETED" && reservation.isPast);

  if (!allowed) {
    pageNotice.value = {
      type: "warning",
      message: t("reservations.stateAction.invalid")
    };
    return;
  }

  stateAction.value = { reservation, state };
  stateActionError.value = "";
}

function closeStateActionModal() {
  if (stateActionSubmitting.value) {
    return;
  }

  stateAction.value = { reservation: null, state: "" };
  stateActionError.value = "";
}

async function submitStateAction() {
  if (!stateAction.value.reservation || !stateAction.value.state) {
    return;
  }

  stateActionSubmitting.value = true;
  stateActionId.value = stateAction.value.reservation.id;
  stateActionError.value = "";

  try {
    await ReservationSessionRepository.updateState(stateAction.value.reservation.id, {
      state: stateAction.value.state
    });

    pageNotice.value = {
      type: "success",
      message: t(`reservations.stateAction.${stateAction.value.state}.success`)
    };
    stateAction.value = { reservation: null, state: "" };
    stateActionError.value = "";
    await loadReservations();
  } catch (error) {
    stateActionError.value = getApiErrorMessage(error, t, "reservations.stateAction.error");
  } finally {
    stateActionSubmitting.value = false;
    stateActionId.value = null;
  }
}

function handleEdit(reservation) {
  editingReservation.value = reservation;
  editError.value = "";
  editForm.value = {
    sessionDate: reservation.sessionDate || "",
    startTime: normalizeTimeString(reservation.startTime),
    endTime: normalizeTimeString(reservation.endTime),
    attendeesCount: Number(reservation.attendeesCount || 1),
    sessionType: reservation.sessionType || "REHEARSAL",
    observations: reservation.observations || ""
  };
}

function closeReservationDetail() {
  selectedReservation.value = null;

  if (route.query.reservationId) {
    router.replace({
      query: {
        ...route.query,
        reservationId: undefined
      }
    });
  }
}

function closeEditModal() {
  editingReservation.value = null;
  editError.value = "";
}

async function submitEdit() {
  if (!editingReservation.value) {
    return;
  }

  const validationError = validateEditForm(editingReservation.value);

  if (validationError) {
    editError.value = validationError;
    return;
  }

  savingEdit.value = true;
  editError.value = "";

  try {
    const updatedReservation = await ReservationSessionRepository.update(
      editingReservation.value.id,
      buildReservationUpdatePayload(editingReservation.value)
    );

    reservations.value = reservations.value.map((reservation) =>
      reservation.id === updatedReservation.id ? updatedReservation : reservation
    );

    closeEditModal();
    pageNotice.value = {
      type: "success",
      message: t("reservations.edit.updated")
    };
  } catch (error) {
    editError.value = getApiErrorMessage(error, t, "reservations.edit.error");
  } finally {
    savingEdit.value = false;
  }
}

function handleCancel(reservation) {
  cancelAction.value = {
    reservation,
    reason: ""
  };
  cancelError.value = "";
  pageNotice.value = null;
}

function closeCancelModal() {
  if (cancellingId.value) {
    return;
  }

  cancelAction.value = {
    reservation: null,
    reason: ""
  };
  cancelError.value = "";
}

async function submitCancel() {
  const reservation = cancelAction.value.reservation;
  const reason = cancelAction.value.reason;

  if (!reservation) {
    return;
  }

  if (!reason.trim()) {
    cancelError.value = t("reservations.cancel.emptyReason");
    return;
  }

  cancellingId.value = reservation.id;
  cancelError.value = "";

  try {
    await ReservationSessionRepository.cancel(reservation.id, {
      cancellationReason: reason.trim()
    });

    pageNotice.value = {
      type: "success",
      message: t("reservations.cancel.success")
    };
    cancelAction.value = {
      reservation: null,
      reason: ""
    };
    await loadReservations();
  } catch (error) {
    cancelError.value = getApiErrorMessage(error, t, "reservations.cancel.error");
  } finally {
    cancellingId.value = null;
  }
}

function handleContact(reservation) {
  router.push({
    name: "MessageInbox",
    query: { reservationId: String(reservation.id) }
  });
}

function handleRebook(reservation) {
  if (!reservation.spaceId) {
    pageNotice.value = {
      type: "warning",
      message: t("reservations.placeholders.rebookUnavailable")
    };
    return;
  }

  router.push({
    name: "MusicalSpaceDetail",
    params: { id: reservation.spaceId }
  });
}

function handleReason(reservation) {
  pageNotice.value = {
    type: "warning",
    message: reservation.cancellationReason
  };
}

function buildReservationDetails(reservation) {
  return [
    { label: t("reservations.detail.fields.space"), value: reservation.name },
    { label: t("reservations.detail.fields.location"), value: reservation.location },
    { label: t("reservations.detail.fields.date"), value: reservation.dateLabel },
    { label: t("reservations.detail.fields.schedule"), value: reservation.timeRangeLabel },
    { label: t("reservations.detail.fields.status"), value: reservation.statusLabel },
    { label: t("reservations.detail.fields.sessionType"), value: reservation.sessionTypeLabel },
    { label: t("reservations.detail.fields.attendees"), value: reservation.attendeesLabel },
    { label: t("reservations.detail.fields.price"), value: reservation.priceLabel },
    { label: t("reservations.detail.fields.user"), value: formatUserName(reservation.user) },
    { label: t("reservations.detail.fields.band"), value: reservation.band?.name },
    { label: t("reservations.detail.fields.createdAt"), value: formatDateTime(reservation.createdAt, locale.value) }
  ].filter((row) => row.value);
}

function formatUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}

function validateEditForm(reservation) {
  if (!editForm.value.sessionDate || !editForm.value.startTime || !editForm.value.endTime) {
    return t("reservations.edit.validationRequired");
  }

  if (editForm.value.startTime >= editForm.value.endTime) {
    return t("reservations.edit.validationTimeOrder");
  }

  if (Number(editForm.value.attendeesCount) < 1) {
    return t("reservations.edit.validationAttendees");
  }

  if (
    reservation.musicalSpace?.capacity &&
    Number(editForm.value.attendeesCount) > Number(reservation.musicalSpace.capacity)
  ) {
    return t("reservations.edit.validationCapacity", {
      capacity: reservation.musicalSpace.capacity
    });
  }

  return "";
}

function buildReservationUpdatePayload(reservation) {
  return {
    musicalSpaceId: reservation.musicalSpace?.id || reservation.spaceId,
    sessionDate: editForm.value.sessionDate,
    startTime: editForm.value.startTime,
    endTime: editForm.value.endTime,
    attendeesCount: Number(editForm.value.attendeesCount),
    sessionType: editForm.value.sessionType,
    observations: editForm.value.observations?.trim() || null,
    bandId: reservation.band?.id || null
  };
}
</script>

<style scoped>
.reservation-page {
  min-height: calc(100vh - 72px);
  background:
    linear-gradient(180deg, #080a09 0%, #0d100f 28%, #080a09 100%);
  color: #ffffff;
}

.reservation-shell {
  padding-bottom: 3rem;
}

.reservation-header {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 1.25rem;
  align-items: end;
  margin-bottom: 1rem;
  padding: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.035);
}

.reservation-header__copy {
  min-width: 0;
  text-align: left;
}

.reservation-header__copy p {
  max-width: 620px;
  margin: 0.45rem 0 0;
  color: #c7c7c7;
  line-height: 1.6;
}

.reservation-header__mode {
  display: inline-flex;
  gap: 0.4rem;
  margin-top: 0.85rem;
  padding: 0.25rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
}

.reservation-header__mode button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-height: 38px;
  padding: 0 0.85rem;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: #d9d9d9;
  font-weight: 700;
}

.reservation-header__mode button.is-active {
  background: #1db954;
  color: #071308;
}

.reservation-header__mode button:focus-visible {
  outline: 2px solid #9ee6b3;
  outline-offset: 2px;
}

.reservation-panel__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.reservation-header__copy h1 {
  margin: 0;
  font-size: clamp(1.65rem, 3vw, 2.35rem);
  letter-spacing: 0;
}

.reservation-header__cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  justify-self: end;
  min-height: 42px;
  padding-inline: 1rem;
  border-radius: 10px;
}

.reservation-panel {
  padding: 1rem;
  border-radius: 18px;
  background: rgba(17, 19, 18, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 12px 34px rgba(0, 0, 0, 0.18);
}

.reservation-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.95rem;
}

.reservation-panel__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.4rem;
}

.reservation-panel__count {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 0.75rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.055);
  color: #b9b9b9;
}

.reservation-panel__tabs {
  margin-top: 0.85rem;
  margin-bottom: 0.85rem;
}

.reservation-stack {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section-spacing {
  margin-top: 1rem;
}

.page-notice,
.state-card {
  margin-bottom: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 14px;
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

.page-notice.info {
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

.reservation-modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1050;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.76);
  backdrop-filter: blur(8px);
}

.reservation-modal {
  width: min(760px, 100%);
  max-height: min(860px, calc(100vh - 2rem));
  overflow: auto;
  padding: 1.25rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: #121414;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.38);
}

.reservation-modal__header,
.reservation-modal__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.reservation-modal__header {
  margin-bottom: 1rem;
}

.reservation-modal__header h2 {
  margin: 0.3rem 0 0;
  font-size: 1.45rem;
}

.reservation-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.reservation-modal__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.reservation-modal__grid,
.reservation-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
}

.reservation-modal__field,
.reservation-modal__notes,
.reservation-form-grid label {
  padding: 0.9rem;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.reservation-modal__field span,
.reservation-modal__notes span,
.reservation-form-grid label {
  color: #a9a9a9;
  font-size: 0.82rem;
}

.reservation-modal__field strong {
  display: block;
  margin-top: 0.3rem;
  color: #ffffff;
}

.reservation-modal__field--stacked {
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
  margin-top: 1rem;
}

.reservation-modal__field--stacked textarea {
  width: 100%;
  min-height: 110px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  padding: 0.65rem 0.75rem;
}

.reservation-modal__notes {
  margin-top: 0.85rem;
}

.reservation-modal__notes p {
  margin: 0.35rem 0 0;
  color: #ffffff;
}

.reservation-modal__copy {
  margin: 0;
  color: #d6d6d6;
  line-height: 1.7;
}

.reservation-modal--confirm {
  width: min(620px, 100%);
}

.reservation-form-grid label {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.reservation-form-grid__wide {
  grid-column: 1 / -1;
}

.reservation-form-grid input,
.reservation-form-grid textarea {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  padding: 0.65rem 0.75rem;
}

.reservation-modal__error {
  margin: 0.9rem 0 0;
  padding: 0.85rem 1rem;
  border-radius: 12px;
  border: 1px solid rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.reservation-modal__footer {
  justify-content: flex-end;
  margin-top: 1rem;
}

@media (max-width: 991.98px) {
  .reservation-header {
    grid-template-columns: 1fr;
    justify-items: stretch;
  }

  .reservation-header__copy,
  .reservation-header__cta {
    grid-column: auto;
  }

  .reservation-header__mode {
    width: 100%;
  }

  .reservation-header__mode button {
    flex: 1 1 0;
  }

  .reservation-header__cta {
    justify-self: stretch;
  }

  .reservation-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 575.98px) {
  .reservation-header,
  .reservation-panel {
    margin-inline: -0.25rem;
  }

  .reservation-modal__grid,
  .reservation-form-grid {
    grid-template-columns: 1fr;
  }

  .reservation-modal__header,
  .reservation-modal__footer {
    align-items: stretch;
    flex-direction: column;
  }

  .reservation-modal__footer .btn {
    width: 100%;
  }
}
</style>
