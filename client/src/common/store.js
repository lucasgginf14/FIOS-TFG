import { ref } from "vue";

function createDefaultUser() {
  return {
    id: null,
    email: "",
    name: "",
    firstSurname: "",
    secondSurname: "",
    profileImage: "",
    active: false,
    createdAt: null,
    platformRole: "",
    instrument: null,
    instruments: [],
    logged: false
  };
}

const store = ref({
  state: {
    user: createDefaultUser()
  }
});

export { getStore, resetUser, setUser };

function getStore() {
  return store.value;
}

function resetUser() {
  store.value.state.user = createDefaultUser();
}

function setUser(user) {
  store.value.state.user = {
    ...createDefaultUser(),
    id: user?.id ?? null,
    email: user?.email ?? "",
    name: user?.name ?? "",
    firstSurname: user?.firstSurname ?? "",
    secondSurname: user?.secondSurname ?? "",
    profileImage: user?.profileImage ?? "",
    active: Boolean(user?.active),
    createdAt: user?.createdAt ?? null,
    platformRole: user?.platformRole ?? "",
    instrument: user?.instrument ?? null,
    instruments: Array.isArray(user?.instruments) ? user.instruments : [],
    logged: Boolean(user?.logged)
  };
}
