package com.jinloes.graphs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JgraphtBFSTest extends BaseGraphTest {

    @BeforeEach
    public void search() {
        assertThat(JgraphtBFS.search(stringGraph, "v4")).isTrue();
        assertThat(JgraphtBFS.search(stringGraph, "vx")).isFalse();
        assertThat(JgraphtBFS.search(stringGraph, null)).isFalse();
    }
}