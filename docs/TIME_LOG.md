# Time log

Times are taken from the commit timestamps on 3–4 November 2026 (local time). They are the AI agent's
wall-clock time while building; please replace them with your own time and split.

| # | Milestone | Start | End | Duration | Commit |
| --- | --- | --- | --- | --- | --- |
| 0 | Read the brief, check the toolchain, plan and approval | 13:00 | 13:12 | ~12 min | — |
| 1 | Skeleton: repo, Spring Boot service and schema, React shell, Docker files, CLAUDE.md | 13:12 | 13:31 | ~19 min | `3f204ee` |
| 2a | Connect Four engine, AI and API tests | 13:31 | 13:39 | ~8 min | `f28c918` |
| 2b | Connect Four client, ocean scene, shared game shell, Playwright | 13:39 | 13:53 | ~14 min | `e1c8ad2` |
| 3 | Checkers: engine, AI, winter scene, board, tests | 13:53 | 14:07 | ~14 min | `2c57ca4` |
| 4a | Backgammon engine, AI and rule tests | 14:07 | 14:20 | ~13 min | `b511596` |
| 4b | Backgammon client, desert scene, step graph, Playwright | 14:20 | 14:38 | ~18 min | `d0b8932` |
| 5 | Home page, jungle scene, game cards | 14:38 | 14:44 | ~6 min | `a4c812e` |
| 6 | Polish: motion settings, focus, contrast, keyboard tests | 14:44 | 14:51 | ~7 min | `4982395` |
| 7 | Kubernetes manifests, README, decisions, this log | 14:51 | 14:56 | ~5 min | `da90c63` |
| 8 | Next day, once Docker was installed: Compose, Testcontainers and kind runs, two Kubernetes fixes | 20:27 | 21:08 | ~41 min | see `git log` |
| 9 | Scene refresh: rainforest home, sea life, lake and fox, cacti and tumbleweed, hero check, screenshots | 21:36 | 22:35 | ~59 min | `820d479`…`HEAD` |
|   | **Total** | | | **~3 h 36 min** | |

## Split by area (approximate)

| Area | Share |
| --- | --- |
| Rules engines and their tests | ~16% |
| Computer players | ~8% |
| Frontend (boards, shell, scenes) | ~40% |
| Infrastructure (Docker, Kubernetes) and verifying it | ~21% |
| Testing and visual review (Playwright, screenshots) | ~10% |
| Docs | ~5% |
