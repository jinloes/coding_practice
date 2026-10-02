package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static final class Node {
        final Node[] children = new Node[26];
        String word;
    }

    public static List<String> findWords(char[][] grid, String[] words) {
        Node root = new Node();
        for (String word : words) {
            Node node = root;
            for (int i = 0; i < word.length(); i++) {
                int index = word.charAt(i) - 'a';
                if (node.children[index] == null) {
                    node.children[index] = new Node();
                }
                node = node.children[index];
            }
            node.word = word;
        }
        List<String> found = new ArrayList<>();
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                search(grid, r, c, root, found);
            }
        }
        return found;
    }

    private static void search(char[][] grid, int r, int c, Node parent, List<String> found) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[r].length) {
            return;
        }
        char letter = grid[r][c];
        if (letter == '#') {
            return;
        }
        Node node = parent.children[letter - 'a'];
        if (node == null) {
            return;
        }
        if (node.word != null) {
            found.add(node.word);
            node.word = null;
        }
        grid[r][c] = '#';
        search(grid, r + 1, c, node, found);
        search(grid, r - 1, c, node, found);
        search(grid, r, c + 1, node, found);
        search(grid, r, c - 1, node, found);
        grid[r][c] = letter;
    }
}
