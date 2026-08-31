const SearchPageView = () => import("./pages/SearchPageView.vue");

export default [
  {
    path: "/search",
    name: "SearchPage",
    component: SearchPageView,
    meta: { public: true }
  }
];
