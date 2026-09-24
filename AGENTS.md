# Coding Practice Agent Guide

This file contains practical instructions for coding agents and contributors working in this repository.

## Scope

Apply these rules when editing code, tests, or build configuration in this workspace.

## Build And Test

Use Gradle from the repository root.

```bash
./gradlew build
./gradlew :regex:test
./gradlew :regex:test --tests "com.jinloes.regex.PCRE2EngineTest"
```

## Coding And Test Conventions

- Use Java 17 for all modules except `regex`, which uses Java 21.
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

The tracked repository (~1.6 MB) is larger than one agent context window; the plugin alone fits comfortably.

- Scope work to one module. Repo-wide reviews or audits should run one module or area at a time (for example `practice-plugin/`, then the algorithm modules, then `system_design/`) and merge findings afterward, rather than loading everything at once.
- `system_design/*.excalidraw` diagrams are hidden from ripgrep-based search by the root `.ignore`. Open one directly only when the task is about that diagram.
- Keep source and test files under about 20 KB so they can be read in one pass; split oversized files by responsibility.

## Versioning Metadata

- Group: `com.jinloes.coding_practice`
- Version: `1.0-SNAPSHOT`
