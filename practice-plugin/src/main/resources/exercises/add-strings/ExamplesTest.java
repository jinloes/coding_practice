package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void addsNumbersOfDifferentLengths() {
        assertThat(Solution.addStrings("456", "77"))
                .as("addStrings(\"456\", \"77\")")
                .isEqualTo("533");
    }

    @Test
    void carriesIntoANewLeadingDigit() {
        assertThat(Solution.addStrings("1", "999"))
                .as("addStrings(\"1\", \"999\") must carry into a new leading digit")
                .isEqualTo("1000");
    }

    @Test
    void addsZeroToZero() {
        assertThat(Solution.addStrings("0", "0"))
                .as("addStrings(\"0\", \"0\") must not produce leading zeros")
                .isEqualTo("0");
    }
}
