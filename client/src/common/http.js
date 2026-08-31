import axios from "axios";
import { BACKEND_URL } from "../constants.js";
import auth from "./auth.js";

const HTTP = axios.create({
  baseURL: BACKEND_URL
});

const onResponseSuccess = (response) => response;

function normalizeEndpoint(url) {
  const rawUrl = String(url ?? "");

  try {
    const parsedUrl = new URL(rawUrl, BACKEND_URL);
    const parsedBaseUrl = new URL(BACKEND_URL);
    let endpoint = parsedUrl.pathname.replace(/^\/+/, "");
    const basePath = parsedBaseUrl.pathname.replace(/^\/+|\/+$/g, "");

    if (basePath && endpoint.startsWith(`${basePath}/`)) {
      endpoint = endpoint.slice(basePath.length + 1);
    }

    return endpoint.replace(/^api\//, "");
  } catch {
    return rawUrl.replace(/^\/+/, "").replace(/^api\//, "");
  }
}

function isLoginRequest(config) {
  return normalizeEndpoint(config?.url).endsWith("account/login");
}

function isPublicRequest(config) {
  const method = String(config?.method ?? "get").toLowerCase();
  const endpoint = normalizeEndpoint(config?.url);

  if (method === "post") {
    return endpoint === "account/register" || endpoint === "search/natural-language";
  }

  if (method !== "get") {
    return false;
  }

  return /^(images|instruments|equipments|band-recruitments|musical-spaces|search|events|bands)(\/|$)/.test(endpoint);
}

const onResponseFailure = (error) => {
  const status = error?.response?.status;

  if (!status) {
    return Promise.reject(error);
  }

  if (isLoginRequest(error.config)) {
    return Promise.reject(error);
  }

  if (status === 401) {
    auth.logout();

    if (isPublicRequest(error.config) && !error.config?._retriedWithoutAuth) {
      const retryConfig = {
        ...error.config,
        _retriedWithoutAuth: true,
        headers: { ...(error.config?.headers ?? {}) }
      };
      delete retryConfig.headers.Authorization;
      delete retryConfig.headers.authorization;
      return HTTP.request(retryConfig);
    }
  }

  return Promise.reject(error);
};

const onRequest = (config) => {
  const token = auth.getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
};

HTTP.interceptors.response.use(onResponseSuccess, onResponseFailure);
HTTP.interceptors.request.use(onRequest);

export default HTTP;
