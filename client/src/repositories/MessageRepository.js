import HTTP from "@/common/http";

export default {
  async getByReservation(reservationId) {
    return (await HTTP.get(`reservations/${reservationId}/messages`)).data;
  },

  async sendToReservation(reservationId, text) {
    return (await HTTP.post(`reservations/${reservationId}/messages`, { content: text })).data;
  },

  async getUnread() {
    return (await HTTP.get("messages/unread")).data;
  }
};
