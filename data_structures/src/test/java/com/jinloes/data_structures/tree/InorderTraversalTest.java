package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InorderTraversalTest extends BaseTreeTest {

    @Test
    public void traverse() {
        assertThat(InorderTraversal.traverse(root)).containsExactly(4, 2, 5, 1, 3);
    }
}