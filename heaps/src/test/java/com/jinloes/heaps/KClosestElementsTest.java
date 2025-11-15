package com.jinloes.heaps;

import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class KClosestElementsTest {
    private KClosestElements kClosestElements;

    @BeforeEach
    void setUp() {
        kClosestElements = new KClosestElements();
    }

    @Test
    void findClosestElements() {
        assertThat(kClosestElements.findClosestElements(new int[]{2, 4, 5, 8}, 2, 6))
                .containsExactly(4, 5);
    }

    @Test
    void findClosestElementsBinarySearch() {
        assertThat(kClosestElements.findClosestElementsBinarySearch(new int[]{2, 4, 5, 8}, 2, 6))
                .containsExactly(4, 5);
    }
}