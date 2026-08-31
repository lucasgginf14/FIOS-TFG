<template>
  <article
    class="band-list-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="band-list-card__media">
      <AppImage
        :src="band.image"
        :alt="band.name"
        :fallback-src="bandPlaceholder"
        :fallback-label="band.name"
        icon-class="bi bi-vinyl"
      />
    </div>

    <div class="band-list-card__content">
      <div class="band-list-card__header">
        <div>
          <div class="band-list-card__title-row">
            <h3>{{ band.name }}</h3>
            <span class="status-pill" :class="`status-pill--${band.status}`">{{
              band.statusLabel
            }}</span>
          </div>
          <p>{{ band.previewDescription }}</p>
        </div>
      </div>

      <div class="band-list-card__meta">
        <span><i class="bi bi-disc"></i>{{ band.genreLabel }}</span>
        <span><i class="bi bi-geo-alt"></i>{{ band.cityLabel }}</span>
        <span><i class="bi bi-people"></i>{{ band.memberCountLabel }}</span>
        <span><i class="bi bi-clock-history"></i>{{ band.createdAtLabel }}</span>
      </div>

      <div class="band-list-card__actions" @keydown.stop>
        <span class="clickable-card__open-indicator" aria-hidden="true">
          <i class="bi bi-arrow-up-right"></i>
        </span>
        <button
          v-if="canManageMembers"
          type="button"
          class="btn btn-outline-light btn-sm"
          @click.stop="$emit('members', band)"
        >
          {{ membersLabel }}
        </button>
        <button
          v-if="canEdit"
          type="button"
          class="btn btn-outline-light btn-sm"
          @click.stop="$emit('edit', band)"
        >
          {{ editLabel }}
        </button>
        <button
          v-if="canPublishRecruitment"
          type="button"
          class="btn btn-success btn-sm"
          @click.stop="$emit('recruitment', band)"
        >
          {{ recruitmentLabel }}
        </button>
        <button
          v-if="canPublishEvent"
          type="button"
          class="btn btn-success btn-sm"
          @click.stop="$emit('event', band)"
        >
          <i class="bi bi-calendar-plus" aria-hidden="true"></i>
          {{ eventLabel }}
        </button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import AppImage from "@/common/components/AppImage.vue";
import bandPlaceholder from "@/assets/placeholders/band-placeholder.svg";

const props = defineProps({
  band: { type: Object, required: true },
  viewLabel: { type: String, default: "" },
  membersLabel: { type: String, default: "" },
  canManageMembers: { type: Boolean, default: false },
  canEdit: { type: Boolean, default: false },
  canPublishRecruitment: { type: Boolean, default: false },
  canPublishEvent: { type: Boolean, default: false },
  editLabel: { type: String, default: "" },
  recruitmentLabel: { type: String, default: "" },
  eventLabel: { type: String, default: "" }
});

const emit = defineEmits(["view", "members", "edit", "recruitment", "event"]);

const cardLabel = computed(() => `${props.viewLabel}: ${props.band.name}`);

function openDetail() {
  emit("view", props.band);
}
</script>

<style scoped>
.band-list-card {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr);
  gap: 1rem;
  padding: 1rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.18);
}

.band-list-card__media,
.band-list-card__placeholder {
  width: 100%;
  aspect-ratio: 1;
  min-height: 0;
  border-radius: 22px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
}

.band-list-card__media :deep(.app-image),
.band-list-card__media :deep(.app-image__img),
.band-list-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.band-list-card__content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.band-list-card__title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.65rem;
}

.band-list-card__title-row h3 {
  margin: 0;
  font-size: 1.2rem;
}

.band-list-card__header p {
  margin: 0.35rem 0 0;
  color: #b5b5b5;
}

.band-list-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem 1rem;
  color: #d4d4d4;
}

.band-list-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
}

.band-list-card__meta i {
  color: #1db954;
}

.band-list-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.65rem;
}

.band-list-card__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
  background: rgba(255, 255, 255, 0.1);
  color: #d5d5d5;
}

.status-pill--recruiting {
  background: rgba(255, 193, 7, 0.14);
  color: #ffe49c;
}

.status-pill--forming {
  background: rgba(43, 138, 255, 0.14);
  color: #b9dcff;
}

.status-pill--inactive {
  background: rgba(220, 53, 69, 0.14);
  color: #ffbdc6;
}

@media (max-width: 767.98px) {
  .band-list-card {
    grid-template-columns: 1fr;
  }

  .band-list-card__media,
  .band-list-card__placeholder {
    aspect-ratio: auto;
    height: clamp(180px, 45vw, 240px);
  }

  .band-list-card__actions {
    justify-content: flex-start;
  }
}

@media (max-width: 575.98px) {
  .band-list-card__actions .btn {
    width: 100%;
  }
}
</style>
