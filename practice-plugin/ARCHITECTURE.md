# Algorithm Practice Plugin Architecture

This document describes the durable design of the IntelliJ Algorithm Practice
plugin. Update it whenever code changes package responsibilities, component
boundaries, runtime or data flow, persistence, generated artifacts, lifecycle,
or security assumptions.

## Purpose And Boundaries

The plugin provides original Java algorithm exercises inside IntelliJ IDEA. A
learner selects an exercise, works in a durable `Solution.java`, runs visible
examples or custom input through native IDE run/debug support, and checks the
solution in an isolated generated Gradle project.

The plugin is intentionally:

- local-only: it has no account, remote judge, telemetry, or cloud sync;
- Java-only for learner solutions;
- integrated with IntelliJ's editor, modules, run configurations, and debugger;
- a correctness and feedback tool, not a security sandbox or tamper-proof judge.

Learner and generated build code run with the current user's permissions, but
never in the IDE process itself.

## Component Map

```text
plugin.xml
  |
  v
PracticeToolWindowFactory
  |
  v
PracticePanel -------------------------------------------+
  |                                                      |
  | selects/opens attempts                               | runs/checks/stops
  v                                                      v
ManagedPracticeWorkspace                         PracticeRunner
  |                                                      |
  | creates durable source                               +-- example/input/debug
  v                                                      |      |
IDE config/algorithm-practice/attempts/                   |      v
                                                         |  PracticeModuleWorkspace
ExerciseCatalog <--- bundled manifests/templates          |      |
  |                                                      |      v
  +-- ExampleRunner + AssertJ libraries ------------------+  native IDE run/debug
  |
  +-- tests + workload + verification build -------------+
                                                         |
                                                         +-- full check
                                                                |
                                                                v
                                                        VerificationWorkspace
                                                                |
                                                                v
                                                        child Gradle + test JVM

ManagedPracticeProgress <---- results, selection, limits, hints
LegacyImportService <--------- legacy reader + managed storage + progress
```

### `catalog`

`ExerciseCatalog` is the authoritative ordered registry of shipped exercises.
It loads each `exercise.json`, validates metadata, derives visible and full test
counts from bundled sources, and provides safe access to exercise and harness
resources.

`ExampleLibraries` reads the generated library index and extracts bundled
AssertJ jars for example compilation and execution.

Exercise content is data plus Java templates under
`src/main/resources/exercises/<id>/`. Author-only reference implementations
live under `src/test/resources/reference/<id>/` and are never shipped as learner
starters.

### `ui`

`PracticeToolWindowFactory` registers the Practice tool window.
`PracticePanel` owns presentation and user interaction:

- catalog search and topic, difficulty, and progress filters;
- exercise statements, hints, attempts, and result rendering;
- attempt creation and opening;
- example, custom-input, debug, check, stop, cleanup, limits, and legacy-import
  actions;
- stale-result feedback when the selected solution changes.

The panel delegates storage, execution, and migration work. It does not own
their persistence or process lifecycle. `ExerciseFilters` holds the search and
filter controls, `LimitsDialog` edits execution limits, and `PracticeViews`
provides stateless Swing building blocks; none of them hold panel state.

### `workspace`

`ManagedPracticeWorkspace` owns durable learner attempts below the IntelliJ
configuration path. It validates IDs and paths, writes starters atomically, and
fingerprints the solution together with the exercise revision and correctness
tests.

`PracticeModuleWorkspace` projects a managed attempt into the current IDE as a
non-persistent Java module. It selects a usable JDK, applies Java 17 language
level, manages compiler output, extracts example libraries, creates temporary
example harnesses, and tracks temporary run configurations. It delegates
marker-based ownership of harness and module-output directories to
`GeneratedDirectories`, which it guards with its own monitor.

`VerificationWorkspace` creates a marked, per-run Gradle project below the IDE
system path. It copies the saved solution, tests, workload, complexity probe,
filtered verification build script, and Gradle bootstrap resources. It also
owns process markers and conservative cleanup.

`PracticeWorkspace` is a read-only compatibility reader for the legacy
project-based storage format. New attempts never use it.

### `run`

`PracticeRunner` is a project service and the single coordinator for execution.
It allows one active practice run per project, saves documents before launch,
publishes status to UI listeners, owns cancellation and watchdog behavior, and
records full-check results.

`FullCheck` launches one full check on behalf of the runner: it builds the
verification process, enforces setup and suite limits with a watchdog, bounds
console output through `BoundedProcessHandler`, and translates the finished
process into a `CheckResult`. It never touches the run guard; the runner claims
the guard first and releases it when `FullCheck` reports completion.
`ExampleRunConfigurations` builds the temporary native Application
configuration for example and custom-input runs.

`RunSession` models the lifecycle as one atomic idle-or-active state. Mutable
signals such as the live process, watchdog, cancellation reason, and child
processes are scoped to one `RunControl`, preventing state leakage between
runs.

`PracticeRunConfiguration` and `PracticeConfigurationType` connect full checks
to IntelliJ's execution infrastructure. `TestReports`, `FailureText`,
`CheckResult`, `ComplexityAnalysis`, and `ComplexityReport` translate generated
run artifacts into user-facing results.

### `state`

`ManagedPracticeProgress` is non-roaming application state keyed by managed
attempt ID. It stores selections, limits, hint progress, fingerprints,
current/previous results, complexity text, historical passes, and legacy
mappings.

`PracticeProgress` is project-level legacy state retained only to support
migration.

### `app`

`LegacyImportService` is the application use case spanning legacy workspace
reading, managed attempt creation, and progress migration. Keeping orchestration
here prevents the `workspace` package from depending on `run` or `state`.

### `platform`

`OwnedDirectory` centralizes marker-based ownership and safe recursive cleanup.
`Os` holds platform-specific executable naming. Generated or verification paths
must prove ownership before deletion.

## Runtime Flows

### Start Or Resume

1. The panel asks `ManagedPracticeWorkspace` for attempts for the selected
   exercise.
2. Creating an attempt writes the bundled starter atomically to the managed
   configuration-path hierarchy.
3. `PracticeModuleWorkspace` creates or reuses a non-persistent module rooted at
   that attempt and opens `Solution.java`.
4. Selection and hint state are persisted by `ManagedPracticeProgress`.

The stable solution path is
`src/main/java/com/jinloes/practice/Solution.java`. It is an on-disk storage
contract because every existing attempt contains it.

### Visible Examples And Custom Input

1. `PracticeRunner` saves documents and claims the one-run guard.
2. `PracticeModuleWorkspace` creates a marked temporary harness and extracts
   bundled AssertJ libraries.
3. The learner solution and generated `ExampleRunner` compile with
   `javac --release 17`.
4. A temporary native Application run configuration starts under Run or Debug.
5. No arguments run AssertJ-backed visible examples. One joined input argument
   runs the parser and prints the learner result without embedding a hidden
   reference solution.
6. Termination removes temporary execution state and releases the run guard.

These runs are exploratory and never update correctness progress.

### Full Check

1. `PracticeRunner` saves documents, validates limits, reads the managed
   attempt, and computes a fingerprint.
2. `VerificationWorkspace` builds a unique marked Gradle project from the saved
   solution and bundled verification resources.
3. A child Gradle process runs with a shared plugin-owned Gradle home and starts
   child test/probe JVMs with Java 17 compatibility.
4. Visible and correctness tests run first. If they pass, the advisory
   complexity probe measures doubling workloads.
5. The runner reads fresh report files, compares the checked fingerprint with
   current content, records the result, marks the workspace finished, and
   removes it.
6. Timeout, cancellation, or disposal terminates owned children. Uncertain
   ownership or process state causes preservation rather than risky deletion.

The scaling report never determines pass/fail. Its classifier deliberately
groups growth rates that wall-clock measurements cannot reliably distinguish.

## Storage And Generated Artifacts

| Data | Location base | Lifetime |
|---|---|---|
| Learner attempts | IDE configuration path | Durable |
| Managed progress | `algorithm-practice-managed.xml` | Durable, non-roaming |
| Legacy progress | Project workspace state | Read for migration |
| Example harnesses | IDE system path | Temporary, marked |
| Module outputs | IDE system path | Temporary, marked |
| Verification projects | IDE system path | Per check, marked |
| Verification Gradle home | IDE system path | Shared cache, durable |

Cleanup never targets learner attempts, the shared Gradle home, foreign files,
redirecting paths, or unmarked directories.

## Build-Time Resource Assembly

The standalone Gradle build:

- compiles plugin code with a JDK 25 toolchain;
- packages the Gradle wrapper as verification bootstrap resources;
- resolves `exampleLibraries`, packages their jars, and generates `index.txt`;
- replaces dependency and Java release tokens in
  `gradle/verification-build.gradle`;
- packages exercise, harness, and plugin descriptor resources.

Runtime code must not assume dependency jar filenames. It consumes the generated
library index.

## Architectural Invariants

- There is at most one active practice execution per project.
- Durable attempts are independent of the currently open host project.
- Attempts and generated workspaces never mutate host project SDKs, roots, or
  build files.
- Full checks operate on saved source and fresh report directories.
- A result belongs to a content fingerprint; later edits make it stale.
- Reference solutions are test resources only.
- Generated directories require ownership markers before cleanup.
- Legacy formats remain readable until an explicit compatibility decision and
  migration removes them.
- Exercise tests and runners compile against Java 17 and AssertJ.
