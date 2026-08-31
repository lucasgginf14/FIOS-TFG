<template>
  <section class="detail-section availability-panel">
    <div class="detail-section__header">
      <div>
        <div class="detail-section__eyebrow">{{ eyebrow }}</div>
        <h2>{{ title }}</h2>
      </div>
      <button class="detail-section__link" type="button" @click="$emit('view-schedules')">
        {{ actionLabel }}
      </button>
    </div>

    <div class="availability-summary" :class="{ 'availability-summary--empty': !slots.length || errorMessage }">
      <span class="availability-summary__indicator" aria-hidden="true"></span>
      <div>
        <strong>{{ summaryLabel }}</strong>
        <span>{{ summaryDetailLabel }}</span>
      </div>
    </div>

    <div class="availability-layout">
      <div class="availability-controls">
        <div class="availability-layout__date">
          <label class="availability-label">{{ dateLabel }}</label>
          <div class="availability-date-shell">
            <i class="bi bi-calendar3" aria-hidden="true"></i>
            <input
              :value="selectedDate"
              :min="minDate"
              type="date"
              class="form-control availability-input"
              @input="$emit('update:selectedDate', $event.target.value)"
            />
          </div>
          <span class="availability-date-readable">{{ selectedDateReadable }}</span>
        </div>

        <div class="availability-shortcuts">
          <div class="availability-label">{{ quickDatesLabel }}</div>
          <div class="availability-day-list">
            <button
              v-for="preset in datePresets"
              :key="preset.value"
              type="button"
              class="availability-day"
              :class="{ 'is-active': preset.value === selectedDate }"
              :aria-pressed="preset.value === selectedDate"
              @click="$emit('update:selectedDate', preset.value)"
            >
              <strong>{{ preset.dayLabel }}</strong>
              <span>{{ preset.dateLabel }}</span>
            </button>
          </div>
        </div>
      </div>

      <div class="availability-layout__slots">
        <div v-if="loading" class="availability-state">{{ loadingLabel }}</div>
        <div v-else-if="errorMessage" class="availability-state availability-state--error">{{ errorMessage }}</div>
        <template v-else>
          <div v-if="!slots.length" class="availability-state">{{ emptyLabel }}</div>
          <div v-else class="availability-slots">
            <button
              v-for="slot in slots"
              :key="slot.id"
              class="availability-slot"
              :class="{ 'is-active': selectedSlotId === slot.id }"
              type="button"
              :aria-pressed="selectedSlotId === slot.id"
              @click="$emit('select-slot', slot)"
            >
              <span class="availability-slot__topline">
                <span class="availability-slot__range">
                  <i class="bi bi-clock" aria-hidden="true"></i>
                  <strong>{{ slot.rangeLabel }}</strong>
                </span>
                <span class="availability-slot__period">{{ slot.periodLabel }}</span>
              </span>
              <span class="availability-slot__meta">
                <span>
                  <i class="bi bi-hourglass-split" aria-hidden="true"></i>
                  {{ slot.durationLabel }}
                </span>
                <span v-if="slot.priceLabel">
                  <i class="bi bi-cash-stack" aria-hidden="true"></i>
                  {{ slot.priceLabel }}
                </span>
              </span>
              <span class="availability-slot__action">
                <i
                  :class="selectedSlotId === slot.id ? 'bi bi-check2-circle' : 'bi bi-arrow-right-circle'"
                  aria-hidden="true"
                ></i>
                {{ selectedSlotId === slot.id ? selectedSlotLabel : chooseSlotLabel }}
              </span>
            </button>
          </div>

          <div v-if="bookedSlots.length" class="availability-booked">
            <div class="availability-booked__heading">
              <strong>{{ bookedTitleLabel }}</strong>
              <span>{{ bookedDetailLabel }}</span>
            </div>

            <div class="availability-booked__list">
              <article v-for="slot in bookedSlots" :key="slot.id" class="availability-booked-slot">
                <span class="availability-booked-slot__range">
                  <i class="bi bi-calendar-x" aria-hidden="true"></i>
                  <strong>{{ slot.rangeLabel }}</strong>
                </span>
                <span class="availability-booked-slot__state">{{ slot.stateLabel }}</span>
              </article>
            </div>
          </div>
        </template>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  actionLabel: { type: String, default: "" },
  dateLabel: { type: String, default: "" },
  quickDatesLabel: { type: String, default: "" },
  loadingLabel: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  summaryLabel: { type: String, default: "" },
  summaryDetailLabel: { type: String, default: "" },
  selectedSlotLabel: { type: String, default: "" },
  chooseSlotLabel: { type: String, default: "" },
  bookedTitleLabel: { type: String, default: "" },
  bookedDetailLabel: { type: String, default: "" },
  todayLabel: { type: String, default: "" },
  tomorrowLabel: { type: String, default: "" },
  selectedDate: { type: String, default: "" },
  minDate: { type: String, default: "" },
  selectedSlotId: { type: String, default: "" },
  locale: { type: String, default: "es" },
  loading: { type: Boolean, default: false },
  slots: { type: Array, default: () => [] },
  bookedSlots: { type: Array, default: () => [] }
});

defineEmits(["update:selectedDate", "select-slot", "view-schedules"]);

const datePresets = computed(() => {
  const today = createLocalDate(props.minDate || toIsoDate(new Date()));

  return Array.from({ length: 7 }, (_, index) => {
    const date = new Date(today);
    date.setDate(today.getDate() + index);
    const value = toIsoDate(date);

    return {
      value,
      dayLabel: getPresetDayLabel(date, index),
      dateLabel: new Intl.DateTimeFormat(props.locale, {
        day: "2-digit",
        month: "short"
      }).format(date)
    };
  });
});

const selectedDateReadable = computed(() => {
  if (!props.selectedDate) {
    return "";
  }

  return new Intl.DateTimeFormat(props.locale, {
    weekday: "long",
    day: "2-digit",
    month: "long"
  }).format(createLocalDate(props.selectedDate));
});

function getPresetDayLabel(date, index) {
  if (index === 0 && props.todayLabel) {
    return props.todayLabel;
  }

  if (index === 1 && props.tomorrowLabel) {
    return props.tomorrowLabel;
  }

  return new Intl.DateTimeFormat(props.locale, { weekday: "short" }).format(date);
}

function createLocalDate(value) {
  if (!value) {
    return new Date();
  }

  const [year, month, day] = value.split("-").map(Number);
  return new Date(year, (month || 1) - 1, day || 1);
}

function toIsoDate(date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0")
  ].join("-");
}
</script>

<style scoped>
.detail-section {
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}
.detail-section__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
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
  font-size: 1.35rem;
  font-weight: 700;
}
.detail-section__link {
  min-height: 38px;
  padding: 0 0.85rem;
  border: 0;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
  font-weight: 700;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}
.detail-section__link:hover {
  background: rgba(29, 185, 84, 0.22);
  transform: translateY(-1px);
}

.availability-summary {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  min-height: 64px;
  margin-bottom: 1rem;
  padding: 0.85rem 1rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.16), transparent 34%),
    rgba(29, 185, 84, 0.07);
}

.availability-summary--empty {
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.availability-summary__indicator {
  flex: 0 0 auto;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #1db954;
  box-shadow: 0 0 0 6px rgba(29, 185, 84, 0.14);
}

.availability-summary--empty .availability-summary__indicator {
  background: #7a7a7a;
  box-shadow: 0 0 0 6px rgba(255, 255, 255, 0.06);
}

.availability-summary div {
  display: grid;
  gap: 0.15rem;
  min-width: 0;
}

.availability-summary strong {
  color: #ffffff;
  font-size: 0.98rem;
}

.availability-summary span:last-child {
  color: #b8b8b8;
  font-size: 0.9rem;
  line-height: 1.45;
}

.availability-layout {
  display: grid;
  gap: 1rem;
}

.availability-controls {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 1rem;
}
.availability-label {
  display: block;
  margin-bottom: 0.55rem;
  color: #9e9e9e;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0;
}

.availability-date-shell {
  position: relative;
}

.availability-date-shell i {
  position: absolute;
  left: 0.9rem;
  top: 50%;
  color: #1db954;
  pointer-events: none;
  transform: translateY(-50%);
}

.availability-input {
  min-height: 50px;
  padding: 0.65rem 0.85rem 0.65rem 2.45rem;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}
.availability-input:focus {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 0.25rem rgba(29, 185, 84, 0.14);
  border-color: rgba(29, 185, 84, 0.65);
}

.availability-date-readable {
  display: block;
  margin-top: 0.55rem;
  color: #bdbdbd;
  font-size: 0.88rem;
  line-height: 1.35;
}

.availability-day-list {
  display: grid;
  grid-template-columns: repeat(7, minmax(74px, 1fr));
  gap: 0.5rem;
}

.availability-day {
  display: grid;
  gap: 0.2rem;
  min-height: 58px;
  padding: 0.55rem 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  text-align: center;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}

.availability-day:hover,
.availability-day.is-active {
  border-color: rgba(29, 185, 84, 0.45);
  background: rgba(29, 185, 84, 0.12);
}

.availability-day:hover {
  transform: translateY(-1px);
}

.availability-day strong {
  font-size: 0.84rem;
  line-height: 1.1;
  text-transform: capitalize;
}

.availability-day span {
  color: #bdbdbd;
  font-size: 0.75rem;
  line-height: 1.1;
  white-space: nowrap;
}

.availability-slots {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}
.availability-slot {
  display: grid;
  gap: 0.7rem;
  min-height: 118px;
  padding: 0.9rem 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.055), rgba(255, 255, 255, 0.025));
  color: #ffffff;
  text-align: left;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}
.availability-slot:hover {
  border-color: rgba(29, 185, 84, 0.35);
  background: rgba(29, 185, 84, 0.08);
  transform: translateY(-1px);
}

.availability-slot__topline,
.availability-slot__meta,
.availability-slot__action,
.availability-slot__range {
  display: flex;
  align-items: center;
}

.availability-slot__topline {
  justify-content: space-between;
  gap: 0.75rem;
}

.availability-slot__range {
  gap: 0.45rem;
  min-width: 0;
}

.availability-slot__range i {
  color: #1db954;
}

.availability-slot__range strong {
  font-size: 0.98rem;
}

.availability-slot__period {
  flex: 0 0 auto;
  padding: 0.24rem 0.55rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.12);
  color: #dfffe9;
  font-size: 0.72rem;
  font-weight: 800;
}

.availability-slot__meta {
  flex-wrap: wrap;
  gap: 0.45rem;
}

.availability-slot__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  min-height: 30px;
  padding: 0 0.55rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.05);
  color: #9e9e9e;
  font-size: 0.82rem;
  font-weight: 700;
}

.availability-slot__meta i {
  color: #1db954;
}

.availability-slot__action {
  justify-content: space-between;
  align-self: end;
  gap: 0.5rem;
  color: #dfffe9;
  font-size: 0.86rem;
  font-weight: 800;
}
.availability-slot.is-active {
  border-color: rgba(29, 185, 84, 0.55);
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.18), transparent 44%),
    rgba(29, 185, 84, 0.12);
  box-shadow: inset 4px 0 0 #1db954;
}

.availability-booked {
  display: grid;
  gap: 0.7rem;
  margin-top: 0.85rem;
  padding: 0.9rem;
  border: 1px solid rgba(255, 193, 7, 0.16);
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(255, 193, 7, 0.1), transparent 42%),
    rgba(255, 255, 255, 0.035);
}

.availability-booked__heading {
  display: grid;
  gap: 0.18rem;
}

.availability-booked__heading strong {
  color: #f7f7f7;
  font-size: 0.95rem;
}

.availability-booked__heading span {
  color: #a9a9a9;
  font-size: 0.84rem;
  line-height: 1.4;
}

.availability-booked__list {
  display: grid;
  gap: 0.5rem;
}

.availability-booked-slot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  min-height: 48px;
  padding: 0.65rem 0.75rem;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 14px;
  background: rgba(8, 8, 8, 0.28);
}

.availability-booked-slot__range {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 0;
  color: #ffffff;
}

.availability-booked-slot__range i {
  color: #f4d06f;
}

.availability-booked-slot__range strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.availability-booked-slot__state {
  flex: 0 0 auto;
  display: inline-flex;
  align-items: center;
  min-height: 26px;
  padding: 0 0.58rem;
  border-radius: 999px;
  background: rgba(255, 193, 7, 0.12);
  color: #f4d06f;
  font-size: 0.75rem;
  font-weight: 800;
}

.availability-state {
  padding: 1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #d1d1d1;
}
.availability-state--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}
@media (max-width: 991.98px) {
  .availability-controls {
    grid-template-columns: 1fr;
  }

  .availability-day-list {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .availability-slots {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 767.98px) {
  .detail-section__header {
    flex-direction: column;
  }
}

@media (max-width: 575.98px) {
  .availability-day-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .availability-slot__topline,
  .availability-slot__action,
  .availability-booked-slot {
    align-items: flex-start;
    flex-direction: column;
  }

  .availability-booked-slot__state {
    align-self: flex-start;
  }
}
</style>
