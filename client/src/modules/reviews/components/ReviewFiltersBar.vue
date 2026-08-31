<template>
  <section class="review-filters">
    <div class="row g-3">
      <div class="col-12 col-lg-6">
        <label class="review-filters__label">{{ t("reviewBoard.filters.search") }}</label>
        <input
          :value="modelValue.searchText"
          type="search"
          class="form-control"
          :placeholder="t('reviewBoard.filters.searchPlaceholder')"
          @input="updateField('searchText', $event.target.value)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="review-filters__label">{{ t("reviewBoard.filters.minRating") }}</label>
        <AppSelect
          :model-value="modelValue.minRating"
          :options="ratingOptions"
          :label="t('reviewBoard.filters.minRating')"
          @update:model-value="updateField('minRating', $event)"
        />
      </div>

      <div class="col-12 col-md-6 col-lg-3">
        <label class="review-filters__label">{{ t("reviewBoard.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderOptions"
          :label="t('reviewBoard.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>

      <div class="col-12 d-flex justify-content-end">
        <button type="button" class="btn btn-outline-light" @click="$emit('clear')">
          {{ t("reviewBoard.filters.clear") }}
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
  }
});

const emit = defineEmits(["update:modelValue", "clear"]);
const { t } = useI18n();

const ratingOptions = computed(() => [
  { value: "", label: t("reviewBoard.filters.allRatings") },
  ...[5, 4, 3, 2, 1].map((value) => ({
    value: String(value),
    label: t("reviewBoard.filters.minRatingOption", { value })
  }))
]);

const orderOptions = computed(() => [
  { value: "recent", label: t("reviewBoard.filters.orderOptions.recent") },
  { value: "highest", label: t("reviewBoard.filters.orderOptions.highest") },
  { value: "lowest", label: t("reviewBoard.filters.orderOptions.lowest") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.review-filters {
  padding: 1.2rem;
  border-radius: 26px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.review-filters__label {
  display: block;
  margin-bottom: 0.45rem;
  color: #c4c4c4;
  font-size: 0.82rem;
  font-weight: 600;
}

.review-filters :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.review-filters :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.review-filters :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}
</style>
