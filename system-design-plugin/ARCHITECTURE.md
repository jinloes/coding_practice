# System Design Practice Architecture

## Overview

The plugin has four parts:

- a bundled, read-only problem catalog;
- a durable attempt workspace in the IDE configuration directory;
- two React pages served to JCEF from the plugin jar: the tool window and the
  Excalidraw diagram editor;
- an interviewer that grades an attempt through the user's Copilot CLI.

It depends only on `com.intellij.modules.platform` and
`com.intellij.modules.jcef`, and shares no code with `practice-plugin`. Small
helpers (`OwnedDirectory`, `MarkdownHtml`) were copied deliberately.

## Layers

| Package | Responsibility | IDE dependencies |
|---|---|---|
| `catalog` | Loads and validates `problems/<id>.json` and the notes template | None |
| `platform` | Symlink-safe owned-directory creation, writes, and deletion | None |
| `workspace` | Attempt creation, lookup, ownership checks, fingerprints, feedback files | `PathManager` for the default root only |
| `state` | `DesignProgress` persistent state: selections, hints, verdicts, settings | Application service |
| `interview` | Diagram summary, prompt, feedback schema, Copilot CLI process, model list | Process handler, `EnvironmentUtil`; Copilot Java SDK |
| `web` | Page contract: asset URL mapping, page events, state JSON, and tool-window view assembly (`ToolWindowView`) | None |
| `ui` | Tool window, JCEF pages, diagram `FileEditor`, settings dialog | Full IDE |

Everything outside `ui` is unit tested with the coverage gate. `ui` needs a live
IDE and JCEF and is verified manually with `runIde`.

## Problems and attempts

A problem has an id, title, difficulty, summary, statement, exactly three hints,
and at least five interviewer-only expectations. Expectations are sent to the
interviewer only; `PageState` never serializes them, and feedback paraphrases
missed expectations as topic-level gaps rather than quoting them verbatim.

An attempt is `<config>/system-design-practice/attempts/<problem>/<attempt>/` with
`design.excalidraw`, `notes.md`, and `feedback/`. Attempt IDs are random. Every
read and write checks that the attempt directory is owned (no symlinks, real path
inside the attempts root). `ownerOfDiagram` is the only way a file is recognized as
an attempt diagram, so the Excalidraw editor never opens other `.excalidraw` files.

The fingerprint is a SHA-256 over the workspace revision, the problem id, and the
notes and diagram bytes. Stored feedback records the fingerprint it graded;
feedback is stale when the current fingerprint differs. The diagram editor writes
only when elements or files change, so panning or zooming does not make feedback
stale.

## Pages

Both pages are built by Vite from `web/` into `build/web/` and packaged under
`/web` in the jar. JCEF loads them from the private origin
`https://system-design.invalid/`. `WebAssetHandler` serves only safe paths with known
extensions that exist in the jar and cancels every other request, including CDN
and off-origin URLs. Main-frame navigation away from the origin and all popups are
cancelled; an explicit click on an http(s) link opens the system browser instead.
Excalidraw's fonts are bundled, and `EXCALIDRAW_ASSET_PATH` points at the origin.
Excalidraw's help, social, and library links are hidden.

The tool window page is stateless. Java computes a `PageState.View` off the EDT,
serializes it with `PageState.toJson`, and calls `window.designRender(state)`. The
page sends events through `window.designSend`, which `PageEvent.parse` validates.
A VFS listener on the attempts root re-renders so the stale flag follows edits.

The plugin injects `window.designSend` after each page load and then fires a
`designbridge` window event. The diagram editor page posts `ready` once that bridge
exists, receives `designLoad(scene, dark)`, and
posts sequenced whole-scene `save` (`{type, seq, scene}`) messages debounced by
300 ms. `PageEvent.parseEditor` rejects scenes over 10 MiB or that are not
Excalidraw JSON while preserving any extractable `seq`; `DiagramEditor`
re-checks ownership, ignores superseded sequences, writes through the VFS, and
reports the result with `designSaveResult(seq, error)`. A failed save keeps the
scene dirty and shows a banner. `designFlush(seq)` saves immediately and posts a
sequenced `flushed` (`{type, seq}`) message; once `ready` has arrived, the IDE
flushes, bounded to 2 seconds, before a diagram tab closes, before app exit, and
before feedback.

## Interviewer flow

1. Save all documents and flush open diagram editors for the attempt; if a flush
   times out or an open diagram has an unresolved save error, abort before
   invoking the CLI so stale disk content is never graded.
2. In a cancellable background task: fingerprint, read notes and diagram, and
   build a text `DiagramSummary` (labeled shapes with duplicate disambiguation,
   standalone text, directional and undirected connections, frames as sections;
   at most 500 elements).
3. `InterviewPrompt` embeds the statement, rubric, expectations, notes, and
   summary. Learner content is fenced with markers that are stripped from the
   content and bounded to 40,000 characters per section.
4. `CopilotInterviewer` runs the CLI and parses the JSON reply. Invalid output is
   kept as a raw fallback (at most 64 KiB) rather than discarded.
5. The feedback, fingerprint, model, and time are saved under `feedback/` (up to
   20 reviews per attempt are surfaced newest-first in the tool window), and the
   attempt's verdict is recorded in `DesignProgress`.

## Privacy and security boundary

Feedback is the plugin's only flow of learner content off the machine. It happens only on an
explicit **Get feedback** click and sends only the problem, rubric, expectations,
notes, and diagram summary, through the user's own installed and authenticated
Copilot CLI. The plugin stores no credentials. Listing models (see below) also
contacts GitHub through the CLI but carries no learner content.

The CLI must never act on the machine. It runs with every tool disabled: the
argument list is fixed (`-s`, `--no-custom-instructions`, `--disable-builtin-mcps`,
`--available-tools` set to a non-existent tool, `--no-ask-user`, and an optional
`--model`; never an allow flag), the prompt goes
through standard input, and the working directory is a fresh empty owned
directory that is deleted afterwards (abandoned ones are cleaned up on the next
run). Output is capped at 512 KiB; the process is killed on cancel, timeout, or
overflow. Only one request runs at a time.

## Model list

The settings dialog fills its Model dropdown with
`CopilotInterviewer.listModels`, which uses the Copilot Java SDK
(`com.github:copilot-sdk-java`, bundled with its Jackson jars). JNA is excluded:
it serves only the SDK's in-process mode, and an explicit `cliPath` always selects
stdio. The SDK launches `<cli> --no-custom-instructions --disable-builtin-mcps
--server --no-auto-update --log-level info --stdio` in a fresh owned run
directory, with the IDE's shell environment (`EnvironmentUtil`). The plugin calls
only `start()` and `listModels()`, so the traffic is `connect`, `models.list`, and
`runtime.shutdown`; the CLI also writes its process log. No session is created
and no learner content is sent. Calls are bounded to 20 seconds and poll a cancel
check; cancel or timeout force-stops the client (waiting up to 35 seconds for an
in-flight handshake). `CopilotModels` keeps enabled models with plain IDs, drops
duplicates, and caps the list at 200. The dialog runs each load on a pooled
thread, cancels it when a newer load starts or the dialog closes, and applies
only the latest result, keeping whatever model text is already typed.

The pages load no network resources, never see expectations, and render all
model text as plain text. Statement and hint Markdown is escaped by `MarkdownHtml`
before the page inserts it as HTML.
