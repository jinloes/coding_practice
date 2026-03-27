package com.jinloes.graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JgraphtDFSTest extends BaseGraphTest {

    @BeforeEach
    public void search() {
        assertThat(JgraphtDFS.search(stringGraph, "v4")).isTrue();
        assertThat(JgraphtDFS.search(stringGraph, "vx")).isFalse();
        assertThat(JgraphtDFS.search(stringGraph, null)).isFalse();
    }
}