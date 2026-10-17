package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsMiddleHiddenNumber() { assertThat(Solution.guessNumber(10, feedback(6))).isEqualTo(6); }
    @Test
    void handlesSingleCandidate() { assertThat(Solution.guessNumber(1, feedback(1))).isEqualTo(1); }
    @Test
    void findsValueNearUpperBound() { assertThat(Solution.guessNumber(100, feedback(99))).isEqualTo(99); }
    private static java.util.function.IntUnaryOperator feedback(int hidden) { return guess -> Integer.compare(hidden, guess); }
}

