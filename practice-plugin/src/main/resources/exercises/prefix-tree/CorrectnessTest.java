package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void insertingTwiceIsHarmless() {
        Solution trie = new Solution();
        trie.insert("dog");
        trie.insert("dog");
        assertThat(trie.contains("dog")).isTrue();
        assertThat(trie.contains("do")).isFalse();
    }

    @Test
    void laterInsertMakesAPrefixAWord() {
        Solution trie = new Solution();
        trie.insert("apple");
        assertThat(trie.contains("app")).isFalse();
        trie.insert("app");
        assertThat(trie.contains("app")).isTrue();
        assertThat(trie.contains("apple")).isTrue();
    }

    @Test
    void longerQueryThanStoredWordFails() {
        Solution trie = new Solution();
        trie.insert("bat");
        assertThat(trie.hasPrefix("bath")).isFalse();
        assertThat(trie.contains("bath")).isFalse();
    }

    @Test
    void fullWordIsItsOwnPrefix() {
        Solution trie = new Solution();
        trie.insert("zebra");
        assertThat(trie.hasPrefix("zebra")).isTrue();
    }

    @Test
    void divergingBranchesStayApart() {
        Solution trie = new Solution();
        trie.insert("ab");
        trie.insert("ba");
        assertThat(trie.contains("aa")).isFalse();
        assertThat(trie.hasPrefix("bb")).isFalse();
        assertThat(trie.hasPrefix("b")).isTrue();
    }

    @Test
    void singleLettersFromTheWholeAlphabet() {
        Solution trie = new Solution();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            trie.insert(String.valueOf(letter));
        }
        for (char letter = 'a'; letter <= 'z'; letter++) {
            assertThat(trie.contains(String.valueOf(letter))).as("letter " + letter).isTrue();
        }
        assertThat(trie.contains("aa")).isFalse();
    }

    @Test
    void handlesVeryLongWords() {
        Solution trie = new Solution();
        String word = "xyz".repeat(500);
        trie.insert(word);
        assertThat(trie.contains(word)).isTrue();
        assertThat(trie.contains(word.substring(0, 1499))).isFalse();
        assertThat(trie.hasPrefix(word.substring(0, 1499))).isTrue();
    }

    @Test
    void separateTreesDoNotShareWords() {
        Solution first = new Solution();
        Solution second = new Solution();
        first.insert("shared");
        assertThat(second.contains("shared")).isFalse();
    }
}
