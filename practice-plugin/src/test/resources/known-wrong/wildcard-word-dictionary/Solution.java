package com.jinloes.practice;

public class Solution {
    private static final class Node {
        final Node[] children = new Node[26];
        boolean isWord;
    }

    private final Node root = new Node();

    public void add(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';
            if (node.children[index] == null) {
                node.children[index] = new Node();
            }
            node = node.children[index];
        }
        node.isWord = true;
    }

    public boolean matches(String pattern) {
        return search(root, pattern, 0);
    }

    private boolean search(Node node, String pattern, int position) {
        if (position == pattern.length()) {
            return node.isWord;
        }
        char letter = pattern.charAt(position);
        if (letter != '.') {
            Node child = node.children[letter - 'a'];
            return child != null && search(child, pattern, position + 1);
        }
        for (Node child : node.children) {
            if (child != null) {
                return search(child, pattern, position + 1);
            }
        }
        return false;
    }
}
