package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void acceptsEmptyInput() {
        assertBalanced("");
    }

    @Test
    void handlesEachPairType() {
        assertBalanced("()");
        assertBalanced("[]");
        assertBalanced("{}");
    }

    @Test
    void rejectsAClosingDelimiterWithoutAnOpeningOne() {
        assertUnbalanced("]");
        assertUnbalanced("())");
    }

    @Test
    void rejectsAnUnclosedOpeningDelimiter() {
        assertUnbalanced("{{");
        assertUnbalanced("[{()}");
    }

    @Test
    void distinguishesDifferentDelimiterKinds() {
        assertUnbalanced("(]");
        assertUnbalanced("{[}]");
    }

    @Test
    void handlesAdjacentAndDeepSequences() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            input.append("([{".charAt(i % 3));
        }
        for (int i = 127; i >= 0; i--) {
            input.append(")]}".charAt(i % 3));
        }
        assertBalanced(input.toString());
    }

    @Test
    void rejectsADeepSequenceWithOneWrongCloser() {
        assertUnbalanced("{{[()]}}]");
    }

    @Test
    void handlesLongAdjacentPairs() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            input.append("()");
        }
        assertBalanced(input.toString());
    }

    private static void assertBalanced(String input) {
        assertThat(isBalanced(input))
                .as("isBalanced(%s) must accept this balanced input", quote(input))
                .isTrue();
    }

    private static void assertUnbalanced(String input) {
        assertThat(isBalanced(input))
                .as("isBalanced(%s) must reject this unbalanced input", quote(input))
                .isFalse();
    }

    private static boolean isBalanced(String input) {
        try {
            return Solution.isBalanced(input);
        } catch (Throwable thrown) {
            return fail("isBalanced(%s) threw %s".formatted(quote(input), thrown), thrown);
        }
    }

    private static String quote(String input) {
        return input.length() <= 60
                ? "\"" + input + "\""
                : "\"" + input.substring(0, 60) + "...\" (length " + input.length() + ")";
    }
}
