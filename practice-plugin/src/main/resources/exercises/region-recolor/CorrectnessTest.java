package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void recolorsASinglePixelImage() {
        check(new int[][]{{3}}, new int[][]{{5}}, 0, 0, 3);
        check(new int[][]{{5}}, new int[][]{{5}}, 0, 0, 5);
    }

    @Test
    void stopsAtPixelsOfAnotherColor() {
        check(new int[][]{{9, 0, 1}}, new int[][]{{1, 0, 1}}, 0, 0, 9);
        check(new int[][]{{1, 0, 9}}, new int[][]{{1, 0, 1}}, 0, 2, 9);
        check(new int[][]{{4}, {6}, {6}}, new int[][]{{4}, {5}, {5}}, 2, 0, 6);
    }

    @Test
    void startsFromCornersAndEdges() {
        int[][] image = {{3, 3, 4, 4}, {3, 4, 4, 3}, {3, 3, 3, 3}};
        check(new int[][]{{3, 3, 8, 8}, {3, 8, 8, 3}, {3, 3, 3, 3}}, copy(image), 0, 3, 8);
        check(new int[][]{{0, 0, 4, 4}, {0, 4, 4, 0}, {0, 0, 0, 0}}, copy(image), 2, 0, 0);
    }

    @Test
    void separatesARingFromTheEnclosedPixel() {
        int[][] image = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        check(new int[][]{{4, 4, 4}, {4, 0, 4}, {4, 4, 4}}, copy(image), 0, 0, 4);
        check(new int[][]{{1, 1, 1}, {1, 4, 1}, {1, 1, 1}}, copy(image), 1, 1, 4);
    }

    @Test
    void newColorMayMatchANeighboringRegion() {
        check(new int[][]{{2, 2, 2}, {2, 2, 2}}, new int[][]{{1, 1, 2}, {2, 2, 2}}, 0, 0, 2);
        check(new int[][]{{2, 2}, {2, 1}}, new int[][]{{1, 2}, {2, 1}}, 0, 0, 2);
        check(new int[][]{{5, 5}, {5, 5}}, new int[][]{{5, 3}, {3, 3}}, 1, 1, 5);
    }

    @Test
    void handlesLargeColorValues() {
        check(new int[][]{{0, Integer.MAX_VALUE}, {Integer.MAX_VALUE, Integer.MAX_VALUE}},
                new int[][]{{0, 65_535}, {65_535, 65_535}}, 1, 1, Integer.MAX_VALUE);
    }

    @Test
    void followsALongWindingPath() {
        int size = 101;
        int[][] image = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                boolean bend = r % 4 == 1 ? c == size - 1 : r % 4 == 3 && c == 0;
                image[r][c] = r % 2 == 0 || bend ? 1 : 0;
            }
        }
        image[1][0] = 3;
        int[][] expected = copy(image);
        for (int[] row : expected) {
            for (int c = 0; c < size; c++) {
                if (row[c] == 1) {
                    row[c] = 2;
                }
            }
        }
        check(expected, image, size - 1, size - 1, 2);
    }

    @Test
    void recolorsALargeUniformImage() {
        int[][] image = new int[100][100];
        int[][] expected = new int[100][100];
        for (int[] row : expected) {
            Arrays.fill(row, 6);
        }
        check(expected, image, 50, 50, 6);
    }

    private static int[][] copy(int[][] image) {
        int[][] copy = new int[image.length][];
        for (int r = 0; r < image.length; r++) {
            copy[r] = image[r].clone();
        }
        return copy;
    }

    private static void check(int[][] expected, int[][] image, int row, int col, int color) {
        String call = image.length * image[0].length > 64
                ? "recolor(%dx%d image, %d, %d, %d)".formatted(image.length, image[0].length, row, col, color)
                : "recolor(%s, %d, %d, %d)".formatted(Arrays.deepToString(image), row, col, color);
        assertThat(Solution.recolor(image, row, col, color)).as(call).isDeepEqualTo(expected);
    }
}
