package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsTypicalAddress() {
        assertThat(Solution.splitIpAddresses("25525511135"))
                .containsExactly("255.255.11.135", "255.255.111.35");
    }

    @Test
    void allowsSingleZeroParts() {
        assertThat(Solution.splitIpAddresses("0000")).containsExactly("0.0.0.0");
    }

    @Test
    void rejectsTooShortInput() {
        assertThat(Solution.splitIpAddresses("123")).isEmpty();
    }
}
