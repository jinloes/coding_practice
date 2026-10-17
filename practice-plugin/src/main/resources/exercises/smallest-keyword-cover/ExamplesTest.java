package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsShortestCoveringRange() { assertThat(Solution.smallestCover(new String[]{"a","b","c","a","b"}, new String[]{"a","c"})).containsExactly(2, 3); }
    @Test
    void reportsMissingCover() { assertThat(Solution.smallestCover(new String[]{"alpha","beta"}, new String[]{"gamma"})).containsExactly(-1, -1); }
    @Test
    void emptyKeywordSetHasEmptyRangeAtStart() { assertThat(Solution.smallestCover(new String[]{"one","two","three"}, new String[]{})).containsExactly(0, -1); }
}
