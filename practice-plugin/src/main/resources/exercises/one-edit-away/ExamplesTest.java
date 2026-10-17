package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void oneDeletionCanMatch() { assertThat(Solution.isOneEditAway("pale", "ple")).isTrue(); }
    @Test
    void oneInsertionCanMatch() { assertThat(Solution.isOneEditAway("pales", "pale")).isTrue(); }
    @Test
    void twoReplacementsDoNotMatch() { assertThat(Solution.isOneEditAway("pale", "bake")).isFalse(); }
}
