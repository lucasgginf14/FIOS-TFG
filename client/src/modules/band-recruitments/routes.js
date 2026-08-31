const BandRecruitmentListView = () => import("./pages/BandRecruitmentListView.vue");

export default [
  {
    path: "/band-recruitments",
    name: "BandRecruitmentList",
    component: BandRecruitmentListView,
    meta: { public: true, scrollToTop: true }
  }
];
