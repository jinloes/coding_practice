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
bash ./gradlew :concurrency:check :cryptography:check :reflection:check :regex:check :system-design:check --no-daemon --console=plain
```

The last command matches `.github/workflows/root-modules.yml`. It requires a
working native PCRE2 library; Ubuntu CI installs `libpcre2-8-0`. Root-only
changes do not replace the standalone plugin checks.

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

The tracked repository (~5.0 MB, roughly 1.28M tokens across ~2,650 files) cannot fit in one 264k agent context window. About 2.7 MB (~685k tokens) is per-exercise and per-problem content, ~270 KB (~69k tokens) is `package-lock.json` files, and ~1.4 MB (~355k tokens) is plugin Java and TypeScript source and tests (`practice-plugin` code + tests alone is ~820 KB / ~210k tokens).

- Scope work by module—and within `practice-plugin/`, by package (consult `practice-plugin/CODE-MAP.md` first rather than loading all plugin code and tests at once). Repo-wide reviews or audits should run one module or package slice at a time and merge findings afterward.
- Read documentation selectively: check markdown headings first (`^#{1,3} `) and read only the relevant line ranges of `CODE-MAP.md`, `ARCHITECTURE.md`, and `README.md` instead of loading full files upfront.
- Treat `practice-plugin/src/main/resources/exercises/<id>/` and `practice-plugin/src/test/resources/{reference,known-wrong}/<id>/` as per-exercise data, and `system-design-plugin/src/main/resources/problems/<id>.json` as per-problem data. Read only the IDs a task names; never read or search the whole tree. `exercises/index.txt` and `problems/index.txt` remain unignored and list every ID.
- Root `.ignore` hides per-exercise/problem content directories, `package-lock.json`, `*.excalidraw`, `build/`, `.gradle/`, and `.intellijPlatform/` from ripgrep-based `grep` searches by default (while explicit-path reads and greps on a named exercise/problem path still work). File `glob` tools and IDE MCP `search_text`/`search_regex`/`search_file` calls do not read `.ignore`: always pass narrow `paths` (for example `practice-plugin/src/main/java/**`, or cwd-independent exclusions `!**/resources/**`, `!**/build/**`, `!**/node_modules/**`, `!**/.gradle/**`, `!**/.intellijPlatform/**`), and pass whichever project root is open in IntelliJ (repo root or standalone plugin directory) as `projectPath`.
- Content authoring rules for `practice-plugin` exercises, guides, and tracks live in `.github/instructions/practice-plugin-content.instructions.md` so code-only tasks do not pay their context cost.
- Delegate full `./gradlew build`, `./gradlew test`, and `./gradlew check` runs to a `task` subagent (or pass `--quiet` / `--console=plain`) so build and test logs do not flood the main context window.
- Keep source and test files under about 20 KB so they can be read in one pass; split oversized files by responsibility.
- Avoid tests that restate catalog content (exercise or guide membership lists). Derive expectations from the manifests or assert invariants, so content changes do not require reading and editing large test files.

## Versioning Metadata

- Group: `com.jinloes.coding_practice`
- Version: `1.0-SNAPSHOT`
