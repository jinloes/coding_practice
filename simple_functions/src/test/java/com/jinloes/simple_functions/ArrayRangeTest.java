package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ArrayRange}
 */
public class ArrayRangeTest {
    @Test
    public void testSortedArray() {
        int[] ints = new int[]{0, 1, 2, 7, 21, 22, 1098, 1099, 2000};
        List<String> expected = new ArrayList<>();
        expected.add("0-2");
        expected.add("7");
        expected.add("21-22");
        expected.add("1098-1099");
        expected.add("2000");
        assertEquals(ArrayRange.calculateRanges(ints), expected);
    }

    @Test
    public void testNull() {
        assertEquals(ArrayRange.calculateRanges(null), new ArrayList<>());
    }

    @Test
    public void testEmptyArray() {
        assertEquals(ArrayRange.calculateRanges(new int[]{}), new ArrayList<>());
    }
}
