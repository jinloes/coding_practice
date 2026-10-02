package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void distinguishesWordsFromPrefixes() {
        Solution trie = new Solution();
        trie.insert("apple");
        assertThat(trie.contains("apple")).as("contains apple").isTrue();
        assertThat(trie.contains("app")).as("contains app").isFalse();
        assertThat(trie.hasPrefix("app")).as("hasPrefix app").isTrue();
    }

    @Test
    void storesWordsThatShareAPath() {
        Solution trie = new Solution();
        trie.insert("car");
        trie.insert("cart");
        assertThat(trie.contains("car")).isTrue();
        assertThat(trie.contains("cart")).isTrue();
        assertThat(trie.contains("carts")).isFalse();
    }

    @Test
    void newTreeIsEmpty() {
        Solution trie = new Solution();
        assertThat(trie.contains("a")).isFalse();
        assertThat(trie.hasPrefix("a")).isFalse();
    }
}
