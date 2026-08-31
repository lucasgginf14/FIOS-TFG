import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  plugins: [vue(), mode === "development" && vueDevTools()].filter(Boolean),
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url))
    }
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes("node_modules")) {
            return;
          }

          if (id.includes("vue") || id.includes("vue-router") || id.includes("vue-i18n")) {
            return "vendor-vue";
          }

          if (id.includes("bootstrap") || id.includes("@popperjs")) {
            return "vendor-bootstrap";
          }

          if (id.includes("leaflet")) {
            return "vendor-leaflet";
          }

          if (id.includes("axios")) {
            return "vendor-axios";
          }
        }
      }
    }
  }
}));
