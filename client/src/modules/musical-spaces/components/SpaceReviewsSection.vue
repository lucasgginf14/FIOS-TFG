<template>
  <section class="detail-section">
    <div class="detail-section__header">
      <div>
        <div class="detail-section__eyebrow">{{ eyebrow }}</div>
        <h2>{{ title }}</h2>
      </div>
      <div class="reviews-summary">
        <strong>{{ ratingLabel }}</strong>
        <span>{{ countLabel }}</span>
      </div>
    </div>

    <div v-if="loading" class="empty-state">{{ loadingLabel }}</div>
    <div v-else-if="errorMessage" class="empty-state empty-state--error">{{ errorMessage }}</div>
    <div v-else-if="!reviews.length" class="empty-state">{{ emptyLabel }}</div>
    <div v-else class="reviews-list">
      <article v-for="review in reviews" :key="review.id" class="review-card">
        <div class="review-card__header">
          <div>
            <h3>{{ review.author }}</h3>
            <span>{{ review.createdAtLabel }}</span>
          </div>
          <div class="review-card__rating">
            <i class="bi bi-star-fill"></i>
            {{ review.overallRating }}
          </div>
        </div>

        <p class="review-card__comment">{{ review.comment || fallbackComment }}</p>

        <div class="review-card__metrics">
          <span>{{ soundLabel }} {{ review.soundQualityRating }}/5</span>
          <span>{{ equipmentLabel }} {{ review.equipmentRating }}/5</span>
          <span>{{ cleanlinessLabel }} {{ review.cleanlinessRating }}/5</span>
          <span>{{ locationLabel }} {{ review.locationRating }}/5</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  ratingLabel: { type: String, default: "" },
  countLabel: { type: String, default: "" },
  loadingLabel: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  fallbackComment: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  soundLabel: { type: String, default: "" },
  equipmentLabel: { type: String, default: "" },
  cleanlinessLabel: { type: String, default: "" },
  locationLabel: { type: String, default: "" },
  loading: { type: Boolean, default: false },
  reviews: { type: Array, default: () => [] }
});
</script>

<style scoped>
.detail-section {
  padding: 1.25rem;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}
.detail-section__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}
.detail-section__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}
.detail-section h2 {
  margin: 0.35rem 0 0;
  color: #ffffff;
  font-size: 1.35rem;
  font-weight: 700;
}
.reviews-summary {
  min-width: 112px;
  padding: 0.7rem 0.85rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 18px;
  background: rgba(29, 185, 84, 0.08);
  text-align: right;
}
.reviews-summary strong {
  display: block;
  color: #ffffff;
  font-size: 1.3rem;
  line-height: 1;
}
.reviews-summary span {
  color: #9e9e9e;
  font-size: 0.82rem;
}
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}
.review-card {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.review-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.8rem;
}
.review-card__header h3 {
  margin: 0;
  color: #ffffff;
  font-size: 1rem;
  font-weight: 700;
}
.review-card__header span {
  color: #9e9e9e;
  font-size: 0.86rem;
}
.review-card__rating {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  min-height: 34px;
  padding: 0 0.7rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.08);
  color: #ffffff;
  font-weight: 700;
}
.review-card__rating i {
  color: #1db954;
}
.review-card__comment {
  margin-bottom: 0.9rem;
  color: #d1d1d1;
  line-height: 1.65;
}
.review-card__metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  color: #bdbdbd;
  font-size: 0.86rem;
}
.review-card__metrics span {
  padding: 0.35rem 0.55rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.empty-state {
  padding: 1.1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #d1d1d1;
}
.empty-state--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}
@media (max-width: 767.98px) {
  .detail-section__header,
  .review-card__header {
    flex-direction: column;
  }

  .reviews-summary {
    width: 100%;
    text-align: left;
  }
}
</style>
