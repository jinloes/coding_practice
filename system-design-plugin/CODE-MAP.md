# System Design Practice Code Map

Package root: `src/main/java/com/jinloes/system_design_plugin/`.

## Packages

| Package | Key classes |
|---|---|
| `catalog` | `DesignCatalog` (problem IDs and order, rubric `DIMENSIONS`, `VERDICTS`, `HINT_COUNT`, manifest validation, notes template) |
| `platform` | `OwnedDirectory` (symlink-safe create, write, and delete under an owned root) |
| `workspace` | `DesignWorkspace` (attempts, `ownerOfDiagram`, `isAttemptsPath`, fingerprint and staleness, feedback history) |
| `state` | `DesignProgress` (persistent selections, hints, verdicts, interviewer settings) |
| `interview` | `DiagramSummary`, `InterviewPrompt`, `InterviewFeedback` (schema, parsing, stored form), `CopilotInterviewer` (CLI process), `CopilotModels` |
| `web` | `WebAssets` (private origin and URL mapping), `PageEvent` (page and sequenced editor messages), `PageState` (tool window JSON and review history), `ToolWindowView` (builds `PageState.View` from attempts, progress, and feedback history), `MarkdownHtml` |
| `ui` | `DesignToolWindowFactory`, `DesignController` (events and rendering), `BrowserPage` and `WebAssetHandler` (JCEF page and request filter), `DiagramEditorProvider` and `DiagramEditor`, `InterviewerSettingsDialog`, `IdeTheme` |

Resources: `src/main/resources/problems/<id>.json`, `notes-template.md`, and
`META-INF/plugin.xml`.

## Web pages

`web/` is a Vite + React + Mantine project with two entry points:

- `index.html` / `src/main.tsx` / `src/App.tsx` — the tool window. `src/types.ts`
  mirrors `PageState` and `PageEvent`; change them together.
- `editor.html` / `src/editor/main.tsx` / `src/editor/saveTracker.ts` — the Excalidraw editor and its
  `designLoad`, `designFlush(seq)`, `designSaveResult(seq, error)`, and `designTheme` bridge.

The Gradle `webUi` task runs `npm run build` with Gradle-managed Node into
`build/web/`, which `processResources` copies to `/web` in the jar. Any new file
type the build emits must have its extension added to `WebAssets`, and file names
must match its safe-path pattern. For page-only work, run `npm run dev` in `web/`;
`src/sample.ts` provides sample state.

## Commands

Run from `system-design-plugin/`:

```bash
./gradlew test                     # unit tests (also builds the web pages)
./gradlew test -x webUi            # unit tests without the web build
./gradlew check                    # tests plus the coverage gate
./gradlew buildPlugin verifyPlugin
./gradlew runLocalIde              # sandbox IDE on newest locally installed IntelliJ build
./gradlew runIde                   # sandbox IDE on pinned target platform
./gradlew generateIdeRunConfig     # write .idea/runConfigurations/Run_Sandbox_IDE.xml (also runs on IDE sync)
```

Set `idePath` in a local, uncommitted Gradle properties file to build against a
local IDE installation instead of downloading one. `runLocalIde` and
`generateIdeRunConfig` live in `gradle/local-sandbox.gradle`. Forked IDE and
test JVMs set `winp.folder.preferred` to `build/winp` so `winp` never extracts
native helpers into Gradle's immutable platform transform cache on Windows.

## Coverage

`check` enforces 98% line and 92% branch coverage on everything except
`com/jinloes/system_design_plugin/ui/**`. `ui` is excluded because every class
there needs a live IDE, the EDT, or JCEF (browser pages, file editors, dialogs,
tool windows). Keep logic out of `ui`: anything testable belongs in `web`,
`interview`, `workspace`, or `state`. The minimums are a ratchet; never lower
them or add exclusions to land a change.

## Tests

Tests mirror the main packages under `src/test/java/`. `FakeCopilotCli` stands in
for the CLI; `CopilotInterviewerTest` checks the exact argument list and that no
allow flag is present. `PageStateTest` checks that expectations are never
serialized. Catalog tests derive expectations from the manifests rather than
restating problem content.

## Adding a problem

1. Choose a lowercase kebab-case id and add it to `IDS` in `DesignCatalog` at the
   position the tool window should list it.
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
