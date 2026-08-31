<template>
  <PageLayout class="register-layout" :title="t('account.register.title')" theme="dark">
    <div class="auth-shell">
      <div class="register-panel">
        <div class="auth-card register-form-card card border-0 shadow-lg">
          <div class="card-body p-4 p-md-5">
            <div v-if="feedback.message" :class="feedbackClass" role="alert">
              {{ feedback.message }}
            </div>

            <form class="row g-3" novalidate @submit.prevent="handleSubmit">
              <div class="col-md-6">
                <label class="form-label auth-label" for="name">{{ t("account.register.name") }}</label>
                <input
                  id="name"
                  v-model.trim="form.name"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.name }"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.name" class="auth-field-error">{{ fieldErrors.name }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="firstSurname">{{ t("account.register.firstSurname") }}</label>
                <input
                  id="firstSurname"
                  v-model.trim="form.firstSurname"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.firstSurname }"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.firstSurname" class="auth-field-error">{{ fieldErrors.firstSurname }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="secondSurname">{{ t("account.register.secondSurname") }}</label>
                <input
                  id="secondSurname"
                  v-model.trim="form.secondSurname"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.secondSurname }"
                  :disabled="loading"
                />
                <small v-if="fieldErrors.secondSurname" class="auth-field-error">{{ fieldErrors.secondSurname }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="phone">{{ t("account.register.phone") }}</label>
                <input
                  id="phone"
                  v-model.trim="form.phone"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.phone }"
                  autocomplete="tel"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.phone" class="auth-field-error">{{ fieldErrors.phone }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="email">{{ t("account.register.email") }}</label>
                <input
                  id="email"
                  v-model.trim="form.email"
                  type="email"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.email }"
                  autocomplete="email"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.email" class="auth-field-error">{{ fieldErrors.email }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="birthDate">{{ t("account.register.birthDate") }}</label>
                <input
                  id="birthDate"
                  v-model="form.birthDate"
                  type="date"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.birthDate }"
                  :max="today"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.birthDate" class="auth-field-error">{{ fieldErrors.birthDate }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label">{{ t("account.register.instrument") }}</label>
                <AppSelect
                  class="auth-select"
                  :class="{ 'auth-select--invalid': fieldErrors.instrumentId || fieldErrors.instrumentIds }"
                  :model-value="form.instrumentId"
                  :options="instrumentSelectOptions"
                  :label="t('account.register.instrument')"
                  :disabled="loading || instrumentsLoading || Boolean(instrumentCatalogError)"
                  @update:model-value="form.instrumentId = $event"
                />
                <small v-if="fieldErrors.instrumentId || fieldErrors.instrumentIds" class="auth-field-error">
                  {{ fieldErrors.instrumentId || fieldErrors.instrumentIds }}
                </small>
                <small v-else-if="instrumentCatalogError" class="auth-field-error">{{ instrumentCatalogError }}</small>
                <small v-else class="auth-field-help">{{ t("account.register.instrumentHelp") }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="password">{{ t("account.register.password") }}</label>
                <input
                  id="password"
                  v-model="form.password"
                  type="password"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.password }"
                  autocomplete="new-password"
                  minlength="8"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.password" class="auth-field-error">{{ fieldErrors.password }}</small>
              </div>

              <div class="col-md-6">
                <label class="form-label auth-label" for="confirmPassword">{{ t("account.register.confirmPassword") }}</label>
                <input
                  id="confirmPassword"
                  v-model="form.confirmPassword"
                  type="password"
                  class="form-control auth-input"
                  :class="{ 'is-invalid': fieldErrors.confirmPassword }"
                  autocomplete="new-password"
                  minlength="8"
                  :disabled="loading"
                  required
                />
                <small v-if="fieldErrors.confirmPassword" class="auth-field-error">{{ fieldErrors.confirmPassword }}</small>
              </div>

              <div class="col-12 d-flex flex-column flex-sm-row justify-content-center gap-2 pt-2">
                <button class="btn auth-submit" type="submit" :disabled="loading || !canSubmit">
                  <span
                    v-if="loading"
                    class="spinner-border spinner-border-sm me-2"
                    aria-hidden="true"
                  />
                  {{ loading ? t("account.register.submitting") : t("account.register.submit") }}
                </button>
                <RouterLink class="btn auth-secondary" :to="{ name: 'Login' }">
                  {{ t("account.register.backToLogin") }}
                </RouterLink>
              </div>
            </form>
          </div>
        </div>

        <aside class="register-checklist-card">
          <div class="register-checklist">
            <div class="register-checklist__section">
              <strong>{{ t("account.register.passwordRulesTitle") }}</strong>
              <ul class="register-checklist__list">
                <li
                  v-for="rule in passwordRules"
                  :key="rule.key"
                  :class="{ 'is-valid': rule.valid }"
                >
                  <i :class="rule.valid ? 'bi bi-check-circle-fill' : 'bi bi-dot'"></i>
                  <span>{{ rule.label }}</span>
                </li>
              </ul>
            </div>

            <div class="register-checklist__section">
              <strong>{{ t("account.register.formRulesTitle") }}</strong>
              <ul class="register-checklist__list">
                <li
                  v-for="rule in formRules"
                  :key="rule.key"
                  :class="{ 'is-valid': rule.valid }"
                >
                  <i :class="rule.valid ? 'bi bi-check-circle-fill' : 'bi bi-dot'"></i>
                  <span>{{ rule.label }}</span>
                </li>
              </ul>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </PageLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import AppSelect from "@/common/components/AppSelect.vue";
import PageLayout from "@/common/components/PageLayout.vue";
import auth from "@/common/auth";
import InstrumentRepository from "@/repositories/InstrumentRepository";
import { getApiFieldErrors } from "@/common/apiErrors";
import { isValidPhone } from "@/common/validation";
import { getAccountApiErrorMessage } from "../utils/accountApiErrors";

const router = useRouter();
const { t } = useI18n();

const form = reactive({
  name: "",
  firstSurname: "",
  secondSurname: "",
  email: "",
  password: "",
  confirmPassword: "",
  phone: "",
  birthDate: "",
  instrumentId: ""
});

const loading = ref(false);
const instruments = ref([]);
const instrumentsLoading = ref(false);
const instrumentCatalogError = ref("");
const fieldErrors = ref({});
const feedback = ref({
  type: "",
  message: ""
});

const today = new Date().toISOString().slice(0, 10);
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const canSubmit = computed(() => {
  return (
    form.name.trim().length > 0 &&
    form.firstSurname.trim().length > 0 &&
    form.email.trim().length > 0 &&
    form.phone.trim().length > 0 &&
    form.birthDate.length > 0 &&
    form.password.length >= 8 &&
    form.confirmPassword.length >= 8
  );
});

const feedbackClass = computed(() =>
  feedback.value.type === "error" ? "alert alert-danger auth-alert" : "alert alert-success auth-alert"
);

const instrumentSelectOptions = computed(() => [
  {
    value: "",
    label: instrumentsLoading.value
      ? t("account.register.loadingInstruments")
      : t("account.register.noInstrument")
  },
  ...instruments.value.map((instrument) => ({
    value: String(instrument.id),
    label: instrument.name
  }))
]);

const passwordRules = computed(() => [
  {
    key: "length",
    valid: form.password.length >= 8,
    label: t("account.register.passwordRules.minLength")
  },
  {
    key: "letter",
    valid: /[A-Za-z]/.test(form.password),
    label: t("account.register.passwordRules.hasLetter")
  },
  {
    key: "number",
    valid: /\d/.test(form.password),
    label: t("account.register.passwordRules.hasNumber")
  },
  {
    key: "notEmpty",
    valid: form.password.trim().length > 0,
    label: t("account.register.passwordRules.notEmpty")
  }
]);

const formRules = computed(() => [
  {
    key: "email",
    valid: emailRegex.test(form.email.trim()),
    label: t("account.register.formRules.validEmail")
  },
  {
    key: "name",
    valid: form.name.trim().length > 0,
    label: t("account.register.formRules.requiredName")
  },
  {
    key: "firstSurname",
    valid: form.firstSurname.trim().length > 0,
    label: t("account.register.formRules.requiredFirstSurname")
  },
  {
    key: "phone",
    valid: isValidPhone(form.phone),
    label: t("account.register.formRules.validPhone")
  },
  {
    key: "birthDate",
    valid: Boolean(form.birthDate) && form.birthDate <= today,
    label: t("account.register.formRules.validBirthDate")
  }
]);

function setFeedback(type, message) {
  feedback.value = { type, message };
}

function setFieldError(field, message) {
  fieldErrors.value = {
    ...fieldErrors.value,
    [field]: message
  };
}

function validateForm() {
  fieldErrors.value = {};

  if (!canSubmit.value) {
    if (!form.name.trim()) {
      setFieldError("name", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.firstSurname.trim()) {
      setFieldError("firstSurname", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.phone.trim()) {
      setFieldError("phone", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.email.trim()) {
      setFieldError("email", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.birthDate) {
      setFieldError("birthDate", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.password) {
      setFieldError("password", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    if (!form.confirmPassword) {
      setFieldError("confirmPassword", t("apiFieldErrors.FIELD_REQUIRED"));
    }

    setFeedback("error", t("account.errors.requiredFields"));
    return false;
  }

  if (form.name.trim().length > 100) {
    setFieldError("name", t("apiFieldErrors.INVALID_LENGTH"));
    setFeedback("error", t("apiErrors.tooLong"));
    return false;
  }

  if (form.firstSurname.trim().length > 100) {
    setFieldError("firstSurname", t("apiFieldErrors.INVALID_LENGTH"));
    setFeedback("error", t("apiErrors.tooLong"));
    return false;
  }

  if (form.secondSurname.trim().length > 100) {
    setFieldError("secondSurname", t("apiFieldErrors.INVALID_LENGTH"));
    setFeedback("error", t("apiErrors.tooLong"));
    return false;
  }

  if (form.password !== form.confirmPassword) {
    setFieldError("confirmPassword", t("account.errors.passwordMismatch"));
    setFeedback("error", t("account.errors.passwordMismatch"));
    return false;
  }

  if (!emailRegex.test(form.email.trim())) {
    setFieldError("email", t("apiFieldErrors.INVALID_EMAIL"));
    setFeedback("error", t("account.errors.invalidEmail"));
    return false;
  }

  if (form.email.trim().length > 150) {
    setFieldError("email", t("apiFieldErrors.INVALID_LENGTH"));
    setFeedback("error", t("apiErrors.tooLong"));
    return false;
  }

  if (!isValidPhone(form.phone)) {
    setFieldError("phone", t("apiFieldErrors.INVALID_PHONE"));
    setFeedback("error", t("apiErrors.invalidPhone"));
    return false;
  }

  if (form.phone.trim().length > 30) {
    setFieldError("phone", t("apiFieldErrors.INVALID_LENGTH"));
    setFeedback("error", t("apiErrors.tooLong"));
    return false;
  }

  if (
    form.instrumentId &&
    instruments.value.length &&
    !instruments.value.some((instrument) => String(instrument.id) === String(form.instrumentId))
  ) {
    setFieldError("instrumentId", t("account.register.validationInstrument"));
    setFeedback("error", t("account.register.validationInstrument"));
    return false;
  }

  if (!/[A-Za-z]/.test(form.password) || !/\d/.test(form.password)) {
    setFieldError("password", t("account.errors.passwordWeak"));
    setFeedback("error", t("account.errors.passwordWeak"));
    return false;
  }

  if (form.birthDate > today) {
    setFieldError("birthDate", t("account.errors.futureBirthDate"));
    setFeedback("error", t("account.errors.futureBirthDate"));
    return false;
  }

  return true;
}

async function handleSubmit() {
  if (!validateForm()) {
    return;
  }

  loading.value = true;
  fieldErrors.value = {};
  setFeedback("", "");

  const credentials = {
    email: form.email.trim(),
    password: form.password
  };

  try {
    await auth.register({
      name: form.name.trim(),
      firstSurname: form.firstSurname.trim(),
      secondSurname: form.secondSurname.trim() || null,
      email: credentials.email,
      password: credentials.password,
      confirmPassword: form.confirmPassword,
      phone: form.phone.trim(),
      birthDate: form.birthDate,
      instrumentIds: buildInstrumentIds(form.instrumentId)
    });

    try {
      await auth.login(credentials);
      await router.push({ name: "Home" });
    } catch {
      await router.push({
        name: "Login",
        query: {
          registered: "1",
          autoLoginFailed: "1",
          email: credentials.email
        }
      });
    }
  } catch (error) {
    fieldErrors.value = getApiFieldErrors(error, t);
    setFeedback("error", getAccountApiErrorMessage(error, t, "account.errors.registerGeneric"));
  } finally {
    loading.value = false;
  }
}

function buildInstrumentIds(instrumentId) {
  const normalizedId = Number(instrumentId);
  return Number.isFinite(normalizedId) && normalizedId > 0 ? [normalizedId] : [];
}

async function loadInstrumentCatalog() {
  if (instrumentsLoading.value) {
    return;
  }

  instrumentsLoading.value = true;
  instrumentCatalogError.value = "";

  try {
    instruments.value = (await InstrumentRepository.getAll()) ?? [];
  } catch {
    instruments.value = [];
    instrumentCatalogError.value = t("account.register.instrumentCatalogError");
  } finally {
    instrumentsLoading.value = false;
  }
}

onMounted(() => {
  loadInstrumentCatalog();
});
</script>

<style scoped>
.auth-shell {
  padding-top: 2rem;
  padding-bottom: 2rem;
}

.register-layout :deep(.page-header) {
  margin-right: auto;
  margin-left: auto;
  text-align: center;
}

.register-panel {
  display: grid;
  grid-template-columns: minmax(0, 720px) minmax(280px, 360px);
  align-items: start;
  justify-content: center;
  gap: 1.5rem;
  width: 100%;
  max-width: 1120px;
  margin: 0 auto;
}

.auth-card {
  overflow: hidden;
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 32%),
    linear-gradient(180deg, #111111 0%, #0b0b0b 100%);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 34px 70px rgba(0, 0, 0, 0.32);
}

.auth-alert {
  border-radius: 16px;
}

.register-form-card {
  width: 100%;
}

.register-checklist-card {
  width: 100%;
  border-radius: 28px;
  padding: 1.25rem;
  background:
    radial-gradient(circle at top right, rgba(37, 211, 102, 0.12), transparent 34%),
    rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.09);
  box-shadow: 0 24px 52px rgba(0, 0, 0, 0.22);
}

.register-checklist {
  display: grid;
  gap: 1rem;
}

.register-checklist__section {
  padding: 1.1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.register-checklist__section strong {
  display: block;
  margin-bottom: 0.75rem;
  color: #ffffff;
}

.register-checklist__list {
  display: grid;
  gap: 0.55rem;
  margin: 0;
  padding: 0;
  list-style: none;
}

.register-checklist__list li {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: #a5a5a5;
  font-size: 0.92rem;
}

.register-checklist__list li i {
  color: #7f7f7f;
}

.register-checklist__list li.is-valid {
  color: #dfffe9;
}

.register-checklist__list li.is-valid i {
  color: #1db954;
}

.auth-label {
  color: #fff;
  font-weight: 600;
}

.auth-input {
  min-height: 52px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: #fff;
}

.auth-input:focus {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(29, 185, 84, 0.65);
  box-shadow: 0 0 0 0.25rem rgba(29, 185, 84, 0.14);
}

.auth-input.is-invalid {
  border-color: rgba(255, 99, 132, 0.78);
  box-shadow: none;
}

.auth-field-error {
  display: block;
  margin-top: 0.4rem;
  color: #ffb8c2;
  font-size: 0.82rem;
}

.auth-field-help {
  display: block;
  margin-top: 0.4rem;
  color: #a5a5a5;
  font-size: 0.82rem;
}

.auth-select :deep(.app-select__trigger) {
  min-height: 52px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
}

.auth-select.auth-select--invalid :deep(.app-select__trigger) {
  border-color: rgba(255, 99, 132, 0.78);
  box-shadow: none;
}

.auth-submit,
.auth-secondary {
  min-height: 52px;
  padding-left: 1.2rem;
  padding-right: 1.2rem;
  border-radius: 16px;
  font-weight: 700;
}

.auth-submit {
  border: 0;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
}

.auth-submit:disabled {
  background: linear-gradient(135deg, #159244, #159244);
  color: rgba(4, 17, 6, 0.7);
}

.auth-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: #fff;
  background: rgba(255, 255, 255, 0.03);
}

.auth-secondary:hover {
  color: #1db954;
  border-color: rgba(29, 185, 84, 0.45);
  background: rgba(255, 255, 255, 0.05);
}

@media (max-width: 991.98px) {
  .register-panel {
    grid-template-columns: 1fr;
    max-width: 760px;
  }
}

@media (max-width: 575.98px) {
  .register-checklist-card {
    padding: 1rem;
  }
}
</style>
