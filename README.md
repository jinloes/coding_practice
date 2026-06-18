# Coding Practice

A multi-module Java repository for practicing algorithms, data structures, concurrency, design exercises, and regex engine work.

## What Is In This Repository

The project is organized as independent Gradle submodules so each topic can evolve separately while still sharing common dependency management and test conventions.

### Algorithm And Data Structure Modules

- `arrays`
- `bit_manipulation`
- `strings`
- `linked_lists`
- `stacks_and_queues`
- `binary_trees`
- `binary_search_tree`
- `heaps`
- `searching`
- `hash_tables`
- `recursion`
- `greedy`
- `graphs`
- `dynamic_programming`

These modules contain standalone implementations and tests for common interview-style problems and foundational computer science exercises.

### Java Platform And Design Modules

- `concurrency`
- `reflection`
- `design`

These modules focus on Java APIs, language features, and design-oriented practice problems.

### Regex Module

- `regex`

This module experiments with regex engines beyond the JDK implementation and includes PCRE2 integration through `pcre4j`.

### System Design Assets

- `system_design`

This directory contains design diagrams and explorations. It is documentation-oriented and is not currently part of the Gradle multi-module build.

## Tech Stack

- Java 17 for most modules
- Java 21 for `regex`
- Gradle multi-module build
- JUnit Jupiter and AssertJ for tests

## Build And Test

Run commands from the repository root:

```bash
gradle build
gradle :regex:test
gradle :regex:test --tests "com.jinloes.regex.PCRE2EngineTest"
```

## Notes About `regex`

- Uses `pcre4j` with the JNA backend
- On macOS, tests expect `libpcre2-8.dylib` from Homebrew
- The module includes a `checkPcre2` task to verify or install `pcre2` before tests run

## Project Layout

- Root `build.gradle` centralizes shared dependency management and test configuration
- Root `settings.gradle` defines the active Gradle submodules
- Each module owns its own source and test code under `src/main/java` and `src/test/java`

## Current Validation Status

The repository build currently fails in the `concurrency` module because `ParallelStreamTest` uses try-with-resources with executor types that are not `AutoCloseable` under the configured Java toolchain. This README update does not change that behavior.
