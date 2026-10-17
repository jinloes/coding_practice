package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsInteriorDuplicateRun() { assertThat(Solution.targetRange(new int[]{1, 2, 2, 2, 4}, 2)).containsExactly(1, 3); }
    @Test
    void reportsAbsentTarget() { assertThat(Solution.targetRange(new int[]{1, 3, 5}, 4)).containsExactly(-1, -1); }
    @Test
    void coversWholeArrayWhenAllValuesMatch() { assertThat(Solution.targetRange(new int[]{7, 7, 7}, 7)).containsExactly(0, 2); }
}

