<template>
  <PageLayout class="login-layout" :title="t('account.login.title')" theme="dark">
    <div class="auth-shell row justify-content-center">
      <div class="col-12 col-lg-6 col-xl-5">
        <div class="auth-card card border-0 shadow-lg">
          <div class="card-body p-4 p-md-5">
            <div v-if="successMessage" class="alert alert-success auth-alert" role="status">
              {{ successMessage }}
            </div>

            <div v-if="errorMessage" class="alert alert-danger auth-alert" role="alert">
              {{ errorMessage }}
            </div>

            <form class="d-grid gap-3" novalidate @submit.prevent="handleSubmit">
              <div>
                <label class="form-label auth-label" for="email">{{
                  t("account.login.email")
                }}</label>
                <input
                  id="email"
                  v-model.trim="form.email"
                  type="email"
                  class="form-control auth-input"
                  autocomplete="email"
                  :disabled="loading"
                  required
                />
              </div>

              <div>
                <label class="form-label auth-label" for="password">{{
                  t("account.login.password")
                }}</label>
                <input
                  id="password"
                  v-model="form.password"
                  type="password"
                  class="form-control auth-input"
                  autocomplete="current-password"
                  :disabled="loading"
                  required
                />
              </div>

              <button class="btn auth-submit" type="submit" :disabled="loading || !canSubmit">
                <span
                  v-if="loading"
                  class="spinner-border spinner-border-sm me-2"
                  aria-hidden="true"
                />
                {{ loading ? t("account.login.submitting") : t("account.login.submit") }}
              </button>
            </form>

            <div class="auth-card__footer mt-4">
              <span class="text-secondary">{{ t("account.login.noAccount") }}</span>
              <RouterLink class="auth-inline-link" :to="{ name: 'Register' }">
                {{ t("account.login.registerLink") }}
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </PageLayout>
</template>

<script setup>
import { computed, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import PageLayout from "@/common/components/PageLayout.vue";
import auth from "@/common/auth";
import { getAccountApiErrorMessage } from "../utils/accountApiErrors";

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const form = reactive({
  email: "",
  password: ""
});

const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

const canSubmit = computed(() => form.email.trim().length > 0 && form.password.length > 0);

watch(
  () => [route.query.registered, route.query.autoLoginFailed, route.query.email],
  ([registered, autoLoginFailed, email]) => {
    if (autoLoginFailed === "1") {
      successMessage.value = "";
      errorMessage.value = t("account.login.autoLoginFallback");
    } else {
      errorMessage.value = "";
      successMessage.value = registered === "1" ? t("account.login.registeredSuccess") : "";
    }

    if (typeof email === "string" && email.length > 0) {
      form.email = email;
    }
  },
  { immediate: true }
);

async function handleSubmit() {
  if (!canSubmit.value) {
    errorMessage.value = t("account.errors.requiredLogin");
    return;
  }

  loading.value = true;
  errorMessage.value = "";
  successMessage.value = "";

  try {
    await auth.login({
      email: form.email.trim(),
      password: form.password
    });

    const redirect = route.query.redirect;

    if (typeof redirect === "string" && redirect.length > 0) {
      await router.push(redirect);
      return;
    }

    await router.push({ name: "Home" });
  } catch (error) {
    errorMessage.value = getAccountApiErrorMessage(error, t, "account.errors.loginGeneric");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-shell {
  padding-top: 2rem;
  padding-bottom: 2rem;
}

.login-layout :deep(.page-header) {
  margin-right: auto;
  margin-left: auto;
  text-align: center;
}

.auth-card {
  overflow: hidden;
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.18), transparent 34%),
    linear-gradient(180deg, #111111 0%, #0b0b0b 100%);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 34px 70px rgba(0, 0, 0, 0.32);
}

.auth-alert {
  border-radius: 16px;
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

.auth-input::placeholder {
  color: #8c8c8c;
}

.auth-input:focus {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(29, 185, 84, 0.65);
  box-shadow: 0 0 0 0.25rem rgba(29, 185, 84, 0.14);
}

.auth-submit {
  min-height: 52px;
  border-radius: 16px;
  border: 0;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  font-weight: 700;
}

.auth-submit:disabled {
  background: linear-gradient(135deg, #159244, #159244);
  color: rgba(4, 17, 6, 0.7);
}

.auth-card__footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.auth-inline-link {
  color: #1db954;
  font-weight: 600;
  text-decoration: none;
}

.auth-inline-link:hover {
  color: #25d366;
}
</style>
