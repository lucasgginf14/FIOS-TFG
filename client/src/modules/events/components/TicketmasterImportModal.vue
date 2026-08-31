<template>
  <div v-if="open" class="event-modal" @click.self="$emit('close')">
    <div class="event-modal__dialog" role="dialog" aria-modal="true">
      <div class="event-modal__header">
        <div>
          <span class="event-modal__eyebrow">{{ t("events.ticketmaster.eyebrow") }}</span>
          <h2>{{ t("events.ticketmaster.title") }}</h2>
        </div>

        <button type="button" class="event-modal__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="errorMessage" class="event-modal__error">
        {{ errorMessage }}
      </div>

      <div class="event-modal__grid">
        <div class="event-field">
          <label>{{ t("events.ticketmaster.city") }}</label>
          <input
            :value="modelValue.city"
            type="text"
            class="form-control"
            :placeholder="t('events.ticketmaster.cityPlaceholder')"
            @input="updateField('city', $event.target.value)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.ticketmaster.keyword") }}</label>
          <input
            :value="modelValue.keyword"
            type="text"
            class="form-control"
            :placeholder="t('events.ticketmaster.keywordPlaceholder')"
            @input="updateField('keyword', $event.target.value)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.ticketmaster.genre") }}</label>
          <input
            :value="modelValue.musicalGenre"
            type="text"
            class="form-control"
            :placeholder="t('events.ticketmaster.genrePlaceholder')"
            @input="updateField('musicalGenre', $event.target.value)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.ticketmaster.startDate") }}</label>
          <input
            :value="modelValue.startDate"
            type="date"
            class="form-control"
            :min="minimumStartDate"
            @input="updateField('startDate', $event.target.value)"
          />
        </div>

        <div class="event-field">
          <label>{{ t("events.ticketmaster.endDate") }}</label>
          <input
            :value="modelValue.endDate"
            type="date"
            class="form-control"
            :min="modelValue.startDate || minimumStartDate"
            @input="updateField('endDate', $event.target.value)"
          />
        </div>
      </div>

      <div class="event-modal__footer">
        <span class="event-modal__source">{{ t("events.ticketmaster.hint") }}</span>

        <div class="event-modal__actions">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("events.actions.cancel") }}
          </button>
          <button
            type="button"
            class="btn btn-outline-success"
            :disabled="!results.length || searching || bulkImporting"
            @click="$emit('import-all')"
          >
            {{
              bulkImporting
                ? t("events.ticketmaster.bulkImporting")
                : t("events.ticketmaster.bulkImport", { count: results.length })
            }}
          </button>
          <button type="button" class="btn btn-success" :disabled="searching || bulkImporting" @click="submitSearch">
            {{ searching ? t("events.ticketmaster.searching") : t("events.ticketmaster.search") }}
          </button>
        </div>
      </div>

      <div v-if="results.length" class="ticketmaster-results">
        <article v-for="item in results" :key="item.externalId" class="ticketmaster-result">
          <div class="ticketmaster-result__media">
            <AppImage
              :src="item.posterImage"
              :alt="item.title"
              :fallback-src="eventPlaceholder"
              icon-class="bi bi-calendar2-event"
            />
          </div>

          <div class="ticketmaster-result__content">
            <h3>{{ item.title }}</h3>
            <p>{{ formatEventDate(item.eventDate, locale) }} | {{ item.venueName || item.city }}</p>
            <span>{{ item.city || item.venueName }}</span>
          </div>

          <button
            type="button"
            class="btn btn-success"
            :disabled="Boolean(importingId) || bulkImporting"
            @click="$emit('import', item)"
          >
            {{ importingId === item.externalId ? t("events.ticketmaster.importing") : t("events.ticketmaster.import") }}
          </button>
        </article>
      </div>

      <div v-else-if="hasSearched && !searching" class="ticketmaster-empty">
        <h3>{{ t("events.ticketmaster.empty") }}</h3>
        <p>{{ t("events.ticketmaster.emptyHint") }}</p>
        <div class="ticketmaster-empty__actions">
          <button v-if="canBroadenSearch" type="button" class="btn btn-outline-light" @click="searchWithoutNarrowFilters">
            {{ t("events.ticketmaster.broadenSearch") }}
          </button>
          <button type="button" class="btn btn-success" @click="submitSearch">
            {{ t("events.ticketmaster.searchAgain") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import eventPlaceholder from "@/assets/placeholders/event-placeholder.svg";
import { formatEventDate } from "../eventUtils";

const props = defineProps({
  open: { type: Boolean, default: false },
  modelValue: { type: Object, required: true },
  minimumStartDate: { type: String, default: "" },
  results: { type: Array, default: () => [] },
  searching: { type: Boolean, default: false },
  bulkImporting: { type: Boolean, default: false },
  importingId: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  hasSearched: { type: Boolean, default: false }
});

const emit = defineEmits(["close", "import", "import-all", "search", "update:modelValue"]);
const { locale, t } = useI18n();

const canBroadenSearch = computed(() => Boolean(props.modelValue.keyword || props.modelValue.musicalGenre));

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value,
    countryCode: "ES"
  });
}

function searchWithoutNarrowFilters() {
  const nextFilters = {
    ...props.modelValue,
    keyword: "",
    musicalGenre: "",
    countryCode: "ES"
  };

  emit("update:modelValue", nextFilters);
  emit("search", nextFilters);
}

function submitSearch() {
  const nextFilters = {
    ...props.modelValue,
    countryCode: "ES"
  };

  emit("update:modelValue", nextFilters);
  emit("search", nextFilters);
}
</script>

<style scoped>
.event-modal {
  position: fixed;
  inset: 0;
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
}

.event-modal__dialog {
  width: min(980px, 100%);
  max-height: 92vh;
  overflow: auto;
  padding: 1.35rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.event-modal__header,
.event-modal__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.event-modal__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.event-modal__header h2 {
  margin: 0.35rem 0 0;
}

.event-modal__close {
  width: 42px;
  height: 42px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.event-modal__grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.event-field {
  grid-column: span 2;
}

.event-field:nth-child(1),
.event-field:nth-child(2) {
  grid-column: span 3;
}

.event-field {
  display: flex;
  flex-direction: column;
  gap: 0.38rem;
}

.event-field label {
  color: #c0c0c0;
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
  color: #858585;
}

.event-modal__footer {
  margin-top: 1rem;
  align-items: flex-end;
}

.event-modal__source {
  max-width: 560px;
  color: #b5b5b5;
  line-height: 1.45;
}

.event-modal__actions,
.ticketmaster-empty__actions {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: flex-end;
  gap: 0.6rem;
}

.event-modal__actions .btn {
  min-height: 46px;
  border-radius: 12px;
  padding: 0.65rem 1rem;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
}

.event-modal__actions .btn-outline-light {
  min-width: 104px;
  border-color: rgba(255, 255, 255, 0.72);
  background: rgba(255, 255, 255, 0.03);
}

.event-modal__actions .btn-outline-success {
  min-width: 198px;
  border-color: rgba(29, 185, 84, 0.58);
  background: rgba(29, 185, 84, 0.08);
}

.event-modal__actions .btn-success {
  min-width: 108px;
  border-color: #1db954;
  background: linear-gradient(180deg, #25c863 0%, #169949 100%);
  box-shadow: 0 12px 26px rgba(29, 185, 84, 0.22);
}

.event-modal__actions .btn:disabled {
  opacity: 0.48;
  box-shadow: none;
}

.event-modal__error {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 1rem;
  padding: 0.85rem 1rem;
  border-radius: 16px;
}

.event-modal__error {
  border: 1px solid rgba(220, 53, 69, 0.2);
  background: rgba(220, 53, 69, 0.08);
  color: #ffb8c2;
}

.ticketmaster-results {
  display: grid;
  gap: 0.85rem;
  margin-top: 1rem;
}

.ticketmaster-result {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.ticketmaster-result__media {
  flex: 0 0 132px;
  width: 132px;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 12px;
  background: #101010;
}

.ticketmaster-result__media :deep(.app-image),
.ticketmaster-result__media :deep(.app-image__img),
.ticketmaster-result__media :deep(.app-image__placeholder) {
  min-height: 0;
}

.ticketmaster-result__content {
  min-width: 0;
  flex: 1;
}

.ticketmaster-result h3 {
  margin: 0;
  font-size: 1.05rem;
}

.ticketmaster-result p,
.ticketmaster-result span {
  margin: 0.35rem 0 0;
  color: #b8b8b8;
}

.ticketmaster-empty {
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  color: #c9c9c9;
  text-align: center;
}

.ticketmaster-empty h3 {
  margin: 0;
  color: #ffffff;
  font-size: 1.05rem;
}

.ticketmaster-empty p {
  margin: 0.45rem auto 0.9rem;
  max-width: 620px;
}

.ticketmaster-empty__actions {
  justify-content: center;
}

@media (max-width: 767.98px) {
  .event-modal {
    padding: 0.75rem;
  }

  .event-modal__header,
  .event-modal__footer,
  .event-modal__actions,
  .ticketmaster-result {
    flex-direction: column;
    align-items: stretch;
  }

  .event-modal__source {
    max-width: none;
  }

  .event-modal__actions {
    flex-wrap: nowrap;
    width: 100%;
  }

  .event-modal__actions .btn {
    width: 100%;
  }

  .ticketmaster-result__media {
    width: 100%;
    flex-basis: auto;
  }

  .event-modal__grid {
    grid-template-columns: 1fr;
  }

  .event-field,
  .event-field:nth-child(1),
  .event-field:nth-child(2) {
    grid-column: span 1;
  }
}
</style>
