package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void queryPointNeedNotBeStored() {
        Solution counter = new Solution();
        add(counter, new int[]{1, 5}, new int[]{5, 1}, new int[]{5, 5});
        check(counter, 1, 1, 1);
    }

    @Test
    void copiesMultiply() {
        Solution counter = new Solution();
        add(counter, new int[]{0, 2}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 0}, new int[]{2, 0},
                new int[]{2, 2}, new int[]{2, 2}, new int[]{2, 2}, new int[]{2, 2});
        check(counter, 0, 0, 24);
    }

    @Test
    void squaresInAllFourDirections() {
        Solution counter = new Solution();
        for (int x = 0; x <= 8; x += 4) {
            for (int y = 0; y <= 8; y += 4) {
                if (x != 4 || y != 4) {
                    counter.add(x, y);
                }
            }
        }
        check(counter, 4, 4, 4);
        check(counter, 0, 0, 1);
    }

    @Test
    void rectangleIsNotASquare() {
        Solution counter = new Solution();
        add(counter, new int[]{0, 3}, new int[]{5, 0}, new int[]{5, 3});
        check(counter, 0, 0, 0);
    }

    @Test
    void countReflectsLaterAdds() {
        Solution counter = new Solution();
        add(counter, new int[]{0, 4}, new int[]{4, 0});
        check(counter, 0, 0, 0);
        counter.add(4, 4);
        check(counter, 0, 0, 1);
        counter.add(4, 4);
        check(counter, 0, 0, 2);
    }

    @Test
    void boundaryCoordinates() {
        Solution counter = new Solution();
        add(counter, new int[]{1000, 1000}, new int[]{0, 1000}, new int[]{1000, 0});
        check(counter, 0, 0, 1);
        check(counter, 1000, 1000, 0);
    }

    @Test
    void sameSideOffsetsMustMatch() {
        Solution counter = new Solution();
        add(counter, new int[]{2, 6}, new int[]{9, 2}, new int[]{9, 6});
        check(counter, 2, 2, 0);
    }

    @Test
    void gridCountsAllSquaresFromTheCorner() {
        Solution counter = new Solution();
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 6; y++) {
                counter.add(x, y);
            }
        }
        check(counter, 0, 0, 5);
        check(counter, 2, 3, 9);
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
