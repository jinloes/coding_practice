package com.jinloes.simple_functions.sliding_window;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestCharacterReplacementTest {
    private LongestCharacterReplacement longestCharacterReplacement;

    @Before
    public void setUp() throws Exception {
        longestCharacterReplacement = new LongestCharacterReplacement();
    }

    @Test
    public void characterReplacement() {
        assertThat(longestCharacterReplacement.characterReplacement("AABABBA", 1))
                .isEqualTo(4);
    }


    @Test
    public void characterReplacement2() {
        assertThat(longestCharacterReplacement.characterReplacement("ABAB", 2))
                .isEqualTo(4);
    }

    @Test
    public void characterReplacement3() {
        assertThat(longestCharacterReplacement.characterReplacement("ABBB", 2))
                .isEqualTo(4);
    }
}