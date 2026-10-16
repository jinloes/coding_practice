package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noCoursesGivesAnEmptyOrder() {
        checkOrder(0, new int[0][]);
    }

    @Test
    void selfPrerequisiteIsImpossible() {
        checkImpossible(2, new int[][]{{1, 1}});
    }

    @Test
    void diamondOfPrerequisites() {
        checkOrder(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}});
    }

    @Test
    void duplicatePrerequisitesAreHarmless() {
        checkOrder(3, new int[][]{{1, 0}, {1, 0}, {2, 1}, {2, 1}});
    }

    @Test
    void cycleBehindValidCoursesIsStillImpossible() {
        checkImpossible(5, new int[][]{{1, 0}, {2, 1}, {3, 2}, {2, 3}});
    }

    @Test
    void twoCourseCycleWithAnIndependentCourse() {
        checkImpossible(3, new int[][]{{0, 1}, {1, 0}});
    }

    @Test
    void reverseNumberedChain() {
        checkOrder(5, new int[][]{{3, 4}, {2, 3}, {1, 2}, {0, 1}});
    }

    @Test
    void independentComponents() {
        checkOrder(6, new int[][]{{1, 0}, {3, 2}, {5, 4}});
    }

    @Test
    void longChainOfCourses() {
        int[][] chain = new int[1999][];
        for (int i = 0; i < 1999; i++) {
            chain[i] = new int[]{i + 1, i};
        }
        checkOrder(2000, chain);
    }


    private static void checkOrder(int courses, int[][] prerequisites) {
        String call = "courseOrder(" + courses + ", " + Arrays.deepToString(prerequisites) + ")";
        int[] order = Solution.courseOrder(courses, prerequisites);
        assertThat(order).as(call).isNotNull().hasSize(courses);
        int[] expectedCourses = new int[courses];
        for (int i = 0; i < courses; i++) {
            expectedCourses[i] = i;
        }
        assertThat(Arrays.stream(order).sorted().toArray()).as(call + " must list each course once").isEqualTo(expectedCourses);
        int[] position = new int[courses];
        for (int i = 0; i < courses; i++) {
            position[order[i]] = i;
        }
        for (int[] pair : prerequisites) {
            assertThat(position[pair[1]]).as(call + " order " + Arrays.toString(order) + ": course " + pair[1]
                    + " before " + pair[0]).isLessThan(position[pair[0]]);
        }
    }

    private static void checkImpossible(int courses, int[][] prerequisites) {
        String call = "courseOrder(" + courses + ", " + Arrays.deepToString(prerequisites) + ")";
        assertThat(Solution.courseOrder(courses, prerequisites)).as(call).isNotNull().isEmpty();
    }

    private static int[][] parseGrid(String text) {
        String body = text.trim();
        if (body.equals("[]")) {
            return new int[0][];
        }
        if (!body.startsWith("[[") || !body.endsWith("]]")) {
            throw new IllegalArgumentException("not a grid");
        }
        String[] rows = body.substring(2, body.length() - 2).split("\\]\\s*,\\s*\\[");
        int[][] grid = new int[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            String[] cells = rows[r].trim().split("\\s*,\\s*");
            grid[r] = new int[cells.length];
            for (int c = 0; c < cells.length; c++) {
                grid[r][c] = cells[c].equalsIgnoreCase("inf") ? Integer.MAX_VALUE : Integer.parseInt(cells[c]);
            }
        }
        return grid;
    }
}
