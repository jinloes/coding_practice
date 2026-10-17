package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversEdgesAndLargeTotals() {
        assertThat(Solution.maxProfit(new int[0])).isZero();
        assertThat(Solution.maxProfit(new int[]{37})).isEqualTo(37);
        assertThat(Solution.maxProfit(new int[]{8, 1, 8, 8})).isEqualTo(24);
        int large = Integer.MAX_VALUE;
        assertThat(Solution.maxProfit(new int[]{large, large, large, large})).isEqualTo(3L * large);
    }
    @Test
    void matchesBruteForceAndDoesNotMutate() {
        int[] payouts = {9, 3, 12, 4, 7, 8, 2, 11, 5};
        int[] before = payouts.clone();
        assertThat(Solution.maxProfit(payouts)).isEqualTo(brute(payouts, 0, 0));
        assertThat(payouts).containsExactly(before);
    }
    private static long brute(int[] a, int i, int streak) {
        if (i == a.length) return 0;
        long skip = brute(a, i + 1, 0);
        long take = streak == 2 ? Long.MIN_VALUE / 4 : a[i] + brute(a, i + 1, streak + 1);
        return Math.max(skip, take);
    }
}

