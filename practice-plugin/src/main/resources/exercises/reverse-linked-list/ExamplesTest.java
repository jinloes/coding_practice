package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reversesThreeNodes() {
        Solution.Node one = new Solution.Node(1, new Solution.Node(2, new Solution.Node(3, null)));
        Solution.Node result = Solution.reverse(one);
        assertThat(result.value).isEqualTo(3);
        assertThat(result.next.value).isEqualTo(2);
        assertThat(result.next.next.value).isEqualTo(1);
        assertThat(result.next.next.next).isNull();
    }

    @Test
    void preservesASingleNode() {
        Solution.Node node = new Solution.Node(8, null);
        assertThat(Solution.reverse(node)).isSameAs(node);
    }

    @Test
    void reversesAnEmptyList() {
        assertThat(Solution.reverse(null)).isNull();
    }
}
