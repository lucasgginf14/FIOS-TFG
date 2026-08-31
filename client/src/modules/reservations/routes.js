const ReservationListView = () => import("./pages/ReservationListView.vue");

export default [
  {
    path: "/reservations",
    name: "ReservationList",
    component: ReservationListView,
    meta: { roles: ["USER"], scrollToTop: true }
  }
];
