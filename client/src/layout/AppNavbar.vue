<template>
  <header class="app-navbar">
    <div class="container app-navbar__inner">
      <RouterLink class="app-navbar__brand" :to="brandRoute" @click="closeAll">
        <span class="app-navbar__brand-mark">
          <img :src="logo" alt="FIOS" class="app-navbar__logo" />
        </span>
        <span class="app-navbar__brand-copy">
          <span class="app-navbar__brand-name">Fios</span>
          <span class="app-navbar__brand-tagline">{{ t("navbar.brandTagline") }}</span>
        </span>
      </RouterLink>

      <div class="app-navbar__actions d-xl-none">
        <button
          class="icon-button"
          type="button"
          :aria-label="t('common.labels.language')"
          @click="openLanguageMenuFromMobile"
        >
          {{ currentLocaleLabel }}
        </button>
        <button
          class="hamburger-button"
          type="button"
          :aria-expanded="String(mobileMenuOpen)"
          :aria-label="t('common.labels.navigation')"
          @click="toggleMobileMenu"
        >
          <span />
          <span />
          <span />
        </button>
      </div>

      <div class="app-navbar__panel" :class="{ 'is-open': mobileMenuOpen }">
        <nav
          class="app-navbar__menu"
          :class="{ 'is-admin-menu': isAdmin }"
          aria-label="Main navigation"
        >
          <div
            v-for="section in visibleSections"
            :key="section.key"
            class="nav-section"
            :class="{
              'is-active': isSectionActive(section),
              'is-open': openDropdown === section.key
            }"
            @mouseenter="handleSectionMouseEnter(section.key)"
            @mouseleave="handleSectionMouseLeave(section.key)"
          >
            <RouterLink
              class="nav-section__link"
              :class="{ 'is-active': isSectionActive(section) }"
              :to="section.to"
              @click="handleSectionLinkClick($event, section)"
            >
              <i
                v-if="section.icon"
                class="nav-section__icon"
                :class="section.icon"
                aria-hidden="true"
              ></i>
              <span>{{ section.label }}</span>
            </RouterLink>

            <div v-if="section.items?.length" class="nav-dropdown">
              <RouterLink
                v-for="item in section.items"
                :key="item.label"
                class="nav-dropdown__item"
                :class="{ 'is-active': isItemActive(item, section) }"
                :to="item.to"
                @click="handleNavigate"
              >
                <span class="nav-dropdown__title">{{ item.label }}</span>
                <span v-if="item.description" class="nav-dropdown__meta">{{
                  item.description
                }}</span>
              </RouterLink>
            </div>
          </div>
        </nav>

        <div class="app-navbar__utility">
          <div
            class="language-menu"
            :class="{ 'is-open': openDropdown === 'language' }"
            @mouseenter="handleSectionMouseEnter('language')"
            @mouseleave="handleSectionMouseLeave('language')"
          >
            <button
              class="utility-pill"
              type="button"
              :aria-expanded="String(openDropdown === 'language')"
              :aria-label="t('common.labels.language')"
              @click.stop="toggleDropdown('language')"
            >
              <span class="utility-pill__label">{{ currentLocaleLabel }}</span>
            </button>

            <div class="nav-dropdown nav-dropdown--language">
              <button
                v-for="option in localeOptions"
                :key="option.value"
                class="nav-dropdown__item nav-dropdown__item--action"
                type="button"
                @click="changeLocale(option.value)"
              >
                <span class="nav-dropdown__title">{{ option.label }}</span>
                <span class="nav-dropdown__meta">{{ option.nativeLabel }}</span>
              </button>
            </div>
          </div>

          <div
            v-if="isLogged && !isAdmin"
            class="notification-menu"
            :class="{ 'is-open': openDropdown === 'notifications' }"
            @mouseenter="handleSectionMouseEnter('notifications')"
            @mouseleave="handleSectionMouseLeave('notifications')"
          >
            <button
              class="icon-button icon-button--notification"
              :class="{ 'is-open': openDropdown === 'notifications' }"
              type="button"
              :aria-label="t('common.labels.notifications')"
              :title="t('common.labels.notifications')"
              :aria-expanded="String(openDropdown === 'notifications')"
              @click.stop="toggleNotifications"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  d="M12 4a4 4 0 0 0-4 4v1.3c0 .8-.27 1.58-.77 2.2L5.6 13.5a1 1 0 0 0 .78 1.63h11.24a1 1 0 0 0 .78-1.63l-1.63-2c-.5-.62-.77-1.4-.77-2.2V8a4 4 0 0 0-4-4Z"
                />
                <path d="M10 18a2 2 0 0 0 4 0" />
              </svg>
              <span v-if="notificationCount > 0" class="notification-badge">{{
                notificationCount
              }}</span>
            </button>

            <div class="nav-dropdown nav-dropdown--notifications">
              <div v-if="notificationItems.length" class="notification-list">
                <button
                  v-for="item in notificationItems"
                  :key="item.reservationId"
                  class="nav-dropdown__item nav-dropdown__item--action notification-item"
                  type="button"
                  @click="openMessages(item.reservationId)"
                >
                  <span class="nav-dropdown__title notification-item__title">
                    {{ item.title }}
                    <span class="notification-item__count">{{ item.unreadMessagesCount }}</span>
                  </span>
                  <span class="nav-dropdown__meta">{{ item.meta }}</span>
                </button>
              </div>
              <div v-else class="nav-dropdown__empty">
                {{ t("navbar.notifications.empty") }}
              </div>

              <button
                class="nav-dropdown__item nav-dropdown__item--action notification-cta"
                type="button"
                @click="openMessages()"
              >
                <span class="nav-dropdown__title">{{
                  t("navbar.notifications.viewMessages")
                }}</span>
                <span class="nav-dropdown__meta">{{
                  t("navbar.notifications.viewMessagesMeta")
                }}</span>
              </button>
            </div>
          </div>

          <template v-if="!isLogged">
            <RouterLink
              class="auth-button auth-button--ghost"
              :to="{ name: 'Login' }"
              @click="closeAll"
            >
              {{ t("common.actions.login") }}
            </RouterLink>
            <RouterLink
              class="auth-button auth-button--accent"
              :to="{ name: 'Register' }"
              @click="closeAll"
            >
              {{ t("common.actions.register") }}
            </RouterLink>
          </template>

          <div
            v-else
            class="user-menu"
            :class="{ 'is-open': openDropdown === 'user' }"
            @mouseenter="handleSectionMouseEnter('user')"
            @mouseleave="handleSectionMouseLeave('user')"
          >
            <button
              class="user-menu__trigger"
              type="button"
              :aria-expanded="String(openDropdown === 'user')"
              @click.stop="toggleDropdown('user')"
            >
              <span class="user-menu__avatar">
                <AppImage
                  :src="store.state.user.profileImage"
                  :alt="displayName"
                  :fallback-src="avatarPlaceholder"
                  :fallback-label="displayName"
                  icon-class="bi bi-person"
                />
              </span>
              <span class="user-menu__identity">
                <span class="user-menu__name">{{ displayName }}</span>
                <span class="user-menu__role">{{ userRoleLabel }}</span>
              </span>
              <svg class="user-menu__caret" viewBox="0 0 16 16" aria-hidden="true">
                <path d="M4 6l4 4 4-4" />
              </svg>
            </button>

            <div class="nav-dropdown nav-dropdown--user">
              <RouterLink
                v-for="item in userMenuItems"
                :key="item.label"
                class="nav-dropdown__item"
                :to="item.to"
                @click="handleNavigate"
              >
                <span class="nav-dropdown__title">{{ item.label }}</span>
                <span v-if="item.description" class="nav-dropdown__meta">{{
                  item.description
                }}</span>
              </RouterLink>

              <button
                class="nav-dropdown__item nav-dropdown__item--action"
                type="button"
                @click="handleLogout"
              >
                <span class="nav-dropdown__title">{{ t("common.actions.logout") }}</span>
                <span class="nav-dropdown__meta">{{ t("navbar.userMenu.logoutMeta") }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import logo from "@/assets/logo.png";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import auth from "@/common/auth";
import AppImage from "@/common/components/AppImage.vue";
import { getStore } from "@/common/store";
import { getLocale, setLocale } from "@/i18n";
import MessageRepository from "@/repositories/MessageRepository";

const router = useRouter();
const route = useRoute();
const store = getStore();
const { t, locale } = useI18n();

const mobileMenuOpen = ref(false);
const openDropdown = ref(null);
const isDesktop = ref(false);
const notificationCount = ref(0);
const notificationSummary = ref({
  totalUnreadMessages: 0,
  reservations: []
});
const dropdownCloseDelay = 180;

let dropdownCloseTimer = null;

const isLogged = computed(() => store.state.user.logged);
const isAdmin = computed(() => auth.isAdmin());
const canUseUserFeatures = computed(() => isLogged.value && !isAdmin.value);
const brandRoute = computed(() => ({ name: "Home" }));
const displayName = computed(
  () => store.state.user.name || store.state.user.email || t("navbar.userMenu.profile")
);
const userRoleLabel = computed(() => t(`admin.roles.${store.state.user.platformRole || "USER"}`));
const currentLocaleLabel = computed(() => locale.value.toUpperCase());

const localeOptions = computed(() => [
  { value: "es", label: "ES", nativeLabel: "Español" },
  { value: "en", label: "EN", nativeLabel: "English" },
  { value: "gl", label: "GL", nativeLabel: "Galego" }
]);

const sections = computed(() => [
  ...(isAdmin.value
    ? [
      {
        key: "admin",
        icon: "bi bi-shield-lock",
        label: t("navbar.userMenu.admin"),
        to: { name: "AdminDashboard" },
        activeNames: ["AdminDashboard"],
        items: []
      }
    ]
    : []),
    {
      key: "search",
      label: t("navbar.menu.search"),
      to: { name: "SearchPage" },
      activeNames: ["SearchPage"],
      items: [
        {
          label: t("navbar.dropdowns.smartSearch"),
          description: t("navbar.dropdowns.smartSearchMeta"),
          to: { name: "SearchPage" }
        },
        {
          label: t("navbar.dropdowns.searchMap"),
          description: t("navbar.dropdowns.searchMapMeta"),
          to: { name: "SearchPage", query: { view: "map" } }
        }
      ]
    },
    {
      key: "spaces",
      label: t("navbar.menu.spaces"),
      to: { name: "MusicalSpaceList" },
      activeNames: ["MusicalSpaceList", "MusicalSpaceDetail"],
      items: [
        {
          label: t("navbar.dropdowns.exploreSpaces"),
          description: t("navbar.dropdowns.exploreSpacesMeta"),
          to: { name: "MusicalSpaceList" }
        },
        ...(canUseUserFeatures.value
          ? [
              {
                label: t("navbar.dropdowns.mySpaces"),
                description: t("navbar.dropdowns.mySpacesMeta"),
                to: { name: "MusicalSpaceList", query: { view: "mine" } }
              },
              {
                label: t("navbar.dropdowns.createSpace"),
                description: t("navbar.dropdowns.createSpaceMeta"),
                to: { name: "MusicalSpaceList", query: { mode: "create" } }
              }
            ]
          : [])
      ]
    },
    ...(canUseUserFeatures.value
      ? [
          {
            key: "reservations",
            label: t("navbar.menu.reservations"),
            to: { name: "ReservationList" },
            activeNames: ["ReservationList"],
            items: [
              {
                label: t("navbar.dropdowns.myReservations"),
                description: t("navbar.dropdowns.myReservationsMeta"),
                to: { name: "ReservationList", query: { tab: "mine" } }
              },
              {
                label: t("navbar.dropdowns.receivedReservations"),
                description: t("navbar.dropdowns.receivedReservationsMeta"),
                to: { name: "ReservationList", query: { tab: "received" } }
              }
            ]
          }
        ]
      : []),
    {
      key: "events",
      label: t("navbar.menu.events"),
      to: { name: "EventList" },
      activeNames: ["EventList", "EventDetail"],
      items: [
        {
          label: t("navbar.dropdowns.upcomingEvents"),
          description: t("navbar.dropdowns.upcomingEventsMeta"),
          to: { name: "EventList", query: { quick: "upcoming" } }
        },
        {
          label: t("navbar.dropdowns.eventMap"),
          description: t("navbar.dropdowns.eventMapMeta"),
          to: { name: "EventList", query: { view: "map" } }
        }
      ]
    },
    {
      key: "bands",
      label: t("navbar.menu.bands"),
      to: { name: "BandList" },
      activeNames: ["BandList", "BandDetail", "BandRecruitmentList"],
      items: [
        {
          label: t("navbar.dropdowns.exploreBands"),
          description: t("navbar.dropdowns.exploreBandsMeta"),
          to: { name: "BandList" }
        },
        ...(canUseUserFeatures.value
          ? [
              {
                label: t("navbar.dropdowns.myBands"),
                description: t("navbar.dropdowns.myBandsMeta"),
                to: { name: "BandList", query: { view: "mine" } }
              },
              {
                label: t("navbar.dropdowns.createBand"),
                description: t("navbar.dropdowns.createBandMeta"),
                to: { name: "BandList", query: { mode: "create" } }
              }
            ]
          : []),
        {
          label: t("navbar.dropdowns.memberSearch"),
          description: t("navbar.dropdowns.memberSearchMeta"),
          to: { name: "BandRecruitmentList" }
        }
      ]
    },
    ...(canUseUserFeatures.value
      ? [
          {
            key: "messages",
            label: t("navbar.menu.messages"),
            to: { name: "MessageInbox" },
            activeNames: ["MessageInbox"],
            items: []
          }
        ]
      : [])
]);

const visibleSections = computed(() => sections.value);

const userMenuItems = computed(() => {
  if (isAdmin.value) {
    return [];
  }

  return [
    {
      label: t("navbar.userMenu.profile"),
      description: t("navbar.userMenu.profileMeta"),
      to: { name: "Profile" }
    },
    {
      label: t("navbar.userMenu.reservations"),
      description: t("navbar.userMenu.reservationsMeta"),
      to: { name: "ReservationList", query: { tab: "mine" } }
    },
    {
      label: t("navbar.userMenu.reviews"),
      description: t("navbar.userMenu.reviewsMeta"),
      to: { name: "ReviewList" }
    },
    {
      label: t("navbar.userMenu.tickets"),
      description: t("navbar.userMenu.ticketsMeta"),
      to: { name: "MyTickets" }
    },
    {
      label: t("navbar.userMenu.bands"),
      description: t("navbar.userMenu.bandsMeta"),
      to: { name: "BandList", query: { view: "mine" } }
    },
    {
      label: t("navbar.userMenu.favorites"),
      description: t("navbar.userMenu.favoritesMeta"),
      to: { name: "FavoriteSpaceList" }
    }
  ];
});

const notificationItems = computed(() =>
  (notificationSummary.value?.reservations ?? []).slice(0, 5).map((item) => ({
    ...item,
    title:
      item.musicalSpace?.name ||
      item.otherUser?.name ||
      item.otherUser?.email ||
      t("navbar.notifications.fallbackConversation"),
    meta: buildNotificationMeta(item)
  }))
);

watch(
  () => route.fullPath,
  () => {
    closeAll();
  }
);

watch(canUseUserFeatures, (enabled) => {
  if (!enabled) {
    notificationCount.value = 0;
    notificationSummary.value = { totalUnreadMessages: 0, reservations: [] };
    closeAll();
    return;
  }

  loadNotificationSummary();
});

onMounted(() => {
  locale.value = getLocale();
  syncViewport();
  window.addEventListener("resize", syncViewport);
  window.addEventListener("messages:refresh-unread", loadNotificationSummary);
  document.addEventListener("click", handleOutsideClick);

  if (canUseUserFeatures.value) {
    loadNotificationSummary();
  }
});

onBeforeUnmount(() => {
  clearDropdownCloseTimer();
  window.removeEventListener("resize", syncViewport);
  window.removeEventListener("messages:refresh-unread", loadNotificationSummary);
  document.removeEventListener("click", handleOutsideClick);
});

function syncViewport() {
  isDesktop.value = window.innerWidth >= 1200;
  if (isDesktop.value) {
    mobileMenuOpen.value = false;
    return;
  }

  clearDropdownCloseTimer();
}

function clearDropdownCloseTimer() {
  if (dropdownCloseTimer) {
    window.clearTimeout(dropdownCloseTimer);
    dropdownCloseTimer = null;
  }
}

function scheduleDropdownClose(key) {
  clearDropdownCloseTimer();
  dropdownCloseTimer = window.setTimeout(() => {
    if (openDropdown.value === key) {
      openDropdown.value = null;
    }
    dropdownCloseTimer = null;
  }, dropdownCloseDelay);
}

function toggleMobileMenu() {
  clearDropdownCloseTimer();
  mobileMenuOpen.value = !mobileMenuOpen.value;
  if (!mobileMenuOpen.value) {
    openDropdown.value = null;
  }
}

function openLanguageMenuFromMobile() {
  clearDropdownCloseTimer();
  mobileMenuOpen.value = true;
  openDropdown.value = "language";
}

function toggleDropdown(key) {
  clearDropdownCloseTimer();
  openDropdown.value = openDropdown.value === key ? null : key;
}

function toggleNotifications() {
  const isOpening = openDropdown.value !== "notifications";
  toggleDropdown("notifications");
  if (isOpening) {
    loadNotificationSummary();
  }
}

function handleSectionMouseEnter(key) {
  if (isDesktop.value) {
    clearDropdownCloseTimer();
    openDropdown.value = key;
  }
}

function handleSectionMouseLeave(key) {
  if (isDesktop.value && openDropdown.value === key) {
    scheduleDropdownClose(key);
  }
}

function handleOutsideClick(event) {
  if (!event.target.closest(".app-navbar")) {
    clearDropdownCloseTimer();
    openDropdown.value = null;
  }
}

function handleNavigate() {
  clearDropdownCloseTimer();
  if (!isDesktop.value) {
    mobileMenuOpen.value = false;
  }
  openDropdown.value = null;
}

function handleSectionLinkClick(event, section) {
  if (!isDesktop.value && section.items?.length && openDropdown.value !== section.key) {
    event.preventDefault();
    clearDropdownCloseTimer();
    openDropdown.value = section.key;
    return;
  }

  handleNavigate();
}

function closeAll() {
  clearDropdownCloseTimer();
  mobileMenuOpen.value = false;
  openDropdown.value = null;
}

function isSectionActive(section) {
  return section.activeNames?.includes(route.name);
}

function isItemActive(item, section) {
  const targetName = item.to?.name ?? router.resolve(item.to).name;

  if (route.name !== targetName) {
    return false;
  }

  const itemQuery = item.to?.query ?? {};
  const itemQueryEntries = Object.entries(itemQuery);

  if (itemQueryEntries.length > 0) {
    return itemQueryEntries.every(([key, value]) => isSameQueryValue(route.query[key], value));
  }

  const sectionQueryKeys = new Set(
    (section.items ?? []).flatMap((sectionItem) => Object.keys(sectionItem.to?.query ?? {}))
  );

  return [...sectionQueryKeys].every((key) => route.query[key] == null);
}

function isSameQueryValue(currentValue, expectedValue) {
  const normalize = (value) => {
    if (Array.isArray(value)) {
      return value.map((entry) => String(entry)).join(",");
    }

    return value == null ? "" : String(value);
  };

  return normalize(currentValue) === normalize(expectedValue);
}

function changeLocale(nextLocale) {
  setLocale(nextLocale);
  locale.value = nextLocale;
  clearDropdownCloseTimer();
  openDropdown.value = null;
}

async function loadNotificationSummary() {
  if (!canUseUserFeatures.value) {
    notificationCount.value = 0;
    notificationSummary.value = { totalUnreadMessages: 0, reservations: [] };
    return;
  }

  try {
    const response = await MessageRepository.getUnread();
    notificationSummary.value = {
      totalUnreadMessages: response?.totalUnreadMessages ?? 0,
      reservations: response?.reservations ?? []
    };
    notificationCount.value = notificationSummary.value.totalUnreadMessages;
  } catch {
    notificationCount.value = 0;
    notificationSummary.value = { totalUnreadMessages: 0, reservations: [] };
  }
}

async function handleLogout() {
  auth.logout();
  notificationCount.value = 0;
  notificationSummary.value = { totalUnreadMessages: 0, reservations: [] };
  closeAll();
  await router.push({ name: "Home" });
}

function buildNotificationMeta(item) {
  const chunks = [];
  if (item.otherUser?.name) {
    chunks.push(item.otherUser.name);
  }
  if (item.lastMessageDateTime) {
    chunks.push(formatNotificationDate(item.lastMessageDateTime));
  }
  return chunks.join(" · ") || t("navbar.notifications.pending");
}

function formatNotificationDate(value) {
  const parsed = new Date(value);
  if (Number.isNaN(parsed.getTime())) {
    return "";
  }

  return new Intl.DateTimeFormat(locale.value, {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit"
  }).format(parsed);
}

async function openMessages(reservationId) {
  handleNavigate();
  await router.push({
    name: "MessageInbox",
    query: reservationId ? { reservationId: String(reservationId) } : {}
  });
}
</script>

<style scoped>
.app-navbar {
  position: sticky;
  top: 0;
  z-index: 1040;
  background:
    linear-gradient(180deg, rgba(8, 10, 9, 0.98), rgba(8, 8, 8, 0.94)),
    #050505;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.22);
}

.app-navbar__inner {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  align-items: center;
  gap: 1.25rem;
  min-height: 76px;
}

.app-navbar__brand {
  justify-self: start;
  display: inline-flex;
  align-items: center;
  gap: 0.85rem;
  text-decoration: none;
  min-width: 0;
}

.app-navbar__brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(145deg, rgba(29, 185, 84, 0.2), rgba(255, 255, 255, 0.04));
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.app-navbar__logo {
  width: 30px;
  height: 30px;
  object-fit: contain;
}

.app-navbar__brand-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.app-navbar__brand-name {
  color: #fff;
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.app-navbar__brand-tagline {
  color: #b3b3b3;
  font-size: 0.72rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.app-navbar__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-left: auto;
}

.app-navbar__panel {
  display: contents;
}

.app-navbar__menu {
  grid-column: 2;
  justify-self: center;
  display: flex;
  align-items: center;
  gap: 0.18rem;
  margin-left: 0;
  padding: 0.22rem;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.025);
}

.app-navbar__menu.is-admin-menu {
  padding: 0.28rem;
  border-color: rgba(29, 185, 84, 0.24);
  background:
    linear-gradient(135deg, rgba(29, 185, 84, 0.13), rgba(13, 202, 240, 0.05)),
    rgba(255, 255, 255, 0.035);
  box-shadow:
    0 18px 38px rgba(0, 0, 0, 0.22),
    inset 0 0 0 1px rgba(255, 255, 255, 0.03);
}

.app-navbar__utility {
  grid-column: 3;
  justify-self: end;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-left: 0;
}

.nav-section,
.user-menu,
.language-menu,
.notification-menu {
  position: relative;
}

.nav-section {
  display: flex;
  align-items: center;
  gap: 0.12rem;
}

.nav-section__link,
.user-menu__trigger,
.icon-button,
.utility-pill,
.auth-button {
  transition:
    color 0.18s ease,
    border-color 0.18s ease,
    background-color 0.18s ease,
    box-shadow 0.18s ease,
    transform 0.18s ease;
}

.nav-section__link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.44rem;
  color: #e8e8e8;
  text-decoration: none;
  font-weight: 700;
  font-size: 0.96rem;
  padding: 0.72rem 1.05rem;
  border-radius: 999px;
}

.app-navbar__menu.is-admin-menu .nav-section__link {
  padding-inline: 1.25rem;
}

.nav-section__icon {
  font-size: 1rem;
  line-height: 1;
}

.user-menu__caret,
.icon-button svg {
  width: 1rem;
  height: 1rem;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.nav-section:hover .nav-section__link,
.nav-section.is-open:not(.is-active) .nav-section__link,
.user-menu.is-open .user-menu__trigger,
.language-menu.is-open .utility-pill,
.notification-menu.is-open .icon-button {
  color: #1db954;
}

.nav-section:hover:not(.is-active) .nav-section__link,
.nav-section.is-open:not(.is-active) .nav-section__link {
  background: rgba(255, 255, 255, 0.06);
}

.nav-section.is-active .nav-section__link,
.nav-section__link.is-active {
  color: #061208;
  background: linear-gradient(135deg, #1ed760 0%, #14b94c 100%);
  box-shadow:
    0 10px 26px rgba(29, 185, 84, 0.24),
    inset 0 0 0 1px rgba(255, 255, 255, 0.16);
}

.nav-dropdown {
  position: absolute;
  top: calc(100% + 0.62rem);
  left: 0;
  min-width: 282px;
  padding: 0.68rem;
  overflow: hidden;
  background:
    radial-gradient(circle at top right, rgba(29, 185, 84, 0.12), transparent 38%),
    rgba(15, 17, 16, 0.98);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 22px;
  box-shadow: 0 30px 70px rgba(0, 0, 0, 0.48);
  backdrop-filter: blur(18px);
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px) scale(0.98);
  pointer-events: none;
  transition:
    opacity 0.18s ease,
    visibility 0.18s ease,
    transform 0.18s ease;
}

.nav-dropdown::before {
  content: "";
  position: absolute;
  top: -0.75rem;
  left: 0;
  right: 0;
  height: 0.75rem;
}

.nav-section.is-open .nav-dropdown,
.user-menu.is-open .nav-dropdown,
.language-menu.is-open .nav-dropdown,
.notification-menu.is-open .nav-dropdown {
  opacity: 1;
  visibility: visible;
  transform: translateY(0) scale(1);
  pointer-events: auto;
}

.nav-dropdown--user {
  right: 0;
  left: auto;
  min-width: 280px;
}

.nav-dropdown--language {
  right: 0;
  left: auto;
  min-width: 160px;
}

.nav-dropdown--notifications {
  right: 0;
  left: auto;
  min-width: 320px;
}

.nav-dropdown__item {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 0.24rem;
  width: 100%;
  text-align: left;
  text-decoration: none;
  padding: 0.82rem 0.95rem 0.82rem 1.15rem;
  border-radius: 16px;
  border: 1px solid transparent;
  background: transparent;
  transition:
    color 0.18s ease,
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease;
}

.nav-dropdown__item::before {
  content: "";
  position: absolute;
  top: 0.78rem;
  bottom: 0.78rem;
  left: 0.55rem;
  width: 3px;
  border-radius: 999px;
  background: transparent;
}

.nav-dropdown__title {
  color: #f4f4f4;
  font-size: 0.95rem;
  font-weight: 750;
}

.nav-dropdown__meta {
  color: #adadad;
  font-size: 0.8rem;
  line-height: 1.35;
}

.nav-dropdown__empty {
  color: #9f9f9f;
  font-size: 0.8rem;
}

.nav-dropdown__empty {
  padding: 0.35rem 0.95rem 0.95rem;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.notification-item__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.notification-item__count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.8rem;
  min-height: 1.8rem;
  padding: 0 0.45rem;
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.14);
  color: #dfffe9;
  font-size: 0.78rem;
  font-weight: 700;
}

.notification-cta {
  margin-top: 0.3rem;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding-top: 0.85rem;
}

.nav-dropdown__item:hover,
.nav-dropdown__item:focus-visible {
  background: rgba(255, 255, 255, 0.055);
  border-color: rgba(255, 255, 255, 0.08);
  outline: none;
  transform: translateX(2px);
}

.nav-dropdown__item.is-active {
  background: linear-gradient(135deg, rgba(29, 185, 84, 0.18), rgba(29, 185, 84, 0.08));
  border-color: rgba(29, 185, 84, 0.28);
  box-shadow: inset 0 0 0 1px rgba(29, 185, 84, 0.04);
}

.nav-dropdown__item.is-active::before {
  background: #1ed760;
}

.nav-dropdown__item:hover .nav-dropdown__title,
.nav-dropdown__item:focus-visible .nav-dropdown__title,
.nav-dropdown__item.is-active .nav-dropdown__title,
.nav-dropdown__item--action:hover .nav-dropdown__title {
  color: #1db954;
}

.nav-dropdown__item.is-active .nav-dropdown__meta {
  color: #d6d6d6;
}

.utility-pill,
.icon-button,
.auth-button,
.user-menu__trigger {
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.utility-pill,
.icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 44px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.03);
  color: #fff;
}

.utility-pill__label {
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.icon-button--notification {
  position: relative;
}

.notification-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 0.3rem;
  border-radius: 999px;
  background: #1db954;
  color: #050505;
  font-size: 0.72rem;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.icon-button:hover,
.icon-button.is-open,
.utility-pill:hover,
.hamburger-button:hover,
.auth-button--ghost:hover,
.user-menu__trigger:hover {
  color: #1db954;
  border-color: rgba(29, 185, 84, 0.45);
  box-shadow: 0 0 0 4px rgba(29, 185, 84, 0.08);
}

.auth-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  padding: 0 1rem;
  border-radius: 999px;
  font-size: 0.92rem;
  font-weight: 600;
  text-decoration: none;
}

.auth-button--ghost {
  color: #fff;
  background: transparent;
}

.auth-button--accent {
  color: #050505;
  background: #1db954;
  border-color: #1db954;
  box-shadow: 0 12px 30px rgba(29, 185, 84, 0.24);
}

.auth-button--accent:hover {
  color: #050505;
  background: #23d15f;
  border-color: #23d15f;
  transform: translateY(-1px);
}

.user-menu__trigger {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  min-height: 48px;
  padding: 0.3rem 0.45rem 0.3rem 0.3rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.03);
  color: #fff;
}

.user-menu__avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.user-menu__avatar :deep(.app-image),
.user-menu__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.user-menu__avatar :deep(.app-image) {
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.user-menu__avatar :deep(.app-image__img) {
  object-fit: cover;
}

.user-menu__identity {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.1;
}

.user-menu__name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 0.93rem;
  font-weight: 600;
}

.user-menu__role {
  color: #b3b3b3;
  font-size: 0.74rem;
  letter-spacing: 0.08em;
}

.user-menu__caret {
  width: 0.95rem;
  height: 0.95rem;
  color: #b3b3b3;
}

.hamburger-button {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.24rem;
  width: 46px;
  height: 46px;
  padding: 0;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
}

.hamburger-button span {
  width: 18px;
  height: 2px;
  margin: 0 auto;
  border-radius: 999px;
  background: #fff;
}

@media (max-width: 1199.98px) {
  .app-navbar__inner {
    display: flex;
    flex-wrap: wrap;
    padding-top: 1rem;
    padding-bottom: 1rem;
  }

  .app-navbar__panel {
    display: none;
    width: 100%;
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
    padding: 1rem 0 0.25rem;
  }

  .app-navbar__panel.is-open {
    display: flex;
  }

  .app-navbar__menu,
  .app-navbar__utility {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
    margin: 0;
  }

  .app-navbar__menu {
    gap: 0.65rem;
    padding: 0;
    border: 0;
    border-radius: 0;
    background: transparent;
  }

  .nav-section,
  .user-menu,
  .language-menu,
  .notification-menu {
    width: 100%;
    flex-wrap: wrap;
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.02);
  }

  .nav-section__link,
  .user-menu__trigger,
  .utility-pill {
    flex: 1;
    min-width: 0;
  }

  .nav-dropdown,
  .nav-dropdown--user,
  .nav-dropdown--language,
  .nav-dropdown--notifications {
    position: static;
    width: 100%;
    min-width: 0;
    margin: 0 0.5rem 0.5rem;
    opacity: 1;
    visibility: visible;
    transform: none;
    transition: none;
    pointer-events: auto;
    display: none;
  }

  .nav-section.is-open .nav-dropdown,
  .user-menu.is-open .nav-dropdown,
  .language-menu.is-open .nav-dropdown,
  .notification-menu.is-open .nav-dropdown {
    display: block;
  }

  .app-navbar__utility {
    padding-top: 0.25rem;
  }

  .icon-button,
  .auth-button,
  .utility-pill {
    width: 100%;
  }

  .user-menu__name {
    max-width: none;
  }
}
</style>
