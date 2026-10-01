package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void handlesNoCoursesAndNoPrerequisites() {
        assertThat(Solution.canFinish(0, new int[0][2])).isTrue();
        assertThat(Solution.canFinish(3, new int[0][2])).isTrue();
    }
    @Test
    void detectsLongAndSelfCycles() {
        assertThat(Solution.canFinish(4, new int[][]{{1, 0}, {2, 1}, {0, 2}})).isFalse();
        assertThat(Solution.canFinish(1, new int[][]{{0, 0}})).isFalse();
    }
    @Test
    void ignoresDuplicateEdges() {
        assertThat(Solution.canFinish(3, new int[][]{{1, 0}, {1, 0}, {2, 1}})).isTrue();
    }
    @Test
    void handlesDisconnectedComponents() {
        assertThat(Solution.canFinish(6, new int[][]{{1, 0}, {3, 2}, {4, 3}})).isTrue();
        assertThat(Solution.canFinish(6, new int[][]{{1, 0}, {3, 2}, {2, 3}})).isFalse();
    }
    @Test
    void doesNotMutatePrerequisites() {
        int[][] prerequisites = {{1, 0}, {2, 1}};
        int[][] before = {{1, 0}, {2, 1}};
        Solution.canFinish(3, prerequisites);
        assertThat(prerequisites).isDeepEqualTo(before);
    }
}
