<template>
  <article class="instrument-card">
    <div class="instrument-card__topline">
      <div class="instrument-card__icon">{{ categoryEmoji }}</div>
      <InstrumentCategoryBadge :category="instrument.category" />
    </div>

    <h2>{{ instrument.name }}</h2>
    <p>{{ t("instrumentBoard.card.categoryLabel") }} {{ t(`instrumentBoard.categories.${instrument.category || "OTHER"}`) }}</p>

    <div class="instrument-card__actions">
      <button
        v-if="logged && !owned"
        type="button"
        class="btn btn-success"
        :disabled="busy"
        @click="$emit('toggle', instrument)"
      >
        {{ busy ? t("instrumentBoard.card.saving") : t("instrumentBoard.card.add") }}
      </button>

      <button
        v-else-if="logged && owned"
        type="button"
        class="btn btn-outline-light"
        :disabled="busy"
        @click="$emit('toggle', instrument)"
      >
        {{ busy ? t("instrumentBoard.card.saving") : t("instrumentBoard.card.remove") }}
      </button>

      <button
        v-if="admin"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('edit', instrument)"
      >
        {{ t("instrumentBoard.card.edit") }}
      </button>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import InstrumentCategoryBadge from "./InstrumentCategoryBadge.vue";

const props = defineProps({
  instrument: {
    type: Object,
    required: true
  },
  logged: {
    type: Boolean,
    default: false
  },
  owned: {
    type: Boolean,
    default: false
  },
  admin: {
    type: Boolean,
    default: false
  },
  busy: {
    type: Boolean,
    default: false
  }
});

defineEmits(["toggle", "edit"]);
const { t } = useI18n();

const categoryEmoji = computed(() => {
  const emojiMap = {
    STRINGS: "🎸",
    WIND: "🎷",
    BRASS: "🎺",
    PERCUSSION: "🥁",
    KEYBOARD: "🎹",
    ELECTRONIC: "🎛️",
    VOICE: "🎤",
    OTHER: "🎶"
  };

  return emojiMap[props.instrument.category] || emojiMap.OTHER;
});
</script>

<style scoped>
.instrument-card {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.instrument-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.instrument-card__icon {
  width: 56px;
  height: 56px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: rgba(29, 185, 84, 0.12);
  font-size: 1.65rem;
}

.instrument-card h2 {
  margin: 1rem 0 0.45rem;
  color: #ffffff;
  font-size: 1.28rem;
  font-weight: 700;
}

.instrument-card p {
  margin: 0;
  color: #b8b8b8;
  line-height: 1.6;
}

.instrument-card__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: auto;
  padding-top: 1.1rem;
}

.instrument-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 575.98px) {
  .instrument-card__topline {
    flex-direction: column;
    align-items: flex-start;
  }

  .instrument-card__actions .btn {
    width: 100%;
  }
}
</style>
