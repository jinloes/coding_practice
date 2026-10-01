package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void acceptsAValidPrerequisiteChain() { assertThat(Solution.canFinish(2, new int[][]{{1, 0}})).isTrue(); }
    @Test
    void rejectsATwoCourseCycle() { assertThat(Solution.canFinish(2, new int[][]{{1, 0}, {0, 1}})).isFalse(); }
    @Test
    void acceptsAChainWithUnusedCourses() { assertThat(Solution.canFinish(4, new int[][]{{1, 0}, {2, 1}, {3, 2}})).isTrue(); }
}
