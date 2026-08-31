import HTTP from "@/common/http";

export default {
  async getAll(params = {}) {
    return (await HTTP.get("musical-spaces", { params })).data;
  },

  async getMine() {
    return (await HTTP.get("musical-spaces/me")).data;
  },

  async getById(id) {
    return (await HTTP.get(`musical-spaces/${id}`)).data;
  },

  async getEquipment(id) {
    return (await HTTP.get(`musical-spaces/${id}/equipment`)).data;
  },

  async addSpaceEquipment(id, payload) {
    return (await HTTP.post(`musical-spaces/${id}/equipment`, payload)).data;
  },

  async updateSpaceEquipment(id, payload) {
    return (await HTTP.put(`space-equipment/${id}`, payload)).data;
  },

  async deleteSpaceEquipment(id) {
    return (await HTTP.delete(`space-equipment/${id}`)).data;
  },

  async getAvailability(id, date) {
    return (await HTTP.get(`musical-spaces/${id}/availability`, { params: { date } })).data;
  },

  async getSchedules(id) {
    return (await HTTP.get(`musical-spaces/${id}/schedules`)).data;
  },

  async createSchedule(id, payload) {
    return (await HTTP.post(`musical-spaces/${id}/schedules`, payload)).data;
  },

  async updateSchedule(id, payload) {
    return (await HTTP.put(`schedules/${id}`, payload)).data;
  },

  async deleteSchedule(id) {
    return (await HTTP.delete(`schedules/${id}`)).data;
  },

  async getExceptions(id) {
    return (await HTTP.get(`musical-spaces/${id}/exceptions`)).data;
  },

  async createException(id, payload) {
    return (await HTTP.post(`musical-spaces/${id}/exceptions`, payload)).data;
  },

  async updateException(id, payload) {
    return (await HTTP.put(`exceptions/${id}`, payload)).data;
  },

  async deleteException(id) {
    return (await HTTP.delete(`exceptions/${id}`)).data;
  },

  async create(payload) {
    return (await HTTP.post("musical-spaces", payload)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`musical-spaces/${id}`, payload)).data;
  },

  async deactivate(id) {
    return (await HTTP.patch(`musical-spaces/${id}/deactivate`)).data;
  }
};
