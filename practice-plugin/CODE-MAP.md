# Algorithm Practice Plugin Code Map

This document is the implementation map for the plugin. Update it in the same
change whenever packages, important classes, build commands, coding
conventions, extension procedures, or validation requirements change. System
boundaries and runtime flows belong in `ARCHITECTURE.md`.

## Project Layout

```text
practice-plugin/
  build.gradle
  settings.gradle
  gradle.properties
  src/main/java/com/jinloes/practice_plugin/
    app/
    catalog/
    platform/
    run/
    state/
    ui/
    workspace/
  src/main/resources/
    exercises/<id>/
    guides/<id>.json
    tracks/<id>.json
    gradle/verification-build.gradle
    harness/ComplexityProbe.java
    META-INF/plugin.xml
  src/test/java/com/jinloes/practice_plugin/
  src/test/resources/reference/<id>/Solution.java
  src/test/resources/known-wrong/<id>/Solution.java
  web/                      React + Mantine pages (Vite, TypeScript)
    src/types.ts            JSON contract mirrored by ui/PageJson.java
```

## Package Responsibilities

| Package | Responsibility | Important classes |
|---|---|---|
| `app` | Cross-package application use cases | `LegacyImportService` |
| `catalog` | Exercise, study guide, and practice track registries, manifest parsing, bundled example libraries | `ExerciseCatalog`, `GuideCatalog`, `TrackCatalog`, `ExampleLibraries` |
| `platform` | Safe filesystem ownership and OS differences | `OwnedDirectory`, `Os` |
| `run` | Run/debug/check lifecycle, process control, reports, complexity | `PracticeRunner`, `RunDecisions` (pure run messages/decisions), `RunSession`, `FullCheck`, `ExampleRunConfigurations`, `CheckResult`, `TestReports`, `ComplexityAnalysis` |
| `state` | IntelliJ persistent state | `ManagedPracticeProgress`, legacy `PracticeProgress` |
| `ui` | Tool window UI and rendering | `PracticePanel`, `ExerciseFilters`, `ResultsView` (result and run banners), `HintButton`, `PracticeActions`, `ExerciseCellRenderer`, `PageView` (`ReactPageView`/`HtmlPageView`), `PageJson`, `PageEvent`, `StudyGuideView`, `LimitsDialog`, `PracticeViews`, `BackgroundTasks`, `AttemptFiles`, `PracticeToolWindowFactory`, `MarkdownHtml` |
| `workspace` | Durable attempts, IDE modules, generated checks, legacy format | `ManagedPracticeWorkspace`, `PracticeModuleWorkspace`, `GeneratedDirectories`, `VerificationWorkspace`, `PracticeWorkspace` |

Dependencies should point toward focused lower-level services. Keep
cross-package orchestration in `app`, `run`, or `ui`; do not make filesystem
types depend on UI or persistent-state types.

## Build And Run

Run from `practice-plugin/`:

```bash
./gradlew test
./gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest"
./gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest" -PcatalogOnly=id1,id2
./gradlew check            # full suite plus the coverage gate
./gradlew buildPlugin
./gradlew runIde
```

The build is standalone and is deliberately not included by the repository
root's Gradle settings. Plugin compilation uses JDK 25. Learner and generated
verification compilation uses Java 17.

`ExerciseCatalogTest` compiles and runs every exercise (reference, starter,
known-wrong, and complexity probe), which takes several minutes for the full
catalog. `-PcatalogOnly` (passed to the test JVM as `practice.catalog.only`)
restricts those per-exercise checks to the listed IDs while authoring. The
complexity probe is timing-sensitive; run the final full suite on an otherwise
idle machine.

Set `idePath` in `practice-plugin/gradle.properties` or user-level Gradle
properties to use a local IDE. Never commit a real machine path.

### Web pages

`processResources` builds `web/` with a Gradle-downloaded Node (`npm ci`, then
`npm run build`), so no local Node is needed for the plugin build. To iterate on
the pages in a browser with sample data:

```bash
cd web && npm ci && npm run dev
```

- Change `web/src/types.ts` and `ui/PageJson.java` together, and update
  `PageJsonTest`.
- New page actions need a `PageEvent.Type`, a handler in
  `PracticePanel.onPageEvent`, and a `PageEventTest` case.
- Pages must work offline: Vite inlines all JS and CSS into one `index.html`,
  so do not add runtime CDN or font URLs.
- `HtmlPageView` is the fallback when JCEF is unavailable and in unit tests.
  Keep it showing the same information, without the interactivity.

## Coding Conventions

- Package names stay below `com.jinloes.practice_plugin`; generated learner
  sources use `com.jinloes.practice`.
- Prefer immutable records for snapshots and explicit service ownership for
  mutable lifecycle state.
- Preserve IntelliJ threading rules. Filesystem/process work belongs off the
  event dispatch thread; IDE model changes use the required write context.
- Dispose listeners, temporary run configurations, modules, watchdogs, and
  child processes with their owning IntelliJ service or UI component.
- Fail closed for ownership and path validation. If cleanup cannot prove that a
  path is plugin-owned and inactive, preserve it.
- Treat marker schemas, attempt IDs, resource names, persistent fields, and
  `ManagedPracticeWorkspace.SOLUTION_PATH` as compatibility contracts.
- Keep user-facing errors actionable, bounded, and suitable for display in the
  tool window.
- Keep each source and test file under about 20 KB so agent tooling can read it
  in one pass. Split by responsibility (for example, `FullCheck` out of
  `PracticeRunner`) rather than by arbitrary line ranges.

## Test Conventions

- Use JUnit Jupiter and AssertJ only for authored tests.
- Keep test classes and methods package-private.
- Exercise test templates (`ExamplesTest.java`, `CorrectnessTest.java`) use
  only plain `@Test`: counts are derived from those annotations and the
  verification build supplies the timeout. Plugin tests may use any JUnit
  Jupiter feature.
- Prefer behavior-level tests through public or package-visible seams.
- Add regression tests for lifecycle, cleanup, persistence, path safety, report
  parsing, and generated source behavior when changing those areas.
- The runtime-only JUnit 4 dependency exists only because the IntelliJ fixture
  bootstrap references legacy APIs.

### Coverage Gate

JaCoCo runs with every `test` task and writes
`build/reports/jacoco/test/html/index.html`. `jacocoTestCoverageVerification`
is part of `check`. It reads the last run's `build/jacoco/test.exec`, so it can
also run after test failures with `--continue`.

- **Gated:** every package except `ui`, measured on the instrumented classes
  (`instrumentCode`). The test IDE loads plugin classes with `PathClassLoader`,
  which is why the test task sets `includeNoLocationClasses`.
- **Minimums:** 98% line and 92% branch, a ratchet just below the 2026-10
  baseline of 98.2% line and 92.8% branch (Windows, Developer Mode on so the
  symlink tests run). Raise the minimums as coverage improves; never lower
  them. Without symlink support, the symlink-safety tests fail and coverage
  drops below the gate.
- **Known uncoverable misses (not excluded, just accepted):**
  - Branches that IntelliJ's `@NotNull` instrumentation adds, such as
    `getState()` returns and some anonymous IDE wrappers.
  - `Os.IS_WINDOWS` ternaries, which take only one side per OS.
  - JVM invariants such as a missing SHA-256 `MessageDigest`.
  - A few IDE race and launch-failure paths in `PracticeRunner` and `FullCheck`.
  New code must not add to this list without a reason.
- **Excluded, with reasons:**
  - `ui/**`: Swing and JCEF components need a live IDE or browser. Its pure
    logic is still tested directly (`PageJsonTest`, `PageEventTest`,
    `HtmlPageViewTest`, `StudyGuideViewTest`, `MarkdownHtmlTest`).
- To add an exclusion, change `coverageExclusions` in `build.gradle` and add
  the class and its reason to the list above.

## Adding An Exercise

1. Choose a stable lowercase kebab-case ID and append it to
   `src/main/resources/exercises/index.txt`, which lists exercises in display
   order. `ExerciseCatalogTest` checks that the index, the exercise directories,
   and the reference and known-wrong directories all agree.
2. Add `src/main/resources/exercises/<id>/exercise.json` with:
   - stable `id`, title, topic, and difficulty;
   - an original statement with `Contract` and `Examples`;
   - structured visible examples;
   - exactly three progressive hints;
   - exact `inputSyntax` and a matching `sampleInput`;
   - intended time and space labels compatible with `ComplexityAnalysis`.
3. Add the five shipped Java files:
   - `Solution.java`: learner API and throwing starter only;
   - `ExamplesTest.java`: one plain `@Test` per visible example;
   - `CorrectnessTest.java`: deterministic additional coverage;
   - `ExampleRunner.java`: visible-example and custom-input modes;
   - `Workload.java`: representative doubling workload;
   - the manifest already listed above.
4. Add the author-only reference implementation at
   `src/test/resources/reference/<id>/Solution.java`.
5. Add a compile-compatible known-wrong implementation at
   `src/test/resources/known-wrong/<id>/Solution.java`; it must fail at least
   one shipped test. `KnownWrongSolutions` loads it.
   `ExerciseCatalogTest` compiles and runs it through `CatalogSandbox`.
6. Add the exercise ID to every study guide whose technique it practices, and
   to any practice track whose list or commonly reported patterns it matches.
7. Update the exercise and difficulty counts in the README.
8. Run the catalog test, then the full plugin suite.

## Adding A Study Guide

1. Choose a stable lowercase kebab-case ID and append it to the ordered `IDS`
   list in `GuideCatalog`.
2. Add `src/main/resources/guides/<id>.json` with `id`, `title`, a one-line
   `summary`, a `lesson`, and an `exercises` array of existing exercise IDs.
   Order within a level follows the array; levels come from each exercise's
   difficulty, so do not repeat difficulty in the guide.
3. Write the lesson in the `MarkdownHtml` subset with the label lines
   `How it works`, `When to reach for it`, `Common patterns`, and `Pitfalls`,
   each followed by `- ` bullets. Teach the technique without solving a listed
   exercise.
4. Add the guide ID to the guide order in `GuideCatalogTest` and the README
   guide list, then run `GuideCatalogTest` and the full plugin suite. Guide
   membership is checked against the manifests, so editing a guide's
   `exercises` array needs no test change.

## Adding A Practice Track

1. Choose a stable lowercase kebab-case ID (for example `blind-75` or a company
   name) that does not collide with a guide ID, and append it to the ordered
   `IDS` list in `TrackCatalog`.
2. Add `src/main/resources/tracks/<id>.json` using the guide manifest schema:
   `id`, `title`, `summary`, `lesson`, and `exercises`. The summary must state
   that the list is not an official or verified question list; company tracks
   must also call themselves approximate.
3. Write the notes in the `MarkdownHtml` subset with the label lines
   `About this list`, `What to expect`, and `How to prepare`, in original
   words. Never copy third-party problem statements, scrape company-tag data,
   or claim specific questions.
4. Add any missing exercises first (see Adding An Exercise), then update
   `TrackCatalogTest` expectations and the README track list, and run
   `TrackCatalogTest` and the full plugin suite.

### Exercise Assertions

Generated tests and example runners use static AssertJ imports. Prefer
`containsExactly`, `isEqualTo`, `isSameAs`, `isNull`, `isEmpty`, and range
assertions over boolean comparisons. Add `.as(...)` context when values alone
do not identify the call or iteration.

When multiple outputs satisfy the contract, test validity rather than one
reference choice.

### Example Runner Contract

With no arguments, the runner asserts every visible example and prints:

```text
Examples passed: <count>
```

With arguments, it joins them into one input string, validates/parses one case,
and prints the learner result. It must not embed a reference implementation.
The `USAGE` constant must exactly match manifest `inputSyntax`, and parse
failures must print it.

AssertJ jars come from the `exampleLibraries` Gradle configuration. The build
packages them and creates an index; runtime code extracts that index rather than
hard-coding jar names.

### Complexity Workload Contract

Every `Workload.java` declares:

```java
static final String UNIT;
static final int[] SIZES;
static Object prepare(int size);
static void run(Object state);
static int units(int size);
```

`prepare` is outside the timed region and runs before every repetition. Rebuild
mutable input there. `run` should force representative work and may throw only
when the probe setup itself is invalid. `units` returns the number of logical
operations represented by one run so measurements can be normalized.

The probe is advisory and must not fail a correctness check.

Before measuring, the probe runs the smallest size until its timing settles
(at least 300 ms, at most 2 s), so JIT compilation and CPU clock ramp-up do not
inflate the first measurement. When writing a workload:

- keep the largest size's working set small enough to stay in CPU cache;
  crossing a cache boundary between sizes looks like superlinear growth;
- make the largest size's raw run take well over 50 µs, repeating the call
  inside `run` if a single call is too brief, so its timing is trusted;
- check each reference with several probe runs, not just one.

## Changing Execution

When modifying example or full-check execution, inspect these surfaces
together:

- `PracticeRunner` and `RunSession` for ownership and state transitions;
- `FullCheck` and `BoundedProcessHandler` for full-check launch, watchdog
  limits, output bounds, and result translation;
- `ExampleRunConfigurations` for the temporary example/input run
  configuration;
- `PracticeModuleWorkspace` for module, SDK, harness, compilation, and native
  run integration, with `GeneratedDirectories` for marked harness and output
  directories;
- `VerificationWorkspace` and `verification-build.gradle` for generated checks;
- `ManagedPracticeProgress` for persisted result semantics;
- `PracticePanel` for controls, status, and stale-result presentation;
- integration tests for cancellation, timeout, cleanup, and report freshness:
  `ExampleExecutionIntegrationTest` and `FullCheckIntegrationTest`, which share
  the fixture in `PracticeExecutionTestBase`.

Do not introduce a second independent run-state flag. The atomic `RunSession`
is the source of truth. `FullCheck` holds no guard state: `PracticeRunner`
claims the guard before starting it and releases it from the completion
callback.

## Changing Storage Or Cleanup

Storage changes require:

- compatibility analysis for existing attempts and persistent XML state;
- schema or revision handling rather than silent reinterpretation;
- path normalization and symlink/redirect tests;
- ownership-marker tests for every deletion path;
- updates to `ARCHITECTURE.md` and this guide.

Never broaden recursive cleanup without proving the target is beneath the
expected base, marked by the plugin, and inactive.

## Documentation Change Matrix

| Code change | Required documentation |
|---|---|
| Package/class responsibility or dependency direction | `ARCHITECTURE.md`, `CODE-MAP.md` |
| Runtime, storage, lifecycle, security, or data flow | `ARCHITECTURE.md` |
| Build commands, dependencies, conventions, or extension steps | `CODE-MAP.md` |
| User workflow or supported feature set | `README.md` |
| Contributor/agent invariant | `AGENTS.md` |

Documentation-only changes do not require tests unless a test explicitly
validates that documentation.
