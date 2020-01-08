package com.jinloes.simple_functions;

class WordSearch {
    private int height;
    private int width;
    private boolean[][] visited;

    public boolean exist(char[][] board, String word) {
        height = board.length;
        width = board[0].length;
        visited = new boolean[height][width];
        for (int i = 0; i < board.length; i++) {
            char[] row = board[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] == word.charAt(0)) {
                    if (search(board, word, 0, i, j)) {
                        return true;
                    }
                    visited = new boolean[board.length][board[0].length];
                }
            }
        }
        return false;
    }

    private boolean search(char[][] board, String word, int wordIdx, int x, int y) {
        boolean found = false;
        visited[x][y] = true;
        if (word.charAt(wordIdx) == board[x][y]) {
            if (word.length() - 1 == wordIdx) {
                return true;
            }
            if (x - 1 >= 0 && !visited[x - 1][y]) {
                found |= search(board, word, wordIdx + 1, x - 1, y);
            }
            if (x + 1 < height && !visited[x + 1][y]) {
                found |= search(board, word, wordIdx + 1, x + 1, y);
            }

            if (y - 1 >= 0 && !visited[x][y - 1]) {
                found |= search(board, word, wordIdx + 1, x, y - 1);
            }

            if (y + 1 < width && !visited[x][y + 1]) {
                found |= search(board, word, wordIdx + 1, x, y + 1);
            }

        }
        if (!found) {
            visited[x][y] = false;
        }
        return found;
    }
}
