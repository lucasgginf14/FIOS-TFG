import HTTP from "@/common/http";

export default {
  async getAll() {
    return (await HTTP.get("equipments")).data;
  }
};
