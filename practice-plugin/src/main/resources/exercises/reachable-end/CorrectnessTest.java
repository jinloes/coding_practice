package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleCellWithJump() {
        assertResult(new int[]{5}, true);
    }

    @Test
    void twoCellsReachable() {
        assertResult(new int[]{1, 0}, true);
    }

    @Test
    void twoCellsBlocked() {
        assertResult(new int[]{0, 1}, false);
    }

    @Test
    void jumpsOverZeros() {
        assertResult(new int[]{2, 0, 0}, true);
    }

    @Test
    void jumpOverSingleZero() {
        assertResult(new int[]{2, 0, 1}, true);
    }

    @Test
    void zeroTrapAtEnd() {
        assertResult(new int[]{1, 1, 0, 1}, false);
    }

    @Test
    void exactJumpToEnd() {
        assertResult(new int[]{4, 0, 0, 0, 0}, true);
    }

    @Test
    void jumpOneShort() {
        assertResult(new int[]{3, 0, 0, 0, 0}, false);
    }

    @Test
    void maxIntJumpDoesNotOverflow() {
        assertResult(new int[]{Integer.MAX_VALUE, 0, 0, 0}, true);
    }

    @Test
    void laterCellExtendsReach() {
        assertResult(new int[]{1, 2, 0, 1}, true);
    }

    @Test
    void allZerosLongArray() {
        assertResult(new int[8], false);
    }

    @Test
    void unreachableAfterLongRun() {
        assertResult(stuck(1000), false);
    }

    @Test
    void reachableLongRun() {
        assertResult(ones(1000), true);
    }

    private static void assertResult(int[] values, boolean expected) {
        int[] copy = values.clone();
        boolean actual = Solution.canReach(values);
        assertThat(actual).as("canReach(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("canReach must not mutate its input").containsExactly(copy);
    }

    private static int[] ones(int length) {
        int[] values = new int[length];
        Arrays.fill(values, 1);
        return values;
    }

    private static int[] stuck(int length) {
        int[] values = ones(length);
        values[length / 2] = 0;
        values[length / 2 - 1] = 1;
        return values;
    }
}
