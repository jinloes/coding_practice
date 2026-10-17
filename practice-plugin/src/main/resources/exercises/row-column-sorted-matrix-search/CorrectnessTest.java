package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyShapesAndCorners() {
        assertThat(Solution.contains(new int[0][0], 1)).isFalse();
        assertThat(Solution.contains(new int[][]{{}}, 1)).isFalse();
        int[][] matrix = {{1,4,9},{2,5,10},{3,6,11}};
        assertThat(Solution.contains(matrix, 1)).isTrue();
        assertThat(Solution.contains(matrix, 11)).isTrue();
        assertThat(Solution.contains(matrix, 7)).isFalse();
    }
    @Test
    void handlesThinMatricesAndDoesNotMutate() {
        int[][] matrix = {{1,4},{2,5}}; int[][] before = {{1,4},{2,5}};
        assertThat(Solution.contains(new int[][]{{1,3,5,7,9}}, 7)).isTrue();
        assertThat(Solution.contains(new int[][]{{1},{3},{5}}, 4)).isFalse();
        Solution.contains(matrix, 2);
        assertThat(matrix).isDeepEqualTo(before);
    }
}

