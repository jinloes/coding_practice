package com.jinloes.simple_functions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WordSearchTest {

    @Test
    public void testExist() {
        char[][] board = new char[][]{
                new char[]{'A', 'B', 'C', 'E'},
                new char[]{'S', 'F', 'C', 'S'},
                new char[]{'A', 'D', 'E', 'E'}
        };

        assertThat(new WordSearch().exist(board, "ABCCED")).isTrue();
        assertThat(new WordSearch().exist(board, "ABCB")).isFalse();

    }

    @Test
    public void testExist2() {
        char[][] board = new char[][]{new char[]{'A'}};
        String word = "A";

        assertThat(new WordSearch().exist(board, word)).isTrue();
    }

    @Test
    public void testExist3() {
        char[][] board = new char[][]{new char[]{'a', 'b'}};
        String word = "ba";

        assertThat(new WordSearch().exist(board, word)).isTrue();
    }
}