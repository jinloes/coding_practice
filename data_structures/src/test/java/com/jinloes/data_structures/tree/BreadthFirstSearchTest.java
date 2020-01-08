package com.jinloes.data_structures.tree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BreadthFirstSearchTest extends BaseTreeTest {

    @Test
    public void testSearch() {
        assertThat(BreadthFirstSearch.search(root, 5)).isTrue();
        assertThat(BreadthFirstSearch.search(root, 1)).isTrue();
        assertThat(BreadthFirstSearch.search(root, 999)).isFalse();
        assertThat(BreadthFirstSearch.search(root, null)).isFalse();
    }

}