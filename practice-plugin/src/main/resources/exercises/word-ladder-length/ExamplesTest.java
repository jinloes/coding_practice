package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void shortestChainCountsBothEndWords() {
        assertThat(Solution.ladderLength("bake", "cave", List.of("cake", "cave", "base")))
                .as("ladderLength(bake -> cave)")
                .isEqualTo(3);
    }

    @Test
    void endWordMustBeInTheList() {
        assertThat(Solution.ladderLength("bake", "cane", List.of("cake", "base", "cave")))
                .as("ladderLength(bake -> cane)")
                .isEqualTo(0);
    }

    @Test
    void longerChain() {
        assertThat(Solution.ladderLength("lead", "gold", List.of("load", "goad", "gold", "mold")))
                .as("ladderLength(lead -> gold)")
                .isEqualTo(4);
    }
}
