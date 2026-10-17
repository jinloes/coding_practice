package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsExistingTarget() { assertThat(Solution.insertPosition(new int[]{1,3,5,6}, 5)).isEqualTo(2); }
    @Test
    void findsMiddleGap() { assertThat(Solution.insertPosition(new int[]{1,3,5,6}, 2)).isEqualTo(1); }
    @Test
    void canReturnLength() { assertThat(Solution.insertPosition(new int[]{1,3,5,6}, 7)).isEqualTo(4); }
}

