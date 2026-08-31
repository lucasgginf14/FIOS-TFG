<template>
  <div class="tickets-page">
    <section class="tickets-shell">
      <div class="container py-5">
        <header class="tickets-header">
          <div class="tickets-header__copy">
            <h1>{{ t("events.tickets.header.title") }}</h1>
            <p>{{ t("events.tickets.header.subtitle") }}</p>
          </div>

          <RouterLink class="btn btn-success tickets-header__cta" :to="{ name: 'EventList' }">
            {{ t("events.tickets.header.explore") }}
          </RouterLink>
        </header>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("events.tickets.states.loading") }}</p>
        </div>

        <div v-else-if="errorMessage" class="state-card state-card--error">
          <strong>{{ t("events.tickets.states.errorTitle") }}</strong>
          <p>{{ errorMessage }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadTickets">
            {{ t("events.tickets.actions.retry") }}
          </button>
        </div>

        <template v-else>
          <section class="tickets-list-section">
            <div class="tickets-list-section__header">
              <div>
                <span class="tickets-header__eyebrow">{{ t("events.tickets.list.eyebrow") }}</span>
                <h2>{{ t("events.tickets.list.title") }}</h2>
              </div>
              <span class="tickets-list-section__count">
                {{ t("events.tickets.list.total", { count: tickets.length }) }}
              </span>
            </div>

            <div v-if="tickets.length" class="tickets-list">
              <article v-for="ticket in tickets" :key="ticket.id" class="ticket-card">
                <RouterLink
                  class="ticket-card__media"
                  :to="buildEventRoute(ticket.event)"
                  :aria-label="t('events.tickets.actions.viewEventFor', { title: ticket.title })"
                >
                  <AppImage
                    :src="ticket.image"
                    :alt="ticket.title"
                    :fallback-src="eventPlaceholder"
                    :fallback-label="t('events.cards.imageFallback')"
                    icon-class="bi bi-ticket-perforated"
                  />
                </RouterLink>

                <div class="ticket-card__body">
                  <div class="ticket-card__badges">
                    <span class="ticket-badge ticket-badge--success">
                      <i class="bi bi-ticket-perforated"></i>
                      {{ ticket.cancelled ? t("events.tickets.card.cancelledLabel") : t("events.tickets.card.ticketLabel") }}
                    </span>
                    <span class="ticket-badge">
                      {{ t("events.tickets.card.purchasedAt", { date: ticket.purchasedAtLabel }) }}
                    </span>
                  </div>

                  <h3>{{ ticket.title }}</h3>

                  <dl class="ticket-card__details">
                    <div>
                      <dt>{{ t("events.tickets.card.date") }}</dt>
                      <dd>{{ ticket.dateLabel }}</dd>
                    </div>
                    <div>
                      <dt>{{ t("events.tickets.card.time") }}</dt>
                      <dd>{{ ticket.timeLabel }}</dd>
                    </div>
                    <div>
                      <dt>{{ t("events.tickets.card.place") }}</dt>
                      <dd>{{ ticket.locationLabel }}</dd>
                    </div>
                    <div>
                      <dt>{{ t("events.tickets.card.amountDue") }}</dt>
                      <dd>{{ ticket.priceLabel }}</dd>
                    </div>
                    <div v-if="ticket.cancelled">
                      <dt>{{ t("events.tickets.card.cancelledAt") }}</dt>
                      <dd>{{ ticket.cancelledAtLabel }}</dd>
                    </div>
                  </dl>

                  <p class="ticket-card__payment-note">
                    <i class="bi bi-info-circle" aria-hidden="true"></i>
                    {{ t("events.tickets.card.paymentNote") }}
                  </p>

                  <div class="ticket-card__footer">
                    <span>{{ ticket.statusLabel }}</span>
                    <RouterLink class="btn btn-outline-light" :to="buildEventRoute(ticket.event)">
                      {{ t("events.tickets.actions.viewEvent") }}
                    </RouterLink>
                  </div>
                </div>
              </article>
            </div>

            <EventEmptyState
              v-else
              :title="t('events.tickets.empty.title')"
              :text="t('events.tickets.empty.text')"
              :action-label="t('events.tickets.empty.action')"
              @explore="goToEvents"
            />
          </section>
        </template>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { RouterLink, useRouter } from "vue-router";
import AppImage from "@/common/components/AppImage.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import EventPurchaseRepository from "@/repositories/EventPurchaseRepository";
import EventEmptyState from "../components/EventEmptyState.vue";
import {
  formatEventDate,
  formatEventMoney,
  formatEventTimeRange,
  getEventImage,
  getEventLocationLabel,
  toEventDateTime
} from "../eventUtils";

const router = useRouter();
const { locale, t } = useI18n();

const loading = ref(true);
const errorMessage = ref("");
const purchases = ref([]);

const tickets = computed(() =>
  purchases.value
    .map(mapPurchaseToTicket)
    .filter((ticket) => ticket.event?.id && !ticket.cancelled)
    .sort(sortTickets)
);

onMounted(loadTickets);

async function loadTickets() {
  loading.value = true;
  errorMessage.value = "";

  try {
    purchases.value = await EventPurchaseRepository.getMine();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "events.tickets.states.error");
  } finally {
    loading.value = false;
  }
}

function mapPurchaseToTicket(purchase) {
  const event = purchase.event ?? {};
  const timeLabel = formatEventTimeRange(event);
  const locationLabel = getEventLocationLabel(event);
  const eventDate = event.eventDate || "";
  const upcoming = eventDate >= getTodayString();
  const reservedAt = purchase.reservedAt || purchase.purchasedAt;
  const cancelled = purchase.state === "CANCELLED" || purchase.active === false;
  const amountDue = getPurchaseAmountDue(purchase);

  return {
    id: purchase.id ?? `${event.id}-${reservedAt}`,
    event,
    title: event.title || t("events.tickets.card.untitled"),
    image: getEventImage(event),
    eventDate,
    upcoming,
    cancelled,
    amountDue,
    dateLabel: formatEventDate(eventDate, locale.value),
    timeLabel: timeLabel === "--" ? t("events.tickets.card.noTime") : timeLabel,
    locationLabel: locationLabel || t("events.tickets.card.locationFallback"),
    priceLabel: formatEventMoney(
      amountDue,
      locale.value,
      t("common.labels.onRequest"),
      t("common.labels.free")
    ),
    purchasedAtLabel: formatPurchaseDate(reservedAt),
    cancelledAtLabel: formatPurchaseDate(purchase.cancelledAt),
    statusLabel: cancelled
      ? t("events.tickets.card.cancelled")
      : upcoming
        ? t("events.tickets.card.upcoming")
        : t("events.tickets.card.past")
  };
}

function getPurchaseAmountDue(purchase) {
  return purchase?.amountDue ?? purchase?.pricePaid;
}

function sortTickets(left, right) {
  if (left.upcoming !== right.upcoming) {
    return left.upcoming ? -1 : 1;
  }

  const direction = left.upcoming ? 1 : -1;
  return (toEventDateTime(left.event) - toEventDateTime(right.event)) * direction;
}

function formatPurchaseDate(value) {
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

function buildEventRoute(event) {
  return {
    name: "EventDetail",
    params: { id: event.id }
  };
}

function goToEvents() {
  router.push({ name: "EventList" });
}

function getTodayString() {
  const date = new Date();
  const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}
</script>

<style scoped>
.tickets-page {
  min-height: calc(100vh - 72px);
  background:
    linear-gradient(180deg, #050505 0%, #0c0f0d 32%, #050505 100%);
  color: #ffffff;
}

.tickets-shell {
  padding-bottom: 3rem;
}

.tickets-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.tickets-header__copy {
  max-width: 760px;
  min-width: 0;
}

.tickets-header__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.tickets-header h1 {
  margin: 0.4rem 0 0.6rem;
  font-size: 2.55rem;
  line-height: 1.08;
  letter-spacing: 0;
}

.tickets-header p {
  margin: 0;
  color: #b6b6b6;
}

.tickets-header__cta {
  flex: 0 0 auto;
}

.tickets-list-section {
  margin-top: 1.25rem;
}

.tickets-list-section__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.tickets-list-section__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
}

.tickets-list-section__count {
  color: #b9b9b9;
  font-weight: 700;
}

.tickets-list {
  display: grid;
  gap: 1rem;
}

.ticket-card {
  display: grid;
  grid-template-columns: minmax(210px, 280px) minmax(0, 1fr);
  min-width: 0;
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(13, 15, 14, 0.98) 100%);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.ticket-card__media {
  height: clamp(220px, 24vw, 260px);
  min-height: 0;
  overflow: hidden;
  background: #0d0d0d;
}

.ticket-card__media :deep(.app-image),
.ticket-card__media :deep(.app-image__img),
.ticket-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.ticket-card__body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-width: 0;
  padding: 1.2rem;
}

.ticket-card__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
}

.ticket-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  min-height: 30px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
}

.ticket-badge--success {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.ticket-card h3 {
  margin: 0;
  font-size: 1.35rem;
  line-height: 1.25;
  overflow-wrap: anywhere;
}

.ticket-card__details {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.75rem;
  margin: 0;
}

.ticket-card__details div {
  min-width: 0;
  padding: 0.85rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.045);
}

.ticket-card__details dt {
  margin-bottom: 0.35rem;
  color: #8f8f8f;
  font-size: 0.72rem;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.ticket-card__details dd {
  margin: 0;
  color: #ffffff;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.ticket-card__payment-note {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin: -0.2rem 0 0;
  color: #c9f4d3;
  font-size: 0.92rem;
  line-height: 1.45;
}

.ticket-card__payment-note i {
  flex: 0 0 auto;
  margin-top: 0.05rem;
  color: #75df96;
}

.ticket-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.8rem;
  margin-top: auto;
}

.ticket-card__footer span {
  color: #b9b9b9;
  font-weight: 700;
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  margin-top: 1.35rem;
  padding: 1.25rem;
  text-align: center;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

@media (max-width: 1199.98px) {
  .ticket-card__details {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .tickets-header,
  .tickets-list-section__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .ticket-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 575.98px) {
  .tickets-header h1 {
    font-size: 2.1rem;
  }

  .tickets-header__cta,
  .ticket-card__footer .btn {
    width: 100%;
  }

  .ticket-card__details {
    grid-template-columns: 1fr;
  }
}
</style>
