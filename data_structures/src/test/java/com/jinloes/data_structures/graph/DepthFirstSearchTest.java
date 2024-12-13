package com.jinloes.data_structures.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DepthFirstSearchTest extends BaseGraphTest {

    @BeforeEach
    public void search() {
        assertThat(DepthFirstSearch.search(stringGraph, "v4")).isTrue();
        assertThat(DepthFirstSearch.search(stringGraph, "vx")).isFalse();
        assertThat(DepthFirstSearch.search(stringGraph, null)).isFalse();
    }
}