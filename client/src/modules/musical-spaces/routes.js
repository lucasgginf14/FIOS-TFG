const MusicalSpaceListView = () => import("./pages/MusicalSpaceListView.vue");
const MusicalSpaceDetailView = () => import("./pages/MusicalSpaceDetailView.vue");
const MusicalSpaceAvailabilityManageView = () => import("./pages/MusicalSpaceAvailabilityManageView.vue");

export default [
  {
    path: "/musical-spaces",
    name: "MusicalSpaceList",
    component: MusicalSpaceListView,
    meta: { public: true }
  },
  {
    path: "/musical-spaces/:id",
    name: "MusicalSpaceDetail",
    component: MusicalSpaceDetailView,
    meta: { public: true }
  },
  {
    path: "/musical-spaces/:id/availability",
    name: "MusicalSpaceAvailabilityManage",
    component: MusicalSpaceAvailabilityManageView,
    meta: { scrollToTop: true }
  }
];
