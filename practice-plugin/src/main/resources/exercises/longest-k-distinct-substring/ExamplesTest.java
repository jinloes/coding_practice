package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsBestMiddleWindow() { assertThat(Solution.longestAtMostKDistinct("eceba", 2)).isEqualTo(3); }
    @Test
    void acceptsRepeatedSingleCharacter() { assertThat(Solution.longestAtMostKDistinct("aa", 1)).isEqualTo(2); }
    @Test
    void zeroDistinctAllowedReturnsZero() { assertThat(Solution.longestAtMostKDistinct("abc", 0)).isZero(); }
}
