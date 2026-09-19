# Algorithm Practice for IntelliJ IDEA

A reusable Java practice plugin with original algorithm and data-structure
exercises. Read a problem in the Practice tool window, implement its starter in
the regular editor, debug examples, and run the supplied correctness cases.

The plugin source lives here, but it creates a separate practice project. It does
not depend on this repository's other modules or modify their exercises.

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

The main repository's Gradle settings deliberately do not include this build.
Open `practice-plugin/` as its own Gradle project when developing the plugin.

## Practice workflow

1. Open **View > Tool Windows > Practice**.
2. Choose **Create Practice Project**, select a parent folder, and give the new
   directory a name. Existing directories are never overwritten. Reopen an
   existing generated project with **Open Practice Project**.
3. Set the practice project's SDK to a **JDK 17 or newer** under **File > Project
   Structure**. Wait for Gradle import. **Reload Gradle** retries an import after
   SDK or network problems.
4. Choose a problem and **Start / Resume**. Each attempt is an independent Gradle
   subproject. Use the attempt selector to revisit earlier work; **New Attempt**
   preserves all earlier files.
5. Write the solution in `src/main/java/com/jinloes/practice/Solution.java`.
6. **Run Examples** executes just the example tests. **Debug Examples** creates a
   native JUnit debug configuration: put a breakpoint in the solution and use
   IntelliJ's usual stepping and variable inspection.
7. **Check Solution** executes examples and additional correctness cases. Read
   the **Results** tab for a summary and assertion details, or the native **Run**
   console for compiler diagnostics and process output.

The catalog includes Pair Sum, Binary Search, Balanced Delimiters, Reverse
Linked List, Array Stack, and Binary Min Heap. Each has an explicit contract and
three optional hints. The last hint discusses the intended complexity.

## What a passing result means

- All expected tests from a **fresh full check** completed without failures or
  skips. Running or debugging examples alone never marks an attempt passed.
- Reports are read from a unique directory for each execution; Gradle task and
  build caches cannot turn an old report into a new pass.
- Results refer to the saved files that were checked. Edits make a previous
  result stale; save and check again. A historical pass remains visible.
- Tests are intentionally inspectable and editable. This is not a tamper-proof
  judge, proof of correctness for every possible input, or complexity analyzer.
- No LeetCode account, scraping, remote submission, AI service, or telemetry is
  involved. Statements and tests are original content bundled with the plugin.

## Execution and storage

Practice Run configurations launch the generated project's Gradle wrapper and
target only the selected attempt. Learner code runs in child test JVMs, never
inside the IDE. Debugging uses the native JUnit runner after Gradle import.

Default limits are 5 seconds per test, 60 seconds for test execution, and a
256 MiB test heap. Change these with **Limits** in the Practice tool window.
Dependency download and compilation have a separate five-minute startup limit.
**Stop** cancels the owned execution; timed-out checks stop their owned process
tree. Debug sessions have no automatic timeout so breakpoints remain usable.

These process boundaries are **not a security sandbox**: learner code and
project build scripts run with your user permissions. Run only trusted local
code. First use needs network access for Gradle and test dependencies; later
runs can reuse their local caches.

Solutions and tests live under `attempts/`. The project marker and each attempt's
metadata identify plugin-created workspaces and catalog revisions. Progress,
limits, selected attempts, and revealed hints are local IntelliJ workspace state
in `.idea/workspace.xml`; they are not uploaded or synchronized. Per-run reports
live under the ignored `.practice-results/` directory.

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
three hints, and exact example/full test counts. Add `Solution.java`,
`ExamplesTest.java`, and `CorrectnessTest.java` under `resources/exercises/<id>/`.
Keep tests deterministic, use individual `@Test` methods, validate equivalent
legal answers rather than one particular implementation, and add an author-only
reference fixture plus an incorrect candidate to the content tests.

Not included: other solution languages, arbitrary existing-project injection,
remote judging, secure hidden tests, visualizations, automatic complexity
grading, cloud sync, or Marketplace publishing.
