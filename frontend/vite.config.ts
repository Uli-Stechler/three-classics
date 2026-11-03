import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In development the API runs separately; in Docker nginx does the same proxying.
const apiTarget = process.env.API_TARGET ?? 'http://localhost:8080';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': apiTarget,
    },
  },
  test: {
    include: ['src/**/*.test.ts'],
  },
});
