package com.jinloes.simple_functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FindAnagramsTest {
    private FindAnagrams findAnagrams;

    @BeforeEach
    void setUp() {
        findAnagrams = new FindAnagrams();
    }

    @Test
    void find() {
        assertThat(findAnagrams.find("CABCDBACDABD", "AB"))
                .containsExactly(1, 5, 9);
        assertThat(findAnagrams.find("CABCDBACDABD", "ABC"))
                .containsExactly(0, 1, 5);
        assertThat(findAnagrams.find("CABCDBACDABD", null))
                .isEmpty();
        assertThat(findAnagrams.find(null, "AB"))
                .isEmpty();
        assertThat(findAnagrams.find("A", "AB"))
                .isEmpty();
        assertThat(findAnagrams.find("ABC", "ABC"))
                .containsExactly(0);
    }
}