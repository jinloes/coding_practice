package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void singleNodeIsATree() {
        assertThat(Solution.isTree(1, new int[0][2])).isTrue();
    }

    @Test
    void pathIsATree() {
        assertThat(Solution.isTree(3, new int[][]{{0, 1}, {1, 2}})).isTrue();
    }

    @Test
    void triangleHasACycle() {
        assertThat(Solution.isTree(3, new int[][]{{0, 1}, {1, 2}, {2, 0}})).isFalse();
    }
}
