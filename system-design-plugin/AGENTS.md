# System Design Plugin Agent Guide

Rules for agents and contributors working inside `system-design-plugin/`. The
repository-root `AGENTS.md` also applies; this file takes precedence where the
rules overlap.

## Keep Documentation Synchronized

`ARCHITECTURE.md` and `CODE-MAP.md` are part of the implementation contract.
Read both before changing behavior, storage, the page contract, the interviewer,
or the build, and update them in the same change.

## Interviewer Invariants

- The Copilot CLI must run with every tool disabled. Never add `--allow-tool`,
  `--allow-all-tools`, `--allow-all-paths`, `--allow-url`, an MCP server, or any
  flag that grants file, shell, or network access to
  `CopilotInterviewer.arguments`. `CopilotInterviewerTest` asserts the exact
  argument list; update it only to make the review more restrictive.
- The CLI runs in a fresh, empty owned directory and receives the prompt on
  standard input. Never pass learner content as a command-line argument.
- Send only the problem, rubric, expectations, notes, and diagram summary. Do
  not add files, paths, project contents, or environment data to the prompt.
- Feedback runs only on an explicit user click. Never trigger it automatically.
- Never store credentials. Authentication belongs to the user's CLI.
- Use the Copilot Java SDK only for `CopilotInterviewer.listModels`, always with
  an explicit `cliPath` (stdio, so the excluded JNA is never needed). Never create
  an SDK session or send learner content through it; feedback stays on the
  restricted `-s` command line above.

## Page Invariants

- Pages load nothing from the network. Bundle every asset, and keep
  `WebAssetHandler` cancelling every off-origin request.
- Never serialize a problem's expectations into `PageState`.
- Render model output as plain text. Only `MarkdownHtml` output may be inserted
  as HTML.
- Keep `web/src/types.ts` in sync with `PageState` and `PageEvent`.

## Build, Test, and Coverage

Run from `system-design-plugin/` with its own wrapper: `./gradlew test`, then
`./gradlew check` before finishing. Coverage is gated at 98% line and 92% branch
outside `ui`; never lower it or add exclusions to pass. Use JUnit 5 with AssertJ,
keep test classes package-private, and do not add JUnit 4 or Hamcrest.

## Problem Content Rules

Each problem is `src/main/resources/problems/<id>.json`, with its id listed in
`DesignCatalog`. See "Adding a problem" in `CODE-MAP.md`.

- Write original problems. Never copy or paraphrase statements, diagrams, or
  solutions from books, courses, interview sites, or company question lists.
- Statements give functional requirements and concrete scale, and use only the
  `MarkdownHtml` subset (paragraphs, `- ` bullets, inline code).
- Exactly three hints, ordered from a nudge to a near-answer. Hints must not
  reveal the expectations verbatim.
- At least five expectations, each a specific, checkable point. Expectations are
  interviewer-only and never shown in the UI.
- Treat problem files as per-problem data: read only the IDs a task names.
- Tests must not restate problem content; derive checks from the manifests.

## Repository Hygiene

- Do not commit `.idea/`, `build/`, `web/node_modules/`, or local IDE paths.
- Keep source and test files under about 20 KB.
