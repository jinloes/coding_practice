package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void diagonalNeighboursAreNotAdjacent() {
        char[][] g = grid("ab", "cd");
        assertThat(Solution.findWords(g, new String[]{"ad", "bc", "ab", "ac", "bd", "cd"}))
                .containsExactlyInAnyOrder("ab", "ac", "bd", "cd");
    }

    @Test
    void followsASnakingPath() {
        assertThat(Solution.findWords(grid("ab", "cd"), new String[]{"abdc", "acdb", "adbc"}))
                .containsExactlyInAnyOrder("abdc", "acdb");
    }

    @Test
    void wordLongerThanTheGridIsNotFound() {
        assertThat(Solution.findWords(grid("ab", "cd"), new String[]{"abcde", "abdcb"})).isEmpty();
    }

    @Test
    void findsAWordAndItsPrefixWord() {
        assertThat(Solution.findWords(grid("dog"), new String[]{"do", "dog", "dot"})).containsExactlyInAnyOrder("do", "dog");
    }

    @Test
    void backtracksOutOfDeadEnds() {
        assertThat(Solution.findWords(grid("abc", "fed"), new String[]{"abcdef", "abef", "abed", "abfe"}))
                .containsExactlyInAnyOrder("abcdef", "abef", "abed");
    }

    @Test
    void emptyWordListFindsNothing() {
        assertThat(Solution.findWords(grid("abc"), new String[0])).isEmpty();
    }

    @Test
    void worksOnASingleColumn() {
        assertThat(Solution.findWords(grid("a", "b", "c"), new String[]{"abc", "cba", "ac"}))
                .containsExactlyInAnyOrder("abc", "cba");
    }

    @Test
    void repeatedLettersNeedDistinctCells() {
        assertThat(Solution.findWords(grid("aa", "aa"), new String[]{"aaaa", "aaaaa"})).containsExactly("aaaa");
    }

    @Test
    void multiplePathsReportTheWordOnce() {
        assertThat(Solution.findWords(grid("aba", "bab"), new String[]{"ab"})).containsExactly("ab");
    }

    @Test
    void restoresTheGrid() {
        char[][] g = grid("cat", "ors", "den");
        Solution.findWords(g, new String[]{"cat", "cod", "care"});
        assertThat(g).isEqualTo(grid("cat", "ors", "den"));
    }

    @Test
    void findsPathsThatCircleAroundTheGrid() {
        assertThat(Solution.findWords(grid("ab", "ba"), new String[]{"abab", "baba", "abba"}))
                .containsExactlyInAnyOrder("abab", "baba");
    }

    private static char[][] grid(String... rows) {
        char[][] grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        return grid;
    }
}
