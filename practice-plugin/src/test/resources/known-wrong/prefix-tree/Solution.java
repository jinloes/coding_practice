package com.jinloes.practice;

public class Solution {
    private static final class Node {
        final Node[] children = new Node[26];
        boolean isWord;
    }

    private final Node root = new Node();

    public void insert(String word) {
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

    public boolean contains(String word) {
        Node node = find(word);
        return node != null;
    }

    public boolean hasPrefix(String prefix) {
        return find(prefix) != null;
    }

    private Node find(String text) {
        Node node = root;
        for (int i = 0; i < text.length() && node != null; i++) {
            node = node.children[text.charAt(i) - 'a'];
        }
        return node;
    }
}
