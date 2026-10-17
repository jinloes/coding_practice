package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() { assertThat(Solution.minimumTotalWaitTime(new int[0])).isZero(); }
    @Test
    void ignoresOriginalOrder() { assertThat(Solution.minimumTotalWaitTime(new int[]{10,1,1,1})).isEqualTo(6); }
    @Test
    void includesZeroLengthJobs() { assertThat(Solution.minimumTotalWaitTime(new int[]{0,5,0,2})).isEqualTo(2); }
    @Test
    void usesLongForLargeTotals() { assertThat(Solution.minimumTotalWaitTime(new int[]{1_000_000_000,1_000_000_000,1_000_000_000})).isEqualTo(3_000_000_000L); }
    @Test
    void doesNotMutateInput() { int[] t={5,1,3}; Solution.minimumTotalWaitTime(t); assertThat(t).containsExactly(5,1,3); }
}
