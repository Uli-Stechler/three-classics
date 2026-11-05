# Three Classics

Connect Four, Checkers and Backgammon against the computer. The brief is `docs/reference/game-build-prompt.pdf`; its Section 5 rules win over the rulebooks.

## Stack
- Backend: Java 21, Spring Boot 3.5, JPA, Flyway, PostgreSQL 16, Maven wrapper (`backend/`)
- Frontend: React 19 + TypeScript + Vite 6, plain CSS Modules + CSS variables, lucide-react (`frontend/`)
- Infra: Docker Compose (`docker-compose.yml`), Kustomize (`k8s/`), `Makefile`

## Commands
- Run everything: `docker compose up --build` → http://localhost:8080
- Backend tests: `cd backend && ./mvnw test` (set `TEST_DATABASE_URL` to skip Testcontainers)
- Frontend: `npm run dev` (proxies /api to :8080), `npm test` (Vitest), `npm run e2e` (Playwright)
- Backend with dev endpoint: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Kubernetes on kind: `make k8s-up` / `make k8s-down` (README lists the plain commands for Windows)

## Layout
- `backend/.../core` shared `GameEngine<S,M>`, `AiPlayer<S,M>`, `Outcome`, `Side`; one package per game
- `backend/.../service/GameService` is the referee: load → check version → apply → AI reply → persist
- `frontend/src/games/<game>` board + page; `games/shared` session hook, page shell, prefix matcher
- `frontend/src/themes/<theme>` palette + Scene; `themes/scene` shared ticker/particles

## Rules of the codebase
- The server is the referee. The client only renders state and picks from `legalMoves`.
- Engines are pure: immutable state, `apply` returns new state, randomness only via injected `RandomGenerator`.

## Board layout (non-negotiable)
- One `<svg>` per board with a fixed viewBox; pieces are a fixed fraction of a cell.
- Board box has a fixed aspect-ratio, is the largest fit (cap ~760px tall), centred in its column.
- Nothing reflows during play: highlights use SVG fills/rings/transforms/opacity only; status, dice and
  "thinking…" lines reserve their height. Top bar 56px; side panel 280px at ≥1100px, else below the board.
- Radial scrim behind the board; board on a solid surface with one soft shadow; WCAG AA text.
- No scrollbars at 1280×720; playable at 390×844.

## Scenes
- Props: flat SVG in 2–4 palette tones, dot eyes, no outlines or glows; heroes (`data-prop="hero"`) sit in the gutters in cqw/cqh and stay ≥50% visible (`e2e/scenes.spec.ts`).
- Motion: CSS keyframes on transform/opacity only, 0% is the rest pose, crossers (`data-crosser`) are rare and hidden when paused; no new canvases or JS loops.

## Anti-slop
No purple/indigo gradients, gradient text, neon glows, glassmorphism, emoji or sparkle icons, confetti,
heavy shadows on rounded-everything, bouncing chrome, hero sections, fake stats/lorem, "Powered by AI",
extra font families (Fraunces display + IBM Plex Sans UI only), or more than two accents per theme.
Do: 8px spacing scale, short plain copy, Lucide icons at one stroke (1.75), visible focus rings,
colour plus shape for state, 8–12px radii. No Chess.com or Hasbro branding.
