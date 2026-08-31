<template>
  <section class="space-empty-state">
    <div class="space-empty-state__icon">
      <i :class="filtered ? 'bi bi-funnel' : mine ? 'bi bi-building' : 'bi bi-music-note-list'"></i>
    </div>
    <h2>{{ title }}</h2>
    <p>{{ text }}</p>

    <div class="space-empty-state__actions">
      <button
        v-if="filtered"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('clear')"
      >
        {{ t("spaceList.empty.resetAction") }}
      </button>

      <button
        v-else-if="mine && logged"
        type="button"
        class="btn btn-success"
        @click="$emit('create')"
      >
        {{ t("spaceList.empty.createAction") }}
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

defineEmits(["create", "clear"]);

const { t } = useI18n();

const title = computed(() => {
  if (props.filtered) return t("spaceList.empty.filteredTitle");
  return props.mine ? t("spaceList.empty.mineTitle") : t("spaceList.empty.exploreTitle");
});

const text = computed(() => {
  if (props.filtered) return t("spaceList.empty.filteredText");
  return props.mine ? t("spaceList.empty.mineText") : t("spaceList.empty.exploreText");
});
</script>

<style scoped>
.space-empty-state {
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

.space-empty-state__icon {
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

.space-empty-state h2 {
  margin: 0;
  font-size: 1.45rem;
}

.space-empty-state p {
  max-width: 620px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.space-empty-state__actions {
  display: flex;
  gap: 0.75rem;
}
</style>
