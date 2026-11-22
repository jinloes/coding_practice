# System Design Practice

A standalone JetBrains plugin for practicing system design interviews. Pick an
original problem, sketch your design in an embedded Excalidraw editor, write
requirements, estimates, and APIs in `notes.md`, and on request get
interviewer-style feedback from GitHub Copilot through your own Copilot CLI.

The plugin depends only on the IntelliJ Platform and the bundled JCEF plugin, so
it runs in any JetBrains IDE (2026.2+) that has the embedded browser.

## Install and run

Requires JDK 25 (the Gradle toolchain); point `JAVA_HOME` at it or put its `java`
on `PATH`. Run from `system-design-plugin/`:

```bash
./gradlew runLocalIde   # start a sandbox IDE using the newest installed IntelliJ build
./gradlew runIde        # start a sandbox IDE using the pinned target platform
./gradlew buildPlugin   # build/distributions/system-design-plugin-1.0-SNAPSHOT.zip
```

Install the zip with **Settings | Plugins | ⚙ | Install Plugin from Disk**. The
build needs network access once to download Node.js and the npm packages for
the web pages.

## Workflow

1. Open the **System Design** tool window, browse the **Guides** tab for 8
   concise delivery, storage-matrix, API/security, and architecture guides, check
   your **Rubric readiness** averages across dimensions, or filter **Problems**
   by keyword, domain track, difficulty, and status (including `Needs retry`),
   use **Surprise me** for a random cold prompt, and set your **Target level**
   (`Mid-Level`, `Senior`, or `Staff+`).
2. Switch to **Ambiguous prompt** mode if you want to practice gathering
   requirements and scale assumptions before revealing the full specification, or
   jump to **Related guides** for the selected problem. Click **New attempt**
   (and optionally start the wall-clock **45-minute paced timer**). The plugin
   creates an attempt and opens its `design.excalidraw` diagram (preloaded with
   offline system-design stencils in the Library drawer) and its
   delivery-framework `notes.md`. Attempts persist across restarts and projects
   and can be renamed or deleted.
3. Reveal up to three hints, one at a time, and run through the level-calibrated
   **Pre-review self-check** before grading.
4. Click **Get feedback**. Open diagrams are saved first; the interviewer grades
   against your selected target level and returns a verdict (`Strong hire` to
   `No hire`), a 1–4 score with strengths, gaps, review-over-review score
   deltas, and targeted **Study guide** links for each rubric dimension,
   follow-up questions, and missed expectations.
5. Answer the interviewer's probing questions directly in the **Interactive
   follow-up round** box (optionally updating your diagram or notes first) and
   click **Submit follow-up answers** for a probed debrief. Earlier reviews on
   the same attempt remain available from the review history selector.

The rubric dimensions are: Requirements and scope, Capacity estimates, API and
data model, High-level architecture, Deep dives and scalability, Reliability and
tradeoffs, and Communication.

## Copilot CLI setup

Feedback uses the [GitHub Copilot CLI](https://docs.github.com/copilot) with
your own subscription:

```bash
npm install -g @github/copilot
copilot          # sign in once
```

Use **Interviewer settings** in the tool window to set the CLI path (empty means
`copilot` on `PATH`), an optional model, and the timeout (30–1800 seconds).

The **Model** field is a dropdown of the models your account can use. The plugin
asks the CLI for them, through the bundled
[Copilot Java SDK](https://github.com/github/copilot-sdk-java), when the dialog
opens, when the CLI path changes, and when you click **Refresh**. The status
line shows the model count or why listing failed. You can still type any model
ID; leave it empty for the CLI default.

## Privacy

Nothing leaves your machine until you click **Get feedback**. Then the plugin
sends only the problem statement, the rubric, the problem's interviewer
expectations, your notes, and a text summary of your diagram (labels and
connections, not the image) through your installed and signed-in Copilot CLI.
The CLI runs with every tool disabled, in an empty temporary directory, so it
cannot read files, run commands, or browse. The plugin stores no tokens, and its
pages load nothing from the network.

Opening **Interviewer settings** also starts the CLI in server mode, in an empty
temporary directory, to list models. That exchange is only the SDK handshake
(`connect`), `models.list`, and `runtime.shutdown`; it carries none of your
work. The CLI asks GitHub for your account's models and may write its usual
process log. Closing the dialog stops the CLI; one that hangs before the
handshake can take up to 35 seconds to stop.

## Storage

Attempts live under `<IDE config>/system-design-practice/attempts/<problem>/<attempt>/`
with `design.excalidraw`, `notes.md`, and `feedback/<timestamp>.json`. Progress
(selected attempt, custom labels and creation timestamps, hints, best verdict,
and strongest-verdict per-problem dimension scores) and interviewer settings are
stored in `system-design-practice.xml` in the IDE options directory and are not
synced.

## Limitations

- JCEF is required. If the IDE runtime has no JCEF, or the bundled JCEF plugin
  is disabled, the tool window shows a "requires JCEF" message.
- Images in diagrams are not graded, and a scene over 10 MiB is not saved.
- Only one feedback request runs at a time.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the design and
[CODE-MAP.md](CODE-MAP.md) for the code layout.
