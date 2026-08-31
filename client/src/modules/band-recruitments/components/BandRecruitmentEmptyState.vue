<template>
  <section class="recruitment-empty-state">
    <div class="recruitment-empty-state__icon">
      <i :class="iconClass"></i>
    </div>
    <h2>{{ title }}</h2>
    <p>{{ text }}</p>

    <div class="recruitment-empty-state__actions">
      <button
        v-if="filtered"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('clear')"
      >
        {{ t("recruitmentBoard.empty.resetAction") }}
      </button>

      <button
        v-else-if="mine && canPublish"
        type="button"
        class="btn btn-success"
        @click="$emit('create')"
      >
        {{ t("recruitmentBoard.empty.createAction") }}
      </button>

      <RouterLink
        v-else-if="mine && !hasBands"
        class="btn btn-success"
        :to="{ name: 'BandList', query: { mode: 'create' } }"
      >
        {{ t("recruitmentBoard.empty.createBandAction") }}
      </RouterLink>

      <RouterLink
        v-else
        class="btn btn-success"
        :to="{ name: 'BandList' }"
      >
        {{ t("recruitmentBoard.empty.exploreAction") }}
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
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
  canPublish: {
    type: Boolean,
    default: false
  },
  hasBands: {
    type: Boolean,
    default: false
  }
});

defineEmits(["clear", "create"]);

const { t } = useI18n();

const iconClass = computed(() => {
  if (props.filtered) return "bi bi-funnel";
  if (props.mine) return "bi bi-megaphone";
  return "bi bi-people";
});

const title = computed(() => {
  if (props.filtered) return t("recruitmentBoard.empty.filteredTitle");
  return props.mine ? t("recruitmentBoard.empty.mineTitle") : t("recruitmentBoard.empty.title");
});

const text = computed(() => {
  if (props.filtered) return t("recruitmentBoard.empty.filteredText");
  return props.mine ? t("recruitmentBoard.empty.mineText") : t("recruitmentBoard.empty.text");
});
</script>

<style scoped>
.recruitment-empty-state {
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

.recruitment-empty-state__icon {
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

.recruitment-empty-state h2 {
  margin: 0;
  font-size: 1.45rem;
}

.recruitment-empty-state p {
  max-width: 620px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.recruitment-empty-state__actions {
  display: flex;
  gap: 0.75rem;
}
</style>
