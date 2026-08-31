<template>
  <section class="preview-card">
    <div class="preview-card__header">
      <div>
        <span class="preview-card__eyebrow">{{ eyebrow }}</span>
        <h3>{{ title }}</h3>
      </div>
      <span class="preview-card__count">{{ countLabel }}</span>
    </div>

    <div v-if="members.length" class="member-list">
      <article v-for="member in members" :key="member.id" class="member-row">
        <div class="member-row__avatar">
          <AppImage
            :src="member.user?.profileImage"
            :alt="fullName(member.user) || memberInstrumentLabel(member)"
            :fallback-src="avatarPlaceholder"
            icon-class="bi bi-person"
          />
        </div>
        <div class="member-row__body">
          <strong>{{ fullName(member.user) }}</strong>
          <span>{{ memberInstrumentLabel(member) }}</span>
        </div>
        <small>{{ formatJoin(member.joinDate) }}</small>
      </article>
    </div>

    <div v-else class="preview-card__empty">{{ emptyLabel }}</div>
  </section>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import { formatDateTime } from "@/modules/musical-spaces/spaceDetailUtils";
import { formatMemberInstruments } from "../bandMemberUtils";

defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  countLabel: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  members: { type: Array, default: () => [] }
});

const { locale, t } = useI18n();

function fullName(user) {
  return [user?.name, user?.firstSurname].filter(Boolean).join(" ");
}

function formatJoin(value) {
  return value ? formatDateTime(value, locale.value) : "--";
}

function memberInstrumentLabel(member) {
  return formatMemberInstruments(member, t("bands.members.noInstruments"));
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

.member-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.member-row {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) auto;
  gap: 0.85rem;
  align-items: center;
  padding: 0.8rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
}

.member-row__avatar {
  overflow: hidden;
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-row__avatar :deep(.app-image),
.member-row__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.member-row__avatar :deep(.app-image) {
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-row__body strong,
.member-row__body span,
.member-row small {
  display: block;
}

.member-row__body span,
.member-row small {
  color: #a9a9a9;
}

.preview-card__empty {
  min-height: 140px;
  display: grid;
  place-items: center;
  text-align: center;
  color: #b6b6b6;
}

@media (max-width: 575.98px) {
  .member-row {
    grid-template-columns: 46px 1fr;
  }

  .member-row small {
    grid-column: 2;
  }
}
</style>
