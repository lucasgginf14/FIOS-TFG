import { getStore, resetUser, setUser } from "./store";
import AccountRepository from "@/repositories/AccountRepository";

const TOKEN_KEY = "token";

export default {
  login,
  logout,
  getToken,
  isAdmin,
  isAuthenticated,
  isAuthenticationChecked: isAuthenticationChecked(),
  register,
  getAccountInfo,
  refreshAuthenticatedUser,
  updateProfile,
  updatePassword,
  updateProfileImage,
  removeProfileImage,
  syncAuthenticatedUser
};

async function login(credentials) {
  const response = await AccountRepository.login(credentials);

  if (!response?.token) {
    throw new Error("Authentication token missing in login response.");
  }

  _saveToken(response.token);

  if (response.user) {
    _setAuthenticatedUser(response.user);
    return getStore().state.user;
  }

  return refreshAuthenticatedUser();
}

function logout() {
  _removeToken();
  resetUser();
}

function isAdmin() {
  return getStore().state.user.platformRole === "ADMIN";
}

function isAuthenticated() {
  return getStore().state.user.logged;
}

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function _saveToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

function _removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}

function _setAuthenticatedUser(user) {
  setUser({
    id: user.id,
    email: user.email,
    name: user.name,
    firstSurname: user.firstSurname,
    secondSurname: user.secondSurname,
    profileImage: user.profileImage,
    active: user.active,
    createdAt: user.createdAt,
    platformRole: user.platformRole,
    instrument: user.instrument,
    instruments: user.instruments,
    logged: true
  });
}

async function refreshAuthenticatedUser() {
  const response = await AccountRepository.getMe();
  _setAuthenticatedUser(response);
  return getStore().state.user;
}

function isAuthenticationChecked() {
  if (!getToken()) {
    return Promise.resolve(true);
  }

  return refreshAuthenticatedUser()
    .catch(() => logout())
    .then(() => true);
}

async function register(user) {
  return await AccountRepository.register(user);
}

async function getAccountInfo() {
  return await AccountRepository.getMe();
}

async function updateProfile(user) {
  const response = await AccountRepository.updateMe(user);
  _setAuthenticatedUser(response);
  return response;
}

async function updatePassword(payload) {
  return await AccountRepository.updatePassword(payload);
}

async function updateProfileImage(profileImage) {
  const response = await AccountRepository.updateProfileImage({ profileImage });
  _setAuthenticatedUser(response);
  return response;
}

async function removeProfileImage() {
  const response = await AccountRepository.removeProfileImage();
  _setAuthenticatedUser(response);
  return response;
}

function syncAuthenticatedUser(user) {
  _setAuthenticatedUser(user);
  return getStore().state.user;
}
