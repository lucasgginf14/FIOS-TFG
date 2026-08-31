import HTTP from "@/common/http";

export default {
  async search(params = {}) {
    return (await HTTP.get("search", { params })).data;
  },

  async searchNaturalLanguage(payload) {
    return (await HTTP.post("search/natural-language", payload)).data;
  },

  async getHistory() {
    return (await HTTP.get("search/history")).data;
  },

  async getMap(params = {}) {
    return (await HTTP.get("search/map", { params })).data;
  }
};
