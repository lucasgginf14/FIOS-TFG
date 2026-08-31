import HTTP from "@/common/http";

export default {
  async reserve(eventId) {
    return (await HTTP.post(`events/${eventId}/reserve`)).data;
  },

  async purchase(eventId) {
    return this.reserve(eventId);
  },

  async getMine() {
    return (await HTTP.get("event-purchases/me")).data;
  },

  async getEventStatus(eventId) {
    return (await HTTP.get(`events/${eventId}/reservation-status`)).data;
  },

  async cancel(purchaseId) {
    return (await HTTP.patch(`event-purchases/${purchaseId}/cancel`)).data;
  },

  async getEventReservations(eventId) {
    return (await HTTP.get(`events/${eventId}/reservations`)).data;
  }
};
