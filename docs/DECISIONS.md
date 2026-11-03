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
