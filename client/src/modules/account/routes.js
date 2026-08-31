const LoginView = () => import("./pages/LoginView.vue");
const RegisterView = () => import("./pages/RegisterView.vue");

export default [
  {
    path: "/login",
    name: "Login",
    component: LoginView,
    meta: { public: true, isLoginPage: true }
  },
  {
    path: "/register",
    name: "Register",
    component: RegisterView,
    meta: { public: true, isLoginPage: true }
  }
];
