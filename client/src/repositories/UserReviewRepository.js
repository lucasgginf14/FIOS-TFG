import HTTP from "@/common/http";

export default {
  async getPending() {
    return (await HTTP.get("user-reviews/pending")).data;
  },

  async createForReservation(reservationId, payload) {
    return (await HTTP.post(`reservations/${reservationId}/user-review`, payload)).data;
  },

  async getMine() {
    return (await HTTP.get("user-reviews/me")).data;
  },

  async getReceived() {
    return (await HTTP.get("user-reviews/received")).data;
  },

  async getByUser(userId) {
    return (await HTTP.get(`users/${userId}/reviews`)).data;
  },

  async getUserRating(userId) {
    return (await HTTP.get(`users/${userId}/rating`)).data;
  }
};
