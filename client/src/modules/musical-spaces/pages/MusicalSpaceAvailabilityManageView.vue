<template>
  <div class="availability-manage-page">
    <section class="availability-manage-shell">
      <div class="container py-5">
        <RouterLink class="availability-back" :to="backRoute">
          <i class="bi bi-arrow-left" aria-hidden="true"></i>
          {{ t("spaceAvailabilityManage.header.back") }}
        </RouterLink>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("spaceAvailabilityManage.states.loading") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error">
          <strong>{{ t("spaceAvailabilityManage.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
        </div>

        <template v-else-if="space">
          <header class="availability-hero">
            <div>
              <span class="availability-hero__eyebrow">{{ t("spaceAvailabilityManage.header.eyebrow") }}</span>
              <h1>{{ space.name }}</h1>
              <div class="availability-hero__meta">
                <span>
                  <i class="bi bi-geo-alt" aria-hidden="true"></i>
                  {{ spaceLocationLabel }}
                </span>
                <span>
                  <i class="bi bi-patch-check" aria-hidden="true"></i>
                  {{ t(`spaceList.status.${space.approvalStatus || "PENDING"}`) }}
                </span>
              </div>
            </div>

            <RouterLink class="btn btn-outline-light availability-hero__link" :to="detailRoute">
              <i class="bi bi-arrow-up-right" aria-hidden="true"></i>
              {{ t("spaceAvailabilityManage.header.detail") }}
            </RouterLink>
          </header>

          <div v-if="lockMessage" class="page-notice page-notice--warning">
            <strong>{{ lockTitle }}</strong>
            <span>{{ lockMessage }}</span>
          </div>

          <template v-else>
            <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
              {{ pageNotice.message }}
            </div>

            <section class="availability-stats">
              <article class="availability-stat">
                <span>{{ t("spaceAvailabilityManage.summary.schedules") }}</span>
                <strong>{{ schedules.length }}</strong>
              </article>
              <article class="availability-stat">
                <span>{{ t("spaceAvailabilityManage.summary.exceptions") }}</span>
                <strong>{{ exceptions.length }}</strong>
              </article>
              <article class="availability-stat">
                <span>{{ t("spaceAvailabilityManage.summary.blocked") }}</span>
                <strong>{{ blockedExceptionsCount }}</strong>
              </article>
              <article class="availability-stat">
                <span>{{ t("spaceAvailabilityManage.summary.custom") }}</span>
                <strong>{{ customExceptionsCount }}</strong>
              </article>
            </section>

            <section class="availability-grid">
              <article class="availability-panel">
                <header class="availability-panel__header">
                  <div>
                    <span>{{ t("spaceAvailabilityManage.schedules.eyebrow") }}</span>
                    <h2>{{ t("spaceAvailabilityManage.schedules.formTitle") }}</h2>
                  </div>
                  <button
                    v-if="editingScheduleId"
                    type="button"
                    class="btn btn-outline-light btn-sm"
                    @click="resetScheduleForm"
                  >
                    {{ t("spaceAvailabilityManage.actions.cancelEdit") }}
                  </button>
                </header>

                <div v-if="scheduleError" class="form-error">{{ scheduleError }}</div>

                <form class="availability-form" @submit.prevent="submitScheduleForm">
                  <div class="availability-field availability-field--wide">
                    <label>{{ t("spaceAvailabilityManage.fields.dayOfWeek") }}</label>
                    <AppSelect
                      v-if="editingScheduleId"
                      v-model="scheduleForm.dayOfWeek"
                      :options="dayOptions"
                      :label="t('spaceAvailabilityManage.fields.dayOfWeek')"
                    />
                    <div
                      v-else
                      class="schedule-day-grid"
                      role="group"
                      :aria-label="t('spaceAvailabilityManage.fields.dayOfWeek')"
                    >
                      <label
                        v-for="option in dayOptions"
                        :key="option.value"
                        class="schedule-day-option"
                        :class="{ 'is-selected': scheduleForm.dayOfWeeks.includes(option.value) }"
                      >
                        <input
                          v-model="scheduleForm.dayOfWeeks"
                          class="schedule-day-option__input"
                          type="checkbox"
                          :value="option.value"
                        />
                        <span>{{ option.label }}</span>
                      </label>
                    </div>
                  </div>

                  <div class="availability-field">
                    <label for="schedule-start">{{ t("spaceAvailabilityManage.fields.startTime") }}</label>
                    <input
                      id="schedule-start"
                      v-model="scheduleForm.startTime"
                      type="time"
                      class="form-control"
                      required
                    />
                  </div>

                  <div class="availability-field">
                    <label for="schedule-end">{{ t("spaceAvailabilityManage.fields.endTime") }}</label>
                    <input
                      id="schedule-end"
                      v-model="scheduleForm.endTime"
                      type="time"
                      class="form-control"
                      required
                    />
                  </div>

                  <div class="availability-field availability-field--wide">
                    <label for="schedule-price">{{ t("spaceAvailabilityManage.fields.price") }}</label>
                    <input
                      id="schedule-price"
                      v-model="scheduleForm.price"
                      type="number"
                      min="0.01"
                      step="0.01"
                      class="form-control"
                      required
                    />
                  </div>

                  <div class="availability-form__footer">
                    <button type="submit" class="btn btn-success" :disabled="scheduleSubmitting">
                      <span
                        v-if="scheduleSubmitting"
                        class="spinner-border spinner-border-sm"
                        role="status"
                        aria-hidden="true"
                      ></span>
                      {{ scheduleSubmitLabel }}
                    </button>
                  </div>
                </form>
              </article>

              <article class="availability-panel">
                <header class="availability-panel__header">
                  <div>
                    <span>{{ t("spaceAvailabilityManage.schedules.eyebrow") }}</span>
                    <h2>{{ t("spaceAvailabilityManage.schedules.listTitle") }}</h2>
                  </div>
                </header>

                <div v-if="!scheduleRows.length" class="availability-empty">
                  {{ t("spaceAvailabilityManage.schedules.empty") }}
                </div>

                <div v-else class="availability-row-list">
                  <article
                    v-for="schedule in scheduleRows"
                    :key="schedule.id ?? `${schedule.dayOfWeek}-${schedule.startTime}-${schedule.endTime}`"
                    class="availability-row"
                  >
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.dayOfWeek") }}</span>
                      <strong>{{ schedule.dayLabel }}</strong>
                    </div>
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.timeRange") }}</span>
                      <strong>{{ schedule.timeLabel }}</strong>
                    </div>
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.price") }}</span>
                      <strong>{{ schedule.priceLabel }}</strong>
                    </div>
                    <div class="availability-row__actions">
                      <button type="button" class="btn btn-outline-light btn-sm" @click="editSchedule(schedule)">
                        <i class="bi bi-pencil" aria-hidden="true"></i>
                        {{ t("spaceAvailabilityManage.actions.edit") }}
                      </button>
                      <button
                        type="button"
                        class="btn btn-outline-danger btn-sm"
                        :disabled="deletingScheduleId === schedule.id"
                        @click="deleteSchedule(schedule)"
                      >
                        <i class="bi bi-trash" aria-hidden="true"></i>
                        {{ deletingScheduleId === schedule.id ? t("spaceAvailabilityManage.actions.deleting") : t("spaceAvailabilityManage.actions.delete") }}
                      </button>
                    </div>
                  </article>
                </div>
              </article>
            </section>

            <section class="availability-grid">
              <article class="availability-panel">
                <header class="availability-panel__header">
                  <div>
                    <span>{{ t("spaceAvailabilityManage.exceptions.eyebrow") }}</span>
                    <h2>{{ t("spaceAvailabilityManage.exceptions.formTitle") }}</h2>
                  </div>
                  <button
                    v-if="editingExceptionId"
                    type="button"
                    class="btn btn-outline-light btn-sm"
                    @click="resetExceptionForm"
                  >
                    {{ t("spaceAvailabilityManage.actions.cancelEdit") }}
                  </button>
                </header>

                <div v-if="exceptionError" class="form-error">{{ exceptionError }}</div>

                <form class="availability-form" @submit.prevent="submitExceptionForm">
                  <div class="availability-field">
                    <label for="exception-date">{{ t("spaceAvailabilityManage.fields.date") }}</label>
                    <input
                      id="exception-date"
                      v-model="exceptionForm.date"
                      type="date"
                      class="form-control"
                      :min="todayDate"
                      required
                    />
                  </div>

                  <div class="availability-field">
                    <label>{{ t("spaceAvailabilityManage.fields.exceptionType") }}</label>
                    <AppSelect
                      v-model="exceptionForm.exceptionType"
                      :options="exceptionTypeOptions"
                      :label="t('spaceAvailabilityManage.fields.exceptionType')"
                    />
                  </div>

                  <div class="availability-field">
                    <label for="exception-start">{{ t("spaceAvailabilityManage.fields.startTime") }}</label>
                    <input
                      id="exception-start"
                      v-model="exceptionForm.startTime"
                      type="time"
                      class="form-control"
                      required
                    />
                  </div>

                  <div class="availability-field">
                    <label for="exception-end">{{ t("spaceAvailabilityManage.fields.endTime") }}</label>
                    <input
                      id="exception-end"
                      v-model="exceptionForm.endTime"
                      type="time"
                      class="form-control"
                      required
                    />
                  </div>

                  <div v-if="isCustomExceptionForm" class="availability-field availability-field--wide">
                    <label for="exception-price">{{ t("spaceAvailabilityManage.fields.price") }}</label>
                    <input
                      id="exception-price"
                      v-model="exceptionForm.price"
                      type="number"
                      min="0.01"
                      step="0.01"
                      class="form-control"
                      required
                    />
                  </div>

                  <div class="availability-field availability-field--wide">
                    <label for="exception-reason">{{ t("spaceAvailabilityManage.fields.reason") }}</label>
                    <textarea
                      id="exception-reason"
                      v-model="exceptionForm.reason"
                      rows="3"
                      class="form-control"
                    ></textarea>
                  </div>

                  <div class="availability-form__footer">
                    <button type="submit" class="btn btn-success" :disabled="exceptionSubmitting">
                      <span
                        v-if="exceptionSubmitting"
                        class="spinner-border spinner-border-sm"
                        role="status"
                        aria-hidden="true"
                      ></span>
                      {{ exceptionSubmitLabel }}
                    </button>
                  </div>
                </form>
              </article>

              <article class="availability-panel">
                <header class="availability-panel__header">
                  <div>
                    <span>{{ t("spaceAvailabilityManage.exceptions.eyebrow") }}</span>
                    <h2>{{ t("spaceAvailabilityManage.exceptions.listTitle") }}</h2>
                  </div>
                </header>

                <div v-if="!exceptionRows.length" class="availability-empty">
                  {{ t("spaceAvailabilityManage.exceptions.empty") }}
                </div>

                <div v-else class="availability-row-list">
                  <article
                    v-for="spaceException in exceptionRows"
                    :key="spaceException.id ?? `${spaceException.date}-${spaceException.startTime}-${spaceException.endTime}`"
                    class="availability-row availability-row--exception"
                    :class="`availability-row--${spaceException.exceptionType}`"
                  >
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.date") }}</span>
                      <strong>{{ spaceException.dateLabel }}</strong>
                    </div>
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.exceptionType") }}</span>
                      <strong>{{ spaceException.typeLabel }}</strong>
                    </div>
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.timeRange") }}</span>
                      <strong>{{ spaceException.timeLabel }}</strong>
                    </div>
                    <div>
                      <span class="availability-row__label">{{ t("spaceAvailabilityManage.fields.price") }}</span>
                      <strong>{{ spaceException.priceLabel }}</strong>
                    </div>
                    <p v-if="spaceException.reason" class="availability-row__reason">
                      {{ spaceException.reason }}
                    </p>
                    <div class="availability-row__actions">
                      <button type="button" class="btn btn-outline-light btn-sm" @click="editException(spaceException)">
                        <i class="bi bi-pencil" aria-hidden="true"></i>
                        {{ t("spaceAvailabilityManage.actions.edit") }}
                      </button>
                      <button
                        type="button"
                        class="btn btn-outline-danger btn-sm"
                        :disabled="deletingExceptionId === spaceException.id"
                        @click="deleteException(spaceException)"
                      >
                        <i class="bi bi-trash" aria-hidden="true"></i>
                        {{ deletingExceptionId === spaceException.id ? t("spaceAvailabilityManage.actions.deleting") : t("spaceAvailabilityManage.actions.delete") }}
                      </button>
                    </div>
                  </article>
                </div>
              </article>
            </section>

            <section class="availability-panel availability-panel--preview">
              <header class="availability-panel__header availability-panel__header--preview">
                <div>
                  <span>{{ t("spaceAvailabilityManage.calculated.eyebrow") }}</span>
                  <h2>{{ t("spaceAvailabilityManage.calculated.title") }}</h2>
                </div>
                <div class="availability-preview-controls">
                  <input
                    v-model="selectedDate"
                    type="date"
                    class="form-control"
                    :min="todayDate"
                  />
                  <button type="button" class="btn btn-outline-light btn-sm" @click="refreshCalculatedAvailability">
                    <i class="bi bi-arrow-clockwise" aria-hidden="true"></i>
                    {{ t("spaceAvailabilityManage.actions.refresh") }}
                  </button>
                </div>
              </header>

              <div v-if="availabilityLoading" class="availability-empty">
                <div class="spinner-border spinner-border-sm text-success" role="status"></div>
                {{ t("spaceAvailabilityManage.calculated.loading") }}
              </div>

              <div v-else-if="availabilityError" class="availability-empty availability-empty--error">
                {{ availabilityError }}
              </div>

              <div v-else class="calculated-grid">
                <div class="calculated-list">
                  <h3>{{ t("spaceAvailabilityManage.calculated.availableTitle") }}</h3>
                  <div v-if="!calculatedSlotRows.length" class="availability-empty">
                    {{ t("spaceAvailabilityManage.calculated.emptySlots") }}
                  </div>
                  <article v-for="slot in calculatedSlotRows" :key="slot.id" class="calculated-row">
                    <span>
                      <i class="bi bi-clock" aria-hidden="true"></i>
                      {{ slot.timeLabel }}
                    </span>
                    <strong>{{ slot.priceLabel }}</strong>
                  </article>
                </div>

                <div class="calculated-list">
                  <h3>{{ t("spaceAvailabilityManage.calculated.bookedTitle") }}</h3>
                  <div v-if="!bookedSlotRows.length" class="availability-empty">
                    {{ t("spaceAvailabilityManage.calculated.emptyBooked") }}
                  </div>
                  <article v-for="slot in bookedSlotRows" :key="slot.id" class="calculated-row calculated-row--booked">
                    <span>
                      <i class="bi bi-calendar-check" aria-hidden="true"></i>
                      {{ slot.timeLabel }}
                    </span>
                    <strong>{{ slot.stateLabel }}</strong>
                  </article>
                </div>
              </div>
            </section>
          </template>
        </template>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";
import AppSelect from "@/common/components/AppSelect.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import { buildAddress, formatDate, formatMoney, getInitialDate, normalizeTimeString } from "../spaceDetailUtils";
import {
  DAY_KEYS,
  EXCEPTION_TYPE_KEYS,
  buildExceptionPayload,
  buildSchedulePayload,
  buildSchedulePayloads,
  canManageApprovedAvailability,
  createEmptyExceptionForm,
  createEmptyScheduleForm,
  hasAvailabilityManagementPermission,
  mapExceptionToForm,
  mapScheduleToForm,
  sortExceptions,
  sortSchedules
} from "../spaceAvailabilityManageUtils";

const route = useRoute();
const store = getStore();
const { locale, t } = useI18n();

const todayDate = getInitialDate();

const loading = ref(true);
const fatalError = ref("");
const pageNotice = ref(null);
const space = ref(null);
const schedules = ref([]);
const exceptions = ref([]);
const calculatedAvailability = ref({ slots: [], bookedSlots: [] });
const selectedDate = ref(todayDate);

const scheduleForm = ref(createEmptyScheduleForm());
const exceptionForm = ref(createEmptyExceptionForm(todayDate));
const editingScheduleId = ref(null);
const editingExceptionId = ref(null);
const scheduleError = ref("");
const exceptionError = ref("");
const availabilityError = ref("");
const scheduleSubmitting = ref(false);
const exceptionSubmitting = ref(false);
const availabilityLoading = ref(false);
const deletingScheduleId = ref(null);
const deletingExceptionId = ref(null);

let activeAvailabilityRequest = 0;

const currentUser = computed(() => store.state.user);
const isAdmin = computed(() => currentUser.value.platformRole === "ADMIN");
const detailRoute = computed(() => ({
  name: "MusicalSpaceDetail",
  params: { id: space.value?.id || route.params.id }
}));
const backRoute = computed(() =>
  isAdmin.value
    ? { name: "AdminDashboard", query: { section: "spaces" } }
    : { name: "MusicalSpaceList", query: { view: "mine" } }
);

const canManageSpace = computed(() => hasAvailabilityManagementPermission(space.value, currentUser.value));
const canManageCurrentSpace = computed(() => canManageApprovedAvailability(space.value, currentUser.value));
const isCustomExceptionForm = computed(() => exceptionForm.value.exceptionType === "CUSTOM_AVAILABILITY");

const lockTitle = computed(() => {
  if (!canManageSpace.value) {
    return t("spaceAvailabilityManage.states.noPermissionTitle");
  }

  if (space.value?.active === false) {
    return t("spaceAvailabilityManage.states.inactiveTitle");
  }

  if (space.value?.approvalStatus !== "APPROVED") {
    return t("spaceAvailabilityManage.states.notApprovedTitle");
  }

  return "";
});

const lockMessage = computed(() => {
  if (!canManageSpace.value) {
    return t("spaceAvailabilityManage.states.noPermission");
  }

  if (space.value?.active === false) {
    return t("spaceAvailabilityManage.states.inactive");
  }

  if (space.value?.approvalStatus !== "APPROVED") {
    return t("spaceAvailabilityManage.states.notApproved");
  }

  return "";
});

const spaceLocationLabel = computed(() =>
  buildAddress(space.value?.location) || [space.value?.city, space.value?.province].filter(Boolean).join(", ") || "--"
);

const dayOptions = computed(() =>
  DAY_KEYS.map((day) => ({
    value: day,
    label: formatDayName(day)
  }))
);

const exceptionTypeOptions = computed(() =>
  EXCEPTION_TYPE_KEYS.map((type) => ({
    value: type,
    label: t(`spaceAvailabilityManage.exceptionTypes.${type}`)
  }))
);

const scheduleRows = computed(() =>
  sortSchedules(schedules.value).map((schedule) => ({
    ...schedule,
    dayLabel: formatDayName(schedule.dayOfWeek),
    timeLabel: formatTimeRange(schedule),
    priceLabel: formatPrice(schedule.price)
  }))
);

const exceptionRows = computed(() =>
  sortExceptions(exceptions.value).map((spaceException) => ({
    ...spaceException,
    dateLabel: formatDate(spaceException.date, locale.value),
    timeLabel: formatTimeRange(spaceException),
    typeLabel: t(`spaceAvailabilityManage.exceptionTypes.${spaceException.exceptionType || "BLOCKED"}`),
    priceLabel: spaceException.exceptionType === "CUSTOM_AVAILABILITY"
      ? formatPrice(spaceException.price)
      : t("spaceAvailabilityManage.exceptions.noPrice")
  }))
);

const calculatedSlotRows = computed(() =>
  (calculatedAvailability.value?.slots ?? []).map((slot) => {
    const startTime = normalizeTimeString(slot.startTime);
    const endTime = normalizeTimeString(slot.endTime);

    return {
      id: `${startTime}-${endTime}`,
      timeLabel: `${startTime} - ${endTime}`,
      priceLabel: formatPrice(slot.price)
    };
  })
);

const bookedSlotRows = computed(() =>
  (calculatedAvailability.value?.bookedSlots ?? []).map((slot) => {
    const startTime = normalizeTimeString(slot.startTime);
    const endTime = normalizeTimeString(slot.endTime);
    const state = slot.state || "PENDING";

    return {
      id: `${startTime}-${endTime}-${state}`,
      timeLabel: `${startTime} - ${endTime}`,
      stateLabel: t(`reservations.statuses.${state}`)
    };
  })
);

const blockedExceptionsCount = computed(() =>
  exceptions.value.filter((spaceException) => spaceException.exceptionType === "BLOCKED").length
);
const customExceptionsCount = computed(() =>
  exceptions.value.filter((spaceException) => spaceException.exceptionType === "CUSTOM_AVAILABILITY").length
);

const scheduleSubmitLabel = computed(() =>
  scheduleSubmitting.value
    ? t("spaceAvailabilityManage.actions.saving")
    : editingScheduleId.value
      ? t("spaceAvailabilityManage.actions.saveSchedule")
      : t("spaceAvailabilityManage.actions.createSchedule")
);

const exceptionSubmitLabel = computed(() =>
  exceptionSubmitting.value
    ? t("spaceAvailabilityManage.actions.saving")
    : editingExceptionId.value
      ? t("spaceAvailabilityManage.actions.saveException")
      : t("spaceAvailabilityManage.actions.createException")
);

onMounted(loadPage);

watch(
  () => route.params.id,
  () => {
    loadPage();
  }
);

watch(selectedDate, () => {
  if (space.value && canManageCurrentSpace.value) {
    refreshCalculatedAvailability();
  }
});

async function loadPage() {
  loading.value = true;
  fatalError.value = "";
  pageNotice.value = null;
  scheduleError.value = "";
  exceptionError.value = "";
  availabilityError.value = "";
  schedules.value = [];
  exceptions.value = [];
  calculatedAvailability.value = { slots: [], bookedSlots: [] };

  try {
    space.value = await MusicalSpaceRepository.getById(route.params.id);

    if (!canManageCurrentSpace.value) {
      return;
    }

    await refreshManagedData();
  } catch (error) {
    fatalError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.states.error");
  } finally {
    loading.value = false;
  }
}

async function refreshManagedData() {
  if (!space.value) {
    return;
  }

  await Promise.all([
    loadSchedules(),
    loadExceptions(),
    refreshCalculatedAvailability()
  ]);
}

async function loadSchedules() {
  schedules.value = sortSchedules(await MusicalSpaceRepository.getSchedules(space.value.id));
}

async function loadExceptions() {
  exceptions.value = sortExceptions(await MusicalSpaceRepository.getExceptions(space.value.id));
}

async function refreshCalculatedAvailability() {
  if (!space.value || !selectedDate.value) {
    return;
  }

  const requestId = ++activeAvailabilityRequest;
  availabilityLoading.value = true;
  availabilityError.value = "";

  try {
    const response = await MusicalSpaceRepository.getAvailability(space.value.id, selectedDate.value);

    if (requestId === activeAvailabilityRequest) {
      calculatedAvailability.value = {
        slots: response?.slots ?? [],
        bookedSlots: response?.bookedSlots ?? []
      };
    }
  } catch (error) {
    if (requestId === activeAvailabilityRequest) {
      calculatedAvailability.value = { slots: [], bookedSlots: [] };
      availabilityError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.calculated.error");
    }
  } finally {
    if (requestId === activeAvailabilityRequest) {
      availabilityLoading.value = false;
    }
  }
}

async function submitScheduleForm() {
  if (!canManageCurrentSpace.value) {
    return;
  }

  const result = editingScheduleId.value
    ? buildSchedulePayload(scheduleForm.value)
    : buildSchedulePayloads(scheduleForm.value);

  if (!(result.payload || result.payloads?.length)) {
    scheduleError.value = t(`spaceAvailabilityManage.validation.${result.errorKey}`);
    return;
  }

  scheduleSubmitting.value = true;
  scheduleError.value = "";

  try {
    if (editingScheduleId.value) {
      await MusicalSpaceRepository.updateSchedule(editingScheduleId.value, result.payload);
      setNotice("success", t("spaceAvailabilityManage.notices.scheduleUpdated"));
    } else {
      for (const payload of result.payloads) {
        await MusicalSpaceRepository.createSchedule(space.value.id, payload);
      }

      setNotice(
        "success",
        result.payloads.length > 1
          ? t("spaceAvailabilityManage.notices.schedulesCreated", { count: result.payloads.length })
          : t("spaceAvailabilityManage.notices.scheduleCreated")
      );
    }

    resetScheduleForm();
    await Promise.all([loadSchedules(), refreshCalculatedAvailability()]);
  } catch (error) {
    scheduleError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.notices.scheduleError");
    await Promise.allSettled([loadSchedules(), refreshCalculatedAvailability()]);
  } finally {
    scheduleSubmitting.value = false;
  }
}

function editSchedule(schedule) {
  editingScheduleId.value = schedule.id;
  scheduleForm.value = mapScheduleToForm(schedule);
  scheduleError.value = "";
}

async function deleteSchedule(schedule) {
  if (!window.confirm(t("spaceAvailabilityManage.confirm.deleteSchedule"))) {
    return;
  }

  deletingScheduleId.value = schedule.id;
  scheduleError.value = "";

  try {
    await MusicalSpaceRepository.deleteSchedule(schedule.id);

    if (editingScheduleId.value === schedule.id) {
      resetScheduleForm();
    }

    setNotice("success", t("spaceAvailabilityManage.notices.scheduleDeleted"));
    await Promise.all([loadSchedules(), refreshCalculatedAvailability()]);
  } catch (error) {
    scheduleError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.notices.scheduleDeleteError");
  } finally {
    deletingScheduleId.value = null;
  }
}

async function submitExceptionForm() {
  if (!canManageCurrentSpace.value) {
    return;
  }

  const result = buildExceptionPayload(exceptionForm.value);

  if (!result.payload) {
    exceptionError.value = t(`spaceAvailabilityManage.validation.${result.errorKey}`);
    return;
  }

  exceptionSubmitting.value = true;
  exceptionError.value = "";

  try {
    if (editingExceptionId.value) {
      await MusicalSpaceRepository.updateException(editingExceptionId.value, result.payload);
      setNotice("success", t("spaceAvailabilityManage.notices.exceptionUpdated"));
    } else {
      await MusicalSpaceRepository.createException(space.value.id, result.payload);
      setNotice("success", t("spaceAvailabilityManage.notices.exceptionCreated"));
    }

    resetExceptionForm();
    await Promise.all([loadExceptions(), refreshCalculatedAvailability()]);
  } catch (error) {
    exceptionError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.notices.exceptionError");
  } finally {
    exceptionSubmitting.value = false;
  }
}

function editException(spaceException) {
  editingExceptionId.value = spaceException.id;
  exceptionForm.value = mapExceptionToForm(spaceException);
  exceptionError.value = "";
}

async function deleteException(spaceException) {
  if (!window.confirm(t("spaceAvailabilityManage.confirm.deleteException"))) {
    return;
  }

  deletingExceptionId.value = spaceException.id;
  exceptionError.value = "";

  try {
    await MusicalSpaceRepository.deleteException(spaceException.id);

    if (editingExceptionId.value === spaceException.id) {
      resetExceptionForm();
    }

    setNotice("success", t("spaceAvailabilityManage.notices.exceptionDeleted"));
    await Promise.all([loadExceptions(), refreshCalculatedAvailability()]);
  } catch (error) {
    exceptionError.value = getApiErrorMessage(error, t, "spaceAvailabilityManage.notices.exceptionDeleteError");
  } finally {
    deletingExceptionId.value = null;
  }
}

function resetScheduleForm() {
  editingScheduleId.value = null;
  scheduleForm.value = createEmptyScheduleForm();
  scheduleError.value = "";
}

function resetExceptionForm() {
  editingExceptionId.value = null;
  exceptionForm.value = createEmptyExceptionForm(selectedDate.value || todayDate);
  exceptionError.value = "";
}

function setNotice(type, message) {
  pageNotice.value = { type, message };
}

function formatDayName(dayOfWeek) {
  const dayIndex = DAY_KEYS.indexOf(dayOfWeek);

  if (dayIndex < 0) {
    return dayOfWeek || "--";
  }

  const monday = new Date(Date.UTC(2024, 0, 1 + dayIndex));
  return new Intl.DateTimeFormat(locale.value, { weekday: "long" }).format(monday);
}

function formatTimeRange(item) {
  const startTime = normalizeTimeString(item.startTime);
  const endTime = normalizeTimeString(item.endTime);
  return startTime && endTime ? `${startTime} - ${endTime}` : "--";
}

function formatPrice(value) {
  return formatMoney(value, locale.value) || t("common.labels.onRequest");
}
</script>

<style scoped>
.availability-manage-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.13), transparent 28%),
    radial-gradient(circle at top right, rgba(80, 145, 255, 0.08), transparent 26%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 20%, #050505 100%);
  color: #ffffff;
}

.availability-manage-page .container {
  max-width: 1180px;
}

.availability-manage-shell {
  padding-bottom: 3rem;
}

.availability-back {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  margin-bottom: 1rem;
  color: #d8d8d8;
  font-weight: 700;
  text-decoration: none;
}

.availability-back:hover {
  color: #ffffff;
}

.availability-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.45rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.16), transparent 34%),
    linear-gradient(180deg, rgba(17, 17, 17, 0.98) 0%, rgba(24, 24, 24, 0.98) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.24);
}

.availability-hero__eyebrow,
.availability-panel__header span {
  color: #1db954;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.availability-hero h1 {
  margin: 0.45rem 0 0;
  color: #ffffff;
  font-size: 2.4rem;
  line-height: 1.1;
  letter-spacing: 0;
}

.availability-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  margin-top: 0.9rem;
}

.availability-hero__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 36px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  font-size: 0.92rem;
  font-weight: 600;
}

.availability-hero__meta i {
  color: #1db954;
}

.availability-hero__link {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 42px;
  border-radius: 14px;
  white-space: nowrap;
}

.availability-stats,
.availability-grid,
.calculated-grid {
  display: grid;
  gap: 1rem;
}

.availability-stats {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 1rem;
}

.availability-stat,
.availability-panel,
.page-notice,
.state-card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(24, 24, 24, 0.96) 100%);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.availability-stat {
  display: grid;
  gap: 0.25rem;
  padding: 1rem;
  border-radius: 20px;
}

.availability-stat span {
  color: #a8a8a8;
  font-size: 0.82rem;
  font-weight: 700;
}

.availability-stat strong {
  color: #ffffff;
  font-size: 1.8rem;
  line-height: 1;
}

.availability-grid {
  grid-template-columns: minmax(300px, 0.9fr) minmax(0, 1.1fr);
  margin-top: 1rem;
  align-items: start;
}

.availability-panel {
  min-width: 0;
  padding: 1.2rem;
  border-radius: 24px;
}

.availability-panel--preview {
  margin-top: 1rem;
}

.availability-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.availability-panel__header h2 {
  margin: 0.25rem 0 0;
  color: #ffffff;
  font-size: 1.25rem;
}

.availability-panel__header--preview {
  align-items: center;
}

.availability-preview-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  justify-content: flex-end;
}

.availability-preview-controls .form-control {
  width: 180px;
}

.availability-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
}

.availability-field {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 0.38rem;
}

.availability-field--wide {
  grid-column: 1 / -1;
}

.availability-field label {
  color: #c0c0c0;
  font-size: 0.82rem;
  font-weight: 600;
}

.availability-field .form-control,
.availability-preview-controls .form-control {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.availability-field textarea.form-control {
  min-height: 96px;
}

.availability-field .form-control:focus,
.availability-preview-controls .form-control:focus {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.schedule-day-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(112px, 1fr));
  gap: 0.55rem;
}

.schedule-day-option {
  position: relative;
  isolation: isolate;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 0.65rem 0.75rem;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  color: #f5f5f5;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 700;
  line-height: 1.15;
  text-align: center;
  user-select: none;
  transition: border-color 0.18s ease, background-color 0.18s ease, color 0.18s ease;
}

.schedule-day-option:hover,
.schedule-day-option:focus-within {
  border-color: rgba(29, 185, 84, 0.42);
  background: rgba(29, 185, 84, 0.12);
}

.schedule-day-option.is-selected {
  border-color: rgba(29, 185, 84, 0.78);
  background: #1db954;
  color: #061208;
}

.schedule-day-option__input {
  position: absolute;
  inset: 0;
  margin: 0;
  cursor: pointer;
  opacity: 0;
}

.schedule-day-option span {
  position: relative;
  z-index: 1;
  min-width: 0;
  overflow-wrap: anywhere;
}

.availability-form__footer {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
}

.availability-form__footer .btn,
.availability-row__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  border-radius: 14px;
}

.availability-row-list {
  display: grid;
  gap: 0.75rem;
}

.availability-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
  padding: 0.95rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.availability-row--exception {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.availability-row--BLOCKED {
  border-color: rgba(255, 193, 7, 0.22);
}

.availability-row--CUSTOM_AVAILABILITY {
  border-color: rgba(29, 185, 84, 0.22);
}

.availability-row div {
  min-width: 0;
}

.availability-row__label {
  display: block;
  margin-bottom: 0.2rem;
  color: #9e9e9e;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.availability-row strong {
  color: #ffffff;
  word-break: break-word;
}

.availability-row__reason {
  grid-column: 1 / -1;
  margin: 0;
  color: #cfcfcf;
  line-height: 1.65;
}

.availability-row__actions {
  grid-column: 1 / -1;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.65rem;
}

.availability-empty,
.form-error,
.page-notice,
.state-card {
  padding: 1rem;
  border-radius: 18px;
}

.availability-empty {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  color: #bdbdbd;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.availability-empty--error,
.form-error,
.page-notice--error,
.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.form-error {
  margin-bottom: 0.85rem;
}

.page-notice {
  display: grid;
  gap: 0.25rem;
  margin-top: 1rem;
  color: #ffffff;
}

.page-notice--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice--warning {
  color: #fff2c5;
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  margin-top: 1rem;
  text-align: center;
}

.calculated-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.calculated-list {
  display: grid;
  align-content: start;
  gap: 0.65rem;
}

.calculated-list h3 {
  margin: 0;
  color: #ffffff;
  font-size: 1rem;
}

.calculated-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.85rem;
  border-radius: 16px;
  border: 1px solid rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.calculated-row--booked {
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.calculated-row span,
.calculated-row strong {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-width: 0;
}

.calculated-row i {
  color: #1db954;
}

.calculated-row strong {
  color: #ffffff;
}

@media (max-width: 991.98px) {
  .availability-hero,
  .availability-panel__header--preview {
    flex-direction: column;
  }

  .availability-grid,
  .availability-stats,
  .calculated-grid {
    grid-template-columns: 1fr;
  }

  .availability-preview-controls {
    justify-content: flex-start;
    width: 100%;
  }
}

@media (max-width: 767.98px) {
  .availability-manage-page .container {
    padding-right: 1rem;
    padding-left: 1rem;
  }

  .availability-hero h1 {
    font-size: 2rem;
  }

  .availability-form,
  .availability-row,
  .availability-row--exception {
    grid-template-columns: 1fr;
  }

  .schedule-day-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .availability-preview-controls,
  .availability-preview-controls .form-control,
  .availability-form__footer .btn,
  .availability-row__actions .btn,
  .availability-hero__link {
    width: 100%;
  }
}
</style>
