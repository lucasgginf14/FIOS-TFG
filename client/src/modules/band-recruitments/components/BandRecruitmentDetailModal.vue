<template>
  <div v-if="open" class="detail-modal" @click.self="$emit('close')">
    <div class="detail-modal__card" role="dialog" aria-modal="true">
      <div class="detail-modal__header">
        <div>
          <span class="detail-modal__eyebrow">{{ t("recruitmentBoard.detail.eyebrow") }}</span>
          <h3>{{ item?.title || t("recruitmentBoard.detail.titleFallback") }}</h3>
        </div>
        <button type="button" class="detail-modal__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="loading" class="detail-modal__state">
        <div class="spinner-border text-success" role="status"></div>
        <p>{{ t("recruitmentBoard.detail.loading") }}</p>
      </div>

      <div v-else-if="errorMessage" class="detail-modal__banner detail-modal__banner--error">
        {{ errorMessage }}
      </div>

      <template v-else-if="item">
        <div class="detail-modal__meta">
          <span class="detail-pill">{{ item.band?.name || t("recruitmentBoard.card.bandFallback") }}</span>
          <span class="detail-pill">{{ item.instrument?.name || item.roleWanted || t("recruitmentBoard.card.instrumentFallback") }}</span>
          <span class="detail-pill">{{ t(`bands.levels.${item.levelRequired || "BEGINNER"}`) }}</span>
          <BandRecruitmentStatusBadge v-if="showStatus" :status="item.status" />
        </div>

        <div class="detail-grid">
          <article class="detail-card">
            <span>{{ t("recruitmentBoard.detail.fields.city") }}</span>
            <strong>{{ item.city || item.band?.baseCity || t("recruitmentBoard.card.cityFallback") }}</strong>
          </article>
          <article class="detail-card">
            <span>{{ t("recruitmentBoard.detail.fields.genre") }}</span>
            <strong>{{ item.band?.mainGenre || t("recruitmentBoard.card.genreFallback") }}</strong>
          </article>
          <article class="detail-card">
            <span>{{ t("recruitmentBoard.detail.fields.role") }}</span>
            <strong>{{ item.roleWanted || t("recruitmentBoard.card.instrumentFallback") }}</strong>
          </article>
          <article class="detail-card">
            <span>{{ t("recruitmentBoard.detail.fields.vacancies") }}</span>
            <strong>{{ t("recruitmentBoard.card.vacancies", { count: item.vacancies ?? 0 }) }}</strong>
          </article>
          <article class="detail-card">
            <span>{{ t("recruitmentBoard.detail.fields.publicationDate") }}</span>
            <strong>{{ publicationDateLabel }}</strong>
          </article>
        </div>

        <article v-if="contactEmail" class="detail-contact">
          <div class="detail-contact__copy">
            <span>{{ t("recruitmentBoard.detail.contactTitle") }}</span>
            <strong>{{ publisherName }}</strong>
            <p>{{ t("recruitmentBoard.detail.contactHint") }}</p>
          </div>
          <a class="btn btn-success detail-contact__link" :href="contactHref">
            <i class="bi bi-envelope" aria-hidden="true"></i>
            {{ t("recruitmentBoard.detail.contactAction") }}
          </a>
          <a class="detail-contact__email" :href="contactHref">{{ contactEmail }}</a>
        </article>

        <div class="detail-copy">
          <h4>{{ t("recruitmentBoard.detail.descriptionTitle") }}</h4>
          <p>{{ item.description || t("recruitmentBoard.detail.emptyDescription") }}</p>
        </div>

        <div class="detail-modal__actions">
          <RouterLink
            class="detail-band-link clickable-card"
            :to="bandRoute"
            :aria-label="bandCardLabel"
          >
            <span>{{ t("recruitmentBoard.card.bandFallback") }}</span>
            <strong>{{ item.band?.name || t("recruitmentBoard.card.bandFallback") }}</strong>
            <span class="clickable-card__open-indicator" aria-hidden="true">
              <i class="bi bi-arrow-up-right"></i>
            </span>
          </RouterLink>
          <button
            v-if="canEdit && item.status === 'OPEN'"
            type="button"
            class="btn btn-success"
            @click="$emit('edit', item)"
          >
            {{ t("recruitmentBoard.card.edit") }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { useI18n } from "vue-i18n";
import BandRecruitmentStatusBadge from "./BandRecruitmentStatusBadge.vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  item: {
    type: Object,
    default: null
  },
  locale: {
    type: String,
    default: "es"
  },
  loading: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  },
  canEdit: {
    type: Boolean,
    default: false
  },
  showStatus: {
    type: Boolean,
    default: false
  }
});

defineEmits(["close", "edit"]);

const { t } = useI18n();

const bandRoute = computed(() =>
  props.item?.band?.id ? { name: "BandDetail", params: { id: props.item.band.id } } : { name: "BandList" }
);

const bandCardLabel = computed(
  () => `${t("recruitmentBoard.card.bandFallback")}: ${props.item?.band?.name || ""}`
);

const publicationDateLabel = computed(() => {
  if (!props.item?.publicationDate) {
    return t("recruitmentBoard.card.noPublicationDate");
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.item.publicationDate));
});

const contactEmail = computed(() => props.item?.publishedByEmail || props.item?.publishedBy?.email || "");

const contactHref = computed(() => `mailto:${contactEmail.value}`);

const publisherName = computed(() => {
  const publishedBy = props.item?.publishedBy;
  const name = [
    publishedBy?.name,
    publishedBy?.firstSurname,
    publishedBy?.secondSurname
  ]
    .filter(Boolean)
    .join(" ");

  return name || contactEmail.value;
});
</script>

<style scoped>
.detail-modal {
  position: fixed;
  inset: 0;
  z-index: 1080;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.74);
  backdrop-filter: blur(10px);
}

.detail-modal__card {
  width: min(100%, 820px);
  max-height: calc(100vh - 2rem);
  overflow: auto;
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.detail-modal__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.detail-modal__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.detail-modal__header h3 {
  margin: 0.35rem 0 0;
  font-size: 1.45rem;
}

.detail-modal__close {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.detail-modal__state,
.detail-modal__banner {
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 16px;
}

.detail-modal__state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
}

.detail-modal__banner--error {
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.detail-modal__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  margin-top: 1rem;
}

.detail-pill {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.detail-card {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.detail-card span {
  display: block;
  color: #9e9e9e;
  font-size: 0.84rem;
}

.detail-card strong {
  display: block;
  margin-top: 0.45rem;
}

.detail-copy {
  margin-top: 1rem;
}

.detail-contact {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.85rem 1rem;
  align-items: center;
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 20px;
  border: 1px solid rgba(29, 185, 84, 0.22);
  background: rgba(29, 185, 84, 0.08);
}

.detail-contact__copy {
  min-width: 0;
}

.detail-contact__copy span,
.detail-contact__email {
  color: #9e9e9e;
  font-size: 0.84rem;
}

.detail-contact__copy strong {
  display: block;
  margin-top: 0.25rem;
  color: #ffffff;
}

.detail-contact__copy p {
  margin: 0.45rem 0 0;
  color: #d1d1d1;
  line-height: 1.55;
}

.detail-contact__link {
  min-height: 44px;
  border-radius: 14px;
  white-space: nowrap;
}

.detail-contact__email {
  grid-column: 1 / -1;
  overflow-wrap: anywhere;
  text-decoration: none;
}

.detail-contact__email:hover {
  color: #1db954;
}

.detail-copy h4 {
  margin: 0;
  font-size: 1.15rem;
}

.detail-copy p {
  margin: 0.8rem 0 0;
  color: #d1d1d1;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-modal__actions {
  display: flex;
  align-items: stretch;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.2rem;
}

.detail-band-link {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.15rem 0.8rem;
  min-width: min(100%, 280px);
  padding: 0.85rem 0.95rem;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
  text-decoration: none;
}

.detail-band-link > span:first-child {
  color: #9e9e9e;
  font-size: 0.82rem;
  font-weight: 700;
}

.detail-band-link strong {
  min-width: 0;
}

.detail-band-link .clickable-card__open-indicator {
  grid-row: 1 / span 2;
  grid-column: 2;
  align-self: center;
  width: 36px;
  height: 36px;
}

@media (max-width: 767.98px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-modal__header,
  .detail-modal__actions,
  .detail-contact {
    flex-direction: column;
    align-items: stretch;
  }

  .detail-contact {
    display: flex;
  }
}
</style>
