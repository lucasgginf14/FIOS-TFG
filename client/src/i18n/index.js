import { createI18n } from "vue-i18n";
import en from "./locales/en";
import es from "./locales/es";
import gl from "./locales/gl";

export const LOCALE_STORAGE_KEY = "fios-locale";
export const SUPPORTED_LOCALES = ["es", "en", "gl"];

function getInitialLocale() {
  const storedLocale = localStorage.getItem(LOCALE_STORAGE_KEY);

  if (storedLocale && SUPPORTED_LOCALES.includes(storedLocale)) {
    return storedLocale;
  }

  return "es";
}

const i18n = createI18n({
  legacy: false,
  locale: getInitialLocale(),
  fallbackLocale: "es",
  globalInjection: true,
  messages: {
    es,
    en,
    gl
  }
});

document.documentElement.lang = i18n.global.locale.value;

export function setLocale(locale) {
  if (!SUPPORTED_LOCALES.includes(locale)) {
    return;
  }

  i18n.global.locale.value = locale;
  document.documentElement.lang = locale;
  localStorage.setItem(LOCALE_STORAGE_KEY, locale);
}

export function getLocale() {
  return i18n.global.locale.value;
}

export default i18n;
