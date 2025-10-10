import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";


const BACKEND = "http://localhost:8080";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {

      "/api": {
        target: BACKEND,
        changeOrigin: true,
      },

      "/me": {
        target: BACKEND,
        changeOrigin: true,
      },
      "/leagues": {
        target: BACKEND,
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      "@": "/src",
    },
  },
});

