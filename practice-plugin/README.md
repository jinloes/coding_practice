# Algorithm Practice for IntelliJ IDEA

A reusable Java practice plugin with original algorithm and data-structure
exercises. Read a problem in the Practice tool window, implement its durable Java
attempt in the regular editor, run it on the visible examples or your own
input, and run the supplied correctness cases.

The plugin source lives here, but learners do not need to create a project,
module, folder, SDK, or Gradle import. It does not depend on this repository's
other modules or modify their exercises.

## Build and install

The initial target is IntelliJ IDEA **2026.2.3**, build **262.10968.63**. The
plugin requires the Java, Gradle, Gradle for Java, and JUnit bundled plugins.
Plugin development uses JDK 25; exercise compilation uses Java 17's language
level and API (`--release 17`).

From the repository root, with `JAVA_HOME` pointing to JDK 25:

```bash
./practice-plugin/gradlew -p practice-plugin buildPlugin
```

To use a locally installed IDE instead of downloading the pinned SDK:

```bash
./practice-plugin/gradlew -p practice-plugin \
  -PidePath="/Applications/IntelliJ IDEA CE.app" buildPlugin
```

Install `practice-plugin/build/distributions/practice-plugin-1.0-SNAPSHOT.zip`
using **Settings > Plugins > gear menu > Install Plugin from Disk**, then restart
if prompted. This is a local development distribution, not a Marketplace release.

To launch an isolated development IDE:

```bash
./practice-plugin/gradlew -p practice-plugin \
  -PidePath="/Applications/IntelliJ IDEA CE.app" runIde
```

Open `practice-plugin/` as its own project to get the shared
**Sandbox IDE (runIde)** run configuration from `practice-plugin/.run/`. Run it
to launch the sandbox, or debug it to attach to the sandbox IDE and breakpoint
plugin code. It takes no hardcoded IDE path: set `idePath` in
`practice-plugin/gradle.properties` (a commented template is checked in) or in
`~/.gradle/gradle.properties` to reuse a local installation, otherwise the
pinned SDK is downloaded.

The main repository's Gradle settings deliberately do not include this build.
Open `practice-plugin/` as its own Gradle project when developing the plugin.

## Practice workflow

1. Open **View > Tool Windows > Practice**.
2. Choose a problem and **Start / Resume**. The plugin creates
   `algorithm-practice/attempts/<exercise-id>/<attempt-id>/src/main/java/`
   `com/jinloes/practice/Solution.java` below the IDE configuration directory,
   opens it, and remembers it from every host project. **New Attempt** creates a
   distinct attempt and never overwrites an earlier one.
3. Write only the requested API in the opened `Solution.java`. Starters contain
   no main method, assertions, or test harness. The Problem tab shows three
   structured input/output examples.
4. **Run Examples** generates a temporary AssertJ-backed `ExampleRunner.java`
   outside durable storage, compiled and launched against the AssertJ jars the
   plugin bundles. **Run With Input...** prompts for one case, shows that
   exercise's input syntax and a sample, and passes it to the same runner as a
   program argument. Custom input prints what the solution returned instead of
   asserting, so you can explore values the visible examples do not cover.
   **Debug With Input...** prompts the same way and launches that case under the
   debugger. Every mode uses a temporary native Application configuration, so a
   breakpoint in the solution gives IntelliJ's usual stepping and variable
   inspection.
5. **Check Solution** copies the saved solution and bundled tests to a unique
   plugin-owned workspace, then executes examples and additional correctness cases
   with Java 17 compatibility. Read
   the **Results** tab for a summary and assertion details, or the native **Run**
   console for compiler diagnostics and process output. When every test passes,
   the same isolated process measures how the saved solution scales and reports
   the result alongside the exercise's intended complexity.

The catalog includes Pair Sum, Binary Search, Balanced Delimiters, Reverse
Linked List, Array Stack, and Binary Min Heap. Each has an explicit contract and
three optional hints. The last hint discusses the intended complexity.

## What a passing result means

- All expected tests from a **fresh full check** completed without failures or
  skips. Running examples or custom input alone never marks an attempt passed.
- Reports are read from a unique directory for each execution; Gradle task and
  build caches cannot turn an old report into a new pass.
- Results refer to the saved files that were checked. Edits make a previous
  result stale; save and check again. A historical pass remains visible.
- Tests are intentionally inspectable and editable. This is not a tamper-proof
  judge or proof of correctness for every possible input.
- The scaling measurement is advisory and never decides a pass or a failure. It
  times the solution on doubling inputs and counts bytes allocated, so it
  describes observed growth on this machine under its current load, not a proof
  of complexity. Timing at these sizes cannot separate O(1) from O(log n) or
  O(n) from O(n log n), so those are reported as one range. Noisy or
  sub-microsecond measurements are reported as inconclusive rather than guessed.
- No LeetCode account, scraping, remote submission, AI service, or telemetry is
  involved. Statements and tests are original content bundled with the plugin.

## Execution and storage

Each opened attempt receives its own non-persistent Java module with real content
and source roots, Java 17 language/API level, and a usable JDK 17 or newer. The
plugin prefers the host project SDK, then another registered JDK, then a temporary
SDK backed by the full IDE runtime. It never changes the host project SDK, roots,
build files, or persistent module metadata. Full checks run the saved solution in
a child Gradle process and child test JVMs; learner, build, and
test code never run in the IDE process.

Default limits are 5 seconds per test, 60 seconds for test execution, and a
256 MiB test heap. The scaling measurement runs inside that execution budget and
caps its own work at about six seconds. Change these with **Limits** in the Practice tool window.
Dependency download and compilation have a separate five-minute startup limit.
**Stop** cancels the owned execution; timed-out checks stop their owned process
tree. Example, custom-input, and debug runs have no automatic timeout, so
breakpoints remain usable.

These process boundaries are **not a security sandbox**: learner code and
project build scripts run with your user permissions. Run only trusted local
code. First use needs network access for Gradle and test dependencies; later
runs can reuse their local caches.

Solutions live below the IDE configuration path in the durable
`algorithm-practice/attempts/` hierarchy. Progress, limits, selected attempts,
revealed hints, fingerprints, and
historical passes are stored in a non-roaming application setting keyed by attempt
ID; they are not uploaded or synchronized. Full-check workspaces and reports are
created below the IDE system path, marked for ownership, and removed after
terminal runs. The Gradle home beside them is not: every check under the same
IDE system path shares one `algorithm-practice/gradle-home`, so the downloaded
Gradle distribution and test dependencies are fetched once and reused by later
checks instead of being deleted with each finished workspace. **Clean Generated
Artifacts** removes only marked, confirmed-inactive harness, output, and
verification
leftovers; attempts, the shared Gradle home, and host or legacy files are
never cleanup targets.

When the current project is a marked project created by an earlier plugin
version, **Import Legacy Attempts** validates and copies each valid attempt once.
Originals remain untouched. Selections, hints, limits, and historical-pass dates
are migrated, while every imported attempt starts at `NOT_RUN` and requires a
fresh full check. Invalid, missing, foreign, or redirecting entries are skipped.

## Development

```bash
./practice-plugin/gradlew -p practice-plugin \
  -PidePath="/Applications/IntelliJ IDEA CE.app" test buildPlugin
```

Tests use JUnit Jupiter and AssertJ. Content fixtures keep reference solutions
under `src/test/resources/reference/`, separate from the shipped starter
templates. The catalog tests compile exercise sources with `--release 17` and
exercise the actual bundled test suites.

The IntelliJ fixture harness needs a runtime-only JUnit 4 compatibility
dependency. This is an approved exception for the SDK bootstrap only: plugin
tests are authored with Jupiter, and neither generated projects nor the shipped
plugin depend on JUnit 4.

To add content, extend `ExerciseCatalog` with a stable ID, original statement,
three hints, structured visible examples, and exact example/full test counts. Add
`Solution.java`, `ExampleRunner.java`, `ExamplesTest.java`, and
`CorrectnessTest.java` under `resources/exercises/<id>/`.
Keep tests deterministic, use individual `@Test` methods, validate equivalent
legal answers rather than one particular implementation, and add an author-only
reference fixture plus an incorrect candidate to the content tests. Tests and the
example runner both assert with AssertJ; the runner's classpath comes from the
jars in `resources/example-libraries/`, which the build populates from the
`exampleLibraries` configuration.

Not included: other solution languages, arbitrary existing-project injection,
remote judging, secure hidden tests, visualizations, automatic complexity
grading, cloud sync, or Marketplace publishing.
