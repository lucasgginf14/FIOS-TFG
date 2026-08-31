const AdminDashboardView = () => import("./pages/AdminDashboardView.vue");

export default [
  {
    path: "/admin",
    name: "AdminDashboard",
    component: AdminDashboardView,
    meta: { roles: ["ADMIN"] }
  }
];
