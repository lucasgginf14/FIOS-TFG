<template>
  <section class="booking-card">
    <div class="booking-card__header">
      <div>
        <div class="booking-card__eyebrow">{{ eyebrow }}</div>
        <h2>{{ title }}</h2>
      </div>
    </div>

    <div class="booking-card__highlights">
      <div class="booking-card__price">{{ priceLabel }}</div>
      <div class="booking-card__rating">
        <i class="bi bi-star-fill"></i>
        <span>{{ ratingLabel }}</span>
      </div>
    </div>

    <div class="booking-card__fields">
      <div class="booking-field">
        <label>{{ dateLabel }}</label>
        <input :value="sessionDate" type="date" class="form-control booking-input" @input="$emit('update:sessionDate', $event.target.value)" />
      </div>

      <div class="booking-field">
        <label>{{ startTimeLabel }}</label>
        <AppSelect
          :model-value="startTime"
          :options="startTimeSelectOptions"
          :label="startTimeLabel"
          :disabled="!startTimeOptions.length"
          @update:model-value="$emit('update:startTime', $event)"
        />
      </div>

      <div class="booking-field">
        <label>{{ durationLabel }}</label>
        <AppSelect
          :model-value="durationMinutes"
          :options="durationSelectOptions"
          :label="durationLabel"
          :disabled="!durationOptions.length"
          @update:model-value="$emit('update:durationMinutes', Number($event))"
        />
      </div>

      <div class="booking-field">
        <label>{{ attendeesLabel }}</label>
        <div class="attendees-stepper">
          <button type="button" :disabled="attendeesCount <= 1" @click="$emit('update:attendeesCount', attendeesCount - 1)"><i class="bi bi-dash"></i></button>
          <span>{{ attendeesCount }}</span>
          <button type="button" :disabled="attendeesCount >= maxAttendees" @click="$emit('update:attendeesCount', attendeesCount + 1)"><i class="bi bi-plus"></i></button>
        </div>
      </div>
    </div>

    <div class="booking-summary">
      <div class="booking-summary__row"><span>{{ subtotalLabel }}</span><strong>{{ subtotalValue }}</strong></div>
      <div class="booking-summary__row"><span>{{ totalLabel }}</span><strong>{{ totalValue }}</strong></div>
    </div>

    <div v-if="errorMessage" class="booking-banner booking-banner--error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="booking-banner booking-banner--success">{{ successMessage }}</div>

    <button class="booking-card__submit" type="button" :disabled="loading" @click="$emit('reserve')">
      {{ loading ? loadingLabel : submitLabel }}
    </button>
  </section>
</template>

<script setup>
import { computed } from "vue";
import AppSelect from "@/common/components/AppSelect.vue";

const props = defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  priceLabel: { type: String, default: "" },
  ratingLabel: { type: String, default: "" },
  dateLabel: { type: String, default: "" },
  startTimeLabel: { type: String, default: "" },
  durationLabel: { type: String, default: "" },
  attendeesLabel: { type: String, default: "" },
  subtotalLabel: { type: String, default: "" },
  totalLabel: { type: String, default: "" },
  submitLabel: { type: String, default: "" },
  loadingLabel: { type: String, default: "" },
  placeholderLabel: { type: String, default: "" },
  sessionDate: { type: String, default: "" },
  startTime: { type: String, default: "" },
  startTimeOptions: { type: Array, default: () => [] },
  durationMinutes: { type: Number, default: 0 },
  attendeesCount: { type: Number, default: 1 },
  maxAttendees: { type: Number, default: 1 },
  durationOptions: { type: Array, default: () => [] },
  subtotalValue: { type: String, default: "" },
  totalValue: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  successMessage: { type: String, default: "" },
  loading: { type: Boolean, default: false }
});

defineEmits(["update:sessionDate", "update:startTime", "update:durationMinutes", "update:attendeesCount", "reserve"]);

const startTimeSelectOptions = computed(() => {
  if (!props.startTimeOptions.length) {
    return [{ value: "", label: props.placeholderLabel }];
  }

  return props.startTimeOptions.map((option) => ({
    value: option.value ?? option,
    label: option.label ?? option
  }));
});

const durationSelectOptions = computed(() => {
  if (!props.durationOptions.length) {
    return [{ value: 0, label: props.placeholderLabel }];
  }

  return props.durationOptions.map((option) => ({
    value: Number(option.value),
    label: option.label
  }));
});
</script>

<style scoped>
.booking-card {
  position: relative;
  padding: 1.1rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #ffffff;
  box-shadow: 0 28px 70px rgba(0, 0, 0, 0.3);
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.booking-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.85rem;
}

.booking-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.booking-card h2 {
  margin: 0.25rem 0 0;
  color: #ffffff;
  font-size: clamp(1.25rem, 2vw, 1.5rem);
  font-weight: 700;
}

.booking-card__highlights {
  display: grid;
  grid-template-columns: minmax(0, 1.18fr) minmax(0, 0.82fr);
  gap: 0.55rem;
  margin-bottom: 0.85rem;
}

.booking-card__price,
.booking-card__rating {
  min-height: 50px;
  padding: 0.7rem 0.78rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(29, 185, 84, 0.12), rgba(29, 185, 84, 0.07));
}

.booking-card__price {
  display: flex;
  align-items: center;
  color: #ffffff;
  font-size: clamp(0.95rem, 1.2vw, 1.05rem);
  font-weight: 700;
  line-height: 1.2;
}

.booking-card__rating {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  color: #ffffff;
  font-size: 0.9rem;
  font-weight: 700;
}

.booking-card__rating i {
  color: #1db954;
}

.booking-card__fields {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.7rem;
}

.booking-field label {
  display: block;
  margin-bottom: 0.35rem;
  color: #9e9e9e;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.booking-input,
.booking-static {
  min-height: 44px;
  padding: 0.55rem 0.72rem;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.055);
  color: #ffffff;
}

.booking-input:focus {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 0.25rem rgba(29, 185, 84, 0.14);
  border-color: rgba(29, 185, 84, 0.65);
}

.booking-static {
  display: flex;
  align-items: center;
  padding: 0 0.78rem;
}

.attendees-stepper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.8rem;
  width: 100%;
  min-height: 44px;
  padding: 0 0.45rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.055);
}

.attendees-stepper button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: 1px solid rgba(29, 185, 84, 0.22);
  border-radius: 50%;
  background: rgba(29, 185, 84, 0.1);
  color: #ffffff;
}

.attendees-stepper button:disabled {
  opacity: 0.45;
}

.attendees-stepper span {
  min-width: 24px;
  text-align: center;
  font-weight: 800;
}

.booking-summary {
  margin-top: 0.85rem;
  padding: 0.72rem 0.78rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.055);
}

.booking-summary__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  color: #cfcfcf;
}

.booking-summary__row + .booking-summary__row {
  margin-top: 0.45rem;
  padding-top: 0.45rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.booking-summary__row strong {
  color: #ffffff;
  font-size: 1.02rem;
}

.booking-banner {
  margin-top: 0.85rem;
  padding: 0.75rem 0.85rem;
  border-radius: 16px;
  font-size: 0.92rem;
}

.booking-banner--error {
  background: rgba(220, 53, 69, 0.08);
  border: 1px solid rgba(220, 53, 69, 0.18);
  color: #ffb3bd;
}

.booking-banner--success {
  background: rgba(29, 185, 84, 0.08);
  border: 1px solid rgba(29, 185, 84, 0.18);
  color: #dfffe9;
}

.booking-card__submit {
  width: 100%;
  min-height: 48px;
  margin-top: 0.85rem;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  font-weight: 700;
  transition:
    filter 0.18s ease,
    transform 0.18s ease;
}

.booking-card__submit:hover:not(:disabled) {
  filter: brightness(1.05);
  transform: translateY(-1px);
}

.booking-card__submit:disabled {
  cursor: wait;
  opacity: 0.72;
}

@media (max-width: 1199.98px) {
  .booking-card {
    padding: 1.25rem;
  }

  .booking-card__fields {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .booking-card__fields {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 575.98px) {
  .booking-card__highlights,
  .booking-card__fields {
    grid-template-columns: 1fr;
  }
}
</style>
