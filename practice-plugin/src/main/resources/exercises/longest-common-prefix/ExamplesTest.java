package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void stopsAtTheFirstColumnThatDisagrees() {
        assertThat(Solution.longestCommonPrefix(new String[]{"flower", "flow", "flight"}))
                .as("longestCommonPrefix([flower, flow, flight])")
                .isEqualTo("fl");
    }

    @Test
    void returnsEmptyWhenTheFirstLettersDiffer() {
        assertThat(Solution.longestCommonPrefix(new String[]{"dog", "racecar", "car"}))
                .as("longestCommonPrefix([dog, racecar, car])")
                .isEmpty();
    }

    @Test
    void returnsASingleWordWhole() {
        assertThat(Solution.longestCommonPrefix(new String[]{"interview"}))
                .as("longestCommonPrefix([interview])")
                .isEqualTo("interview");
    }
}
