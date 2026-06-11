# Coding Practice Architecture

## Overview

This repository is a multi-module Gradle project for practicing algorithms, data structures, and regex engines in Java.

## Build System

- Build tool: Gradle 9.3.1
- Multi-module layout driven by root `settings.gradle`
- Spring dependency management plugin is used in the build

## Module Organization

Each topic is implemented as an independent Gradle submodule.

| Module Group | Modules | Responsibility |
|---|---|---|
| Core algorithm practice | `arrays`, `strings`, `sorting`, `searching`, `recursion`, `dynamic_programming`, `greedy`, `graphs` | Algorithm exercises and implementations |
| Data structure practice | `hash_tables`, `linked_lists`, `stacks_and_queues`, `heaps`, `binary_trees`, `binary_search_tree` | ADT and tree/heap implementations |
| Platform and language features | `concurrency`, `reflection`, `design` | Java platform, patterns, and API-focused practice |
| Regex engine work | `regex` | Java regex and PCRE2 integration via pcre4j |

## Language Strategy

- Java 17 is the default baseline across modules.
- The `regex` module uses Java 21 due to its dependency and integration requirements.

## regex Integration Architecture

- `regex` integrates with PCRE2 through `pcre4j` (JNA backend).
- Native dependency is resolved from Homebrew (`libpcre2-8.dylib`) on macOS.
- Build/test flow includes a pre-test verification step (`checkPcre2`) to ensure PCRE2 availability.
- Matching logic in `PCRE2Engine.findMatches()` uses dual indexing:
  - Byte offsets for UTF-8 byte-slice extraction.
  - Char offsets for `pcre4j` match invocation.

## Naming And Packaging

- Group ID: `com.jinloes.coding_practice`
- Version: `1.0-SNAPSHOT`
- Java packages follow `com.jinloes.<module_name>`
