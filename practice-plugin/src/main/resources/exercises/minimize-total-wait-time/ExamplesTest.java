package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void minimizesTypicalCase() { assertThat(Solution.minimumTotalWaitTime(new int[]{2,5,1,3})).isEqualTo(10); }
    @Test
    void singleJobDoesNotWait() { assertThat(Solution.minimumTotalWaitTime(new int[]{7})).isZero(); }
    @Test
    void handlesEqualDurations() { assertThat(Solution.minimumTotalWaitTime(new int[]{4,4,4})).isEqualTo(12); }
}
