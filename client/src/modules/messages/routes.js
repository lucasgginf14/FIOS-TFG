const MessageInboxView = () => import("./pages/MessageInboxView.vue");

export default [
  {
    path: "/messages",
    name: "MessageInbox",
    component: MessageInboxView,
    meta: { roles: ["USER"] }
  }
];
