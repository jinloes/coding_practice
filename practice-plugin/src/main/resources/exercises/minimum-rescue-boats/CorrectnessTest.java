package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyInputNeedsNoBoats() { assertThat(Solution.minBoats(new int[0], 10)).isZero(); }
    @Test
    void allPeopleMayNeedSeparateBoats() { assertThat(Solution.minBoats(new int[]{5, 5, 5}, 5)).isEqualTo(3); }
    @Test
    void pairsLightWithHeavyGreedily() { assertThat(Solution.minBoats(new int[]{5, 1, 4, 2}, 6)).isEqualTo(2); }
    @Test
    void doesNotMutateInput() { int[] people = {3, 1, 2}; Solution.minBoats(people, 3); assertThat(people).containsExactly(3, 1, 2); }
}
