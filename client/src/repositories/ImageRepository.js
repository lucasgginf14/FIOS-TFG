import HTTP from "@/common/http";

export default {
  async upload(file) {
    const formData = new FormData();
    formData.append("file", file);

    return (await HTTP.post("images", formData)).data;
  }
};
