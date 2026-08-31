<template>
  <section class="band-filters">
    <div class="band-filters__field band-filters__field--search">
      <i class="bi bi-search"></i>
      <input
        :value="modelValue.searchText"
        type="search"
        class="form-control"
        :placeholder="searchPlaceholder"
        @input="updateField('searchText', $event.target.value)"
      />
    </div>

    <div class="band-filters__field">
      <label>{{ genreLabel }}</label>
      <AppSelect
        :model-value="modelValue.genre"
        :options="genreSelectOptions"
        :label="genreLabel"
        @update:model-value="updateField('genre', $event)"
      />
    </div>

    <div class="band-filters__field">
      <label>{{ statusLabel }}</label>
      <AppSelect
        :model-value="modelValue.state"
        :options="stateSelectOptions"
        :label="statusLabel"
        @update:model-value="updateField('state', $event)"
      />
    </div>

    <div class="band-filters__field">
      <label>{{ orderLabel }}</label>
      <AppSelect
        :model-value="modelValue.order"
        :options="orderSelectOptions"
        :label="orderLabel"
        @update:model-value="updateField('order', $event)"
      />
    </div>

    <button type="button" class="btn btn-outline-light band-filters__reset" @click="reset">
      {{ clearLabel }}
    </button>
  </section>
</template>

<script setup>
import { computed } from "vue";
import AppSelect from "@/common/components/AppSelect.vue";

const props = defineProps({
  modelValue: { type: Object, required: true },
  genreOptions: { type: Array, default: () => [] },
  searchPlaceholder: { type: String, default: "" },
  genreLabel: { type: String, default: "" },
  allGenresLabel: { type: String, default: "" },
  statusLabel: { type: String, default: "" },
  allStatesLabel: { type: String, default: "" },
  statusActiveLabel: { type: String, default: "" },
  statusInactiveLabel: { type: String, default: "" },
  statusFormingLabel: { type: String, default: "" },
  statusRecruitingLabel: { type: String, default: "" },
  orderLabel: { type: String, default: "" },
  orderRecentLabel: { type: String, default: "" },
  orderNameLabel: { type: String, default: "" },
  orderCityLabel: { type: String, default: "" },
  orderMembersLabel: { type: String, default: "" },
  clearLabel: { type: String, default: "" }
});

const emit = defineEmits(["update:modelValue"]);

const genreSelectOptions = computed(() => [
  { value: "", label: props.allGenresLabel },
  ...props.genreOptions.map((genre) => ({
    value: genre,
    label: genre
  }))
]);

const stateSelectOptions = computed(() => [
  { value: "", label: props.allStatesLabel },
  { value: "active", label: props.statusActiveLabel },
  { value: "inactive", label: props.statusInactiveLabel },
  { value: "forming", label: props.statusFormingLabel },
  { value: "recruiting", label: props.statusRecruitingLabel }
]);

const orderSelectOptions = computed(() => [
  { value: "recent", label: props.orderRecentLabel },
  { value: "name", label: props.orderNameLabel },
  { value: "city", label: props.orderCityLabel },
  { value: "members", label: props.orderMembersLabel }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function reset() {
  emit("update:modelValue", {
    searchText: "",
    genre: "",
    state: "",
    order: "recent"
  });
}
</script>

<style scoped>
.band-filters {
  display: grid;
  grid-template-columns: minmax(220px, 1.35fr) repeat(3, minmax(0, 1fr)) auto;
  gap: 0.8rem;
  align-items: end;
}

.band-filters__field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.band-filters__field label {
  color: #b7b7b7;
  font-size: 0.82rem;
  font-weight: 600;
}

.band-filters__field :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.band-filters__field :deep(.form-control::placeholder) {
  color: #9d9d9d;
}

.band-filters__field :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.band-filters__field--search {
  position: relative;
}

.band-filters__field--search i {
  position: absolute;
  left: 0.9rem;
  bottom: 0.92rem;
  color: #1db954;
}

.band-filters__field--search :deep(.form-control) {
  padding-left: 2.5rem;
}

.band-filters__reset {
  min-height: 46px;
  border-radius: 16px;
}

@media (max-width: 1199.98px) {
  .band-filters {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 767.98px) {
  .band-filters {
    grid-template-columns: 1fr;
  }
}
</style>
