const EventDetailView = () => import("./pages/EventDetailView.vue");
const EventListView = () => import("./pages/EventListView.vue");
const MyTicketsView = () => import("./pages/MyTicketsView.vue");

export default [
  {
    path: "/events",
    name: "EventList",
    component: EventListView,
    meta: { public: true }
  },
  {
    path: "/events/:id",
    name: "EventDetail",
    component: EventDetailView,
    meta: { public: true, scrollToTop: true }
  },
  {
    path: "/tickets",
    name: "MyTickets",
    component: MyTicketsView,
    meta: { roles: ["USER"], scrollToTop: true }
  }
];
