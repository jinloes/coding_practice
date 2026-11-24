package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void exactFitWordsWithSingleSpaces() {
        Solution solution = new Solution();
        assertThat(solution.fullJustify(new String[]{"ab", "cd", "ef"}, 8))
                .containsExactly("ab cd ef");
    }

    @Test
    void handlesMultipleUnevenGapsAcrossSeveralLines() {
        Solution solution = new Solution();
        String[] words = {
                "Clear", "docs", "help", "new", "engineers", "ramp", "up", "fast", "and", "ship",
                "safe", "changes", "to", "live", "services."
        };
        assertThat(solution.fullJustify(words, 18)).containsExactly(
                "Clear   docs  help",
                "new engineers ramp",
                "up  fast  and ship",
                "safe   changes  to",
                "live services.    ");
    }

    @Test
    void handlesSingleWordEqualToMaxWidth() {
        Solution solution = new Solution();
        assertThat(solution.fullJustify(new String[]{"hello", "world"}, 5))
                .containsExactly("hello", "world");
    }
}
