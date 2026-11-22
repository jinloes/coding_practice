# System Design Practice Code Map

Package root: `src/main/java/com/jinloes/system_design_plugin/`.

## Packages

| Package | Key classes |
|---|---|
| `catalog` | `DesignCatalog` (problem order from `problems/index.txt`, `LEVELS`, rubric `DIMENSIONS`, `VERDICTS`, `HINT_COUNT`, manifest validation, notes template), `TrackCatalog` (domain tracks and problem-to-track index), `GuideCatalog` (concept and delivery guides) |
| `platform` | `OwnedDirectory` (symlink-safe create, write, and delete under an owned root) |
| `workspace` | `DesignWorkspace` (attempts, symlink-safe `delete`, `ownerOfDiagram`, `isAttemptsPath`, fingerprint and staleness, feedback history) |
| `state` | `DesignProgress` (persistent selections, custom attempt labels, creation timestamps, hints, target level, monotonic best verdicts, strongest-verdict per-problem dimension scores paired with `scoresVerdict`, change subscriptions, interviewer settings) |
| `interview` | `DiagramLabels` (spatial `T`-tool label attachment and arrow redirection), `DiagramSummary`, `InterviewPrompt` (level calibration and follow-up probe fencing), `InterviewFeedback` (schema, parsing, stored round/level/follow-up metadata), `CopilotInterviewer` (CLI process), `CopilotModels` |
| `web` | `WebAssets` (private origin and URL mapping), `PageEvent` (tool-window events and sequenced editor messages), `PageState` (tool-window JSON, tracks, guides, dimension readiness summary, and review history), `ToolWindowView` (builds `PageState.View` and `DimensionSummary`), `DesignSession` (testable off-EDT attempt, timestamp, backfill, follow-up lookup, and review-recording coordinator), `MarkdownHtml` |
| `ui` | `DesignToolWindowFactory`, `DesignController` (thin EDT/VFS/JCEF adapter over `DesignSession`), `BrowserPage` and `WebAssetHandler` (JCEF page and request filter), `DiagramEditorProvider` and `DiagramEditor`, `InterviewerSettingsDialog`, `IdeTheme` |

Resources: `src/main/resources/problems/index.txt`, `problems/<id>.json`,
`tracks/index.txt`, `tracks/<id>.json`, `guides/index.txt`, `guides/<id>.json`,
`notes-template.md`, and `META-INF/plugin.xml`.

## Web pages

`web/` is a Vite + React + Mantine project with two entry points:

- `index.html` / `src/main.tsx` / `src/App.tsx` (`ProblemView.tsx`,
  `FeedbackView.tsx`, `GuideView.tsx`, `InterviewTimer.tsx`,
  `ReadinessPanel.tsx`) — the tool window with readiness summary, collapsible
  per-dimension rubric readiness panel, `Surprise me` random problem picker,
  search/track/difficulty/status (`Needs retry`) filters, 12 concept guides with
  contextual problem links, ambiguous-prompt scoping mode, wall-clock 45-minute
  timer, level-calibrated 5-point pre-review checklist, attempt rename/delete,
  review-over-review score deltas, and interactive follow-up probe input.
  `src/types.ts` mirrors `PageState` and `PageEvent`; change them together.
- `editor.html` / `src/editor/main.tsx` / `src/editor/saveTracker.ts` /
  `src/editor/stencils.ts` — the Excalidraw editor with preloaded offline
  system-design stencils and its `designLoad`, `designFlush(seq)`,
  `designSaveResult(seq, error)`, and `designTheme` bridge.

The Gradle `webUi` task runs `npm run build` with Gradle-managed Node into
`build/web/`, which `processResources` copies to `/web` in the jar, and
`webTest` runs the Vitest frontend unit suite (`App.test.tsx`,
`InterviewTimer.test.tsx`, `ReadinessPanel.test.tsx`, `saveTracker.test.ts`). Any new file type the build emits must have its
extension added to `WebAssets`, and file names must match its safe-path pattern.
For page-only work, run `npm run dev` in `web/`; `src/sample.ts` provides sample
state.

## Commands

Run from `system-design-plugin/`:

```bash
./gradlew test                     # Java unit tests (also builds the web pages)
./gradlew test -x webUi            # Java unit tests without the web build
./gradlew webTest                  # Vitest frontend unit tests
./gradlew check                    # Java tests, Vitest suite, and coverage gate
./gradlew buildPlugin verifyPlugin
./gradlew runLocalIde              # sandbox IDE on newest locally installed IntelliJ build
./gradlew runIde                   # sandbox IDE on pinned target platform
./gradlew generateIdeRunConfig     # write .idea/runConfigurations/Run_Sandbox_IDE.xml (also runs on IDE sync)
```

Set `idePath` in a local, uncommitted Gradle properties file to build against a
local IDE installation instead of downloading one. `gradle.properties` enables
the Gradle configuration cache, build cache, parallel execution, and skips the
IntelliJ plugin self-update network check. `buildSearchableOptions` is disabled
because the plugin registers no IDE Settings configurables, `npmInstall` uses
`fastNpmInstall`, and `webUi` and `test` normalize path inputs so both cache
cleanly across `clean` builds. `runLocalIde` and `generateIdeRunConfig` live in
`gradle/local-sandbox.gradle`. Forked IDE and test JVMs set
`winp.folder.preferred` to `build/winp` so `winp` never extracts native helpers
into Gradle's immutable platform transform cache on Windows.

## Coverage

`check` enforces 100% line and 100% branch coverage on everything except
`com/jinloes/system_design_plugin/ui/**`. `ui` is excluded because every class
there needs a live IDE, the EDT, or JCEF (browser pages, file editors, dialogs,
tool windows). Keep logic out of `ui`: anything testable belongs in `web`,
`interview`, `workspace`, `state`, `catalog`, or `platform`. The minimums are a
ratchet; never lower them or add exclusions to land a change.

## Tests

Tests mirror the main packages under `src/test/java/`. `FakeCopilotCli` stands in
for the CLI; `CopilotInterviewerTest` checks the exact argument list and that no
allow flag is present. `PageStateTest` checks that expectations are never
serialized. Catalog tests derive expectations from the manifests rather than
restating problem content.

## Adding a problem

1. Choose a lowercase kebab-case id, add it to `src/main/resources/problems/index.txt`
   at the position the tool window should list it, and include it in the
   appropriate track manifest under `src/main/resources/tracks/<track>.json` and
   at least one concept guide's `relatedProblemIds` under
   `src/main/resources/guides/<guide>.json`.
2. Create `src/main/resources/problems/<id>.json`:

   ```json
   {
     "id": "<id>",
     "title": "…",
     "difficulty": "Easy | Medium | Hard",
     "summary": "One sentence.",
     "statement": "Markdown in the MarkdownHtml subset: requirements and scale.",
     "hints": ["…", "…", "…"],
     "expectations": ["at least five interviewer-only points"]
   }
   ```

3. Exactly three hints, from a nudge to a near-answer. At least five
   expectations, each a concrete point a strong answer covers.
4. Run `./gradlew test --tests "com.jinloes.system_design_plugin.catalog.*"`.
   Follow the content rules in `AGENTS.md`.
