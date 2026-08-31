import HTTP from "@/common/http";

export default {
  async create(payload) {
    return (await HTTP.post("reservations", payload)).data;
  },

  async getMine() {
    return (await HTTP.get("reservations/me")).data;
  },

  async getById(id) {
    return (await HTTP.get(`reservations/${id}`)).data;
  },

  async update(id, payload) {
    return (await HTTP.put(`reservations/${id}`, payload)).data;
  },

  async cancel(id, payload) {
    return (await HTTP.patch(`reservations/${id}/cancel`, payload)).data;
  },

  async updateState(id, payload) {
    return (await HTTP.patch(`reservations/${id}/state`, payload)).data;
  },

  async getManagedSpacesReservations() {
    return (await HTTP.get("musical-spaces/me/reservations")).data;
  }
};
