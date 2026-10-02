package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void rejectsADiagonalStep() {
        assertThat(Solution.wordExists(grid("AB", "CD"), "AD")).isFalse();
    }

    @Test
    void rejectsReusingACellInALoop() {
        assertThat(Solution.wordExists(grid("ABC", "SED", "TRO"), "ABCB")).isFalse();
    }

    @Test
    void rejectsAWordLongerThanTheGrid() {
        assertThat(Solution.wordExists(grid("ABC", "SED", "TRO"), "ABCDEFGHIJ")).isFalse();
    }

    @Test
    void followsASnakePathThroughEveryCell() {
        assertThat(Solution.wordExists(grid("ABC", "DEF", "GHI"), "ABCFEDGHI")).isTrue();
    }

    @Test
    void backtracksOutOfDeadEnds() {
        assertThat(Solution.wordExists(grid("AAB", "AAA"), "AAAAB")).isTrue();
    }

    @Test
    void rejectsWhenOnlyEnoughRepeatedLettersExistByReuse() {
        assertThat(Solution.wordExists(grid("AAB", "AAA"), "AAAAAAB")).isFalse();
    }

    @Test
    void findsAWordRunningDownAColumn() {
        assertThat(Solution.wordExists(grid("C", "A", "T"), "CAT")).isTrue();
    }

    @Test
    void findsAWordRunningBackwardsAlongARow() {
        assertThat(Solution.wordExists(grid("TAC"), "CAT")).isTrue();
    }

    @Test
    void treatsUppercaseAndLowercaseAsDifferent() {
        assertThat(Solution.wordExists(grid("Ab"), "ab")).isFalse();
    }

    @Test
    void returnsFalseForAnEmptyGrid() {
        assertThat(Solution.wordExists(new char[0][], "A")).isFalse();
    }

    @Test
    void restoresTheGridWhenTheWordIsFound() {
        char[][] grid = grid("ABC", "SED", "TRO");
        Solution.wordExists(grid, "BEST");
        assertThat(grid).isDeepEqualTo(grid("ABC", "SED", "TRO"));
    }

    @Test
    void restoresTheGridWhenTheWordIsMissing() {
        char[][] grid = grid("AAB", "AAA");
        Solution.wordExists(grid, "AAAAAAB");
        assertThat(grid).isDeepEqualTo(grid("AAB", "AAA"));
    }

    @Test
    void canSearchTheSameGridRepeatedly() {
        char[][] grid = grid("ABC", "SED", "TRO");
        assertThat(Solution.wordExists(grid, "BEST")).isTrue();
        assertThat(Solution.wordExists(grid, "BEST")).isTrue();
        assertThat(Solution.wordExists(grid, "BEE")).isFalse();
    }

    private static char[][] grid(String... rows) {
        char[][] grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        return grid;
    }
}
