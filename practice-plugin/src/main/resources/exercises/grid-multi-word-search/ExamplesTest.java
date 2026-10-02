package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTracedWords() {
        assertThat(Solution.findWords(grid("cat", "ors", "den"), new String[]{"cat", "cod", "care", "dog"}))
                .containsExactlyInAnyOrder("cat", "cod", "care");
    }

    @Test
    void neverReusesACellWithinAWord() {
        assertThat(Solution.findWords(grid("ab"), new String[]{"aba"})).isEmpty();
    }

    @Test
    void reportsDuplicateWordsOnce() {
        assertThat(Solution.findWords(grid("x"), new String[]{"x", "x"})).containsExactly("x");
    }

    private static char[][] grid(String... rows) {
        char[][] grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        return grid;
    }
}
