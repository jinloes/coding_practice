package com.jinloes.data_structures.trie;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TrieTest {
    private Trie trie;

    @BeforeEach
    public void setUp() {
        trie = new Trie();
    }

    @Test
    public void containsWord() {
        trie.addWord("bob");
        trie.addWord("cat");

        assertThat(trie.containsWord(null)).isFalse();
        assertThat(trie.containsWord("")).isFalse();
        assertThat(trie.containsWord("bob")).isTrue();
        assertThat(trie.containsWord("bob2")).isFalse();
        assertThat(trie.containsWord("cat")).isTrue();
    }
}