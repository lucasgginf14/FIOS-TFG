const ReviewListView = () => import("./pages/ReviewListView.vue");

export default [
  {
    path: "/reviews",
    name: "ReviewList",
    component: ReviewListView,
    meta: { roles: ["USER"] }
  }
];
