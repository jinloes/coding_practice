# Coding Practice Agent Guide

This file contains practical instructions for coding agents and contributors working in this repository.

## Scope

Apply these rules when editing code, tests, or build configuration in this workspace.

## Build And Test

Use the Gradle wrapper from the repository root (requires JDK 25 for the Gradle daemon when syncing or building the included plugin builds):

```bash
./gradlew testClasses
./gradlew :regex:test
./gradlew :regex:test --tests "com.jinloes.regex.PCRE2EngineTest"
./gradlew :practice-plugin:test --tests "com.jinloes.practice_plugin.catalog.ExerciseCatalogTest" -PcatalogOnly=pair-sum
./gradlew :system-design-plugin:check
```

## Coding And Test Conventions

- Use Java 17 for the root Gradle modules except `regex`, which uses Java 21. `practice-plugin` and `system-design-plugin` are isolated Gradle builds wired into root `settings.gradle` via `includeBuild(...)` and use a Java 25 toolchain.
- Use JUnit 5 annotations: `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@Disabled`.
- Use AssertJ assertions (`assertThat(...)`) instead of JUnit assertions.
- Do not introduce JUnit 4 or Hamcrest in tests.
- Keep package names under `com.jinloes.<module_name>`.
- Keep test classes package-private (no `public` modifier).

## regex Module Notes

- Uses `pcre4j` 1.0.0 with the JNA backend.
- Requires `libpcre2-8.dylib` from Homebrew on macOS.
- The `checkPcre2` Gradle task auto-installs `pcre2` via Homebrew before tests run.
- Test execution sets `jna.library.path` to the Homebrew library directory.
- `PCRE2Engine` should use `Pcre2CompileOption` and `Pcre2MatchOption` from `org.pcre4j.option`.
- In `findMatches()`, keep both byte offsets (UTF-8 extraction) and char offsets (pcre4j match input). Do not collapse them.

## Agent Context Budget

The tracked repository (~3.3 MB, roughly 850k tokens) cannot fit in one agent context window. About 2.5 MB of it is per-exercise plugin content and about 0.5 MB is plugin Java and TypeScript code.

- Scope work to one module. Repo-wide reviews or audits should run one module or area at a time (for example `practice-plugin/` code, then `system-design-plugin/` code, then the platform and regex modules) and merge findings afterward, rather than loading everything at once.
- Read documentation selectively: check markdown headings first (`^#{1,3} `) and read only the relevant line ranges of `CODE-MAP.md`, `ARCHITECTURE.md`, and `README.md` instead of loading full files upfront.
- Treat `practice-plugin/src/main/resources/exercises/<id>/` and `practice-plugin/src/test/resources/{reference,known-wrong}/<id>/` as per-exercise data. Read only the IDs a task names; never read or search the whole tree. `practice-plugin/src/main/resources/exercises/index.txt` lists every ID.
- Use cwd-independent globs when excluding content/build trees—`!**/resources/**` (or `!**/{exercises,reference,known-wrong,problems}/**`), `!**/build/**`, and `!**/node_modules/**`—so exclusions match whether running from the repository root or inside a plugin directory. Apply the same `paths` filter (plus a module prefix like `practice-plugin/**` when working from root) to IDE MCP `search_text`/`search_regex`/`search_file` calls, and pass whichever project root is open in IntelliJ (repo root or standalone plugin directory) as `projectPath`.
- Treat `system-design-plugin/src/main/resources/problems/<id>.json` as per-problem data in the same way: read only the problem IDs a task names, starting from `system-design-plugin/CODE-MAP.md` and `AGENTS.md`.
- Delegate full `./gradlew build`, `./gradlew test`, and `./gradlew check` runs to a `task` subagent (or pass `--quiet` / `--console=plain`) so build and test logs do not flood the main context window.
- Keep source and test files under about 20 KB so they can be read in one pass; split oversized files by responsibility.
- Avoid tests that restate catalog content (exercise or guide membership lists). Derive expectations from the manifests or assert invariants, so content changes do not require reading and editing large test files.

## Versioning Metadata

- Group: `com.jinloes.coding_practice`
- Version: `1.0-SNAPSHOT`
