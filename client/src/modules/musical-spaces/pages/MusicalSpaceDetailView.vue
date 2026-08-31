<template>
  <div class="space-detail-page">
    <section class="space-detail-shell">
      <div class="container py-5">
        <div v-if="initialLoading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("spaceDetail.states.loading") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error">
          <strong>{{ t("spaceDetail.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
        </div>

        <template v-else-if="space">
          <SpaceDetailHeader
            :eyebrow="t('spaceDetail.header.eyebrow')"
            :title="space.name"
            :items="headerMetaItems"
          />

          <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
            {{ pageNotice.message }}
          </div>

          <section v-if="showOwnerActions" class="space-detail-owner-actions">
            <div>
              <span>{{ t("spaceDetail.ownerActions.eyebrow") }}</span>
              <strong>{{ t("spaceDetail.ownerActions.title") }}</strong>
            </div>
            <div class="space-detail-owner-actions__buttons">
              <RouterLink v-if="canManageAvailability" class="btn btn-success" :to="availabilityManageRoute">
                <i class="bi bi-calendar-week" aria-hidden="true"></i>
                {{ t("spaceDetail.ownerActions.manageAvailability") }}
              </RouterLink>
              <button
                v-if="canManageSpaceEquipment"
                type="button"
                class="btn btn-outline-light"
                @click="openEquipmentModal"
              >
                <i class="bi bi-speaker" aria-hidden="true"></i>
                {{ t("spaceDetail.ownerActions.manageEquipment") }}
              </button>
            </div>
          </section>

          <section class="space-detail-hero">
            <div class="space-detail-hero__media">
              <SpaceHeroMedia
                :image="space.mainImage"
                :title="space.name"
                :is-favorite="isFavorite"
                :favorite-busy="favoriteBusy"
                :favorite-label="favoriteLabel"
                :placeholder-label="t('spaceDetail.media.placeholder')"
                :show-favorite="canUseUserActions"
                @toggle-favorite="handleToggleFavorite"
              />
            </div>

            <SpaceMapCard
              :eyebrow="t('spaceDetail.map.eyebrow')"
              :title="t('spaceDetail.map.title')"
              :subtitle="mapSubtitle"
              :empty-label="t('spaceDetail.map.empty')"
              :marker-title="space.name"
              :latitude="space.location?.latitude ?? null"
              :longitude="space.location?.longitude ?? null"
            />
          </section>

          <section class="space-detail-intro">
            <section class="detail-section detail-section--intro">
              <div class="detail-section__header">
                <div>
                  <div class="detail-section__eyebrow">{{ t("spaceDetail.quickServices.eyebrow") }}</div>
                  <h2>{{ t("spaceDetail.quickServices.title") }}</h2>
                </div>
              </div>

              <div class="service-chips">
                <span v-for="service in quickServices" :key="service.id" class="service-chip">
                  <i :class="service.icon"></i>
                  {{ t(`spaceDetail.quickServices.items.${service.id}`) }}
                </span>
              </div>
            </section>

            <section class="detail-section detail-section--intro">
              <div class="detail-section__header">
                <div>
                  <div class="detail-section__eyebrow">{{ t("spaceDetail.description.eyebrow") }}</div>
                  <h2>{{ t("spaceDetail.description.title") }}</h2>
                </div>
              </div>

              <p class="detail-copy">
                {{ space.description || t("spaceDetail.description.empty") }}
              </p>
            </section>
          </section>

          <div class="space-detail-layout">
            <main class="space-detail-main">
              <SpaceInfoGrid
                :eyebrow="t('spaceDetail.info.eyebrow')"
                :title="t('spaceDetail.info.title')"
                :items="infoItems"
              />

              <SpaceEquipmentGrid
                :eyebrow="t('spaceDetail.equipment.eyebrow')"
                :title="t('spaceDetail.equipment.title')"
                :count-label="equipmentCountLabel"
                :loading="equipmentLoading"
                :loading-label="t('spaceDetail.states.loadingSection')"
                :error-message="equipmentError"
                :empty-label="t('spaceDetail.equipment.empty')"
                :equipment="equipmentCards"
              />

              <SpaceAvailabilityPanel
                :eyebrow="t('spaceDetail.availability.eyebrow')"
                :title="t('spaceDetail.availability.title')"
                :action-label="t('spaceDetail.availability.fullSchedule')"
                :date-label="t('spaceDetail.availability.date')"
                :quick-dates-label="t('spaceDetail.availability.quickDates')"
                :loading-label="t('spaceDetail.availability.loading')"
                :empty-label="t('spaceDetail.availability.empty')"
                :error-message="availabilityError"
                :summary-label="availabilitySummaryLabel"
                :summary-detail-label="availabilitySummaryDetailLabel"
                :selected-slot-label="t('spaceDetail.availability.selectedSlot')"
                :choose-slot-label="t('spaceDetail.availability.chooseSlot')"
                :booked-title-label="t('spaceDetail.availability.bookedTitle')"
                :booked-detail-label="t('spaceDetail.availability.bookedDetail')"
                :today-label="t('spaceDetail.availability.today')"
                :tomorrow-label="t('spaceDetail.availability.tomorrow')"
                :selected-date="selectedDate"
                :min-date="todayDate"
                :selected-slot-id="selectedSlot?.id ?? ''"
                :locale="locale"
                :loading="availabilityLoading"
                :slots="displayAvailabilitySlots"
                :booked-slots="displayBookedSlots"
                @update:selectedDate="handleSelectedDateChange"
                @select-slot="handleSlotSelection"
                @view-schedules="openScheduleModal"
              />

              <SpaceReviewsSection
                :eyebrow="t('spaceDetail.reviews.eyebrow')"
                :title="t('spaceDetail.reviews.title')"
                :rating-label="reviewsRatingLabel"
                :count-label="reviewsCountLabel"
                :loading="reviewsLoading"
                :loading-label="t('spaceDetail.states.loadingSection')"
                :empty-label="t('spaceDetail.reviews.empty')"
                :fallback-comment="t('spaceDetail.reviews.noComment')"
                :error-message="reviewsError"
                :sound-label="t('spaceDetail.reviews.metrics.sound')"
                :equipment-label="t('spaceDetail.reviews.metrics.equipment')"
                :cleanliness-label="t('spaceDetail.reviews.metrics.cleanliness')"
                :location-label="t('spaceDetail.reviews.metrics.location')"
                :reviews="reviewCards"
              />
            </main>

            <aside v-if="canUseUserActions" class="space-detail-side">
              <SpaceBookingCard
                :eyebrow="t('spaceDetail.booking.eyebrow')"
                :title="t('spaceDetail.booking.title')"
                :price-label="bookingPriceLabel"
                :rating-label="bookingRatingDisplayLabel"
                :date-label="t('spaceDetail.booking.date')"
                :start-time-label="t('spaceDetail.booking.startTime')"
                :duration-label="t('spaceDetail.booking.duration')"
                :attendees-label="t('spaceDetail.booking.attendees')"
                :subtotal-label="t('spaceDetail.booking.subtotal')"
                :total-label="t('spaceDetail.booking.total')"
                :submit-label="bookingSubmitLabel"
                :loading-label="t('spaceDetail.booking.submitting')"
                :placeholder-label="t('spaceDetail.booking.selectSlot')"
                :session-date="selectedDate"
                :start-time="selectedStartTime"
                :start-time-options="startTimeOptions"
                :duration-minutes="selectedDurationMinutes"
                :attendees-count="attendeesCount"
                :max-attendees="space.capacity"
                :duration-options="durationOptions"
                :subtotal-value="bookingSubtotalLabel"
                :total-value="bookingTotalLabel"
                :error-message="bookingError"
                :success-message="bookingSuccess"
                :loading="bookingLoading"
                @update:sessionDate="handleSelectedDateChange"
                @update:startTime="handleStartTimeChange"
                @update:durationMinutes="selectedDurationMinutes = $event"
                @update:attendeesCount="attendeesCount = $event"
                @reserve="handleReserve"
              />
            </aside>
          </div>
        </template>
      </div>
    </section>

    <SpaceEquipmentManageModal
      :open="equipmentModalOpen"
      :space-id="space?.id"
      :space-name="space?.name || ''"
      :items="equipment"
      @close="closeEquipmentModal"
      @updated="handleEquipmentUpdated"
    />

    <div v-if="scheduleModalOpen" class="schedule-modal-backdrop" @click.self="closeScheduleModal">
      <section class="schedule-modal" role="dialog" aria-modal="true">
        <header class="schedule-modal__header">
          <div>
            <span class="schedule-modal__eyebrow">{{ t("spaceDetail.availability.eyebrow") }}</span>
            <h2>{{ t("spaceDetail.availability.fullScheduleTitle") }}</h2>
          </div>
          <button
            type="button"
            class="schedule-modal__close"
            :aria-label="t('spaceDetail.availability.fullScheduleClose')"
            @click="closeScheduleModal"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </header>

        <div v-if="scheduleModalLoading" class="schedule-modal__state">
          <div class="spinner-border text-success" role="status"></div>
          <span>{{ t("spaceDetail.availability.fullScheduleLoading") }}</span>
        </div>

        <div v-else-if="scheduleModalError" class="schedule-modal__state schedule-modal__state--error">
          {{ scheduleModalError }}
        </div>

        <div v-else-if="!sortedSchedules.length" class="schedule-modal__state">
          {{ t("spaceDetail.availability.fullScheduleEmpty") }}
        </div>

        <div v-else class="schedule-list">
          <article v-for="schedule in sortedSchedules" :key="schedule.id ?? `${schedule.dayOfWeek}-${schedule.startTime}-${schedule.endTime}`" class="schedule-row">
            <div>
              <span>{{ t("spaceDetail.availability.scheduleDay") }}</span>
              <strong>{{ formatScheduleDay(schedule.dayOfWeek) }}</strong>
            </div>
            <div>
              <span>{{ t("spaceDetail.availability.scheduleTime") }}</span>
              <strong>{{ formatScheduleTimeRange(schedule) }}</strong>
            </div>
            <div>
              <span>{{ t("spaceDetail.availability.schedulePrice") }}</span>
              <strong>{{ formatSchedulePrice(schedule) }}</strong>
            </div>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { getApiErrorMessage } from "@/common/apiErrors";
import { getStore } from "@/common/store";
import FavoriteSpaceRepository from "@/repositories/FavoriteSpaceRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";
import ReservationSessionRepository from "@/repositories/ReservationSessionRepository";
import SpaceReviewRepository from "@/repositories/SpaceReviewRepository";
import SpaceAvailabilityPanel from "../components/SpaceAvailabilityPanel.vue";
import SpaceBookingCard from "../components/SpaceBookingCard.vue";
import SpaceDetailHeader from "../components/SpaceDetailHeader.vue";
import SpaceEquipmentGrid from "../components/SpaceEquipmentGrid.vue";
import SpaceEquipmentManageModal from "../components/SpaceEquipmentManageModal.vue";
import SpaceHeroMedia from "../components/SpaceHeroMedia.vue";
import SpaceInfoGrid from "../components/SpaceInfoGrid.vue";
import SpaceMapCard from "../components/SpaceMapCard.vue";
import SpaceReviewsSection from "../components/SpaceReviewsSection.vue";
import {
  addMinutesToTime,
  buildAddress,
  estimateReservationPrice,
  formatDateTime,
  formatDurationLabel,
  formatMoney,
  getDurationOptions,
  getInitialDate,
  getQuickServices,
  getScheduleSummary,
  getSlotDurationMinutes,
  getStartTimeOptions,
  getTomorrowDate,
  normalizeTimeString,
  timeToMinutes
} from "../spaceDetailUtils";

const route = useRoute();
const router = useRouter();
const store = getStore();
const { locale, t } = useI18n();

const initialLoading = ref(true);
const fatalError = ref("");

const space = ref(null);
const equipment = ref([]);
const ratingSummary = ref(null);
const reviews = ref([]);
const schedules = ref([]);
const availabilitySlots = ref([]);
const bookedAvailabilitySlots = ref([]);
const selectedSlot = ref(null);
const selectedStartTime = ref("");

const selectedDate = ref(getInitialDate());
const selectedDurationMinutes = ref(0);
const attendeesCount = ref(1);

const equipmentLoading = ref(false);
const reviewsLoading = ref(false);
const availabilityLoading = ref(false);
const bookingLoading = ref(false);
const favoriteBusy = ref(false);
const equipmentModalOpen = ref(false);
const scheduleModalOpen = ref(false);
const scheduleModalLoading = ref(false);

const equipmentError = ref("");
const reviewsError = ref("");
const availabilityError = ref("");
const bookingError = ref("");
const bookingSuccess = ref("");
const scheduleModalError = ref("");

const isFavorite = ref(false);
const pageNotice = ref(null);

let ignoreNextDateWatch = false;
let activeAvailabilityRequest = 0;
const bookingRatingSeparator = ` ${String.fromCharCode(183)} `;
const startTimeStepMinutes = 30;

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => store.state.user.platformRole === "ADMIN");
const currentUserId = computed(() => Number(store.state.user.id || 0));
const canUseUserActions = computed(() => !isAdmin.value);
const todayDate = computed(() => getInitialDate());
const quickServices = computed(() => (space.value ? getQuickServices(space.value) : []));
const scheduleSummary = computed(() => getScheduleSummary(schedules.value));
const sortedSchedules = computed(() =>
  [...schedules.value].sort((left, right) => {
    const dayDiff = getDayOrder(left.dayOfWeek) - getDayOrder(right.dayOfWeek);
    return dayDiff || `${left.startTime || ""}`.localeCompare(`${right.startTime || ""}`);
  })
);

const favoriteLabel = computed(() =>
  isFavorite.value ? t("spaceDetail.media.removeFavorite") : t("spaceDetail.media.addFavorite")
);

const headerMetaItems = computed(() => {
  if (!space.value) return [];

  return [
    {
      icon: "bi bi-geo-alt",
      label: [space.value.location?.city, space.value.location?.province].filter(Boolean).join(", ")
    },
    {
      icon: "bi bi-building",
      label: t(`spaceDetail.spaceTypeLabels.${space.value.spaceType || "OTHER"}`)
    },
    {
      icon: "bi bi-people",
      label: t("spaceDetail.header.capacity", { value: space.value.capacity })
    },
    {
      icon: "bi bi-soundwave",
      label: space.value.soundproofed
        ? t("spaceDetail.header.soundproofed")
        : t("spaceDetail.header.notSoundproofed")
    },
    {
      icon: "bi bi-bounding-box",
      label: t("spaceDetail.header.squareMeters", { value: space.value.squareMeters ?? "--" })
    },
    {
      icon: "bi bi-star-fill",
      label: bookingRatingDisplayLabel.value
    }
  ];
});

const infoItems = computed(() => {
  if (!space.value) return [];

  return [
    {
      icon: "bi bi-building",
      label: t("spaceDetail.info.items.spaceType"),
      value: t(`spaceDetail.spaceTypeLabels.${space.value.spaceType || "OTHER"}`)
    },
    {
      icon: "bi bi-people",
      label: t("spaceDetail.info.items.capacity"),
      value: t("spaceDetail.info.capacityValue", { value: space.value.capacity })
    },
    {
      icon: "bi bi-rulers",
      label: t("spaceDetail.info.items.squareMeters"),
      value: t("spaceDetail.info.squareMetersValue", { value: space.value.squareMeters ?? "--" })
    },
    {
      icon: "bi bi-clock",
      label: t("spaceDetail.info.items.opening"),
      value: scheduleSummary.value.openTime || t("spaceDetail.info.notAvailable")
    },
    {
      icon: "bi bi-clock-history",
      label: t("spaceDetail.info.items.closing"),
      value: scheduleSummary.value.closeTime || t("spaceDetail.info.notAvailable")
    },
    {
      icon: "bi bi-signpost",
      label: t("spaceDetail.info.items.address"),
      value: buildAddress(space.value.location) || t("spaceDetail.info.notAvailable")
    }
  ];
});

const equipmentCards = computed(() =>
  equipment.value.map((item) => ({
    id: item.id,
    icon: getEquipmentIcon(item.equipment?.category),
    name: item.equipment?.name || t("spaceDetail.equipment.unknown"),
    quantityLabel: t("spaceDetail.equipment.quantity", { value: item.quantity }),
    stateLabel: t(`spaceDetail.equipment.states.${item.state || "AVAILABLE"}`),
    observations: item.observations
  }))
);

const equipmentCountLabel = computed(() =>
  t("spaceDetail.equipment.count", { value: equipment.value.length })
);

const reviewCards = computed(() =>
  reviews.value.map((review) => ({
    ...review,
    author: [review.user?.name, review.user?.firstSurname].filter(Boolean).join(" ") || t("spaceDetail.reviews.anonymous"),
    createdAtLabel: formatDateTime(review.createdAt, locale.value)
  }))
);

const reviewsRatingLabel = computed(() => {
  const rating = Number(ratingSummary.value?.averageOverallRating);
  return Number.isFinite(rating) && rating > 0 ? rating.toFixed(1) : "--";
});

const reviewsCountLabel = computed(() =>
  t("spaceDetail.reviews.count", { value: Number(ratingSummary.value?.reviewsCount || 0) })
);

const mapSubtitle = computed(() => buildAddress(space.value?.location) || t("spaceDetail.map.noAddress"));

const bookingRatingLabel = computed(() => {
  const count = Number(ratingSummary.value?.reviewsCount || 0);
  const average = Number(ratingSummary.value?.averageOverallRating);

  if (Number.isFinite(average) && average > 0) {
    return `${average.toFixed(1)}${bookingRatingSeparator}${t("spaceDetail.reviews.count", { value: count })}`;
  }

  return t("spaceDetail.booking.noReviews");
});

const bookingRatingDisplayLabel = computed(() =>
  bookingRatingLabel.value.replace(/\s+.{1,2}\s+/, bookingRatingSeparator)
);

const bookingPriceLabel = computed(() => {
  const minRate = scheduleSummary.value.minHourlyRate;
  const maxRate = scheduleSummary.value.maxHourlyRate;

  if (minRate == null && maxRate == null) {
    return t("common.labels.onRequest");
  }

  if (minRate != null && maxRate != null && minRate !== maxRate) {
    return t("spaceDetail.booking.priceRange", {
      from: formatMoney(minRate, locale.value),
      to: formatMoney(maxRate, locale.value)
    });
  }

  return t("spaceDetail.booking.priceSingle", {
    value: formatMoney(minRate ?? maxRate, locale.value)
  });
});

const startTimeOptions = computed(() =>
  getStartTimeOptions(selectedSlot.value, startTimeStepMinutes).map((value) => ({
    value,
    label: value
  }))
);

const selectedBookingSlot = computed(() => {
  if (!selectedSlot.value || !selectedStartTime.value) {
    return null;
  }

  return {
    startTime: selectedStartTime.value,
    endTime: selectedSlot.value.endTime
  };
});

const durationOptions = computed(() =>
  getDurationOptions(selectedBookingSlot.value).map((value) => ({
    value,
    label: formatDurationLabel(value, t)
  }))
);

const computedEndTime = computed(() => {
  if (!selectedBookingSlot.value || selectedDurationMinutes.value <= 0) {
    return "";
  }

  return addMinutesToTime(selectedBookingSlot.value.startTime, selectedDurationMinutes.value);
});

const estimatedBookingPrice = computed(() => {
  if (!space.value || !selectedBookingSlot.value || !computedEndTime.value) {
    return null;
  }

  return estimateReservationPrice({
    schedules: schedules.value,
    slot: selectedSlot.value,
    sessionDate: selectedDate.value,
    startTime: selectedBookingSlot.value.startTime,
    endTime: computedEndTime.value
  });
});

const bookingSubtotalLabel = computed(() =>
  estimatedBookingPrice.value == null
    ? t("common.labels.onRequest")
    : formatMoney(estimatedBookingPrice.value, locale.value)
);

const bookingTotalLabel = computed(() => bookingSubtotalLabel.value);

const bookingSubmitLabel = computed(() =>
  isLogged.value ? t("spaceDetail.booking.submit") : t("spaceDetail.booking.loginToReserve")
);

const canManageSpace = computed(() => {
  if (!space.value || !isLogged.value || space.value.active === false) {
    return false;
  }

  const managerId = Number(space.value.manager?.id || 0);
  return isAdmin.value || (managerId > 0 && managerId === currentUserId.value);
});
const canManageAvailability = computed(() =>
  Boolean(canManageSpace.value && space.value?.approvalStatus === "APPROVED")
);
const canManageSpaceEquipment = computed(() => canManageSpace.value);
const showOwnerActions = computed(() => canManageAvailability.value || canManageSpaceEquipment.value);

const availabilityManageRoute = computed(() => ({
  name: "MusicalSpaceAvailabilityManage",
  params: { id: space.value?.id || route.params.id }
}));

const displayAvailabilitySlots = computed(() =>
  availabilitySlots.value.map((slot) => ({
    ...slot,
    durationLabel: formatDurationLabel(slot.durationMinutes, t),
    periodLabel: getSlotPeriodLabel(slot.startTime),
    priceLabel: getAvailabilitySlotPriceLabel(slot)
  }))
);

const displayBookedSlots = computed(() =>
  bookedAvailabilitySlots.value.map((slot) => ({
    ...slot,
    stateLabel: t(`reservations.statuses.${slot.state || "PENDING"}`)
  }))
);

const selectedBookingRangeLabel = computed(() => {
  if (!selectedSlot.value) {
    return "";
  }

  if (selectedStartTime.value && computedEndTime.value) {
    return `${selectedStartTime.value} - ${computedEndTime.value}`;
  }

  return selectedSlot.value.rangeLabel;
});

const availabilitySummaryLabel = computed(() => {
  if (availabilityLoading.value) {
    return t("spaceDetail.availability.loading");
  }

  if (availabilityError.value) {
    return t("spaceDetail.availability.errorSummary");
  }

  if (!availabilitySlots.value.length && bookedAvailabilitySlots.value.length) {
    return t("spaceDetail.availability.bookedOnlySummary", {
      count: bookedAvailabilitySlots.value.length
    });
  }

  if (!availabilitySlots.value.length) {
    return t("spaceDetail.availability.emptySummary");
  }

  if (bookedAvailabilitySlots.value.length) {
    return t("spaceDetail.availability.availableWithBookedSummary", {
      free: availabilitySlots.value.length,
      booked: bookedAvailabilitySlots.value.length
    });
  }

  return t("spaceDetail.availability.availableSummary", {
    count: availabilitySlots.value.length
  });
});

const availabilitySummaryDetailLabel = computed(() => {
  if (availabilityLoading.value) {
    return t("spaceDetail.availability.loadingHint");
  }

  if (availabilityError.value) {
    return t("spaceDetail.availability.errorHint");
  }

  if (selectedSlot.value) {
    return t("spaceDetail.availability.selectedSummary", {
      range: selectedBookingRangeLabel.value
    });
  }

  if (availabilitySlots.value[0]) {
    return t("spaceDetail.availability.firstSlotSummary", {
      time: availabilitySlots.value[0].startTime
    });
  }

  if (bookedAvailabilitySlots.value.length) {
    return t("spaceDetail.availability.bookedOnlyHint");
  }

  return t("spaceDetail.availability.emptyHint");
});

onMounted(() => {
  loadDetail(route.params.id);
});

watch(() => route.params.id, (id) => {
  loadDetail(id);
});

watch(isLogged, async (logged) => {
  if (!space.value) return;

  if (logged && canUseUserActions.value) {
    try {
      await syncFavoriteState();
    } catch {
      // Soft failure: favorites are secondary on this screen.
    }
  } else {
    isFavorite.value = false;
  }
});

watch(selectedDate, async (date) => {
  if (!space.value || !date) return;

  if (ignoreNextDateWatch) {
    ignoreNextDateWatch = false;
    return;
  }

  try {
    await loadAvailability(space.value.id, date, false);
  } catch {
    // The section already exposes its own error state.
  }
});

async function loadDetail(id) {
  initialLoading.value = true;
  fatalError.value = "";
  pageNotice.value = null;
  bookingError.value = "";
  bookingSuccess.value = "";
  selectedSlot.value = null;
  selectedStartTime.value = "";
  equipmentModalOpen.value = false;
  equipment.value = [];
  reviews.value = [];
  schedules.value = [];
  availabilitySlots.value = [];
  bookedAvailabilitySlots.value = [];

  try {
    space.value = await MusicalSpaceRepository.getById(id);
    attendeesCount.value = Math.min(Math.max(1, attendeesCount.value), space.value.capacity || 1);
    ignoreNextDateWatch = true;
    selectedDate.value = getInitialDate();

    const tasks = await Promise.allSettled([
      loadEquipment(id),
      loadReviews(id),
      loadSchedules(id),
      loadAvailability(id, selectedDate.value, true),
      isLogged.value && canUseUserActions.value ? syncFavoriteState() : Promise.resolve()
    ]);

    if (!isLogged.value || !canUseUserActions.value) {
      isFavorite.value = false;
    }

    tasks.forEach((result) => {
      if (result.status === "rejected" && !pageNotice.value) {
        pageNotice.value = {
          type: "warning",
          message: t("spaceDetail.states.partialData")
        };
      }
    });
  } catch (error) {
    fatalError.value = getApiErrorMessage(error, t, "spaceDetail.states.error");
  } finally {
    initialLoading.value = false;
  }
}

async function loadEquipment(spaceId) {
  equipmentLoading.value = true;
  equipmentError.value = "";

  try {
    equipment.value = await MusicalSpaceRepository.getEquipment(spaceId);
  } catch (error) {
    equipment.value = [];
    equipmentError.value = getApiErrorMessage(error, t, "spaceDetail.equipment.error");
    throw error;
  } finally {
    equipmentLoading.value = false;
  }
}

function openEquipmentModal() {
  if (!canManageSpaceEquipment.value) {
    return;
  }

  equipmentModalOpen.value = true;
}

function closeEquipmentModal() {
  equipmentModalOpen.value = false;
}

function handleEquipmentUpdated(items) {
  equipment.value = items ?? [];
}

async function loadReviews(spaceId) {
  reviewsLoading.value = true;
  reviewsError.value = "";

  const [reviewsResult, ratingResult] = await Promise.allSettled([
    SpaceReviewRepository.getBySpace(spaceId),
    SpaceReviewRepository.getSpaceRating(spaceId)
  ]);

  if (reviewsResult.status === "fulfilled") {
    reviews.value = reviewsResult.value ?? [];
  } else {
    reviews.value = [];
    reviewsError.value = getApiErrorMessage(reviewsResult.reason, t, "spaceDetail.reviews.error");
  }

  if (ratingResult.status === "fulfilled") {
    ratingSummary.value = ratingResult.value;
  } else {
    ratingSummary.value = null;
    reviewsError.value = reviewsError.value || t("spaceDetail.reviews.error");
  }

  reviewsLoading.value = false;

  if (reviewsResult.status === "rejected" || ratingResult.status === "rejected") {
    throw new Error("reviews-load-failed");
  }
}

async function loadSchedules(spaceId) {
  try {
    schedules.value = await MusicalSpaceRepository.getSchedules(spaceId);
  } catch {
    schedules.value = [];
    throw new Error("schedule-load-failed");
  }
}

async function loadAvailability(spaceId, date, allowFallback) {
  const requestId = ++activeAvailabilityRequest;
  availabilityLoading.value = true;
  availabilityError.value = "";

  try {
    let resolvedDate = date;
    const response = await MusicalSpaceRepository.getAvailability(spaceId, date);
    let mappedSlots = mapAvailabilitySlots(response?.slots ?? []);
    let mappedBookedSlots = mapBookedAvailabilitySlots(response?.bookedSlots ?? []);

    if (allowFallback && !mappedSlots.length && date === getInitialDate()) {
      const tomorrow = getTomorrowDate();
      const tomorrowResponse = await MusicalSpaceRepository.getAvailability(spaceId, tomorrow);
      const tomorrowSlots = mapAvailabilitySlots(tomorrowResponse?.slots ?? []);

      if (tomorrowSlots.length) {
        resolvedDate = tomorrow;
        mappedSlots = tomorrowSlots;
        mappedBookedSlots = mapBookedAvailabilitySlots(tomorrowResponse?.bookedSlots ?? []);
        ignoreNextDateWatch = true;
        selectedDate.value = tomorrow;
      }
    }

    if (requestId !== activeAvailabilityRequest) {
      return;
    }

    availabilitySlots.value = mappedSlots;
    bookedAvailabilitySlots.value = mappedBookedSlots;

    if (!mappedSlots.length) {
      selectedSlot.value = null;
      selectedStartTime.value = "";
      selectedDurationMinutes.value = 0;
      return;
    }

    const existing = selectedSlot.value
      ? mappedSlots.find((slot) => slot.id === selectedSlot.value.id)
      : null;
    selectedSlot.value = existing || mappedSlots[0];
    syncSelectedStartTime();

    if (resolvedDate !== selectedDate.value) {
      ignoreNextDateWatch = true;
      selectedDate.value = resolvedDate;
    }

    syncSelectedDuration();
  } catch (error) {
    if (requestId !== activeAvailabilityRequest) {
      return;
    }

    availabilitySlots.value = [];
    bookedAvailabilitySlots.value = [];
    selectedSlot.value = null;
    selectedStartTime.value = "";
    selectedDurationMinutes.value = 0;
    availabilityError.value = getApiErrorMessage(error, t, "spaceDetail.availability.error");
    throw error;
  } finally {
    if (requestId === activeAvailabilityRequest) {
      availabilityLoading.value = false;
    }
  }
}

async function syncFavoriteState() {
  try {
    const favorites = await FavoriteSpaceRepository.getMine();
    isFavorite.value = (favorites ?? []).some(
      (item) => item.musicalSpace?.id === space.value?.id
    );
  } catch {
    isFavorite.value = false;
    throw new Error("favorite-sync-failed");
  }
}

async function handleToggleFavorite() {
  if (!space.value) return;
  if (!canUseUserActions.value) return;

  if (!isLogged.value) {
    await router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  favoriteBusy.value = true;
  pageNotice.value = null;

  try {
    if (isFavorite.value) {
      await FavoriteSpaceRepository.remove(space.value.id);
      isFavorite.value = false;
      pageNotice.value = { type: "success", message: t("spaceDetail.media.favoriteRemoved") };
    } else {
      await FavoriteSpaceRepository.add(space.value.id);
      isFavorite.value = true;
      pageNotice.value = { type: "success", message: t("spaceDetail.media.favoriteAdded") };
    }
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "spaceDetail.media.favoriteError")
    };
  } finally {
    favoriteBusy.value = false;
  }
}

function handleSelectedDateChange(value) {
  bookingError.value = "";
  bookingSuccess.value = "";
  selectedDate.value = normalizeSelectableDate(value);
}

function handleSlotSelection(slot) {
  selectedSlot.value = slot;
  selectedStartTime.value = slot.startTime;
  bookingError.value = "";
  bookingSuccess.value = "";
  syncSelectedDuration();
}

function handleStartTimeChange(value) {
  selectedStartTime.value = normalizeTimeString(value);
  bookingError.value = "";
  bookingSuccess.value = "";
  syncSelectedDuration();
}

function syncSelectedStartTime() {
  if (!selectedSlot.value) {
    selectedStartTime.value = "";
    return;
  }

  const availableStartTimes = startTimeOptions.value.map((option) => option.value);

  if (!availableStartTimes.length) {
    selectedStartTime.value = "";
    return;
  }

  if (!availableStartTimes.includes(selectedStartTime.value)) {
    selectedStartTime.value = availableStartTimes[0];
  }
}

function syncSelectedDuration() {
  const options = getDurationOptions(selectedBookingSlot.value);

  if (!options.length) {
    selectedDurationMinutes.value = 0;
    return;
  }

  if (!options.includes(selectedDurationMinutes.value)) {
    const preferred = options.find((value) => value >= 60) ?? options[0];
    selectedDurationMinutes.value = preferred;
  }
}

async function openScheduleModal() {
  if (!space.value) {
    return;
  }

  scheduleModalOpen.value = true;
  scheduleModalLoading.value = true;
  scheduleModalError.value = "";

  try {
    schedules.value = await MusicalSpaceRepository.getSchedules(space.value.id);
  } catch (error) {
    schedules.value = [];
    scheduleModalError.value = getApiErrorMessage(
      error,
      t,
      "spaceDetail.availability.fullScheduleError"
    );
  } finally {
    scheduleModalLoading.value = false;
  }
}

function closeScheduleModal() {
  scheduleModalOpen.value = false;
  scheduleModalError.value = "";
}

function formatScheduleDay(dayOfWeek) {
  const dayOrder = getDayOrder(dayOfWeek);

  if (dayOrder < 0) {
    return dayOfWeek || "--";
  }

  const monday = new Date(Date.UTC(2024, 0, 1 + dayOrder));
  return new Intl.DateTimeFormat(locale.value, { weekday: "long" }).format(monday);
}

function formatScheduleTimeRange(schedule) {
  const startTime = normalizeTimeString(schedule.startTime);
  const endTime = normalizeTimeString(schedule.endTime);
  return startTime && endTime ? `${startTime} - ${endTime}` : "--";
}

function formatSchedulePrice(schedule) {
  return schedule.price == null
    ? t("common.labels.onRequest")
    : formatMoney(schedule.price, locale.value);
}

function getDayOrder(dayOfWeek) {
  const order = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
  return order.indexOf(dayOfWeek);
}

async function handleReserve() {
  bookingError.value = "";
  bookingSuccess.value = "";

  if (!space.value) return;
  if (!canUseUserActions.value) return;

  if (!isLogged.value) {
    await router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  if (!selectedSlot.value || !selectedStartTime.value) {
    bookingError.value = t("spaceDetail.booking.errors.slotRequired");
    return;
  }

  if (!selectedDurationMinutes.value || !computedEndTime.value) {
    bookingError.value = t("spaceDetail.booking.errors.durationRequired");
    return;
  }

  if (attendeesCount.value < 1 || attendeesCount.value > space.value.capacity) {
    bookingError.value = t("spaceDetail.booking.errors.attendees");
    return;
  }

  bookingLoading.value = true;

  try {
    await ReservationSessionRepository.create({
      musicalSpaceId: space.value.id,
      sessionDate: selectedDate.value,
      startTime: selectedStartTime.value,
      endTime: computedEndTime.value,
      attendeesCount: attendeesCount.value,
      sessionType: mapReservationType(space.value.spaceType),
      observations: ""
    });

    bookingSuccess.value = t("spaceDetail.booking.success");
    await loadAvailability(space.value.id, selectedDate.value, false);
  } catch (error) {
    bookingError.value = getApiErrorMessage(error, t, "spaceDetail.booking.errors.generic");
  } finally {
    bookingLoading.value = false;
  }
}

function mapAvailabilitySlots(slots) {
  return (slots ?? []).map((slot) => {
    const startTime = normalizeTimeString(slot.startTime);
    const endTime = normalizeTimeString(slot.endTime);
    const slotObject = { startTime, endTime };
    const minutes = getSlotDurationMinutes(slotObject);

    return {
      id: `${startTime}-${endTime}`,
      startTime,
      endTime,
      price: normalizeSlotPrice(slot.price),
      durationMinutes: minutes,
      rangeLabel: `${startTime} - ${endTime}`,
      durationLabel: formatDurationLabel(minutes, t)
    };
  });
}

function mapBookedAvailabilitySlots(slots) {
  return (slots ?? []).map((slot) => {
    const startTime = normalizeTimeString(slot.startTime);
    const endTime = normalizeTimeString(slot.endTime);

    return {
      id: `${startTime}-${endTime}-${slot.state || "PENDING"}`,
      startTime,
      endTime,
      state: slot.state || "PENDING",
      rangeLabel: `${startTime} - ${endTime}`
    };
  });
}

function normalizeSelectableDate(value) {
  const fallbackDate = getInitialDate();

  if (!value) {
    return fallbackDate;
  }

  return value < fallbackDate ? fallbackDate : value;
}

function getSlotPeriodLabel(startTime) {
  const minutes = timeToMinutes(startTime);

  if (minutes < 12 * 60) {
    return t("spaceDetail.availability.periods.morning");
  }

  if (minutes < 18 * 60) {
    return t("spaceDetail.availability.periods.afternoon");
  }

  if (minutes < 22 * 60) {
    return t("spaceDetail.availability.periods.evening");
  }

  return t("spaceDetail.availability.periods.night");
}

function getAvailabilitySlotPriceLabel(slot) {
  const recommendedMinutes = getRecommendedPriceDuration(slot.durationMinutes);

  if (!recommendedMinutes) {
    return "";
  }

  const price = estimateReservationPrice({
    schedules: schedules.value,
    slot,
    sessionDate: selectedDate.value,
    startTime: slot.startTime,
    endTime: addMinutesToTime(slot.startTime, recommendedMinutes)
  });

  if (price == null) {
    return "";
  }

  return t("spaceDetail.availability.priceFrom", {
    price: formatMoney(price, locale.value)
  });
}

function getRecommendedPriceDuration(durationMinutes) {
  if (durationMinutes >= 60) {
    return 60;
  }

  return durationMinutes >= 30 ? 30 : 0;
}

function normalizeSlotPrice(price) {
  if (price == null || price === "") {
    return null;
  }

  const numericPrice = Number(price);
  return Number.isFinite(numericPrice) ? numericPrice : null;
}

function getEquipmentIcon(category) {
  const icons = {
    INSTRUMENT: "bi bi-music-note-beamed",
    SOUND: "bi bi-speaker",
    LIGHTING: "bi bi-lightbulb",
    RECORDING: "bi bi-mic",
    FURNITURE: "bi bi-lamp",
    ACCESSORY: "bi bi-plugin",
    OTHER: "bi bi-grid"
  };

  return icons[category] || icons.OTHER;
}

function mapReservationType(spaceType) {
  const mapping = {
    RECORDING_STUDIO: "RECORDING",
    CLASSROOM: "CLASS",
    CONCERT_HALL: "EVENT_PREPARATION",
    REHEARSAL_ROOM: "REHEARSAL",
    MULTIPURPOSE: "REHEARSAL",
    OTHER: "OTHER"
  };

  return mapping[spaceType] || "OTHER";
}
</script>

<style scoped>
.space-detail-page {
  min-height: calc(100vh - 72px);
  scroll-behavior: smooth;
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 28%),
    radial-gradient(circle at top right, rgba(80, 145, 255, 0.08), transparent 26%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.space-detail-page .container {
  max-width: 1180px;
}

.space-detail-shell {
  padding-bottom: 3rem;
}

.space-detail-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.22fr) minmax(320px, 0.95fr);
  gap: 1.4rem;
  margin-top: 1.25rem;
  align-items: stretch;
}

.space-detail-hero__media {
  min-width: 0;
}

.space-detail-hero__media :deep(.hero-media),
.space-detail-hero__media :deep(.hero-media__image-shell) {
  height: 100%;
}

.space-detail-hero :deep(.map-card) {
  min-width: 0;
  min-height: 100%;
}

.space-detail-hero :deep(.map-card__canvas) {
  height: 360px;
}

.space-detail-intro {
  display: grid;
  grid-template-columns: minmax(280px, 0.82fr) minmax(0, 1.18fr);
  gap: 1.4rem;
  margin-top: 1.4rem;
  align-items: stretch;
}

.space-detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 380px);
  gap: 1.4rem;
  margin-top: 1.4rem;
  align-items: start;
}

.space-detail-main {
  display: flex;
  flex-direction: column;
  gap: 1.15rem;
}

.space-detail-side {
  position: sticky;
  top: 88px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-self: start;
}

.space-detail-side :deep(.booking-card) {
  position: relative;
  top: auto;
}

.detail-section {
  padding: 1.35rem;
  overflow: hidden;
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.detail-section--intro {
  min-height: 0;
}

.detail-section--intro:first-child {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.detail-section__header {
  margin-bottom: 1rem;
}

.detail-section__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.detail-section h2 {
  margin: 0.35rem 0 0;
  color: #ffffff;
  font-size: 1.3rem;
  font-weight: 700;
}

.detail-copy {
  margin: 0;
  color: #d1d1d1;
  font-size: 1rem;
  line-height: 1.8;
}

.service-chips {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
}

.service-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  min-height: 42px;
  padding: 0 0.75rem;
  border-radius: 18px;
  border: 1px solid rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
  color: #ffffff;
  font-size: 0.92rem;
  font-weight: 600;
}

.service-chip i {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(29, 185, 84, 0.16);
  color: #1db954;
}

.page-notice,
.state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.page-notice.warning {
  color: #f9dda1;
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.page-notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice.info {
  color: #d1d1d1;
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

.space-detail-owner-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
  color: #ffffff;
}

.space-detail-owner-actions div {
  display: grid;
  gap: 0.2rem;
}

.space-detail-owner-actions span {
  color: #92efb3;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.space-detail-owner-actions strong {
  color: #ffffff;
  font-size: 1rem;
}

.space-detail-owner-actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-height: 42px;
  border-radius: 14px;
  white-space: nowrap;
}

.space-detail-owner-actions__buttons {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.schedule-modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1050;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.72);
}

.schedule-modal {
  width: min(760px, 100%);
  max-height: min(820px, calc(100vh - 2rem));
  overflow: auto;
  padding: 1.25rem;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  color: #ffffff;
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.45);
}

.schedule-modal__header,
.schedule-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.schedule-modal__header {
  margin-bottom: 1rem;
}

.schedule-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.schedule-modal__header h2 {
  margin: 0.3rem 0 0;
  color: #ffffff;
  font-size: 1.45rem;
}

.schedule-modal__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.schedule-modal__state {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #d1d1d1;
}

.schedule-modal__state--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.schedule-list {
  display: grid;
  gap: 0.75rem;
}

.schedule-row {
  padding: 0.95rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.schedule-row div {
  display: grid;
  gap: 0.25rem;
}

.schedule-row span {
  color: #9e9e9e;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.schedule-row strong {
  color: #ffffff;
}

@media (max-width: 1199.98px) {
  .space-detail-hero,
  .space-detail-intro,
  .space-detail-layout {
    grid-template-columns: 1fr;
  }

  .space-detail-side {
    position: static;
    max-height: none;
    overflow: visible;
  }
}

@media (max-width: 767.98px) {
  .space-detail-page .container {
    padding-right: 1rem;
    padding-left: 1rem;
  }

  .space-detail-owner-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .space-detail-owner-actions__buttons {
    justify-content: stretch;
  }

  .space-detail-owner-actions__buttons .btn {
    flex: 1 1 100%;
  }

  .service-chips {
    grid-template-columns: 1fr;
  }

  .schedule-row {
    flex-direction: column;
  }
}
</style>
