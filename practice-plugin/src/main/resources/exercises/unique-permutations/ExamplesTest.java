package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesDuplicatePermutations() { assertThat(Solution.uniquePermutations(new int[]{1, 1, 2})).containsExactlyInAnyOrder(List.of(1, 1, 2), List.of(1, 2, 1), List.of(2, 1, 1)); }
    @Test
    void emptyInputHasOneEmptyPermutation() { assertThat(Solution.uniquePermutations(new int[]{})).containsExactly(List.of()); }
    @Test
    void allEqualValuesReturnOnePermutation() { assertThat(Solution.uniquePermutations(new int[]{3, 3, 3})).containsExactly(List.of(3, 3, 3)); }
}
