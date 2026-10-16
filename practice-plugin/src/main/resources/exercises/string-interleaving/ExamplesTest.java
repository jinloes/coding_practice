package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsAnInterleaving() {
        assertThat(Solution.isInterleaving("ab", "ac", "acab")).as("isInterleaving(\"ab\", \"ac\", \"acab\")").isEqualTo(true);
    }

    @Test
    void rejectsForeignCharacter() {
        assertThat(Solution.isInterleaving("ab", "cd", "acbe")).as("isInterleaving(\"ab\", \"cd\", \"acbe\")").isEqualTo(false);
    }

    @Test
    void acceptsWhenFirstIsEmpty() {
        assertThat(Solution.isInterleaving("", "xyz", "xyz")).as("isInterleaving(\"\", \"xyz\", \"xyz\")").isEqualTo(true);
    }
}
