package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesKOneAndEmptyString() { assertThat(Solution.longestSubstring("abc", 1)).isEqualTo(3); assertThat(Solution.longestSubstring("", 3)).isZero(); }
    @Test
    void splitsAroundSeveralWeakCharacters() { assertThat(Solution.longestSubstring("aaabxybbbbcc", 3)).isEqualTo(4); }
}
