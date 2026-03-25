package com.jinloes.simple_functions.sliding_window;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestSubstringKRepeatingCharsTest {
    private LongestSubstringKRepeatingChars longestSubstringKRepeatingChars;

    @BeforeEach
    public void setUp() throws Exception {
        this.longestSubstringKRepeatingChars = new LongestSubstringKRepeatingChars();
    }

    @Test
    public void longestSubstring() {
        assertThat(longestSubstringKRepeatingChars.longestSubstring("aaabb", 3)).isEqualTo(3);
    }
}