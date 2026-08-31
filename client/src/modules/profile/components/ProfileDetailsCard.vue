<template>
  <section class="profile-card profile-details">
    <div class="profile-card__header">
      <span class="profile-card__eyebrow">{{ t("profile.details.eyebrow") }}</span>
      <h2>{{ t("profile.details.title") }}</h2>
    </div>

    <div class="profile-details__grid">
      <article v-for="item in items" :key="item.label" class="profile-details__item">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";

import { formatPrimaryInstrumentName } from "../profileInstrumentUtils.js";

const props = defineProps({
  account: {
    type: Object,
    required: true
  },
  locale: {
    type: String,
    default: "es"
  }
});

const { t } = useI18n();

const items = computed(() => {
  const birthDate = props.account.birthDate
    ? new Intl.DateTimeFormat(props.locale, {
        year: "numeric",
        month: "short",
        day: "numeric"
      }).format(new Date(props.account.birthDate))
    : t("profile.fallbacks.noData");

  const createdAt = props.account.createdAt
    ? new Intl.DateTimeFormat(props.locale, {
        year: "numeric",
        month: "short",
        day: "numeric"
      }).format(new Date(props.account.createdAt))
    : t("profile.fallbacks.noData");

  return [
    { label: t("profile.fields.name"), value: props.account.name || t("profile.fallbacks.noData") },
    { label: t("profile.fields.firstSurname"), value: props.account.firstSurname || t("profile.fallbacks.noData") },
    { label: t("profile.fields.secondSurname"), value: props.account.secondSurname || t("profile.fallbacks.noData") },
    { label: t("profile.fields.email"), value: props.account.email || t("profile.fallbacks.noData") },
    { label: t("profile.fields.phone"), value: props.account.phone || t("profile.fallbacks.noData") },
    { label: t("profile.fields.birthDate"), value: birthDate },
    {
      label: t("profile.fields.instrument"),
      value: formatPrimaryInstrumentName(props.account, t("profile.fallbacks.noInstrument"))
    },
    { label: t("profile.fields.createdAt"), value: createdAt }
  ];
});
</script>

<style scoped>
.profile-card {
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.profile-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-card__header h2 {
  margin: 0.35rem 0 0;
  font-size: 1.3rem;
}

.profile-details__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.85rem;
  margin-top: 1rem;
}

.profile-details__item {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.profile-details__item span {
  display: block;
  color: #9f9f9f;
  font-size: 0.84rem;
}

.profile-details__item strong {
  display: block;
  margin-top: 0.45rem;
  font-size: 0.95rem;
}

@media (max-width: 767.98px) {
  .profile-details__grid {
    grid-template-columns: 1fr;
  }
}
</style>
