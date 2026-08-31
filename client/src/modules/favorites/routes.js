const FavoriteSpaceListView = () => import("./pages/FavoriteSpaceListView.vue");

export default [
  {
    path: "/favorites",
    name: "FavoriteSpaceList",
    component: FavoriteSpaceListView,
    meta: { roles: ["USER"] }
  }
];
