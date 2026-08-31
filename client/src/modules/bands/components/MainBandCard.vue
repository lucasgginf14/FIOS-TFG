<template>
  <section
    class="main-band-card clickable-card"
    role="link"
    tabindex="0"
    :aria-label="cardLabel"
    @click="openDetail"
    @keydown.enter.prevent="openDetail"
    @keydown.space.prevent="openDetail"
  >
    <div class="main-band-card__media">
      <AppImage
        :src="band.image"
        :alt="band.name"
        :fallback-src="bandPlaceholder"
        :fallback-label="band.name"
        icon-class="bi bi-music-note-list"
      />
    </div>

    <div class="main-band-card__content">
      <div class="main-band-card__topline">
        <span class="genre-pill">{{ band.genreLabel }}</span>
        <span class="status-pill" :class="`status-pill--${band.status}`">{{
          band.statusLabel
        }}</span>
      </div>

      <h2>{{ band.name }}</h2>
      <p>{{ band.previewDescription }}</p>

      <div class="main-band-card__meta">
        <span><i class="bi bi-geo-alt"></i>{{ band.cityLabel }}</span>
        <span><i class="bi bi-people"></i>{{ band.memberCountLabel }}</span>
      </div>

      <div class="member-avatars">
        <div v-for="member in band.heroMembers" :key="member.id" class="member-avatars__item">
          <span class="member-avatars__image">
            <AppImage
              :src="member.user?.profileImage"
              :alt="memberName(member)"
              :fallback-src="avatarPlaceholder"
              icon-class="bi bi-person"
            />
          </span>
          <small :title="memberName(member)">{{ memberName(member) }}</small>
        </div>
      </div>
    </div>

    <div class="main-band-card__actions" @keydown.stop>
      <span class="clickable-card__open-indicator" aria-hidden="true">
        <i class="bi bi-arrow-up-right"></i>
      </span>
      <button
        v-if="canManageMembers"
        type="button"
        class="btn btn-outline-light"
        @click.stop="$emit('members', band)"
      >
        {{ membersLabel }}
      </button>
      <button
        v-if="canPublishRecruitment"
        type="button"
        class="btn btn-success"
        @click.stop="$emit('recruitment', band)"
      >
        {{ recruitmentLabel }}
      </button>
      <button
        v-if="canPublishEvent"
        type="button"
        class="btn btn-success"
        @click.stop="$emit('event', band)"
      >
        <i class="bi bi-calendar-plus" aria-hidden="true"></i>
        {{ eventLabel }}
      </button>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import bandPlaceholder from "@/assets/placeholders/band-placeholder.svg";

const props = defineProps({
  band: {
    type: Object,
    required: true
  },
  viewLabel: {
    type: String,
    default: ""
  },
  membersLabel: {
    type: String,
    default: ""
  },
  canManageMembers: {
    type: Boolean,
    default: false
  },
  canPublishRecruitment: {
    type: Boolean,
    default: false
  },
  recruitmentLabel: {
    type: String,
    default: ""
  },
  canPublishEvent: {
    type: Boolean,
    default: false
  },
  eventLabel: {
    type: String,
    default: ""
  }
});

const emit = defineEmits(["view", "members", "recruitment", "event"]);
const { t } = useI18n();

const cardLabel = computed(() => `${props.viewLabel}: ${props.band.name}`);

function openDetail() {
  emit("view", props.band);
}

function memberName(member) {
  return [member.user?.name, member.user?.firstSurname].filter(Boolean).join(" ") || roleLabel(member.roleInBand);
}

function roleLabel(role) {
  return t(`bands.roles.${role}`);
}
</script>

<style scoped>
.main-band-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) auto;
  gap: 1rem;
  padding: 1.3rem;
  border-radius: 30px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.16), transparent 34%),
    linear-gradient(180deg, #101010 0%, #171717 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.main-band-card__media,
.main-band-card__placeholder {
  width: 100%;
  height: clamp(220px, 22vw, 280px);
  min-height: 0;
  border-radius: 26px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
}

.main-band-card__media :deep(.app-image),
.main-band-card__media :deep(.app-image__img),
.main-band-card__media :deep(.app-image__placeholder) {
  height: 100%;
  min-height: 0;
}

.main-band-card__content {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.main-band-card__topline {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
}

.genre-pill,
.status-pill {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.8rem;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
}

.genre-pill {
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
}

.status-pill {
  background: rgba(255, 255, 255, 0.08);
  color: #d6d6d6;
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

.main-band-card h2 {
  margin: 0;
  font-size: clamp(1.6rem, 3vw, 2.2rem);
}

.main-band-card p {
  margin: 0;
  color: #c1c1c1;
  line-height: 1.75;
}

.main-band-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem 1rem;
  color: #d4d4d4;
}

.main-band-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
}

.main-band-card__meta i {
  color: #1db954;
}

.member-avatars {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.member-avatars__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  width: 5.75rem;
  min-width: 0;
}

.member-avatars__image {
  display: block;
  overflow: hidden;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-avatars__image :deep(.app-image),
.member-avatars__image :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.member-avatars__image :deep(.app-image) {
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.member-avatars__item small {
  display: -webkit-box;
  overflow: hidden;
  color: #d8d8d8;
  font-size: 0.72rem;
  line-height: 1.15;
  text-align: center;
  text-transform: none;
  overflow-wrap: anywhere;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.main-band-card__actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  justify-content: center;
  min-width: 190px;
}

.main-band-card__actions .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
}

@media (max-width: 1199.98px) {
  .main-band-card {
    grid-template-columns: 1fr;
  }

  .main-band-card__media,
  .main-band-card__placeholder {
    height: clamp(220px, 34vw, 320px);
  }

  .main-band-card__actions {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: flex-start;
    min-width: 0;
  }
}

@media (max-width: 575.98px) {
  .main-band-card__actions .btn {
    width: 100%;
  }
}
</style>
