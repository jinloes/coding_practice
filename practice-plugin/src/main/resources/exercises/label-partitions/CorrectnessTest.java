package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyTextHasNoPieces() {
        check("");
    }

    @Test
    void singleLetter() {
        check("a", 1);
    }

    @Test
    void interleavedLettersMergeIntoOnePiece() {
        check("abab", 4);
    }

    @Test
    void nestedSpansExtendThePiece() {
        check("mnonmpqpq", 5, 4);
    }

    @Test
    void adjacentPairs() {
        check("aabbcc", 2, 2, 2);
    }

    @Test
    void firstLetterRepeatsAtTheEnd() {
        check("abcdefga", 8);
    }

    @Test
    void lastPieceIsSingle() {
        check("xyzzyxw", 6, 1);
    }

    @Test
    void longSpan() {
        check("a" + "b".repeat(100) + "a", 102);
    }

    private static void check(String text, Integer... expected) {
        assertThat(Solution.partitionSizes(text)).as("partitionSizes(\"%s\")".formatted(text)).containsExactly(expected);
    }
}
