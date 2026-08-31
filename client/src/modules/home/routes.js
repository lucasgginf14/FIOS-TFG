const HomeView = () => import("./pages/HomeView.vue");

export default [
  {
    path: "/",
    name: "Home",
    component: HomeView,
    meta: { public: true }
  }
];
