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
../gradlew test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest" -PcatalogOnly=id1,id2
../gradlew runIde
```

`-PcatalogOnly` limits the slow per-exercise catalog checks to the listed
exercise IDs; the full catalog takes several minutes. Use the smallest relevant
test first, then run the full plugin test suite before
finishing a code change.

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
src/test/resources/known-wrong/<id>/Solution.java
```

The known-wrong candidate compiles against the shipped tests but must fail at
least one of them; `KnownWrongSolutions` loads it from the test resources.

- Starters contain only the learner API, no `main`, assertions, or harness, and
  throw `UnsupportedOperationException("Implement <method>")`.
- `ExamplesTest.java` and `CorrectnessTest.java` use one plain `@Test` per
  case. Do not use `@ParameterizedTest`, `@TestFactory`, or `@Timeout` there:
  expected test counts come from counting `@Test` annotations, and a
  parameterized or dynamic test runs more tests than it declares, so the check
  reports an incomplete run. The verification build already applies the
  learner's per-test timeout. Plugin tests under `src/test/` are not affected.
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

## Study Guide Content Rules

Every guide ID listed in `GuideCatalog` must provide
`src/main/resources/guides/<id>.json` with a matching `id`, non-blank `title`,
`summary`, and `lesson`, and a non-empty `exercises` array of existing,
non-duplicated exercise IDs.

- Lessons are original text in the `MarkdownHtml` subset (paragraphs, `- `
  bullets, inline code) and contain the label lines `How it works`,
  `When to reach for it`, `Common patterns`, and `Pitfalls`, each followed by
  bullets.
- Lessons teach the technique; they must not walk through a listed exercise's
  solution, which belongs to that exercise's hints.
- Guides never declare difficulty. Levels come from each exercise's manifest
  and must be `Easy`, `Medium`, or `Hard`.
- An exercise may appear in several guides. Update `GuideCatalogTest` when the
  guide list or membership changes.

## Practice Track Content Rules

Every track ID listed in `TrackCatalog` must provide
`src/main/resources/tracks/<id>.json` in the guide manifest schema with a
non-empty `exercises` array of existing, non-duplicated exercise IDs. Tracks
are hand-curated problem lists such as Blind 75 or a company list.

- Every summary must say the list is not an official or verified question
  list. Company tracks must also say they are approximate, and their notes must
  not claim specific questions.
- Notes are original text in the `MarkdownHtml` subset with the label lines
  `About this list`, `What to expect`, and `How to prepare`.
- Exercises on a track are original problems. Never copy or paraphrase
  third-party problem statements, and never scrape or copy third-party
  company-tag data (for example paid question-frequency lists) into a track.
- Track IDs must not collide with guide IDs. Update `TrackCatalogTest` when the
  track list or membership changes.

## Repository Hygiene

- Keep shared run configurations in `.run/`; do not commit `.idea/` state or
  machine-specific IDE paths.
- Configure a local IDE with `idePath` in a local Gradle properties file; never
  commit a real installation path.
- Do not check dependency jars into source control or hard-code generated jar
  names. Change the `exampleLibraries` dependency in `build.gradle`.
- Preserve storage schemas, marker formats, and generated path contracts unless
  the change includes an explicit migration and corresponding documentation.
