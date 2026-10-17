package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTwoSeparateConcatenations() { assertThat(Solution.findStarts("barfoothefoobarman", new String[]{"foo", "bar"})).containsExactly(0, 9); }
    @Test
    void honorsRepeatedRequiredWords() { assertThat(Solution.findStarts("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "good"})).containsExactly(8); }
    @Test
    void reportsOverlappingMatches() { assertThat(Solution.findStarts("aaaaaa", new String[]{"aa", "aa"})).containsExactly(0, 1, 2); }
}
