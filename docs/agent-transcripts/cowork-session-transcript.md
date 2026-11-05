# Cowork Session Transcript: Three Classics (Chess.com Take-Home)

| | |
|---|---|
| **Participant** | Uli Stechler |
| **Assistant** | Claude (Cowork, desktop app), configured model `claude-opus-5-5` |
| **Dates** | November 3–4, 2026 |
| **Purpose** | Turn a draft build prompt into a complete, AI-ready brief for the Chess.com Product Engineering Internship take-home (Connect Four, Checkers and Backgammon), then write a follow-up "scene refresh" prompt for the finished repo |
| **Repo built from the prompt** | https://github.com/Uli-Stechler/three-classics |

> **About this file**
>
> - **How it was made:** Claude compiled it from the session itself.
> - **Messages:** Uli's messages and Claude's replies appear as they were sent. Links to local files are shown as plain file names.
> - **Tool activity:** file reads, web searches, page fetches, file writes and saves to Uli's computer are summarized under **Actions**.
> - **Not included:** Claude's private reasoning, and the app's own system instructions.
> - **Attachments:** listed by name, not embedded.
> - **Deliverables:** the two prompts the session produced are reproduced in full in the appendices.

---

## Outcomes at a glance

| # | Request | Result |
|---|---|---|
| 1 | Improve the build prompt and recommend the right Claude tool and model | `game-build-prompt.md`, a full build brief. Recommendation: Claude Code on Opus at Extra/High effort for the build. |
| 2 | Use online rule sources for Connect 4 and Backgammon | The brief was updated with the Boardgammon rules and cross-checked Connect 4 rules. wikiHow was blocked for Claude. |
| 3 | Clarify whether the rule files are still needed | Answered: the rule PDFs are no longer needed, but keep the moodboards. |
| 4 | Save the 4 moodboard photos to the computer | Saved `jungle.png`, `ocean.png`, `desert.png` and `winter.png` to `Downloads\Chess_Project`. |
| 5 | Write an add-on prompt for scene and design fixes on the finished repo | `scene-refresh-prompt.md`, plus `jungle-rainforest.png` saved to `Chess_Project`. |
| 6 | Export this session | This file. |

### Files

**Inputs Uli provided:**
- `ChessCom_Product_Engineering_Internship_Take_Home_Assignment.pdf` (1 page)
- `backgammon_Official-Rules.pdf` (12 pages, a rulebook for a whole game set)
- `A5640_en-in_connect-4-game.pdf` (10 pages, a Hasbro kids' playbook)
- `checkers-rules.pdf` (1 page)
- 4 Pinterest moodboard screenshots (jungle, ocean, desert, winter), each attached twice
- 1 later rainforest moodboard screenshot
- 2 rule links:
  - boardgammon.com/backgammon/rules
  - wikihow.com/Play-Connect-4

**Outputs Claude produced:**
- `game-build-prompt.md`: the main build brief (Appendix A)
- `scene-refresh-prompt.md`: the add-on prompt for the scene redesign (Appendix B)
- Saved to `C:\Users\ustec\Downloads\Chess_Project`:
  - `jungle.png`, `ocean.png`, `desert.png`, `winter.png`
  - `jungle-rainforest.png`
- `cowork-session-transcript.md`: this file

---

## Turn 1: Improve the build prompt and pick the right AI

### Uli

*Attachments: the 4 Pinterest boards (jungle, ocean, desert, winter), the assignment PDF, and three rules PDFs (backgammon, Connect 4, checkers).*

> Below is a prompt that I've written and I've attached the assignment instructions, 4 pinterest boards for design, and 3 files with additional game rules. Please read through this prompt and correct it/ add to it in any way you think that will make it better, cooler, and just work better for an AI. Priorities are games work and looks cool but doesn't look like AI slop. I will provide the same files when I give the updated prompt. Please make it so I can give it to an AI to build this project.
>
> Since I've done some of these games in my own free time already, I want to focus on the ones I haven't done. Let's do backgammon, checkers, and connect 4. I want to create a main page that has options for connecting to all 3, pick one of the themes I've provided for the main page, please give things natural nice animations, like water flowing or leaves moving or snow falling. The first and foremost goal is to make sure the games are playable, it's important to have the window display in a way where the game is easy to see and the background is nice but not overshadowing the game, and also that the gameplay itself works. One thing to look out for is that the boards are centered and the squares and pieces are all the correct size, and that they dont change size based on if there's a piece there or not. I want the background and theme of each of them to look clean, not AI slop, focus on clean versus overdoing it, keep the design simple. Make sure all the games follow the rules and have a pop window for winning/losing with play again option and return to home option, also restart option and difficulty settings, easy medium and hard for each game.
>
> Let's start by trying to use all 4 themes, one for the main page and one for each of the games. I'm going to also provide you with the official rules to each game, please let me know if you can't read any of them. Follow the coding guidelines for structure in the doc, and I'll make edits after.
>
> Also before we do any task, please confirm which AI you think is best for this task, Claude Chat, Cowork, Design, or Code, and which model Opus Sonnet or Haiku would be best, and at what strength. Right now I am using Cowork Opus 5.5 Extra, if that is the correct model to do all of this, just let me know and then continue with the task, if that isn't the best, please give me a detailed explanation and explain which is best and why, and once I change it, then we will work on the actual task.
>
> Please give me ideas for anything you think I might be missing regards to game functionality and design or any other part of this project you can think of.

### Actions

- **Read the assignment PDF.** Required stack:
  - Docker + Kubernetes
  - MySQL or PostgreSQL
  - a PHP, Go or Java backend
  - a Vue or React frontend

  The time budget is about 4 hours. The submission needs:
  - a Git repo
  - a one-command README
  - a decisions note
  - a time breakdown
  - agent transcripts
- **Read the checkers PDF:** standard American checkers rules on one page.
- **Read all 12 pages of the "backgammon" PDF.** It turned out to be a full game-set rulebook:
  - backgammon on pages 1–4
  - then checkers, chess, Chinese checkers, dominoes, poker dice, poker and sudoku

  Claude noticed its single-game scoring rule is nonstandard.
- **Read the Connect 4 PDF** and extracted the text from all 10 pages. It's a Hasbro "Toybox Tools" kids' playbook about counting and dropping discs. It contains no win rules.
- **Web searches:**
  - comparisons of Claude, Claude Code and Cowork
  - Chess.com's backend stack. Its job posts are PHP-heavy.

### Claude (interim message)

> **Quick answer on the model:** Cowork + Opus 5.5 Extra is a good fit for what we're doing right now (reading your files and rewriting the prompt), so I'll keep going. For the **build**, though, I'd hand the finished prompt to **Claude Code**, still on Opus at Extra/high effort. I'll explain why in the final reply. You don't need to switch anything yet.
>
> **What I found in your files:**
> - The **Connect 4 PDF** is a Hasbro "Toybox Tools" playbook for young kids. It covers counting discs and dropping them in, but has **no actual rules** (nothing on four-in-a-row, diagonals or draws). I'll write the standard rules into the prompt.
> - The **"backgammon" PDF** is a 12-page rulebook for a whole game set. Backgammon is pages 1–4, followed by checkers, chess, Chinese checkers, dominoes, poker and sudoku. I could read all of it. One catch: its single-game scoring rule isn't standard, so I'll tell the builder which rule to use instead.
> - The **checkers PDF** and the **assignment** read fine.
>
> One thing the assignment changes: Chess.com requires **Docker + Kubernetes, MySQL/Postgres, a PHP/Go/Java backend and a React/Vue frontend**, plus a README, a decisions note, a time breakdown and agent transcripts. I'm building the prompt around that.

### Claude asked (multiple choice)

> The Chess.com take-home requires the backend to be PHP, Go, or Java. The plan is to put the game rules and the computer opponent (easy/medium/hard) on the backend so the server is the referee, with React rendering the board. Which backend language should the prompt use? Pick the one you'd be most comfortable defending line-by-line in a follow-up interview.
>
> - **Java + Spring Boot (Recommended):** Your strongest listed language. Records/enums/sealed types model game state cleanly, JUnit for rule tests, plenty fast for the AI search. Heavier Docker image and slower startup.
> - **Go:** Lean and fast, tiny Docker image, instant startup, very readable. Great fit for AI search. Choose if you're comfortable reading Go.
> - **PHP (Symfony/Laravel):** Chess.com's own backend job posts are PHP-heavy, so it signals stack fit. But PHP is the weakest fit for CPU-heavy game-tree search on 'hard'.

**Uli chose: Java + Spring Boot.**

### Actions (continued)

- **Wrote `game-build-prompt.md`**, a 14-section build brief. It covers:
  - the hard requirements
  - priorities and product scope
  - architecture and API
  - authoritative rules for all three games
  - the AI difficulty levels
  - visual design and the anti-slop rules
  - accessibility and dependencies
  - stretch goals
  - testing
  - infrastructure and docs
  - milestones and the definition of done
- **Corrected it:**
  - used the exact reference file names
  - tightened the "AI must block" test wording
  - added the 4-hour time-budget note
- **Checked** with a scripted grep that key requirements appear in the brief: Play again, Restart, the difficulty levels, aspect-ratio, the max-dice rule, crowning mid-jump, bar entry, `docker compose`, `kubectl`, TIME_LOG, reduced motion and gammon scoring.

### Claude (final reply)

> I rewrote your prompt into a complete build brief you can hand to an AI as-is: game-build-prompt.md.
>
> **Which tool to use:** Cowork with Opus 5.5 Extra is the right setup for rewriting the prompt, so I went ahead. For the build itself, switch to **Claude Code, still on Opus at Extra or High**.
> - **It works in your actual repo.** You're handing in a Git repo that runs with Docker and Kubernetes. Claude Code works directly in that repo on your machine: it can run Docker, the tests and screenshot checks, fix what fails, and commit as it goes. Cowork works in a cloud copy, so getting a multi-part project back onto your machine is clunkier.
> - **Chess.com wants the agent transcripts.** A Claude Code session exports cleanly with `/export`. Its plan mode also shows reviewers a clear "plan, approve, build" flow.
> - **The rules stick across sessions.** The prompt has the AI write a `CLAUDE.md` file, which Claude Code reloads every session, so the design and layout rules don't get forgotten during a long build.
> - **The others don't fit.** Chat can't run code. Design is for visual mockups, not a Java/Postgres/Docker project.
> - **Stay on Opus.** Backgammon's move rules and the computer opponent are the hard part, and that's where the stronger model pays off. Sonnet is fine for later polish. Haiku is too light. Max effort is slower and uses more of your limits without much benefit.
>
> **Theme picks:**
> - **Home:** jungle.
> - **Connect Four:** ocean. The classic frame is blue, and discs dropping look like they're falling through water.
> - **Backgammon:** desert. It's an ancient game, and warm wood and sand colors suit the board.
> - **Checkers:** winter. An ice-and-slate board looks crisp, and snowfall is calm enough not to distract.
>
> **Main additions:**
> - The required Chess.com stack (Java + Spring Boot, as you chose) and every item they ask you to submit.
> - The server acts as referee: it checks every move and runs the computer opponent.
> - Rule edge cases AI builders usually get wrong, such as backgammon's "use both dice if you can" rule and a checkers piece ending its turn when it's crowned mid-jump.
> - To keep squares and pieces from ever resizing, each board is drawn on a fixed grid. An automated check measures every square before and after moves at four screen sizes.
> - A concrete "don't look like AI" list, and a build order of one game at a time, each fully done before the next starts.
>
> **Ideas you might be missing:**
> - **Don't use the Pinterest images in the app.** They're copyrighted or of unknown origin, and your repo will be public. The prompt has the AI draw the scenes in code instead, which also looks cleaner.
> - **Already added to the prompt:**
>   - A 2-player same-device mode. It's nearly free and makes testing much easier.
>   - Games resume after a refresh, and the home cards show your win/loss record. This gives the required database a real job.
>   - A rules panel, a last-move highlight, and Undo/Confirm plus a pip count in backgammon.
> - **Cut on purpose:** the backgammon doubling cube is a stretch goal and noted as a decision.
> - **Reviewers will read this prompt** as part of your transcripts, so save this conversation too.
> - **Three games is ambitious for 4 hours.** The prompt presents them as one shared game engine with three games on top. That's your interview answer for why doing three didn't cost quality.
> - **Track your real time per phase** yourself. The time log only starts from commit times.
>
> **How to use it:**
> 1. Put the PDFs in `docs/reference/` under their original names.
> 2. Save the four boards as `docs/reference/moodboards/jungle.png`, `ocean.png`, `desert.png` and `winter.png`. Each screenshot was attached twice, so there are only four unique ones.
> 3. Save the prompt as `docs/PROMPT.md`.
> 4. In Claude Code, switch to plan mode (Shift+Tab) and say: "Read docs/PROMPT.md and everything in docs/reference/, then give me the milestone plan."
>
> Sources:
> - [Chess.com job: Senior Backend PHP Product Engineer](https://www.escapethecity.org/opportunity/11267-senior-backend-php-product-engineer-at-chesscom)
> - [Chess.com job: Engineering Internship, Backend](https://ats.rippling.com/chess/jobs/f199901c-ee5f-4846-97ed-b2252be44a30)
> - [Claude vs Claude Code vs Cowork (roadmap.sh)](https://roadmap.sh/claude-code/vs-cowork-vs-chat)

---

## Turn 2: Online rule sources

### Uli

> I found links with the rules for connect 4 and backgammon, can you use these?:
>
> [Backgammon Rules — The Complete Official Rulebook](https://boardgammon.com/backgammon/rules/)
> [How to Play Connect 4: Setup, Rules, and Strategies](https://www.wikihow.com/Play-Connect-4)

### Actions

- **Fetched boardgammon.com/backgammon/rules.** Claude extracted:
  - setup and the starting pip count of 167
  - the max-dice and larger-die rules
  - bar entry and the closeout rule
  - the bear-off edge cases
  - standard 1/2/3× scoring
  - the doubling cube, plus the Crawford, Jacoby, beaver and automatic-double rules

  Claude also found one inconsistency: the page says a 1 enters on "the opponent's 24-point", which contradicts its own numbering.
- **Tried wikihow.com/Play-Connect-4:** access was blocked for Claude. No workaround was attempted.
- **Searched for other Connect 4 rule sources** and fetched officialgamerules.org and Wikipedia. They confirmed:
  - 7×6 board with 21 discs each
  - no fixed first player, and alternating starts is suggested
  - five or more in a row counts as a win
  - the first player wins with perfect play by opening in the center
- **Edited `game-build-prompt.md` (7 changes):**
  - added an "Online rule sources" table
  - expanded the Connect Four rules
  - added the backgammon closeout rule
  - added the doubling-cube exclusions and the "Confirm ends the turn" rule
  - expanded the doubling-cube stretch spec
  - added tests: a five-in-a-row win, double-line highlighting, alternating the starter, a starting pip count of 167, bar-entry mapping and closeout

### Claude

> I used both links. Backgammon worked fully, but wikiHow blocks my access, so I couldn't read the Connect 4 page. I checked those rules against [officialgamerules.org](https://officialgamerules.org/game-rules/connect-4/) and [Wikipedia](https://en.wikipedia.org/wiki/Connect_Four) instead. Your wikiHow link is still listed in the prompt, so the AI can read it if its access allows. The updated file is game-build-prompt.md.
>
> **What changed:**
> - **Rule sources:** there's a new "Online rule sources" table, with the backgammon page as the main source. That page has one mistake: it says a 1 enters on the opponent's 24-point, which contradicts its own numbering. The prompt gives the correct rule (a roll of *n* enters on the opponent's *n*-point).
> - **Connect Four rules:**
>   - Five or more in a row counts as a win.
>   - If one disc completes two lines, both get highlighted.
>   - Who starts alternates each time you click Play again.
>   - Connect Four is a solved game: whoever moves first wins with perfect play by starting in the center. So Hard opens center when it goes first, while Easy and Medium vary their openings.
> - **Backgammon rules:**
>   - Added the "closeout" case: if every entry point is blocked, the player's turn passes automatically.
>   - The Confirm button counts as the end of the turn, like picking up your dice in a real game.
>   - The doubling cube is still a stretch goal, but it now has exact rules. The tournament and gambling variations are excluded.
> - **New tests:** each player starts with a pip count of 167, plus tests for bar entry, closeout, a five-disc win, double-line highlighting and alternating who starts.
>
> None of this changes the core plan. These are edge cases the AI would otherwise have had to guess.
>
> Sources:
> - [Boardgammon — Backgammon Rules](https://boardgammon.com/backgammon/rules/)
> - [Official Connect 4 Rules — officialgamerules.org](https://officialgamerules.org/game-rules/connect-4/)
> - [Connect Four — Wikipedia](https://en.wikipedia.org/wiki/Connect_Four)

---

## Turn 3: Are the rule files still needed?

### Uli

> To clarify, all the rules are in the prompt, which means I no longer need to provide those files when I use this prompt?

### Claude

> Yes, for the rules. Section 5 of the prompt has the complete rules for all three games, and it overrides the PDFs wherever they disagree, so you can leave the three rulebooks out.
>
> The other files are different:
>
> - **Assignment PDF:** optional. Section 1 already covers every requirement, but it's one small file and gives the AI the original wording to check against, so I'd still include it.
> - **Pinterest screenshots:** keep these. The prompt describes the colors and scenes in words, but the images show the mood in a way text can't. They help most on the "looks good, not like AI slop" goal.
>
> If you drop any files, edit the prompt to match. It currently says "read every file in `docs/reference/`" and lists all of them in a table, so the AI would go looking for files that aren't there. I can make that change if you tell me which files you're keeping.

---

## Turn 4: Save the moodboard photos

*Uli connected the folder `C:\Users\ustec\Downloads\Chess_Project` to the session.*

### Uli

> please save the 4 photos into the folder chess project onto my computer

### Actions

- **Listed `Chess_Project`.** It held the assignment PDF and `game-build-prompt.md.pdf`.
- **Matched the four unique screenshots to their themes** by dominant colour: green → jungle, teal → ocean, warm → desert, pale blue-grey → winter.
- **Saved** `jungle.png`, `ocean.png`, `desert.png` and `winter.png` into `Chess_Project`.

### Claude

> The four moodboard photos are now in your Chess_Project folder as `jungle.png`, `ocean.png`, `desert.png` and `winter.png`. Each one is named after the theme it's used for.
>
> When you set up the repo, move them into `docs/reference/moodboards/`, because that's where the prompt tells the AI to look. I can move them now if you'd like.

---

## Turn 5: Add-on prompt for the scene redesign

### Uli

*Attachment: a new rainforest Pinterest board showing:*
- *dense layered greens, ferns and fan palms*
- *buttress-root trunks and mossy trees*
- *waterfalls and streams*
- *wooden boardwalks and bridges*
- *light shafts*
- *a snake and a leopard*

> Please make this into a prompt I can add into the Claude code chat I already have that has finished everything else from the old prompt. This is an add on, I need it to make these changes and push it to the repo
>
> Ok so a few issues, firstly, the project was built and is now on my github through this link:
> [Uli-Stechler/three-classics: Connect Four, Checkers and Backgammon against the computer: a Spring Boot referee, React + SVG boards, Docker Compose and Kubernetes.](https://github.com/Uli-Stechler/three-classics)
> So that's good. There's a few design issues I need to work on. Firstly, the main page looks a bit like a haunted deserted mountain town, i need like a rainforest/jungle, here are new photos for the main page. Secondly, I need a few things done to the background of each of the games (also to the preview pictures of the main page) to clarify, I like the style so far, the things that need to be changed that I'm about to add shouldn't make it look like ai slop, keep it simple and cute. For the backgrounds of all the games, there needs to be some small things added in. Some examples (but feel free to add others) winter scene: maybe a white fox or wolf, a frozen lake, the trees (that are already there) need to have snow on them. The water scene: needs fish and seahorse, maybe treasure chest. Desert scene: needs cactuses, cow or bull skull, or tumbleweed. The main thing is to make the scenes feel a little more full. Obviously if any of these things could have simple animations that go with the object, please put it in. For the rainforest scene on the main page, make sure to include some animals, maybe pandas parrots snakes monkeys, anything that fits the vibe. Remember to please make sure that none of this becomes AI slop, keep it simple and cute. If an idea I made doesn't look good, just let me know and we can decide to remove it. Also feel free to add in anything else that will go with the scenes/backgrounds

### Actions

- **Cloned the public repo** `Uli-Stechler/three-classics` (read-only, into a scratch folder). Claude reviewed:
  - the commit history
  - `CLAUDE.md`
  - the frontend structure
  - the four scene components (`JungleScene`, `OceanScene`, `WinterScene`, `DesertScene`)
  - the shared `SceneFrame`, including its pause rule and the scrim's `saturate(0.55)` filter
  - the home-card thumbnail mode
  - the Playwright screenshot spec
- **Viewed the committed screenshots:**
  - `home-1440x900`
  - `checkers-1440x900`
  - `connect-four-1920x1080`
  - `backgammon-1440x900`
- **Diagnosed why the home page reads as a "haunted mountain town":**
  - the open sky and far ridges look like mountains
  - the grey haze and near-black silhouettes feel gloomy
  - the bamboo stalks look like poles
- **Found that the game pages leave little background visible.** On backgammon at 1440×900, the left gutter is only about 100 px. Props therefore have to go in the visible zones.
- **Wrote `scene-refresh-prompt.md`** (Appendix B). It covers:
  - the style rules
  - the visible-zone placement rules
  - the jungle rebuild with a new palette and animals
  - additions for the ocean, winter and desert scenes
  - the home thumbnails
  - the motion rules
  - verification, the commit plan and push instructions
- **Saved the new moodboard** as `jungle-rainforest.png` in `Chess_Project`. The prompt says not to commit it to the public repo.

### Claude

> I wrote the add-on prompt: scene-refresh-prompt.md. Paste it into your existing Claude Code chat and attach the new rainforest photo. I also saved that photo in your Chess_Project folder as `jungle-rainforest.png`, and the prompt points there as a backup. The prompt ends with Claude Code committing each scene, running the tests, refreshing the screenshots and pushing to main.
>
> Before writing it, I looked at your repo's scene code and screenshots, so the prompt names the real files and fixes the real problems:
>
> - **The "haunted mountain town" look:** it comes from the open sky and far ridges (they read as mountains), grey haze with near-black plant silhouettes, and bamboo stalks that look like telephone poles. The prompt replaces all of that with a closed canopy, misty tree trunks, warm light shafts, a small waterfall and two-tone green leaves.
> - **Most of each game's background is hidden** behind the board and side panel, especially in backgammon, where only about 100px shows on the left. The prompt makes Claude Code put the animals and objects where you can see them. A new test checks they aren't hidden.
> - **Washed-out colours:** the game pages currently fade the whole background so it doesn't compete with the board, which would make new animals look muddy. The prompt allows a lighter fade for them, as long as the board still stands out most.
>
> What goes in each scene:
>
> - **Home (rainforest):** a toucan or parrot, a monkey, a sloth and a snake, with butterflies or a frog as optional extras. At most five animals, kept away from the cards and title.
> - **Ocean:** a treasure chest, a seahorse, a fish school, a sandy floor and coral.
> - **Winter:** snow on every tree tier, a frozen lake and an arctic fox. A far-off wolf and a cozy cabin are optional.
> - **Desert:** saguaro cacti, a cow skull, a tumbleweed that rolls when the wind gusts, and a distant mesa.
>
> The home-page cards use the same scenes, so the fox, seahorse and cactus show up there too.
>
> Ideas I changed:
>
> - **No panda.** Pandas live in cool mountain bamboo forests, not rainforests, so it would look out of place. If you still want one, it can sit on a bamboo cluster.
> - **Desert is American Southwest only**, so no pyramids or camels mixed in with the saguaros.
> - **Generic fish**, not clownfish, so nothing looks like Nemo.
>
> Claude Code will also report anything that doesn't look good once drawn, so you can make the final call.

---

## Turn 6: Export this session

### Uli

> I need a transcript of this cowork session. Please compile our entire conversation history, including the major actions you took and the code/files you processed into a single, well-formatted markdown (.md) or text file so I can download it.

### Actions

- **Compiled this file** from the session and appended the two generated prompts in full (Appendices A and B).
- **Saved a copy** to `Chess_Project`.

---

## Appendix A: `game-build-prompt.md` (final version, after the Turn 2 edits)

The main build brief handed to Claude Code, which built the repo. It is reproduced verbatim.

````markdown
# Build Brief: Three Classics (Connect Four, Checkers, Backgammon)

You're building my submission for the **Chess.com Product Engineering Internship take-home**. It's a small full-stack web app with a home page and three games you play against the computer: **Connect Four, Checkers and Backgammon**. Reviewers will clone the repo, run one command and play. They'll also read the code, the docs and this conversation, so write code and commit messages you'd want a senior engineer to see.

Read this whole brief and every file in `docs/reference/` before writing any code.

---

## 0. How to work

1. **Plan first.** Start by restating the plan as milestones and listing any open questions. Wait for my approval before writing code.
2. **Build one game at a time, end to end.** Rules, tests, AI, UI and result modal for one game must be done before the next one starts. If time runs short, two polished games beat three broken ones.
3. **Don't stop for small decisions.** Pick the sensible option and log it in `docs/DECISIONS.md`. Ask me only before you:
   - change the stack
   - add a dependency that isn't on the approved list (Section 9)
   - cut a core feature
4. **Prove it works.** Never call a milestone done until its tests pass and you've taken screenshots and looked at them (Section 11).
5. **Commit small and often.** Write commit messages in the imperative mood, and commit at least once per milestone.
6. **Write a project `CLAUDE.md` in Milestone 1.** Keep it to 40 lines or fewer and cover the stack, commands, folder layout, the layout rules from Section 7.3 and the anti-slop list from Section 7.6. Then later sessions keep the conventions.
7. **The rules in Section 5 win over the PDFs.** If the reference rulebooks disagree with this brief, follow this brief. It corrects known problems in those files.

### Reference files (`docs/reference/`)

| File | What it is | Notes |
|---|---|---|
| `ChessCom_Product_Engineering_Internship_Take_Home_Assignment.pdf` | The assignment | Hard requirements. See Section 1. |
| `checkers-rules.pdf` | One-page American checkers rules | Good source. Section 5.2 adds the edge cases it leaves out. |
| `backgammon_Official-Rules.pdf` | Rulebook for a 12-game set | **Backgammon is pages 1–4 only.** Ignore the chess, dominoes, poker and sudoku pages. Its single-game scoring rule is nonstandard, so use Section 5.3. |
| `A5640_en-in_connect-4-game.pdf` | Hasbro kids' playbook | **Has no win rules.** It only shows the 7×6 grid and 21 discs per colour. Use Section 5.1. |
| `moodboards/jungle.png`, `ocean.png`, `desert.png`, `winter.png` | Pinterest boards | **Mood and palette reference only.** Never copy, trace, hotlink or ship these images. See Section 7. |

### Online rule sources

Use these to cross-check. Section 5 is still the final word.

| Source | Use for | Notes |
|---|---|---|
| [Boardgammon — Backgammon Rules](https://boardgammon.com/backgammon/rules/) | **Primary backgammon source.** It's more complete than the PDF: max-dice rule, closeout, bear-off edge cases, doubling cube. | Its bar-entry line ("a 1 enters on the opponent's 24-point") contradicts its own numbering. Use Section 5.3: a die of *n* enters on the **opponent's n-point**, which is the mover's point `25 − n`. Ignore the physical and tournament procedure (cups, cocked dice, clocks, money-play side rules). |
| [wikiHow — How to Play Connect 4](https://www.wikihow.com/Play-Connect-4) | Connect Four basics | Cross-checked against [officialgamerules.org](https://officialgamerules.org/game-rules/connect-4/) and [Wikipedia](https://en.wikipedia.org/wiki/Connect_Four). Section 5.1 combines them. |

---

## 1. Hard requirements (from the assignment)

- **Stack:**
  - Infrastructure: Docker + Kubernetes
  - Database: PostgreSQL
  - Backend: Java 21+ with Spring Boot
  - Frontend: React + TypeScript (Vite), HTML, CSS
- **One-command local run:** `docker compose up --build`, then open `http://localhost:8080`.
- **Kubernetes manifests** in `k8s/` that run the same stack on a local cluster (kind or Docker Desktop).
- **Submission docs:**
  - `README.md`: how to run, how to test, architecture overview
  - `docs/DECISIONS.md`: main decisions, trade-offs, what I'd do next
  - `docs/TIME_LOG.md`: time per milestone
  - `docs/agent-transcripts/`: I'll export the transcripts there at the end
- **The reviewers' stated value:** "a working, well-built core game over covering every feature." Correctness and feel come first. Extras come last.
- **Time budget:** the assignment expects about 4 hours of my time. Keep the scope disciplined, and present the three games as one shared engine design (Section 4), not three separate apps.

---

## 2. Priorities, in order

1. **Rules are 100% correct.** Every legal move is allowed and every illegal move is impossible. Wins and draws are detected correctly.
2. **Playable layout.** The board is centered, fully visible without scrolling and easy to read. Cells and pieces never change size. The background never competes with the board.
3. **Clean, restrained design.** Each page gets one theme, a calm palette and simple controls.
4. **Natural motion.** Pieces move smoothly, and each theme has slow ambient animation (leaves, water, sand, snow).
5. **Everything else.**

---

## 3. Product scope

### 3.1 Pages and themes

| Route | Page | Theme | Ambient motion |
|---|---|---|---|
| `/` | Home | **Jungle** | Fronds sway, mist drifts, light shafts |
| `/connect-four` | Connect Four | **Ocean** | Light rays shift, bubbles rise, slow surface shimmer |
| `/checkers` | Checkers | **Winter** | Layered snowfall with gentle wind |
| `/backgammon` | Backgammon | **Desert** | Wind-blown sand grains, haze drifts over the dunes |

Why each theme fits:

- **Connect Four → Ocean.** The classic frame is blue, and discs dropping read like falling through water.
- **Backgammon → Desert.** It's one of the oldest board games, and its warm wood and sandstone palette suits the board.
- **Checkers → Winter.** A crisp two-tone board reads naturally as ice and slate. Snowfall is the calmest motion for a game that needs focus.
- **Home → Jungle.** It's the lush "hub" you step out of into the other three worlds.

### 3.2 Home page

- Project name as a small, confident wordmark (working name: **"Three Classics"**), plus a one-line subtitle. No marketing copy.
- Three tall portrait cards, like windows into each game's world. Each card shows:
  - a static thumbnail of that game's theme scene, rendered by the same scene component in thumbnail mode
  - the game name and a one-line description
  - a **Play** button, or **Continue** if an unfinished game exists
  - a small record line, e.g. `W 4 · L 2 · D 1`
- Hovering a card starts its scene's ambient motion and lifts it slightly. Use transform only, so layout never shifts.
- Keyboard: Tab moves between cards, Enter opens a game.

### 3.3 Features every game must have

- **Mode:** vs Computer (default) or 2 Players on one device. Two-player mode is nearly free with a server-side engine and makes manual testing easy.
- **Difficulty:** Easy / Medium / Hard, chosen before the game and shown during it. Changing it mid-game asks "Restart on Hard?"
- **Side or order:**
  - Connect Four: Go first / Go second
  - Checkers: Play Dark (moves first) / Play Light. The board flips so the human is always at the bottom.
  - Backgammon: the opening roll decides who starts
- **Turn indicator** and a **"Computer is thinking…"** state. Show the AI move after a minimum delay of about 500 ms so it feels natural, even if it was computed instantly.
- **Legal-move highlighting:** select a piece to see where it can go. Clicking an illegal target does nothing (a subtle shake is fine). It never errors.
- **Last-move highlight**, so the player can see what the computer just did.
- **Restart** button. If a game is in progress, confirm first: "Restart this game?" → Restart / Cancel.
- **Home** button.
- **Rules** button that opens a side drawer with a short how-to-play for that game. Write it in your own words from Section 5. Don't paste rulebook text.
- **Result modal** when the game ends:
  - Title: **You win** / **Computer wins** / **Draw**. In 2-player mode: **Dark wins**, **Player 1 wins**, etc.
  - Subtitle with the reason, e.g. "Four in a row (diagonal)", "No legal moves left", "Gammon (2 points)", "Draw by 40-move rule"
  - Buttons: **Play again** (primary, same settings), an inline **Easy / Medium / Hard** switch so you can play again at a new level, and **Home**
  - Enter = Play again. Esc closes the modal so you can study the final board, and a "Show result" pill reopens it.
  - First let the player see the ending: highlight the winning line or final move for about 800 ms, then open the modal.
- **Resume after refresh:** store the active game id per game type in `localStorage`, wrapped in try/catch. On load, fetch that game. If it's missing or finished, offer a new game.
- **Settings** (small gear menu):
  - Ambient motion on/off. Default is on, but off when `prefers-reduced-motion` is set.
  - Sound on/off, if sound is built (stretch goal)

---

## 4. Architecture

### 4.1 Principle: the server is the referee

All rules and all AI live in the Java backend. The frontend **never** decides legality:

- It renders the state and the server-provided `legalMoves`.
- It sends the chosen move back.
- The server validates and applies the move, runs the AI and returns the result.

This keeps one source of truth, makes the rules unit-testable, and mirrors how a real game site prevents cheating. Note it in DECISIONS.md.

**Multi-step moves.** For checkers multi-jumps and backgammon turns, the server returns each legal move as a complete **path or sequence of steps**. The frontend lets the user build a move click by click, accepting only steps that follow a legal prefix, then submits the completed move. The rules logic stays in one place, and the UI is still instant and supports in-turn undo.

### 4.2 Backend layout (Java 21+, Spring Boot)

```
backend/src/main/java/.../
  api/            REST controllers, request/response DTOs, error handler (ProblemDetail)
  core/           Shared abstractions:
                    GameEngine<S, M>  initialState, legalMoves, apply, outcome
                    AiPlayer<S, M>    chooseMove(state, difficulty, rng, timeBudget)
                    Outcome, Difficulty, Side, IllegalMoveException
  connectfour/    Engine + AI (bitboard search is fine inside the AI)
  checkers/       Engine + AI
  backgammon/     Engine + AI + dice
  service/        GameService: load → validate → apply → AI reply → persist (one transaction)
  persistence/    JPA entities, repositories
resources/db/migration/   Flyway migrations
backend/src/test/java/... Rules tests per game, AI tests, API tests
```

- **Engines are pure.** Use immutable state (records) and `apply()` returns a new state. Inject `Random` (seeded in tests). Never use `Math.random()` or `new Random()` inside engine logic.
- **Dice are rolled on the server** with `SecureRandom` in production and seeded `Random` in tests.
- **Optimistic locking:** each game has a `version`. A move request carries the version it was based on, and the server returns 409 on mismatch so the client can refetch. This stops double-clicks from applying twice.
- **AI time budget:** Hard must answer in about 1 s and never more than 2 s. Use iterative deepening with a deadline.

### 4.3 API (JSON)

```
POST /api/games                 { type, mode, difficulty, humanSide }  → GameView
GET  /api/games/{id}                                                   → GameView
POST /api/games/{id}/moves      { version, move }                      → { game: GameView, events: Event[] }
GET  /api/stats                                                        → per game type × difficulty: wins, losses, draws
GET  /actuator/health
```

- **Request fields:**
  - `type`: `CONNECT_FOUR | CHECKERS | BACKGAMMON`
  - `mode`: `VS_AI | LOCAL`
  - `difficulty`: `EASY | MEDIUM | HARD`
- **`GameView`** contains: id, type, mode, difficulty, version, status, sideToMove, a per-game `state` object, `legalMoves`, `outcome`, `lastMove`.
- **`events`** is the ordered list of what happened in this request, e.g. human move → AI roll → AI steps. The frontend animates them in order, then snaps to the final `game` state, which is the source of truth.
- **Backgammon rolls** are a move: `{ kind: "ROLL" }`. Keep one move endpoint.
- **Dev/test profile only:** `POST /api/dev/games` accepts a custom position, so tests can start near a win and check the modal. It must not be exposed in the default profile.

### 4.4 Database (PostgreSQL + Flyway)

- `games`: id (uuid), type, mode, difficulty, human_side, status, winner, result_reason, points, state (jsonb), version, created_at, updated_at
- `moves`: id, game_id, ply, actor (HUMAN/AI/P1/P2), move (jsonb), created_at
- Stats are computed from finished `games`. There are no accounts; this is a single local player.

### 4.5 Frontend layout (React + TypeScript + Vite)

```
frontend/src/
  api/            Typed client + DTO types
  app/            Router, layout shell, settings context
  components/     Shared UI only: Button, SegmentedControl, Modal, Drawer, TopBar, ConfirmDialog, Toast
  games/
    connect-four/ Board (SVG), hooks, game page
    checkers/
    backgammon/
  themes/
    tokens.css    Spacing, type, motion tokens
    jungle/ ocean/ desert/ winter/   Palette vars + Scene component (SVG layers + one canvas)
  pages/Home.tsx
```

Use plain CSS with CSS variables and CSS Modules. No component kits.

---

## 5. Rules (authoritative)

### 5.1 Connect Four

1. **Board:** a vertical grid, 7 columns × 6 rows. Each player has 21 discs.
2. **Turns:** players alternate. Each turn, drop one disc into any column that isn't full. It falls to the lowest empty slot.
   - The player picks a column, never a cell. A full column can't be chosen.
   - A disc never moves or comes out once dropped.
3. **Win:** the first player with **four in a row** wins immediately. The line can be horizontal, vertical, or either diagonal.
   - A line of **five or more counts as a win**.
   - If one drop completes two lines at once, highlight every winning disc.
4. **Draw:** all 42 slots are filled with no four in a row.
5. **Who goes first:** the rules don't fix a colour to start.
   - In the first game, the human chooses Go first or Go second. The first player always uses coral.
   - **Play again alternates the starting player** by default, as the rules suggest for fairness. The pre-game choice can override this.
6. **Board orientation:** column 0 is on the left.
7. **Solved-game note, for the AI and DECISIONS.md:** with perfect play the first player wins by opening in the center column. Columns next to the center draw, and outer columns lose.
   - Hard should therefore open in the center when it moves first.
   - Easy and Medium should vary their openings.

### 5.2 Checkers (American checkers / English draughts)

1. **Board:** 8×8. Play only on the 32 dark squares. Each player's **bottom-left corner square is a dark, playable square**.
2. **Setup:** 12 pieces each on the dark squares of the three rows nearest each player. The two middle rows start empty. **Dark moves first.**
3. **Men** move one square diagonally **forward** onto an empty dark square.
4. **Kings** move one square diagonally forward **or backward**.
   - **No flying kings.** This is not international draughts.
5. **Captures:**
   - **Jumping:** a piece jumps diagonally over an adjacent opponent piece into the empty square right beyond it, and the jumped piece is removed. Men capture forward only; kings capture both directions.
   - **Mandatory:** captures are **mandatory**. If any capture is available, only capture moves are legal.
   - **Free choice:** if several captures are available, the player **may choose any of them**. There is no requirement to take the longest sequence or the most pieces.
   - **Multi-jumps:** once a piece starts jumping, it **must keep jumping** with that same piece while further jumps exist. It may not stop partway, and it can't jump the same piece twice.
6. **Crowning:**
   - A man reaching the far row becomes a king. Show it as a stacked, double-height piece with a small crown mark, so it isn't colour-only.
   - **If a man is crowned during a jump sequence, the move ends there.** It can't continue jumping as a king that turn.
7. **Loss:** a player loses if they have **no pieces** or **no legal move** on their turn.
8. **Draws** (needed for vs-computer play):
   - **40-move rule:** 40 moves by each side (80 plies) with no capture and no man move (only kings moving).
   - **Threefold repetition:** the same position with the same side to move appears three times.

### 5.3 Backgammon

**Numbering:** points are numbered 1–24 from the side to move's perspective. Their home board is points 1–6 and they move from 24 toward 1. On screen, the human's home board is **bottom-right**. Human checkers travel counter-clockwise: from the top-right, across the top, down the left, and along the bottom into home. The bar runs down the middle, and the bear-off tray is on the right.

1. **Setup** (each player, own perspective): 2 checkers on the 24-point, 5 on 13, 3 on 8, 5 on 6. That's 15 each.
2. **Opening roll:**
   - Each side rolls one die, and ties re-roll.
   - The higher die moves first, **using both of those dice as its first roll**.
   - After that, players alternate rolling two dice.
3. **Moving:**
   - **Separate moves:** each die is a separate move. One checker can use both dice (as long as the intermediate landing point is open), or two checkers can use one die each.
   - **Doubles:** play the number **four times**.
4. **Landing:**
   - **Allowed:** a checker may land on an empty point, a point it already holds, or a point with exactly **one** opposing checker (a blot).
   - **Hitting:** landing on a blot **hits** it, and the blot goes to the bar.
   - **Blocked:** a point with 2+ opposing checkers can't be landed on, but it can be jumped over.
5. **Using the roll** (where most implementations go wrong):
   - **Maximum dice:** the player **must use as many dice as legally possible**. That means both dice, or up to four for doubles.
   - **Larger die:** if only one die can be used and either could be, **the larger die must be used**.
   - **No legal moves:** the turn is forfeited. Show a short toast like "No legal moves — turn passes."
   - **Implementation:** generate every complete step sequence by DFS over both die orders. Keep only the sequences that use the maximum number of dice. If that maximum is 1 and both dice are individually playable, keep only the larger-die moves. Deduplicate by resulting position.
6. **The bar:**
   - A player with checkers on the bar **must enter all of them before moving any other checker**.
   - A checker enters on point `25 − die`, i.e. in the opponent's home board, which is the mover's points 19–24.
   - If the entry points are blocked for every usable die, the turn is over.
   - **Closeout:** if all six points of the opponent's home board are made, the player can't enter at all. They keep passing, without needing to roll, until a point opens. Show "Closed out — turn passes."
7. **Bearing off:**
   - **When:** only when **all 15** of the player's checkers are in their home board, with none on the bar.
   - **Exact die:** a die of `d` bears off a checker from point `d`.
   - **No checker on point `d`:** if a checker sits on a higher point, the player must use the die to move a checker from a higher point. If no checker is on any higher point, they must bear off from the **highest occupied** point.
   - **Moving instead:** the player may move a checker inside the home board instead of bearing off, as long as the maximum-dice rule is still met.
   - **Hit while bearing off:** the checker must re-enter and travel back home before bearing off resumes.
8. **Winning:** the first player to bear off all 15 wins. Use **standard scoring** and show it in the modal:
   - **Single game, 1 point:** the loser has borne off at least one checker.
   - **Gammon, 2 points:** the loser has borne off no checkers.
   - **Backgammon, 3 points:** the loser has borne off none **and** still has a checker on the bar or in the winner's home board.
   - The PDF's alternative "count the loser's remaining checkers" rule is **not** used. Log this in DECISIONS.md.
9. **Doubling cube:** **not in v1.** It's a stretch goal (Section 10). Log it in DECISIONS.md as a deliberate cut.
   - We play single games, not matches, so ignore the Crawford rule.
   - Also ignore the money-play side rules: Jacoby, beavers, raccoons and automatic doubles.
10. **Ending a turn:** in physical play, a move stands once you pick up your dice. Here, the **Confirm** button is that moment. Before Confirm, Undo is free. After Confirm, the turn is final.

Backgammon UI specifics:

- **Rolling:** a **Roll** button for the human. Dice tumble in 3D for about 600 ms. Show both dice, and dim each one as it's used. Doubles show four pips or a ×2 badge.
- **Moving checkers:**
  - Click a checker (or the bar) to see its destinations. Click a destination to move.
  - Show **Undo** (within the turn) and **Confirm**. Confirm turns on only when a complete legal turn has been built.
  - Optional: double-click a checker to play the larger usable die automatically.
- **Counts and stacks:**
  - Show each side's **pip count**.
  - If a stack holds more than 5 checkers, overlap them or show a count badge on the 5th. The stack must never overflow its point.
- **Bear-off tray** shows the count, e.g. `7 / 15`.

---

## 6. Computer opponent

Shared rules for every AI:

- It only ever plays moves taken from the engine's `legalMoves`.
- Its randomness comes from the injected `Random`.
- Hard respects the time budget.

Difficulty targets: **Easy**, a beginner wins most games. **Medium**, a casual player wins sometimes. **Hard**, plays solid and rarely blunders.

| | Easy | Medium | Hard |
|---|---|---|---|
| **Connect Four** | Mostly random. Takes an immediate win or block only ~50% of the time. | Minimax depth 4–5 with a window-scoring heuristic and a center preference | Negamax with alpha-beta, bitboards, center-first move ordering, transposition table, iterative deepening (~10–12 ply in budget) |
| **Checkers** | Random legal move (forced captures still apply) | Minimax depth 4. Eval: material (man 100, king ~160) + advancement | Alpha-beta + iterative deepening (~8–10 ply). Eval: material, advancement, back-row guard, center control, mobility, endgame king activity |
| **Backgammon** | Random legal turn | Greedy one-ply: best resulting position by heuristic (pip count, blots exposed to direct shots, points made, primes, home-board strength, checkers on bar) | Heuristic eval + 2-ply expectimax over all 21 opponent rolls, with race vs contact phase detection |

If the moves are close in value, Medium may pick randomly among near-equal moves so games vary.

---

## 7. Visual design

### 7.1 Direction

The goal is **quiet and atmospheric, not flashy**. Think of a calm nature documentary title card, not a gaming landing page.

- **The board is the hero.** The scene is a backdrop.
- **Scenes are built in code, not photos:**
  - layered SVG silhouettes with atmospheric perspective (farther layers are lighter, hazier and less saturated)
  - CSS gradients
  - one lightweight `<canvas>` per page for particles
- **Photo slot:** structure each scene so a licensed photo could later be dropped in as the bottom layer, under the same scrim, via one CSS variable.

### 7.2 Theme palettes

These are pulled from the moodboards. Treat them as starting points and check contrast.

| Theme | Background range | Surface / board | Accents |
|---|---|---|---|
| **Jungle** (home) | mist `#D7E4D9` → canopy `#4C7A55` → deep `#0F2619` | card surface `#F4F1E8` at 92% | bird-of-paradise orange `#E8742C` (sparingly), lagoon teal `#2F8F86` |
| **Ocean** (Connect Four) | surface `#5CC8D0` → teal `#138A9E` → deep `#0B4F63` → abyss `#04202F` | frame `#0D3B4F`, empty holes `#062A38` with inner shadow | discs: coral `#FF7A5C`, sand-gold `#F2C14E`, each with an embossed inner ring |
| **Desert** (Backgammon) | dusk lavender `#B9A3C9` → peach `#F3C49A` → dunes `#E9B98A` / `#D98A52` / `#B5562A` | frame walnut `#5A2E1B`, field sand `#E8CFA4`, points alternating terracotta `#C4562A` / umber `#8A5A3B` | checkers: ivory `#F6EFE2`, obsidian `#2B1A12`; dice ivory with dark pips |
| **Winter** (Checkers) | blue hour `#1F2F4D` → `#5F7FA8` → horizon `#C9D8EA` → snow `#EEF3F8` | light squares `#E6EEF6`, dark squares `#3B5573`, frame `#22324A` | pieces: Dark = charcoal-navy `#151D2B` with frosted rim, Light = snow `#FBFDFF` with ice edge `#A9C6E2`; warm lamp accent `#F2C48D` for highlights |

**Typography:**

- **Fraunces** for display text (page titles and modal titles only). **IBM Plex Sans** for all UI text.
- Self-host both via `@fontsource`, since the app must work offline in Docker.
- Use tabular numbers for counts.

### 7.3 Layout rules for boards (non-negotiable)

1. **Each board is a single `<svg>` with a fixed `viewBox`.**
   - Cells, points and pieces are drawn in that fixed coordinate system, so their size can never depend on content.
   - The SVG is scaled as a whole by its container.
   - Pieces render at a fixed fraction of the cell (e.g. radius = 40% of cell size), whether or not neighbours exist.
2. **Board sizing:**
   - The board container has a fixed `aspect-ratio`: 1/1 for checkers, 7/6 plus frame for Connect Four, about 4/3 for backgammon.
   - Size it to the largest box that fits the viewport minus the top bar, side panel and gutters, capped at about 760 px tall.
   - It is **horizontally and vertically centered** in its column.
3. **Nothing reflows during play.** Selection, hover and highlight states use SVG rings, fills, transforms or opacity, never borders or padding that change geometry.
   - Status text, the dice area and the "thinking…" line **reserve their height** up front, so messages appearing never push the board.
   - The board only resizes when the window resizes.
4. **Screen structure:**
   - A top bar (about 56 px): Home on the left, game name in the center, difficulty, Restart, Rules and Settings on the right.
   - The board, centered.
   - On screens ≥ 1100 px wide, a 280 px side panel: turn, status, captured pieces or pip counts, and move list. Below that width, the panel moves under the board.
5. **The background stays a backdrop.** Behind the board area, apply a soft radial scrim that darkens and desaturates the scene by about 40–60%.
   - The board sits on a solid surface with **one** soft shadow.
   - All text meets WCAG AA contrast.
6. **Minimum targets:** the board is fully visible with no scrolling at **1280×720**, and still playable at **390×844** (phone portrait).

### 7.4 Ambient scenes

**Jungle (home)**, back to front:
1. A misty gradient sky with far karst ridges, hazy and low-contrast.
2. One or two mist bands drifting horizontally on 60–90 s loops.
3. Soft light shafts whose opacity breathes over 8–12 s.
4. Foreground fronds (monstera, palm, bamboo) framing the edges. They sway ±1.5–3° from the stem (`transform-origin` at the base) over 6–10 s, staggered.
5. A rare falling leaf: at most 3 on screen at once, one every 6–10 s.

**Ocean (Connect Four):**
1. A depth gradient.
2. 4–6 soft god rays from the surface, swaying slowly over 12–20 s.
3. A faint surface shimmer at the top.
4. 15–30 small bubbles rising with a slight wobble.
5. Reef silhouettes at the bottom edges.
6. Optional: a faint manta silhouette gliding across about once a minute.

**Desert (Backgammon):**
1. A dusk sky with a low, pale sun.
2. Three dune layers with atmospheric perspective.
3. Fine sand grains blowing horizontally, sparse, with an occasional 2 s gust.
4. Very slow haze drift.

**Winter (Checkers):**
1. A blue-hour sky.
2. Hazy snow-capped ridges and snow-laden pine silhouettes.
3. Snowfall in **three depth layers** (far: tiny and slow; near: larger, faster and slightly soft), with sinusoidal drift and slowly changing wind. About 120–200 flakes total, scaled down on small screens.
4. Optional: a barely-there aurora band at 10–15% opacity.

**Performance and accessibility:**
- One `requestAnimationFrame` loop per page, aware of devicePixelRatio.
- Pause when the tab is hidden, and cut particle counts on small screens.
- With `prefers-reduced-motion` or the setting turned off, show a static scene.
- Target 60 fps on a normal laptop.

### 7.5 Piece motion

- **Motion tokens:**
  - `--dur-fast: 150ms` (hovers)
  - `--dur-base: 250ms` (UI)
  - `--dur-piece: 300–400ms` (moves)
  - easing `cubic-bezier(.2,.8,.2,1)`
- **Connect Four:**
  - A preview disc hovers above the column under the cursor.
  - Dropped discs fall with gravity (ease-in) and land with one small settle bounce.
  - The winning four get a slow pulse ring.
- **Checkers:**
  - The selected piece lifts (scale 1.06 and a deeper shadow, transform only).
  - Moves slide diagonally. Multi-jumps animate hop by hop.
  - Captured pieces shrink and fade.
  - Crowning stacks the second disc with a tiny frost sparkle.
- **Backgammon:**
  - Dice tumble.
  - Checkers slide to their point. A hit checker slides to the bar. Bearing off slides into the tray.
  - Legal destination points glow softly.
- **Result modal:**
  - It fades and scales from 0.98 to 1 over 200 ms while the backdrop dims.
  - One understated themed flourish: a brief snow flurry, a rise of bubbles, or a sand gust. Nothing more.
- With reduced motion on, piece moves become quick crossfades.

### 7.6 Anti-slop rules

**Don't use:**
- purple or indigo "AI gradients"
- gradient text
- neon glows
- glassmorphism everywhere
- emoji as icons
- sparkle icons
- confetti cannons
- rounded-everything with heavy drop shadows
- bouncing UI chrome
- marketing hero sections
- fake stats or placeholder or lorem text
- "Powered by AI" badges
- more than one font family besides the display face
- more than two accent colours per theme

**Do:**
- generous whitespace on an 8 px spacing scale
- short plain copy ("New game", "Your turn", "You win")
- one icon set (Lucide) at one stroke weight
- visible focus rings
- colour plus shape for state, never colour alone
- consistent 8–12 px radii

No Chess.com or Hasbro logos or branding. The game is called "Connect Four" in plain text only.

---

## 8. Accessibility and input

- Every interactive cell or point is a focusable element with an `aria-label`, e.g. "Column 4, 3 discs" or "Point 13, 5 light checkers".
- **Connect Four:** ←/→ choose a column and Enter drops a disc.
- **Checkers and Backgammon:** Tab or arrow keys move focus, and Enter selects or moves. Mouse-first is fine, but keyboard must work.
- The modal traps focus and returns it to the board when closed.

---

## 9. Dependencies

Approved dependencies, by area. Ask before adding anything else.

- **Backend:**
  - `spring-boot-starter-web`, `-data-jpa`, `-validation`, `-actuator`
  - `flyway-core` (+ postgres module), `postgresql`
  - `spring-boot-starter-test`, Testcontainers (Postgres) for integration tests
  - Maven or Gradle **wrapper** (pick one), so no local install is needed
- **Frontend:**
  - `react`, `react-dom`, `react-router-dom`, `typescript`, `vite`
  - `lucide-react`, `@fontsource/fraunces`, `@fontsource/ibm-plex-sans`
  - `vitest`, `@testing-library/react`, `@playwright/test`
  - Optional: `motion` (Framer Motion), only if it clearly simplifies piece animation
- **Not allowed:**
  - Tailwind or other CSS frameworks
  - component kits (MUI, Chakra, shadcn)
  - state libraries (Redux etc.)
  - game or chess engines from npm or Maven. All rules and AI must be our own code.

---

## 10. Out of scope / stretch goals

Do stretch goals only after everything else is done, and in this order:

1. **Hint button:** ask the Hard AI for a suggested move and highlight it.
2. **Takeback:** undo your last move plus the computer's reply. The move history makes this easy.
3. **Subtle sound effects:** disc clack, dice roll, piece slide. Off by default.
4. **Backgammon doubling cube**, following Boardgammon's basic rules:
   - **Offering:** either player may offer the first double, but only **before rolling** on their own turn.
   - **Take:** stakes double and the taker owns the cube. Only the owner may redouble later.
   - **Pass:** the game ends and the player who passed loses the current cube value.
   - **Limits:** the cube is capped at 64. No beavers, automatic doubles or Jacoby rule.
   - **Scoring:** gammons and backgammons multiply the cube value.
   - **AI:** it offers, takes and passes using eval thresholds.
   - **Display:** show the cube centered (value 1) or on its owner's side.

**Out of scope:** accounts, login, online multiplayer, WebSockets, chess, leaderboards.

---

## 11. Testing and verification

### 11.1 Backend (JUnit), required per game

**Connect Four:**
- wins horizontally, vertically and on both diagonals, including at the edges
- a line of five counts as a win
- a double line is fully highlighted
- Play again alternates the starting player
- a full column is rejected
- a full board is a draw
- the game stops accepting moves after a win

**Checkers:**
- setup has 12 per side on dark squares, and Dark moves first
- men can't move backward
- kings move both ways and don't fly
- forced capture removes all non-captures from `legalMoves`
- multi-jump must continue
- the player may pick a shorter capture over a longer one
- **a man crowned mid-jump ends the move**
- no legal moves loses
- the 40-move draw and threefold repetition work

**Backgammon:**
- setup has 15 per side on 24/13/8/6, and each starting pip count is **167**
- bar entry for a die of *n* lands on the opponent's n-point
- a closeout passes the turn
- the opening roll uses both opening dice
- 2+ checkers block a point
- hitting sends a blot to the bar
- the bar must be cleared first
- blocked entry forfeits the turn
- doubles give four moves
- **the max-dice rule**
- **the larger-die rule when only one die can be played**
- no bear-off unless all 15 are home
- bearing off with a higher die from the highest point
- having to move from a higher point instead of bearing off
- a hit during bear-off stops bearing off
- single, gammon and backgammon scoring

**Invariants (soak test):**
- 500 AI-vs-AI games per type (Easy vs Easy, seeded) run to completion with no exceptions.
- Checkers piece counts never increase.
- Backgammon always has 15 per side across board + bar + off.
- Every AI move is in `legalMoves`.

**AI:**
- Hard Connect Four always takes an immediate win, and always blocks when the opponent has exactly one immediate winning threat.
- Hard responds within the 2 s cap on sample positions.

**API:**
- an illegal move → 400 with a clear ProblemDetail
- a stale version → 409
- the dev endpoint isn't exposed in the default profile

### 11.2 Frontend

**Vitest:**
- the legal-prefix matcher for multi-step moves
- the board coordinate mapping, including the checkers flip and backgammon orientation

**Playwright e2e:**
- Home → each game → make a move → the AI responds → Restart → Home
- Start from a near-win dev position, win, see the modal, click Play again, and confirm a fresh board

**Playwright layout test** for each game at **1280×720, 1440×900, 1920×1080 and 390×844**:
- the board is fully inside the viewport, with no scrollbars
- the board is centered in its column within 2 px
- **all cells or points have identical size (±0.5 px)**
- **cell rects are unchanged before and after a move and after the AI reply**
- nothing overlaps the board

### 11.3 Visual review (you must do this)

After each game milestone and at the end:
- Take Playwright screenshots of every page at the four viewports, plus the result modal.
- **Open the screenshots and look at them.**
- Fix anything misaligned, cramped, low-contrast or busier than this brief allows before moving on.
- Save the final set to `docs/screenshots/` and use a few in the README.

---

## 12. Infrastructure

**Dockerfiles:**
- **Backend:** multi-stage. Build with the wrapper, then run on `eclipse-temurin` JRE as a non-root user.
- **Frontend:** multi-stage. Node builds the app, then `nginx:alpine` serves it and proxies `/api` to the backend.

**`docker-compose.yml`:**
- three services: `db` (postgres:16-alpine with a named volume), `backend` and `frontend`
- healthchecks on all three
- `depends_on: condition: service_healthy`
- the app at `http://localhost:8080`
- defaults that work with zero config, with credentials in `.env.example`

**`k8s/` (Kustomize):**
- namespace
- Postgres StatefulSet + PVC + Secret
- backend Deployment + Service, with readiness/liveness probes on `/actuator/health`
- frontend Deployment + Service
- ConfigMap
- a `kustomization.yaml`

The README shows the kind flow: create the cluster, build the images, `kind load`, `kubectl apply -k k8s/`, then port-forward. A `make k8s-up` / `make k8s-down` helper is welcome.

**Makefile:** `make up`, `make down`, `make test`, `make e2e`, `make k8s-up`.

---

## 13. Docs to write

**`README.md`:**
- a one-paragraph pitch and a screenshot strip
- **Quick start: `docker compose up --build`**
- running the tests
- Kubernetes
- an architecture section: a Mermaid diagram of browser → nginx → Spring Boot → Postgres, plus the "server is the referee" explanation
- the project layout
- how the AI works, briefly per game

**`docs/DECISIONS.md`:**
- the key decisions and why:
  - server-authoritative engine
  - legal-move paths with prefix matching
  - SVG boards
  - procedural scenes instead of photos
  - three games via a shared `GameEngine` interface
- trade-offs, including no doubling cube, standard scoring versus the PDF, and no accounts
- what I'd do next with more time

**`docs/TIME_LOG.md`:** a table of milestones with start and end times taken from commit timestamps. I'll correct it.

---

## 14. Milestones

1. **Skeleton.**
   - Repo structure, `CLAUDE.md`, Docker Compose with all three services healthy, a `/api/games` stub persisting to Postgres, and the React shell with routing and the theme token system.
   - **Gate:** `docker compose up --build` works from a clean clone.
2. **Connect Four, complete.**
   - Engine, tests, AI ×3, ocean scene, board, all shared features from Section 3.3, and the result modal.
   - This sets the pattern for the other two games.
   - **Gate:** all Section 11 tests for Connect Four pass and the screenshots have been reviewed.
3. **Checkers, complete.** Same gate.
4. **Backgammon, complete.** Same gate. This is the hardest rules engine, so get the rule tests green before polishing the UI.
5. **Home page.** Jungle scene, game cards with thumbnails, stats and Continue.
6. **Polish pass.**
   - Motion review, reduced-motion check, keyboard pass, contrast check.
   - A full screenshot review, then fixes.
7. **Kubernetes + docs.** Manifests tested on kind, plus the README, DECISIONS and TIME_LOG.
8. **Stretch goals** (Section 10), only if time remains.

### Definition of done (whole project)

- [ ] `docker compose up --build` on a clean machine → all three games playable at `localhost:8080`
- [ ] All backend tests, Vitest and Playwright (e2e + layout) pass
- [ ] Every game: correct rules, three difficulty levels, result modal with Play again / Home, Restart, Rules drawer, resume after refresh
- [ ] Boards centered, fully visible at 1280×720, cells and pieces fixed-size, verified by the layout test
- [ ] Ambient scenes on all four pages, paused on hidden tabs, off with reduced motion
- [ ] `kubectl apply -k k8s/` works on kind
- [ ] README, DECISIONS and TIME_LOG are written. Remind me to export the agent transcripts into `docs/agent-transcripts/`.

````

---

## Appendix B: `scene-refresh-prompt.md`

The add-on prompt for the existing Claude Code chat: the rainforest home page and fuller game scenes, then commit and push. It is reproduced verbatim.

````markdown
# Add-on: Scene refresh (rainforest home + fuller game backdrops)

Everything from the original brief is done and pushed to `origin/main`. This is a **design-only pass on the four ambient scenes.**

- **Don't change:** game logic, the API, the boards, the side panel, or the layout rules in `CLAUDE.md`.
- **Work in:** `frontend/src/themes/**`, plus `pages/Home.*` only if the thumbnails need it.

**Attached:** a new moodboard for the home page, a Pinterest rainforest board. If you can't see the attachment, it's at `C:\Users\ustec\Downloads\Chess_Project\jungle-rainforest.png`.

- Use it for mood and palette only. **Don't commit it.** The repo is public and the image isn't ours.
- The other three moodboards still apply: ocean, winter and desert.

## How to work

1. **Look first.** Open `docs/screenshots/` and read the four `*Scene.tsx` files before changing anything.
2. **Build, then report back.** You don't need my approval for each item.
   - If something I suggested looks bad once it's drawn, cut it or swap it, and tell me why in your final report. I'd rather lose an item than keep a bad one.
   - Put anything you're unsure about in its own commit so it's easy to remove.
3. **Commit, test, push.**
   - Commit once per scene.
   - Run every test.
   - Refresh the screenshots.
   - Push to `origin main`.

## 1. Style rules for everything you add: simple and cute, not AI slop

- **Match the current flat look.**
  - Use flat SVG shapes and soft rounded forms.
  - Give each object 2–4 tones taken from (or mixed from) its scene palette.
  - Don't add outlines, textures, new gradients, glows or drop shadows. The one exception is a flat, soft ground-shadow ellipse under objects.
- **Cute means simple.**
  - Draw rounded, slightly chunky shapes with tiny dark dot eyes (one optional 1 px highlight).
  - Don't draw mouths, teeth, eyelashes, expressions, clothing or animals standing on two legs.
  - Nothing should resemble a known character, so no Nemo or Dory fish and no Disney or Pokémon lookalikes.
- **Keep things small and tucked in.**
  - Hero animals should be about 6–12% of the viewport height.
  - Tuck them partly behind leaves, rocks, kelp or snowbanks, so they feel discovered rather than placed.
  - Far objects are lighter and hazier (atmospheric perspective), like the existing layers.
- **Fuller, not busy.**
  - On a game page, add at most about 5 new elements.
  - Small pops of colour from animals or flowers should cover no more than about 5% of the picture.
  - When in doubt, cut.
- **No text, signs or logos in any scene.**
- **The board stays the hero.** On game pages no prop may be brighter, more saturated or higher-contrast than the board and its pieces.
  - The scrim currently desaturates the whole scene to `saturate(0.55)`. If that makes the props muddy, give props their own layer in `SceneFrame` with a lighter filter (around 0.75–0.85). Check that the board still wins.

## 2. Where props go: visible zones only

On game pages, the board and the side panel (≥1100 px) cover most of the scene, so a prop behind them is wasted.

- **Placement zones:** put hero props in the zones that stay visible:
  - the left gutter
  - the bottom strip
  - the gap between the board and the panel
  - the band under the top bar
- **Positioning:** use viewport-relative positions with `clamp()` sizes.
- **Backgammon:** its gutters are the narrowest (about 100 px at 1440×900). Favour tall, narrow props on the left edge and small props along the bottom.
- **Mobile:** below 600 px wide, hide the crossers and the secondary props, and keep one hero prop per scene.
- **Tagging and testing:** mark each hero prop `data-prop="hero"`. Add a Playwright check that every hero prop is at least 50% visible, not covered by the board or the panel, at 1440×900 and 1920×1080.

## 3. Home: rebuild the jungle as a rainforest

**What's wrong now:** the page reads like "a haunted, deserted mountain town" because of three things.

- **Mountains:** the open sky and far ridges look like mountains.
- **Gloom:** the grey, desaturated haze and near-black foreground silhouettes feel gloomy.
- **Poles:** the bamboo stalks look like fence posts or telephone poles.

**Rebuild it as a lush, enclosed rainforest**, like the new moodboard.

- **No mountains or open sky.** The top is a canopy with small gaps of light.
- **New palette** (a starting point; check AA contrast for the title and subtitle):

  | Role | Colour |
  |---|---|
  | Canopy shade | `#0E2E1C` |
  | Emerald | `#1B5A34` |
  | Leaf | `#2F7D3E` |
  | Fresh leaf | `#6FAE45` |
  | Sunlit lime | `#A9CF5E` |
  | Moss | `#6B8E3A` |
  | Trunk | `#4A3B2C` / `#2F271E` |
  | Mist | `#D6E8CF` |
  | Warm light | `#F4EFC4` |
  | Water | `#A7D8CC` |
  | Heliconia red (tiny amounts) | `#C8323C` |
  | Toucan orange | the existing `#E8742C` |

- **Layers, back to front:**
  1. Canopy band across the top: layered leaf masses in dark-to-mid greens, with a few bright gaps.
  2. Far forest: tall, misty trunks fading into pale green haze. This is depth, not ridges.
  3. Light shafts: keep the existing ones, recoloured warm golden-green at 10–18% opacity.
  4. Waterfall: a slim, 2–3 tier waterfall on one side of the mid-ground, falling into a small pool or stream at the bottom.
     - The water flows using a moving dash pattern on transforms, not a filter.
     - Soft mist sits at its base.
  5. Mid trunks: 3–5 bigger trunks with buttress roots and moss patches.
     - Lianas or vines hang from the canopy and sway gently.
  6. Foreground frame: ferns, monstera, fan palms and a heliconia or two.
     - Use two-tone greens (a light side and a shade side) in place of black silhouettes.
     - Keep the current sway.
  7. Optional: a small wooden boardwalk or rope bridge in the mid-distance, as on the moodboard.
- **Keep:** the mist bands and the rare falling leaves, recoloured to match.
- **Animals:** up to 5, around the edges and corners. Keep them clear of the cards and the title.
  - **Toucan or green parrot**
    - Sits on a branch.
    - Tilts its head and blinks every 8–12 s.
    - About once a minute, a parrot flies across the canopy band in a short 4–6 s flight with simple wing flaps.
  - **Monkey**
    - Hangs by its tail from a vine.
    - Slow pendulum swing (±4° over 5–6 s).
    - Looks around now and then.
  - **Sloth**
    - Hangs from a low branch.
    - Nearly still: a slow blink every ~10 s and a very slow head turn every ~30 s.
  - **Snake (green tree python)**
    - Draped over a branch near a lower corner.
    - Slow "breathing" ripple along its body.
    - A quick tongue flick every ~12 s.
  - **Optional extras:** 2–3 blue morpho butterflies on lazy paths near the water, or a small frog on a leaf by the pool whose throat puffs now and then.
  - **No panda.** Pandas live in cool mountain bamboo forests, not rainforests. Mixing them in makes the scene feel random. I can add one on a bamboo cluster later if I want it.

## 4. Connect Four: ocean

- **Seabed:** a lighter sandy floor along the bottom, muted by depth, with a few shells and a starfish in front of the existing reef rocks.
- **Treasure chest** (hero, bottom-left)
  - Half-buried in sand with its lid slightly open.
  - The gold rim glints softly (an opacity pulse every ~6 s).
  - A few bubbles escape from the lid every 8–10 s.
- **Seahorse** (hero, by the left kelp)
  - Its tail is curled around a kelp stem.
  - It bobs ±6 px over about 4 s, with a quick fin flutter.
- **Fish school:** 5–7 small fish in a loose formation cross the mid-water every 25–35 s on a gentle sine path. They enter and leave through the visible margins.
- **Reef fish:** 1–2 near the reef swim lazily back and forth with soft turnarounds. Use generic yellow, teal and coral colours, not clownfish stripes.
- **Coral and anemone:** a couple of simple branching corals in muted coral pink, plus an anemone whose tentacles sway.
- **Optional:** one translucent jellyfish drifting slowly upward with a gentle bell pulse.
- **Keep** the manta, light rays and bubbles.

## 5. Checkers: winter

- **Snow on the trees** (required)
  - Every pine tier, near and far, gets a white snow cap along its top edge, with a slight blue shadow underneath.
  - Add one or two bigger, heavily snowed pines in the left gutter.
  - Every 20–30 s, a little clump of snow slides off a branch with a soft puff.
- **Frozen lake**
  - A pale ice-blue lake in the mid-ground, visible from the left gutter or the bottom strip.
  - It faintly reflects the trees, has a few hairline cracks, and a slow light glint sweeps across it every ~12 s.
- **Arctic fox** (hero)
  - White with soft blue-grey shading, sitting curled on a snowbank by the lake.
  - Every 6–8 s its tail swishes or an ear twitches, and it blinks slowly.
  - About once a minute it looks up at the falling snow.
  - A line of small paw prints leads to it.
- **Optional:**
  - A small, far-off grey wolf on a ridge that lifts its head now and then.
  - A small log cabin among the far pines with one warm window (`#F2C48D`, a very subtle flicker) and a thin chimney-smoke curl.
- **Keep** the snowfall and the faint aurora.
- **No penguins or polar bears.** It's the wrong biome.

## 6. Backgammon: desert (American Southwest)

Keep the biome consistent: saguaros, mesas, skulls and tumbleweed. No pyramids or camels.

- **Saguaro cacti**
  - One tall saguaro with raised arms in the left-gutter foreground (hero).
  - One or two smaller, hazier ones on the far dunes.
- **Prickly pear:** a cluster near the bottom with one small pink flower.
- **Cow skull** (hero)
  - Bleached and half-sunk in the sand near the big saguaro.
  - Static.
- **Tumbleweed**
  - Rolls along the bottom strip every 25–40 s, spinning, with small bounces, and leaves off-screen.
  - Trigger it with the existing sand gust, so the wind and the tumbleweed feel connected.
- **Mesa:** a distant mesa or butte silhouette on the horizon, hazy and static (the Monument Valley look from the moodboard).
- **Optional:**
  - A small lizard on a rock that does a quick head-bob every ~15 s.
  - A small hawk drifting in a slow, wide circle high in the sky.

## 7. Home card thumbnails

The cards render each scene with `mode="thumbnail"`, so the new props carry over. Make sure each portrait crop clearly shows its signature props:

| Card | Signature props |
|---|---|
| Ocean | Seahorse + treasure chest |
| Winter | Snowy pines + fox by the lake |
| Desert | Saguaro + skull (the tumbleweed rolls through on hover) |

- If needed, reposition props for thumbnails with `[data-mode='thumbnail']` CSS.
- Keep the props clear of the piece emblem at the bottom centre of each card.
- Props animate only while the card is hovered, which is the existing behaviour.

## 8. Motion rules

- **How to animate:**
  - Use CSS keyframes on `transform` and `opacity` only, with `transform-box: fill-box` and a per-part `transform-origin`.
  - The existing particle canvas is fine to reuse.
  - No new canvases, no per-prop JavaScript animation loops, and no animated SVG filters.
- **Mostly idle:**
  - Each animal is at rest most of the time, and its action takes no more than about 15% of its loop.
  - Use loop lengths that don't line up (e.g. 7.3 s, 11.7 s, 13.9 s) so animations never sync.
- **Crossers are rare:** the fish school, parrot flight and tumbleweed run once every 25–60 s, one at a time per scene.
- **Soft easing:** no cartoon squash-and-stretch, except a tiny tumbleweed bounce.
- **Pausing:**
  - The existing rule `.scene:not([data-animate='true']) * { animation-play-state: paused }` must still freeze everything for reduced motion, the Ambient toggle and non-hovered thumbnails.
  - The paused frame must look natural: animals in their rest pose and crossers off-screen.
- **Performance:** with motion on, keep about 60 fps at 1920×1080 and don't add noticeable CPU.

## 9. Verify before pushing

1. All backend, Vitest and Playwright tests pass, including the layout test and the new hero-visibility check.
2. Refresh the screenshots: `SCREENSHOTS=1 npx playwright test screenshots`. Also capture the home page at 1440×900 with each card hovered.
3. **Open every screenshot and check:**
   - Does the home page read instantly as a bright, lush rainforest, and not mountains or gloom?
   - Is each hero prop visible at 1280×720, 1440×900 and 1920×1080?
   - Is anything muddy, cluttered, too bright, or crowding the title, cards or board?
   - Fix it and re-shoot until the answer is clean.

## 10. Docs, commit, push

- **`docs/DECISIONS.md`:** add a short "Scene refresh" note: what was added, anything cut or swapped and why, and the biome-consistency choice.
- **`docs/TIME_LOG.md`:** add a row for this pass.
- **`CLAUDE.md`:** add 2–3 lines on the prop style rules (Section 1) and the motion rules (Section 8).
- **Commits**, one per scene:
  - "Rebuild home scene as a rainforest with animals"
  - "Add sea life and a treasure chest to the ocean scene"
  - "Add a frozen lake, arctic fox and snow-laden pines to the winter scene"
  - "Add cacti, skull and tumbleweed to the desert scene"
  - "Refresh screenshots"
- **Push:** `git push origin main`.

## 11. Report back to me

- **Per scene:** what you added and which animations, with screenshot paths.
- **Cut or swapped:** anything you cut or swapped and why, plus anything you think looks off and want my call on.
- **Confirmation:** that the tests passed and the push succeeded, with the final commit hash.

````

*End of transcript.*
