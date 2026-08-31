<template>
  <section class="favorite-empty-state">
    <div class="favorite-empty-state__icon">
      <i :class="filtered ? 'bi bi-funnel' : 'bi bi-heart'"></i>
    </div>
    <h2>{{ filtered ? t("favorites.empty.filteredTitle") : t("favorites.empty.title") }}</h2>
    <p>{{ filtered ? t("favorites.empty.filteredText") : t("favorites.empty.text") }}</p>

    <div class="favorite-empty-state__actions">
      <button
        v-if="filtered"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('clear')"
      >
        {{ t("favorites.empty.resetAction") }}
      </button>

      <RouterLink
        v-else
        class="btn btn-success"
        :to="{ name: 'MusicalSpaceList' }"
      >
        {{ t("favorites.empty.exploreAction") }}
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { RouterLink } from "vue-router";
import { useI18n } from "vue-i18n";

defineProps({
  filtered: {
    type: Boolean,
    default: false
  }
});

defineEmits(["clear"]);

const { t } = useI18n();
</script>

<style scoped>
.favorite-empty-state {
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

.favorite-empty-state__icon {
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

.favorite-empty-state h2 {
  margin: 0;
  font-size: 1.45rem;
}

.favorite-empty-state p {
  max-width: 620px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.favorite-empty-state__actions {
  display: flex;
  gap: 0.75rem;
}
</style>
