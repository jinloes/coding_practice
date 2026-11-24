package com.jinloes.practice_plugin.run;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Example;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats actionable failure diagnostics (input, expected output, error, and learner stack frame)
 * when a solution crashes or fails during visible-example or custom-input execution.
 */
final class ExampleFailureDiagnosis {
    private static final Pattern EXAMPLE_LABEL = Pattern.compile("\\bExample\\s+(\\d{1,4})\\b");
    private static final Pattern RUNNER_LINE = Pattern.compile("ExampleRunner\\.java:(\\d{1,6})");
    private static final Pattern ASSERT_CALL = Pattern.compile("^assert[A-Z]\\w*\\(");
    private static final Pattern TEST_METHOD = Pattern.compile("@Test\\b[^\\{]*?\\bvoid\\s+(\\w+)\\s*\\(");
    private static final Pattern EXCEPTION_HEAD = Pattern.compile("Exception in thread \"[^\"]+\"\\s+(.+)");
    private static final Pattern BUT_WAS = Pattern.compile("\\bbut was:?\\s+(.+)");

    private ExampleFailureDiagnosis() {
    }

    static String describe(Exercise exercise, String runnerSource, String customInput, String processOutput) {
        String output = processOutput == null ? "" : processOutput;
        String error = extractErrorSummary(output);
        String actual = extractActualValue(output);
        String frame = FailureText.learnerFrame(output);
        String input = customInput == null ? "" : customInput.trim();

        if (!input.isEmpty()) {
            StringBuilder text = new StringBuilder("Failed on custom input:\n");
            text.append("  Input: ").append(input);
            appendErrorAndFrame(text, "", error, frame);
            return text.toString();
        }

        List<Example> examples = exercise == null ? List.of() : exercise.examples();
        int exampleNumber = resolveExampleNumber(runnerSource, output, examples.size());
        if (exampleNumber >= 1) {
            Example example = examples.get(exampleNumber - 1);
            StringBuilder text = new StringBuilder("Failed on Example ").append(exampleNumber).append(":\n");
            text.append("  Input:    ").append(example.input()).append('\n');
            text.append("  Expected: ").append(example.output());
            appendErrorAndFrame(text, actual, error, frame);
            return text.toString();
        }

        if (error.isEmpty() && examples.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        if (!error.isEmpty()) {
            text.append("Error: ").append(error);
            if (!frame.isEmpty()) {
                text.append("\n  at ").append(frame);
            }
        }
        if (!examples.isEmpty()) {
            if (!text.isEmpty()) {
                text.append('\n');
            }
            text.append("Visible examples:");
            for (int i = 0; i < examples.size(); i++) {
                Example example = examples.get(i);
                text.append("\n  Example ").append(i + 1)
                        .append(": Input: ").append(example.input())
                        .append(" | Expected: ").append(example.output());
            }
        }
        return text.toString();
    }

    /** Maps a JUnit {@code ExamplesTest} method name to its matching visible {@link Example}, or null. */
    static Example exampleForTest(Exercise exercise, String examplesTestSource, String testName) {
        if (exercise == null || examplesTestSource == null || testName == null) {
            return null;
        }
        String normalized = testName.endsWith("()") ? testName.substring(0, testName.length() - 2) : testName;
        Matcher matcher = TEST_METHOD.matcher(examplesTestSource);
        int index = 0;
        while (matcher.find()) {
            if (matcher.group(1).equals(normalized)) {
                return index < exercise.examples().size() ? exercise.examples().get(index) : null;
            }
            index++;
        }
        return null;
    }

    private static void appendErrorAndFrame(StringBuilder text, String actual, String error, String frame) {
        if (!actual.isEmpty()) {
            text.append("\n  Actual:   ").append(actual);
        } else if (!error.isEmpty()) {
            text.append("\n  Error:    ").append(error);
        }
        if (!frame.isEmpty()) {
            text.append("\n  at ").append(frame);
        }
    }

    private static int resolveExampleNumber(String runnerSource, String output, int exampleCount) {
        if (exampleCount <= 0) {
            return -1;
        }
        Matcher label = EXAMPLE_LABEL.matcher(output);
        if (label.find()) {
            int parsed = Integer.parseInt(label.group(1));
            if (parsed >= 1 && parsed <= exampleCount) {
                return parsed;
            }
        }
        int runnerLine = extractRunnerLine(output);
        if (runnerLine <= 0 || runnerSource == null || runnerSource.isBlank()) {
            return -1;
        }
        List<Integer> assertLines = mainAssertStartLines(runnerSource);
        if (assertLines.size() != exampleCount) {
            return -1;
        }
        for (int i = assertLines.size() - 1; i >= 0; i--) {
            if (runnerLine >= assertLines.get(i)) {
                return i + 1;
            }
        }
        return -1;
    }

    private static int extractRunnerLine(String output) {
        Matcher matcher = RUNNER_LINE.matcher(output);
        int last = -1;
        while (matcher.find()) {
            last = Integer.parseInt(matcher.group(1));
        }
        return last;
    }

    private static List<Integer> mainAssertStartLines(String runnerSource) {
        List<Integer> lines = new ArrayList<>();
        String[] all = runnerSource.split("\n", -1);
        boolean inMain = false;
        boolean pastArgsGuard = false;
        for (int i = 0; i < all.length; i++) {
            String trimmed = all[i].strip();
            if (!inMain) {
                if (trimmed.contains("public static void main(")) {
                    inMain = true;
                }
                continue;
            }
            if (!pastArgsGuard) {
                if ("}".equals(trimmed)) {
                    pastArgsGuard = true;
                }
                continue;
            }
            if (trimmed.contains("Examples passed") || "}".equals(trimmed)) {
                break;
            }
            if (ASSERT_CALL.matcher(trimmed).find()) {
                lines.add(i + 1);
            }
        }
        return lines;
    }

    private static String extractErrorSummary(String output) {
        for (String raw : output.split("\n")) {
            String line = raw.strip();
            Matcher head = EXCEPTION_HEAD.matcher(line);
            if (head.find()) {
                return head.group(1).strip();
            }
            if (line.startsWith("Caused by: ")) {
                return line.substring("Caused by: ".length()).strip();
            }
        }
        return "";
    }

    private static String extractActualValue(String output) {
        String[] lines = output.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].strip();
            Matcher butWas = BUT_WAS.matcher(line);
            if (butWas.find()) {
                return butWas.group(1).strip();
            }
            if ("Expecting actual:".equals(line) && i + 1 < lines.length) {
                return lines[i + 1].strip();
            }
            if ("Expecting actual not to be null".equals(line)) {
                return "null";
            }
        }
        return "";
    }
}
