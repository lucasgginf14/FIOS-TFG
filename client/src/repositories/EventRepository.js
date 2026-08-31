import HTTP from "@/common/http";

export default {
  async getAll(params = {}) {
    return (await HTTP.get("events", { params })).data;
  },

  async getUpcoming() {
    return (await HTTP.get("events/upcoming")).data;
  },

  async getMap(params = {}) {
    return (await HTTP.get("events/map", { params })).data;
  },

  async getById(id) {
    return (await HTTP.get(`events/${id}`)).data;
  },

  async create(payload) {
    return (await HTTP.post("events", payload)).data;
  },

  async createForBand(bandId, payload) {
    return (await HTTP.post(`bands/${bandId}/events`, payload)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`events/${id}`, payload)).data;
  },

  async delete(id) {
    return (await HTTP.delete(`events/${id}`)).data;
  }
};
