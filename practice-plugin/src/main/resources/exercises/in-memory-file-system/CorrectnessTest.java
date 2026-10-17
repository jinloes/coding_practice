package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void rootAndNestedListingsAreSorted() {
        Solution fs = new Solution();
        fs.mkdir("/z"); fs.mkdir("/a"); fs.addContentToFile("/m.txt", "m");
        assertThat(fs.ls("/")).containsExactly("a", "m.txt", "z");
        assertThat(fs.ls("/missing")).isEmpty();
    }
    @Test
    void listingAFileReturnsOnlyTheFileName() {
        Solution fs = new Solution();
        fs.addContentToFile("/notes/today.txt", "one");
        assertThat(fs.ls("/notes/today.txt")).containsExactly("today.txt");
        assertThat(fs.readContentFromFile("/notes/today.txt")).isEqualTo("one");
    }
}
