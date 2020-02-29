package com.jinloes.data_structures.trie;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren()
                    .computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setIsWord(true);
    }

    public boolean containsWord(String word) {
        if (Strings.isNullOrEmpty(word)) {
            return false;
        }

        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().get(word.charAt(i));
            if (current == null) {
                return false;
            }
        }

        return current != null && current.isWord();
    }

    private static class TrieNode {
        private final Map<Character, TrieNode> children;
        private boolean isWord;

        public TrieNode() {
            this.children = new HashMap<>();
            this.isWord = false;
        }

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public boolean isWord() {
            return isWord;
        }

        public void setIsWord(boolean word) {
            isWord = word;
        }
    }
}
