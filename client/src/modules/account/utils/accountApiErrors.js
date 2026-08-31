import { getApiErrorMessage } from "@/common/apiErrors";

export function getAccountApiErrorMessage(error, t, fallbackKey) {
  return getApiErrorMessage(error, t, fallbackKey);
}
