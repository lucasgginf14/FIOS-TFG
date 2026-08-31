<template>
  <section class="recruitment-filters">
    <div class="row g-3">
      <div class="col-12 col-lg-4">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.search") }}</label>
        <input
          :value="modelValue.searchText"
          type="search"
          class="form-control"
          :placeholder="t('recruitmentBoard.filters.searchPlaceholder')"
          @input="updateField('searchText', $event.target.value)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.instrument") }}</label>
        <AppSelect
          :model-value="modelValue.instrumentId"
          :options="instrumentSelectOptions"
          :label="t('recruitmentBoard.filters.instrument')"
          @update:model-value="updateField('instrumentId', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.level") }}</label>
        <AppSelect
          :model-value="modelValue.levelRequired"
          :options="levelSelectOptions"
          :label="t('recruitmentBoard.filters.level')"
          @update:model-value="updateField('levelRequired', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.city") }}</label>
        <AppSelect
          :model-value="modelValue.city"
          :options="citySelectOptions"
          :label="t('recruitmentBoard.filters.city')"
          @update:model-value="updateField('city', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.genre") }}</label>
        <AppSelect
          :model-value="modelValue.genre"
          :options="genreSelectOptions"
          :label="t('recruitmentBoard.filters.genre')"
          @update:model-value="updateField('genre', $event)"
        />
      </div>

      <div class="col-12 col-md-8 col-lg-3">
        <label class="recruitment-filters__label">{{ t("recruitmentBoard.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderSelectOptions"
          :label="t('recruitmentBoard.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>

      <div class="col-12 col-md-4 col-lg-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-light w-100" @click="$emit('clear')">
          {{ t("recruitmentBoard.filters.clear") }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  instruments: {
    type: Array,
    default: () => []
  },
  cityOptions: {
    type: Array,
    default: () => []
  },
  genreOptions: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["update:modelValue", "clear"]);
const { t } = useI18n();

const levels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "PROFESSIONAL"];

const instrumentSelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.filters.allInstruments") },
  ...props.instruments.map((instrument) => ({
    value: String(instrument.id),
    label: instrument.name
  }))
]);

const levelSelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.filters.allLevels") },
  ...levels.map((level) => ({
    value: level,
    label: t(`bands.levels.${level}`)
  }))
]);

const citySelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.filters.allCities") },
  ...props.cityOptions.map((city) => ({
    value: city,
    label: city
  }))
]);

const genreSelectOptions = computed(() => [
  { value: "", label: t("recruitmentBoard.filters.allGenres") },
  ...props.genreOptions.map((genre) => ({
    value: genre,
    label: genre
  }))
]);

const orderSelectOptions = computed(() => [
  { value: "recent", label: t("recruitmentBoard.filters.orderOptions.recent") },
  { value: "city", label: t("recruitmentBoard.filters.orderOptions.city") },
  { value: "instrument", label: t("recruitmentBoard.filters.orderOptions.instrument") },
  { value: "vacancies", label: t("recruitmentBoard.filters.orderOptions.vacancies") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.recruitment-filters {
  padding: 1.2rem;
  border-radius: 26px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.recruitment-filters__label {
  display: block;
  margin-bottom: 0.45rem;
  color: #c4c4c4;
  font-size: 0.82rem;
  font-weight: 600;
}

.recruitment-filters :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.recruitment-filters :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.recruitment-filters :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}
</style>
