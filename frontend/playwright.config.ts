import { defineConfig, devices } from '@playwright/test';

// Defaults to the Vite dev server (with the backend on :8080 using the dev profile). Point
// E2E_BASE_URL at http://localhost:8080 to test the Docker Compose stack instead.
const baseURL = process.env.E2E_BASE_URL ?? 'http://localhost:5173';

export default defineConfig({
  testDir: './e2e',
  timeout: 60_000,
  expect: { timeout: 10_000 },
  // One backend serves every test; running serially keeps AI timing predictable.
  workers: 1,
  reporter: [['list']],
  use: {
    baseURL,
    // Set PW_CHANNEL=msedge or chrome to use an installed browser instead of Playwright's Chromium.
    channel: process.env.PW_CHANNEL || undefined,
    trace: 'retain-on-failure',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
