# Coding Practice

A Java repository for practicing algorithms, data structures, concurrency, and regex engine work. Algorithm and data structure problems live in the IntelliJ practice plugin (`practice-plugin`).

## What Is In This Repository

The project is organized as independent Gradle submodules so each topic can evolve separately while still sharing common dependency management and test conventions.

### Algorithm And Data Structure Practice

All algorithm, data structure, and object-design problems live in the IntelliJ practice plugin under `practice-plugin`. Each exercise is tagged with a topic and difficulty, grouped by technique in study guides, and collected into curated practice tracks. See [practice-plugin/README.md](practice-plugin/README.md).

### Java Platform Modules

- `concurrency`
- `reflection`

These modules focus on Java APIs and language features.

### Regex Module

- `regex`

This module experiments with regex engines beyond the JDK implementation and includes PCRE2 integration through `pcre4j`.

### Cryptography And Key Management Study Guide

- `cryptography`

A four-week study plan for key management, cryptography, secrets management, and PKI, with JDK-only reference implementations (AES-GCM, HKDF, envelope encryption, an in-memory KMS, Ed25519/X25519, Shamir secret sharing) and tests. Start at [cryptography/README.md](cryptography/README.md).

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

## IntelliJ Practice Plugin

[`practice-plugin/`](practice-plugin/README.md) is an independent IntelliJ IDEA
plugin build for practicing original Java algorithm and data-structure exercises.
It presents problems and hints, creates durable managed Java attempts that work
from any open project, and supports local correctness checks, native debugging,
and saved progress. Existing plugin-created practice projects can be imported
without changing their files. It is intentionally not included in the root
multi-module build.

## Project Layout

- Root `build.gradle` centralizes shared dependency management and test configuration
- Root `settings.gradle` defines the active Gradle submodules
- Each module owns its own source and test code under `src/main/java` and `src/test/java`

## Current Validation Status

The repository build currently fails in the `concurrency` module because `ParallelStreamTest` uses try-with-resources with executor types that are not `AutoCloseable` under the configured Java toolchain. This README update does not change that behavior.
