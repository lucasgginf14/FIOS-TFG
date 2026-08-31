const ProfileView = () => import("./pages/ProfileView.vue");

export default [
  {
    path: "/profile",
    name: "Profile",
    component: ProfileView,
    meta: { roles: ["USER"] }
  }
];
