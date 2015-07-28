package com.jinloes.simple_functions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ArrayRange}
 */
public class ArrayRangeTest {
    @Test
    public void testSortedArray() {
        int[] ints = new int[]{0, 1, 2, 7, 21, 22, 1098, 1099, 2000};
        assertThat(ArrayRange.calculateRanges(ints))
                .containsExactly("0-2", "7", "21-22", "1098-1099", "2000");
    }

    @Test
    public void testNull() {
        assertThat(ArrayRange.calculateRanges(null)).isEmpty();
    }

    @Test
    public void testEmptyArray() {
        assertThat(ArrayRange.calculateRanges(new int[]{})).isEmpty();
    }
}
