import { createRouter, createWebHistory } from "vue-router";
import auth from "@/common/auth";
import { getStore } from "@/common/store";
import homeRoutes from "@/modules/home/routes.js";
import accountRoutes from "@/modules/account/routes.js";
import profileRoutes from "@/modules/profile/routes.js";
import musicalSpaceRoutes from "@/modules/musical-spaces/routes.js";
import reservationRoutes from "@/modules/reservations/routes.js";
import messageRoutes from "@/modules/messages/routes.js";
import bandRoutes from "@/modules/bands/routes.js";
import bandRecruitmentRoutes from "@/modules/band-recruitments/routes.js";
import instrumentRoutes from "@/modules/instruments/routes.js";
import eventRoutes from "@/modules/events/routes.js";
import searchRoutes from "@/modules/search/routes.js";
import favoriteRoutes from "@/modules/favorites/routes.js";
import reviewRoutes from "@/modules/reviews/routes.js";
import adminRoutes from "@/modules/admin/routes.js";

const routes = [
  ...homeRoutes,
  ...accountRoutes,
  ...profileRoutes,
  ...musicalSpaceRoutes,
  ...reservationRoutes,
  ...messageRoutes,
  ...bandRoutes,
  ...bandRecruitmentRoutes,
  ...instrumentRoutes,
  ...eventRoutes,
  ...searchRoutes,
  ...favoriteRoutes,
  ...reviewRoutes,
  ...adminRoutes,
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("@/modules/home/pages/NotFoundView.vue"),
    meta: { public: true }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    const isSamePage = to.path === from.path;

    if (to.meta.scrollToTop || !isSamePage) {
      return { left: 0, top: 0 };
    }

    return savedPosition ?? false;
  }
});

router.beforeEach(async (to) => {
  await auth.isAuthenticationChecked;

  const user = getStore().state.user;
  const requiresAuth = !to.meta.public;
  const allowedRoles = to.meta.roles ?? [];

  if (!requiresAuth) {
    if (user.logged && to.meta.isLoginPage) {
      return { name: auth.isAdmin() ? "AdminDashboard" : "Home", replace: true };
    }

    return true;
  }

  if (!user.logged) {
    return {
      name: "Login",
      query: { redirect: to.fullPath }
    };
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user.platformRole)) {
    return { name: "Home" };
  }

  return true;
});

export default router;
