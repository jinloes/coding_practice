# Coding Practice

A Java repository for practicing algorithms, data structures, concurrency, regex engine work, cryptography, and system design. Algorithm and data structure problems live in the IntelliJ practice plugin (`practice-plugin`); system design pattern guides, reference implementations, and labs live in `system-design`, paired with interactive mock interviews in the System Design Practice plugin (`system-design-plugin`).

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

### System Design Patterns, Reference Code & Labs

- `system-design`

A pattern-first system design study guide (`docs/foundations.md`, `docs/interview-playbook.md`, 13 composable pattern deep-dives, and 8 worked post-attempt case studies), JDK-only reference implementations (token-bucket and sliding-window rate limiters, consistent hash ring, circuit breaker, transactional outbox + idempotent inbox, fenced leadership leases, and orchestrated sagas), and a Docker Compose PostgreSQL/Debezium/Kafka/Elasticsearch Change Data Capture lab (`labs/change-data-capture/`). Start at [system-design/README.md](system-design/README.md).

### System Design Interview Practice

- `system-design-plugin`

All system design interview problems live in the standalone JetBrains plugin under `system-design-plugin`. It presents original problems, lets you answer with an Excalidraw diagram and notes, and grades your answer like an interviewer through your own GitHub Copilot CLI. See [system-design-plugin/README.md](system-design-plugin/README.md).

## Tech Stack

- Java 17 for most root modules
- Java 21 for `regex`
- Java 25 toolchain for `practice-plugin` and `system-design-plugin` (and for the root Gradle daemon)
- Gradle 9.3.1 multi-module + composite build (`includeBuild`)
- JUnit Jupiter and AssertJ for tests

## Build And Test

Open the repository root in IntelliJ IDEA (using JDK 25 as the Gradle JVM) to work across all modules and both plugins in a single window, with shared sandbox run configurations in `.run/`.

Run commands from the repository root with `./gradlew`:

```bash
./gradlew check
./gradlew :regex:test
./gradlew :regex:test --tests "com.jinloes.regex.PCRE2EngineTest"
./gradlew :practice-plugin:check
./gradlew :system-design-plugin:check
```

## Notes About `regex`

- Uses `pcre4j` with the JNA backend
- On macOS, tests expect `libpcre2-8.dylib` from Homebrew (Windows/Linux hosts without native `libpcre2-8` can exclude `-x :regex:test`)
- The module includes a `checkPcre2` task to verify or install `pcre2` before tests run

## IntelliJ Practice Plugin

[`practice-plugin/`](practice-plugin/README.md) is an isolated IntelliJ IDEA
plugin build included in root `settings.gradle` via `includeBuild("practice-plugin")`.
It presents problems and hints, creates durable managed Java attempts that work
from any open project, and supports local correctness checks, native debugging,
and saved progress. Existing plugin-created practice projects can be imported
without changing their files. Run it from root (`./gradlew :practice-plugin:runIde`
or the shared **Algorithm Practice Sandbox (runIde)** configuration) or standalone
from `practice-plugin/`.

## System Design Practice Plugin

[`system-design-plugin/`](system-design-plugin/README.md) is an isolated
JetBrains plugin build included in root `settings.gradle` via
`includeBuild("system-design-plugin")`. It works in any JetBrains IDE with JCEF,
stores durable attempts (an Excalidraw diagram plus `notes.md`), and requests
interviewer-style feedback through the user's installed Copilot CLI with every
tool disabled. Run it from root (`./gradlew :system-design-plugin:runLocalIde`
or the shared **System Design Sandbox (runLocalIde)** configuration) or standalone from
`system-design-plugin/`.

## Project Layout

- Root `build.gradle` centralizes shared dependency management and test configuration for root submodules
- Root `settings.gradle` defines the root submodules and links `practice-plugin` and `system-design-plugin` via `includeBuild`
- Root `.run/` provides shared IntelliJ Gradle sandbox run configurations for both plugins
- Each module owns its own source and test code under `src/main/java` and `src/test/java`
