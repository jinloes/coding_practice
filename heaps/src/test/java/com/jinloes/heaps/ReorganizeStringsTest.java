package com.jinloes.heaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReorganizeStringsTest {
    private ReorganizeStrings reorganizeStrings;

    @BeforeEach
    void setUp() {
        reorganizeStrings = new ReorganizeStrings();
    }

    @Test
    void reorganizeString() {
        assertThat(reorganizeStrings.reorganizeString("axyy"))
                .isEqualTo("yaxy");

        assertThat(reorganizeStrings.reorganizeString("aaabc"))
                .isEqualTo("acaba");

    }
}