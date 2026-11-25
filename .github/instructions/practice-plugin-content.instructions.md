---
applyTo: "practice-plugin/src/main/resources/{exercises,guides,tracks}/**,practice-plugin/src/test/resources/{reference,known-wrong}/**"
---

# Practice Plugin Content Authoring Rules

Apply these rules when adding or modifying exercises, study guides, practice
tracks, or reference/known-wrong solutions in `practice-plugin/`. Also consult
the "Adding An Exercise", "Adding A Study Guide", and "Adding A Practice Track"
sections of `practice-plugin/CODE-MAP.md`.

## Exercise Content Rules

Every exercise ID listed in `src/main/resources/exercises/index.txt` must
provide:

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

- Every `exercise.json` must declare `"topics"` as a non-empty array of unique,
  non-blank topic strings (never a single `"topic"` field). List the primary
  domain or data structure first and include every applicable secondary pattern
  or sub-topic (for example `["Arrays", "Two Pointers"]` or
  `["Arrays", "Hash Maps"]`). Prefer existing canonical topic names (`Arrays`,
  `Backtracking`, `Binary Search`, `Binary Trees`, `Bit Manipulation`,
  `Data Structures`, `Dynamic Programming`, `Graphs`, `Greedy`, `Hash Maps`,
  `Heaps`, `Intervals`, `Linked Lists`, `Math`, `Matrices`, `Prefix Sums`,
  `Queues`, `Sliding Window`, `Stacks`, `Strings`, `Tries`, `Two Pointers`)
  before introducing a new one.
- Starters contain only the learner API, no `main`, assertions, or harness, and
  throw `UnsupportedOperationException("Implement <method>")`.
- `ExamplesTest.java` and `CorrectnessTest.java` use one plain `@Test` per
  case. Do not use `@ParameterizedTest`, `@TestFactory`, or `@Timeout` there:
  expected test counts come from counting `@Test` annotations, and a
  parameterized or dynamic test runs more tests than it declares, so the check
  reports an incomplete run. The verification runner already applies the
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

Every guide ID listed in `src/main/resources/guides/index.txt` (`GuideCatalog`)
must provide `src/main/resources/guides/<id>.json` with a matching `id`,
non-blank `title`, `summary`, and `lesson`, and a non-empty `exercises` array
of existing, non-duplicated exercise IDs.

- Lessons are original text in the `MarkdownHtml` subset (paragraphs, `- `
  bullets, inline code) and contain the label lines `How it works`,
  `When to reach for it`, `Common patterns`, and `Pitfalls`, each followed by
  bullets.
- Lessons teach the technique; they must not walk through a listed exercise's
  solution, which belongs to that exercise's hints.
- Guides never declare difficulty. Levels come from each exercise's manifest
  and must be `Easy`, `Medium`, or `Hard`.
- An exercise may appear in several guides. `GuideCatalogTest` validates
  invariants directly from `guides/index.txt` and manifests, and
  `python3 practice-plugin/scripts/catalog_tool.py validate` checks all
  exercises, guides, and tracks statically in under a second.

## Practice Track Content Rules

Every track ID listed in `src/main/resources/tracks/index.txt` (`TrackCatalog`)
must provide `src/main/resources/tracks/<id>.json` in the guide manifest schema
with a non-empty `exercises` array of existing, non-duplicated exercise IDs.
Tracks are hand-curated problem lists such as Blind 75 or a company list.

- Every summary must say the list is not an official or verified question
  list. Company tracks must also say they are approximate, and their notes must
  not claim specific questions.
- Notes are original text in the `MarkdownHtml` subset with the label lines
  `About this list`, `What to expect`, and `How to prepare`.
- Exercises on a track are original problems. Never copy or paraphrase
  third-party problem statements, and never scrape or copy third-party
  company-tag data (for example paid question-frequency lists) into a track.
- Track IDs must not collide with guide IDs. `TrackCatalogTest` validates
  invariants directly from `tracks/index.txt` and manifests, and
  `python3 practice-plugin/scripts/catalog_tool.py validate` checks all
  exercises and tracks statically in under a second.
