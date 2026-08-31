import HTTP from "@/common/http";

export default {
  async getAll() {
    return (await HTTP.get("instruments")).data;
  },

  async getById(id) {
    return (await HTTP.get(`instruments/${id}`)).data;
  },

  async create(payload) {
    return (await HTTP.post("instruments", payload)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`instruments/${id}`, payload)).data;
  },

  async getMine() {
    return (await HTTP.get("account/me/instruments")).data;
  },

  async updateMine(payload) {
    return (await HTTP.put("account/me/instruments", payload)).data;
  }
};
