package com.jinloes.data_structures.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BreadthFirstSearchTest extends BaseGraphTest {

    @BeforeEach
    public void search() {
        assertThat(BreadthFirstSearch.search(stringGraph, "v4")).isTrue();
        assertThat(BreadthFirstSearch.search(stringGraph, "vx")).isFalse();
        assertThat(BreadthFirstSearch.search(stringGraph, null)).isFalse();
    }
}