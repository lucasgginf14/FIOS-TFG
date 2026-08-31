<template>
  <article
    class="recruitment-card clickable-card"
    :class="{ 'is-highlighted': highlighted }"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="recruitment-card__body">
      <div class="recruitment-card__topline">
        <span class="recruitment-card__instrument">{{ item.instrument?.name || item.roleWanted || t("recruitmentBoard.card.instrumentFallback") }}</span>
        <BandRecruitmentStatusBadge v-if="mine" :status="item.status" />
      </div>

      <h2 class="recruitment-card__title">{{ item.title }}</h2>
      <p class="recruitment-card__band">
        {{ item.band?.name || t("recruitmentBoard.card.bandFallback") }}
      </p>

      <div class="recruitment-card__meta">
        <span><i class="bi bi-geo-alt"></i> {{ item.city || item.band?.baseCity || t("recruitmentBoard.card.cityFallback") }}</span>
        <span><i class="bi bi-music-note-beamed"></i> {{ item.roleWanted || item.instrument?.name || t("recruitmentBoard.card.instrumentFallback") }}</span>
        <span><i class="bi bi-bar-chart"></i> {{ t(`bands.levels.${item.levelRequired || "BEGINNER"}`) }}</span>
        <span><i class="bi bi-vinyl"></i> {{ item.band?.mainGenre || t("recruitmentBoard.card.genreFallback") }}</span>
        <span><i class="bi bi-people"></i> {{ t("recruitmentBoard.card.vacancies", { count: item.vacancies ?? 0 }) }}</span>
        <span><i class="bi bi-calendar3"></i> {{ publicationDateLabel }}</span>
      </div>

      <div class="recruitment-card__actions" @keydown.stop>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>

        <template v-if="mine && item.status === 'OPEN'">
          <button type="button" class="btn btn-outline-light" @click.stop="$emit('edit', item)">
            {{ t("recruitmentBoard.card.edit") }}
          </button>
          <button type="button" class="btn btn-outline-danger" :disabled="busy" @click.stop="$emit('close', item)">
            {{ busy ? t("recruitmentBoard.card.closing") : t("recruitmentBoard.card.close") }}
          </button>
        </template>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import BandRecruitmentStatusBadge from "./BandRecruitmentStatusBadge.vue";

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  locale: {
    type: String,
    default: "es"
  },
  mine: {
    type: Boolean,
    default: false
  },
  busy: {
    type: Boolean,
    default: false
  },
  highlighted: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["view", "edit", "close"]);

const { t } = useI18n();

const cardLabel = computed(() => `${t("recruitmentBoard.card.view")}: ${props.item.title}`);

const publicationDateLabel = computed(() => {
  if (!props.item.publicationDate) {
    return t("recruitmentBoard.card.noPublicationDate");
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.item.publicationDate));
});

function openDetail() {
  emit("view", props.item);
}
</script>

<style scoped>
.recruitment-card {
  display: flex;
  min-height: 100%;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.recruitment-card.is-highlighted {
  border-color: rgba(29, 185, 84, 0.4);
  box-shadow: 0 0 0 1px rgba(29, 185, 84, 0.18), 0 24px 60px rgba(0, 0, 0, 0.22);
}

.recruitment-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 1.25rem;
}

.recruitment-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.recruitment-card__instrument {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.recruitment-card__title {
  margin: 1rem 0 0.4rem;
  font-size: 1.35rem;
  font-weight: 700;
}

.recruitment-card__band {
  margin: 0;
  color: #b3b3b3;
  font-weight: 600;
}

.recruitment-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.9rem 1.2rem;
  margin-top: 1rem;
  color: #c4c4c4;
}

.recruitment-card__meta i {
  margin-right: 0.35rem;
  color: #1db954;
}

.recruitment-card__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: auto;
  padding-top: 1.2rem;
}

.recruitment-card__actions .btn {
  min-height: 42px;
  border-radius: 14px;
}

@media (max-width: 575.98px) {
  .recruitment-card__topline {
    flex-direction: column;
    align-items: flex-start;
  }

  .recruitment-card__actions .btn {
    width: 100%;
  }
}
</style>
