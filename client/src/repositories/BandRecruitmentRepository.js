import HTTP from "@/common/http";

export default {
  async getAll() {
    return (await HTTP.get("band-recruitments")).data;
  },

  async getMine() {
    return (await HTTP.get("band-recruitments/me")).data;
  },

  async getById(id) {
    return (await HTTP.get(`band-recruitments/${id}`)).data;
  },

  async createForBand(bandId, payload) {
    return (await HTTP.post(`bands/${bandId}/recruitments`, payload)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`band-recruitments/${id}`, payload)).data;
  },

  async close(id) {
    return (await HTTP.patch(`band-recruitments/${id}/close`)).data;
  }
};
