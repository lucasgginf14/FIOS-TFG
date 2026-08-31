<template>
  <div class="profile-page">
    <section class="profile-shell">
      <div class="container py-5">
        <header class="profile-page__header">
          <div class="profile-page__copy">
            <span class="profile-page__eyebrow">{{ t("profile.header.eyebrow") }}</span>
            <h1>{{ t("profile.header.title") }}</h1>
            <p>{{ t("profile.header.subtitle") }}</p>
          </div>
        </header>

        <div v-if="loading" class="state-card">
          <div class="spinner-border text-success" role="status"></div>
          <p>{{ t("profile.states.loading") }}</p>
        </div>

        <div v-else-if="fatalError" class="state-card state-card--error">
          <strong>{{ t("profile.states.errorTitle") }}</strong>
          <p>{{ fatalError }}</p>
          <button type="button" class="btn btn-outline-light" @click="loadProfilePage">
            {{ t("profile.actions.retry") }}
          </button>
        </div>

        <template v-else-if="account">
          <div v-if="pageNotice" class="page-notice" :class="`page-notice--${pageNotice.type}`">
            {{ pageNotice.message }}
          </div>

          <ProfileHeaderCard
            :account="account"
            :locale="locale"
            :edit-open="editOpen"
            :password-open="passwordOpen"
            class="section-spacing"
            @toggle-edit="toggleEdit"
            @toggle-password="togglePassword"
          />

          <section class="profile-stats section-spacing">
            <article v-for="card in statCards" :key="card.id" class="profile-stat-card">
              <div class="profile-stat-card__icon">
                <i :class="card.icon"></i>
              </div>
              <div>
                <div class="profile-stat-card__value">{{ card.value }}</div>
                <div class="profile-stat-card__label">{{ card.label }}</div>
              </div>
            </article>
          </section>

          <div class="profile-layout section-spacing">
            <main class="profile-main">
              <ProfileDetailsCard :account="account" :locale="locale" />

              <ProfileEditForm
                v-if="editOpen"
                :model-value="editForm"
                :instruments="instruments"
                :instruments-loading="instrumentsLoading"
                :instrument-catalog-error="instrumentCatalogError"
                :email="account.email"
                :submitting="editSubmitting"
                :error-message="editError"
                :field-errors="editFieldErrors"
                @update:model-value="editForm = $event"
                @submit="submitProfile"
                @cancel="closeEdit"
              />

              <PasswordChangeForm
                v-if="passwordOpen"
                :model-value="passwordForm"
                :submitting="passwordSubmitting"
                :error-message="passwordError"
                :success-message="passwordSuccess"
                :field-errors="passwordFieldErrors"
                @update:model-value="passwordForm = $event"
                @submit="submitPassword"
                @cancel="closePassword"
              />
            </main>

            <aside class="profile-side">
              <ProfileImageForm
                :current-image="account.profileImage"
                :model-value="imageForm"
                :submitting="imageSubmitting"
                :removing="imageRemoving"
                :error-message="imageError"
                :success-message="imageSuccess"
                @update:model-value="imageForm = $event"
                @submit="submitImage"
                @remove="removeImage"
              />
            </aside>
          </div>
        </template>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { getApiErrorMessage, getApiFieldErrors } from "@/common/apiErrors";
import { isValidPhone } from "@/common/validation";
import auth from "@/common/auth";
import BandRepository from "@/repositories/BandRepository";
import FavoriteSpaceRepository from "@/repositories/FavoriteSpaceRepository";
import InstrumentRepository from "@/repositories/InstrumentRepository";
import MessageRepository from "@/repositories/MessageRepository";
import ReservationSessionRepository from "@/repositories/ReservationSessionRepository";
import ProfileDetailsCard from "../components/ProfileDetailsCard.vue";
import ProfileEditForm from "../components/ProfileEditForm.vue";
import ProfileHeaderCard from "../components/ProfileHeaderCard.vue";
import ProfileImageForm from "../components/ProfileImageForm.vue";
import PasswordChangeForm from "../components/PasswordChangeForm.vue";
import {
  buildPrimaryInstrumentIds,
  getPrimaryInstrumentFromAccount
} from "../profileInstrumentUtils.js";

const { locale, t } = useI18n();

const loading = ref(true);
const fatalError = ref("");
const pageNotice = ref(null);
const account = ref(null);
const instruments = ref([]);

const editOpen = ref(false);
const passwordOpen = ref(false);

const editSubmitting = ref(false);
const instrumentsLoading = ref(false);
const passwordSubmitting = ref(false);
const imageSubmitting = ref(false);
const imageRemoving = ref(false);

const editError = ref("");
const passwordError = ref("");
const passwordSuccess = ref("");
const imageError = ref("");
const imageSuccess = ref("");
const instrumentCatalogError = ref("");
const editFieldErrors = ref({});
const passwordFieldErrors = ref({});

const editForm = ref(createEditForm());
const passwordForm = ref(createPasswordForm());
const imageForm = ref(createImageForm());
const stats = ref(createStats());

const statCards = computed(() => [
  {
    id: "reservations",
    icon: "bi bi-calendar2-check",
    value: formatStatValue(stats.value.reservations),
    label: t("profile.summary.reservations")
  },
  {
    id: "bands",
    icon: "bi bi-people",
    value: formatStatValue(stats.value.bands),
    label: t("profile.summary.bands")
  },
  {
    id: "favorites",
    icon: "bi bi-heart",
    value: formatStatValue(stats.value.favorites),
    label: t("profile.summary.favorites")
  },
  {
    id: "messages",
    icon: "bi bi-chat-dots",
    value: formatStatValue(stats.value.unreadMessages),
    label: t("profile.summary.unreadMessages")
  }
]);

onMounted(() => {
  loadProfilePage();
});

async function loadProfilePage() {
  loading.value = true;
  fatalError.value = "";
  pageNotice.value = null;
  instrumentCatalogError.value = "";
  instruments.value = [];
  stats.value = createStats();

  const [
    accountResult,
    reservationsResult,
    bandsResult,
    favoritesResult,
    unreadResult,
    instrumentsResult
  ] = await Promise.allSettled([
    auth.getAccountInfo(),
    ReservationSessionRepository.getMine(),
    BandRepository.getMine(),
    FavoriteSpaceRepository.getMine(),
    MessageRepository.getUnread(),
    InstrumentRepository.getAll()
  ]);

  if (accountResult.status !== "fulfilled") {
    fatalError.value = getApiErrorMessage(accountResult.reason, t, "profile.states.error");
    loading.value = false;
    return;
  }

  applyProfile(accountResult.value);
  instruments.value = instrumentsResult.status === "fulfilled" ? (instrumentsResult.value ?? []) : [];
  instrumentCatalogError.value =
    instrumentsResult.status === "rejected"
      ? getApiErrorMessage(instrumentsResult.reason, t, "profile.edit.instrumentCatalogError")
      : "";

  stats.value = {
    reservations: reservationsResult.status === "fulfilled" ? reservationsResult.value?.length ?? 0 : null,
    bands: bandsResult.status === "fulfilled" ? bandsResult.value?.length ?? 0 : null,
    favorites: favoritesResult.status === "fulfilled" ? favoritesResult.value?.length ?? 0 : null,
    unreadMessages:
      unreadResult.status === "fulfilled" ? unreadResult.value?.totalUnreadMessages ?? 0 : null
  };

  loading.value = false;
}

function applyProfile(user) {
  account.value = user;
  auth.syncAuthenticatedUser(user);
  editForm.value = createEditForm(user);
  imageForm.value = createImageForm(user);
}

function toggleEdit() {
  editOpen.value = !editOpen.value;
  if (editOpen.value) {
    passwordOpen.value = false;
    editError.value = "";
    editFieldErrors.value = {};
    pageNotice.value = null;
    editForm.value = createEditForm(account.value);
    loadInstrumentCatalog();
  }
}

function closeEdit() {
  editOpen.value = false;
  editError.value = "";
  editFieldErrors.value = {};
  editForm.value = createEditForm(account.value);
}

function togglePassword() {
  passwordOpen.value = !passwordOpen.value;
  if (passwordOpen.value) {
    editOpen.value = false;
    passwordError.value = "";
    passwordSuccess.value = "";
    passwordFieldErrors.value = {};
    passwordForm.value = createPasswordForm();
  }
}

function closePassword() {
  passwordOpen.value = false;
  passwordError.value = "";
  passwordSuccess.value = "";
  passwordFieldErrors.value = {};
  passwordForm.value = createPasswordForm();
}

async function submitProfile() {
  editFieldErrors.value = getProfileClientFieldErrors(editForm.value, t);

  if (hasFieldErrors(editFieldErrors.value)) {
    editError.value = firstFieldError(editFieldErrors.value) || t("profile.edit.validationRequired");
    return;
  }

  editSubmitting.value = true;
  editError.value = "";
  editFieldErrors.value = {};
  pageNotice.value = null;

  try {
    const updated = await auth.updateProfile({
      name: editForm.value.name.trim(),
      firstSurname: editForm.value.firstSurname.trim(),
      secondSurname: editForm.value.secondSurname.trim(),
      phone: editForm.value.phone.trim(),
      birthDate: editForm.value.birthDate || null,
      instrumentIds: buildPrimaryInstrumentIds(editForm.value.instrumentId)
    });

    applyProfile(updated);
    editOpen.value = false;
    pageNotice.value = {
      type: "success",
      message: t("profile.notices.profileUpdated")
    };
  } catch (error) {
    editFieldErrors.value = getApiFieldErrors(error, t);
    editError.value = getApiErrorMessage(error, t, "profile.edit.error");
  } finally {
    editSubmitting.value = false;
  }
}

async function submitPassword() {
  passwordFieldErrors.value = getPasswordClientFieldErrors(passwordForm.value, t);

  if (hasFieldErrors(passwordFieldErrors.value)) {
    passwordError.value = firstFieldError(passwordFieldErrors.value) || t("profile.password.validationRequired");
    passwordSuccess.value = "";
    return;
  }

  passwordSubmitting.value = true;
  passwordError.value = "";
  passwordSuccess.value = "";
  passwordFieldErrors.value = {};

  try {
    await auth.updatePassword({
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
      confirmNewPassword: passwordForm.value.confirmNewPassword
    });

    passwordForm.value = createPasswordForm();
    passwordSuccess.value = t("profile.notices.passwordUpdated");
  } catch (error) {
    passwordFieldErrors.value = getApiFieldErrors(error, t);
    passwordError.value = getApiErrorMessage(error, t, "profile.password.error");
  } finally {
    passwordSubmitting.value = false;
  }
}

async function submitImage() {
  const validationError = validateImageForm(imageForm.value, t);

  if (validationError) {
    imageError.value = validationError;
    imageSuccess.value = "";
    return;
  }

  imageSubmitting.value = true;
  imageError.value = "";
  imageSuccess.value = "";

  try {
    const updated = await auth.updateProfileImage(imageForm.value.profileImage.trim());
    applyProfile(updated);
    imageSuccess.value = t("profile.notices.imageUpdated");
  } catch (error) {
    imageError.value = getApiErrorMessage(error, t, "profile.image.error");
  } finally {
    imageSubmitting.value = false;
  }
}

async function removeImage() {
  if (!account.value?.profileImage) {
    return;
  }

  imageRemoving.value = true;
  imageError.value = "";
  imageSuccess.value = "";

  try {
    const updated = await auth.removeProfileImage();
    applyProfile(updated);
    imageSuccess.value = t("profile.notices.imageRemoved");
  } catch (error) {
    imageError.value = getApiErrorMessage(error, t, "profile.image.removeError");
  } finally {
    imageRemoving.value = false;
  }
}

function createEditForm(user = null) {
  const primaryInstrument = getPrimaryInstrumentFromAccount(user ?? {});

  return {
    name: user?.name ?? "",
    firstSurname: user?.firstSurname ?? "",
    secondSurname: user?.secondSurname ?? "",
    phone: user?.phone ?? "",
    birthDate: user?.birthDate ?? "",
    instrumentId: primaryInstrument?.id ? String(primaryInstrument.id) : ""
  };
}

function createPasswordForm() {
  return {
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: ""
  };
}

function createImageForm(user = null) {
  return {
    profileImage: user?.profileImage ?? ""
  };
}

function createStats() {
  return {
    reservations: null,
    bands: null,
    favorites: null,
    unreadMessages: null
  };
}

function getProfileClientFieldErrors(form, translate) {
  const errors = {};

  if (!form.name.trim()) {
    errors.name = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (form.name.trim().length > 100) {
    errors.name = translate("apiFieldErrors.INVALID_LENGTH");
  }

  if (!form.firstSurname.trim()) {
    errors.firstSurname = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (form.firstSurname.trim().length > 100) {
    errors.firstSurname = translate("apiFieldErrors.INVALID_LENGTH");
  }

  if (form.secondSurname.trim().length > 100) {
    errors.secondSurname = translate("apiFieldErrors.INVALID_LENGTH");
  }

  if (!form.phone.trim()) {
    errors.phone = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (!isValidPhone(form.phone)) {
    errors.phone = translate("apiFieldErrors.INVALID_PHONE");
  } else if (form.phone.trim().length > 30) {
    errors.phone = translate("apiFieldErrors.INVALID_LENGTH");
  }

  if (form.birthDate && form.birthDate > new Date().toISOString().slice(0, 10)) {
    errors.birthDate = translate("profile.edit.validationBirthDate");
  }

  if (
    form.instrumentId &&
    instruments.value.length &&
    !instruments.value.some((instrument) => String(instrument.id) === String(form.instrumentId))
  ) {
    errors.instrumentId = translate("profile.edit.validationInstrument");
  }

  return errors;
}

async function loadInstrumentCatalog() {
  if (instruments.value.length || instrumentsLoading.value) {
    return;
  }

  instrumentsLoading.value = true;
  instrumentCatalogError.value = "";

  try {
    instruments.value = (await InstrumentRepository.getAll()) ?? [];
  } catch (error) {
    instrumentCatalogError.value = getApiErrorMessage(error, t, "profile.edit.instrumentCatalogError");
  } finally {
    instrumentsLoading.value = false;
  }
}

function getPasswordClientFieldErrors(form, translate) {
  const errors = {};

  if (!form.currentPassword) {
    errors.currentPassword = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (form.currentPassword.length > 100) {
    errors.currentPassword = translate("apiFieldErrors.INVALID_LENGTH");
  }

  if (!form.newPassword) {
    errors.newPassword = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (form.newPassword.length < 8 || form.newPassword.length > 100) {
    errors.newPassword = translate("profile.password.validationLength");
  } else if (!isStrongPassword(form.newPassword)) {
    errors.newPassword = translate("profile.password.validationPolicy");
  }

  if (!form.confirmNewPassword) {
    errors.confirmNewPassword = translate("apiFieldErrors.FIELD_REQUIRED");
  } else if (form.confirmNewPassword.length < 8 || form.confirmNewPassword.length > 100) {
    errors.confirmNewPassword = translate("profile.password.validationLength");
  } else if (!isStrongPassword(form.confirmNewPassword)) {
    errors.confirmNewPassword = translate("profile.password.validationPolicy");
  }

  if (!errors.newPassword && !errors.confirmNewPassword && form.newPassword !== form.confirmNewPassword) {
    errors.confirmNewPassword = translate("profile.password.validationMatch");
  }

  return errors;
}

function isStrongPassword(value) {
  return /[A-Za-z]/.test(value) && /\d/.test(value);
}

function hasFieldErrors(errors) {
  return Object.keys(errors).length > 0;
}

function firstFieldError(errors) {
  return Object.values(errors)[0] ?? "";
}

function validateImageForm(form, translate) {
  const value = form.profileImage.trim();

  if (!value) {
    return translate("profile.image.validationRequired");
  }

  try {
    new URL(value);
  } catch {
    return translate("profile.image.validationUrl");
  }

  return "";
}

function formatStatValue(value) {
  return value == null ? "--" : value;
}
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 24%),
    linear-gradient(180deg, #050505 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.profile-shell {
  padding-bottom: 3rem;
}

.profile-page__header {
  display: block;
}

.profile-page__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-page__copy h1 {
  margin: 0.4rem 0 0.55rem;
  font-size: clamp(2rem, 4vw, 3.1rem);
  letter-spacing: -0.04em;
}

.profile-page__copy p {
  margin: 0;
  color: #b7b7b7;
  line-height: 1.75;
}

.section-spacing {
  margin-top: 1.25rem;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1rem;
}

.profile-stat-card {
  display: flex;
  align-items: center;
  gap: 0.95rem;
  padding: 1.1rem 1.15rem;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.profile-stat-card__icon {
  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: rgba(29, 185, 84, 0.12);
  color: #1db954;
  font-size: 1.15rem;
}

.profile-stat-card__value {
  font-size: 1.45rem;
  font-weight: 700;
  line-height: 1;
}

.profile-stat-card__label {
  margin-top: 0.35rem;
  color: #b5b5b5;
  font-size: 0.9rem;
}

.profile-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.95fr);
  gap: 1.25rem;
  align-items: start;
}

.profile-main,
.profile-side {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.page-notice,
.state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.page-notice--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.page-notice--error,
.state-card--error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  text-align: center;
}

@media (max-width: 1199.98px) {
  .profile-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .profile-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 575.98px) {
  .profile-stats {
    grid-template-columns: 1fr;
  }
}
</style>
