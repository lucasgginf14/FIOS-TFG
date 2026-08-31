import HTTP from "@/common/http";

export default {
  async login(credentials) {
    return (await HTTP.post("account/login", credentials)).data;
  },

  async register(payload) {
    return (await HTTP.post("account/register", payload)).data;
  },

  async getMe() {
    return (await HTTP.get("account/me")).data;
  },

  async updateMe(payload) {
    return (await HTTP.put("account/me", payload)).data;
  },

  async updatePassword(payload) {
    return (await HTTP.put("account/me/password", payload)).data;
  },

  async updateProfileImage(payload) {
    return (await HTTP.put("account/me/profile-image", payload)).data;
  },

  async removeProfileImage() {
    return (await HTTP.delete("account/me/profile-image")).data;
  },

  async getMyInstruments() {
    return (await HTTP.get("account/me/instruments")).data;
  },

  async updateMyInstruments(payload) {
    return (await HTTP.put("account/me/instruments", payload)).data;
  },

  async logout() {
    return (await HTTP.post("account/logout")).data;
  }
};
