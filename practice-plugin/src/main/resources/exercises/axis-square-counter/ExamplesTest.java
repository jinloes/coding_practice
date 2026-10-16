package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsOneSquare() {
        Solution counter = new Solution();
        add(counter, new int[]{3, 3}, new int[]{7, 3}, new int[]{7, 7});
        check(counter, 3, 7, 1);
    }

    @Test
    void duplicateCornersCountSeparately() {
        Solution counter = new Solution();
        add(counter, new int[]{0, 0}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 2}, new int[]{2, 2});
        check(counter, 0, 0, 2);
    }

    @Test
    void squareNeedsPositiveSide() {
        Solution counter = new Solution();
        check(counter, 5, 5, 0);
        counter.add(5, 5);
        check(counter, 5, 5, 0);
    }

    private static void add(Solution counter, int[]... points) {
        for (int[] point : points) {
            counter.add(point[0], point[1]);
        }
    }

    private static void check(Solution counter, int x, int y, int expected) {
        assertThat(counter.count(x, y)).as("count(%d, %d)".formatted(x, y)).isEqualTo(expected);
    }
}
