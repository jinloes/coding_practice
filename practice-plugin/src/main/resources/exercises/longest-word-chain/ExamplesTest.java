package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void findsFiveStepChainWithBranching() {
        assertThat(solution.longestChain(new String[]{"a", "at", "cat", "chat", "chart", "dog"}))
                .isEqualTo(5);
    }

    @Test
    void findsFiveStepChainFromUnsortedInput() {
        assertThat(solution.longestChain(new String[]{"spline", "in", "spine", "spin", "pin", "on"}))
                .isEqualTo(5);
    }

    @Test
    void returnsOneWhenNoPredecessorExists() {
        assertThat(solution.longestChain(new String[]{"team", "jira"}))
                .isEqualTo(1);
    }
}
