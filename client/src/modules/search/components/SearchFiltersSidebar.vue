<template>
  <section class="filters-shell">
    <button class="filters-shell__toggle d-lg-none" type="button" @click="expanded = !expanded">
      <span>{{ t("search.filters.title") }}</span>
      <i :class="expanded ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
    </button>

    <div class="filters-shell__body" :class="{ 'is-open': expanded }">
      <div class="filters-card">
        <div class="filters-card__header">
          <div>
            <div class="filters-card__eyebrow">{{ t("search.filters.eyebrow") }}</div>
            <h2>{{ t("search.filters.title") }}</h2>
          </div>
          <button class="filters-card__clear" type="button" @click="handleClear">
            {{ t("search.filters.clear") }}
          </button>
        </div>

        <div class="filters-section">
          <h3>{{ t("search.filters.dateTitle") }}</h3>
          <div class="filters-pill-grid">
            <button
              v-for="preset in datePresets"
              :key="preset.id"
              class="filters-pill"
              :class="{ 'is-active': localFilters.datePreset === preset.id }"
              type="button"
              @click="toggleDatePreset(preset.id)"
            >
              {{ preset.label }}
            </button>
          </div>
          <input
            v-model="localFilters.date"
            type="date"
            class="form-control filters-input"
            @input="localFilters.datePreset = ''"
          />
        </div>

        <div class="filters-section">
          <h3>{{ t("search.filters.spaceTypeTitle") }}</h3>
          <div class="filters-pill-grid">
            <button
              v-for="option in spaceTypeOptions"
              :key="option.id"
              class="filters-pill filters-pill--wide"
              :class="{ 'is-active': localFilters.spaceType === option.id }"
              type="button"
              @click="localFilters.spaceType = localFilters.spaceType === option.id ? '' : option.id"
            >
              {{ option.label }}
            </button>
          </div>
        </div>

        <div class="filters-section">
          <h3>{{ t("search.filters.genreTitle") }}</h3>
          <div class="filters-pill-grid">
            <button
              v-for="option in genreOptions"
              :key="option"
              class="filters-pill"
              :class="{ 'is-active': localFilters.musicalGenre === option }"
              type="button"
              @click="localFilters.musicalGenre = localFilters.musicalGenre === option ? '' : option"
            >
              {{ option }}
            </button>
          </div>
        </div>

        <div class="filters-section filters-section--control">
          <div class="filters-control-heading">
            <span class="filters-control-heading__icon" aria-hidden="true">
              <i class="bi bi-currency-euro"></i>
            </span>
            <h3>{{ t("search.filters.budgetTitle") }}</h3>
          </div>
          <div class="filters-control-scale">
            <span>0 EUR</span>
            <strong>{{ budgetScaleValue }}</strong>
            <span>150 EUR</span>
          </div>
          <input
            v-model.number="localFilters.maxBudget"
            type="range"
            min="0"
            max="150"
            step="5"
            class="form-range filters-range filters-range--compact"
          />
        </div>

        <div class="filters-section filters-section--control">
          <div class="filters-control-heading">
            <span class="filters-control-heading__icon" aria-hidden="true">
              <i class="bi bi-person"></i>
            </span>
            <h3>{{ t("search.filters.peopleTitle") }}</h3>
          </div>
          <div class="people-stepper">
            <button
              class="people-stepper__button"
              type="button"
              :disabled="localFilters.peopleCount <= 0"
              @click="changePeopleCount(-1)"
            >
              <i class="bi bi-dash"></i>
            </button>
            <span class="people-stepper__value">{{ peopleScaleValue }}</span>
            <button class="people-stepper__button" type="button" @click="changePeopleCount(1)">
              <i class="bi bi-plus"></i>
            </button>
          </div>
        </div>

        <div class="filters-section">
          <h3>{{ t("search.filters.timeTitle") }}</h3>
          <div class="filters-pill-grid">
            <button
              v-for="option in timeOptions"
              :key="option.id"
              class="filters-pill"
              :class="{ 'is-active': localFilters.timeOfDay === option.id }"
              type="button"
              @click="localFilters.timeOfDay = localFilters.timeOfDay === option.id ? '' : option.id"
            >
              {{ option.label }}
            </button>
          </div>
        </div>

        <button class="btn filters-card__apply" type="button" :disabled="loading" @click="handleApply">
          {{ loading ? t("common.actions.loading") : t("search.filters.apply") }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { ALL_RESULT_TYPES, createDefaultSearchFilters, normalizeSearchFilters } from "../searchFilters";

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => createDefaultSearchFilters()
  },
  counts: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["apply", "clear"]);
const { t } = useI18n();

const expanded = ref(false);
const localFilters = reactive(normalizeSearchFilters(props.modelValue));

watch(
  () => props.modelValue,
  (value) => {
    Object.assign(localFilters, normalizeSearchFilters(value));
  },
  { deep: true, immediate: true }
);

const datePresets = computed(() => [
  { id: "today", label: t("search.filters.datePresets.today") },
  { id: "tomorrow", label: t("search.filters.datePresets.tomorrow") },
  { id: "weekend", label: t("search.filters.datePresets.weekend") },
  { id: "week", label: t("search.filters.datePresets.week") }
]);

const spaceTypeOptions = computed(() => [
  { id: "REHEARSAL_ROOM", label: t("search.filters.spaceTypes.rehearsal") },
  { id: "RECORDING_STUDIO", label: t("search.filters.spaceTypes.recording") },
  { id: "CONCERT_HALL", label: t("search.filters.spaceTypes.performance") },
  { id: "MULTIPURPOSE", label: t("search.filters.spaceTypes.barStage") },
  { id: "OTHER", label: t("search.filters.spaceTypes.multiuse") }
]);

const genreOptions = computed(() => [
  "Rock",
  "Jazz",
  "Folk",
  "Electronica",
  "Clasica",
  "Pop",
  "Metal",
  "Indie",
  "Hip-hop"
]);

const timeOptions = computed(() => [
  { id: "morning", label: t("search.filters.timeSlots.morning") },
  { id: "afternoon", label: t("search.filters.timeSlots.afternoon") },
  { id: "night", label: t("search.filters.timeSlots.night") },
  { id: "allday", label: t("search.filters.timeSlots.allday") }
]);

const budgetScaleValue = computed(() =>
  localFilters.maxBudget > 0 ? `${localFilters.maxBudget} EUR` : t("search.filters.anyBudget")
);

const peopleScaleValue = computed(() =>
  localFilters.peopleCount > 0
    ? t("search.filters.peopleValue", { value: localFilters.peopleCount })
    : t("search.filters.anyPeople")
);

function toggleDatePreset(id) {
  localFilters.datePreset = localFilters.datePreset === id ? "" : id;

  if (localFilters.datePreset) {
    localFilters.date = "";
  }
}

function changePeopleCount(delta) {
  localFilters.peopleCount = Math.max(0, Number(localFilters.peopleCount || 0) + delta);
}

function handleApply() {
  emit("apply", normalizeSearchFilters({
    ...localFilters,
    resultTypes: [...ALL_RESULT_TYPES]
  }));
}

function handleClear() {
  const reset = createDefaultSearchFilters();
  Object.assign(localFilters, reset);
  emit("clear");
}
</script>

<style scoped>
.filters-shell {
  position: sticky;
  top: 96px;
  width: 100%;
}

.filters-shell__toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 52px;
  padding: 0 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  font-weight: 700;
}

.filters-shell__body {
  width: 100%;
}

.filters-card {
  padding: 1.05rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 34%),
    linear-gradient(180deg, #101010 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.filters-card__header,
.filters-inline-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.filters-card__header {
  margin-bottom: 0.95rem;
  padding: 0 0 0.9rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.filters-card__eyebrow {
  color: #1db954;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.filters-card h2,
.filters-card h3 {
  margin: 0;
  color: #ffffff;
}

.filters-card h2 {
  font-size: 1.2rem;
  font-weight: 700;
}

.filters-card h3 {
  font-size: 0.96rem;
  font-weight: 700;
}

.filters-card__clear {
  min-height: 40px;
  padding: 0 0.9rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
  color: #a7a7a7;
  font-size: 0.9rem;
  font-weight: 600;
}

.filters-card__clear:hover {
  color: #ffffff;
  border-color: rgba(29, 185, 84, 0.28);
  background: rgba(29, 185, 84, 0.08);
}

.filters-section {
  min-width: 0;
  padding: 0.95rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.032);
  border: 1px solid rgba(255, 255, 255, 0.075);
}

.filters-section + .filters-section {
  margin-top: 0.75rem;
  padding-top: 0.95rem;
}

.filters-section--control {
  padding: 0.95rem 1rem;
}

.filters-control-heading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filters-control-heading__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  color: #f2f2f2;
  font-size: 1.15rem;
}

.filters-control-heading h3 {
  flex: 1;
  font-size: 1.02rem;
}

.filters-control-scale {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.7rem;
  margin-top: 0.7rem;
  color: #d5d5d5;
  font-size: 0.78rem;
  font-weight: 700;
}

.filters-control-scale strong {
  color: #ffffff;
}

.filters-pill-grid,
.filters-stack {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  margin-top: 0.9rem;
}

.filters-stack {
  flex-direction: column;
}

.filters-pill,
.filters-option {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
  color: #d6d6d6;
  transition:
    border-color 0.2s ease,
    background 0.2s ease,
    color 0.2s ease;
}

.filters-pill {
  flex: 1 1 auto;
  min-height: 38px;
  padding: 0 0.95rem;
  border-radius: 999px;
  font-size: 0.88rem;
}

.filters-pill--wide {
  text-align: center;
}

.filters-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-width: 0;
  min-height: 44px;
  padding: 0 0.95rem;
  border-radius: 16px;
}

.filters-pill:hover,
.filters-option:hover,
.filters-pill.is-active,
.filters-option.is-active {
  border-color: rgba(29, 185, 84, 0.3);
  background: rgba(29, 185, 84, 0.11);
  color: #ffffff;
}

.filters-option__count {
  color: #1db954;
  font-weight: 700;
}

.filters-input {
  min-height: 46px;
  margin-top: 0.9rem;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  text-align: center;
}

.filters-input:focus {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(29, 185, 84, 0.28);
  box-shadow: none;
}

.filters-range {
  margin-top: 0.9rem;
  accent-color: #1db954;
}

.filters-range--compact {
  display: block;
  width: 78%;
  margin: 0.45rem auto 0;
}

.people-stepper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.7rem;
  width: 100%;
  margin-top: 0.6rem;
}

.people-stepper__button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border: 0;
  border-radius: 14px;
  background: transparent;
  color: #ffffff;
  font-size: 1.15rem;
}

.people-stepper__button:disabled {
  opacity: 0.45;
}

.people-stepper__value {
  flex: 1;
  min-width: 0;
  color: #ffffff;
  text-align: center;
  font-weight: 700;
}

.filters-card__apply {
  width: 100%;
  min-height: 50px;
  margin-top: 1rem;
  border: 0;
  border-radius: 18px;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  font-weight: 700;
}

@media (max-width: 991.98px) {
  .filters-shell {
    position: static;
  }

  .filters-shell__body {
    display: none;
    margin-top: 0.85rem;
  }

  .filters-shell__body.is-open {
    display: block;
  }

  .filters-card {
    border-radius: 24px;
  }
}

@media (max-width: 575.98px) {
  .filters-card__header,
  .filters-inline-heading {
    align-items: stretch;
    flex-direction: column;
    text-align: center;
  }

  .filters-card__clear {
    width: 100%;
  }

  .filters-option {
    flex-basis: 100%;
  }
}

@media (min-width: 992px) {
  .filters-shell__body {
    display: block !important;
  }
}
</style>
