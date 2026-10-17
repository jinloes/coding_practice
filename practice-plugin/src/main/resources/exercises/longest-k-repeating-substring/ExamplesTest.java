package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void keepsOnlyCharactersMeetingThreshold() { assertThat(Solution.longestSubstring("aaabb", 3)).isEqualTo(3); }
    @Test
    void canUseMiddleSubstring() { assertThat(Solution.longestSubstring("ababbc", 2)).isEqualTo(5); }
    @Test
    void returnsZeroWhenNoneQualify() { assertThat(Solution.longestSubstring("abc", 2)).isZero(); }
}
