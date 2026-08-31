<template>
  <div v-if="open" class="modal-shell" @click.self="$emit('close')">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-card__header">
        <div>
          <span class="modal-card__eyebrow">{{ t("reviewBoard.form.eyebrow") }}</span>
          <h3>{{ formTitle }}</h3>
          <p>{{ formIntro }}</p>
        </div>
        <button type="button" class="modal-card__close" :aria-label="$t('common.actions.close')" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div v-if="reservation" class="modal-card__summary">
        <strong>{{ summaryTitle }}</strong>
        <span>{{ summarySubtitle }}</span>
      </div>

      <div v-if="errorMessage" class="modal-card__banner modal-card__banner--error">
        {{ errorMessage }}
      </div>

      <form class="modal-card__body" @submit.prevent="$emit('submit')">
        <div class="rating-grid">
          <div class="rating-field">
            <span>{{ t("reviewBoard.form.overallRating") }}</span>
            <RatingStars
              :model-value="Number(modelValue.overallRating || 0)"
              :label="t('reviewBoard.form.overallRating')"
              @update:model-value="updateField('overallRating', $event)"
            />
          </div>

          <div v-for="field in metricFields" :key="field.name" class="rating-field">
            <span>{{ field.label }}</span>
            <RatingStars
              :model-value="Number(modelValue[field.name] || 0)"
              :label="field.label"
              @update:model-value="updateField(field.name, $event)"
            />
          </div>
        </div>

        <div class="mt-3">
          <label class="form-label" for="review-comment">{{ t("reviewBoard.form.comment") }}</label>
          <textarea
            id="review-comment"
            :value="modelValue.comment"
            rows="5"
            class="form-control"
            :placeholder="t('reviewBoard.form.commentPlaceholder')"
            @input="updateField('comment', $event.target.value)"
          ></textarea>
        </div>

        <div class="modal-card__footer">
          <button type="button" class="btn btn-outline-light" @click="$emit('close')">
            {{ t("reviewBoard.actions.cancel") }}
          </button>
          <button type="submit" class="btn btn-success" :disabled="submitting">
            {{ submitting ? t("reviewBoard.form.submitting") : t("reviewBoard.form.submit") }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import RatingStars from "./RatingStars.vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  modelValue: {
    type: Object,
    required: true
  },
  reservation: {
    type: Object,
    default: null
  },
  submitting: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  }
});

const emit = defineEmits(["close", "submit", "update:modelValue"]);
const { t } = useI18n();

const isUserReview = computed(() => props.reservation?.reviewType === "USER");

const formTitle = computed(() =>
  isUserReview.value ? t("reviewBoard.form.userTitle") : t("reviewBoard.form.title")
);

const formIntro = computed(() =>
  isUserReview.value ? t("reviewBoard.form.userIntro") : t("reviewBoard.form.intro")
);

const summaryTitle = computed(() => {
  if (!props.reservation) {
    return "";
  }

  if (isUserReview.value) {
    return buildUserName(props.reservation.reviewedUser) || t("reviewBoard.pending.fallbackUser");
  }

  return props.reservation.musicalSpace?.name || t("reviewBoard.pending.fallbackSpace");
});

const summarySubtitle = computed(() => {
  const parts = isUserReview.value
    ? [
        props.reservation?.musicalSpace?.name,
        props.reservation?.musicalSpace?.city,
        props.reservation?.musicalSpace?.province
      ]
    : [props.reservation?.musicalSpace?.city, props.reservation?.musicalSpace?.province];

  return parts.filter(Boolean).join(", ");
});

const metricFields = computed(() => {
  if (isUserReview.value) {
    return [
      { name: "communicationRating", label: t("reviewBoard.form.communicationRating") },
      { name: "punctualityRating", label: t("reviewBoard.form.punctualityRating") },
      { name: "careRating", label: t("reviewBoard.form.careRating") }
    ];
  }

  return [
    { name: "soundQualityRating", label: t("reviewBoard.form.soundQualityRating") },
    { name: "equipmentRating", label: t("reviewBoard.form.equipmentRating") },
    { name: "cleanlinessRating", label: t("reviewBoard.form.cleanlinessRating") },
    { name: "locationRating", label: t("reviewBoard.form.locationRating") }
  ];
});

function updateField(field, value) {
  emit("update:modelValue", {
    ...props.modelValue,
    [field]: value
  });
}

function buildUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}
</script>

<style scoped>
.modal-shell {
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

.modal-card {
  width: min(100%, 860px);
  max-height: calc(100vh - 2rem);
  overflow: auto;
  padding: 1.2rem;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.32);
  color: #ffffff;
}

.modal-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.modal-card__eyebrow {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.modal-card__header h3 {
  margin: 0.35rem 0;
  font-size: 1.35rem;
}

.modal-card__header p,
.modal-card__summary span {
  margin: 0;
  color: #b8b8b8;
}

.modal-card__close {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.modal-card__summary {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-top: 1rem;
  padding: 0.95rem 1rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.modal-card__banner {
  margin-top: 1rem;
  padding: 0.85rem 0.95rem;
  border-radius: 16px;
}

.modal-card__banner--error {
  background: rgba(220, 53, 69, 0.08);
  color: #ffb3bd;
}

.modal-card__body {
  margin-top: 1rem;
}

.rating-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.rating-field {
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.rating-field span {
  color: #d7d7d7;
  font-weight: 600;
}

.modal-card :deep(.form-label) {
  color: #c4c4c4;
}

.modal-card :deep(.form-control) {
  min-height: 120px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.modal-card :deep(.form-control::placeholder) {
  color: #8d8d8d;
}

.modal-card :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.modal-card__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.2rem;
}

@media (max-width: 767.98px) {
  .rating-grid {
    grid-template-columns: 1fr;
  }

  .modal-card__header,
  .modal-card__footer {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
