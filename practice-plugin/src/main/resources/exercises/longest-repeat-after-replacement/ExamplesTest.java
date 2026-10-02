package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void replacesOneLetterInTheMiddle() {
        assertThat(Solution.longestRepeat("QQRQQ", 1)).isEqualTo(5);
    }

    @Test
    void needsNoReplacementsWhenKIsZero() {
        assertThat(Solution.longestRepeat("ABCD", 0)).isEqualTo(1);
    }

    @Test
    void usesTwoReplacementsInAMixedWindow() {
        assertThat(Solution.longestRepeat("XYXYXYX", 2)).isEqualTo(5);
    }
}
