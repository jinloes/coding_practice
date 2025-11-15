package com.jinloes.simple_functions.sliding_window;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MinimumWindowSubStringTest {
    private MinimumWindowSubString minimumWindowSubString;

    @Before
    public void setUp() throws Exception {
        minimumWindowSubString = new MinimumWindowSubString();
    }

    @Test
    public void minWindow() {
        assertThat(minimumWindowSubString.minWindow("ADOBECODEBANC", "ABC"))
                .isEqualTo("BANC");
    }

    @Test
    public void minWindowSingle() {
        assertThat(minimumWindowSubString.minWindow("a", "a"))
                .isEqualTo("a");
    }

    @Test
    public void minWindowBBA() {
        assertThat(minimumWindowSubString.minWindow("bba", "ab"))
                .isEqualTo("ba");
    }

    @Test
    public void testLongString() {
        assertThat(minimumWindowSubString.minWindow("aaaaaaaaaaaabbbbbcdd", "abcdd"))
                .isEqualTo("abbbbbcdd");

    }

    @Test
    public void testLonger() {
        assertThat(minimumWindowSubString.minWindow("a", "aa"))
                .isEqualTo("");
    }

    @Test
    public void testDiffA() {
        assertThat(minimumWindowSubString.minWindow("a", "b"))
                .isEqualTo("");
    }
}