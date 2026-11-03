# Decisions

## The main decisions

1. **The server is the referee.** All rules and all computer play are in the Java backend. The client
   draws the state, offers only the server's `legalMoves`, and sends back the one the player picked;
   the server validates, applies, lets the computer reply and persists, in one transaction. One source
   of truth, rules that are unit-tested in isolation, and no way to cheat from the browser.
2. **Multi-step moves are legal paths matched by prefix.** A checkers multi-jump arrives as a complete
   path; the client accepts a click only if the squares so far are a prefix of some legal path, and
   submits when a path is complete. Backgammon turns use the same idea through a step graph, because
   listing every ordering of a double was measured at up to 1.8 MB (see Backgammon below).
3. **Boards are single SVGs with a fixed viewBox.** Cells and pieces are drawn in fixed coordinates and
   the SVG scales as a whole, so nothing can change size with content. Highlights are fills, rings and
   transforms only, and a Playwright layout test checks every cell's size and position across moves.
4. **Scenes are procedural, not photos.** Each theme is layered SVG silhouettes plus CSS gradients and
   one particle canvas, all driven by a single shared `requestAnimationFrame` loop that pauses when
   the tab is hidden. That keeps the repo free of licensing questions, scales to any screen and can
   go fully still for reduced motion. A licensed photo could be dropped in via `--scene-photo`.
5. **Three games, one engine interface.** `GameEngine<S, M>` and `AiPlayer<S, M>` are implemented per
   game and registered as a `GameModule`. The service, API, persistence, session hook and page shell
   are written once; each game adds an engine, an AI, a board and its rules text.

## Trade-offs

- **No doubling cube.** Backgammon is played as single games, scored 1/2/3 for single, gammon and
  backgammon. The cube (and the Crawford, Jacoby and beaver rules that come with matches and money
  play) was the first thing to cut for time.
- **Standard scoring rather than the reference PDF's.** The PDF scores a game by counting the loser's
  remaining checkers; the brief and standard play use single/gammon/backgammon, so that's what is used.
- **No accounts.** One local player; stats are computed from finished games in the database.
- **Spring Boot 3.5, not 4.x**, to keep to APIs I know well inside the time budget.
- **No Docker on the build machine.** Everything except the containers was run and tested (backend
  against a local PostgreSQL 16, Playwright against the dev server and the production bundle). The
  Dockerfiles, compose file and Kubernetes manifests are written and their YAML is validated, but
  `docker compose up` and kind were not run here. That is the first thing to verify.
- **AI depth is budgeted, not fixed.** Hard searches as deep as it can in about 0.85 s, so its strength
  depends a little on the machine. Medium and Easy are deterministic in effort.
- **The client draws its own move before the server confirms it.** This keeps input instant. It never
  decides legality (the move comes from the server's list), and it always snaps to the server's state.

## What I'd do next

- Run `docker compose up --build` and `make k8s-up` on a machine with Docker and fix anything they turn
  up; then add CI (GitHub Actions) running the Maven suite with Testcontainers, Vitest, and Playwright
  against the compose stack.
- The stretch goals in order: a Hint button (ask Hard), takeback (undo the last move pair from history),
  optional sound, then the backgammon doubling cube.
- Stronger computer play: an opening book and transposition table for checkers, a perfect-play Connect
  Four solver for Hard, and a 3-ply or learned evaluation for backgammon.
- Property-based tests (random positions) comparing engine invariants, and a replay test that rebuilds
  every stored game from its move history.
- Small UX: double-click a backgammon checker to play the larger die, and announce the computer's
  move to screen readers in words ("Computer played c3 to d4").

---

## Log (as built)

Decisions made along the way, by area.

## Environment and tooling

- **Spring Boot 3.5 rather than 4.x.** 3.5 is the mature line with the APIs I know best (Jackson 2,
  Flyway starter auto-config). For a four-hour build, low risk beats the newest major version.
- **Java 21 target, built with a newer JDK locally.** The dev machine only has JDK 17 system-wide, so
  local builds use JetBrains Runtime 25 with `--release 21`. Containers build and run on Temurin 21.
- **Vite 6.** Vite 7+ needs Node 20.19; the dev machine has 20.17. Vitest 3 matches Vite 6.
- **No Docker on the dev machine.** Compose, Testcontainers and kind could not be run while building.
  Engines, AI, service and frontend were tested against a portable local PostgreSQL 16 instead. The
  integration tests use Testcontainers when Docker is present, or `TEST_DATABASE_URL` when it is not.
- **No `@types/node` or jsdom.** Neither is on the approved list. The Vitest suites test pure
  functions, so they need no DOM, and the config files are left out of `tsc`.
- **Moodboards are git-ignored.** They are Pinterest captures of other people's photos; the brief says
  never to ship them, so they stay local in `docs/reference/moodboards/`.

## Architecture

- **The server is the referee.** All rules and AI live in Java. The client renders `state`, offers only
  `legalMoves`, and sends back the move it picked. One source of truth, unit-testable rules, and the
  same shape a real game site uses to stop cheating.
- **One `GameEngine<S, M>` interface for three games.** Each game is a `GameModule` (engine, AI, JSON
  types). `GameService` is written once against the interface; adding a game means adding a module bean.
- **Create returns the same envelope as a move** (`{ game, before, events }` rather than a bare
  `GameView`). When the computer moves first, its opening move arrives as an event and animates like
  any other reply; `before` is the position to animate from.
- **Rematches go through the server.** `POST /api/games` takes an optional `rematchOf`; the rule that a
  Connect Four rematch swaps who goes first lives in `RematchPolicy` and is unit-tested.
- **Optimistic locking twice over.** Each move names the version it was based on (409 on mismatch, which
  stops double clicks), and JPA `@Version` guards concurrent writes as a backstop.
- **Events carry a state snapshot.** Each event in a response includes the state right after it, so the
  client animates event by event and snaps to server truth after each one.
- **The client previews only what it was offered.** When the human picks a move, the board draws it
  at once (a dropped disc, a slid checker) so input never waits on the network or the AI. The server
  still validates it, and the board snaps to the server's state from the response.
- **Shared game shell.** Top bar, centred board column, side panel, rules drawer, confirm dialogs and the
  result modal are one component (`GameShell`) plus one hook (`useGameSession`). Each game supplies its
  board, side names, swatches and rules. Connect Four set the pattern; the others reuse it unchanged.

## Connect Four

- **AI levels follow the solved game.** With perfect play the first player wins by opening in the
  centre, so Hard always opens there. Medium opens in one of the three middle columns and then picks
  at random among moves within a few points of its best, so games vary. Easy plays randomly but takes
  a win or blocks one about half the time each.
- **Hard's search** is negamax with alpha-beta on a bitboard, a transposition table, centre-first move
  ordering and iterative deepening against an 85% slice of the one-second budget. An explicit
  "win now, else block a single threat" check runs first, which is exactly what the tests demand.
- **Discs fall behind the frame.** The SVG draws hole backs, then discs, then the frame with round
  holes cut out, so a falling disc is only visible through the holes, like the real thing.
- **Colour plus shape.** Gold discs carry a centre dot, so the two sides differ by more than colour.
  The turn marker in the panel is a bar as well as a highlight.

## Checkers

- **Board as a 64-character string** (`d`/`D`/`l`/`L`/`.`), square = row × 8 + column with Dark at the
  bottom. It is compact in JSON, readable in tests, and doubles as the repetition key.
- **Draw bookkeeping lives in the state.** `quietPlies` counts plies since the last capture or man move
  (80 = 40 each), and `positions` keeps the keys since then, so threefold repetition is a pure check.
- **Captured pieces stay on the board until the move ends,** which is how American checkers works and
  what stops a piece being jumped twice.
- **The move list uses algebraic squares from Dark's side** ("c3×e5×g7") rather than 1–32 numbering,
  because casual players read it more easily.
- **Hard** is alpha-beta with iterative deepening and root move reordering, plus a capture extension so
  exchanges resolve past the horizon. The evaluation adds back-row guard, centre control, mobility and,
  in endgames, drives the leading side's kings toward the enemy so wins get converted.
- **Building a jump click by click.** The human clicks the piece, then each landing square; only steps
  that continue a legal path are accepted, Esc or clicking the piece again undoes, and the move submits
  itself when the path is complete. Pieces about to be captured are dimmed while the jump is built.

## Backgammon

- **Points from P1's side, steps in the mover's own numbering.** The state stores 24 signed counts
  from Ivory's point of view; every step uses the mover's numbers (25 = bar, 0 = off), matching how
  players write moves. A bar entry with a die of n lands on the mover's point 25 − n, the opponent's
  n-point.
- **Turn generation follows the brief's recipe:** DFS over both dice orders (four steps for doubles),
  keep the longest sequences (max-dice rule), and if only one die fits, keep the larger.
- **The client gets a step graph, not every ordering.** Doubles can be played in thousands of orders:
  measured over 300 games, the worst roll had 9,085 ordered sequences (1.8 MB of JSON). The server
  now merges orderings that reach the same intermediate position into a graph (worst case 62 KB,
  average under 2 KB). The client walks it one checker step at a time, so any legal order is
  accepted and nothing illegal is offered. `legalMoves` lists one turn per distinct result, and the
  server still validates the submitted turn against every legal ordering.
- **Passing is automatic and announced.** A roll with no legal play, or being closed out, passes the
  turn inside the engine and is recorded as a notice in the state, which the client turns into a
  short toast. A closed-out player doesn't roll at all.
- **Standard scoring, not the PDF's.** Single 1, gammon 2, backgammon 3. The rulebook's "count the
  loser's remaining checkers" variant is not used.
- **No doubling cube in v1.** A deliberate cut: single games only, so no Crawford or money-play rules.
- **Hard** prunes to the eight best candidates by the static evaluation, then scores each by the
  opponent's best reply averaged over all 21 rolls. In a pure race it plays greedily on pip count,
  which is close to optimal there.
- **Checkers have identities on the client.** Each checker gets an id from a base position, replayed
  through the steps since then, so moves, hits (to the bar) and bear-offs (to the tray) all slide.
  Stacks over five show a count badge instead of overflowing the point.

## Polish

- **Ambient motion follows the system until the player chooses.** The setting is only saved once it is
  toggled, so a later `prefers-reduced-motion` change still applies to anyone who never touched it.
- **Accessibility is tested, not just intended.** A Playwright spec drives Connect Four and Checkers
  entirely from the keyboard, checks the result modal traps focus and returns it, opens a game from
  the home page with Tab and Enter, and confirms every scene is static under reduced motion.
- **Contrast was measured.** All text/surface pairs in the four themes clear WCAG AA (most are 8:1 or
  better). The home page's lagoon teal was deepened from #2F8F86 to #287A72 so white button text
  reaches 5.1:1.
