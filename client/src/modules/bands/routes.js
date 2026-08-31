const BandListView = () => import("./pages/BandListView.vue");
const BandDetailView = () => import("./pages/BandDetailView.vue");

export default [
  {
    path: "/bands",
    name: "BandList",
    component: BandListView,
    meta: { public: true }
  },
  {
    path: "/bands/:id",
    name: "BandDetail",
    component: BandDetailView,
    meta: { public: true, scrollToTop: true }
  }
];
