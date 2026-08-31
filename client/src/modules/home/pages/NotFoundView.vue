<template>
  <PageLayout
    :title="t('home.notFound.title')"
    eyebrow="404"
    :description="t('home.notFound.description')"
    theme="dark"
  >
    <div class="not-found-card">
      <div class="not-found-card__code">404</div>
      <div>
        <p class="not-found-card__text">
          {{ t("home.notFound.text") }}
        </p>
        <RouterLink class="btn btn-success not-found-card__cta" :to="{ name: 'Home' }">
          {{ t("home.notFound.action") }}
        </RouterLink>
      </div>
    </div>
  </PageLayout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import PageLayout from "@/common/components/PageLayout.vue";

const { t } = useI18n();
</script>

<style scoped>
.not-found-card {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 1.5rem;
  align-items: center;
  max-width: 760px;
  padding: 1.5rem;
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.16), transparent 34%),
    linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.22);
}

.not-found-card__code {
  display: grid;
  place-items: center;
  width: 112px;
  aspect-ratio: 1;
  border-radius: 24px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 2rem;
  font-weight: 800;
}

.not-found-card__text {
  max-width: 520px;
  margin-bottom: 1rem;
  color: #b6b6b6;
  line-height: 1.65;
}

.not-found-card__cta {
  font-weight: 700;
}

@media (max-width: 575.98px) {
  .not-found-card {
    grid-template-columns: 1fr;
  }

  .not-found-card__code {
    width: 96px;
  }
}
</style>
