package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void linksTwoChildren() {
        Solution.Node left = new Solution.Node(2);
        Solution.Node right = new Solution.Node(3);
        Solution.Node root = new Solution.Node(1, left, right);
        assertThat(Solution.connect(root)).isSameAs(root);
        assertThat(left.next).isSameAs(right);
        assertThat(right.next).isNull();
    }

    @Test
    void skipsMissingChildrenWithinLevel() {
        Solution.Node four = new Solution.Node(4);
        Solution.Node five = new Solution.Node(5);
        Solution.Node seven = new Solution.Node(7);
        Solution.Node root = new Solution.Node(1, new Solution.Node(2, four, five), new Solution.Node(3, null, seven));
        Solution.connect(root);
        assertThat(four.next).isSameAs(five);
        assertThat(five.next).isSameAs(seven);
        assertThat(seven.next).isNull();
    }
}
