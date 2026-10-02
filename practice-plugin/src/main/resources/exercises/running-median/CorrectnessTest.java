package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyStreamHasNoMedian() {
        assertThatThrownBy(() -> new Solution().median()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void singleValueIsItsOwnMedian() {
        Solution stream = new Solution();
        stream.add(-8);
        assertThat(stream.median()).as("median of [-8]").isEqualTo(-8.0);
        assertThat(stream.median()).as("median must not change when asked twice").isEqualTo(-8.0);
    }

    @Test
    void evenCountAveragesTheTwoMiddleValues() {
        Solution stream = new Solution();
        for (int value : new int[]{7, 1, 3, 9}) {
            stream.add(value);
        }
        assertThat(stream.median()).as("median of [7, 1, 3, 9]").isEqualTo(5.0);
    }

    @Test
    void averageCanBeFractional() {
        Solution stream = new Solution();
        stream.add(1);
        stream.add(2);
        assertThat(stream.median()).as("median of [1, 2]").isEqualTo(1.5);
    }

    @Test
    void handlesAscendingInput() {
        Solution stream = new Solution();
        for (int value = 1; value <= 9; value++) {
            stream.add(value);
            assertThat(stream.median()).as("median after adding 1 through %d", value)
                    .isEqualTo(expectedMedian(range(1, value)));
        }
    }

    @Test
    void handlesDescendingInput() {
        Solution stream = new Solution();
        List<Integer> seen = new ArrayList<>();
        for (int value = 9; value >= 1; value--) {
            stream.add(value);
            seen.add(value);
            assertThat(stream.median()).as("median after adding 9 down to %d", value)
                    .isEqualTo(expectedMedian(seen));
        }
    }

    @Test
    void handlesAllEqualValues() {
        Solution stream = new Solution();
        for (int i = 0; i < 6; i++) {
            stream.add(42);
            assertThat(stream.median()).as("median after %d copies of 42", i + 1).isEqualTo(42.0);
        }
    }

    @Test
    void medianDoesNotOverflowNearIntegerLimits() {
        Solution stream = new Solution();
        stream.add(Integer.MAX_VALUE);
        stream.add(Integer.MAX_VALUE - 1);
        assertThat(stream.median()).as("median of [MAX_VALUE, MAX_VALUE - 1]").isEqualTo(2147483646.5);
        Solution low = new Solution();
        low.add(Integer.MIN_VALUE);
        low.add(Integer.MIN_VALUE + 1);
        assertThat(low.median()).as("median of [MIN_VALUE, MIN_VALUE + 1]").isEqualTo(-2147483647.5);
    }

    @Test
    void handlesExtremesOnBothSides() {
        Solution stream = new Solution();
        stream.add(Integer.MIN_VALUE);
        stream.add(Integer.MAX_VALUE);
        assertThat(stream.median()).as("median of [MIN_VALUE, MAX_VALUE]").isEqualTo(-0.5);
    }

    @Test
    void matchesASortedReferenceOnPseudoRandomInput() {
        Solution stream = new Solution();
        List<Integer> seen = new ArrayList<>();
        long state = 12345;
        for (int i = 0; i < 2000; i++) {
            state = (state * 6364136223846793005L + 1442695040888963407L);
            int value = (int) (state >>> 40) - 8_000_000;
            stream.add(value);
            seen.add(value);
            if (i % 37 == 0 || i > 1990) {
                assertThat(stream.median()).as("median after %d pseudo-random values", i + 1)
                        .isEqualTo(expectedMedian(seen));
            }
        }
    }

    @Test
    void streamsAreIndependent() {
        Solution first = new Solution();
        Solution second = new Solution();
        first.add(100);
        second.add(1);
        assertThat(first.median()).as("median of the first stream").isEqualTo(100.0);
        assertThat(second.median()).as("median of the second stream").isEqualTo(1.0);
    }

    private static List<Integer> range(int from, int to) {
        List<Integer> values = new ArrayList<>();
        for (int value = from; value <= to; value++) {
            values.add(value);
        }
        return values;
    }

    private static double expectedMedian(List<Integer> values) {
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        }
        return ((long) sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
    }
}
