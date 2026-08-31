<template>
  <section class="preview-card">
    <div class="preview-card__header">
      <div>
        <span class="preview-card__eyebrow">{{ eyebrow }}</span>
        <h3>{{ title }}</h3>
      </div>
      <span class="preview-card__count">{{ countLabel }}</span>
    </div>

    <div v-if="recruitments.length" class="recruitment-list">
      <article
        v-for="item in recruitments"
        :key="item.id"
        class="recruitment-row clickable-card"
        role="link"
        tabindex="0"
        :aria-label="cardLabel(item)"
        @click="openDetail(item)"
        @keydown.enter.prevent="openDetail(item)"
        @keydown.space.prevent="openDetail(item)"
      >
        <div class="recruitment-row__body">
          <strong>{{ item.title }}</strong>
          <span>{{ item.band?.name }} · {{ item.instrument?.name || item.roleWanted }}</span>
          <small>{{ item.city }} · {{ levelLabel(item.levelRequired) }} · {{ vacanciesLabel(item.vacancies) }}</small>
        </div>
        <div class="recruitment-row__actions" @keydown.stop>
          <span class="clickable-card__open-indicator" aria-hidden="true">
            <i class="bi bi-arrow-up-right"></i>
          </span>
          <button
            v-if="item.canManage"
            type="button"
            class="btn btn-outline-light btn-sm"
            @click.stop="$emit('manage', item)"
          >
            {{ manageLabel }}
          </button>
          <button
            v-if="item.canManage"
            type="button"
            class="btn btn-outline-danger btn-sm"
            :disabled="closingId === item.id"
            @click.stop="$emit('close', item)"
          >
            {{ closingId === item.id ? closingLabel : closeLabel }}
          </button>
        </div>
      </article>
    </div>

    <div v-else class="preview-card__empty">{{ emptyLabel }}</div>
  </section>
</template>

<script setup>
import { useI18n } from "vue-i18n";

const props = defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  countLabel: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  viewLabel: { type: String, default: "" },
  manageLabel: { type: String, default: "" },
  closeLabel: { type: String, default: "" },
  closingLabel: { type: String, default: "" },
  recruitments: { type: Array, default: () => [] },
  closingId: { type: Number, default: null }
});

const emit = defineEmits(["view", "manage", "close"]);
const { t } = useI18n();

function cardLabel(item) {
  return `${props.viewLabel}: ${item.title}`;
}

function openDetail(item) {
  emit("view", item);
}

function vacanciesLabel(value) {
  return t("bands.recruitments.vacancyShort", { count: value });
}

function levelLabel(value) {
  if (!value) {
    return "--";
  }

  const knownLevels = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "PROFESSIONAL"];
  return knownLevels.includes(value) ? t(`bands.levels.${value}`) : value;
}
</script>

<style scoped>
.preview-card {
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.18);
}

.preview-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.preview-card__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.preview-card__header h3 {
  margin: 0.35rem 0 0;
  font-size: 1.2rem;
}

.preview-card__count {
  color: #b6b6b6;
}

.recruitment-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.recruitment-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.9rem;
  padding: 0.9rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
}

.recruitment-row__body strong,
.recruitment-row__body span,
.recruitment-row__body small {
  display: block;
}

.recruitment-row__body span,
.recruitment-row__body small {
  color: #a9a9a9;
}

.recruitment-row__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  justify-content: flex-end;
}

.preview-card__empty {
  min-height: 140px;
  display: grid;
  place-items: center;
  text-align: center;
  color: #b6b6b6;
}

@media (max-width: 767.98px) {
  .recruitment-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .recruitment-row__actions {
    justify-content: flex-start;
  }
}
</style>
