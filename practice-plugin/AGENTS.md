# Practice Plugin Agent Guide

Rules for agents and contributors working inside `practice-plugin/`. The
repository-root `AGENTS.md` also applies; this file takes precedence where the
rules overlap.

## Keep Documentation Synchronized

`ARCHITECTURE.md`, `CODE-MAP.md`, and `README.md` (~47 KB combined) are part of
the implementation contract, not optional background reading.

- Before changing plugin behavior, package responsibilities, runtime flows,
  persistence, generated files, exercise structure, dependencies, build
  configuration, or test strategy, inspect headings (`^#{1,3} `) and read the
  relevant sections (`view_range`) of `CODE-MAP.md` and `ARCHITECTURE.md`—plus
  relevant sections of `README.md` when user-facing workflows or extension
  summaries are affected—rather than loading all three files end-to-end.
- Update `ARCHITECTURE.md` in the same change when boundaries, ownership, data
  flow, execution flow, storage, lifecycle, or security assumptions change.
- Update `CODE-MAP.md` in the same change when packages, important classes,
  extension procedures, build commands, code conventions, or validation
  requirements change.
- Update `README.md` in the same change when user workflows, UI controls,
  execution or storage behavior, plugin dependencies, build/install commands,
  or contributor extension summaries change. Avoid hardcoding total exercise
  counts in `README.md`; point to `src/main/resources/exercises/index.txt`.
- Update `AGENTS.md` (here and at the repo root) and
  `../.github/instructions/practice-plugin-content.instructions.md` in the same
  change when build tasks, scripts, dependency management, or authoring rules
  change.
- Update every affected document when a change spans design, implementation
  guidance, and user-facing documentation. Do not leave known
  future-documentation work for a later change.
- Keep documentation durable: describe responsibilities and invariants rather
  than copying implementation details that can be discovered directly from one
  method.

## Scope

These rules apply to plugin source, tests, build configuration, the React pages
under `web/`, and exercise content under `src/main/resources/exercises/`.

The Problem and Study Guide tabs are React + Mantine pages in `web/`, rendered
in JCEF with a Swing HTML fallback. Keep `web/src/types.ts` and
`ui/PageJson.java` in sync, and see the "Web pages" section of `CODE-MAP.md`.

For plugin code work, scope to the relevant packages (`CODE-MAP.md`) rather than
loading all ~820 KB (~210k tokens) of plugin source and tests at once. Root
`.ignore` already excludes exercise/reference/known-wrong content and build
caches from ripgrep `grep` by default (while keeping `exercises/index.txt`
visible and allowing explicit-path reads/greps on named exercise IDs). File
`glob` tools and IDE MCP `search_*` calls do not read `.ignore`: scope `paths`
to `src/main/java/**`, `src/test/java/**`, `src/verificationRunner/**`, or
`web/src/**` (prefixed with `practice-plugin/` when working from the repo root)
so ~2,200 exercise files and Gradle/Node caches do not flood results. Pass
whichever project root is open in IntelliJ (repo root or `practice-plugin/`) as
the IDE MCP `projectPath`.

## Build And Test

Run from `practice-plugin/` with the repository wrapper:

```bash
python3 scripts/catalog_tool.py validate   # <1s static check of all exercises and tracks
./gradlew test -Pfast                      # fast unit suite (excludes @Tag("catalog") and @Tag("gradle-integration"))
./gradlew test
./gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest" -PcatalogOnly=id1,id2
./gradlew runLocalIde      # sandbox IDE on newest locally installed IntelliJ build
./gradlew runIde           # sandbox IDE on pinned target platform
```

`-PcatalogOnly` limits the slow per-exercise `@Tag("catalog")` subprocess checks
to the listed exercise IDs; the full catalog takes several minutes (with
parallelized reference/wrong/runner checks and sequential complexity probes).
Use `scripts/catalog_tool.py validate` and `./gradlew test -Pfast` for fast
iteration, then run `./gradlew check` before finishing a code change (delegate
full `./gradlew test` / `./gradlew check` runs to a `task` subagent or pass
`--quiet` to keep test logs out of main context). Shared plugin dependency
versions live in `../gradle/libs.versions.toml`.

## Code Coverage

Every set of changes must keep or raise coverage. Before finishing a code
change, run the full suite with the coverage gate:

```bash
./gradlew check
# Where a known flaky test stops `check` (for example, a complexity probe):
./gradlew test jacocoTestCoverageVerification --continue
```

- On Windows, enable Developer Mode so the symlink-safety tests can create
  symlinks; without it they fail and coverage drops below the gate.
- Add tests for every new or changed line and branch in the gated packages
  (everything except `ui`). Filter `build/reports/jacoco/test/jacocoTestReport.xml`
  (or inspect only the changed class's HTML page) to confirm changed classes
  have no new uncovered lines or branches; never read the full XML report into
  context.
- The minimums in `build.gradle` are a ratchet. Raise them when coverage goes
  up. Never lower them, and never add a coverage exclusion to get a change
  through. An exclusion is only for code that cannot run in unit tests (it
  needs a live IDE, JCEF, or OS privileges); record why in `CODE-MAP.md`.
- `ui` is excluded from the gate but still needs tests for its pure logic,
  such as `PageJson`, `PageEvent`, `StudyGuideView`, and `MarkdownHtml`.
- Never report coverage from a partial run as the result; `-PcatalogOnly` and
  `--tests` runs under-report.

## Java And Test Conventions

- Plugin development uses JDK 25. Generated learner code and verification use
  Java 17 (`--release 17`).
- Use JUnit Jupiter. Do not add JUnit 4 assertions or Hamcrest.
- The runtime-only JUnit 4 dependency in `build.gradle` is permitted only for
  IntelliJ fixture bootstrap compatibility.
- Use AssertJ for every authored assertion, including generated
  `ExampleRunner.java` files. Do not use hand-written `AssertionError` checks.
- Prefer value-specific AssertJ assertions and attach `.as(...)` context when
  the failing input or iteration would otherwise be unclear.
- Keep test classes and test methods package-private.

## Content Rules

Authoring invariants for exercises (`src/main/resources/exercises/<id>/`,
`src/test/resources/{reference,known-wrong}/<id>/`), study guides
(`src/main/resources/guides/<id>.json`), and practice tracks
(`src/main/resources/tracks/<id>.json`) live in
`../.github/instructions/practice-plugin-content.instructions.md` and the
"Adding ..." sections of `CODE-MAP.md`. Read them before adding or modifying
catalog content.

## Repository Hygiene

- Keep shared run configurations in `.run/`; do not commit `.idea/` state or
  machine-specific IDE paths.
- Configure a local IDE with `idePath` in a local Gradle properties file; never
  commit a real installation path.
- Do not check dependency jars into source control or hard-code generated jar
  names. Change the `exampleLibraries` or `verificationLibraries` dependency in
  `build.gradle`.
- Preserve storage schemas, marker formats, and generated path contracts unless
  the change includes an explicit migration and corresponding documentation.
