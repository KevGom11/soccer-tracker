import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Adjust target if your backend port differs
const BACKEND = "http://localhost:8080";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // everything under /api already hits backend
      "/api": {
        target: BACKEND,
        changeOrigin: true,
      },
      // add proxies for endpoints that are NOT under /api
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

