package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void beginWordNeedNotBeListed() {
        assertThat(Solution.ladderLength("bake", "cave", List.of("cake", "cave")))
                .as("ladderLength(bake -> cave)")
                .isEqualTo(3);
    }

    @Test
    void disconnectedWordsGiveZero() {
        assertThat(Solution.ladderLength("aaa", "zzz", List.of("aab", "abb", "zzz")))
                .as("ladderLength(aaa -> zzz)")
                .isEqualTo(0);
    }

    @Test
    void prefersTheShorterOfTwoRoutes() {
        assertThat(Solution.ladderLength("aaa", "ccc", List.of("aac", "acc", "ccc", "baa", "bba", "bbb", "bcb", "bcc")))
                .as("ladderLength(aaa -> ccc)")
                .isEqualTo(4);
    }

    @Test
    void duplicatesAndListedBeginAreHarmless() {
        assertThat(Solution.ladderLength("aaa", "aab", List.of("aaa", "aab", "aab")))
                .as("ladderLength(aaa -> aab)")
                .isEqualTo(2);
    }

    @Test
    void directNeighbours() {
        assertThat(Solution.ladderLength("cat", "cot", List.of("cot")))
                .as("ladderLength(cat -> cot)")
                .isEqualTo(2);
    }

    @Test
    void unlistedIntermediateWordsCannotBeUsed() {
        assertThat(Solution.ladderLength("aaa", "abb", List.of("abb")))
                .as("ladderLength(aaa -> abb)")
                .isEqualTo(0);
    }

    @Test
    void everyBinaryWordAvailable() {
        assertThat(Solution.ladderLength("aaa", "bbb", List.of("aab", "aba", "abb", "baa", "bab", "bba", "bbb")))
                .as("ladderLength(aaa -> bbb)")
                .isEqualTo(4);
    }

    @Test
    void walksOneLetterAtATime() {
        assertThat(Solution.ladderLength("aaaaa", "bbbbb", List.of("baaaa", "bbaaa", "bbbaa", "bbbba", "bbbbb")))
                .as("ladderLength(aaaaa -> bbbbb)")
                .isEqualTo(6);
    }
}
