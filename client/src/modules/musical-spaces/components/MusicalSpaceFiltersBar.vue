<template>
  <section class="space-filters">
    <div class="row g-3">
      <div class="col-12 col-lg-4">
        <label class="space-filters__label">{{ t("spaceList.filters.search") }}</label>
        <div class="space-filters__search">
          <i class="bi bi-search" aria-hidden="true"></i>
          <input
            :value="modelValue.searchText"
            type="search"
            class="form-control"
            :placeholder="t('spaceList.filters.searchPlaceholder')"
            @input="updateField('searchText', $event.target.value)"
          />
        </div>
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="space-filters__label">{{ t("spaceList.filters.city") }}</label>
        <AppSelect
          :model-value="modelValue.city"
          :options="citySelectOptions"
          :label="t('spaceList.filters.city')"
          @update:model-value="updateField('city', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="space-filters__label">{{ t("spaceList.filters.type") }}</label>
        <AppSelect
          :model-value="modelValue.spaceType"
          :options="typeSelectOptions"
          :label="t('spaceList.filters.type')"
          @update:model-value="updateField('spaceType', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="space-filters__label">{{ t("spaceList.filters.minCapacity") }}</label>
        <input
          :value="modelValue.minCapacity"
          type="number"
          min="0"
          class="form-control"
          @input="updateField('minCapacity', $event.target.value)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-2">
        <label class="space-filters__label">{{ t("spaceList.filters.soundproofed") }}</label>
        <AppSelect
          :model-value="modelValue.soundproofed"
          :options="soundproofedSelectOptions"
          :label="t('spaceList.filters.soundproofed')"
          @update:model-value="updateField('soundproofed', $event)"
        />
      </div>

      <div class="col-12 col-md-8 col-lg-3">
        <label class="space-filters__label">{{ t("spaceList.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderSelectOptions"
          :label="t('spaceList.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>

      <div class="col-12 col-md-4 col-lg-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-light w-100" @click="$emit('clear')">
          <i class="bi bi-x-lg" aria-hidden="true"></i>
          {{ t("spaceList.filters.clear") }}
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
  cityOptions: {
    type: Array,
    default: () => []
  },
  typeOptions: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["update:modelValue", "clear"]);
const { t } = useI18n();

const citySelectOptions = computed(() => [
  { value: "", label: t("spaceList.filters.allCities") },
  ...props.cityOptions.map((city) => ({
    value: city,
    label: city
  }))
]);

const typeSelectOptions = computed(() => [
  { value: "", label: t("spaceList.filters.allTypes") },
  ...props.typeOptions.map((type) => ({
    value: type,
    label: t(`spaceDetail.spaceTypeLabels.${type}`)
  }))
]);

const soundproofedSelectOptions = computed(() => [
  { value: "", label: t("spaceList.filters.allSoundproofed") },
  { value: "yes", label: t("spaceList.filters.soundproofedYes") },
  { value: "no", label: t("spaceList.filters.soundproofedNo") }
]);

const orderSelectOptions = computed(() => [
  { value: "rating", label: t("spaceList.filters.orderOptions.rating") },
  { value: "capacity", label: t("spaceList.filters.orderOptions.capacity") },
  { value: "city", label: t("spaceList.filters.orderOptions.city") },
  { value: "name", label: t("spaceList.filters.orderOptions.name") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.space-filters {
  padding: 1.2rem;
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.07), transparent 32%),
    linear-gradient(180deg, rgba(17, 17, 17, 0.94) 0%, rgba(24, 24, 24, 0.94) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.space-filters__label {
  display: block;
  margin-bottom: 0.45rem;
  color: #c4c4c4;
  font-size: 0.82rem;
  font-weight: 700;
}

.space-filters__search {
  position: relative;
}

.space-filters__search > i {
  position: absolute;
  top: 50%;
  left: 0.95rem;
  z-index: 1;
  color: #1db954;
  transform: translateY(-50%);
}

.space-filters :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.space-filters__search :deep(.form-control) {
  padding-left: 2.55rem;
}

.space-filters :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.space-filters :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.space-filters .btn {
  min-height: 46px;
  border-radius: 16px;
  font-weight: 700;
}
</style>
