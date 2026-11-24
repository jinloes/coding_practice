package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void handlesEmptyAndSingleWordInputs() {
        assertThat(solution.longestChain(new String[]{})).isEqualTo(0);
        assertThat(solution.longestChain(new String[]{"solo"})).isEqualTo(1);
    }

    @Test
    void ignoresDuplicateWordsAndNonSubsequenceAnagrams() {
        assertThat(solution.longestChain(new String[]{"ab", "ab", "ba", "cab"}))
                .isEqualTo(2);
        assertThat(solution.longestChain(new String[]{"cba", "abac"}))
                .isEqualTo(1);
    }

    @Test
    void handlesInsertionAtStartMiddleAndEnd() {
        assertThat(solution.longestChain(new String[]{"d", "cd", "cbd", "cbde", "acbd", "acbde"}))
                .isEqualTo(5);
    }
}
