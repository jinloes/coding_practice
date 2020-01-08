package com.jinloes.data_structures.graph;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DepthFirstSearchTest extends BaseGraphTest {

    @Test
    public void search() {
        assertThat(DepthFirstSearch.search(stringGraph, "v4")).isTrue();
        assertThat(DepthFirstSearch.search(stringGraph, "vx")).isFalse();
        assertThat(DepthFirstSearch.search(stringGraph, null)).isFalse();
    }
}