package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesBoundariesAndMissingTargets() {
        assertThat(Solution.targetRange(new int[0], 3)).containsExactly(-1, -1);
        assertThat(Solution.targetRange(new int[]{0, 1, 3, 5}, 0)).containsExactly(0, 0);
        assertThat(Solution.targetRange(new int[]{0, 1, 3, 5}, 5)).containsExactly(3, 3);
        assertThat(Solution.targetRange(new int[]{-4, -2, -2, 0, 8}, -5)).containsExactly(-1, -1);
        assertThat(Solution.targetRange(new int[]{-4, -2, -2, 0, 8}, 9)).containsExactly(-1, -1);
    }
    @Test
    void findsLongRunAndDoesNotMutate() {
        int[] numbers = new int[1000];
        for (int i = 0; i < numbers.length; i++) numbers[i] = i < 123 ? 1 : i < 789 ? 5 : 9;
        int[] before = numbers.clone();
        assertThat(Solution.targetRange(numbers, 5)).containsExactly(123, 788);
        assertThat(numbers).containsExactly(before);
    }
}

