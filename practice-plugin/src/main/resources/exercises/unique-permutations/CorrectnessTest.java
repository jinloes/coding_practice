package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void doesNotMutateInputAndHandlesNegatives() {
        int[] values = {-1, 2, -1};
        assertThat(Solution.uniquePermutations(values)).containsExactlyInAnyOrder(List.of(-1, -1, 2), List.of(-1, 2, -1), List.of(2, -1, -1));
        assertThat(values).containsExactly(-1, 2, -1);
    }
    @Test
    void countsLargerDuplicateSet() { assertThat(Solution.uniquePermutations(new int[]{1, 1, 2, 2})).hasSize(6).doesNotHaveDuplicates(); }
}
