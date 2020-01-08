package com.jinloes.data_structures.tree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PreorderTraversalTest extends BaseTreeTest {

    @Test
    public void traverse() {
        assertThat(PreorderTraversal.traverse(root)).containsExactly(1, 2, 4, 5, 3);
    }

}