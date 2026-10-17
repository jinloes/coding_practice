package com.jinloes.practice;



import org.junit.jupiter.api.Test;



import java.util.Random;



import static org.assertj.core.api.Assertions.assertThat;



class CorrectnessTest {

    @Test

    void returnsMinusOneWhenOnlyOneMatchExists() { assertThat(Solution.minSumOfLengths(new int[]{1, 2, 4}, 3)).isEqualTo(-1); }

    @Test

    void handlesNegativeValues() { assertThat(Solution.minSumOfLengths(new int[]{2, -1, 2, 3, -3, 3}, 3)).isEqualTo(2); }

    @Test

    void requiresNonOverlappingChoices() { assertThat(Solution.minSumOfLengths(new int[]{1, 1, 1}, 2)).isEqualTo(-1); }

    @Test

    void matchesBruteForceOnSeededData() {

        Random random = new Random(7);

        int[] numbers = new int[40];

        for (int i = 0; i < numbers.length; i++) numbers[i] = random.nextInt(7) - 3;

        assertThat(Solution.minSumOfLengths(numbers, 2)).isEqualTo(brute(numbers, 2));

    }

    private static int brute(int[] a, int target) {

        int ans = Integer.MAX_VALUE / 4;

        for (int i = 0; i < a.length; i++) { int s1 = 0; for (int j = i; j < a.length; j++) { s1 += a[j]; if (s1 == target) {

            for (int k = j + 1; k < a.length; k++) { int s2 = 0; for (int l = k; l < a.length; l++) { s2 += a[l]; if (s2 == target) ans = Math.min(ans, j - i + 1 + l - k + 1); } }

        } } }

        return ans >= Integer.MAX_VALUE / 4 ? -1 : ans;

    }

}

