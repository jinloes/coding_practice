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
    gradle/verification-build.gradle
    harness/ComplexityProbe.java
    META-INF/plugin.xml
  src/test/java/com/jinloes/practice_plugin/
  src/test/resources/reference/<id>/Solution.java
```

## Package Responsibilities

| Package | Responsibility | Important classes |
|---|---|---|
| `app` | Cross-package application use cases | `LegacyImportService` |
| `catalog` | Exercise registry, manifest parsing, bundled example libraries | `ExerciseCatalog`, `ExampleLibraries` |
| `platform` | Safe filesystem ownership and OS differences | `OwnedDirectory`, `Os` |
| `run` | Run/debug/check lifecycle, process control, reports, complexity | `PracticeRunner`, `RunSession`, `CheckResult`, `TestReports`, `ComplexityAnalysis` |
| `state` | IntelliJ persistent state | `ManagedPracticeProgress`, legacy `PracticeProgress` |
| `ui` | Tool window UI and rendering | `PracticePanel`, `PracticeToolWindowFactory`, `MarkdownHtml` |
| `workspace` | Durable attempts, IDE modules, generated checks, legacy format | `ManagedPracticeWorkspace`, `PracticeModuleWorkspace`, `VerificationWorkspace`, `PracticeWorkspace` |

Dependencies should point toward focused lower-level services. Keep
cross-package orchestration in `app`, `run`, or `ui`; do not make filesystem
types depend on UI or persistent-state types.

## Build And Run

Run from `practice-plugin/`:

```bash
../gradlew test
../gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest"
../gradlew buildPlugin
../gradlew runIde
```

The build is standalone and is deliberately not included by the repository
root's Gradle settings. Plugin compilation uses JDK 25. Learner and generated
verification compilation uses Java 17.

Set `idePath` in `practice-plugin/gradle.properties` or user-level Gradle
properties to use a local IDE. Never commit a real machine path.

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

## Test Conventions

- Use JUnit Jupiter and AssertJ only for authored tests.
- Keep test classes and methods package-private.
- Use plain `@Test`; the harness provides execution limits.
- Prefer behavior-level tests through public or package-visible seams.
- Add regression tests for lifecycle, cleanup, persistence, path safety, report
  parsing, and generated source behavior when changing those areas.
- The runtime-only JUnit 4 dependency exists only because the IntelliJ fixture
  bootstrap references legacy APIs.

## Adding An Exercise

1. Choose a stable lowercase kebab-case ID and append it to the ordered `IDS`
   list in `ExerciseCatalog`.
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
5. Add a compile-compatible known-wrong implementation to
   `ExerciseCatalogTest.wrongSource`.
6. Update user-facing catalog documentation if it enumerates exercises.
7. Run the catalog test, then the full plugin suite.

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

## Changing Execution

When modifying example or full-check execution, inspect these surfaces
together:

- `PracticeRunner` and `RunSession` for ownership and state transitions;
- `PracticeModuleWorkspace` for module, SDK, harness, compilation, and native
  run integration;
- `VerificationWorkspace` and `verification-build.gradle` for generated checks;
- `ManagedPracticeProgress` for persisted result semantics;
- `PracticePanel` for controls, status, and stale-result presentation;
- integration tests for cancellation, timeout, cleanup, and report freshness.

Do not introduce a second independent run-state flag. The atomic `RunSession`
is the source of truth.

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
