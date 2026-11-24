# Coding Practice Architecture

## Overview

This repository is a multi-module Gradle project for Java platform, regex engine, cryptography, and distributed system design practice, plus two standalone IDE plugin builds: `practice-plugin` (algorithm and data structure exercises) and `system-design-plugin` (system design interview problems).

## Build System

- Build tool: Gradle 9.3.1 (`./gradlew` at repository root and in each plugin directory)
- Multi-module layout in root `settings.gradle` for Java 17/21 modules, plus `includeBuild` composite builds for `plugin-common`, `practice-plugin`, and `system-design-plugin`
- Shared plugin dependency and toolchain versions live in root `gradle/libs.versions.toml` and are imported by `plugin-common`, `practice-plugin`, and `system-design-plugin` (as well as root `build.gradle` for shared AssertJ/JaCoCo versions)
- Shared plugin sandbox launcher (`runLocalIde` and `generateIdeRunConfig`) lives in root `gradle/local-sandbox.gradle`, applied by both plugins with `sandboxProjectName` and `sandboxToolWindowId`
- Shared plugin Java utilities (`com.jinloes.plugin_common.copilot.CopilotModels` and `com.jinloes.plugin_common.platform.OwnedDirectory`) plus the Copilot SDK dependency live in `plugin-common/` with a 100% line/branch JaCoCo gate
- Spring dependency management plugin applies only to root `subprojects`, keeping plugin builds isolated
- Shared IntelliJ run configurations in `.run/` launch plugin sandboxes directly when the repository root is opened in IntelliJ IDEA (with JDK 25 as the Gradle JVM)

## Module Organization

Each topic is implemented as an independent Gradle submodule.

| Module Group | Modules | Responsibility |
|---|---|---|
| Platform and language features | `concurrency`, `reflection` | Java platform and API-focused practice |
| Regex engine work | `regex` | Java regex and PCRE2 integration via pcre4j |
| Security study guide | `cryptography` | Key management and cryptography study docs plus JDK-only reference implementations |
| System design study guide & labs | `system-design` | Pattern-first system design docs, worked case studies, Docker Compose CDC lab, and JDK-only reference implementations |

## Language Strategy

- Java 17 is the default baseline across modules.
- The `regex` module uses Java 21 due to its dependency and integration requirements.

## regex Integration Architecture

- `regex` integrates with PCRE2 through `pcre4j` (JNA backend).
- Native dependency is resolved from Homebrew (`libpcre2-8.dylib`) on macOS.
- Build/test flow includes a pre-test verification step (`checkPcre2`) to ensure PCRE2 availability.
- Matching logic in `PCRE2Engine.findMatches()` uses dual indexing:
  - Byte offsets for UTF-8 byte-slice extraction.
  - Char offsets for `pcre4j` match invocation.

## Naming And Packaging

- Group ID: `com.jinloes.coding_practice`
- Version: `1.0-SNAPSHOT`
- Java packages follow `com.jinloes.<module_name>`

## Algorithm Practice Plugin (`practice-plugin`)

`practice-plugin/` is an IntelliJ IDEA plugin that turns the exercises in this repository into a
guided practice loop inside the IDE: pick an exercise, get a scratch project with a stubbed
`Solution.java`, run the visible examples, then check against the hidden test suite.

### Composite build isolation

The plugin is an **isolated Gradle build** wired into root `settings.gradle` via
`includeBuild("practice-plugin")` rather than `include(...)`. That lets IntelliJ IDEA open the
repository root and work on both plugins and the platform modules in one window (using a JDK 25
Gradle daemon), while keeping the plugin's JDK 25 toolchain and IntelliJ Platform dependencies
isolated from root `subprojects`. Build it from the repository root or standalone from its own
directory:

```
./gradlew :practice-plugin:buildPlugin
# or standalone:
cd practice-plugin && ./gradlew buildPlugin
```

### The four workspace concepts

The word "workspace" means four different things, and keeping them apart is the core of the design:

| Type | Owns | Lives in |
|---|---|---|
| `ManagedPracticeWorkspace` | Durable learner attempts — the solutions a learner keeps | IDE config directory (`PathManager.getConfigPath()`) |
| `PracticeWorkspace` | Read-only access to attempts written by the pre-managed layout | Wherever the legacy attempt was created |
| `PracticeModuleWorkspace` | Disposable IDE modules and compiled example harnesses | Generated, per-project, deleted on dispose |
| `VerificationWorkspace` | The throwaway Java 17 verification directory that compiles and runs the hidden tests and complexity probe | Generated per check, deleted after |

Only the first is durable. `ManagedPracticeWorkspace.SOLUTION_PATH` is effectively a storage format:
every attempt directory on disk contains that exact path, so it exists once and is pinned by a test.

### Isolation model

A learner's solution is untrusted code, so it never runs in the IDE's process:

- Each check materializes a fresh `VerificationWorkspace` and runs a Java 17 child JVM
  (`verification-runner.jar` / `VerificationMain`) that compiles the solution, runs the bundled
  JUnit 5 + AssertJ test suite offline, and runs the advisory complexity probe.
- Results come back only as JUnit XML, parsed by `TestReports` with DOCTYPE processing disabled and
  a size cap, never as objects loaded into the IDE.
- Every generated directory is created and deleted through `OwnedDirectory` (shared via `plugin-common`), which writes a
  marker file and refuses to delete any tree it did not create. One delete protocol, one place to
  audit.

### Layer boundaries

```
ui  ──▶  app / coach / run  ◀──▶  state
                        │
                        ▼
                    workspace  ──▶  catalog
                        │
                        ▼
              platform / plugin-common
```

- `catalog` — exercise, study guide, and practice track definitions (`exercises/index.txt`,
  `guides/index.txt`, `tracks/index.txt`). Counts such as "11 hidden tests" are **derived** from the
  templates at load, never hand-copied.
- `coach` — optional post-submission AI Coach using the user's Copilot CLI with every tool disabled
  and `com.microsoft:copilot-sdk` / `CopilotModels` (via `plugin-common`) for model discovery.
- `platform` — host facts (`Os`); symlink-safe directory ownership (`OwnedDirectory`) lives in `plugin-common`.
- `workspace` — the four workspace types above. It knows nothing about running or UI, which is what
  lets it be tested without an IDE.
- `state` — persisted learner progress (`ManagedPracticeProgress`).
- `run` — the run lifecycle. `PracticeRunner` holds an `AtomicReference<RunSession>`, where
  `RunSession` is a sealed type (`Idle`, `ExampleSession`, `CheckSession`).
- `app` — orchestration that spans layers without dragging UI into it (`LegacyImportService`).
- `ui` — React + Mantine pages (`web/`) rendered in JCEF with a Swing HTML fallback (`HtmlPageView`),
  plus IntelliJ actions and dialogs.

See [practice-plugin/ARCHITECTURE.md](practice-plugin/ARCHITECTURE.md) for the complete component
map, lifecycle, and privacy boundaries.

## System Design Practice Plugin (`system-design-plugin`)

`system-design-plugin/` is a second standalone plugin (ID `com.jinloes.systemdesign`) for practicing
system design interviews: original problems, an embedded Excalidraw editor plus `notes.md` for each
attempt, and interviewer-style feedback from the user's own Copilot CLI running with every tool
disabled. It depends only on the IntelliJ Platform and JCEF, so it runs in any JetBrains IDE, and it
shares no code with `practice-plugin`.

Like `practice-plugin`, it is an isolated Gradle build wired into root `settings.gradle` via
`includeBuild("system-design-plugin")`, with its own wrapper and Gradle-managed Node build for its
React pages:

```
./gradlew :system-design-plugin:buildPlugin
# or standalone:
cd system-design-plugin && ./gradlew buildPlugin
```

Problems are per-problem data in `system-design-plugin/src/main/resources/problems/<id>.json`. See
[system-design-plugin/ARCHITECTURE.md](system-design-plugin/ARCHITECTURE.md) for components, storage,
and the privacy boundary.
