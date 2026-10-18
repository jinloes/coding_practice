package com.jinloes.practice_plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The Results tab: a status banner and check history above the latest details or run output. */
final class ResultsView {
    private final JBLabel banner = new JBLabel();
    private final JBLabel history = new JBLabel();
    private final JBTextArea output = PracticeViews.textArea();
    private final JPanel panel = new JPanel(new BorderLayout());

    ResultsView() {
        banner.setFont(JBFont.label().asBold().biggerOn(1));
        banner.setIconTextGap(JBUI.scale(6));
        history.setForeground(UIUtil.getContextHelpForeground());
        history.setFont(JBFont.small());
        JPanel summary = new JPanel(new GridLayout(0, 1, 0, 2));
        summary.setBorder(JBUI.Borders.empty(8, 8, 0, 8));
        summary.add(banner);
        summary.add(history);
        panel.add(summary, BorderLayout.NORTH);
        panel.add(new JBScrollPane(output), BorderLayout.CENTER);
        clear();
    }

    JComponent component() {
        return panel;
    }

    void clear() {
        setBanner(null, "");
        history.setText("");
        output.setText("");
    }

    /** Shows the saved result of the attempt's last full check. */
    void showEntry(ManagedPracticeProgress.Entry entry) {
        showStatus(entry);
        String details = entry.details == null ? "" : entry.details;
        String complexity = entry.complexity == null || entry.complexity.isBlank() ? "" : "\n\n" + entry.complexity;
        setOutput(details + complexity);
    }

    /** Updates the banner and history from the saved entry without replacing the output. */
    void showStatus(ManagedPracticeProgress.Entry entry) {
        setBanner(icon(entry.status), label(entry.status));
        history.setText("Last full check: " + orNever(entry.checkedAt)
                + "    Most recent pass: " + orNever(entry.lastPassedAt));
    }

    void showRunning() {
        setBanner(AnimatedIcon.Default.INSTANCE, "Running...");
    }

    /** Shows the latest run or check output. */
    void showOutput(String text) {
        setOutput(text);
    }

    void showError(String text) {
        setBanner(AllIcons.General.Error, "Action failed");
        history.setText("");
        setOutput(text);
    }

    /** Shows the banner for a finished example or custom-input run, chosen from its finished text. */
    void showRunFinished(String text) {
        RunBanner run = runBanner(text);
        setBanner(switch (run.outcome()) {
            case STALE -> AllIcons.General.Warning;
            case PASSED -> AllIcons.RunConfigurations.TestPassed;
            case FAILED -> AllIcons.RunConfigurations.TestFailed;
            case ERROR -> AllIcons.RunConfigurations.TestError;
            case INFO -> AllIcons.General.Information;
        }, run.text());
        history.setText("");
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
        setBanner(AllIcons.General.Information, title);
        history.setText("");
        setOutput(text);
    }

    /** Marks the shown result as out of date with the solution and explains why. */
    void markStale(String reason) {
        setBanner(AllIcons.General.Warning, banner.getText().isEmpty() ? "Stale" : banner.getText() + " (stale)");
        output.append("\nSTALE: " + reason);
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

    private static Icon icon(String status) {
        return switch (status == null ? "" : status) {
            case "PASSED" -> AllIcons.RunConfigurations.TestPassed;
            case "ASSERTION_FAILED" -> AllIcons.RunConfigurations.TestFailed;
            case "COMPILATION_FAILED", "RUNTIME_ERROR", "TIMED_OUT" -> AllIcons.RunConfigurations.TestError;
            case "CANCELLED" -> AllIcons.RunConfigurations.TestIgnored;
            case "RUNNER_ERROR" -> AllIcons.General.Error;
            case "RUNNING" -> AnimatedIcon.Default.INSTANCE;
            default -> AllIcons.RunConfigurations.TestNotRan;
        };
    }

    private static String orNever(String timestamp) {
        return timestamp == null || timestamp.isBlank() ? "never" : timestamp;
    }

    private void setBanner(Icon icon, String text) {
        banner.setIcon(icon);
        banner.setText(text);
        banner.setVisible(!text.isEmpty());
    }

    private void setOutput(String text) {
        output.setText(text);
        output.setCaretPosition(0);
    }
}
