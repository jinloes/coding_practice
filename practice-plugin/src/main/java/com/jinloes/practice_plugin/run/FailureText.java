package com.jinloes.practice_plugin.run;

/**
 * Renders one JUnit failure as text a learner can act on: which case failed, what went wrong, and
 * the line in their own code that produced it.
 *
 * <p>The raw JUnit XML is written for tools, not people. It identifies a case by fully qualified
 * class and camel-case method name, repeats the exception message twice, and carries a stack trace
 * whose frames are mostly JDK, JUnit, and reflection internals the learner cannot act on. This
 * keeps the parts that explain the failure and drops the rest.
 */
final class FailureText {
    private FailureText() {}

    static String render(String className, String testName, String type, String message, String trace) {
        StringBuilder text = new StringBuilder(origin(className))
                .append(": ")
                .append(humanize(testName))
                .append('\n');
        for (String line : reason(type, message).split("\n", -1)) {
            String stripped = line.stripTrailing();
            if (!stripped.isBlank()) {
                text.append("    ").append(stripped).append('\n');
            }
        }
        String frame = learnerFrame(trace);
        if (!frame.isEmpty()) {
            text.append("    at ").append(frame).append('\n');
        }
        return text.append('\n').toString();
    }

    /**
     * Turns a camel-case test method name into the sentence it was written as, so the report names
     * the case the same way the learner would say it out loud.
     */
    static String humanize(String testName) {
        String name = testName.endsWith("()") ? testName.substring(0, testName.length() - 2) : testName;
        StringBuilder words = new StringBuilder(name.length() + 8);
        for (int i = 0; i < name.length(); i++) {
            char letter = name.charAt(i);
            boolean boundary = i > 0 && Character.isUpperCase(letter) && isWordStart(name, i);
            if (boundary) {
                words.append(' ');
            }
            words.append(boundary || i == 0 ? Character.toLowerCase(letter) : letter);
        }
        return words.toString();
    }

    private static boolean isWordStart(String name, int index) {
        if (!Character.isUpperCase(name.charAt(index - 1))) {
            return true;
        }
        // A capital between a capital and a lowercase starts a word, as the D in "rejectsADeepSequence".
        return index + 1 < name.length() && Character.isLowerCase(name.charAt(index + 1));
    }

    private static String origin(String className) {
        if (className.endsWith("ExamplesTest")) {
            return "Visible example";
        }
        if (className.endsWith("CorrectnessTest")) {
            return "Hidden case";
        }
        return className.substring(className.lastIndexOf('.') + 1);
    }

    /**
     * AssertJ renders an {@code as(...)} description as a bracketed prefix. Unwrapping it puts the
     * call that failed on its own line instead of burying it in punctuation.
     */
    private static String reason(String type, String message) {
        String text = message == null ? "" : message.strip();
        if (text.isBlank()) {
            return type == null ? "" : type.strip();
        }
        if (text.startsWith("[")) {
            int end = text.indexOf("]\n");
            int width = 2;
            if (end < 0) {
                end = text.indexOf("] \n");
                width = 3;
            }
            if (end > 0) {
                return text.substring(1, end) + "\n" + text.substring(end + width);
            }
        }
        return text;
    }

    /**
     * Returns the type of the exception a failure was caused by, or an empty string when the
     * failure has no cause. A helper that reports a thrown exception together with its input wraps
     * the original in an {@link AssertionError}, so the cause chain is what still distinguishes a
     * crash from a wrong answer.
     */
    static String causeType(String trace) {
        if (trace == null) {
            return "";
        }
        for (String line : trace.split("\n")) {
            String caused = line.strip();
            if (caused.startsWith("Caused by: ")) {
                caused = caused.substring("Caused by: ".length());
                int detail = caused.indexOf(':');
                return detail < 0 ? caused : caused.substring(0, detail);
            }
        }
        return "";
    }

    /**
     * Returns the deepest frame in the learner's own files. Their solution is where they can make a
     * change, so it is the only frame worth printing.
     */
    private static String learnerFrame(String trace) {
        if (trace == null) {
            return "";
        }
        String fallback = "";
        for (String line : trace.split("\n")) {
            String frame = line.strip();
            if (!frame.startsWith("at com.jinloes.practice.")) {
                continue;
            }
            frame = frame.substring("at com.jinloes.practice.".length());
            if (frame.contains("(Solution.java:")) {
                return frame;
            }
            if (fallback.isEmpty()) {
                fallback = frame;
            }
        }
        return fallback;
    }
}
