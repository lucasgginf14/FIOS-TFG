<template>
  <section class="instrument-filters">
    <div class="row g-3">
      <div class="col-12 col-lg-6">
        <label class="instrument-filters__label">{{ t("instrumentBoard.filters.search") }}</label>
        <input
          :value="modelValue.searchText"
          type="search"
          class="form-control"
          :placeholder="t('instrumentBoard.filters.searchPlaceholder')"
          @input="updateField('searchText', $event.target.value)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="instrument-filters__label">{{ t("instrumentBoard.filters.category") }}</label>
        <AppSelect
          :model-value="modelValue.category"
          :options="categorySelectOptions"
          :label="t('instrumentBoard.filters.category')"
          @update:model-value="updateField('category', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="instrument-filters__label">{{ t("instrumentBoard.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderSelectOptions"
          :label="t('instrumentBoard.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>

      <div class="col-12 d-flex justify-content-end">
        <button type="button" class="btn btn-outline-light" @click="$emit('clear')">
          {{ t("instrumentBoard.filters.clear") }}
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
  categoryOptions: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["update:modelValue", "clear"]);
const { t } = useI18n();

const categorySelectOptions = computed(() => [
  { value: "", label: t("instrumentBoard.filters.allCategories") },
  ...props.categoryOptions.map((category) => ({
    value: category,
    label: t(`instrumentBoard.categories.${category}`)
  }))
]);

const orderSelectOptions = computed(() => [
  { value: "name", label: t("instrumentBoard.filters.orderOptions.name") },
  { value: "category", label: t("instrumentBoard.filters.orderOptions.category") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.instrument-filters {
  padding: 1.2rem;
  border-radius: 26px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.instrument-filters__label {
  display: block;
  margin-bottom: 0.45rem;
  color: #c4c4c4;
  font-size: 0.82rem;
  font-weight: 600;
}

.instrument-filters :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.instrument-filters :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.instrument-filters :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}
</style>
