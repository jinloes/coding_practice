package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PreorderTraversalTest extends BaseBinaryTreeTest {

    @Test
    public void traverse() {
        assertThat(PreorderTraversal.traverse(root)).containsExactly(1, 2, 4, 5, 3);
        assertThat(PreorderTraversal.traverse(null)).isEmpty();
    }

    @Test
    public void traverseWithoutRecursion() {
        assertThat(PreorderTraversal.traverseWithoutRecursion(root)).containsExactly(1, 2, 4, 5, 3);
        assertThat(PreorderTraversal.traverseWithoutRecursion(null)).isEmpty();
    }

}