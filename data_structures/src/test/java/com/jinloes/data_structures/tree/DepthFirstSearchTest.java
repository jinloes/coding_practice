package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DepthFirstSearchTest extends BaseTreeTest {

    @Test
    public void search() {
        assertThat(DepthFirstSearch.search(root, 5)).isTrue();
        assertThat(DepthFirstSearch.search(root, 2)).isTrue();
        assertThat(DepthFirstSearch.search(root, 21232)).isFalse();
        assertThat(DepthFirstSearch.search(root, null)).isFalse();
    }
}