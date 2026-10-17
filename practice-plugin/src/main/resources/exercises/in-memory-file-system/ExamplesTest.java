package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void listsCreatedFileInsideDirectory() {
        Solution fs = new Solution();
        fs.mkdir("/a/b");
        fs.addContentToFile("/a/b/c.txt", "hi");
        assertThat(fs.ls("/a/b")).containsExactly("c.txt");
    }
    @Test
    void appendsContentToExistingFile() {
        Solution fs = new Solution();
        fs.addContentToFile("/a/b/c.txt", "hi");
        fs.addContentToFile("/a/b/c.txt", " there");
        assertThat(fs.readContentFromFile("/a/b/c.txt")).isEqualTo("hi there");
    }
}
