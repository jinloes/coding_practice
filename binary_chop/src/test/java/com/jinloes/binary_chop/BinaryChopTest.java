package com.jinloes.binary_chop;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link BinaryChopper}.
 */
public class BinaryChopTest {

    @Test
    public void testRecursive() {
        assertThat(BinaryChopper.chopRecursive(3, new int[]{}), is(-1));
        assertThat(BinaryChopper.chopRecursive(3, new int[]{1}), is(-1));
        assertThat(BinaryChopper.chopRecursive(1, new int[]{1}), is(0));

        assertThat(BinaryChopper.chopRecursive(1, new int[]{1, 3, 5}), is(0));
        assertThat(BinaryChopper.chopRecursive(3, new int[]{1, 3, 5}), is(1));
        assertThat(BinaryChopper.chopRecursive(5, new int[]{1, 3, 5}), is(2));
        assertThat(BinaryChopper.chopRecursive(0, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopRecursive(2, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopRecursive(4, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopRecursive(6, new int[]{1, 3, 5}), is(-1));

        assertThat(BinaryChopper.chopRecursive(1, new int[]{1, 3, 5, 7}), is(0));
        assertThat(BinaryChopper.chopRecursive(3, new int[]{1, 3, 5, 7}), is(1));
        assertThat(BinaryChopper.chopRecursive(5, new int[]{1, 3, 5, 7}), is(2));
        assertThat(BinaryChopper.chopRecursive(7, new int[]{1, 3, 5, 7}), is(3));
        assertThat(BinaryChopper.chopRecursive(0, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopRecursive(2, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopRecursive(4, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopRecursive(6, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopRecursive(8, new int[]{1, 3, 5, 7}), is(-1));
    }

    @Test
    public void testIterative() {
        assertThat(BinaryChopper.chopInterative(3, new int[]{}), is(-1));
        assertThat(BinaryChopper.chopInterative(3, new int[]{1}), is(-1));
        assertThat(BinaryChopper.chopInterative(1, new int[]{1}), is(0));

        assertThat(BinaryChopper.chopInterative(1, new int[]{1, 3, 5}), is(0));
        assertThat(BinaryChopper.chopInterative(3, new int[]{1, 3, 5}), is(1));
        assertThat(BinaryChopper.chopInterative(5, new int[]{1, 3, 5}), is(2));
        assertThat(BinaryChopper.chopInterative(0, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopInterative(2, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopInterative(4, new int[]{1, 3, 5}), is(-1));
        assertThat(BinaryChopper.chopInterative(6, new int[]{1, 3, 5}), is(-1));

        assertThat(BinaryChopper.chopInterative(1, new int[]{1, 3, 5, 7}), is(0));
        assertThat(BinaryChopper.chopInterative(3, new int[]{1, 3, 5, 7}), is(1));
        assertThat(BinaryChopper.chopInterative(5, new int[]{1, 3, 5, 7}), is(2));
        assertThat(BinaryChopper.chopInterative(7, new int[]{1, 3, 5, 7}), is(3));
        assertThat(BinaryChopper.chopInterative(0, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopInterative(2, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopInterative(4, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopInterative(6, new int[]{1, 3, 5, 7}), is(-1));
        assertThat(BinaryChopper.chopInterative(8, new int[]{1, 3, 5, 7}), is(-1));
    }
}
