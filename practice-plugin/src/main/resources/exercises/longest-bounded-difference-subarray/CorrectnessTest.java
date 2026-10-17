package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() { assertThat(Solution.longestSubarray(new int[0], 3)).isZero(); }

    @Test
    void allowsWholeArrayWhenLimitIsWide() {
        assertThat(Solution.longestSubarray(new int[]{4, 2, 9, 1}, 20)).isEqualTo(4);
    }

    @Test
    void usesLongDifferenceAtIntegerEdges() {
        assertThat(Solution.longestSubarray(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, 0))
                .isEqualTo(2);
    }

    @Test
    void matchesBruteForceOnSeededData() {
        Random random = new Random(123);
        int[] numbers = new int[80];
        for (int i = 0; i < numbers.length; i++) numbers[i] = random.nextInt(31) - 15;
        assertThat(Solution.longestSubarray(numbers, 8)).isEqualTo(brute(numbers, 8));
    }

    private static int brute(int[] numbers, int limit) {
        int best = 0;
        for (int left = 0; left < numbers.length; left++) {
            int min = numbers[left], max = numbers[left];
            for (int right = left; right < numbers.length; right++) {
                min = Math.min(min, numbers[right]); max = Math.max(max, numbers[right]);
                if ((long) max - min <= limit) best = Math.max(best, right - left + 1);
            }
        }
        return best;
    }
}
