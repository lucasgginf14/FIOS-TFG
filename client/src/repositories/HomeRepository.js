import HTTP from "@/common/http";

export default {
  async getFeatured() {
    return (await HTTP.get("home/featured")).data;
  }
};
