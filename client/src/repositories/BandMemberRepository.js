import HTTP from "@/common/http";

export default {
  async getByBand(bandId) {
    return (await HTTP.get(`bands/${bandId}/members`)).data;
  },

  async searchCandidates(bandId, query) {
    return (await HTTP.get(`bands/${bandId}/members/candidates`, { params: { query } })).data;
  },

  async addMember(bandId, payload) {
    return (await HTTP.post(`bands/${bandId}/members`, payload)).data;
  },

  async updateRole(bandId, memberId, payload) {
    return (await HTTP.patch(`bands/${bandId}/members/${memberId}/role`, payload)).data;
  },

  async deactivateMember(bandId, memberId) {
    return (await HTTP.patch(`bands/${bandId}/members/${memberId}/deactivate`)).data;
  },

  async leaveBand(bandId) {
    return (await HTTP.patch(`bands/${bandId}/leave`)).data;
  }
};
