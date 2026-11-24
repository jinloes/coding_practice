package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.state.ManagedPracticeProgress;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The Results tab: structured verdict, timestamps, complexity scaling, failure diagnosis, and raw output. */
final class ResultsView {
    private final PageView view;

    private String tone = "idle";
    private String banner = "";
    private String checkedAt = "";
    private String lastPassedAt = "";
    private String output = "";
    private String staleReason = "";
    private boolean askCoachEnabled;
    private boolean displayedResult;
    private boolean stale;

    ResultsView() {
        this(new PageView() {
            private final JPanel placeholder = new JPanel();

            @Override
            public JComponent component() {
                return placeholder;
            }

            @Override
            public void show(PracticePage page) {
            }
        });
    }

    ResultsView(PageView view) {
        this.view = Objects.requireNonNull(view, "view");
        clear();
    }

    void setAskCoachEnabled(boolean enabled) {
        if (askCoachEnabled != enabled) {
            askCoachEnabled = enabled;
            render();
        }
    }

    boolean isAskCoachEnabled() {
        return askCoachEnabled;
    }

    JComponent component() {
        return view.component();
    }

    void setActive(boolean active) {
        view.setActive(active);
    }

    void clear() {
        displayedResult = false;
        stale = false;
        tone = "idle";
        banner = "";
        checkedAt = "";
        lastPassedAt = "";
        staleReason = "";
        output = "";
        render();
    }

    /** Shows the saved result of the attempt's last full check. */
    void showEntry(ManagedPracticeProgress.Entry entry) {
        displayedResult = !entry.checkedFingerprint.isEmpty();
        stale = false;
        staleReason = "";
        tone = toneForStatus(entry.status);
        banner = label(entry.status);
        checkedAt = nonBlank(entry.checkedAt);
        lastPassedAt = nonBlank(entry.lastPassedAt);
        String details = entry.details == null ? "" : entry.details;
        String complexity = "PASSED".equals(entry.status)
                && entry.complexity != null && !entry.complexity.isBlank()
                ? "\n\n" + entry.complexity
                : "";
        output = details + complexity;
        render();
    }

    /** Updates the banner and history from the saved entry without replacing the output. */
    void showStatus(ManagedPracticeProgress.Entry entry) {
        displayedResult = !entry.checkedFingerprint.isEmpty();
        stale = false;
        staleReason = "";
        tone = toneForStatus(entry.status);
        banner = label(entry.status);
        checkedAt = nonBlank(entry.checkedAt);
        lastPassedAt = nonBlank(entry.lastPassedAt);
        render();
    }

    void showRunning() {
        displayedResult = false;
        stale = false;
        staleReason = "";
        tone = "running";
        banner = "Running...";
        render();
    }

    /** Shows the latest run or check output. */
    void showOutput(String text) {
        output = text == null ? "" : text;
        render();
    }

    void showError(String text) {
        displayedResult = false;
        stale = false;
        staleReason = "";
        tone = "error";
        banner = "Action failed";
        checkedAt = "";
        lastPassedAt = "";
        output = text == null ? "" : text;
        render();
    }

    /** Shows the banner for a finished example or custom-input run, chosen from its finished text. */
    void showRunFinished(String text) {
        displayedResult = true;
        RunBanner run = runBanner(text);
        stale = run.outcome() == RunOutcome.STALE;
        staleReason = stale ? "Files changed during the run; run the current solution again." : "";
        tone = switch (run.outcome()) {
            case STALE -> "stale";
            case PASSED -> "passed";
            case FAILED -> "failed";
            case ERROR -> "error";
            case INFO -> "info";
        };
        banner = run.text();
        checkedAt = "";
        lastPassedAt = "";
        render();
    }

    enum RunOutcome { STALE, PASSED, FAILED, INFO, ERROR }

    record RunBanner(RunOutcome outcome, String text) {}

    private static final Pattern EXIT_CODE = Pattern.compile("session finished \\(exit (-?\\d+)\\)");

    /** Maps a finished run's text to its banner; the text contract comes from {@code RunDecisions}. */
    static RunBanner runBanner(String text) {
        String finished = text == null ? "" : text;
        if (finished.startsWith("STALE:")) {
            return new RunBanner(RunOutcome.STALE, "Example result is stale");
        }
        Matcher exit = EXIT_CODE.matcher(finished);
        if (exit.find()) {
            String code = exit.group(1);
            boolean succeeded = "0".equals(code);
            if (finished.contains("Example session finished (exit ")) {
                return succeeded ? new RunBanner(RunOutcome.PASSED, "Examples passed")
                        : new RunBanner(RunOutcome.FAILED,
                        "Examples did not finish successfully (exit " + code + "); see the Run console");
            }
            if (finished.contains("Input session finished (exit ")) {
                return succeeded
                        ? new RunBanner(RunOutcome.INFO, "Input run finished; see the Run console for the output")
                        : new RunBanner(RunOutcome.ERROR,
                        "Input run did not complete (exit " + code + "); see the Run console");
            }
        }
        if (finished.startsWith("Example compilation") || finished.startsWith("Example launch")
                || finished.contains("example runner is not owned")) {
            return new RunBanner(RunOutcome.ERROR, "Examples could not run; see details below");
        }
        return new RunBanner(RunOutcome.INFO, "Run finished");
    }

    void showMessage(String title, String text) {
        displayedResult = false;
        stale = false;
        staleReason = "";
        tone = "info";
        banner = title == null ? "" : title;
        checkedAt = "";
        lastPassedAt = "";
        output = text == null ? "" : text;
        render();
    }

    boolean hasDisplayedResult() {
        return displayedResult;
    }

    String bannerText() {
        return banner;
    }

    String outputText() {
        return output;
    }

    /** Marks the shown result as out of date with the solution and explains why, at most once per result. */
    void markStale(String reason) {
        if (stale) {
            return;
        }
        stale = true;
        staleReason = reason == null ? "" : reason;
        tone = "stale";
        banner = banner.isEmpty() ? "Stale" : banner + " (stale)";
        output = output + (output.isEmpty() ? "" : "\n") + "STALE: " + reason;
        render();
    }

    /** The banner text for a {@code CheckResult.Status} name; unknown names are shown as they are. */
    static String label(String status) {
        return switch (status == null ? "" : status) {
            case "NOT_RUN", "" -> "Not checked yet";
            case "RUNNING" -> "Running...";
            case "PASSED" -> "Passed";
            case "ASSERTION_FAILED" -> "Failed: wrong answer";
            case "COMPILATION_FAILED" -> "Failed: does not compile";
            case "RUNTIME_ERROR" -> "Failed: runtime error";
            case "TIMED_OUT" -> "Failed: timed out";
            case "CANCELLED" -> "Cancelled";
            case "RUNNER_ERROR" -> "Runner error";
            default -> status;
        };
    }

    private static String toneForStatus(String status) {
        return switch (status == null ? "" : status) {
            case "PASSED" -> "passed";
            case "ASSERTION_FAILED" -> "failed";
            case "COMPILATION_FAILED", "RUNTIME_ERROR", "TIMED_OUT", "RUNNER_ERROR" -> "error";
            case "RUNNING" -> "running";
            case "CANCELLED" -> "info";
            default -> "idle";
        };
    }

    private static String nonBlank(String timestamp) {
        return timestamp == null || timestamp.isBlank() ? "" : timestamp;
    }

    private void render() {
        view.show(new PracticePage.Results(
                tone, banner, checkedAt, lastPassedAt, stale, staleReason, askCoachEnabled, output));
    }
}
