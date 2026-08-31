<template>
  <section class="profile-card profile-card--hero">
    <div class="profile-card__hero">
      <div class="profile-avatar">
        <AppImage
          :src="account.profileImage"
          :alt="fullName"
          :fallback-src="avatarPlaceholder"
          :fallback-label="fullName"
          icon-class="bi bi-person"
        />
      </div>

      <div class="profile-card__copy">
        <span class="profile-card__eyebrow">{{ t("profile.header.eyebrow") }}</span>
        <h2>{{ fullName }}</h2>
        <p>{{ account.email }}</p>

        <div class="profile-card__meta">
          <span class="profile-pill">
            <i class="bi bi-shield-check"></i>
            {{ t(`profile.roles.${account.platformRole || "USER"}`) }}
          </span>
          <span class="profile-pill" :class="account.active ? 'profile-pill--success' : 'profile-pill--muted'">
            <i class="bi" :class="account.active ? 'bi-check-circle' : 'bi-x-circle'"></i>
            {{ account.active ? t("profile.status.active") : t("profile.status.inactive") }}
          </span>
          <span class="profile-pill profile-pill--muted">
            <i class="bi bi-calendar3"></i>
            {{ t("profile.header.memberSince", { date: createdAtLabel }) }}
          </span>
        </div>
      </div>

      <div class="profile-card__actions">
        <button
          type="button"
          class="btn"
          :class="editOpen ? 'btn-success' : 'btn-outline-light'"
          @click="$emit('toggle-edit')"
        >
          {{ t("profile.actions.editProfile") }}
        </button>
        <button
          type="button"
          class="btn"
          :class="passwordOpen ? 'btn-success' : 'btn-outline-light'"
          @click="$emit('toggle-password')"
        >
          {{ t("profile.actions.changePassword") }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import AppImage from "@/common/components/AppImage.vue";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";

const props = defineProps({
  account: {
    type: Object,
    required: true
  },
  locale: {
    type: String,
    default: "es"
  },
  editOpen: {
    type: Boolean,
    default: false
  },
  passwordOpen: {
    type: Boolean,
    default: false
  }
});

defineEmits(["toggle-edit", "toggle-password"]);

const { t } = useI18n();

const fullName = computed(() =>
  [props.account.name, props.account.firstSurname, props.account.secondSurname].filter(Boolean).join(" ")
    || props.account.email
    || t("profile.fallbacks.user")
);

const createdAtLabel = computed(() => {
  if (!props.account.createdAt) {
    return t("profile.fallbacks.noData");
  }

  return new Intl.DateTimeFormat(props.locale, {
    year: "numeric",
    month: "short",
    day: "numeric"
  }).format(new Date(props.account.createdAt));
});
</script>

<style scoped>
.profile-card {
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.profile-card--hero {
  padding: 1.3rem;
}

.profile-card__hero {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 1rem;
  align-items: center;
}

.profile-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  width: 96px;
  height: 96px;
  border-radius: 28px;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.profile-avatar :deep(.app-image) {
  width: 100%;
  height: 100%;
  min-height: 0;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.profile-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-card__copy h2 {
  margin: 0.4rem 0 0.3rem;
  font-size: clamp(1.8rem, 3vw, 2.6rem);
  letter-spacing: -0.04em;
}

.profile-card__copy p {
  margin: 0;
  color: #b7b7b7;
}

.profile-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  margin-top: 1rem;
}

.profile-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  min-height: 34px;
  padding: 0 0.85rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #ffffff;
  font-size: 0.82rem;
  font-weight: 700;
}

.profile-pill--success {
  color: #dfffe9;
  background: rgba(29, 185, 84, 0.12);
}

.profile-pill--muted {
  color: #d5d5d5;
}

.profile-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.profile-card__actions .btn {
  min-height: 44px;
  border-radius: 16px;
}

@media (max-width: 991.98px) {
  .profile-card__hero {
    grid-template-columns: 1fr;
    justify-items: start;
  }

  .profile-card__actions {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 575.98px) {
  .profile-card__actions .btn {
    width: 100%;
  }
}
</style>
