package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyRangeWithoutCallingFeedback() {
        AtomicInteger calls = new AtomicInteger();
        assertThat(Solution.guessNumber(0, guess -> { calls.incrementAndGet(); return 0; })).isEqualTo(-1);
        assertThat(calls).hasValue(0);
    }
    @Test
    void findsBoundariesAndSeededTargets() {
        assertThat(Solution.guessNumber(50, feedback(1))).isEqualTo(1);
        for (int hidden : new int[]{2, 17, 64, 511, 1000}) assertThat(Solution.guessNumber(1000, feedback(hidden))).as("hidden %d", hidden).isEqualTo(hidden);
    }
    @Test
    void usesLogarithmicFeedbackCalls() {
        AtomicInteger calls = new AtomicInteger();
        int found = Solution.guessNumber(1_000_000, guess -> { calls.incrementAndGet(); return Integer.compare(734_321, guess); });
        assertThat(found).isEqualTo(734_321);
        assertThat(calls.get()).isLessThanOrEqualTo(25);
    }
    private static java.util.function.IntUnaryOperator feedback(int hidden) { return guess -> Integer.compare(hidden, guess); }
}

