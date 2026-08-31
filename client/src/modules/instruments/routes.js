const InstrumentListView = () => import("./pages/InstrumentListView.vue");

export default [
  {
    path: "/instruments",
    name: "InstrumentList",
    component: InstrumentListView,
    meta: { public: true }
  }
];
