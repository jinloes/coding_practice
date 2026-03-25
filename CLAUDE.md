# Coding Practice

Multi-module Gradle project for practicing data structures, algorithms, and regex engines in Java.

## Build

```bash
./gradlew build                        # build all modules
./gradlew :regex:test                  # test a specific module
./gradlew :regex:test --tests "com.jinloes.regex.PCRE2EngineTest"  # single test class
```

## Project Structure

Each topic is a Gradle submodule under its own directory.

| Module | Content |
|---|---|
| `simple_functions` | Tree traversal, sorting, dynamic programming |
| `data_structures` | HashTable, linked list, stack, queue implementations |
| `arrays`, `strings`, `sorting`, `searching` | Algorithm practice |
| `binary_trees`, `binary_search_tree`, `heaps` | Tree/heap practice |
| `graphs`, `greedy_algorithms`, `recursion` | Graph and algorithm practice |
| `regex` | Java regex, PCRE2 via pcre4j |
| `concurrency` | Threading and concurrency practice |
| `hash_tables`, `linked_lists`, `stacks`, `queues` | ADT practice |

## Language & Tooling

- **Java 17** for all modules (Java 21 for `regex`)
- **Gradle 9.3.1** with Spring dependency management plugin
- **JUnit 5 + AssertJ** for all tests — do not use JUnit 4 or Hamcrest
- Group: `com.jinloes.coding_practice`, version: `1.0-SNAPSHOT`

## Conventions

- Use JUnit 5 annotations: `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@Disabled`
- Use AssertJ fluent assertions (`assertThat(...)`) not JUnit assertions
- Package names follow `com.jinloes.<module_name>`
- Test classes are package-private (no `public` modifier)

## regex module specifics

- Uses **pcre4j 1.0.0** (JNA backend) — requires `libpcre2-8.dylib` from Homebrew
- The Gradle `checkPcre2` task auto-installs pcre2 via Homebrew on macOS before tests run
- `jna.library.path` is set to the Homebrew lib directory automatically in the test task
- `PCRE2Engine` uses `Pcre2CompileOption`/`Pcre2MatchOption` from `org.pcre4j.option` (moved in 1.0.0)
- `findMatches()` tracks both byte offsets (for substring extraction from UTF-8 bytes) and char offsets (for pcre4j's `match()` call) — do not collapse these