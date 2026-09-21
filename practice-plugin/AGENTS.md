# Practice Plugin Agent Guide

Rules for agents and contributors working inside `practice-plugin/`. The
repository-root `AGENTS.md` also applies; this file takes precedence where the
rules overlap.

## Keep Documentation Synchronized

`ARCHITECTURE.md` and `CODE-MAP.md` are part of the implementation contract, not
optional background reading.

- Read both before changing plugin behavior, package responsibilities, runtime
  flows, persistence, generated files, exercise structure, dependencies, build
  configuration, or test strategy.
- Update `ARCHITECTURE.md` in the same change when boundaries, ownership, data
  flow, execution flow, storage, lifecycle, or security assumptions change.
- Update `CODE-MAP.md` in the same change when packages, important classes,
  extension procedures, build commands, code conventions, or validation
  requirements change.
- Update both when a change affects both system design and implementation
  guidance. Do not leave known future-documentation work for a later change.
- Keep documentation durable: describe responsibilities and invariants rather
  than copying implementation details that can be discovered directly from one
  method.

## Scope

These rules apply to plugin source, tests, build configuration, and exercise
content under `src/main/resources/exercises/`.

## Build And Test

Run from `practice-plugin/` with the repository wrapper:

```bash
../gradlew test
../gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest"
../gradlew runIde
```

Use the smallest relevant test first, then run the full plugin test suite before
finishing a code change.

## Java And Test Conventions

- Plugin development uses JDK 25. Generated learner code and verification use
  Java 17 (`--release 17`).
- Use JUnit Jupiter with plain `@Test` methods. Do not add JUnit 4 assertions,
  Hamcrest, `@Timeout`, `@ParameterizedTest`, or `@TestFactory`.
- The runtime-only JUnit 4 dependency in `build.gradle` is permitted only for
  IntelliJ fixture bootstrap compatibility.
- Use AssertJ for every authored assertion, including generated
  `ExampleRunner.java` files. Do not use hand-written `AssertionError` checks.
- Prefer value-specific AssertJ assertions and attach `.as(...)` context when
  the failing input or iteration would otherwise be unclear.
- Keep test classes and test methods package-private.

## Exercise Content Rules

Every exercise ID listed in `ExerciseCatalog` must provide:

```text
src/main/resources/exercises/<id>/
  exercise.json
  Solution.java
  ExamplesTest.java
  CorrectnessTest.java
  ExampleRunner.java
  Workload.java
src/test/resources/reference/<id>/Solution.java
```

Also add a known-wrong candidate to `ExerciseCatalogTest`.

- Starters contain only the learner API, no `main`, assertions, or harness, and
  throw `UnsupportedOperationException("Implement <method>")`.
- Test counts must match the manifest-derived `exampleCount()` and
  `fullCount()`.
- Assert any legal answer when the contract permits multiple results.
- `ExampleRunner` must support no-argument visible examples and custom-input
  mode, use AssertJ for examples, and print `Examples passed: N` on success.
- The runner's `USAGE` value must be byte-identical to the manifest
  `inputSyntax`; invalid custom input must print it.
- `Workload` must declare `UNIT`, `SIZES`, `prepare`, `run`, and `units`.
  Preparation stays outside the timed region, mutable inputs are rebuilt for
  every repetition, and complexity probes measure rather than assert.
- Intended complexity strings must be compatible with the classifier labels in
  `ComplexityAnalysis`.

## Repository Hygiene

- Keep shared run configurations in `.run/`; do not commit `.idea/` state or
  machine-specific IDE paths.
- Configure a local IDE with `idePath` in a local Gradle properties file; never
  commit a real installation path.
- Do not check dependency jars into source control or hard-code generated jar
  names. Change the `exampleLibraries` dependency in `build.gradle`.
- Preserve storage schemas, marker formats, and generated path contracts unless
  the change includes an explicit migration and corresponding documentation.
