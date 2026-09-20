# Practice Plugin Agent Guide

Rules for agents and contributors working inside `practice-plugin/`. The
repository-root `AGENTS.md` still applies; this file adds plugin-specific rules
and takes precedence where the two overlap.

## Scope

Applies to the IntelliJ plugin source, its tests, and the exercise content under
`src/main/resources/exercises/`.

## Assertions: AssertJ Only

Every assertion in this module is written with AssertJ. This is not limited to
JUnit tests — it includes the generated example runner, which is an ordinary
`main` method.

- Import `static org.assertj.core.api.Assertions.assertThat` and
  `static org.assertj.core.api.Assertions.assertThatThrownBy`.
- Do **not** use `org.junit.jupiter.api.Assertions` (`assertEquals`,
  `assertTrue`, `assertThrows`, `assertArrayEquals`, ...), Hamcrest, JUnit 4, or
  hand-rolled checks such as `if (...) throw new AssertionError(...)`.
- Private assertion helpers inside a test or runner must also delegate to
  `assertThat(...)`. A helper is not an excuse to hand-roll a comparison.
- Prefer the specific AssertJ assertion over a boolean one, because the failure
  message carries the values: use `isEqualTo`, `isBetween`, `hasSize`,
  `containsExactly`, `isEmpty`, `isSameAs`, and `isNull` rather than
  `assertThat(a == b).isTrue()`.
- Use `assertThatThrownBy(callable).isInstanceOf(...)` for expected exceptions.
- Attach `.as(...)` context to every assertion whose inputs are not already
  visible in the AssertJ failure message. The failure a learner reads must name
  the call that failed, not just the compared values, for example
  `assertThat(index).as("search(%s, %d)", Arrays.toString(sorted), target)`.
  This matters most for boolean and empty assertions (`isTrue`, `isFalse`,
  `isEmpty`, `isNull`), which otherwise report nothing about the input, and for
  assertions inside loops or shared helpers, where the failing iteration is
  otherwise unidentifiable.
- Build the context string once per helper (a private `call(...)` method that
  formats the arguments) instead of repeating the formatting at each assertion.

This applies to all four generated templates per exercise:
`Solution.java` (no assertions at all), `ExamplesTest.java`,
`CorrectnessTest.java`, and `ExampleRunner.java`.

`ExerciseCatalogTest` enforces part of this: it fails when a runner mentions
`org.junit` or omits `org.assertj.core.api.Assertions`.

## Exercise Content Rules

- Keep test classes and methods package-private; no `public class ExamplesTest`.
- Use plain `@Test` methods. Do not add `@Timeout`, `@ParameterizedTest`, or
  `@TestFactory` — the catalog test rejects them and the harness supplies
  timeouts.
- Keep `@Test` counts in sync with `exampleCount()` and `fullCount()` in
  `ExerciseCatalog`.
- Starters must contain no `main` method, no assertions, and no test harness, and
  must throw `UnsupportedOperationException("Implement <method>")`.
- Assert on any legal answer rather than one particular implementation when the
  contract allows several.
- `ExampleRunner.main` must print `Examples passed: <exampleCount>` on success.
- Add an author-only reference solution under `src/test/resources/reference/<id>/`
  and a known-wrong candidate in `ExerciseCatalogTest`.

## Example Runner Classpath

The example runner compiles and runs against AssertJ jars that the plugin
bundles; it is not JDK-only.

- Jars come from the `exampleLibraries` configuration in `build.gradle`, are
  copied into `example-libraries/` in the plugin resources with a generated
  `index.txt`, and are read through `ExampleLibraries`.
- `PracticeModuleWorkspace` extracts them into the generated harness `libs/`
  directory, passes them to `javac -classpath`, and attaches them as a module
  library so example and custom-input runs resolve them.
- To change the AssertJ version, edit the `exampleLibraries` dependency only. Do
  not check jars into the repository or hard-code jar file names.

## Example Runner Input Mode

Each `ExampleRunner.java` template supports two modes.

- With no program arguments it asserts the visible examples with AssertJ and
  prints `Examples passed: N`.
- With arguments it joins them into one string, parses that single case, and
  **prints** the result. Custom input deliberately does not assert, because
  checking an arbitrary answer would require embedding a reference solution in a
  file the learner can read. The AssertJ-only rule applies to the example
  assertions, not to this mode.
- Every runner declares a `USAGE` constant that must stay byte-identical to that
  exercise's `inputSyntax` in `ExerciseCatalog`, and prints it when parsing
  fails. `ExerciseCatalogTest` asserts both, so update the catalog and the
  template together.

## Complexity Probe

- Every exercise directory needs a `Workload.java` next to its other templates,
  declaring `UNIT`, `SIZES`, `prepare(n)`, `run(state)`, and `units(n)`.
- `prepare` runs outside the timed region and is re-invoked before every
  repetition, so a workload may hand the solution mutable state.
- `units(n)` divides the elapsed time. Return the number of operations performed
  when the workload loops, so the measurement is per operation.
- The probe prints a measurement; it never asserts and never fails a check.
  `ignoreExitValue` keeps a probe crash from turning a pass into a runner error.
- `intendedTime` and `intendedSpace` in `ExerciseCatalog` must be substrings of
  the classifier's bucket labels in `ComplexityAnalysis.growth`, because
  `ComplexityReport.compare` matches them by substring.

## Build And Test

Run from `practice-plugin/` using the repository wrapper.

```bash
../gradlew test
../gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest"
../gradlew runIde
```

Open `practice-plugin/` as its own project to pick up the shared
**Sandbox IDE (runIde)** configuration in `practice-plugin/.run/`. Keep run
configurations there — `.idea/` is gitignored — and keep them free of machine
specific paths. Point the sandbox at a local IDE by setting `idePath` in
`practice-plugin/gradle.properties` or `~/.gradle/gradle.properties`; never
commit a real path into `gradle.properties`.

The IntelliJ fixture bootstrap needs a runtime-only JUnit 4 dependency. That is
an approved SDK-bootstrap exception; authored tests stay on Jupiter and AssertJ,
and neither generated projects nor the shipped plugin depend on JUnit 4.
