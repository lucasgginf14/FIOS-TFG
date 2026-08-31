<template>
  <section class="instrument-empty-state">
    <div class="instrument-empty-state__icon">
      <i :class="iconClass"></i>
    </div>
    <h2>{{ title }}</h2>
    <p>{{ text }}</p>

    <div class="instrument-empty-state__actions">
      <button
        v-if="filtered"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('clear')"
      >
        {{ t("instrumentBoard.empty.clearAction") }}
      </button>

      <button
        v-else-if="mine && logged"
        type="button"
        class="btn btn-success"
        @click="$emit('explore')"
      >
        {{ t("instrumentBoard.empty.mineAction") }}
      </button>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";

const props = defineProps({
  mine: {
    type: Boolean,
    default: false
  },
  filtered: {
    type: Boolean,
    default: false
  },
  logged: {
    type: Boolean,
    default: false
  }
});

defineEmits(["clear", "explore"]);

const { t } = useI18n();

const iconClass = computed(() => {
  if (props.filtered) return "bi bi-funnel";
  return props.mine ? "bi bi-music-note-list" : "bi bi-vinyl";
});

const title = computed(() => {
  if (props.filtered) {
    return t("instrumentBoard.empty.filteredTitle");
  }

  return props.mine ? t("instrumentBoard.empty.mineTitle") : t("instrumentBoard.empty.catalogTitle");
});

const text = computed(() => {
  if (props.filtered) {
    return t("instrumentBoard.empty.filteredText");
  }

  return props.mine ? t("instrumentBoard.empty.mineText") : t("instrumentBoard.empty.catalogText");
});
</script>

<style scoped>
.instrument-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  min-height: 320px;
  padding: 2rem;
  text-align: center;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.instrument-empty-state__icon {
  width: 72px;
  height: 72px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 1.8rem;
}

.instrument-empty-state h2 {
  margin: 0;
  color: #ffffff;
  font-size: 1.45rem;
}

.instrument-empty-state p {
  max-width: 620px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.instrument-empty-state__actions {
  display: flex;
  gap: 0.75rem;
}
</style>
