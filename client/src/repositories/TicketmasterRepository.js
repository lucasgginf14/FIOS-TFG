import HTTP from "@/common/http";

export default {
  async getEvents(params = {}) {
    return (await HTTP.get("ticketmaster/events", { params })).data;
  },

  async getEventById(externalId) {
    return (await HTTP.get(`ticketmaster/events/${externalId}`)).data;
  },

  async importEvent(externalId) {
    return (await HTTP.post(`admin/events/import/ticketmaster/${externalId}`)).data;
  },

  async importEvents(params = {}) {
    return (await HTTP.post("admin/events/import/ticketmaster", null, { params })).data;
  }
};
