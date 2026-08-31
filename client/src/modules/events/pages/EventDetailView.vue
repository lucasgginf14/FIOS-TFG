<template>
  <div class="event-detail-page">
    <section class="event-detail-shell">
      <div class="container py-5">
        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("events.states.loadingDetail") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error">
          <strong>{{ t("events.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
          <button
            type="button"
            class="btn btn-outline-light"
            @click="router.push({ name: 'EventList' })"
          >
            {{ t("events.actions.backToList") }}
          </button>
        </div>

        <template v-else-if="event">
          <header v-if="showInlineAdminActions" class="detail-header">
            <div class="detail-header__actions">
              <button type="button" class="btn btn-outline-light" @click="openEditModal">
                <i class="bi bi-pencil-square"></i>
                {{ t("events.actions.edit") }}
              </button>
              <button
                type="button"
                class="btn btn-outline-danger"
                :disabled="deleteLoading"
                @click="deleteEvent"
              >
                <i class="bi bi-archive"></i>
                {{ deleteLoading ? t("events.actions.archiving") : t("events.actions.archive") }}
              </button>
            </div>
          </header>

          <div v-if="pageNotice" class="page-notice" :class="pageNotice.type">
            {{ pageNotice.message }}
          </div>

          <section class="detail-hero">
            <div class="detail-hero__media">
              <AppImage
                :src="image"
                :alt="event.title"
                :fallback-src="eventPlaceholder"
                :fallback-label="t('events.cards.imageFallback')"
                icon-class="bi bi-calendar2-event"
              />
            </div>

            <div class="detail-hero__body">
              <span class="detail-header__eyebrow">{{ t("events.detail.eyebrow") }}</span>
              <h1>{{ event.title }}</h1>

              <div class="detail-hero__badges">
                <span class="event-badge">{{ t(`events.types.${event.eventType || "OTHER"}`) }}</span>
                <span class="event-badge">{{ event.musicalGenre || t("events.detail.noGenre") }}</span>
                <span class="event-badge" :class="getEventBadgeTone(event.source)">
                  {{ t(`events.sources.${event.source || "INTERNAL"}`) }}
                </span>
                <span class="event-badge" :class="getEventStatusTone(event.status)">
                  {{ t(`events.statuses.${event.status || "DRAFT"}`) }}
                </span>
              </div>

              <div class="detail-header__meta">
                <span v-for="item in metaItems" :key="item.label">
                  <i :class="item.icon"></i>
                  {{ item.label }}
                </span>
              </div>

              <div class="detail-copy">
                <h2>{{ t("events.detail.descriptionTitle") }}</h2>
                <p>{{ event.description || t("events.detail.emptyDescription") }}</p>
              </div>

              <div class="detail-actions">
                <a
                  v-if="safeExternalUrl"
                  :href="safeExternalUrl"
                  target="_blank"
                  rel="noreferrer"
                  class="btn btn-success"
                >
                  <i class="bi bi-box-arrow-up-right"></i>
                  {{
                    event.source === "EXTERNAL"
                      ? t("events.actions.ticketmaster")
                      : t("events.actions.externalLink")
                  }}
                </a>
                <button
                  v-if="showReservationActions"
                  class="btn btn-success"
                  type="button"
                  :disabled="purchaseButtonDisabled"
                  @click="handleReserve"
                >
                  <span
                    v-if="purchaseLoading"
                    class="spinner-border spinner-border-sm"
                    aria-hidden="true"
                  ></span>
                  <i v-else :class="purchaseActionIcon"></i>
                  {{ purchaseActionLabel }}
                </button>
                <button
                  v-if="canCancelReservation"
                  class="btn btn-outline-danger"
                  type="button"
                  :disabled="cancelLoading"
                  @click="handleCancelReservation"
                >
                  <span
                    v-if="cancelLoading"
                    class="spinner-border spinner-border-sm"
                    aria-hidden="true"
                  ></span>
                  <i v-else class="bi bi-x-circle"></i>
                  {{ t("events.detail.purchase.cancelAction") }}
                </button>
                <RouterLink
                  v-if="event.musicalSpace?.id"
                  class="btn btn-outline-light"
                  :to="spaceRoute"
                >
                  <i class="bi bi-building"></i>
                  {{ t("events.actions.viewSpace") }}
                </RouterLink>
                <RouterLink
                  v-if="event.band?.id"
                  class="btn btn-outline-light"
                  :to="bandRoute"
                >
                  <i class="bi bi-people"></i>
                  {{ t("events.actions.viewBand") }}
                </RouterLink>
              </div>
            </div>
          </section>

          <div class="detail-layout">
            <main class="detail-main">
              <section class="detail-section">
                <div class="detail-section__header">
                  <span class="detail-section__eyebrow">{{ t("events.detail.infoEyebrow") }}</span>
                  <h2>{{ t("events.detail.infoTitle") }}</h2>
                </div>

                <div class="detail-info-grid">
                  <article v-for="item in infoCards" :key="item.label" class="detail-info-card">
                    <i :class="item.icon"></i>
                    <span>{{ item.label }}</span>
                    <strong>{{ item.value }}</strong>
                  </article>
                </div>
              </section>

              <section v-if="canViewEventReservations" class="detail-section organizer-reservations">
                <div class="detail-section__header">
                  <span class="detail-section__eyebrow">{{ t("events.detail.organizer.eyebrow") }}</span>
                  <h2>{{ t("events.detail.organizer.title") }}</h2>
                </div>

                <div class="organizer-reservations__stats">
                  <span>
                    <strong>{{ capacityLabel }}</strong>
                    {{ t("events.detail.fields.capacity") }}
                  </span>
                  <span>
                    <strong>{{ reservedTicketsLabel }}</strong>
                    {{ t("events.detail.fields.reserved") }}
                  </span>
                  <span>
                    <strong>{{ availableTicketsLabel }}</strong>
                    {{ t("events.detail.fields.available") }}
                  </span>
                </div>

                <div v-if="eventReservationsLoading" class="organizer-reservations__state">
                  <div class="spinner-border spinner-border-sm text-success" role="status"></div>
                  <span>{{ t("events.detail.organizer.loading") }}</span>
                </div>
                <p v-else-if="eventReservationsError" class="organizer-reservations__error">
                  {{ eventReservationsError }}
                </p>
                <div v-else-if="eventReservations.length" class="organizer-reservations__list">
                  <article
                    v-for="reservation in eventReservations"
                    :key="reservation.id"
                    class="organizer-reservation"
                  >
                    <div>
                      <strong>{{ getReservationUserName(reservation.user) }}</strong>
                      <span>{{ formatReservationDate(reservation.reservedAt || reservation.purchasedAt) }}</span>
                    </div>
                    <strong>{{ formatReservationPrice(getPurchaseAmountDue(reservation)) }}</strong>
                  </article>
                </div>
                <p v-else class="organizer-reservations__empty">
                  {{ t("events.detail.organizer.empty") }}
                </p>
              </section>

              <section class="detail-section">
                <div class="detail-section__header">
                  <span class="detail-section__eyebrow">{{ t("events.detail.linksEyebrow") }}</span>
                  <h2>{{ t("events.detail.linksTitle") }}</h2>
                </div>

                <div class="detail-associations">
                  <article class="detail-link-card">
                    <span>{{ t("events.detail.fields.location") }}</span>
                    <strong>{{ mapSubtitle || t("events.detail.noAddress") }}</strong>
                  </article>
                  <RouterLink
                    v-if="event.musicalSpace?.id"
                    class="detail-link-card clickable-card"
                    :to="spaceRoute"
                    :aria-label="`${t('events.detail.fields.space')}: ${event.musicalSpace.name}`"
                  >
                    <span>{{ t("events.detail.fields.space") }}</span>
                    <strong>{{ event.musicalSpace?.name || t("events.detail.noSpace") }}</strong>
                    <span class="clickable-card__open-indicator" aria-hidden="true">
                      <i class="bi bi-arrow-up-right"></i>
                    </span>
                  </RouterLink>
                  <article v-else class="detail-link-card">
                    <span>{{ t("events.detail.fields.space") }}</span>
                    <strong>{{ t("events.detail.noSpace") }}</strong>
                  </article>
                  <RouterLink
                    v-if="event.band?.id"
                    class="detail-link-card clickable-card"
                    :to="bandRoute"
                    :aria-label="`${t('events.detail.fields.band')}: ${event.band.name}`"
                  >
                    <span>{{ t("events.detail.fields.band") }}</span>
                    <strong>{{ event.band?.name || t("events.detail.noBand") }}</strong>
                    <span class="clickable-card__open-indicator" aria-hidden="true">
                      <i class="bi bi-arrow-up-right"></i>
                    </span>
                  </RouterLink>
                  <article v-else class="detail-link-card">
                    <span>{{ t("events.detail.fields.band") }}</span>
                    <strong>{{ t("events.detail.noBand") }}</strong>
                  </article>
                </div>
              </section>
            </main>

            <aside class="detail-side">
              <section class="detail-section detail-summary">
                <div class="detail-summary__row">
                  <i class="bi bi-calendar-event"></i>
                  <div>
                    <span>{{ t("events.detail.fields.date") }}</span>
                    <strong>{{ formatEventDate(event.eventDate, locale) }}</strong>
                  </div>
                </div>
                <div class="detail-summary__row">
                  <i class="bi bi-geo-alt"></i>
                  <div>
                    <span>{{ t("events.detail.fields.location") }}</span>
                    <strong>{{ mapSubtitle || t("events.detail.noAddress") }}</strong>
                  </div>
                </div>
                <div v-if="isInternalFiosEvent" class="detail-summary__row">
                  <i class="bi bi-ticket-perforated"></i>
                  <div>
                    <span>{{ t("events.detail.fields.price") }}</span>
                    <strong>{{ priceLabel }}</strong>
                  </div>
                </div>
                <div v-if="isInternalFiosEvent" class="detail-summary__row">
                  <i class="bi bi-people"></i>
                  <div>
                    <span>{{ t("events.detail.fields.capacity") }}</span>
                    <strong>{{ capacityLabel }}</strong>
                  </div>
                </div>
                <div v-if="isInternalFiosEvent" class="detail-summary__row">
                  <i class="bi bi-person-check"></i>
                  <div>
                    <span>{{ t("events.detail.fields.reserved") }}</span>
                    <strong>{{ reservedTicketsLabel }}</strong>
                  </div>
                </div>
                <div v-if="isInternalFiosEvent" class="detail-summary__row">
                  <i class="bi bi-person-plus"></i>
                  <div>
                    <span>{{ t("events.detail.fields.available") }}</span>
                    <strong>{{ availableTicketsLabel }}</strong>
                  </div>
                </div>
                <a
                  v-if="safeExternalUrl"
                  :href="safeExternalUrl"
                  target="_blank"
                  rel="noreferrer"
                  class="btn btn-success w-100"
                >
                  <i class="bi bi-box-arrow-up-right"></i>
                  {{
                    event.source === "EXTERNAL"
                      ? t("events.actions.ticketmaster")
                      : t("events.actions.externalLink")
                  }}
                </a>
                <div
                  v-if="showReservationActions"
                  class="event-purchase-box"
                  :class="{ 'is-saved': hasReservedEvent }"
                >
                  <div class="event-purchase-box__copy">
                    <span>{{ t("events.detail.purchase.eyebrow") }}</span>
                    <strong>{{ purchaseTitle }}</strong>
                    <p>{{ purchaseText }}</p>
                  </div>

                  <p v-if="showPurchasePaymentNote" class="event-purchase-box__payment-note">
                    <i class="bi bi-info-circle" aria-hidden="true"></i>
                    {{ t("events.detail.purchase.paymentNote") }}
                  </p>

                  <button
                    class="btn btn-success w-100"
                    type="button"
                    :disabled="purchaseButtonDisabled"
                    @click="handleReserve"
                  >
                    <span
                      v-if="purchaseLoading"
                      class="spinner-border spinner-border-sm"
                      aria-hidden="true"
                    ></span>
                    <i v-else :class="purchaseActionIcon"></i>
                    {{ purchaseActionLabel }}
                  </button>
                  <button
                    v-if="canCancelReservation"
                    class="btn btn-outline-danger w-100"
                    type="button"
                    :disabled="cancelLoading"
                    @click="handleCancelReservation"
                  >
                    <span
                      v-if="cancelLoading"
                      class="spinner-border spinner-border-sm"
                      aria-hidden="true"
                    ></span>
                    <i v-else class="bi bi-x-circle"></i>
                    {{ t("events.detail.purchase.cancelAction") }}
                  </button>

                  <p v-if="purchaseNotice" class="event-purchase-box__notice">
                    {{ purchaseNotice }}
                  </p>
                  <p v-if="purchaseError" class="event-purchase-box__error">
                    {{ purchaseError }}
                  </p>
                </div>
              </section>

              <section class="detail-section detail-section--sticky">
                <div class="detail-section__header">
                  <span class="detail-section__eyebrow">{{ t("events.detail.mapEyebrow") }}</span>
                  <h2>{{ t("events.detail.mapTitle") }}</h2>
                </div>

                <div v-if="!hasCoordinates" class="map-empty">
                  <i class="bi bi-geo-alt"></i>
                  <span>{{ t("events.detail.mapEmpty") }}</span>
                </div>
                <div
                  v-else
                  class="detail-map-shell"
                  :class="{ 'has-fallback': mapTilesUnavailable }"
                >
                  <div ref="mapElement" class="detail-map"></div>
                  <div v-if="mapTilesUnavailable" class="detail-map-fallback">
                    <i class="bi bi-geo-alt"></i>
                    <strong>{{ event.venueName || event.city || t("events.detail.mapEmpty") }}</strong>
                    <span>{{ mapSubtitle || t("events.detail.noAddress") }}</span>
                    <small>{{ coordinatesLabel }}</small>
                  </div>
                </div>
              </section>
            </aside>
          </div>
        </template>
      </div>
    </section>

    <EventAdminModal
      :open="adminModalOpen"
      :model-value="eventForm"
      :editing="true"
      :submitting="adminSubmitting"
      :error-message="adminModalError"
      @close="closeAdminModal"
      @submit="submitAdminEvent"
      @update:model-value="eventForm = $event"
    />
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { RouterLink, useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import L from "leaflet";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import AppImage from "@/common/components/AppImage.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import auth from "@/common/auth";
import { getStore } from "@/common/store";
import EventRepository from "@/repositories/EventRepository";
import EventPurchaseRepository from "@/repositories/EventPurchaseRepository";
import EventAdminModal from "../components/EventAdminModal.vue";
import {
  buildAdminEventPayload,
  buildEventDetailMeta,
  formatEventDate,
  formatEventMoney,
  formatEventTimeRange,
  getEventBadgeTone,
  getEventImage,
  getEventMapSubtitle,
  getEventStatusTone,
  getEventTicketValidationKey,
  hasEventCoordinates,
  mapEventToForm
} from "../eventUtils";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow
});

const route = useRoute();
const router = useRouter();
const { locale, t } = useI18n();
const store = getStore();

const loading = ref(true);
const fatalError = ref("");
const pageNotice = ref(null);
const event = ref(null);
const deleteLoading = ref(false);
const purchaseLoading = ref(false);
const cancelLoading = ref(false);
const purchaseError = ref("");
const purchaseNotice = ref("");
const purchaseStatus = ref(createEmptyPurchaseStatus());
const eventReservations = ref([]);
const eventReservationsLoading = ref(false);
const eventReservationsError = ref("");

const adminModalOpen = ref(false);
const adminSubmitting = ref(false);
const adminModalError = ref("");
const eventForm = ref(mapEventToForm(null));

const mapElement = ref(null);
const mapTilesUnavailable = ref(false);
const isAdmin = computed(() => auth.isAdmin());
const currentUserId = computed(() => Number(store.state.user.id || 0));
const canUseUserActions = computed(() => !isAdmin.value);
const showInlineAdminActions = false;
const image = computed(() => getEventImage(event.value));
const timeRange = computed(() => formatEventTimeRange(event.value));
const priceLabel = computed(() =>
  formatEventMoney(
    event.value?.ticketPrice,
    locale.value,
    t("events.cards.onRequest"),
    t("common.labels.free")
  )
);
const capacityLabel = computed(() =>
  capacityCount.value != null
    ? t("events.detail.capacityValue", { value: capacityCount.value })
    : t("events.detail.noCapacity")
);
const reservedTicketsLabel = computed(() =>
  t("events.detail.reservedValue", { value: reservedTicketsCount.value })
);
const availableTicketsLabel = computed(() =>
  availableTicketsCount.value != null
    ? t("events.detail.availableValue", { value: availableTicketsCount.value })
    : t("events.detail.noCapacity")
);
const mapSubtitle = computed(() => getEventMapSubtitle(event.value));
const coordinatesLabel = computed(() => {
  if (!hasCoordinates.value) {
    return "";
  }

  return `${event.value.latitude.toFixed(4)}, ${event.value.longitude.toFixed(4)}`;
});
const metaItems = computed(() =>
  event.value ? buildEventDetailMeta(event.value, locale.value, t) : []
);
const isInternalFiosEvent = computed(() => event.value?.source === "INTERNAL");
const isPurchaseAvailable = computed(() =>
  isInternalFiosEvent.value &&
  event.value?.status === "PUBLISHED" &&
  capacityCount.value != null &&
  capacityCount.value > 0 &&
  !hasEventStarted(event.value)
);
const showReservationActions = computed(() =>
  isInternalFiosEvent.value && canUseUserActions.value && !hasEventStarted(event.value)
);
const activeReservation = computed(() => purchaseStatus.value.reservation || purchaseStatus.value.purchase || null);
const hasReservedEvent = computed(() =>
  Boolean(purchaseStatus.value.reserved || purchaseStatus.value.purchased || isActiveReservation(activeReservation.value))
);
const canCancelReservation = computed(() =>
  showReservationActions.value && auth.isAuthenticated() && isActiveReservation(activeReservation.value)
);
const showPurchasePaymentNote = computed(() =>
  hasReservedEvent.value || (isPurchaseAvailable.value && !isEventSoldOut.value)
);
const capacityCount = computed(() =>
  toOptionalNumber(purchaseStatus.value.capacity ?? event.value?.capacity)
);
const reservedTicketsCount = computed(
  () =>
    toOptionalNumber(
      purchaseStatus.value.reservedTickets ?? purchaseStatus.value.soldTickets ?? event.value?.reservedTickets
    ) ?? 0
);
const availableTicketsCount = computed(() => {
  const explicitAvailable = toOptionalNumber(
    purchaseStatus.value.availableTickets ?? purchaseStatus.value.remainingTickets ?? event.value?.availableTickets
  );

  if (explicitAvailable != null) {
    return explicitAvailable;
  }

  if (capacityCount.value == null) {
    return null;
  }

  return Math.max(capacityCount.value - reservedTicketsCount.value, 0);
});
const isEventSoldOut = computed(
  () => Boolean(purchaseStatus.value.soldOut) || (availableTicketsCount.value != null && availableTicketsCount.value <= 0)
);
const canViewEventReservations = computed(
  () =>
    isInternalFiosEvent.value &&
    auth.isAuthenticated() &&
    (isAdmin.value || Number(event.value?.createdBy?.id || 0) === currentUserId.value)
);
const purchaseButtonDisabled = computed(() =>
  purchaseLoading.value ||
  hasReservedEvent.value ||
  isEventSoldOut.value ||
  !isPurchaseAvailable.value ||
  !canUseUserActions.value
);
const purchaseActionIcon = computed(() => {
  if (hasReservedEvent.value) {
    return "bi bi-check2-circle";
  }

  if (isEventSoldOut.value || !isPurchaseAvailable.value) {
    return "bi bi-slash-circle";
  }

  return auth.isAuthenticated() ? "bi bi-ticket-perforated" : "bi bi-box-arrow-in-right";
});
const purchaseActionLabel = computed(() => {
  if (purchaseLoading.value) {
    return t("events.detail.purchase.saving");
  }

  if (hasReservedEvent.value) {
    return t("events.detail.purchase.savedAction");
  }

  if (isEventSoldOut.value) {
    return t("events.detail.purchase.soldOutAction");
  }

  if (!isPurchaseAvailable.value) {
    return t("events.detail.purchase.unavailableAction");
  }

  if (!auth.isAuthenticated()) {
    return t("events.detail.purchase.loginAction");
  }

  return t("events.detail.purchase.buyAction");
});
const purchaseTitle = computed(() => {
  if (hasReservedEvent.value) {
    return t("events.detail.purchase.savedTitle");
  }

  if (isEventSoldOut.value) {
    return t("events.detail.purchase.soldOutTitle");
  }

  if (!isPurchaseAvailable.value) {
    return t("events.detail.purchase.unavailableTitle");
  }

  return t("events.detail.purchase.title");
});
const purchaseText = computed(() => {
  if (hasReservedEvent.value) {
    return t("events.detail.purchase.savedText", {
      price: formatReservationPrice(getPurchaseAmountDue(activeReservation.value))
    });
  }

  if (isEventSoldOut.value) {
    return t("events.detail.purchase.soldOutText");
  }

  if (!isPurchaseAvailable.value) {
    return t("events.detail.purchase.unavailableText");
  }

  const remainingTickets = availableTicketsCount.value;

  if (remainingTickets != null) {
    return t("events.detail.purchase.remainingText", { count: remainingTickets });
  }

  return t("events.detail.purchase.text");
});
const infoCards = computed(() => {
  const cards = [
    {
      icon: "bi bi-calendar-event",
      label: t("events.detail.fields.date"),
      value: formatEventDate(event.value?.eventDate, locale.value)
    },
    {
      icon: "bi bi-clock",
      label: t("events.detail.fields.time"),
      value: timeRange.value
    },
    {
      icon: "bi bi-geo-alt",
      label: t("events.detail.fields.city"),
      value: event.value?.city || t("events.detail.noLocation")
    },
    {
      icon: "bi bi-shop",
      label: t("events.detail.fields.venue"),
      value: event.value?.venueName || t("events.detail.noVenue")
    }
  ];

  if (isInternalFiosEvent.value) {
    cards.push(
      {
        icon: "bi bi-ticket-perforated",
        label: t("events.detail.fields.price"),
        value: priceLabel.value
      },
      {
        icon: "bi bi-people",
        label: t("events.detail.fields.capacity"),
        value: capacityLabel.value
      },
      {
        icon: "bi bi-person-check",
        label: t("events.detail.fields.reserved"),
        value: reservedTicketsLabel.value
      },
      {
        icon: "bi bi-person-plus",
        label: t("events.detail.fields.available"),
        value: availableTicketsLabel.value
      }
    );
  }

  return cards;
});
const hasCoordinates = computed(() => hasEventCoordinates(event.value));
const safeExternalUrl = computed(() => getSafeExternalUrl(getExternalUrl(event.value)));
const spaceRoute = computed(() =>
  event.value?.musicalSpace?.id
    ? { name: "MusicalSpaceDetail", params: { id: event.value.musicalSpace.id } }
    : { name: "MusicalSpaceList" }
);
const bandRoute = computed(() =>
  event.value?.band?.id ? { name: "BandDetail", params: { id: event.value.band.id } } : { name: "BandList" }
);

let mapInstance;
let markerInstance;
let mapTileErrorCount = 0;

onMounted(() => {
  loadEvent(route.params.id);
});

watch(
  () => route.params.id,
  (id) => {
    loadEvent(id);
  }
);

watch(
  () => [event.value?.latitude, event.value?.longitude],
  () => {
    buildMap();
  }
);

onBeforeUnmount(() => {
  if (mapInstance) {
    mapInstance.remove();
  }
});

async function loadEvent(id) {
  loading.value = true;
  fatalError.value = "";
  pageNotice.value = null;
  resetPurchaseState();

  try {
    event.value = await EventRepository.getById(id);
    mapTilesUnavailable.value = false;
    mapTileErrorCount = 0;
    eventForm.value = mapEventToForm(event.value);
    await loadPurchaseStatus();
    await loadEventReservations();
  } catch (error) {
    fatalError.value = getApiErrorMessage(error, t, "events.states.detailError");
  } finally {
    loading.value = false;
  }

  await renderMapAfterViewUpdate();
}

async function loadPurchaseStatus() {
  if (!event.value?.id || !isInternalFiosEvent.value) {
    purchaseStatus.value = createEmptyPurchaseStatus();
    return;
  }

  try {
    purchaseStatus.value = normalizePurchaseStatus(
      await EventPurchaseRepository.getEventStatus(event.value.id)
    );
  } catch {
    purchaseStatus.value = createEmptyPurchaseStatus();
  }
}

async function loadEventReservations() {
  eventReservations.value = [];
  eventReservationsError.value = "";

  if (!event.value?.id || !canViewEventReservations.value) {
    return;
  }

  eventReservationsLoading.value = true;

  try {
    eventReservations.value = await EventPurchaseRepository.getEventReservations(event.value.id);
  } catch (error) {
    eventReservationsError.value = getApiErrorMessage(error, t, "events.detail.organizer.error");
  } finally {
    eventReservationsLoading.value = false;
  }
}

async function handleReserve() {
  if (!canUseUserActions.value || !isPurchaseAvailable.value || hasReservedEvent.value || isEventSoldOut.value) {
    return;
  }

  if (!auth.isAuthenticated()) {
    await router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  purchaseLoading.value = true;
  purchaseError.value = "";
  purchaseNotice.value = "";

  try {
    const reservation = await EventPurchaseRepository.reserve(event.value.id);
    await loadPurchaseStatus();
    await loadEventReservations();
    purchaseNotice.value = t("events.detail.purchase.success", {
      price: formatReservationPrice(getPurchaseAmountDue(reservation))
    });
  } catch (error) {
    purchaseError.value = getApiErrorMessage(error, t, "events.detail.purchase.error");
    await loadPurchaseStatus();
    await loadEventReservations();
  } finally {
    purchaseLoading.value = false;
  }
}

async function handleCancelReservation() {
  if (!canCancelReservation.value) {
    return;
  }

  const confirmed = window.confirm(t("events.detail.purchase.cancelConfirm"));

  if (!confirmed) {
    return;
  }

  cancelLoading.value = true;
  purchaseError.value = "";
  purchaseNotice.value = "";

  try {
    await EventPurchaseRepository.cancel(activeReservation.value.id);
    await loadPurchaseStatus();
    await loadEventReservations();
    purchaseNotice.value = t("events.detail.purchase.cancelSuccess");
  } catch (error) {
    purchaseError.value = getApiErrorMessage(error, t, "events.detail.purchase.cancelError");
    await loadPurchaseStatus();
    await loadEventReservations();
  } finally {
    cancelLoading.value = false;
  }
}

function openEditModal() {
  eventForm.value = mapEventToForm(event.value);
  adminModalError.value = "";
  adminModalOpen.value = true;
}

function closeAdminModal() {
  adminModalOpen.value = false;
  adminModalError.value = "";
}

async function submitAdminEvent() {
  const payload = buildAdminEventPayload(eventForm.value);

  if (
    !payload.title ||
    !payload.eventDate ||
    !payload.venueName ||
    !payload.city ||
    !payload.country
  ) {
    adminModalError.value = t("events.admin.validation");
    return;
  }

  const ticketValidationKey = getEventTicketValidationKey(payload);

  if (ticketValidationKey) {
    adminModalError.value = t(ticketValidationKey);
    return;
  }

  adminSubmitting.value = true;
  adminModalError.value = "";

  try {
    event.value = await EventRepository.update(route.params.id, payload);
    pageNotice.value = { type: "success", message: t("events.admin.updated") };
    closeAdminModal();
    await loadPurchaseStatus();
    await loadEventReservations();
    await renderMapAfterViewUpdate();
  } catch (error) {
    adminModalError.value = getApiErrorMessage(error, t, "events.admin.error");
  } finally {
    adminSubmitting.value = false;
  }
}

async function deleteEvent() {
  const confirmed = window.confirm(t("events.admin.deleteConfirm"));

  if (!confirmed) {
    return;
  }

  deleteLoading.value = true;

  try {
    await EventRepository.delete(route.params.id);
    router.push({ name: "EventList", query: { deleted: "true" } });
  } catch (error) {
    pageNotice.value = {
      type: "error",
      message: getApiErrorMessage(error, t, "events.admin.deleteError")
    };
  } finally {
    deleteLoading.value = false;
  }
}

async function renderMapAfterViewUpdate() {
  await nextTick();
  buildMap();
}

function buildMap() {
  if (!hasCoordinates.value || !mapElement.value) {
    mapTilesUnavailable.value = false;
    if (mapInstance) {
      mapInstance.remove();
      mapInstance = null;
      markerInstance = null;
      mapTileErrorCount = 0;
    }
    return;
  }

  if (!mapInstance) {
    mapInstance = L.map(mapElement.value, { zoomControl: true }).setView(
      [event.value.latitude, event.value.longitude],
      14
    );
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors"
    })
      .on("tileerror", () => {
        mapTileErrorCount += 1;
        mapTilesUnavailable.value = mapTileErrorCount >= 3;
      })
      .on("tileload", () => {
        mapTilesUnavailable.value = false;
      })
      .addTo(mapInstance);
  } else {
    mapInstance.setView([event.value.latitude, event.value.longitude], 14);
  }

  if (markerInstance) {
    markerInstance.remove();
  }

  markerInstance = L.marker([event.value.latitude, event.value.longitude]).addTo(mapInstance);
  markerInstance.bindPopup(createDetailMapPopup());
  setTimeout(() => {
    mapInstance?.invalidateSize();
  }, 0);
}

function createDetailMapPopup() {
  const popup = document.createElement("div");
  popup.className = "event-map-popup";

  const title = document.createElement("strong");
  title.textContent = event.value?.title ?? "";
  popup.appendChild(title);

  const location = document.createElement("div");
  location.textContent = [event.value?.venueName, event.value?.city].filter(Boolean).join(" - ");
  popup.appendChild(location);

  return popup;
}

function getSafeExternalUrl(value) {
  if (!value) {
    return "";
  }

  try {
    const parsed = new URL(value, window.location.origin);
    return ["http:", "https:"].includes(parsed.protocol) ? parsed.href : "";
  } catch {
    return "";
  }
}

function getExternalUrl(item) {
  if (!item?.externalUrl) {
    return "";
  }

  return normalizeTicketmasterSearchUrl(item) || item.externalUrl;
}

function normalizeTicketmasterSearchUrl(item) {
  try {
    const parsed = new URL(item.externalUrl, window.location.origin);
    const isTicketmaster = parsed.hostname === "ticketmaster.es" || parsed.hostname.endsWith(".ticketmaster.es");
    const query = parsed.searchParams.get("q") || "";
    const searchTerm = getTicketmasterSearchTerm(item.title);
    const artistUrl = getTicketmasterArtistUrl(searchTerm);

    if (!isTicketmaster || !query) {
      return "";
    }

    if (artistUrl) {
      return artistUrl;
    }

    if (!queryHasDateParts(query)) {
      return "";
    }

    if (!searchTerm) {
      return "";
    }

    parsed.searchParams.set("q", searchTerm);
    return parsed.href;
  } catch {
    return "";
  }
}

function queryHasDateParts(query) {
  return /\b20\d{2}\b/.test(query);
}

function getTicketmasterSearchTerm(title = "") {
  return String(title)
    .split(" - ")[0]
    .split(" + ")[0]
    .trim();
}

function getTicketmasterArtistUrl(searchTerm) {
  const artistUrls = {
    Shakira: "https://www.ticketmaster.es/artist/shakira-entradas/7142",
    Morat: "https://www.ticketmaster.es/artist/morat-entradas/973802",
    "Hombres G": "https://www.ticketmaster.es/artist/hombres-g-entradas/11284",
    "The Strokes": "https://www.ticketmaster.es/artist/the-strokes-entradas/12759",
    Placebo: "https://www.ticketmaster.es/artist/placebo-entradas/5183",
    Pitbull: "https://www.ticketmaster.es/artist/pitbull-entradas/67883",
    Bunbury: "https://www.ticketmaster.es/artist/bunbury-entradas/13858",
    "Niall Horan": "https://www.ticketmaster.es/artist/niall-horan-entradas/977341",
    Cazzu: "https://www.ticketmaster.es/artist/cazzu-entradas/1213894",
    "Simple Plan": "https://www.ticketmaster.es/artist/simple-plan-entradas/16164",
    "Amon Amarth": "https://www.ticketmaster.es/artist/amon-amarth-entradas/35498",
    "Hatsune Miku": "https://www.ticketmaster.es/artist/hatsune-miku-entradas/943109",
    "Tokio Hotel": "https://www.ticketmaster.es/artist/tokio-hotel-entradas/40563",
    "Los Fabulosos Cadillacs": "https://www.ticketmaster.es/artist/los-fabulosos-cadillacs-entradas/4446",
    "Joe Bonamassa": "https://www.ticketmaster.es/artist/joe-bonamassa-entradas/18223",
    "Arlo Parks": "https://www.ticketmaster.es/artist/arlo-parks-entradas/1013762",
    Kamelot: "https://www.ticketmaster.es/artist/kamelot-entradas/31125",
    "Alex Ubago": "https://www.ticketmaster.es/artist/alex-ubago-entradas/121433",
    Anastacia: "https://www.ticketmaster.es/artist/anastacia-tickets/27328"
  };

  return artistUrls[searchTerm] || "";
}

function resetPurchaseState() {
  purchaseLoading.value = false;
  cancelLoading.value = false;
  purchaseError.value = "";
  purchaseNotice.value = "";
  purchaseStatus.value = createEmptyPurchaseStatus();
  eventReservations.value = [];
  eventReservationsLoading.value = false;
  eventReservationsError.value = "";
}

function createEmptyPurchaseStatus() {
  return {
    reserved: false,
    reservation: null,
    purchased: false,
    purchase: null,
    capacity: null,
    reservedTickets: 0,
    soldTickets: 0,
    availableTickets: null,
    remainingTickets: null,
    soldOut: false
  };
}

function normalizePurchaseStatus(status) {
  return {
    ...createEmptyPurchaseStatus(),
    ...(status || {})
  };
}

function isActiveReservation(reservation) {
  return Boolean(reservation?.id) && reservation.active !== false && reservation.state !== "CANCELLED";
}

function formatReservationPrice(value) {
  return formatEventMoney(value, locale.value, "--", t("common.labels.free"));
}

function getPurchaseAmountDue(purchase) {
  return purchase?.amountDue ?? purchase?.pricePaid;
}

function formatReservationDate(value) {
  if (!value) {
    return t("common.states.noData");
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return t("common.states.noData");
  }

  return new Intl.DateTimeFormat(locale.value, {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(date);
}

function getReservationUserName(user) {
  const name = [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
  return name || t("events.detail.organizer.userFallback", { id: user?.id ?? "" });
}

function toOptionalNumber(value) {
  if (value == null || value === "") {
    return null;
  }

  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
}

function hasEventStarted(item) {
  if (!item?.eventDate) {
    return false;
  }

  const today = toIsoDate(new Date());

  if (item.eventDate < today) {
    return true;
  }

  if (item.eventDate > today) {
    return false;
  }

  const referenceTime = item.startTime || "";
  const nowTime = new Date().toTimeString().slice(0, 5);
  return referenceTime ? referenceTime.slice(0, 5) <= nowTime : true;
}

function toIsoDate(date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}
</script>

<style scoped>
.event-detail-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at 16% 0%, rgba(29, 185, 84, 0.15), transparent 30%),
    radial-gradient(circle at 84% 12%, rgba(73, 149, 255, 0.1), transparent 26%),
    linear-gradient(180deg, #070707 0%, #101010 42%, #050505 100%);
  color: #ffffff;
}

.event-detail-shell {
  padding: 1rem 0 3rem;
}

.event-detail-shell .container {
  max-width: 1180px;
}

.detail-header {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

.detail-header__eyebrow,
.detail-section__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.detail-hero h1 {
  margin: 0.55rem 0 0;
  max-width: 980px;
  font-size: clamp(2.05rem, 4vw, 4.35rem);
  line-height: 1.02;
  letter-spacing: 0;
}

.detail-header__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
  margin-top: 1.15rem;
  color: #c8c8c8;
}

.detail-header__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 38px;
  padding: 0 0.85rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.085);
  font-size: 0.92rem;
}

.detail-header__meta i {
  color: #75df96;
}

.detail-header__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.detail-hero {
  display: grid;
  grid-template-columns: minmax(280px, 0.92fr) minmax(0, 1.08fr);
  overflow: hidden;
  min-height: 500px;
  border-radius: 24px;
  background:
    linear-gradient(135deg, rgba(29, 185, 84, 0.13), transparent 36%),
    linear-gradient(180deg, rgba(23, 23, 23, 0.98) 0%, rgba(13, 13, 13, 0.98) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.32);
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, 0.82fr);
  gap: 1.25rem;
  margin-top: 1.25rem;
  align-items: start;
}

.detail-main,
.detail-side {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.detail-section {
  overflow: hidden;
  border-radius: 18px;
  background:
    linear-gradient(180deg, rgba(20, 20, 20, 0.98) 0%, rgba(14, 14, 14, 0.98) 100%);
  border: 1px solid rgba(255, 255, 255, 0.09);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.24);
}

.detail-hero__media {
  position: relative;
  min-height: 500px;
  background: #0d0d0d;
}

.detail-hero__media::after {
  position: absolute;
  inset: 0;
  pointer-events: none;
  content: "";
  background:
    linear-gradient(90deg, transparent 60%, rgba(7, 7, 7, 0.4)),
    linear-gradient(180deg, transparent 62%, rgba(0, 0, 0, 0.38));
}

.detail-hero__media :deep(.app-image),
.detail-hero__media :deep(.app-image__img),
.detail-hero__media :deep(.app-image__placeholder) {
  width: 100%;
  height: 100%;
  min-height: inherit;
}

.detail-hero__media :deep(.app-image__img) {
  object-fit: cover;
}

.detail-hero__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
  padding: clamp(1.35rem, 3vw, 2.55rem);
}

.detail-section {
  padding: 1.15rem;
}

.detail-hero__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  margin-top: 1.1rem;
}

.event-badge {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.78rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
}

.event-badge--internal {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.event-badge--external {
  background: rgba(73, 149, 255, 0.16);
  color: #d9ecff;
}

.event-badge--published {
  background: rgba(29, 185, 84, 0.12);
  color: #dfffe9;
}

.event-badge--draft {
  background: rgba(255, 193, 7, 0.12);
  color: #f9dda1;
}

.event-badge--cancelled,
.event-badge--archived {
  background: rgba(220, 53, 69, 0.12);
  color: #ffc3cd;
}

.detail-copy {
  margin-top: 1.35rem;
  padding-top: 1.15rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.detail-copy h2,
.detail-section h2 {
  margin: 0.35rem 0 0;
  font-size: 1.3rem;
}

.detail-copy p {
  margin: 0.85rem 0 0;
  max-width: 72ch;
  color: #d1d1d1;
  line-height: 1.8;
}

.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: 1.35rem;
}

.detail-actions .btn,
.event-purchase-box .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.detail-info-grid,
.detail-associations {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.detail-info-card,
.detail-link-card {
  position: relative;
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  column-gap: 0.85rem;
  align-items: start;
  min-height: 118px;
  padding: 1rem;
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.052), rgba(255, 255, 255, 0.028));
  border: 1px solid rgba(255, 255, 255, 0.085);
  color: #ffffff;
  text-decoration: none;
}

.detail-info-card::before,
.detail-link-card::before {
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  content: "";
  background: rgba(29, 185, 84, 0.55);
  opacity: 0;
  transition: opacity 160ms ease;
}

.detail-info-card:hover::before,
.detail-link-card:hover::before {
  opacity: 1;
}

.detail-info-card i {
  grid-row: span 2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: rgba(29, 185, 84, 0.12);
  color: #9ef0b6;
  font-size: 1.05rem;
}

.detail-link-card {
  display: flex;
  flex-direction: column;
}

.detail-info-card span,
.detail-link-card span {
  display: block;
  color: #9e9e9e;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.detail-info-card strong,
.detail-link-card strong {
  display: block;
  margin-top: 0.35rem;
  line-height: 1.35;
  overflow-wrap: anywhere;
  min-width: 0;
  color: #f7f7f7;
  font-size: 1rem;
}

.detail-link-card .clickable-card__open-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  align-self: flex-end;
  width: 36px;
  height: 36px;
  margin-top: auto;
}

.detail-section--sticky {
  position: sticky;
  top: 90px;
}

.detail-summary {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border-color: rgba(29, 185, 84, 0.16);
  background:
    linear-gradient(145deg, rgba(29, 185, 84, 0.1), transparent 44%),
    linear-gradient(180deg, rgba(20, 20, 20, 0.98), rgba(13, 13, 13, 0.98));
}

.detail-summary__row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 0.85rem;
  align-items: start;
}

.detail-summary__row i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: rgba(29, 185, 84, 0.12);
  color: #9ef0b6;
}

.detail-summary__row span {
  display: block;
  color: #9e9e9e;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.detail-summary__row strong {
  display: block;
  margin-top: 0.2rem;
  line-height: 1.35;
  overflow-wrap: anywhere;
}

.event-purchase-box {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  padding: 1rem;
  border-radius: 14px;
  border: 1px solid rgba(29, 185, 84, 0.2);
  background:
    linear-gradient(145deg, rgba(29, 185, 84, 0.12), rgba(255, 255, 255, 0.035)),
    rgba(255, 255, 255, 0.035);
}

.event-purchase-box.is-saved {
  border-color: rgba(117, 223, 150, 0.38);
  background:
    linear-gradient(145deg, rgba(29, 185, 84, 0.18), rgba(255, 255, 255, 0.04)),
    rgba(255, 255, 255, 0.04);
}

.event-purchase-box__copy span {
  display: block;
  color: #75df96;
  font-size: 0.74rem;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.event-purchase-box__copy strong {
  display: block;
  margin-top: 0.25rem;
  color: #ffffff;
  font-size: 1.08rem;
  line-height: 1.25;
}

.event-purchase-box__copy p,
.event-purchase-box__payment-note,
.event-purchase-box__notice,
.event-purchase-box__error {
  margin: 0.35rem 0 0;
  color: #cfcfcf;
  font-size: 0.92rem;
  line-height: 1.45;
}

.event-purchase-box__payment-note {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin-top: -0.2rem;
  color: #c9f4d3;
}

.event-purchase-box__payment-note i {
  flex: 0 0 auto;
  margin-top: 0.05rem;
  color: #75df96;
}

.event-purchase-box__notice {
  color: #a8f5bd;
  font-weight: 700;
}

.event-purchase-box__error {
  color: #ffb3bd;
  font-weight: 700;
}

.organizer-reservations__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
  margin-top: 1rem;
}

.organizer-reservations__stats span,
.organizer-reservation,
.organizer-reservations__state,
.organizer-reservations__empty,
.organizer-reservations__error {
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.organizer-reservations__stats span {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 0.25rem;
  padding: 0.9rem;
  color: #9e9e9e;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.organizer-reservations__stats strong {
  color: #ffffff;
  font-size: 1rem;
  letter-spacing: 0;
  text-transform: none;
}

.organizer-reservations__list,
.organizer-reservations__state,
.organizer-reservations__empty,
.organizer-reservations__error {
  margin-top: 0.85rem;
}

.organizer-reservations__state,
.organizer-reservations__empty,
.organizer-reservations__error {
  padding: 0.9rem;
}

.organizer-reservations__state {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: #cfcfcf;
}

.organizer-reservations__error {
  color: #ffb3bd;
  font-weight: 700;
}

.organizer-reservations__empty {
  color: #b9b9b9;
}

.organizer-reservations__list {
  display: grid;
  gap: 0.6rem;
}

.organizer-reservation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.85rem;
  padding: 0.85rem;
}

.organizer-reservation div {
  min-width: 0;
}

.organizer-reservation strong {
  display: block;
  color: #ffffff;
  overflow-wrap: anywhere;
}

.organizer-reservation span {
  display: block;
  margin-top: 0.18rem;
  color: #9e9e9e;
  font-size: 0.86rem;
}

.detail-map-shell {
  position: relative;
  margin-top: 1rem;
  overflow: hidden;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.detail-map {
  height: 360px;
  overflow: hidden;
}

.detail-map-shell.has-fallback .detail-map {
  opacity: 0.18;
  filter: grayscale(1);
}

.detail-map-fallback {
  position: absolute;
  inset: 0;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.65rem;
  padding: 1.25rem;
  text-align: center;
  background:
    linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    radial-gradient(circle at center, rgba(29, 185, 84, 0.18), transparent 38%),
    #101010;
  background-size:
    32px 32px,
    32px 32px,
    auto,
    auto;
  color: #ffffff;
}

.detail-map-fallback i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: rgba(29, 185, 84, 0.16);
  color: #9ef0b6;
  font-size: 1.55rem;
}

.detail-map-fallback strong {
  max-width: 280px;
  line-height: 1.25;
}

.detail-map-fallback span {
  max-width: 320px;
  color: #c9c9c9;
  line-height: 1.45;
}

.detail-map-fallback small {
  color: #8f8f8f;
  font-weight: 700;
  letter-spacing: 0;
}

.map-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  min-height: 240px;
  margin-top: 1rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  color: #c2c2c2;
}

.map-empty i {
  color: #1db954;
  font-size: 2rem;
}

.page-notice,
.state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
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

@media (max-width: 1199.98px) {
  .detail-hero {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .detail-hero__media {
    min-height: 360px;
  }

  .detail-hero__media::after {
    background: linear-gradient(180deg, transparent 58%, rgba(0, 0, 0, 0.42));
  }

  .detail-layout {
    grid-template-columns: 1fr;
  }

  .detail-section--sticky {
    position: static;
  }
}

@media (max-width: 991.98px) {
  .detail-header {
    align-items: stretch;
  }

  .detail-header__actions {
    width: 100%;
  }

  .detail-header__actions .btn {
    flex: 1 1 180px;
  }
}

@media (max-width: 767.98px) {
  .detail-info-grid,
  .detail-associations,
  .organizer-reservations__stats {
    grid-template-columns: 1fr;
  }

  .detail-hero__body,
  .detail-section {
    padding: 1rem;
  }

  .detail-header__meta span {
    flex: 1 1 220px;
  }
}

@media (max-width: 575.98px) {
  .detail-header__actions .btn,
  .detail-actions .btn {
    width: 100%;
  }

  .detail-hero {
    border-radius: 14px;
  }

  .detail-hero__media {
    min-height: 280px;
  }

  .detail-map {
    height: 300px;
  }

  .detail-map-fallback {
    min-height: 300px;
  }
}
</style>
