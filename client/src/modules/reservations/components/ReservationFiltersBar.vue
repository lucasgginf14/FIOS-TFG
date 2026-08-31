<template>
  <section class="filters-bar">
    <div class="filters-bar__field filters-bar__field--search">
      <i class="bi bi-search"></i>
      <input
        :value="modelValue.searchText"
        type="search"
        class="form-control"
        :placeholder="t('reservations.filters.searchPlaceholder')"
        @input="updateField('searchText', $event.target.value)"
      />
    </div>

    <div class="filters-bar__field">
      <label>{{ t("reservations.filters.date") }}</label>
      <input
        :value="modelValue.date"
        type="date"
        class="form-control"
        @input="updateField('date', $event.target.value)"
      />
    </div>

    <div class="filters-bar__field">
      <label>{{ t("reservations.filters.sessionType") }}</label>
      <AppSelect
        :model-value="modelValue.sessionType"
        :options="sessionTypeOptions"
        :label="t('reservations.filters.sessionType')"
        @update:model-value="updateField('sessionType', $event)"
      />
    </div>

    <div class="filters-bar__field">
      <label>{{ t("reservations.filters.order") }}</label>
      <AppSelect
        :model-value="modelValue.order"
        :options="orderOptions"
        :label="t('reservations.filters.order')"
        @update:model-value="updateField('order', $event)"
      />
    </div>

    <button type="button" class="btn btn-outline-light filters-bar__reset" @click="clearFilters">
      <i class="bi bi-x-lg" aria-hidden="true"></i>
      {{ t("reservations.filters.clear") }}
    </button>
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
  }
});

const emit = defineEmits(["update:modelValue"]);
const { t } = useI18n();

const sessionTypeOptions = computed(() => [
  { value: "", label: t("reservations.filters.allSessionTypes") },
  { value: "REHEARSAL", label: t("reservations.sessionTypes.REHEARSAL") },
  { value: "RECORDING", label: t("reservations.sessionTypes.RECORDING") },
  { value: "CLASS", label: t("reservations.sessionTypes.CLASS") },
  { value: "EVENT_PREPARATION", label: t("reservations.sessionTypes.EVENT_PREPARATION") },
  { value: "OTHER", label: t("reservations.sessionTypes.OTHER") }
]);

const orderOptions = computed(() => [
  { value: "nearest", label: t("reservations.filters.orderOptions.nearest") },
  { value: "farthest", label: t("reservations.filters.orderOptions.farthest") },
  { value: "priceDesc", label: t("reservations.filters.orderOptions.priceDesc") },
  { value: "priceAsc", label: t("reservations.filters.orderOptions.priceAsc") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function clearFilters() {
  emit("update:modelValue", {
    searchText: "",
    date: "",
    sessionType: "",
    order: "nearest"
  });
}
</script>

<style scoped>
.filters-bar {
  display: grid;
  grid-template-columns: minmax(220px, 1.35fr) repeat(3, minmax(0, 1fr)) auto;
  gap: 0.8rem;
  align-items: end;
  padding: 0.9rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.035);
}

.filters-bar__field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.filters-bar__field label {
  color: #b7b7b7;
  font-size: 0.82rem;
  font-weight: 600;
}

.filters-bar__field :deep(.form-control) {
  min-height: 42px;
  border-radius: 10px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.filters-bar__field :deep(.form-control::placeholder) {
  color: #9d9d9d;
}

.filters-bar__field :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.filters-bar__field--search {
  position: relative;
}

.filters-bar__field--search i {
  position: absolute;
  left: 0.9rem;
  bottom: 0.74rem;
  color: #1db954;
}

.filters-bar__field--search :deep(.form-control) {
  padding-left: 2.5rem;
}

.filters-bar__reset {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-height: 42px;
  border-radius: 10px;
  white-space: nowrap;
}

@media (max-width: 1199.98px) {
  .filters-bar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .filters-bar {
    grid-template-columns: 1fr;
  }
}
</style>
