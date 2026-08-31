import HTTP from "@/common/http";

export default {
  async getPending() {
    return (await HTTP.get("space-reviews/pending")).data;
  },

  async createForReservation(reservationId, payload) {
    return (await HTTP.post(`reservations/${reservationId}/space-review`, payload)).data;
  },

  async getMine() {
    return (await HTTP.get("space-reviews/me")).data;
  },

  async getBySpace(spaceId) {
    return (await HTTP.get(`musical-spaces/${spaceId}/reviews`)).data;
  },

  async getSpaceRating(spaceId) {
    return (await HTTP.get(`musical-spaces/${spaceId}/rating`)).data;
  }
};
