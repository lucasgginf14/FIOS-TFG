<template>
  <section class="review-empty-state">
    <div class="review-empty-state__icon">
      <i :class="iconClass"></i>
    </div>

    <h2>{{ title }}</h2>
    <p>{{ text }}</p>

    <div class="review-empty-state__actions">
      <button
        v-if="filtered"
        type="button"
        class="btn btn-outline-light"
        @click="$emit('clear')"
      >
        {{ t("reviewBoard.empty.clearAction") }}
      </button>

      <RouterLink
        v-else
        class="btn btn-success"
        :to="emptyActionRoute"
      >
        {{ emptyActionLabel }}
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { useI18n } from "vue-i18n";

const props = defineProps({
  tab: {
    type: String,
    default: "pending"
  },
  filtered: {
    type: Boolean,
    default: false
  }
});

defineEmits(["clear"]);

const { t } = useI18n();

const iconClass = computed(() => {
  if (props.filtered) return "bi bi-funnel";
  if (props.tab === "received") return "bi bi-person-heart";
  return props.tab === "pending" ? "bi bi-chat-square-heart" : "bi bi-star";
});

const title = computed(() => {
  if (props.filtered) {
    return t("reviewBoard.empty.filteredTitle");
  }

  if (props.tab === "pending") return t("reviewBoard.empty.pendingTitle");
  if (props.tab === "received") return t("reviewBoard.empty.receivedTitle");
  return t("reviewBoard.empty.mineTitle");
});

const text = computed(() => {
  if (props.filtered) {
    return t("reviewBoard.empty.filteredText");
  }

  if (props.tab === "pending") return t("reviewBoard.empty.pendingText");
  if (props.tab === "received") return t("reviewBoard.empty.receivedText");
  return t("reviewBoard.empty.mineText");
});

const emptyActionRoute = computed(() =>
  props.tab === "pending" ? { name: "ReservationList" } : { name: "MusicalSpaceList" }
);

const emptyActionLabel = computed(() => {
  if (props.tab === "pending") return t("reviewBoard.empty.pendingAction");
  if (props.tab === "received") return t("reviewBoard.empty.receivedAction");
  return t("reviewBoard.empty.mineAction");
});
</script>

<style scoped>
.review-empty-state {
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

.review-empty-state__icon {
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

.review-empty-state h2 {
  margin: 0;
  color: #ffffff;
  font-size: 1.45rem;
}

.review-empty-state p {
  max-width: 620px;
  margin: 0;
  color: #b8b8b8;
  line-height: 1.7;
}

.review-empty-state__actions {
  display: flex;
  gap: 0.75rem;
}
</style>
