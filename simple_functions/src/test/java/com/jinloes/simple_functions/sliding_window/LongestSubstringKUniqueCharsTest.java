package com.jinloes.simple_functions.sliding_window;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestSubstringKUniqueCharsTest {
    private LongestSubstringKUniqueChars longestSubstringKUniqueChars;

    @Before
    public void setUp() throws Exception {
        longestSubstringKUniqueChars = new LongestSubstringKUniqueChars();
    }

    @Test
    public void kUniques1() {
        assertThat(longestSubstringKUniqueChars.kUniques("aabbcc", 1))
                .isIn("aa", "bb", "cc");
    }

    @Test
    public void kUniques2() {
        assertThat(longestSubstringKUniqueChars.kUniques("aabbcc", 2))
                .isIn("aabb", "bbcc");
    }

    @Test
    public void kUniques3() {
        assertThat(longestSubstringKUniqueChars.kUniques("aabbcc", 3))
                .isIn("aabbcc", "abbcc", "aabbc", "abbc");
    }

    @Test
    public void kUniquesGreaterK() {
        assertThat(longestSubstringKUniqueChars.kUniques("aabb", 3))
                .isEmpty();
    }
}