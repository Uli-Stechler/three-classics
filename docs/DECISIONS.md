# Decisions

A running log of decisions made while building, newest at the bottom of each section. The
summary and "what I'd do next" sections are finalised at the end of the project.

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
- **Create returns the same envelope as a move** (`{ game, events }` rather than a bare `GameView`). When
  the computer moves first, its opening move arrives as an event and animates like any other reply.
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
