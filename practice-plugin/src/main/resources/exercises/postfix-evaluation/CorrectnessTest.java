package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNumberIsItsOwnValue() {
        check(42, "42");
        check(-5, "-5");
        check(0, "0");
    }

    @Test
    void olderValueIsTheLeftOperand() {
        check(4, "7", "3", "-");
        check(-4, "3", "7", "-");
        check(3, "9", "3", "/");
        check(0, "3", "9", "/");
    }

    @Test
    void divisionTruncatesForEverySignCombination() {
        check(3, "7", "2", "/");
        check(-3, "7", "-2", "/");
        check(-3, "-7", "2", "/");
        check(3, "-7", "-2", "/");
    }

    @Test
    void operatorMinusIsNotConfusedWithNegativeNumbers() {
        check(-1, "-4", "-3", "-");
        check(-7, "-4", "-3", "+");
        check(12, "-4", "-3", "*");
    }

    @Test
    void evaluatesRightLeaningExpression() {
        check(21, "2", "3", "4", "5", "+", "*", "+", "8", "-");
        check(3, "20", "2", "3", "+", "/", "-1", "*", "7", "+");
    }

    @Test
    void handlesLargeIntermediateValues() {
        check(Integer.MAX_VALUE, "2147483647", "1", "*");
        check(Integer.MIN_VALUE, "-2147483648", "1", "/");
        check(2_000_000_000, "1000000", "2000", "*");
    }

    @Test
    void handlesADeepStackOfOperands() {
        String[] tokens = new String[39_999];
        for (int i = 0; i < 20_000; i++) {
            tokens[i] = "2";
        }
        for (int i = 20_000; i < tokens.length; i++) {
            tokens[i] = "+";
        }
        check(40_000, tokens);
    }

    @Test
    void handlesALongRunningChain() {
        String[] tokens = new String[20_001];
        tokens[0] = "1";
        for (int i = 1; i < tokens.length; i += 2) {
            tokens[i] = "1";
            tokens[i + 1] = "+";
        }
        check(10_001, tokens);
    }

    private static void check(int expected, String... tokens) {
        String[] snapshot = tokens.clone();
        String call = "evaluate(%s)".formatted(Arrays.toString(tokens));
        assertThat(Solution.evaluate(tokens)).as(call).isEqualTo(expected);
        assertThat(tokens).as(call + " must not mutate its input").containsExactly(snapshot);
    }
}
