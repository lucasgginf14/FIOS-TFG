<template>
  <section class="favorite-filters">
    <div class="row g-3">
      <div class="col-12 col-lg-4">
        <label class="favorite-filters__label">{{ t("favorites.filters.search") }}</label>
        <input
          :value="modelValue.searchText"
          type="search"
          class="form-control"
          :placeholder="t('favorites.filters.searchPlaceholder')"
          @input="updateField('searchText', $event.target.value)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="favorite-filters__label">{{ t("favorites.filters.city") }}</label>
        <AppSelect
          :model-value="modelValue.city"
          :options="citySelectOptions"
          :label="t('favorites.filters.city')"
          @update:model-value="updateField('city', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="favorite-filters__label">{{ t("favorites.filters.type") }}</label>
        <AppSelect
          :model-value="modelValue.spaceType"
          :options="typeSelectOptions"
          :label="t('favorites.filters.type')"
          @update:model-value="updateField('spaceType', $event)"
        />
      </div>

      <div class="col-12 col-md-8 col-lg-2">
        <label class="favorite-filters__label">{{ t("favorites.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderSelectOptions"
          :label="t('favorites.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>

      <div class="col-12 col-md-4 col-lg-12 d-flex justify-content-lg-end">
        <button type="button" class="btn btn-outline-light favorite-filters__clear" @click="$emit('clear')">
          {{ t("favorites.filters.clear") }}
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
  { value: "", label: t("favorites.filters.allCities") },
  ...props.cityOptions.map((city) => ({
    value: city,
    label: city
  }))
]);

const typeSelectOptions = computed(() => [
  { value: "", label: t("favorites.filters.allTypes") },
  ...props.typeOptions.map((type) => ({
    value: type,
    label: t(`spaceDetail.spaceTypeLabels.${type}`)
  }))
]);

const orderSelectOptions = computed(() => [
  { value: "recent", label: t("favorites.filters.orderOptions.recent") },
  { value: "name", label: t("favorites.filters.orderOptions.name") },
  { value: "city", label: t("favorites.filters.orderOptions.city") },
  { value: "rating", label: t("favorites.filters.orderOptions.rating") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.favorite-filters {
  padding: 1.2rem;
  border-radius: 26px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.favorite-filters__label {
  display: block;
  margin-bottom: 0.45rem;
  color: #c4c4c4;
  font-size: 0.82rem;
  font-weight: 600;
}

.favorite-filters :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.favorite-filters :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.favorite-filters :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.favorite-filters__clear {
  min-height: 46px;
  border-radius: 16px;
}
</style>
