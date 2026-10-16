package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void evaluatesSubtractionThenMultiplication() {
        check(8, "7", "3", "-", "2", "*");
    }

    @Test
    void divisionTruncatesTowardZero() {
        check(-3, "-7", "2", "/");
    }

    @Test
    void evaluatesNestedExpression() {
        check(51, "6", "4", "5", "+", "*", "3", "-");
    }

    private static void check(int expected, String... tokens) {
        String[] snapshot = tokens.clone();
        String call = "evaluate(%s)".formatted(Arrays.toString(tokens));
        assertThat(Solution.evaluate(tokens)).as(call).isEqualTo(expected);
        assertThat(tokens).as(call + " must not mutate its input").containsExactly(snapshot);
    }
}
