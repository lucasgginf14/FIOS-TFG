<template>
  <section class="event-filters">
    <div class="event-filters__row event-filters__row--search">
      <div class="event-field event-field--wide">
        <label>{{ t("events.filters.search") }}</label>
        <input
          :value="modelValue.text"
          type="search"
          class="form-control"
          :placeholder="t('events.filters.searchPlaceholder')"
          @input="updateField('text', $event.target.value)"
          @keyup.enter="$emit('apply')"
        />
      </div>

      <div class="event-field">
        <label>{{ t("events.filters.city") }}</label>
        <input
          :value="modelValue.city"
          type="text"
          class="form-control"
          :placeholder="t('events.filters.cityPlaceholder')"
          @input="updateField('city', $event.target.value)"
        />
      </div>

      <div class="event-field">
        <label>{{ t("events.filters.date") }}</label>
        <input
          :value="modelValue.date"
          type="date"
          class="form-control"
          @input="updateField('date', $event.target.value)"
        />
      </div>
    </div>

    <div class="event-filters__row">
      <div class="event-field">
        <label>{{ t("events.filters.genre") }}</label>
        <input
          :value="modelValue.musicalGenre"
          type="text"
          class="form-control"
          :placeholder="t('events.filters.genrePlaceholder')"
          list="event-genre-options"
          @input="updateField('musicalGenre', $event.target.value)"
        />
        <datalist id="event-genre-options">
          <option v-for="genre in genreOptions" :key="genre" :value="genre"></option>
        </datalist>
      </div>

      <div class="event-field">
        <label>{{ t("events.filters.type") }}</label>
        <AppSelect
          :model-value="modelValue.eventType"
          :options="eventTypeOptions"
          :label="t('events.filters.type')"
          @update:model-value="updateField('eventType', $event)"
        />
      </div>

      <div class="event-field">
        <label>{{ t("events.filters.source") }}</label>
        <AppSelect
          :model-value="modelValue.source"
          :options="eventSourceOptions"
          :label="t('events.filters.source')"
          @update:model-value="updateField('source', $event)"
        />
      </div>

      <div class="event-field">
        <label>{{ t("events.filters.order") }}</label>
        <AppSelect
          :model-value="modelValue.order"
          :options="orderOptions"
          :label="t('events.filters.order')"
          @update:model-value="updateField('order', $event)"
        />
      </div>
    </div>

    <div class="event-filters__footer">
      <label class="event-checkbox">
        <input
          :checked="modelValue.freeOnly"
          type="checkbox"
          @change="updateField('freeOnly', $event.target.checked)"
        />
        <span>{{ t("events.filters.freeOnly") }}</span>
      </label>

      <div class="event-filters__actions">
        <button type="button" class="btn btn-outline-light" @click="$emit('clear')">
          {{ t("events.filters.clear") }}
        </button>
        <button type="button" class="btn btn-success" @click="$emit('apply')">
          {{ t("events.filters.apply") }}
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
  genreOptions: {
    type: Array,
    default: () => []
  },
  typeOptions: {
    type: Array,
    default: () => []
  },
  sourceOptions: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(["update:modelValue", "apply", "clear"]);
const { t } = useI18n();

const eventTypeOptions = computed(() => [
  { value: "", label: t("events.filters.allTypes") },
  ...props.typeOptions.map((type) => ({
    value: type,
    label: t(`events.types.${type}`)
  }))
]);

const eventSourceOptions = computed(() => [
  { value: "", label: t("events.filters.allSources") },
  ...props.sourceOptions.map((source) => ({
    value: source,
    label: t(`events.sources.${source}`)
  }))
]);

const orderOptions = computed(() => [
  { value: "nearest", label: t("events.filters.orderOptions.nearest") },
  { value: "farthest", label: t("events.filters.orderOptions.farthest") },
  { value: "priceAsc", label: t("events.filters.orderOptions.priceAsc") },
  { value: "priceDesc", label: t("events.filters.orderOptions.priceDesc") }
]);

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}
</script>

<style scoped>
.event-filters {
  padding: 1.1rem;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.event-filters__row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.85rem;
}

.event-filters__row + .event-filters__row,
.event-filters__footer {
  margin-top: 0.85rem;
}

.event-filters__row--search {
  grid-template-columns: minmax(0, 1.5fr) repeat(2, minmax(0, 0.7fr));
}

.event-field {
  display: flex;
  flex-direction: column;
  gap: 0.38rem;
}

.event-field label {
  color: #bdbdbd;
  font-size: 0.82rem;
  font-weight: 600;
}

.event-field :deep(.form-control) {
  min-height: 46px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.event-field :deep(.form-control::placeholder) {
  color: #7e7e7e;
}

.event-filters__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.event-checkbox {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  color: #d9d9d9;
  font-weight: 600;
}

.event-checkbox input {
  accent-color: #1db954;
}

.event-filters__actions {
  display: flex;
  gap: 0.75rem;
}

@media (max-width: 991.98px) {
  .event-filters__row,
  .event-filters__row--search {
    grid-template-columns: 1fr 1fr;
  }

  .event-filters__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .event-filters__actions {
    justify-content: flex-end;
  }
}

@media (max-width: 575.98px) {
  .event-filters__row,
  .event-filters__row--search {
    grid-template-columns: 1fr;
  }

  .event-filters__actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }
}
</style>
