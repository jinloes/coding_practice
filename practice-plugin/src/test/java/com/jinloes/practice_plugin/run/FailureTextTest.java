package com.jinloes.practice_plugin.run;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FailureTextTest {
    private static final String CORRECTNESS = "com.jinloes.practice.CorrectnessTest";
    private static final String EXAMPLES = "com.jinloes.practice.ExamplesTest";

    @Test
    void namesTheCaseTheWayItWasWritten() {
        assertThat(FailureText.humanize("rejectsADeepSequenceWithOneWrongCloser()"))
                .as("a standalone capital is its own word")
                .isEqualTo("rejects a deep sequence with one wrong closer");
    }

    @Test
    void humanizesNamesWithoutParenthesesAndWithDigits() {
        assertThat(FailureText.humanize("handlesAOneElementArray"))
                .isEqualTo("handles a one element array");
        assertThat(FailureText.humanize("usesLongArithmeticAtIntegerBoundaries"))
                .isEqualTo("uses long arithmetic at integer boundaries");
    }

    @Test
    void distinguishesVisibleExamplesFromHiddenCases() {
        assertThat(FailureText.render(EXAMPLES, "findsTypicalPair()", "T", "boom", ""))
                .as("learners are told which examples they could already see")
                .startsWith("Visible example: finds typical pair\n");
        assertThat(FailureText.render(CORRECTNESS, "findsTypicalPair()", "T", "boom", ""))
                .startsWith("Hidden case: finds typical pair\n");
    }

    @Test
    void showsTheThrownExceptionAndTheLineInTheLearnersSolution() {
        String trace = """
                java.util.EmptyStackException
                \tat java.base/java.util.Stack.peek(Stack.java:103)
                \tat java.base/java.util.Stack.pop(Stack.java:85)
                \tat com.jinloes.practice.Solution.isBalanced(Solution.java:25)
                \tat com.jinloes.practice.CorrectnessTest.assertUnbalanced(CorrectnessTest.java:71)
                \tat java.base/jdk.internal.reflect.Method.invoke(Method.java:568)
                \tat org.junit.platform.engine.support.Something.run(Something.java:12)""";

        String rendered = FailureText.render(CORRECTNESS, "rejectsADeepSequenceWithOneWrongCloser()",
                "java.util.EmptyStackException", "java.util.EmptyStackException", trace);

        assertThat(rendered).as("rendered failure")
                .isEqualTo("""
                        Hidden case: rejects a deep sequence with one wrong closer
                            java.util.EmptyStackException
                            at Solution.isBalanced(Solution.java:25)

                        """);
    }

    @Test
    void dropsJdkJunitAndReflectionFrames() {
        String trace = """
                java.lang.IllegalStateException
                \tat java.base/java.util.Stack.pop(Stack.java:85)
                \tat org.junit.platform.engine.Runner.run(Runner.java:9)
                \tat com.jinloes.practice.Solution.search(Solution.java:11)""";

        assertThat(FailureText.render(CORRECTNESS, "x()", "T", "m", trace))
                .as("only the learner's own frame is actionable")
                .doesNotContain("java.base", "org.junit", "Stack.java", "Runner.java")
                .contains("at Solution.search(Solution.java:11)");
    }

    @Test
    void fallsBackToTheTestFrameWhenTheSolutionIsNotOnTheStack() {
        String trace = """
                org.opentest4j.AssertionFailedError
                \tat com.jinloes.practice.CorrectnessTest.startsEmpty(CorrectnessTest.java:16)""";

        assertThat(FailureText.render(CORRECTNESS, "startsEmpty()", "T", "m", trace))
                .contains("at CorrectnessTest.startsEmpty(CorrectnessTest.java:16)");
    }

    @Test
    void liftsTheAssertJDescriptionOntoItsOwnLine() {
        String message = "[isBalanced(\"]\") must reject this unbalanced input] \n"
                + "Expecting value to be false but was true";

        assertThat(FailureText.render(CORRECTNESS, "rejectsAStrayCloser()", "T", message, ""))
                .as("the failing call belongs on its own line, not inside brackets")
                .isEqualTo("""
                        Hidden case: rejects a stray closer
                            isBalanced("]") must reject this unbalanced input
                            Expecting value to be false but was true

                        """);
    }

    @Test
    void keepsADescriptionThatContainsBracketsIntact() {
        String message = "[search([1, 2, 3], 4) must return -1 because the target is absent]\n"
                + "expected: -1 but was: 2";

        assertThat(FailureText.render(CORRECTNESS, "handlesAnAbsentTarget()", "T", message, ""))
                .as("array arguments contain brackets of their own")
                .contains("search([1, 2, 3], 4) must return -1 because the target is absent")
                .contains("expected: -1 but was: 2");
    }

    @Test
    void usesTheExceptionTypeWhenNoMessageWasGiven() {
        assertThat(FailureText.render(CORRECTNESS, "overflows()", "java.lang.ArithmeticException", "", ""))
                .as("an exception thrown without a message still has a type worth naming")
                .contains("java.lang.ArithmeticException");
    }

    @Test
    void rendersWithoutAFrameWhenNoLearnerCodeAppears() {
        assertThat(FailureText.render(CORRECTNESS, "x()", "T", "m", "at org.junit.Thing.go(Thing.java:1)"))
                .as("no invented frame")
                .isEqualTo("Hidden case: x\n    m\n\n");
    }
}
