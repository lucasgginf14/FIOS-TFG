import HTTP from "@/common/http";

export default {
  async getAll() {
    return (await HTTP.get("bands")).data;
  },

  async getMine() {
    return (await HTTP.get("bands/me")).data;
  },

  async getById(id) {
    return (await HTTP.get(`bands/${id}`)).data;
  },

  async create(payload) {
    return (await HTTP.post("bands", payload)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`bands/${id}`, payload)).data;
  },

  async deactivate(id) {
    return (await HTTP.patch(`bands/${id}/deactivate`)).data;
  }
};
