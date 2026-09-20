# Coding Practice Architecture

## Overview

This repository is a multi-module Gradle project for practicing algorithms, data structures, and regex engines in Java.

## Build System

- Build tool: Gradle 9.3.1
- Multi-module layout driven by root `settings.gradle`
- Spring dependency management plugin is used in the build

## Module Organization

Each topic is implemented as an independent Gradle submodule.

| Module Group | Modules | Responsibility |
|---|---|---|
| Core algorithm practice | `arrays`, `strings`, `sorting`, `searching`, `recursion`, `dynamic_programming`, `greedy`, `graphs` | Algorithm exercises and implementations |
| Data structure practice | `hash_tables`, `linked_lists`, `stacks_and_queues`, `heaps`, `binary_trees`, `binary_search_tree` | ADT and tree/heap implementations |
| Platform and language features | `concurrency`, `reflection`, `design` | Java platform, patterns, and API-focused practice |
| Regex engine work | `regex` | Java regex and PCRE2 integration via pcre4j |

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

### Nested build

The plugin is a **separate Gradle build**, deliberately absent from the root `settings.gradle`. It
targets the IntelliJ Platform (Java 25 toolchain, IntelliJ Platform Gradle Plugin) rather than the
monorepo's Java 17/21 baseline, so including it would force the whole repository onto the plugin's
toolchain. Build it from its own directory, reusing the root wrapper:

```
cd practice-plugin && ../gradlew build
```

### The four workspace concepts

The word "workspace" means four different things, and keeping them apart is the core of the design:

| Type | Owns | Lives in |
|---|---|---|
| `ManagedPracticeWorkspace` | Durable learner attempts — the solutions a learner keeps | IDE config directory (`PathManager.getConfigPath()`) |
| `PracticeWorkspace` | Read-only access to attempts written by the pre-managed layout | Wherever the legacy attempt was created |
| `PracticeModuleWorkspace` | Disposable IDE modules and compiled example harnesses | Generated, per-project, deleted on dispose |
| `VerificationWorkspace` | The throwaway Gradle project that runs the hidden tests | Generated per check, deleted after |

Only the first is durable. `ManagedPracticeWorkspace.SOLUTION_PATH` is effectively a storage format:
every attempt directory on disk contains that exact path, so it exists once and is pinned by a test.

### Isolation model

A learner's solution is untrusted code, so it never runs in the IDE's process:

- Each check materializes a fresh `VerificationWorkspace` — its own Gradle project, wrapper, and
  build script — and runs it as a child process.
- The generated build script is a **resource** (`src/main/resources/gradle/verification-build.gradle`)
  whose dependency versions are injected at build time from `build.gradle`, so the plugin and the
  code it generates cannot drift apart.
- Results come back only as JUnit XML, parsed by `TestReports` with DOCTYPE processing disabled and
  a size cap, never as objects loaded into the IDE.
- Every generated directory is created and deleted through `platform/OwnedDirectory`, which writes a
  marker file and refuses to delete any tree it did not create. One delete protocol, one place to
  audit.

### Layer boundaries

```
ui  ──▶  app  ──▶  run  ◀──▶  state
                    │
                    ▼
                workspace  ──▶  catalog
                    │
                    ▼
                platform
```

- `catalog` — the exercise definitions. Each exercise is a directory under
  `src/main/resources/exercises/<id>/` holding an `exercise.json` manifest plus its Java templates.
  Counts such as "11 hidden tests" are **derived** from the templates at load, never hand-copied.
- `platform` — host facts and mechanisms with no plugin concepts (`Os`, `OwnedDirectory`).
- `workspace` — the four workspace types above. It knows nothing about running or UI, which is what
  lets it be tested without an IDE.
- `state` — persisted learner progress (`ManagedPracticeProgress`).
- `run` — the run lifecycle. `PracticeRunner` holds exactly one piece of state, an
  `AtomicReference<RunSession>`, where `RunSession` is a sealed type: `Idle`, `ExampleSession`, or
  `CheckSession`. What is in flight is a property of which variant is present, not of a dozen
  independently mutable fields.
- `app` — orchestration that spans layers without dragging UI into it (`LegacyImportService`).
- `ui` — Swing only. The tool window panel registers a run-output listener and deregisters it on
  dispose, so closing and reopening the window neither leaks panels nor loses output.

`workspace` references only `catalog` and `platform` — never `run` or `state` — which is what lets
the workspace types be tested without starting an IDE.

One two-way edge remains: `state.ManagedPracticeProgress` imports `run.CheckResult` while
`run.PracticeRunner` imports `state.ManagedPracticeProgress`. `CheckResult` is a result value, not
run machinery, so the natural fix is to move it to a layer both can depend on. That has not been
done, and this document says so rather than drawing a diagram that is cleaner than the code.
