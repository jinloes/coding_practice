package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostorderTraversalTest extends BaseTreeTest {

    @Test
    public void traverse() {
        assertThat(PostorderTraversal.traverse(root)).containsExactly(4, 5, 2, 3, 1);
    }

    @Test
    public void traverseNary() {
        assertThat(PostorderTraversal.traverseNary(nAryRoot)).containsExactly("D", "E", "F", "B", "C", "A");
    }
}