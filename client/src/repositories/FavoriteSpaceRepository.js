import HTTP from "@/common/http";

export default {
  async add(spaceId) {
    return (await HTTP.post(`musical-spaces/${spaceId}/favorite`)).data;
  },

  async remove(spaceId) {
    return (await HTTP.delete(`musical-spaces/${spaceId}/favorite`)).data;
  },

  async getMine() {
    return (await HTTP.get("favorites/me")).data;
  }
};
