import HTTP from "@/common/http";

export default {
  async getOverview() {
    return (await HTTP.get("admin")).data;
  },

  async getUsers() {
    return (await HTTP.get("admin/users")).data;
  },

  async getUserById(id) {
    return (await HTTP.get(`admin/users/${id}`)).data;
  },

  async setUserActive(id, active) {
    return (await HTTP.patch(`admin/users/${id}/active`, { active })).data;
  },

  async promoteUserToAdmin(id) {
    return (await HTTP.patch(`admin/users/${id}/admin-role`)).data;
  },

  async revokeAdminRole(id) {
    return (await HTTP.delete(`admin/users/${id}/admin-role`)).data;
  },

  async getMusicalSpaces() {
    return (await HTTP.get("admin/musical-spaces")).data;
  },

  async getMusicalSpaceById(id) {
    return (await HTTP.get(`admin/musical-spaces/${id}`)).data;
  },

  async updateMusicalSpaceApprovalStatus(id, approvalStatus) {
    return (await HTTP.patch(`admin/musical-spaces/${id}/approval-status`, { approvalStatus }))
      .data;
  },

  async getReservations() {
    return (await HTTP.get("admin/reservations")).data;
  },

  async getReservationById(id) {
    return (await HTTP.get(`admin/reservations/${id}`)).data;
  },

  async cancelReservation(id, cancellationReason) {
    return (await HTTP.patch(`admin/reservations/${id}/cancel`, { cancellationReason })).data;
  },

  async getSpaceReviews() {
    return (await HTTP.get("admin/space-reviews")).data;
  },

  async deleteSpaceReview(id) {
    return (await HTTP.delete(`admin/space-reviews/${id}`)).data;
  },

  async getUserReviews() {
    return (await HTTP.get("admin/user-reviews")).data;
  },

  async deleteUserReview(id) {
    return (await HTTP.delete(`admin/user-reviews/${id}`)).data;
  },

  async getBandRecruitments() {
    return (await HTTP.get("admin/band-recruitments")).data;
  },

  async deleteBandRecruitment(id) {
    return (await HTTP.delete(`admin/band-recruitments/${id}`)).data;
  },

  async getEvents(params = {}) {
    return (await HTTP.get("admin/events", { params })).data;
  },

  async getEventById(id) {
    return (await HTTP.get(`admin/events/${id}`)).data;
  },

  async importTicketmasterEvent(externalId) {
    return (await HTTP.post(`admin/events/import/ticketmaster/${externalId}`)).data;
  }
};
