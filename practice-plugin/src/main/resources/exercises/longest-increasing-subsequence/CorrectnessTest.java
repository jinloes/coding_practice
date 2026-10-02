package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesAnEmptyArray() {
        assertThat(Solution.lengthOfLis(new int[0])).isEqualTo(0);
    }

    @Test
    void handlesASingleElement() {
        assertThat(Solution.lengthOfLis(new int[]{-5})).isEqualTo(1);
    }

    @Test
    void handlesAnAlreadyIncreasingArray() {
        assertThat(Solution.lengthOfLis(new int[]{-3, 0, 2, 9, 40})).isEqualTo(5);
    }

    @Test
    void ignoresDuplicatesInsideAnIncreasingRun() {
        assertThat(Solution.lengthOfLis(new int[]{1, 2, 2, 3, 3, 4})).isEqualTo(4);
    }

    @Test
    void handlesExtremeIntValues() {
        assertThat(Solution.lengthOfLis(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, Integer.MAX_VALUE}))
                .isEqualTo(3);
        assertThat(Solution.lengthOfLis(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE})).isEqualTo(1);
    }

    @Test
    void picksALaterBetterChain() {
        assertThat(Solution.lengthOfLis(new int[]{10, 11, 12, 1, 2, 3, 4})).isEqualTo(4);
        assertThat(Solution.lengthOfLis(new int[]{2, 5, 3, 7, 11, 8, 10, 13, 6})).isEqualTo(6);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {5, 1, 4, 2, 3};
        Solution.lengthOfLis(numbers);
        assertThat(numbers).containsExactly(5, 1, 4, 2, 3);
    }

    @Test
    void agreesWithTheQuadraticDefinition() {
        int[] numbers = new int[200];
        long seed = 12345;
        for (int i = 0; i < numbers.length; i++) {
            seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
            numbers[i] = (int) (seed % 50) - 25;
        }
        int[] best = new int[numbers.length];
        int expected = 0;
        for (int i = 0; i < numbers.length; i++) {
            best[i] = 1;
            for (int j = 0; j < i; j++) {
                if (numbers[j] < numbers[i]) {
                    best[i] = Math.max(best[i], best[j] + 1);
                }
            }
            expected = Math.max(expected, best[i]);
        }
        assertThat(Solution.lengthOfLis(numbers)).isEqualTo(expected);
    }
}
